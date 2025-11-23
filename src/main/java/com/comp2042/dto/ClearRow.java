package com.comp2042.dto;

import com.comp2042.util.MatrixOperations;

import java.util.Collections;
import java.util.List;

/**
 * Immutable DTO containing row-clearing operation results.
 * Encapsulates lines removed, updated board matrix, and score bonus.
 * 
 * @author Dong, Jia.
 */
public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;
    private final int scoreBonus;
    private final List<Integer> clearedRows;

    /**
     * Constructs a ClearRow with operation results.
     *
     * @param linesRemoved Number of lines cleared
     * @param newMatrix    Updated board matrix
     * @param scoreBonus   Score bonus earned
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this(linesRemoved, newMatrix, scoreBonus, Collections.emptyList());
    }

    /**
     * Constructs a ClearRow with operation results including cleared row indices.
     *
     * @param linesRemoved Number of lines cleared
     * @param newMatrix    Updated board matrix
     * @param scoreBonus   Score bonus earned
     * @param clearedRows  List of indices of the cleared rows
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus, List<Integer> clearedRows) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
        this.clearedRows = (clearedRows != null) ? List.copyOf(clearedRows) : Collections.emptyList();
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
     * Gets the list of indices of the cleared rows.
     *
     * @return List of cleared row indices
     */
    public List<Integer> getClearedRows() {
        return clearedRows;
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