package com.comp2042;

/**
 * Interface defining methods for handling input events within the game.
 * Implementations receive events like move left/right/down, rotate, and new game,
 * and process them according to the game's logic and current state.
 */
public interface InputEventListener {

    /**
     * Handles the DOWN event (both user-initiated and automatic descent).
     *
     * @param event The MoveEvent containing event type and source.
     * @return DownData containing view and row-clearing information.
     */
    DownData onDownEvent(MoveEvent event);

    /**
     * Handles the LEFT event.
     *
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape.
     */
    ViewData onLeftEvent(MoveEvent event);

    /**
     * Handles the RIGHT event.
     *
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape.
     */
    ViewData onRightEvent(MoveEvent event);

    /**
     * Handles the ROTATE event.
     *
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape.
     */
    ViewData onRotateEvent(MoveEvent event);

    /**
     * Handles the request to start a new game.
     */
    void createNewGame();
}