package com.comp2042.model.state;

import com.comp2042.model.board.Board;
import com.comp2042.dto.DownData;
import com.comp2042.controller.game.GuiController;
import com.comp2042.event.MoveEvent;
import com.comp2042.dto.ViewData;

/**
 * Paused state ignoring gameplay input while preserving game state (State Pattern).
 * 
 * <p>This state is entered when the player pauses the game. In this state, all gameplay
 * input events are ignored, but the current game state (board, score, current brick)
 * is fully preserved. The game can be seamlessly resumed by handling a pause request,
 * which transitions back to PlayingState.
 * 
 * <p><b>State Behavior:</b>
 * <ul>
 *   <li>All movement/rotation events return current view data without processing</li>
 *   <li>Pause requests transition back to PlayingState (unpause)</li>
 *   <li>New game requests transition to PlayingState and initialize a new game</li>
 * </ul>
 * 
 * @author Dong, Jia.
 */
public class PausedState implements GameState {
    private final Board board;
    private final GuiController guiController;
    private final GameStateContext stateContext;

    /**
     * Constructs a PausedState without state context.
     * 
     * @param board the game board instance, must not be null
     * @param guiController the GUI controller for UI updates, must not be null
     */
    public PausedState(Board board, GuiController guiController) {
        this(board, guiController, null);
    }

    /**
     * Constructs a PausedState with state context.
     * 
     * @param board the game board instance, must not be null
     * @param guiController the GUI controller for UI updates, must not be null
     * @param stateContext the state context for state transitions, may be null
     */
    public PausedState(Board board, GuiController guiController, GameStateContext stateContext) {
        this.board = board;
        this.guiController = guiController;
        this.stateContext = stateContext;
    }

    /**
     * Handles the DOWN event during pause.
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
     * Handles the LEFT event during pause.
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
     * Handles the RIGHT event during pause.
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
     * Handles the ROTATE event during pause.
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
     * Handles the ROTATE_CCW (counterclockwise) event during pause.
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
     * Handles a request to unpause the game.
     * Transitions back to PlayingState, resuming the game from where it was paused.
     * 
     * @return the PlayingState instance for resuming the game
     */
    @Override
    public GameState handlePauseRequest() {
        return new PlayingState(board, guiController, stateContext);
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