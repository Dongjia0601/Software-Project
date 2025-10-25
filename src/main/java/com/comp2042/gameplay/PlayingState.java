package com.comp2042.gameplay;

import com.comp2042.*;

/**
 * Represents the active playing state of the Tetris game.
 * 
 * <p>This class implements the core gameplay logic when the game is actively running.
 * It handles all player input events (movement, rotation, dropping) and manages the
 * game progression including brick landing, row clearing, scoring, and game over detection.</p>
 * 
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Process player input events (move, rotate, drop)</li>
 *   <li>Manage brick movement and collision detection</li>
 *   <li>Handle brick landing and merging to background</li>
 *   <li>Process row clearing and score updates</li>
 *   <li>Detect game over conditions and trigger state transitions</li>
 * </ul>
 */
public class PlayingState implements GameState {
    private final Board board; // Reference to the main game board logic
    private final GuiController guiController; // Reference to update UI (e.g., show game over)
    private final GameController gameController; // For state transitions

    /**
     * Constructs a PlayingState instance.
     * @param board The game board instance.
     * @param guiController The GUI controller instance.
     * @param gameController The main game controller instance for state transitions.
     */
    public PlayingState(Board board, GuiController guiController, GameController gameController) {
        this.board = board;
        this.guiController = guiController;
        this.gameController = gameController;
    }

    @Override
    /**
     * Handles the DOWN event (both user-initiated and automatic descent).
     * Implements the core game logic: move brick down, land if collision,
     * merge to background, clear rows, update score, check for game over.
     * @param event The MoveEvent containing event type and source.
     * @return DownData containing view and row-clearing information.
     */
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove;
        ClearRow clearRow = null;
        
        if (event.getEventType() == EventType.HARD_DROP) {
            // Hard drop: instantly drop to bottom
            int dropDistance = board.hardDropBrick();
            if (dropDistance > 0) {
                // Add score for hard drop (2 points per row dropped)
                board.getScore().add(dropDistance * 2);
                // Update score display immediately for hard drop
                guiController.updateScore(board.getScore().getScore(), 0);
            }
            canMove = false; // Hard drop always lands the brick
        } else {
            // Normal soft drop: move down one position
            canMove = board.moveBrickDown();
        }
        
        if (!canMove) { // Brick landed
            board.mergeBrickToBackground();
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
              
                // Update lines display in GUI
                guiController.updateLines(board.getTotalLinesCleared());
                // Update score display in GUI
                guiController.updateScore(board.getScore().getScore(), 0); // High score not available in PlayingState
            }
            if (board.createNewBrick()) { // Check for game over after landing/creating new brick
                System.out.println("Game Over detected! isEndlessMode: " + guiController.isEndlessMode());
                // Check if we're in Endless Mode and show appropriate game over screen
                if (guiController.isEndlessMode()) {
                    System.out.println("Showing Endless Game Over UI...");
                    guiController.showEndlessGameOverScene(board);
                } else {
                    System.out.println("Showing regular Game Over UI...");
                    guiController.gameOver(); // Notify GUI
                    gameController.transitionToState(new GameOverState(board, guiController)); // Transition state
                }
                return new DownData(clearRow, board.getViewData()); // Return data before transition
            }
            guiController.refreshGameBackground(board.getBoardMatrix());
        } else { // Move successful
            // Only add score for soft drop (down movement), not for left/right movement
            if (event.getEventSource() == EventSource.USER && event.getEventType() == EventType.DOWN) {
                board.getScore().add(1);
                // Update score display immediately for soft drop
                guiController.updateScore(board.getScore().getScore(), 0);
            }
        }
        return new DownData(clearRow, board.getViewData());
    }

    @Override
    /**
     * Handles the LEFT event by attempting to move the brick left.
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape.
     */
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    /**
     * Handles the RIGHT event by attempting to move the brick right.
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape.
     */
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    /**
     * Handles the ROTATE event by attempting to rotate the brick.
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape.
     */
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    @Override
    /**
     * Handles the ROTATE_CCW event by attempting to rotate the brick counterclockwise.
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape.
     */
    public ViewData onRotateCCWEvent(MoveEvent event) {
        board.rotateRightBrick();
        return board.getViewData();
    }

    @Override
    /**
     * Handles a request to pause the game.
     * @return A new PausedState instance.
     */
    public GameState handlePauseRequest() {
        return new PausedState(board, guiController);
    }

    @Override
    /**
     * Handles a request to start a new game.
     * Resets the board state and refreshes the background view.
     * @return A new PlayingState instance initialized for a new game.
     */
    public GameState handleNewGameRequest() {
        board.newGame();
        guiController.refreshGameBackground(board.getBoardMatrix());
        guiController.updateLines(0);
        guiController.updateScore(0, 0);
        return new PlayingState(board, guiController, gameController); // Return new PlayingState instance
    }
}