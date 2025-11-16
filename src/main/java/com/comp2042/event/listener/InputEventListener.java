package com.comp2042.event.listener;

import com.comp2042.event.MoveEvent;

/**
 * Interface for handling all types of input events in the Tetris game.
 * 
 * <p>This interface extends both BrickMovementListener and GameControlListener
 * to provide a complete event handling contract. It serves as a convenience
 * interface for classes that need to handle both brick movement and game control events.</p>
 * 
 * <p>This design follows the Interface Segregation Principle by:
 * <ul>
 *   <li>Separating concerns into BrickMovementListener and GameControlListener</li>
 *   <li>Allowing clients to implement only the interfaces they need</li>
 *   <li>Providing a combined interface for convenience when both are needed</li>
 * </ul>
 * </p>
 * 
 * <p>For classes that only need brick movement handling, implement BrickMovementListener.
 * For classes that only need game control handling, implement GameControlListener.
 * For classes that need both, implement InputEventListener (or both interfaces separately).</p>
 * 
 * @author Dong, Jia.
 */
public interface InputEventListener extends BrickMovementListener, GameControlListener {
    // This interface now extends the segregated interfaces
    // All methods are inherited from BrickMovementListener and GameControlListener
}