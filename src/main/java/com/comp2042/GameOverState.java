package com.comp2042.gameplay;

import com.comp2042.Board;
import com.comp2042.DownData;
import com.comp2042.GuiController;
import com.comp2042.MoveEvent;
import com.comp2042.ViewData;

/**
 * Handles game logic when the game is over (mostly ignores input).
 */
public class GameOverState implements GameState {
    private final Board board;
    private final GuiController guiController;

    public GameOverState(Board board, GuiController guiController) {
        this.board = board;
        this.guiController = guiController;
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        // Do nothing during game over
        return new DownData(null, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        // Do nothing during game over
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        // Do nothing during game over
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        // Do nothing during game over
        return board.getViewData();
    }

    @Override
    public GameState handlePauseRequest() {
        // Pausing during game over doesn't make sense
        return this;
    }

    @Override
    public GameState handleNewGameRequest() {
        // Start new game: delegate to PlayingState (which needs GameController ref for transitions)
        return new PlayingState(board, guiController, null).handleNewGameRequest();
    }
}