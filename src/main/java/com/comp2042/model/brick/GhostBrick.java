package com.comp2042.model.brick;

import com.comp2042.util.MatrixOperations;

/**
 * Ghost piece that shows where the current brick would land if dropped straight down.
 * Has the same shape and x-position as the current brick, but y-position adjusted to landing spot.
 * Improves player UX by showing the drop destination.
 */
public class GhostBrick {
    
    private final int[][] shape;      // The shape matrix of the ghost brick (same as current brick)
    private final int xPosition;       // The x-coordinate (column) of the ghost brick
    private final int yPosition;       // The y-coordinate (row) where the ghost brick would land
    
    /**
     * Constructs a GhostBrick with shape and landing position.
     * 
     * @param shape     Shape matrix (copied defensively)
     * @param xPosition X-coordinate (column)
     * @param yPosition Landing y-coordinate (row)
     */
    public GhostBrick(int[][] shape, int xPosition, int yPosition) {
        this.shape = shape != null ? MatrixOperations.copy(shape) : null;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }
    
    /**
     * Calculates where the current brick would land if dropped straight down.
     * Returns null if ghost position equals current position or calculation fails.
     * 
     * @param currentShape Current brick shape matrix
     * @param currentX     Current x-coordinate
     * @param currentY     Current y-coordinate
     * @param boardMatrix  Board state
     * @param boardHeight  Board height
     * @return GhostBrick at landing position, or null
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
     * Gets the ghost brick shape matrix.
     * Returns a defensive copy.
     * 
     * @return Copy of shape matrix, or null
     */
    public int[][] getShape() {
        return shape != null ? MatrixOperations.copy(shape) : null;
    }
    
    /**
     * Gets the x-coordinate.
     * 
     * @return X-coordinate (column)
     */
    public int getXPosition() {
        return xPosition;
    }
    
    /**
     * Gets the landing y-coordinate.
     * 
     * @return Landing y-coordinate (row)
     */
    public int getYPosition() {
        return yPosition;
    }
    
    /**
     * Checks if the ghost brick is valid.
     * 
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return shape != null && yPosition >= 0;
    }
    
    /**
     * Returns a string representation.
     * 
     * @return String representation
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

