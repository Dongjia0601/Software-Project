package com.comp2042.model.board;

import com.comp2042.model.brick.Brick;
import com.comp2042.model.board.BrickRotator;

/**
 * Encapsulates the hold/ swap behaviour so {@link SimpleBoard} no longer needs
 * to track hold state directly.
 */
class HoldManager {

    private Brick heldBrick;
    private boolean canHold = true;

    boolean holdBrick(BrickRotator rotator, Runnable spawnNewBrick, Runnable resetPosition) {
        if (!canHold) {
            return false;
        }

        Brick currentBrick = rotator.getBrick();

        if (heldBrick == null) {
            heldBrick = currentBrick;
            spawnNewBrick.run();
        } else {
            Brick temp = heldBrick;
            heldBrick = currentBrick;
            rotator.setBrick(temp);
            resetPosition.run();
        }

        canHold = false;
        return true;
    }

    Brick getHeldBrick() {
        return heldBrick;
    }

    void enableHold() {
        canHold = true;
    }

    void reset() {
        heldBrick = null;
        canHold = true;
    }

    void restoreState(Brick heldBrick, boolean canHold) {
        this.heldBrick = heldBrick;
        this.canHold = canHold;
    }

    boolean canHold() {
        return canHold;
    }
}

