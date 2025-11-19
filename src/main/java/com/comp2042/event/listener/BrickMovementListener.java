package com.comp2042.event.listener;

import com.comp2042.dto.DownData;
import com.comp2042.dto.ViewData;
import com.comp2042.event.MoveEvent;

/**
 * Listener interface for brick movement and manipulation events (Interface Segregation Principle).
 * Handles movement, rotation, dropping, and holding operations.
 * 
 * @author Dong, Jia.
 */
public interface BrickMovementListener {
    
    /**
     * Handles DOWN event (user or automatic).
     *
     * @param event MoveEvent
     * @return DownData with view and row-clearing info
     */
    DownData onDownEvent(MoveEvent event);

    /**
     * Handles LEFT event.
     *
     * @param event MoveEvent
     * @return ViewData with updated position
     */
    ViewData onLeftEvent(MoveEvent event);

    /**
     * Handles RIGHT event.
     *
     * @param event MoveEvent
     * @return ViewData with updated position
     */
    ViewData onRightEvent(MoveEvent event);

    /**
     * Handles ROTATE event (clockwise).
     *
     * @param event MoveEvent
     * @return ViewData with rotated brick
     */
    ViewData onRotateEvent(MoveEvent event);

    /**
     * Handles ROTATE_CCW event (counterclockwise).
     *
     * @param event MoveEvent
     * @return ViewData with rotated brick
     */
    ViewData onRotateCCWEvent(MoveEvent event);

    /**
     * Handles HARD_DROP event (instant drop).
     *
     * @param event MoveEvent
     * @return ViewData with final position
     */
    ViewData onHardDropEvent(MoveEvent event);

    /**
     * Handles SOFT_DROP event (faster descent).
     *
     * @param event MoveEvent
     * @return ViewData with updated position
     */
    ViewData onSoftDropEvent(MoveEvent event);

    /**
     * Handles HOLD event (store brick for later).
     *
     * @param event MoveEvent
     * @return ViewData with hold state
     */
    ViewData onHoldEvent(MoveEvent event);
}

