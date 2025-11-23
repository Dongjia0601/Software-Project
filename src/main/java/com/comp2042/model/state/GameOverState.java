package com.comp2042.model.state;

import com.comp2042.model.board.Board;
import com.comp2042.dto.DownData;
import com.comp2042.controller.game.GuiController;
import com.comp2042.event.MoveEvent;
import com.comp2042.dto.ViewData;

/**
 * Game over state ignoring gameplay input and preserving final score (State Pattern).
 * 
 * <p>This state is entered when the game ends (board fills up or level fails).
 * In this state, all gameplay input events are ignored, but the final board state
 * and score are preserved for display. The state handles new game requests by
 * transitioning back to PlayingState.
 * 
 * <p><b>State Behavior:</b>
 * <ul>
 *   <li>All movement/rotation events return current view data without processing</li>
 *   <li>Pause requests are ignored (returns self)</li>
 *   <li>New game requests transition to PlayingState</li>
 * </ul>
 * 
 * @author Dong, Jia.
 */
public class GameOverState implements GameState {
    private final Board board;
    private final GuiController guiController;
    private final GameStateContext stateContext;

    /**
     * Constructs a GameOverState without state context.
     * 
     * @param board the game board instance, must not be null
     * @param guiController the GUI controller for UI updates, must not be null
     */
    public GameOverState(Board board, GuiController guiController) {
        this(board, guiController, null);
    }

    /**
     * Constructs a GameOverState with state context.
     * 
     * @param board the game board instance, must not be null
     * @param guiController the GUI controller for UI updates, must not be null
     * @param stateContext the state context for state transitions, may be null
     */
    public GameOverState(Board board, GuiController guiController, GameStateContext stateContext) {
        this.board = board;
        this.guiController = guiController;
        this.stateContext = stateContext;
    }

    /**
     * Handles the DOWN event during game over.
     * Ignores the event and returns current view data without processing.
     * 
     * @param event the move event (ignored)
     * @return DownData with null clear row and current view data
     */
    @Override
    public DownData onDownEvent(MoveEvent event) {
        return new DownData(null, board.getViewData());
    }

    /**
     * Handles the LEFT event during game over.
     * Ignores the event and returns current view data.
     * 
     * @param event the move event (ignored)
     * @return ViewData containing the current board state
     */
    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        return board.getViewData();
    }

    /**
     * Handles the RIGHT event during game over.
     * Ignores the event and returns current view data.
     * 
     * @param event the move event (ignored)
     * @return ViewData containing the current board state
     */
    @Override
    public ViewData onRightEvent(MoveEvent event) {
        return board.getViewData();
    }

    /**
     * Handles the ROTATE event during game over.
     * Ignores the event and returns current view data.
     * 
     * @param event the move event (ignored)
     * @return ViewData containing the current board state
     */
    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        return board.getViewData();
    }

    /**
     * Handles the ROTATE_CCW (counterclockwise) event during game over.
     * Ignores the event and returns current view data.
     * 
     * @param event the move event (ignored)
     * @return ViewData containing the current board state
     */
    @Override
    public ViewData onRotateCCWEvent(MoveEvent event) {
        return board.getViewData();
    }

    /**
     * Handles a request to pause the game.
     * Pausing during game over is not applicable, so returns self.
     * 
     * @return this GameOverState instance (no state change)
     */
    @Override
    public GameState handlePauseRequest() {
        return this;
    }

    /**
     * Handles a request to start a new game.
     * Transitions to PlayingState and initializes a new game.
     * 
     * @return the PlayingState instance for the new game
     */
    @Override
    public GameState handleNewGameRequest() {
        return new PlayingState(board, guiController, stateContext).handleNewGameRequest();
    }
}