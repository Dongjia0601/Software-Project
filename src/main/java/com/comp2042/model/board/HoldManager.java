package com.comp2042.model.board;

import com.comp2042.model.brick.Brick;

/**
 * Manages the hold/swap mechanic for storing and swapping the current brick.
 * Enforces one hold per piece placement to prevent abuse.
 * 
 * @author Dong, Jia.
 */
class HoldManager {

    private Brick heldBrick;
    private boolean canHold = true;

    /**
     * Attempts to hold/swap the current brick.
     * 
     * @param rotator Current brick rotator
     * @param spawnNewBrick Callback to spawn new brick (if hold is empty)
     * @param resetPosition Callback to reset brick position (if swapping)
     * @return true if hold succeeded, false if already used this piece
     */
    boolean holdBrick(BrickRotator rotator, Runnable spawnNewBrick, Runnable resetPosition) {
        if (!canHold) {
            return false;
        }

        Brick currentBrick = rotator.getBrick();

        if (heldBrick == null) {
            heldBrick = currentBrick;
            spawnNewBrick.run();
        } else {
            Brick swappedBrick = heldBrick;
            heldBrick = currentBrick;
            rotator.setBrick(swappedBrick);
            resetPosition.run();
        }

        canHold = false;
        return true;
    }

    /**
     * Gets the currently held brick.
     * 
     * @return the held brick, or null if no brick is held
     */
    Brick getHeldBrick() {
        return heldBrick;
    }

    /**
     * Re-enables hold functionality for the next brick.
     * Called when a brick is placed to allow holding the next piece.
     */
    void enableHold() {
        canHold = true;
    }

    /**
     * Resets hold state completely.
     * Clears the held brick and re-enables hold functionality.
     */
    void reset() {
        heldBrick = null;
        canHold = true;
    }

    /**
     * Restores hold state from a memento.
     * Used for game state restoration (Memento Pattern).
     * 
     * @param heldBrick the brick to restore as held
     * @param canHold whether hold is currently available
     */
    void restoreState(Brick heldBrick, boolean canHold) {
        this.heldBrick = heldBrick;
        this.canHold = canHold;
    }

    /**
     * Checks if hold functionality is currently available.
     * 
     * @return true if hold can be used, false if already used for current piece
     */
    boolean canHold() {
        return canHold;
    }
}

