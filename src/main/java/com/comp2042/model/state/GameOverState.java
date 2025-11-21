package com.comp2042.model.state;

import com.comp2042.model.board.Board;
import com.comp2042.dto.DownData;
import com.comp2042.controller.game.GuiController;
import com.comp2042.event.MoveEvent;
import com.comp2042.dto.ViewData;

/**
 * Game over state ignoring gameplay input and preserving final score (State Pattern).
 * Handles new game requests and displays final statistics.
 * 
 * @author Dong, Jia.
 */
public class GameOverState implements GameState {
    private final Board board; // Reference to the main game board logic (might be needed for final score display)
    private final GuiController guiController; // Reference to update UI (e.g., show game over screen)
    private final GameStateContext stateContext;

    /**
     * Constructs a GameOverState.
     * @param board Game board
     * @param guiController GUI controller
     */
    public GameOverState(Board board, GuiController guiController) {
        this(board, guiController, null);
    }

    public GameOverState(Board board, GuiController guiController, GameStateContext stateContext) {
        this.board = board;
        this.guiController = guiController;
        this.stateContext = stateContext;
    }

    @Override
    /**
     * Handles the DOWN event. Does nothing during game over.
     * @param event The MoveEvent containing event type and source.
     * @return DownData containing the current view data and no row clearing information.
     */
    public DownData onDownEvent(MoveEvent event) {
        // Do nothing during game over
        return new DownData(null, board.getViewData());
    }

    @Override
    /**
     * Handles the LEFT event. Does nothing during game over.
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the current brick position and shape.
     */
    public ViewData onLeftEvent(MoveEvent event) {
        // Do nothing during game over
        return board.getViewData();
    }

    @Override
    /**
     * Handles the RIGHT event. Does nothing during game over.
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the current brick position and shape.
     */
    public ViewData onRightEvent(MoveEvent event) {
        // Do nothing during game over
        return board.getViewData();
    }

    @Override
    /**
     * Handles the ROTATE event. Does nothing during game over.
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the current brick position and shape.
     */
    public ViewData onRotateEvent(MoveEvent event) {
        // Do nothing during game over
        return board.getViewData();
    }

    @Override
    /**
     * Handles the ROTATE_CCW event. Does nothing during game over.
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the current brick position and shape.
     */
    public ViewData onRotateCCWEvent(MoveEvent event) {
        // Do nothing during game over
        return board.getViewData();
    }

    @Override
    /**
     * Handles a request to pause the game. Pausing during game over doesn't make sense.
     * @return This GameOverState instance.
     */
    public GameState handlePauseRequest() {
        // Pausing during game over doesn't make sense
        return this;
    }

    @Override
    /**
     * Handles a request to start a new game. Delegates to PlayingState.
     * @return The state representing the start of a new game (e.g., PlayingState).
     */
    public GameState handleNewGameRequest() {
        // Start new game: delegate to PlayingState (which needs GameController ref for transitions)
        return new PlayingState(board, guiController, stateContext).handleNewGameRequest();
    }
}