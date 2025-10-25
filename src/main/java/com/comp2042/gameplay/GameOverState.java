package com.comp2042.gameplay;

import com.comp2042.Board;
import com.comp2042.DownData;
import com.comp2042.GuiController;
import com.comp2042.MoveEvent;
import com.comp2042.ViewData;

/**
 * Represents the game over state of the Tetris game.
 * 
 * <p>This class handles the game when it has ended due to a game over condition.
 * During this state, most input events are ignored except for new game requests.
 * The final score and game statistics are preserved and can be displayed to the player.</p>
 * 
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Ignore all gameplay input events (movement, rotation, dropping)</li>
 *   <li>Handle new game requests to restart the game</li>
 *   <li>Maintain final game state and score information</li>
 *   <li>Display game over screen and final statistics</li>
 * </ul>
 */
public class GameOverState implements GameState {
    private final Board board; // Reference to the main game board logic (might be needed for final score display)
    private final GuiController guiController; // Reference to update UI (e.g., show game over screen)

    /**
     * Constructs a GameOverState instance.
     * @param board The game board instance.
     * @param guiController The GUI controller instance.
     */
    public GameOverState(Board board, GuiController guiController) {
        this.board = board;
        this.guiController = guiController;
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
        return new PlayingState(board, guiController, null).handleNewGameRequest();
    }
}