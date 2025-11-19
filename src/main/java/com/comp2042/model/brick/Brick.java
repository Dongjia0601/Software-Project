package com.comp2042.model.brick;

import java.util.List;

/**
 * Interface representing a Tetris brick (Tetromino).
 * Provides access to the brick's rotational states as shape matrices.
 * 
 * @author Dong, Jia.
 */
public interface Brick {

    /**
     * Gets all rotational states of this brick as shape matrices.
     * Each matrix represents one possible orientation.
     *
     * @return List of shape matrices for each rotation
     */
    List<int[][]> getShapeMatrix();
}