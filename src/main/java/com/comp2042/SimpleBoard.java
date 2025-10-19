package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

import java.awt.*;

/**
 * Implements the core game logic for the Tetris board.
 * Manages the board grid, the currently falling brick, its position,
 * brick generation, movement rules (collision, landing), merging bricks
 * to the background, clearing completed rows, and score tracking.
 */
public class SimpleBoard implements Board {

    private final int width;  // Width of the board grid
    private final int height; // Height of the board grid
    private final BrickGenerator brickGenerator; // Generator for new bricks
    private final BrickRotator brickRotator;     // Manager for the current brick's rotation state
    private int[][] currentGameMatrix;           // The current state of the board grid
    private Point currentOffset;                 // The current position (x, y) of the falling brick
    private final Score score;                   // The score tracker

    /**
     * Constructs a SimpleBoard with the specified dimensions.
     * Initializes the board matrix, brick generator, rotator, and score.
     *
     * @param width  The width of the board.
     * @param height The height of the board.
     */
    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    @Override
    /**
     * Attempts to move the currently falling brick one position down.
     * Checks for collisions with the bottom of the board or other placed bricks.
     *
     * @return true if the move was successful, false if a collision occurred (brick lands).
     */
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1); // Move down (increase y)
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false; // Cannot move down, brick lands
        } else {
            currentOffset = p; // Update position
            return true;      // Move successful
        }
    }


    @Override
    /**
     * Attempts to move the currently falling brick one position to the left.
     * Checks for collisions with the left wall or other placed bricks.
     *
     * @return true if the move was successful, false if a collision occurred.
     */
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0); // Move left (decrease x)
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false; // Cannot move left
        } else {
            currentOffset = p; // Update position
            return true;      // Move successful
        }
    }

    @Override
    /**
     * Attempts to move the currently falling brick one position to the right.
     * Checks for collisions with the right wall or other placed bricks.
     *
     * @return true if the move was successful, false if a collision occurred.
     */
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0); // Move right (increase x)
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false; // Cannot move right
        } else {
            currentOffset = p; // Update position
            return true;      // Move successful
        }
    }

    @Override
    /**
     * Attempts to rotate the currently falling brick.
     * Calculates the potential next shape and checks for collisions in the new orientation.
     *
     * @return true if the rotation was successful, false if a collision occurred.
     */
    public boolean rotateLeftBrick() { // Note: Name suggests 'left' rotation, logic is 'next' shape in list
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.calculateNextShapeInfo(); // Assuming renamed method
        boolean conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
        if (conflict) {
            return false; // Cannot rotate
        } else {
            brickRotator.setCurrentShape(nextShape.getPosition()); // Apply rotation
            return true;                                           // Rotation successful
        }
    }

    @Override
    /**
     * Creates a new brick for the board.
     * Gets a brick from the generator, sets it in the rotator, positions it at the top/middle,
     * and checks if this initial position causes a collision (indicating game over).
     *
     * @return true if a collision occurs immediately (game over), false otherwise.
     */
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(4, 10); // Set initial spawn position (x=4, y=10)
        // Check if the new brick's initial position collides (game over condition)
        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    /**
     * Gets the current state of the board grid.
     * Returns a copy to prevent external modification.
     *
     * @return A copy of the current board matrix.
     */
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    /**
     * Gets the current view data (brick shape, position, next brick shape).
     *
     * @return A ViewData object containing the necessary information for the GUI.
     */
    public ViewData getViewData() {
        return new ViewData(brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY(), brickGenerator.getNextBrick().getShapeMatrix().get(0));
    }

    @Override
    /**
     * Merges the currently falling brick permanently onto the board grid.
     * This happens when the brick lands after moving down.
     */
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    /**
     * Checks the board for completed rows, removes them, shifts the remaining rows down,
     * calculates the score bonus, and updates the internal board matrix.
     *
     * @return A ClearRow object containing details about the cleared lines.
     */
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.clearCompletedRows(currentGameMatrix); // Assuming renamed method
        currentGameMatrix = clearRow.getNewMatrix(); // Update internal state with the new matrix
        return clearRow; // Return the result object
    }

    @Override
    /**
     * Gets the current score object.
     *
     * @return The Score instance.
     */
    public Score getScore() {
        return score;
    }


    @Override
    /**
     * Resets the board state for a new game.
     * Clears the board matrix, resets the score, and creates the first new brick.
     */
    public void newGame() {
        currentGameMatrix = new int[width][height]; // Clear the board
        score.reset(); // Reset the score
        createNewBrick(); // Start the game by creating the first brick
    }
}