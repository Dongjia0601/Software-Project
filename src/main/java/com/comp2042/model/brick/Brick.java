package com.comp2042.model.brick;

import java.util.List;

/**
 * Interface representing a Tetris brick (Tetromino).
 * Defines the method to retrieve the list of possible shape matrices for this brick type.
 * 
 * @author Dong, Jia.
 */
public interface Brick {

    /**
     * Gets the list of shape matrices representing the different rotational states of this brick.
     * Each matrix in the list corresponds to a possible orientation of the brick.
     *
     * @return A List of 2D integer arrays representing the brick's shapes.
     */
    List<int[][]> getShapeMatrix();
}