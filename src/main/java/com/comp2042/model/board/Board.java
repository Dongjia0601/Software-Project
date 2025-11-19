package com.comp2042.model.board;

import com.comp2042.model.brick.Brick;
import com.comp2042.dto.ViewData;
import com.comp2042.dto.ClearRow;
import com.comp2042.model.score.Score;

/**
 * Core game logic interface for the Tetris board.
 * Handles board state, brick movement, rotation, collision detection, and row clearing.
 * 
 * @author Dong, Jia.
 */
public interface Board {

    /**
     * Moves the falling brick one position down.
     *
     * @return true if successful, false if collision occurred (brick lands)
     */
    boolean moveBrickDown();

    /**
     * Hard drops the brick to the bottom instantly.
     *
     * @return Number of rows dropped
     */
    int hardDropBrick();

    /**
     * Moves the brick one position left.
     *
     * @return true if successful, false if collision occurred
     */
    boolean moveBrickLeft();

    /**
     * Moves the brick one position right.
     *
     * @return true if successful, false if collision occurred
     */
    boolean moveBrickRight();

    /**
     * Rotates the brick clockwise.
     *
     * @return true if successful, false if collision occurred
     */
    boolean rotateLeftBrick();

    /**
     * Rotates the brick counterclockwise.
     *
     * @return true if successful, false if collision occurred
     */
    boolean rotateRightBrick();

    /**
     * Spawns a new brick at the top of the board.
     *
     * @return true if spawn collision occurs (game over), false otherwise
     */
    boolean createNewBrick();

    /**
     * Gets the current board grid state.
     *
     * @return Board matrix (0 = empty, non-zero = occupied)
     */
    int[][] getBoardMatrix();

    /**
     * Gets the current view data (brick shape, position, next brick shape).
     *
     * @return A ViewData object containing the necessary information for the GUI.
     */
    ViewData getViewData();

    /**
     * Merges the currently falling brick permanently onto the board grid.
     * This happens when the brick lands after moving down.
     */
    void mergeBrickToBackground();

    /**
     * Checks the board for completed rows, removes them, shifts the remaining rows down,
     * calculates the score bonus, and returns the results.
     *
     * @return A ClearRow object containing details about the cleared lines.
     */
    ClearRow clearRows();

    /**
     * Gets the current score object.
     *
     * @return The Score instance.
     */
    Score getScore();

    /**
     * Gets the total number of lines cleared in the current game.
     *
     * @return The total lines cleared.
     */
    int getTotalLinesCleared();

    /**
     * Resets the board state for a new game.
     * Clears the board matrix, resets the score, and creates the first new brick.
     */
    void newGame();
    
    /**
     * Holds the current brick for later use.
     * If no brick is currently held, stores the current brick and creates a new one.
     * If a brick is already held, swaps the current brick with the held brick.
     * 
     * @return true if the hold operation was successful, false otherwise
     */
    boolean holdBrick();
    
    /**
     * Gets the currently held brick.
     * 
     * @return the held brick, or null if no brick is held
     */
    Brick getHeldBrick();
    
    /**
     * Calculates the ghost brick position (where the brick would land if dropped straight down).
     * The ghost brick has the same shape and x-position as the current brick, but with the y-position
     * adjusted to show where it would land.
     * 
     * @return the y-coordinate (row) where the ghost brick would land, or -1 if calculation fails
     */
    int getGhostBrickY();

    /**
     * Adds a garbage line to the bottom of the board.
     * A garbage line is a full row with one random hole.
     * 
     * @return true if adding the garbage line causes game over
     */
    boolean addGarbageLine();

    /**
     * Removes garbage lines from the bottom of the board.
     * Used for combo bonuses in versus mode.
     * 
     * @param linesToRemove the number of garbage lines to remove
     * @return the actual number of lines removed
     */
    int removeGarbageLines(int linesToRemove);
}