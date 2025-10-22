package com.comp2042;

/**
 * Immutable data transfer object containing the results of a DOWN event.
 * Encapsulates both the visual state (ViewData) and line clearing information (ClearRow).
 * 
 * <p>This class represents the complete result of a brick movement downward,
 * including any lines that were cleared and the updated game state.</p>
 */
public final class DownData {
    private final ClearRow clearRow;
    private final ViewData viewData;
    private final boolean brickLanded;
    private final int scoreBonus;

    /**
     * Constructs a DownData object with the specified components.
     *
     * @param clearRow The line clearing information.
     * @param viewData The updated visual state.
     */
    public DownData(ClearRow clearRow, ViewData viewData) {
        this(clearRow, viewData, false, 0);
    }

    /**
     * Constructs a DownData object with all parameters.
     *
     * @param clearRow The line clearing information.
     * @param viewData The updated visual state.
     * @param brickLanded Whether the brick has landed and can't move further.
     * @param scoreBonus Additional score points earned from this move.
     */
    public DownData(ClearRow clearRow, ViewData viewData, boolean brickLanded, int scoreBonus) {
        this.clearRow = clearRow;
        this.viewData = viewData;
        this.brickLanded = brickLanded;
        this.scoreBonus = scoreBonus;
    }

    /**
     * Gets the line clearing information.
     *
     * @return The ClearRow object containing line clearing details.
     */
    public ClearRow getClearRow() {
        return clearRow;
    }

    /**
     * Gets the updated visual state.
     *
     * @return The ViewData object containing the current game state.
     */
    public ViewData getViewData() {
        return viewData;
    }

    /**
     * Checks if the brick has landed and can't move further.
     *
     * @return true if the brick has landed, false otherwise.
     */
    public boolean isBrickLanded() {
        return brickLanded;
    }

    /**
     * Gets the additional score points earned from this move.
     *
     * @return The score bonus points.
     */
    public int getScoreBonus() {
        return scoreBonus;
    }

    /**
     * Checks if any lines were cleared in this move.
     *
     * @return true if lines were cleared, false otherwise.
     */
    public boolean hasLinesCleared() {
        return clearRow != null && clearRow.getLinesRemoved() > 0;
    }

    /**
     * Gets the total score earned from this move (including line clearing bonus).
     *
     * @return The total score points.
     */
    public int getTotalScore() {
        int total = scoreBonus;
        if (clearRow != null) {
            total += clearRow.getScoreBonus();
        }
        return total;
    }

    /**
     * Returns a string representation of this DownData object.
     *
     * @return A string representation of the down data.
     */
    @Override
    public String toString() {
        return String.format("DownData{landed=%b, scoreBonus=%d, linesCleared=%d, totalScore=%d}", 
                           brickLanded, scoreBonus, 
                           clearRow != null ? clearRow.getLinesRemoved() : 0, getTotalScore());
    }

    /**
     * Checks if this DownData is equal to another object.
     *
     * @param obj The object to compare with.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        DownData downData = (DownData) obj;
        return brickLanded == downData.brickLanded &&
               scoreBonus == downData.scoreBonus &&
               (clearRow != null ? clearRow.equals(downData.clearRow) : downData.clearRow == null) &&
               (viewData != null ? viewData.equals(downData.viewData) : downData.viewData == null);
    }

    /**
     * Returns a hash code for this DownData object.
     *
     * @return A hash code for the down data.
     */
    @Override
    public int hashCode() {
        int result = clearRow != null ? clearRow.hashCode() : 0;
        result = 31 * result + (viewData != null ? viewData.hashCode() : 0);
        result = 31 * result + (brickLanded ? 1 : 0);
        result = 31 * result + scoreBonus;
        return result;
    }
}
