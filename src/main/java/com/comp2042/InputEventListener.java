package com.comp2042;

/**
 * Interface for handling all types of input events in the Tetris game.
 * Provides methods for processing movement, rotation, drop, hold, and game control events.
 * 
 * <p>This interface serves as the primary contract for event handling in the game,
 * supporting both basic and advanced Tetris features.</p>
 */
public interface InputEventListener {

    // Basic movement events
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

    // Rotation events
    /**
     * Handles the ROTATE event (clockwise rotation).
     *
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape.
     */
    ViewData onRotateEvent(MoveEvent event);

    /**
     * Handles the ROTATE_CCW event (counter-clockwise rotation).
     *
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape.
     */
    ViewData onRotateCCWEvent(MoveEvent event);

    // Drop events
    /**
     * Handles the HARD_DROP event (instant drop to bottom).
     *
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape.
     */
    ViewData onHardDropEvent(MoveEvent event);

    /**
     * Handles the SOFT_DROP event (faster downward movement).
     *
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape.
     */
    ViewData onSoftDropEvent(MoveEvent event);

    // Hold functionality
    /**
     * Handles the HOLD event (store current brick for later use).
     *
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and hold state.
     */
    ViewData onHoldEvent(MoveEvent event);

    // Game control events
    /**
     * Handles the PAUSE event.
     *
     * @param event The MoveEvent containing event type and source.
     */
    void onPauseEvent(MoveEvent event);

    /**
     * Handles the RESUME event.
     *
     * @param event The MoveEvent containing event type and source.
     */
    void onResumeEvent(MoveEvent event);

    /**
     * Handles the NEW_GAME event.
     *
     * @param event The MoveEvent containing event type and source.
     */
    void onNewGameEvent(MoveEvent event);

    /**
     * Handles the QUIT event.
     *
     * @param event The MoveEvent containing event type and source.
     */
    void onQuitEvent(MoveEvent event);

    // Legacy method for backward compatibility
    /**
     * Handles the request to start a new game.
     * @deprecated Use onNewGameEvent(MoveEvent) instead.
     */
    @Deprecated
    void createNewGame();
}