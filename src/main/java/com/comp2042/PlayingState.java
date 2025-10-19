package com.comp2042.gameplay;

import com.comp2042.*;

/**
 * Handles standard gameplay logic.
 */
public class PlayingState implements GameState {
    private final Board board;
    private final GuiController guiController;
    private final GameController gameController; // For state transitions

    public PlayingState(Board board, GuiController guiController, GameController gameController) {
        this.board = board;
        this.guiController = guiController;
        this.gameController = gameController;
    }

    @Override
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
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    @Override
    public GameState handlePauseRequest() {
        return new PausedState(board, guiController);
    }

    @Override
    public GameState handleNewGameRequest() {
        board.newGame();
        guiController.refreshGameBackground(board.getBoardMatrix());
        return new PlayingState(board, guiController, gameController); // Return new PlayingState instance
    }
}