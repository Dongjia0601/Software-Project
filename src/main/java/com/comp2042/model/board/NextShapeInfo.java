package com.comp2042.model.board;

import com.comp2042.util.MatrixOperations;

/**
 * Immutable DTO containing potential next rotation shape and its index.
 * Used for lookahead collision checking before applying rotation.
 */
public final class NextShapeInfo {

    private final int[][] shape; // The potential next shape matrix
    private final int position;  // The index of this shape within the brick's shape list

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

    /** Gets the shape matrix (defensive copy). */
    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }

    /** Gets the rotation index. */
    public int getPosition() {
        return position;
    }
}