package com.comp2042.logic.bricks;

/**
 * Interface for generating Tetris bricks during gameplay.
 * Defines methods for retrieving the current brick and the next brick to be generated.
 */
public interface BrickGenerator {

    /**
     * Gets the current brick (likely the one currently falling).
     * This action typically advances the internal state to prepare the next brick.
     *
     * @return The current Brick instance.
     */
    Brick getBrick();

    /**
     * Gets the next brick that will be generated (often displayed in a preview area).
     *
     * @return The next Brick instance.
     */
    Brick getNextBrick();
}