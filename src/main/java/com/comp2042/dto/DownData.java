package com.comp2042.dto;

import java.util.Objects;

/**
 * Immutable DTO containing complete results of a downward brick movement.
 * Encapsulates visual state, line clearing information, landing status, and score bonus.
 *
 * @author Dong, Jia.
 */
public record DownData(ClearRow clearRow, ViewData viewData, boolean brickLanded, int scoreBonus) {
    /**
     * Constructs a DownData with basic components.
     *
     * @param clearRow Line clearing information
     * @param viewData Updated visual state
     */
    public DownData(ClearRow clearRow, ViewData viewData) {
        this(clearRow, viewData, false, 0);
    }

    /**
     * Constructs a DownData with all parameters.
     *
     * @param clearRow    Line clearing information
     * @param viewData    Updated visual state
     * @param brickLanded Whether the brick has landed
     * @param scoreBonus  Additional score points earned
     */
    public DownData {
    }

    /**
     * Gets the line clearing information.
     *
     * @return ClearRow with clearing details
     */
    @Override
    public ClearRow clearRow() {
        return clearRow;
    }

    /**
     * Gets the updated visual state.
     *
     * @return ViewData with current game state
     */
    @Override
    public ViewData viewData() {
        return viewData;
    }

    /**
     * Checks if the brick has landed.
     *
     * @return true if landed, false otherwise
     */
    @Override
    public boolean brickLanded() {
        return brickLanded;
    }

    /**
     * Gets the score bonus from this move.
     *
     * @return Score bonus points
     */
    @Override
    public int scoreBonus() {
        return scoreBonus;
    }

    /**
     * Checks if any lines were cleared.
     *
     * @return true if lines cleared, false otherwise
     */
    public boolean hasLinesCleared() {
        return clearRow != null && clearRow.getLinesRemoved() > 0;
    }

    /**
     * Gets the total score including line clearing bonus.
     *
     * @return Total score points
     */
    public int getTotalScore() {
        int total = scoreBonus;
        if (clearRow != null) {
            total += clearRow.getScoreBonus();
        }
        return total;
    }

    /**
     * Returns a string representation of this DownData.
     *
     * @return String representation
     */
    @Override
    public String toString() {
        return String.format("DownData{landed=%b, scoreBonus=%d, linesCleared=%d, totalScore=%d}",
                brickLanded, scoreBonus,
                clearRow != null ? clearRow.getLinesRemoved() : 0, getTotalScore());
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

        DownData downData = (DownData) obj;
        return brickLanded == downData.brickLanded &&
                scoreBonus == downData.scoreBonus &&
                (Objects.equals(clearRow, downData.clearRow)) &&
                (Objects.equals(viewData, downData.viewData));
    }

}
