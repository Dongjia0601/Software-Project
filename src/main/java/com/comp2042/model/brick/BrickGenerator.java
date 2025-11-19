package com.comp2042.model.brick;

/**
 * Strategy interface for Tetris brick generation during gameplay (Strategy Pattern).
 * Provides lookahead functionality for displaying the next brick.
 * 
 * @author Dong, Jia.
 */
public interface BrickGenerator {

    /**
     * Gets and consumes the current brick.
     * Advances the internal state to prepare the next brick.
     *
     * @return Current Brick instance
     */
    Brick getBrick();

    /**
     * Peeks at the next brick without consuming it (for preview display).
     *
     * @return Next Brick instance
     */
    Brick getNextBrick();
}