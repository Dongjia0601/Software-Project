package com.comp2042.model.state;

import com.comp2042.model.board.Board;
import com.comp2042.dto.DownData;
import com.comp2042.controller.game.GuiController;
import com.comp2042.event.MoveEvent;
import com.comp2042.dto.ViewData;

/**
 * Paused state ignoring gameplay input while preserving game state (State Pattern).
 * Handles pause/unpause toggle and new game requests. Game can be resumed seamlessly.
 * 
 * @author Dong, Jia.
 */
public class PausedState implements GameState {
    private final Board board; // Reference to the main game board logic to get current state
    private final GuiController guiController; // Reference to update UI (e.g., show pause screen)
    private final GameStateContext stateContext;

    /**
     * Constructs a PausedState.
     * @param board Game board
     * @param guiController GUI controller
     */
    public PausedState(Board board, GuiController guiController) {
        this(board, guiController, null);
    }

    public PausedState(Board board, GuiController guiController, GameStateContext stateContext) {
        this.board = board;
        this.guiController = guiController;
        this.stateContext = stateContext;
    }

    @Override
    /**
     * Handles the DOWN event. Does nothing during pause.
     * @param event The MoveEvent containing event type and source.
     * @return DownData containing the current view data and no row clearing information.
     */
    public DownData onDownEvent(MoveEvent event) {
        // Do nothing during pause, return current view data
        return new DownData(null, board.getViewData());
    }

    @Override
    /**
     * Handles the LEFT event. Does nothing during pause.
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the current brick position and shape.
     */
    public ViewData onLeftEvent(MoveEvent event) {
        // Do nothing during pause
        return board.getViewData();
    }

    @Override
    /**
     * Handles the RIGHT event. Does nothing during pause.
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the current brick position and shape.
     */
    public ViewData onRightEvent(MoveEvent event) {
        // Do nothing during pause
        return board.getViewData();
    }

    @Override
    /**
     * Handles the ROTATE event. Does nothing during pause.
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the current brick position and shape.
     */
    public ViewData onRotateEvent(MoveEvent event) {
        // Do nothing during pause
        return board.getViewData();
    }

    @Override
    /**
     * Handles the ROTATE_CCW event. Does nothing during pause.
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the current brick position and shape.
     */
    public ViewData onRotateCCWEvent(MoveEvent event) {
        // Do nothing during pause
        return board.getViewData();
    }

    @Override
    /**
     * Handles a request to pause or unpause the game. Unpauses the game.
     * @return A new PlayingState instance.
     */
    public GameState handlePauseRequest() {
        // Unpause: return a new PlayingState instance
        return new PlayingState(board, guiController, stateContext); // Preserve context for transitions
    }

    @Override
    /**
     * Handles a request to start a new game. Delegates to PlayingState.
     * @return The state representing the start of a new game (e.g., PlayingState).
     */
    public GameState handleNewGameRequest() {
        // Start new game: delegate to PlayingState (which needs GameController ref for transitions)
        // This creates a temporary PlayingState just to call handleNewGameRequest, which returns a new PlayingState.
        // This new PlayingState will be the one set by GameController.
        return new PlayingState(board, guiController, stateContext).handleNewGameRequest();
    }
}