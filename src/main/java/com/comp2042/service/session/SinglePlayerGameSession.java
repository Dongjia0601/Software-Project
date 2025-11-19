package com.comp2042.service.session;

import com.comp2042.controller.game.GameViewController;
import com.comp2042.dto.DownData;
import com.comp2042.dto.ViewData;
import com.comp2042.event.MoveEvent;
import com.comp2042.model.board.Board;
import com.comp2042.model.state.GameState;
import com.comp2042.model.state.PlayingState;
import com.comp2042.service.audio.SoundManager;
import javafx.beans.property.IntegerProperty;

/**
 * Single-player session encapsulating board lifecycle and state transitions (Service Layer).
 * Keeps GameController focused on UI/input delegation.
 */
public class SinglePlayerGameSession implements GameSession {

    private final Board board;
    private final GameViewController guiController;
    private GameState currentState;

    public SinglePlayerGameSession(Board board, GameViewController guiController) {
        this.board = board;
        this.guiController = guiController;
    }

    @Override
    public void initialize() {
        board.newGame();
        this.currentState = new PlayingState(board, guiController, this);
        guiController.initGameView(board.getBoardMatrix(), board.getViewData());
    }

    @Override
    public DownData handleDown(MoveEvent event) {
        return currentState.onDownEvent(event);
    }

    @Override
    public ViewData handleLeft(MoveEvent event) {
        return currentState.onLeftEvent(event);
    }

    @Override
    public ViewData handleRight(MoveEvent event) {
        return currentState.onRightEvent(event);
    }

    @Override
    public ViewData handleRotateCW(MoveEvent event) {
        return currentState.onRotateEvent(event);
    }

    @Override
    public ViewData handleRotateCCW(MoveEvent event) {
        return currentState.onRotateCCWEvent(event);
    }

    @Override
    public ViewData handleHardDrop(MoveEvent event) {
        DownData downData = currentState.onDownEvent(event);
        return downData != null ? downData.getViewData() : null;
    }

    @Override
    public ViewData handleSoftDrop(MoveEvent event) {
        DownData downData = currentState.onDownEvent(event);
        return downData != null ? downData.getViewData() : null;
    }

    @Override
    public ViewData handleHold(MoveEvent event) {
        boolean success = board.holdBrick();
        if (success) {
            SoundManager.getInstance().playHoldSound();
        }
        return success ? board.getViewData() : null;
    }

    @Override
    public void requestPause() {
        GameState newState = currentState.handlePauseRequest();
        if (newState != currentState) {
            SoundManager.getInstance().playPauseResumeSound();
            this.currentState = newState;
        }
    }

    @Override
    public void startNewGame() {
        this.currentState = currentState.handleNewGameRequest();
    }

    @Override
    public IntegerProperty scoreProperty() {
        return board.getScore().scoreProperty();
    }

    @Override
    public void transitionToState(GameState newState) {
        this.currentState = newState;
    }

    public GameState getCurrentState() {
        return currentState;
    }
}

