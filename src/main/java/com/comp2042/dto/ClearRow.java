package com.comp2042.dto;

import com.comp2042.util.MatrixOperations;

/**
 * Immutable DTO containing row-clearing operation results.
 * Encapsulates lines removed, updated board matrix, and score bonus.
 */
public final class ClearRow {

    private final int linesRemoved; // Number of lines that were cleared
    private final int[][] newMatrix; // The board matrix after lines have been removed
    private final int scoreBonus; // The score bonus earned from clearing the lines

    /**
     * Constructs a ClearRow with operation results.
     *
     * @param linesRemoved Number of lines cleared
     * @param newMatrix    Updated board matrix
     * @param scoreBonus   Score bonus earned
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
    }

    /**
     * Gets the number of lines removed.
     *
     * @return Lines removed count
     */
    public int getLinesRemoved() {
        return linesRemoved;
    }

    /**
     * Gets the board matrix after clearing.
     * Returns a defensive copy to prevent external modification.
     *
     * @return Copy of the updated board matrix
     */
    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }

    /**
     * Gets the score bonus earned.
     *
     * @return Score bonus points
     */
    public int getScoreBonus() {
        return scoreBonus;
    }
}