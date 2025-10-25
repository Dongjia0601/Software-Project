package com.comp2042;

import com.comp2042.logic.bricks.Brick;

/**
 * Interface defining the core game logic operations for the Tetris board.
 * Implementations handle the state of the board grid, the active brick,
 * movement rules, row clearing, and game state management.
 */
public interface Board {

    /**
     * Attempts to move the currently falling brick one position down.
     * Checks for collisions with the bottom of the board or other placed bricks.
     *
     * @return true if the move was successful, false if a collision occurred (brick lands).
     */
    boolean moveBrickDown();

    /**
     * Instantly drops the currently falling brick to the bottom of the board.
     * This is the hard drop functionality that moves the brick as far down as possible.
     *
     * @return the number of positions the brick was moved down.
     */
    int hardDropBrick();

    /**
     * Attempts to move the currently falling brick one position to the left.
     * Checks for collisions with the left wall or other placed bricks.
     *
     * @return true if the move was successful, false if a collision occurred.
     */
    boolean moveBrickLeft();

    /**
     * Attempts to move the currently falling brick one position to the right.
     * Checks for collisions with the right wall or other placed bricks.
     *
     * @return true if the move was successful, false if a collision occurred.
     */
    boolean moveBrickRight();

    /**
     * Attempts to rotate the currently falling brick.
     * Checks for collisions in the new orientation.
     *
     * @return true if the rotation was successful, false if a collision occurred.
     */
    boolean rotateLeftBrick(); // Note: Method name suggests 'left' rotation logic

    /**
     * Attempts to rotate the currently falling brick counter-clockwise.
     * Checks for collisions in the new orientation.
     *
     * @return true if the rotation was successful, false if a collision occurred.
     */
    boolean rotateRightBrick();

    /**
     * Creates a new brick for the board.
     * Gets a brick from the generator, sets it in the rotator, positions it at the top/middle,
     * and checks if this initial position causes a collision (indicating game over).
     *
     * @return true if a collision occurs immediately (game over), false otherwise.
     */
    boolean createNewBrick();

    /**
     * Gets the current state of the board grid.
     * The returned matrix represents the occupied/unoccupied cells of the board.
     *
     * @return The current board matrix.
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
}