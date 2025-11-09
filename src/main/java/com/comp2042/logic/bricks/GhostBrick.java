package com.comp2042.logic.bricks;

import com.comp2042.MatrixOperations;

/**
 * Represents a ghost brick that shows where the current falling brick would land.
 * The ghost brick has the same shape and x-position as the current brick,
 * but with the y-position adjusted to show the landing position.
 * 
 * <p>This class encapsulates the logic for calculating the ghost brick position
 * and provides a clean interface for ghost brick operations.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Calculates landing position based on current brick and board state</li>
 *   <li>Maintains the same shape and x-position as the current brick</li>
 *   <li>Provides position information for rendering</li>
 * </ul>
 */
public class GhostBrick {
    
    private final int[][] shape;      // The shape matrix of the ghost brick (same as current brick)
    private final int xPosition;       // The x-coordinate (column) of the ghost brick
    private final int yPosition;       // The y-coordinate (row) where the ghost brick would land
    
    /**
     * Constructs a GhostBrick with the specified shape and position.
     * 
     * @param shape the shape matrix of the ghost brick (same as current brick)
     * @param xPosition the x-coordinate (column) of the ghost brick
     * @param yPosition the y-coordinate (row) where the ghost brick would land
     */
    public GhostBrick(int[][] shape, int xPosition, int yPosition) {
        this.shape = shape != null ? MatrixOperations.copy(shape) : null;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }
    
    /**
     * Calculates the ghost brick position for a given current brick and board state.
     * The ghost brick will have the same shape and x-position as the current brick,
     * but the y-position will be adjusted to show where it would land if dropped straight down.
     * 
     * @param currentShape the shape matrix of the current falling brick
     * @param currentX the x-coordinate of the current brick
     * @param currentY the y-coordinate of the current brick
     * @param boardMatrix the current state of the game board matrix
     * @param boardHeight the height of the game board
     * @return a GhostBrick instance representing where the brick would land, or null if calculation fails
     */
    public static GhostBrick calculate(int[][] currentShape, int currentX, int currentY, 
                                       int[][] boardMatrix, int boardHeight) {
        if (currentShape == null || boardMatrix == null) {
            return null;
        }
        
        int[][] currentMatrix = MatrixOperations.copy(boardMatrix);
        int ghostY = currentY;
        
        // Start from current position and move down until we hit a collision
        // We rely entirely on MatrixOperations.intersect() to check boundaries
        // because it checks each cell of the brick shape, which is more accurate
        // than just checking the top-left corner position
        while (true) {
            // Check if moving down one more position would cause a collision
            int testY = ghostY + 1;
            
            // Check for collision with other bricks or out of bounds
            // MatrixOperations.intersect will return true if:
            // 1. Any part of the brick goes out of bounds (targetY >= matrix.length)
            // 2. Any part of the brick collides with existing bricks
            // This is the most accurate check because it considers the brick's actual shape
            boolean conflict = MatrixOperations.intersect(currentMatrix, currentShape, currentX, testY);
            if (conflict) {
                break; // Hit another brick or out of bounds
            }
            
            ghostY = testY; // Move down one position
        }
        
        // Only create ghost brick if it's different from current position
        if (ghostY == currentY) {
            return null; // Ghost position same as current, no need to show
        }
        
        return new GhostBrick(currentShape, currentX, ghostY);
    }
    
    /**
     * Gets the shape matrix of the ghost brick.
     * Returns a copy to prevent external modification.
     * 
     * @return a copy of the ghost brick's shape matrix
     */
    public int[][] getShape() {
        return shape != null ? MatrixOperations.copy(shape) : null;
    }
    
    /**
     * Gets the x-coordinate (column) of the ghost brick.
     * 
     * @return the x-coordinate
     */
    public int getXPosition() {
        return xPosition;
    }
    
    /**
     * Gets the y-coordinate (row) where the ghost brick would land.
     * 
     * @return the y-coordinate
     */
    public int getYPosition() {
        return yPosition;
    }
    
    /**
     * Checks if the ghost brick is valid (has a valid shape and position).
     * 
     * @return true if the ghost brick is valid, false otherwise
     */
    public boolean isValid() {
        return shape != null && yPosition >= 0;
    }
    
    /**
     * Returns a string representation of this GhostBrick.
     * 
     * @return a string representation of this GhostBrick
     */
    @Override
    public String toString() {
        return "GhostBrick{" +
                "xPosition=" + xPosition +
                ", yPosition=" + yPosition +
                ", hasShape=" + (shape != null) +
                '}';
    }
}

