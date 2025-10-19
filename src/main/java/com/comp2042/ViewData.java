package com.comp2042;

/**
 * Value object (DTO) containing the necessary data for the GUI to render the current game state.
 * Includes the shape and position of the currently falling brick and the shape of the next brick.
 */
public final class ViewData {

    private final int[][] brickData; // The shape matrix of the currently falling brick
    private final int xPosition;     // The x-coordinate (column) of the currently falling brick
    private final int yPosition;     // The y-coordinate (row) of the currently falling brick
    private final int[][] nextBrickData; // The shape matrix of the next brick to be generated

    /**
     * Constructs a ViewData object with the specified data.
     *
     * @param brickData      The shape matrix of the current brick.
     * @param xPosition      The x-coordinate of the current brick.
     * @param yPosition      The y-coordinate of the current brick.
     * @param nextBrickData  The shape matrix of the next brick.
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
    }

    /**
     * Gets the shape matrix of the currently falling brick.
     * Returns a copy to prevent external modification.
     *
     * @return A copy of the brick's shape matrix.
     */
    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData); // Return a copy
    }

    /**
     * Gets the x-coordinate (column) of the currently falling brick.
     *
     * @return The x-coordinate.
     */
    public int getxPosition() {
        return xPosition;
    }

    /**
     * Gets the y-coordinate (row) of the currently falling brick.
     *
     * @return The y-coordinate.
     */
    public int getyPosition() {
        return yPosition;
    }

    /**
     * Gets the shape matrix of the next brick to be generated.
     * Returns a copy to prevent external modification.
     *
     * @return A copy of the next brick's shape matrix.
     */
    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData); // Return a copy
    }
}