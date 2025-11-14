package com.comp2042;

/**
 * Value object (DTO) containing the necessary data for the GUI to render the current game state.
 * Includes the shape and position of the currently falling brick and the shape of the next brick.
 * 
 * @author Dong, Jia.
 */
public final class ViewData {

    private final int[][] brickData; // The shape matrix of the currently falling brick
    private final int xPosition;     // The x-coordinate (column) of the currently falling brick
    private final int yPosition;     // The y-coordinate (row) of the currently falling brick
    private final int ghostYPosition; // The y-coordinate (row) where the ghost brick would land (-1 if not calculated)
    private final int[][] nextBrickData; // The shape matrix of the next brick to be generated
    private final int[][] holdBrickData; // The shape matrix of the held brick (null if no brick is held)

    /**
     * Constructs a ViewData object with the specified data.
     *
     * @param brickData      The shape matrix of the current brick.
     * @param xPosition      The x-coordinate of the current brick.
     * @param yPosition      The y-coordinate of the current brick.
     * @param ghostYPosition The y-coordinate where the ghost brick would land (-1 if not calculated).
     * @param nextBrickData  The shape matrix of the next brick.
     * @param holdBrickData  The shape matrix of the held brick (null if no brick is held).
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition, int ghostYPosition, int[][] nextBrickData, int[][] holdBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.ghostYPosition = ghostYPosition;
        this.nextBrickData = nextBrickData;
        this.holdBrickData = holdBrickData;
    }

    /**
     * Constructs a ViewData object with the specified data (backward compatibility).
     * Hold brick data will be set to null, ghost position will be set to -1.
     *
     * @param brickData      The shape matrix of the current brick.
     * @param xPosition      The x-coordinate of the current brick.
     * @param yPosition      The y-coordinate of the current brick.
     * @param nextBrickData  The shape matrix of the next brick.
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData) {
        this(brickData, xPosition, yPosition, -1, nextBrickData, null);
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
     * Gets the y-coordinate (row) where the ghost brick would land.
     *
     * @return The ghost brick y-coordinate, or -1 if not calculated.
     */
    public int getGhostYPosition() {
        return ghostYPosition;
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

    /**
     * Gets the shape matrix of the held brick.
     * Returns a copy to prevent external modification.
     *
     * @return A copy of the held brick's shape matrix, or null if no brick is held.
     */
    public int[][] getHoldBrickData() {
        return holdBrickData != null ? MatrixOperations.copy(holdBrickData) : null;
    }

    /**
     * Returns a string representation of this ViewData object.
     *
     * @return A string representation of this ViewData object.
     */
    @Override
    public String toString() {
        return "ViewData{" +
                "xPosition=" + xPosition +
                ", yPosition=" + yPosition +
                ", ghostYPosition=" + ghostYPosition +
                ", hasBrickData=" + (brickData != null) +
                ", hasNextBrickData=" + (nextBrickData != null) +
                ", hasHoldBrickData=" + (holdBrickData != null) +
                '}';
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ViewData viewData = (ViewData) obj;
        return xPosition == viewData.xPosition &&
               yPosition == viewData.yPosition &&
               ghostYPosition == viewData.ghostYPosition &&
               java.util.Arrays.deepEquals(brickData, viewData.brickData) &&
               java.util.Arrays.deepEquals(nextBrickData, viewData.nextBrickData) &&
               java.util.Arrays.deepEquals(holdBrickData, viewData.holdBrickData);
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        int result = xPosition;
        result = 31 * result + yPosition;
        result = 31 * result + ghostYPosition;
        result = 31 * result + java.util.Arrays.deepHashCode(brickData);
        result = 31 * result + java.util.Arrays.deepHashCode(nextBrickData);
        result = 31 * result + java.util.Arrays.deepHashCode(holdBrickData);
        return result;
    }
}