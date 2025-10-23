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
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) { // Brick landed
            board.mergeBrickToBackground();
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
            }
            if (board.createNewBrick()) { // Check for game over after landing/creating new brick
                guiController.gameOver(); // Notify GUI
                gameController.transitionToState(new GameOverState(board, guiController)); // Transition state
                return new DownData(clearRow, board.getViewData()); // Return data before transition
            }
            guiController.refreshGameBackground(board.getBoardMatrix());
        } else { // Move successful
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
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
        return new PlayingState(board, guiController, gameController); // Return new PlayingState instance
    }
}