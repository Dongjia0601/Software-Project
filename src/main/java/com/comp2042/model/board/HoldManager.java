package com.comp2042.model.board;

import com.comp2042.model.brick.Brick;
import com.comp2042.model.board.BrickRotator;

/**
 * Manages the hold/swap mechanic for storing and swapping the current brick.
 * Enforces one hold per piece placement to prevent abuse.
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

    /** Gets the currently held brick. */
    Brick getHeldBrick() {
        return heldBrick;
    }

    /** Re-enables hold for the next brick. */
    void enableHold() {
        canHold = true;
    }

    /** Resets hold state (clears held brick and re-enables). */
    void reset() {
        heldBrick = null;
        canHold = true;
    }

    /** Restores hold state from memento. */
    void restoreState(Brick heldBrick, boolean canHold) {
        this.heldBrick = heldBrick;
        this.canHold = canHold;
    }

    /** Checks if hold is currently available. */
    boolean canHold() {
        return canHold;
    }
}

