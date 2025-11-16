package com.comp2042.model.board;

import com.comp2042.util.MatrixOperations;

/**
 * Value object (DTO) containing information about the potential next shape
 * a brick could take after a rotation, along with its position index.
 */
public final class NextShapeInfo {

    private final int[][] shape; // The potential next shape matrix
    private final int position;  // The index of this shape within the brick's shape list

    /**
     * Constructs a NextShapeInfo object with the specified shape and position.
     *
     * @param shape    The potential next shape matrix.
     * @param position The index of the shape in the list.
     */
    public NextShapeInfo(final int[][] shape, final int position) {
        this.shape = shape;
        this.position = position;
    }

    /**
     * Gets the potential next shape matrix.
     * Returns a copy to prevent external modification.
     *
     * @return A copy of the shape matrix.
     */
    public int[][] getShape() {
        return MatrixOperations.copy(shape); // Return a copy
    }

    /**
     * Gets the index of the shape within the brick's list of shapes.
     *
     * @return The shape index.
     */
    public int getPosition() {
        return position;
    }
}