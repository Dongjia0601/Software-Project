package com.comp2042.model.state;

import com.comp2042.model.board.Board;
import com.comp2042.controller.game.GuiController;
import com.comp2042.dto.DownData;
import com.comp2042.dto.ViewData;
import com.comp2042.dto.ClearRow;
import com.comp2042.event.MoveEvent;
import com.comp2042.event.EventType;
import com.comp2042.event.EventSource;
import com.comp2042.service.audio.SoundManager;

/**
 * Active playing state implementing core gameplay logic (State Pattern).
 * 
 * <p>This is the primary game state where all gameplay occurs. In this state, the game
 * processes player input, manages brick movement and rotation, handles row clearing,
 * updates scores, and detects game over conditions. The state handles transitions to
 * PausedState (when pause is requested) and GameOverState (when game ends).
 * 
 * <p><b>State Behavior:</b>
 * <ul>
 *   <li>Processes all movement/rotation events and updates board state</li>
 *   <li>Handles hard drop and soft drop with scoring</li>
 *   <li>Manages row clearing, score updates, and sound effects</li>
 *   <li>Detects game over conditions and level completion</li>
 *   <li>Pause requests transition to PausedState</li>
 *   <li>New game requests reset the board and return to PlayingState</li>
 * </ul>
 * 
 * @author Dong, Jia.
 */
public class PlayingState implements GameState {
    private final Board board;
    private final GuiController guiController;
    private final GameStateContext stateContext;

    /**
     * Constructs a PlayingState instance.
     * 
     * @param board the game board instance
     * @param guiController the GUI controller for UI updates
     * @param stateContext the state context for state transitions
     */
    public PlayingState(Board board, GuiController guiController, GameStateContext stateContext) {
        this.board = board;
        this.guiController = guiController;
        this.stateContext = stateContext;
    }

    /**
     * Handles the DOWN event (both user-initiated and automatic descent).
     * 
     * <p>Implements the core game logic for downward movement:
     * <ul>
     *   <li>Hard drop: Instantly drops brick to bottom, adds score (2 points per row)</li>
     *   <li>Soft drop: Moves brick down one row, adds score (1 point per move)</li>
     *   <li>On landing: Merges brick to background, clears completed rows, updates score</li>
     *   <li>After clearing: Creates new brick, checks for game over</li>
     *   <li>Level mode: Checks for level completion and updates progress</li>
     * </ul>
     * 
     * @param event the move event containing event type (DOWN, HARD_DROP) and source
     * @return DownData containing view data and row-clearing information (null if no rows cleared)
     */
    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove;
        ClearRow clearRow = null;
        
        if (event.getEventType() == EventType.HARD_DROP) {
            int dropDistance = board.hardDropBrick();
            if (dropDistance > 0) {
                board.getScore().add(dropDistance * 2);
                int currentHighScore = getCurrentHighScore();
                guiController.updateScore(board.getScore().getScore(), currentHighScore);
                SoundManager.getInstance().playHardDropSound();
            }
            canMove = false;
        } else {
            canMove = board.moveBrickDown();
        }
        
        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                SoundManager.getInstance().playLineClearSound(clearRow.getLinesRemoved());
              
                // Update lines display in GUI
                // Endless Mode updates lines in GuiController.moveDown() using endlessLinesClearedUI
                if (!guiController.isEndlessMode()) {
                    guiController.updateLines(board.getTotalLinesCleared());
                }
                // Update score display in GUI with current high score
                int currentHighScore = getCurrentHighScore();
                guiController.updateScore(board.getScore().getScore(), currentHighScore);
                
                // For Level Mode, also update progress through LevelGameModeImpl
                if (guiController.isLevelMode()) {
                    // Get current level and update progress
                    com.comp2042.model.mode.LevelManager levelManager = com.comp2042.model.mode.LevelManager.getInstance();
                    com.comp2042.model.mode.LevelMode currentLevel = levelManager.getCurrentLevel();
                    if (currentLevel != null) {
                        // Calculate lines cleared in level from board
                        int linesClearedInLevel = board.getTotalLinesCleared();
                        guiController.updateProgress(linesClearedInLevel, currentLevel.getTargetLines());
                        
                        // Check if level completion condition is met (target lines reached)
                        if (linesClearedInLevel >= currentLevel.getTargetLines()) {
                            // Level completed - complete the level in LevelManager first
                            // This will update the best stats and unlock next level
                            long playTimeMs = System.currentTimeMillis() - guiController.getLevelStartTime();
                            if (playTimeMs <= 0) {
                                playTimeMs = 1; // Ensure positive time
                            }
                            
                            // Complete the level in LevelManager to update the best stats and unlock next level
                            boolean[] newRecords = levelManager.completeLevel(
                                currentLevel.getLevelId(),
                                board.getScore().getScore(),
                                linesClearedInLevel,
                                playTimeMs,
                                true // success
                            );
                            
                            // Then trigger game over to show completion screen
                            guiController.showLevelGameOverScene(board, newRecords);
                            return new DownData(clearRow, board.getViewData());
                        }
                    }
                }
            }
            if (board.createNewBrick()) { // Check for game over after landing/creating new brick

                if (guiController.isEndlessMode()) {
                    guiController.showEndlessGameOverScene(board);
                } else if (guiController.isLevelMode()) {
                    // Level failed, no new records - pass current total lines cleared from board
                    guiController.showLevelGameOverScene(board, new boolean[]{false, false}, board.getTotalLinesCleared());
                } else {
                    guiController.gameOver(); // Notify GUI
                    if (stateContext != null) {
                        stateContext.transitionToState(new GameOverState(board, guiController, stateContext));
                    }
                }
                return new DownData(clearRow, board.getViewData()); // Return data before transition
            }
            guiController.refreshGameBackground(board.getBoardMatrix());
        } else { // Move successful
            // Only add score for soft drop (down movement), not for left/right movement
            if (event.getEventSource() == EventSource.USER && event.getEventType() == EventType.DOWN) {
                board.getScore().add(1);
                // Update score display immediately for soft drop with current high score
                int currentHighScore = getCurrentHighScore();
                guiController.updateScore(board.getScore().getScore(), currentHighScore);
                // Play soft drop sound effect
                SoundManager.getInstance().playSoftDropSound();
            }
        }
        return new DownData(clearRow, board.getViewData());
    }

    /**
     * Handles the LEFT event by attempting to move the brick left.
     * Plays move sound effect if the movement is successful.
     * 
     * @param event the move event (ignored, but required by interface)
     * @return ViewData containing the updated brick position and shape
     */
    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        boolean success = board.moveBrickLeft();
        if (success) {
            // Play move sound effect
            SoundManager.getInstance().playMoveSound();
        }
        return board.getViewData();
    }

    /**
     * Handles the RIGHT event by attempting to move the brick right.
     * Plays move sound effect if the movement is successful.
     * 
     * @param event the move event (ignored, but required by interface)
     * @return ViewData containing the updated brick position and shape
     */
    @Override
    public ViewData onRightEvent(MoveEvent event) {
        boolean success = board.moveBrickRight();
        if (success) {
            // Play move sound effect
            SoundManager.getInstance().playMoveSound();
        }
        return board.getViewData();
    }

    /**
     * Handles the ROTATE event by attempting to rotate the brick clockwise.
     * Plays rotate sound effect if the rotation is successful.
     * 
     * @param event the move event (ignored, but required by interface)
     * @return ViewData containing the updated brick position and shape
     */
    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        boolean success = board.rotateLeftBrick();
        if (success) {
            // Play rotate sound effect
            SoundManager.getInstance().playRotateSound();
        }
        return board.getViewData();
    }

    /**
     * Handles the ROTATE_CCW event by attempting to rotate the brick counterclockwise.
     * Plays rotate sound effect if the rotation is successful.
     * 
     * @param event the move event (ignored, but required by interface)
     * @return ViewData containing the updated brick position and shape
     */
    @Override
    public ViewData onRotateCCWEvent(MoveEvent event) {
        boolean success = board.rotateRightBrick();
        if (success) {
            // Play rotate sound effect
            SoundManager.getInstance().playRotateSound();
        }
        return board.getViewData();
    }

    /**
     * Handles a request to pause the game.
     * Transitions to PausedState, preserving the current game state.
     * 
     * @return the PausedState instance for the paused game
     */
    @Override
    public GameState handlePauseRequest() {
        return new PausedState(board, guiController, stateContext);
    }

    /**
     * Handles a request to start a new game.
     * 
     * <p>Resets the board state, refreshes the UI (background, active brick, score, lines),
     * and initializes game mode-specific displays (e.g., level progress for Level Mode).
     * 
     * @return a new PlayingState instance initialized for a new game
     */
    @Override
    public GameState handleNewGameRequest() {
        board.newGame();

        guiController.refreshGameBackground(board.getBoardMatrix());
        guiController.refreshActiveBrick(board.getViewData());
        if (guiController.isEndlessMode()) {
            guiController.updateLines(0);
        } else if (guiController.isLevelMode()) {
            // For Level Mode, update progress display (linesCleared/targetLines)
            com.comp2042.model.mode.LevelManager levelManager = com.comp2042.model.mode.LevelManager.getInstance();
            com.comp2042.model.mode.LevelMode currentLevel = levelManager.getCurrentLevel();
            if (currentLevel != null) {
                guiController.updateProgress(0, currentLevel.getTargetLines());
            }
        } else {
            guiController.updateLines(0);
        }
        int currentHighScore = getCurrentHighScore();
        guiController.updateScore(0, currentHighScore);
        return new PlayingState(board, guiController, stateContext);
    }
    
    /**
     * Gets the current high score from the leaderboard if in Endless Mode.
     * 
     * <p>This method retrieves the highest score from the Endless Mode leaderboard
     * for display purposes. Returns 0 if not in Endless Mode or if an error occurs.
     * 
     * @return the current high score from leaderboard, or 0 if not in Endless Mode or on error
     */
    private int getCurrentHighScore() {
        if (guiController != null && guiController.isEndlessMode()) {
            try {
                com.comp2042.model.mode.EndlessModeLeaderboard leaderboard = 
                    com.comp2042.model.mode.EndlessModeLeaderboard.getInstance();
                return leaderboard.getHighScore();
            } catch (Exception e) {
                System.err.println("Error getting high score from leaderboard: " + e.getMessage());
                return 0;
            }
        }
        return 0;
    }
}