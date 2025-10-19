package com.comp2042;

import com.comp2042.gameplay.GameState;
import com.comp2042.gameplay.PlayingState; // Import new classes
import com.comp2042.gameplay.PausedState;
import com.comp2042.gameplay.GameOverState;

/**
 * Controller managing the overall game flow, state transitions, and input event delegation.
 * It holds references to the game board logic (SimpleBoard) and the GUI controller (GuiController).
 * Delegates input events (from GuiController) to the current GameState instance.
 */
public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(25, 10); // The main game board instance
    private final GuiController viewGuiController; // The GUI controller instance
    private GameState currentState; // Hold current state

    /**
     * Constructs a GameController.
     * Initializes the board, creates the initial PlayingState, initializes the GUI view,
     * binds the score, and sets itself as the event listener for the GUI.
     *
     * @param c The GuiController instance.
     */
    public GameController(GuiController c) {
        viewGuiController = c;
        board.newGame(); // Initialize board state
        // Create initial state, passing necessary references
        this.currentState = new PlayingState(board, viewGuiController, this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.setEventListener(this);
    }

    /**
     * Method to allow states to change the controller's state.
     * Called by states like PlayingState when a transition is needed (e.g., to GameOverState).
     *
     * @param newState The new GameState to transition to.
     */
    public void transitionToState(GameState newState) {
        this.currentState = newState;
    }

    /**
     * Method to handle pause requests (e.g., from GUI via P key).
     * Delegates the pause request to the current state.
     */
    public void requestPause() {
        GameState newState = currentState.handlePauseRequest();
        if (newState != currentState) {
            this.currentState = newState;
        }
    }

    @Override
    /**
     * Handles the DOWN event received from the GUI.
     * Delegates the event to the current GameState instance.
     *
     * @param event The MoveEvent containing event type and source.
     * @return DownData containing view and row-clearing information from the state.
     */
    public DownData onDownEvent(MoveEvent event) {
        // Delegate to current state
        return currentState.onDownEvent(event);
    }

    @Override
    /**
     * Handles the LEFT event received from the GUI.
     * Delegates the event to the current GameState instance.
     *
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape from the state.
     */
    public ViewData onLeftEvent(MoveEvent event) {
        // Delegate to current state
        return currentState.onLeftEvent(event);
    }

    @Override
    /**
     * Handles the RIGHT event received from the GUI.
     * Delegates the event to the current GameState instance.
     *
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape from the state.
     */
    public ViewData onRightEvent(MoveEvent event) {
        // Delegate to current state
        return currentState.onRightEvent(event);
    }

    @Override
    /**
     * Handles the ROTATE event received from the GUI.
     * Delegates the event to the current GameState instance.
     *
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape from the state.
     */
    public ViewData onRotateEvent(MoveEvent event) {
        // Delegate to current state
        return currentState.onRotateEvent(event);
    }

    @Override
    /**
     * Handles the request to start a new game.
     * Delegates the request to the current GameState instance.
     */
    public void createNewGame() {
        // Delegate to current state
        this.currentState = currentState.handleNewGameRequest();
    }
}