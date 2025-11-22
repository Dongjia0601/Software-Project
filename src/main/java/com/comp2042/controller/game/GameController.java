package com.comp2042.controller.game;

import com.comp2042.event.listener.InputEventListener;
import com.comp2042.event.MoveEvent;
import com.comp2042.dto.DownData;
import com.comp2042.dto.ViewData;
import com.comp2042.model.board.SimpleBoard;
import com.comp2042.service.session.GameSession;
import com.comp2042.service.session.SinglePlayerGameSession;

/**
 * Central game flow controller coordinating board logic and state transitions.
 * Delegates input events to game session, manages lifecycle, and coordinates
 * between GUI controller and game session.
 * 
 * @author Dong, Jia
 */
public class GameController implements InputEventListener {

    private final GameSession gameSession;

    /**
     * Constructs a GameController and initializes the game session.
     *
     * @param viewController the GUI controller for rendering and user interaction
     */
    public GameController(GuiController viewController) {
        this.gameSession = new SinglePlayerGameSession(new SimpleBoard(10, 20), viewController);
        this.gameSession.initialize();
        viewController.bindScore(gameSession.scoreProperty());
        viewController.setEventListener(this);
        
        // Ensure high score is displayed correctly for Endless Mode
        if (viewController.isEndlessMode()) {
            try {
                com.comp2042.model.mode.EndlessModeLeaderboard leaderboard = 
                    com.comp2042.model.mode.EndlessModeLeaderboard.getInstance();
                int highScore = leaderboard.getHighScore();
                viewController.updateScore(0, highScore);
            } catch (Exception e) {
                System.err.println("Error updating initial high score: " + e.getMessage());
            }
        }
    }

    /**
     * Handles pause requests from the GUI.
     */
    public void requestPause() {
        gameSession.requestPause();
    }

    /**
     * Handles the DOWN event from the GUI.
     *
     * @param event the move event containing type and source
     * @return down data containing view and row-clearing information
     */
    @Override
    public DownData onDownEvent(MoveEvent event) {
        return gameSession.handleDown(event);
    }

    /**
     * Handles the LEFT event from the GUI.
     *
     * @param event the move event containing type and source
     * @return view data containing updated brick position and shape
     */
    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        return gameSession.handleLeft(event);
    }

    /**
     * Handles the RIGHT event from the GUI.
     *
     * @param event the move event containing type and source
     * @return view data containing updated brick position and shape
     */
    @Override
    public ViewData onRightEvent(MoveEvent event) {
        return gameSession.handleRight(event);
    }

    /**
     * Handles the ROTATE (clockwise) event from the GUI.
     *
     * @param event the move event containing type and source
     * @return view data containing updated brick position and shape
     */
    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        return gameSession.handleRotateCW(event);
    }

    /**
     * Handles the ROTATE_CCW (counter-clockwise) event from the GUI.
     *
     * @param event the move event containing type and source
     * @return view data containing updated brick position and shape
     */
    @Override
    public ViewData onRotateCCWEvent(MoveEvent event) {
        return gameSession.handleRotateCCW(event);
    }

    /**
     * Handles the HARD_DROP event from the GUI.
     *
     * @param event the move event containing type and source
     * @return view data containing updated brick position and shape
     */
    @Override
    public ViewData onHardDropEvent(MoveEvent event) {
        return gameSession.handleHardDrop(event);
    }

    /**
     * Handles the SOFT_DROP event from the GUI.
     *
     * @param event the move event containing type and source
     * @return view data containing updated brick position and shape
     */
    @Override
    public ViewData onSoftDropEvent(MoveEvent event) {
        return gameSession.handleSoftDrop(event);
    }

    /**
     * Handles the HOLD event from the GUI.
     *
     * @param event the move event containing type and source
     * @return view data containing updated brick position and hold state
     */
    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        return gameSession.handleHold(event);
    }

    /**
     * Handles the PAUSE event from the GUI.
     *
     * @param event the move event containing type and source
     */
    @Override
    public void onPauseEvent(MoveEvent event) {
        requestPause();
    }

    /**
     * Handles the RESUME event from the GUI.
     *
     * @param event the move event containing type and source
     */
    @Override
    public void onResumeEvent(MoveEvent event) {
        requestPause();
    }

    /**
     * Handles the NEW_GAME event from the GUI.
     *
     * @param event the move event containing type and source
     */
    @Override
    public void onNewGameEvent(MoveEvent event) {
        gameSession.startNewGame();
    }

    /**
     * Handles the QUIT event from the GUI.
     *
     * @param event the move event containing type and source
     */
    @Override
    public void onQuitEvent(MoveEvent event) {
        System.exit(0);
    }

}