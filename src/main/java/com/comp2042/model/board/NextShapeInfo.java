package com.comp2042.model.board;

import com.comp2042.util.MatrixOperations;

/**
 * Immutable DTO containing potential next rotation shape and its index.
 * Used for lookahead collision checking before applying rotation.
 * 
 * @author Dong, Jia.
 */
public final class NextShapeInfo {

    private final int[][] shape;
    private final int position;

    /**
     * Constructs a NextShapeInfo.
     *
     * @param shape    Potential next shape matrix
     * @param position Shape index in rotation list
     */
    public NextShapeInfo(final int[][] shape, final int position) {
        this.shape = shape;
        this.position = position;
    }

    /**
     * Gets a defensive copy of the shape matrix.
     * 
     * @return a deep copy of the shape matrix
     */
    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }

    /**
     * Gets the rotation index in the brick's rotation list.
     * 
     * @return the rotation position index
     */
    public int getPosition() {
        return position;
    }
}