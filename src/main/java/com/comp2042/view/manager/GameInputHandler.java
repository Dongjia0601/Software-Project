package com.comp2042.view.manager;

import com.comp2042.event.MoveEvent;
import com.comp2042.event.EventType;
import com.comp2042.event.EventSource;
import com.comp2042.event.listener.InputEventListener;
import com.comp2042.dto.ViewData;
import com.comp2042.dto.DownData;
import javafx.beans.property.BooleanProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Responsible for handling all keyboard input and user interactions.
 * 
 * <p>This class processes keyboard events and delegates them to the appropriate
 * game controller methods. It supports both single-player and two-player game modes,
 * with different control schemes for each player.</p>
 * 
 * <p><strong>Control Schemes:</strong></p>
 * <ul>
 *   <li><strong>Single-Player Mode:</strong>
 *     <ul>
 *       <li>A/D: Move left/right</li>
 *       <li>W: Rotate clockwise</li>
 *       <li>F: Rotate counter-clockwise</li>
 *       <li>S: Soft drop</li>
 *       <li>Space: Hard drop</li>
 *       <li>Shift: Hold piece</li>
 *     </ul>
 *   </li>
 *   <li><strong>Two-Player Mode:</strong>
 *     <ul>
 *       <li>Player 1: Same as single-player (WASD + Space + Shift + F)</li>
 *       <li>Player 2: Arrow keys + Numpad (←/→/↑/↓ + 0 for hard drop + 2 for rotate CCW + 3 for hold)</li>
 *     </ul>
 *   </li>
 *   <li><strong>Global Controls:</strong>
 *     <ul>
 *       <li>P: Pause/Resume</li>
 *       <li>N: New game</li>
 *       <li>M: Mute/Unmute</li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * <p><strong>Design Pattern:</strong> Extracted from GuiController to adhere to Single Responsibility Principle (SRP)</p>
 * 
 * @author Dong, Jia
 * @version Phase 3 - SRP Refactoring
 */
public class GameInputHandler {
    
    // Game state properties
    private final BooleanProperty isPause;
    private final BooleanProperty isGameOver;
    private boolean isTwoPlayerMode = false;
    
    // Event listener for game controller
    private InputEventListener eventListener;
    
    // Callback interfaces for actions
    private InputHandlerCallbacks callbacks;
    
    /**
     * Callback interface for input handler actions.
     * This interface defines methods that the GuiController must implement
     * to respond to input events.
     */
    public interface InputHandlerCallbacks {
        /**
         * Called when a brick needs to be refreshed after movement or rotation.
         * 
         * @param viewData the updated brick view data
         */
        void onRefreshBrick(ViewData viewData);
        
        /**
         * Called when a brick needs to move down (soft drop or hard drop).
         * 
         * @param moveEvent the move event (DOWN or HARD_DROP)
         */
        void onMoveDown(MoveEvent moveEvent);
        
        /**
         * Called when the hold piece display needs to be updated.
         * 
         * @param holdBrickData the hold brick data
         */
        void onUpdateHoldDisplay(int[][] holdBrickData);
        
        /**
         * Called when a pause is requested.
         */
        void onPauseRequested();
        
        /**
         * Called when a new game is requested.
         */
        void onNewGameRequested();
        
        /**
         * Called when mute toggle is requested.
         */
        void onMuteToggleRequested();
        
        /**
         * Called when player 1 brick needs to be refreshed (two-player mode).
         * 
         * @param viewData the updated brick view data
         */
        void onRefreshPlayer1Brick(ViewData viewData);
        
        /**
         * Called when player 2 brick needs to be refreshed (two-player mode).
         * 
         * @param viewData the updated brick view data
         */
        void onRefreshPlayer2Brick(ViewData viewData);
        
        /**
         * Called when player 1 brick moves down (two-player mode).
         * 
         * @param downData the down event data
         */
        void onHandlePlayer1Down(DownData downData);
        
        /**
         * Called when player 2 brick moves down (two-player mode).
         * 
         * @param downData the down event data
         */
        void onHandlePlayer2Down(DownData downData);
    }
    
    /**
     * Constructs a GameInputHandler.
     * 
     * @param isPause the pause state property
     * @param isGameOver the game over state property
     */
    public GameInputHandler(BooleanProperty isPause, BooleanProperty isGameOver) {
        this.isPause = isPause;
        this.isGameOver = isGameOver;
    }
    
    /**
     * Sets the event listener for game controller events.
     * 
     * @param eventListener the input event listener
     */
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }
    
    /**
     * Sets the callbacks for input handler actions.
     * 
     * @param callbacks the callback interface implementation
     */
    public void setCallbacks(InputHandlerCallbacks callbacks) {
        this.callbacks = callbacks;
    }
    
    /**
     * Sets the game mode (two-player or single-player).
     * 
     * @param isTwoPlayer true for two-player mode, false for single-player
     */
    public void setTwoPlayerMode(boolean isTwoPlayer) {
        this.isTwoPlayerMode = isTwoPlayer;
    }
    
    /**
     * Handles keyboard key press events.
     * Routes the event to the appropriate handler based on game mode and state.
     * 
     * @param keyEvent the KeyEvent containing information about the key press
     */
    public void handleKeyPressEvent(KeyEvent keyEvent) {
        if (handleGlobalControls(keyEvent)) {
            return;
        }
        
        if (!isPause.getValue() && !isGameOver.getValue()) {
            if (isTwoPlayerMode) {
                handleTwoPlayerControls(keyEvent);
            } else {
                handleSinglePlayerControls(keyEvent);
            }
        }
    }
    
    /**
     * Handles global keyboard controls that work regardless of game state.
     * 
     * @param keyEvent the key event
     * @return true if the event was handled, false otherwise
     */
    private boolean handleGlobalControls(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.P) {
            if (callbacks != null) {
                callbacks.onPauseRequested();
            }
            keyEvent.consume();
            return true;
        }
        
        if (keyEvent.getCode() == KeyCode.N) {
            if (callbacks != null) {
                callbacks.onNewGameRequested();
            }
            keyEvent.consume();
            return true;
        }
        
        if (keyEvent.getCode() == KeyCode.M) {
            if (callbacks != null) {
                callbacks.onMuteToggleRequested();
            }
            keyEvent.consume();
            return true;
        }
        
        return false;
    }
    
    /**
     * Handles keyboard controls for single-player mode (Endless/Level Mode).
     * Controls: A/D - Move, W - Rotate, S - Soft Drop, Space - Hard Drop, Left Shift - Hold, F - Rotate CCW
     */
    private void handleSinglePlayerControls(KeyEvent keyEvent) {
        if (eventListener == null || callbacks == null) {
            return;
        }
        
        KeyCode code = keyEvent.getCode();
        
        if (code == KeyCode.A) {
            // Move left
            ViewData result = eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.KEYBOARD_PLAYER_1));
            if (result != null) {
                callbacks.onRefreshBrick(result);
            }
            keyEvent.consume();
        }
        else if (code == KeyCode.D) {
            // Move right
            ViewData result = eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.KEYBOARD_PLAYER_1));
            if (result != null) {
                callbacks.onRefreshBrick(result);
            }
            keyEvent.consume();
        }
        else if (code == KeyCode.W) {
            // Rotate clockwise
            ViewData result = eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.KEYBOARD_PLAYER_1));
            if (result != null) {
                callbacks.onRefreshBrick(result);
            }
            keyEvent.consume();
        }
        else if (code == KeyCode.F) {
            // Rotate counterclockwise
            ViewData result = eventListener.onRotateCCWEvent(new MoveEvent(EventType.ROTATE_CCW, EventSource.KEYBOARD_PLAYER_1));
            if (result != null) {
                callbacks.onRefreshBrick(result);
            }
            keyEvent.consume();
        }
        else if (code == KeyCode.S) {
            // Soft drop
            callbacks.onMoveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
            keyEvent.consume();
        }
        else if (code == KeyCode.SPACE) {
            // Hard drop
            callbacks.onMoveDown(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
            keyEvent.consume();
        }
        else if (code == KeyCode.SHIFT) {
            // Hold brick (Left Shift only)
            ViewData result = eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.KEYBOARD_PLAYER_1));
            if (result != null) {
                callbacks.onRefreshBrick(result);
                callbacks.onUpdateHoldDisplay(result.getHoldBrickData());
            }
            keyEvent.consume();
        }
    }
    
    /**
     * Handles keyboard controls for two-player mode.
     * Player 1: A/D - Move, W - Rotate, S - Soft Drop, Space - Hard Drop, Shift/C - Hold, F - Rotate CCW
     * Player 2: ←/→ - Move, ↑ - Rotate, ↓ - Soft Drop, 0 - Hard Drop, 2 - Rotate CCW, 3 - Hold
     * Note: Key 1 has no function assigned for Player 2
     */
    private void handleTwoPlayerControls(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();
        
        if (eventListener == null || callbacks == null) {
            keyEvent.consume();
            return;
        }
        
        if (code == KeyCode.A) {
            ViewData result = eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.KEYBOARD_PLAYER_1));
            if (result != null) callbacks.onRefreshPlayer1Brick(result);
            keyEvent.consume();
        }
        else if (code == KeyCode.D) {
            ViewData result = eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.KEYBOARD_PLAYER_1));
            if (result != null) callbacks.onRefreshPlayer1Brick(result);
            keyEvent.consume();
        }
        else if (code == KeyCode.W) {
            ViewData result = eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.KEYBOARD_PLAYER_1));
            if (result != null) callbacks.onRefreshPlayer1Brick(result);
            keyEvent.consume();
        }
        else if (code == KeyCode.F) {
            ViewData result = eventListener.onRotateCCWEvent(new MoveEvent(EventType.ROTATE_CCW, EventSource.KEYBOARD_PLAYER_1));
            if (result != null) callbacks.onRefreshPlayer1Brick(result);
            keyEvent.consume();
        }
        else if (code == KeyCode.S) {
            DownData downData = eventListener.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.KEYBOARD_PLAYER_1));
            if (downData != null) callbacks.onHandlePlayer1Down(downData);
            keyEvent.consume();
        }
        else if (code == KeyCode.SPACE) {
            DownData downData = eventListener.onDownEvent(new MoveEvent(EventType.HARD_DROP, EventSource.KEYBOARD_PLAYER_1));
            if (downData != null) callbacks.onHandlePlayer1Down(downData);
            keyEvent.consume();
        }
        else if (code == KeyCode.SHIFT) {
            ViewData result = eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.KEYBOARD_PLAYER_1));
            if (result != null) callbacks.onRefreshPlayer1Brick(result);
            keyEvent.consume();
        }
        
        String codeName = code.name();
        boolean isNumpad0 = code == KeyCode.DIGIT0 || code == KeyCode.NUMPAD0 || codeName.equals("NUMPAD0") || codeName.equals("DIGIT0");
        boolean isNumpad2 = code == KeyCode.DIGIT2 || code == KeyCode.NUMPAD2 || codeName.equals("NUMPAD2") || codeName.equals("DIGIT2");
        boolean isNumpad3 = code == KeyCode.DIGIT3 || code == KeyCode.NUMPAD3 || codeName.equals("NUMPAD3") || codeName.equals("DIGIT3");
        
        if (isNumpad0) {
            DownData downData = eventListener.onDownEvent(new MoveEvent(EventType.HARD_DROP, EventSource.KEYBOARD_PLAYER_2));
            if (downData != null) {
                callbacks.onHandlePlayer2Down(downData);
            }
            keyEvent.consume();
        }
        else if (isNumpad2) {
            ViewData result = eventListener.onRotateCCWEvent(new MoveEvent(EventType.ROTATE_CCW, EventSource.KEYBOARD_PLAYER_2));
            if (result != null) {
                callbacks.onRefreshPlayer2Brick(result);
            }
            keyEvent.consume();
        }
        else if (isNumpad3) {
            ViewData result = eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.KEYBOARD_PLAYER_2));
            if (result != null) {
                callbacks.onRefreshPlayer2Brick(result);
            }
            keyEvent.consume();
        }
        else if (code == KeyCode.DIGIT1 || code == KeyCode.NUMPAD1) {
            keyEvent.consume();
        }
        else if (code == KeyCode.LEFT) {
            ViewData result = eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.KEYBOARD_PLAYER_2));
            if (result != null) callbacks.onRefreshPlayer2Brick(result);
            keyEvent.consume();
        }
        else if (code == KeyCode.RIGHT) {
            ViewData result = eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.KEYBOARD_PLAYER_2));
            if (result != null) callbacks.onRefreshPlayer2Brick(result);
            keyEvent.consume();
        }
        else if (code == KeyCode.UP) {
            ViewData result = eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.KEYBOARD_PLAYER_2));
            if (result != null) callbacks.onRefreshPlayer2Brick(result);
            keyEvent.consume();
        }
        else if (code == KeyCode.DOWN) {
            DownData downData = eventListener.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.KEYBOARD_PLAYER_2));
            if (downData != null) callbacks.onHandlePlayer2Down(downData);
            keyEvent.consume();
        }
        else if (isNumpadOrDigitKey(code)) {
            keyEvent.consume();
        }
    }
    
    /**
     * Checks if a key code is a numpad or digit key.
     * 
     * @param code the key code to check
     * @return true if the key is a numpad or digit key, false otherwise
     */
    private boolean isNumpadOrDigitKey(KeyCode code) {
        return code == KeyCode.NUMPAD0 || code == KeyCode.NUMPAD1 || code == KeyCode.NUMPAD2 || 
               code == KeyCode.NUMPAD3 || code == KeyCode.NUMPAD4 || code == KeyCode.NUMPAD5 || 
               code == KeyCode.NUMPAD6 || code == KeyCode.NUMPAD7 || code == KeyCode.NUMPAD8 || 
               code == KeyCode.NUMPAD9 ||
               code == KeyCode.DIGIT0 || code == KeyCode.DIGIT1 || code == KeyCode.DIGIT2 || 
               code == KeyCode.DIGIT3 || code == KeyCode.DIGIT4 || code == KeyCode.DIGIT5 || 
               code == KeyCode.DIGIT6 || code == KeyCode.DIGIT7 || code == KeyCode.DIGIT8 || 
               code == KeyCode.DIGIT9;
    }
}

