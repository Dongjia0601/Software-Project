package com.comp2042.gameplay;

import com.comp2042.DownData;
import com.comp2042.MoveEvent;
import com.comp2042.ViewData;

/**
 * Interface defining methods for different game states (e.g., Playing, Paused, GameOver).
 * Each state handles input events and game logic updates according to its specific rules.
 */
public interface GameState {
    /**
     * Handles the DOWN event (both user-initiated and automatic descent).
     * @param event The MoveEvent containing event type and source.
     * @return DownData containing view and row-clearing information (may be null in some states like Paused).
     */
    DownData onDownEvent(MoveEvent event);

    /**
     * Handles the LEFT event.
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape (may be null in some states like Paused/GameOver).
     */
    ViewData onLeftEvent(MoveEvent event);

    /**
     * Handles the RIGHT event.
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape (may be null in some states like Paused/GameOver).
     */
    ViewData onRightEvent(MoveEvent event);

    /**
     * Handles the ROTATE event.
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape (may be null in some states like Paused/GameOver).
     */
    ViewData onRotateEvent(MoveEvent event);

    /**
     * Handles a request to pause or unpause the game.
     * @return The next state after handling the pause request (e.g., Playing -> Paused, Paused -> Playing).
     */
    GameState handlePauseRequest();

    /**
     * Handles a request to start a new game.
     * @return The state representing the start of a new game (e.g., PlayingState).
     */
    GameState handleNewGameRequest();
}