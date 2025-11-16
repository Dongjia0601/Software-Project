package com.comp2042;

/**
 * Interface for handling brick movement and manipulation events.
 * Implements Interface Segregation Principle by separating brick movement
 * concerns from game control concerns.
 * 
 * <p>This interface focuses solely on brick-related operations:
 * movement, rotation, dropping, and holding.</p>
 * 
 * @author Dong, Jia.
 */
public interface BrickMovementListener {
    
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
     * Handles the ROTATE event (clockwise rotation).
     *
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape.
     */
    ViewData onRotateEvent(MoveEvent event);

    /**
     * Handles the ROTATE_CCW event (counterclockwise rotation).
     *
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape.
     */
    ViewData onRotateCCWEvent(MoveEvent event);

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

    /**
     * Handles the HOLD event (store current brick for later use).
     *
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and hold state.
     */
    ViewData onHoldEvent(MoveEvent event);
}

