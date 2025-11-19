package com.comp2042.event.listener;

import com.comp2042.event.MoveEvent;

/**
 * Combined listener interface for all input events (brick movement + game control).
 * Convenience interface that extends both BrickMovementListener and GameControlListener
 * following the Interface Segregation Principle.
 * 
 * @author Dong, Jia.
 */
public interface InputEventListener extends BrickMovementListener, GameControlListener {
    // This interface now extends the segregated interfaces
    // All methods are inherited from BrickMovementListener and GameControlListener
}