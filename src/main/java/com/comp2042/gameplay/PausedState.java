package com.comp2042.gameplay;

import com.comp2042.Board;
import com.comp2042.DownData;
import com.comp2042.GuiController;
import com.comp2042.MoveEvent;
import com.comp2042.ViewData;

/**
 * Represents the paused state of the Tetris game.
 * 
 * <p>This class handles the game when it is in a paused state. During pause,
 * most input events are ignored except for pause/unpause and new game requests.
 * The game state is preserved and can be resumed from the exact same position.</p>
 * 
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Ignore gameplay input events (movement, rotation, dropping)</li>
 *   <li>Handle pause/unpause toggle requests</li>
 *   <li>Process new game requests to restart</li>
 *   <li>Maintain current game state for resumption</li>
 * </ul>
 * 
 * @author Dong, Jia.
 */
public class PausedState implements GameState {
    private final Board board; // Reference to the main game board logic to get current state
    private final GuiController guiController; // Reference to update UI (e.g., show pause screen)

    /**
     * Constructs a PausedState instance.
     * @param board The game board instance.
     * @param guiController The GUI controller instance.
     */
    public PausedState(Board board, GuiController guiController) {
        this.board = board;
        this.guiController = guiController;
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
        return new PlayingState(board, guiController, null); // GameController ref not needed here, will be set later
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
        return new PlayingState(board, guiController, null).handleNewGameRequest();
    }
}