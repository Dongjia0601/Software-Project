package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import com.comp2042.logic.bricks.SevenBagBrickGenerator;
import com.comp2042.config.GameSettings;

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
    private int totalLinesCleared = 0;           // Total lines cleared in the game
    
    // Hold functionality fields
    private Brick heldBrick = null;
    private boolean canHold = true; // Prevents multiple holds per brick

    /**
     * Constructs a SimpleBoard with the specified dimensions.
     * Initializes the board matrix, brick generator, rotator, and score.
     *
     * @param width  The width of the board (must be positive)
     * @param height The height of the board (must be positive)
     * @throws IllegalArgumentException if width or height is not positive
     */
    public SimpleBoard(int width, int height) {
        if (width <= 0) {
            throw new IllegalArgumentException("Board width must be positive, got: " + width);
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Board height must be positive, got: " + height);
        }
        
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[height][width];
        // Choose generator based on settings
        String randomizer = GameSettings.getInstance().getPieceRandomizer();
        if ("pure_random".equalsIgnoreCase(randomizer)) {
            brickGenerator = new RandomBrickGenerator();
        } else {
            brickGenerator = new SevenBagBrickGenerator();
        }
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
        if (currentOffset == null) {
            return false; // No brick to move
        }
        
        // PERFORMANCE OPTIMIZATION: intersect() is read-only, no need to copy matrix
        Point p = new Point(currentOffset);
        p.translate(0, 1); // Move down (increase y)
        
        // Check if the new position is valid
        if (p.getY() < 0 || p.getY() >= height) {
            return false; // Cannot move down, brick lands
        }
        
        boolean conflict = MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false; // Cannot move down, brick lands
        } else {
            currentOffset = p; // Update position
            return true;      // Move successful
        }
    }

    @Override
    /**
     * Instantly drops the currently falling brick to the bottom of the board.
     * This is the hard drop functionality that moves the brick as far down as possible.
     *
     * @return the number of positions the brick was moved down.
     */
    public int hardDropBrick() {
        if (currentOffset == null) {
            return 0; // No brick to drop
        }
        
        // PERFORMANCE OPTIMIZATION: intersect() is read-only, no need to copy matrix
        int dropDistance = 0;
        Point originalPosition = new Point(currentOffset);
        
        // Keep moving down until we hit a collision
        while (true) {
            Point testPosition = new Point(currentOffset);
            testPosition.translate(0, 1); // Move down one position
            
            // Check if the new position is valid
            if (testPosition.getY() < 0 || testPosition.getY() >= height) {
                break; // Hit bottom of board
            }
            
            // Check for collision with other bricks
            boolean conflict = MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) testPosition.getX(), (int) testPosition.getY());
            if (conflict) {
                break; // Hit another brick
            }
            
            // Move is valid, update position and continue
            currentOffset = testPosition;
            dropDistance++;
        }
        
        return dropDistance;
    }


    @Override
    /**
     * Attempts to move the currently falling brick one position to the left.
     * Checks for collisions with the left wall or other placed bricks.
     *
     * @return true if the move was successful, false if a collision occurred.
     */
    public boolean moveBrickLeft() {
        // PERFORMANCE OPTIMIZATION: intersect() is read-only, no need to copy matrix
        Point p = new Point(currentOffset);
        p.translate(-1, 0); // Move left (decrease x)
        boolean conflict = MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
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
        // PERFORMANCE OPTIMIZATION: intersect() is read-only, no need to copy matrix
        Point p = new Point(currentOffset);
        p.translate(1, 0); // Move right (increase x)
        boolean conflict = MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
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
    public boolean rotateLeftBrick() {
        // PERFORMANCE OPTIMIZATION: intersect() is read-only, no need to copy matrix
        NextShapeInfo nextShape = brickRotator.calculateNextShapeInfo(); // Assuming renamed method
        boolean conflict = MatrixOperations.intersect(currentGameMatrix, nextShape.getShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
        if (conflict) {
            return false; // Cannot rotate
        } else {
            brickRotator.setCurrentShape(nextShape.getPosition()); // Apply rotation
            return true;                                           // Rotation successful
        }
    }

    /**
     * Attempts to rotate the currently falling brick counterclockwise.
     * Calculates the potential previous shape and checks for collisions in the new orientation.
     *
     * @return true if the rotation was successful, false if a collision occurred.
     */
    public boolean rotateRightBrick() {
        // PERFORMANCE OPTIMIZATION: intersect() is read-only, no need to copy matrix
        NextShapeInfo prevShape = brickRotator.calculatePreviousShapeInfo();
        boolean conflict = MatrixOperations.intersect(currentGameMatrix, prevShape.getShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
        if (conflict) {
            return false; // Cannot rotate
        } else {
            brickRotator.setCurrentShape(prevShape.getPosition()); // Apply rotation
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
        currentOffset = new Point(4, 0); // Spawn so that the brick occupies the top row (index 0).
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
        int[][] nextBrickShape = null;
        int[][] holdBrickShape = null;
        
        // Safely get next brick shape
        try {
            Brick nextBrick = brickGenerator.getNextBrick();
            if (nextBrick != null && nextBrick.getShapeMatrix() != null && !nextBrick.getShapeMatrix().isEmpty()) {
                nextBrickShape = nextBrick.getShapeMatrix().get(0);
            }
        } catch (Exception e) {
            // Handle any potential errors gracefully
            nextBrickShape = new int[4][4]; // Default empty shape
        }
        
        // Get hold brick shape if available
        if (heldBrick != null && heldBrick.getShapeMatrix() != null && !heldBrick.getShapeMatrix().isEmpty()) {
            holdBrickShape = heldBrick.getShapeMatrix().get(0);
        }
        
        // Calculate ghost brick Y position
        int ghostY = getGhostBrickY();
        
        return new ViewData(brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY(), ghostY, nextBrickShape, holdBrickShape);
    }

    @Override
    /**
     * Merges the currently falling brick permanently onto the board grid.
     * This happens when the brick lands after moving down.
     */
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
        enableHold(); // Re-enable hold for next brick
    }

    @Override
/**
 * Checks the board for completed rows, removes them, shifts the remaining rows down,
 * calculates the score bonus, updates the internal board matrix, and awards the score.
 *
 * @return A ClearRow object containing details about the cleared lines.
 */
    public ClearRow clearRows() {
        // Assuming MatrixOperations.checkRemoving was renamed to clearCompletedRows
        ClearRow clearRow = MatrixOperations.clearCompletedRows(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();

        // Update total lines cleared counter
        totalLinesCleared += clearRow.getLinesRemoved();

        // CRITICAL FIX: Award the calculated score bonus to the player's score
        int scoreBonus = clearRow.getScoreBonus();
        if (scoreBonus > 0) {
            this.score.add(scoreBonus); // Add the bonus to the SimpleBoard's score instance
        }

        return clearRow;
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

    /**
     * Gets the total number of lines cleared in the current game.
     *
     * @return The total lines cleared.
     */
    public int getTotalLinesCleared() {
        return totalLinesCleared;
    }


    /**
     * Swaps the current brick with the held brick.
     * 
     * @return true if hold operation was successful
     */
    public boolean holdBrick() {
        if (!canHold) {
            return false; // Can only hold once per brick
        }
        
        Brick currentBrick = brickRotator.getBrick();
        
        if (heldBrick == null) {
            // First time holding - store current and generate new
            heldBrick = currentBrick;
            createNewBrick();
        } else {
            // Swap with held brick
            Brick temp = heldBrick;
            heldBrick = currentBrick;
            brickRotator.setBrick(temp);
            currentOffset = new Point(4, 0); // Reset position to spawn point (top row)
        }
        
        canHold = false; // Disable hold until next brick
        return true;
    }
    
    /**
     * Gets the currently held brick.
     * 
     * @return the held brick, or null if no brick is held
     */
    public Brick getHeldBrick() {
        return heldBrick;
    }
    
    /**
     * Gets the next brick shape data.
     * 
     * @return the next brick shape matrix
     */
    public int[][] getNextBrick() {
        try {
            Brick nextBrick = brickGenerator.getNextBrick();
            if (nextBrick != null && nextBrick.getShapeMatrix() != null && !nextBrick.getShapeMatrix().isEmpty()) {
                return nextBrick.getShapeMatrix().get(0);
            }
        } catch (Exception e) {
            // Handle any potential errors gracefully
        }
        return null;
    }
    
    /**
     * Re-enables hold functionality when a new brick is created.
     * Called internally when brick is merged to background.
     */
    private void enableHold() {
        canHold = true;
    }
    
    /**
     * Adds a garbage line to the bottom of the board.
     * A garbage line is a full row with one random hole.
     * This is used for VS mode attacks.
     * 
     * @return true if adding the garbage line causes game over
     */
    public boolean addGarbageLine() {
        int width = currentGameMatrix[0].length;
        int height = currentGameMatrix.length;
        
        // Shift all rows up by one
        for (int i = 0; i < height - 1; i++) {
            currentGameMatrix[i] = currentGameMatrix[i + 1].clone();
        }
        
        // Create a garbage line with one random hole
        int[] garbageLine = new int[width];
        int holePosition = (int) (Math.random() * width);
        for (int j = 0; j < width; j++) {
            if (j == holePosition) {
                garbageLine[j] = 0; // Hole
            } else {
                garbageLine[j] = 8; // Garbage block (use color 8 for garbage)
            }
        }
        
        // Place garbage line at the bottom
        currentGameMatrix[height - 1] = garbageLine;
        
        // Check if this causes game over (collision with current brick)
        if (currentOffset != null && brickRotator.getCurrentShape() != null) {
            return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), 
                                             (int) currentOffset.getX(), (int) currentOffset.getY());
        }
        
        // If no current brick, check if new brick spawn would cause game over
        return false;
    }

    @Override
    /**
     * Resets the board state for a new game.
     * Clears the board matrix, resets the score, and creates the first new brick.
     */
    public void newGame() {
        currentGameMatrix = new int[height][width]; // Clear the board
        score.reset(); // Reset the score
        totalLinesCleared = 0; // Reset lines cleared counter
        heldBrick = null; // Clear held brick
        canHold = true;   // Re-enable hold
        createNewBrick(); // Start the game by creating the first brick
    }
    
    @Override
    /**
     * Calculates the ghost brick position (where the brick would land if dropped straight down).
     * The ghost brick has the same shape and x-position as the current brick, but with the y-position
     * adjusted to show where it would land.
     * 
     * @return the y-coordinate (row) where the ghost brick would land, or -1 if calculation fails
     */
    public int getGhostBrickY() {
        if (currentOffset == null || brickRotator.getCurrentShape() == null) {
            return -1; // No brick to calculate ghost position for
        }
        
        // PERFORMANCE OPTIMIZATION: intersect() is read-only, no need to copy matrix
        int currentX = (int) currentOffset.getX();
        int currentY = (int) currentOffset.getY();
        int[][] currentShape = brickRotator.getCurrentShape();
        
        int ghostY = currentY;
        
        // Start from current position and move down until we hit a collision
        while (true) {
            // Check if moving down one more position would cause a collision
            int testY = ghostY + 1;
            
            // Check for collision with other bricks or out of bounds
            boolean conflict = MatrixOperations.intersect(currentGameMatrix, currentShape, currentX, testY);
            if (conflict) {
                break; // Hit another brick or out of bounds
            }
            
            ghostY = testY; // Move down one position
        }
        
        // Only return ghost position if it's different from current position
        if (ghostY == currentY) {
            return -1; // Ghost position same as current, no need to show
        }
        
        return ghostY;
    }
}