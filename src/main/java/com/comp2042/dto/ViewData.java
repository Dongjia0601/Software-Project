package com.comp2042.dto;

import com.comp2042.util.MatrixOperations;

/**
 * Immutable DTO containing rendering data for the current game state.
 * Includes current brick, next brick, hold brick, and ghost piece positions.
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
     * Constructs a ViewData with all rendering information.
     *
     * @param brickData      Current brick shape matrix
     * @param xPosition      Current brick x-coordinate
     * @param yPosition      Current brick y-coordinate
     * @param ghostYPosition Ghost piece y-coordinate (-1 if disabled)
     * @param nextBrickData  Next brick shape matrix
     * @param holdBrickData  Hold brick shape matrix (null if empty)
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
     * Constructs a ViewData without hold/ghost features (backward compatibility).
     *
     * @param brickData     Current brick shape matrix
     * @param xPosition     Current brick x-coordinate
     * @param yPosition     Current brick y-coordinate
     * @param nextBrickData Next brick shape matrix
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData) {
        this(brickData, xPosition, yPosition, -1, nextBrickData, null);
    }

    /**
     * Gets the current brick shape matrix.
     * Returns a defensive copy.
     *
     * @return Copy of brick shape matrix
     */
    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData); // Return a copy
    }

    /**
     * Gets the current brick x-coordinate.
     *
     * @return X-coordinate (column)
     */
    public int getXPosition() {
        return xPosition;
    }

    /**
     * Gets the current brick y-coordinate.
     *
     * @return Y-coordinate (row)
     */
    public int getYPosition() {
        return yPosition;
    }
    
    /**
     * Gets the ghost piece landing y-coordinate.
     *
     * @return Ghost y-coordinate, or -1 if disabled
     */
    public int getGhostYPosition() {
        return ghostYPosition;
    }

    /**
     * Gets the next brick shape matrix.
     * Returns a defensive copy.
     *
     * @return Copy of next brick shape matrix
     */
    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData); // Return a copy
    }

    /**
     * Gets the hold brick shape matrix.
     * Returns a defensive copy.
     *
     * @return Copy of hold brick shape matrix, or null if empty
     */
    public int[][] getHoldBrickData() {
        return holdBrickData != null ? MatrixOperations.copy(holdBrickData) : null;
    }

    /**
     * Returns a string representation of this ViewData.
     *
     * @return String representation
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
     * Checks equality with another object.
     *
     * @param obj Object to compare
     * @return true if equal, false otherwise
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
     * Returns hash code for this ViewData.
     *
     * @return Hash code value
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