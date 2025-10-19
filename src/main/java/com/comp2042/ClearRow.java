package com.comp2042;

/**
 * Value object (DTO) containing the results of a row-clearing operation.
 * Holds the number of lines removed, the new board matrix after clearing,
 * and the score bonus earned.
 */
public final class ClearRow {

    private final int linesRemoved; // Number of lines that were cleared
    private final int[][] newMatrix; // The board matrix after lines have been removed
    private final int scoreBonus; // The score bonus earned from clearing the lines

    /**
     * Constructs a ClearRow object with the specified results.
     *
     * @param linesRemoved The number of lines cleared.
     * @param newMatrix    The updated board matrix.
     * @param scoreBonus   The score bonus earned.
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
    }

    /**
     * Gets the number of lines that were removed.
     *
     * @return The number of lines removed.
     */
    public int getLinesRemoved() {
        return linesRemoved;
    }

    /**
     * Gets the new board matrix after the lines were cleared.
     * Returns a copy to prevent external modification.
     *
     * @return A copy of the new board matrix.
     */
    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix); // Return a copy
    }

    /**
     * Gets the score bonus earned from clearing the lines.
     *
     * @return The score bonus.
     */
    public int getScoreBonus() {
        return scoreBonus;
    }
}