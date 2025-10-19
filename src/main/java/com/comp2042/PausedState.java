package com.comp2042.gameplay;

import com.comp2042.Board;
import com.comp2042.DownData;
import com.comp2042.GuiController;
import com.comp2042.MoveEvent;
import com.comp2042.ViewData;

/**
 * Handles game logic when paused .
 */
public class PausedState implements GameState {
    private final Board board;
    private final GuiController guiController;

    public PausedState(Board board, GuiController guiController) {
        this.board = board;
        this.guiController = guiController;
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        // Do nothing during pause, return current view data
        return new DownData(null, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        // Do nothing during pause
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        // Do nothing during pause
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        // Do nothing during pause
        return board.getViewData();
    }

    @Override
    public GameState handlePauseRequest() {
        // Unpause: return a new PlayingState instance
        return new PlayingState(board, guiController, null); // GameController ref not needed here, will be set later
    }

    @Override
    public GameState handleNewGameRequest() {
        // Start new game: delegate to PlayingState (which needs GameController ref for transitions)
        // This creates a temporary PlayingState just to call handleNewGameRequest, which returns a new PlayingState.
        // This new PlayingState will be the one set by GameController.
        return new PlayingState(board, guiController, null).handleNewGameRequest();
    }
}