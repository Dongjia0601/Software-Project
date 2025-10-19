package com.comp2042;

import com.comp2042.gameplay.GameState;
import com.comp2042.gameplay.PlayingState;
import com.comp2042.gameplay.PausedState;
import com.comp2042.gameplay.GameOverState;

/**
 * Controller managing game state and delegating events to the current state.
 */
public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(25, 10);
    private final GuiController viewGuiController;
    private GameState currentState; // Hold current state

    public GameController(GuiController c) {
        viewGuiController = c;
        board.newGame(); // Initialize board state
        // Create initial state, passing necessary references
        this.currentState = new PlayingState(board, viewGuiController, this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.setEventListener(this);
    }

    // Method to allow states to change the controller's state
    public void transitionToState(GameState newState) {
        this.currentState = newState;
    }

    // Method to handle pause requests (e.g., from GUI)
    public void requestPause() {
        GameState newState = currentState.handlePauseRequest();
        if (newState != currentState) {
            this.currentState = newState;
        }
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        // Delegate to current state
        return currentState.onDownEvent(event);
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        // Delegate to current state
        return currentState.onLeftEvent(event);
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        // Delegate to current state
        return currentState.onRightEvent(event);
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        // Delegate to current state
        return currentState.onRotateEvent(event);
    }

    @Override
    public void createNewGame() {
        // Delegate to current state
        this.currentState = currentState.handleNewGameRequest();
        // Ensure timeline is started if the new state is PlayingState
        // This might need adjustment based on how timeline is managed in GuiController
        // For now, assume GuiController handles timeline restart in newGame()
    }
}