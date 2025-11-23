package com.comp2042.model.savestate;

/**
 * Memento storing complete game state snapshot (Memento Pattern).
 * Captures board matrix, brick state, score, hold/next bricks, and generator state.
 * Enables save/load, pause/resume, and undo functionality.
 * 
 * @author Dong, Jia.
 */
public final class GameStateMemento {
    
    private final int[][] boardMatrix;
    private final int boardWidth;
    private final int boardHeight;
    private final int[][] currentBrickShape;
    private final int currentBrickX;
    private final int currentBrickY;
    private final int currentShapeIndex;
    private final String currentBrickType;
    private final int score;
    private final int totalLinesCleared;
    private final int[][] heldBrickShape;
    private final String heldBrickType;
    private final int[][] nextBrickShape;
    private final String nextBrickType;
    private final boolean canHold;
    private final String brickGeneratorType;
    
    /**
     * Constructs a GameStateMemento with all game state information.
     * 
     * @param boardMatrix the current board matrix
     * @param boardWidth the board width
     * @param boardHeight the board height
     * @param currentBrickShape the current brick's shape matrix
     * @param currentBrickX the current brick's x position
     * @param currentBrickY the current brick's y position
     * @param currentShapeIndex the current brick's rotation index
     * @param currentBrickType the current brick's type (I, J, L, O, S, T, Z)
     * @param score the current score
     * @param totalLinesCleared the total lines cleared
     * @param heldBrickShape the held brick's shape matrix (null if none)
     * @param heldBrickType the held brick's type (null if none)
     * @param nextBrickShape the next brick's shape matrix
     * @param nextBrickType the next brick's type
     * @param canHold whether hold is available
     * @param brickGeneratorType the type of brick generator used
     */
    public GameStateMemento(
            int[][] boardMatrix,
            int boardWidth,
            int boardHeight,
            int[][] currentBrickShape,
            int currentBrickX,
            int currentBrickY,
            int currentShapeIndex,
            String currentBrickType,
            int score,
            int totalLinesCleared,
            int[][] heldBrickShape,
            String heldBrickType,
            int[][] nextBrickShape,
            String nextBrickType,
            boolean canHold,
            String brickGeneratorType) {
        
        this.boardMatrix = deepCopyMatrix(boardMatrix);
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.currentBrickShape = deepCopyMatrix(currentBrickShape);
        this.currentBrickX = currentBrickX;
        this.currentBrickY = currentBrickY;
        this.currentShapeIndex = currentShapeIndex;
        this.currentBrickType = currentBrickType;
        this.score = score;
        this.totalLinesCleared = totalLinesCleared;
        this.heldBrickShape = heldBrickShape != null ? deepCopyMatrix(heldBrickShape) : null;
        this.heldBrickType = heldBrickType;
        this.nextBrickShape = nextBrickShape != null ? deepCopyMatrix(nextBrickShape) : null;
        this.nextBrickType = nextBrickType;
        this.canHold = canHold;
        this.brickGeneratorType = brickGeneratorType;
    }
    
    public int[][] getBoardMatrix() {
        return deepCopyMatrix(boardMatrix);
    }
    
    public int getBoardWidth() {
        return boardWidth;
    }
    
    public int getBoardHeight() {
        return boardHeight;
    }
    
    /**
     * Gets the current brick's shape matrix.
     * 
     * @return a deep copy of the current brick shape matrix
     * @apiNote Reserved for future use - not currently invoked
     */
    public int[][] getCurrentBrickShape() {
        return deepCopyMatrix(currentBrickShape);
    }
    
    public int getCurrentBrickX() {
        return currentBrickX;
    }
    
    public int getCurrentBrickY() {
        return currentBrickY;
    }
    
    public int getCurrentShapeIndex() {
        return currentShapeIndex;
    }
    
    public String getCurrentBrickType() {
        return currentBrickType;
    }
    
    public int getScore() {
        return score;
    }
    
    public int getTotalLinesCleared() {
        return totalLinesCleared;
    }
    
    /**
     * Gets the held brick's shape matrix.
     * 
     * @return a deep copy of the held brick shape matrix, or null if none
     * @apiNote Reserved for future use - not currently invoked
     */
    public int[][] getHeldBrickShape() {
        return heldBrickShape != null ? deepCopyMatrix(heldBrickShape) : null;
    }
    
    public String getHeldBrickType() {
        return heldBrickType;
    }
    
    /**
     * Gets the next brick's shape matrix.
     * 
     * @return a deep copy of the next brick shape matrix, or null if none
     * @apiNote Reserved for future use - not currently invoked
     */
    public int[][] getNextBrickShape() {
        return nextBrickShape != null ? deepCopyMatrix(nextBrickShape) : null;
    }
    
    public String getNextBrickType() {
        return nextBrickType;
    }
    
    public boolean canHold() {
        return canHold;
    }
    
    /**
     * Gets the type of brick generator used.
     * 
     * @return the generator type ("seven_bag" or "pure_random")
     * @apiNote Reserved for future use - not currently invoked
     */
    public String getBrickGeneratorType() {
        return brickGeneratorType;
    }
    
    /**
     * Helper method to create a deep copy of a 2D matrix.
     * 
     * @param original the matrix to copy
     * @return a deep copy of the matrix
     */
    private static int[][] deepCopyMatrix(int[][] original) {
        if (original == null) {
            return null;
        }
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            if (original[i] != null) {
                copy[i] = new int[original[i].length];
                System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
            }
        }
        return copy;
    }
    
    /**
     * Returns a string representation of this memento.
     * 
     * @return a string representation
     */
    @Override
    public String toString() {
        return String.format("GameStateMemento{score=%d, lines=%d, brick=%s@(%d,%d), canHold=%b}",
                score, totalLinesCleared, currentBrickType, currentBrickX, currentBrickY, canHold);
    }
}

