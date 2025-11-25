package com.comp2042.model.board;

import com.comp2042.dto.ViewData;

import com.comp2042.model.brick.Brick;
import com.comp2042.model.brick.BrickGenerator;
import com.comp2042.model.brick.RandomBrickGenerator;
import com.comp2042.model.brick.SevenBagBrickGenerator;
import com.comp2042.config.GameSettings;
import com.comp2042.model.savestate.GameStateMemento;
import com.comp2042.model.score.Score;
import com.comp2042.dto.ClearRow;
import com.comp2042.util.MatrixOperations;

import java.awt.Point;

/**
 * Core Tetris board implementation managing game state and brick movement.
 * Handles board grid, falling brick, collision detection, row clearing, and scoring.
 * Supports configurable brick generators (7-bag or pure random) via GameSettings.
 * 
 * @author Dong, Jia.
 */
public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private final HoldManager holdManager;
    private final GarbageManager garbageManager;
    private final BoardMementoAdapter mementoAdapter;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;
    private int totalLinesCleared = 0;

    /**
     * Constructs a SimpleBoard with specified dimensions.
     * Initializes board components based on GameSettings (generator type).
     *
     * @param width  Board width (must be positive)
     * @param height Board height (must be positive)
     * @throws IllegalArgumentException if dimensions invalid
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
        String randomizer = GameSettings.getInstance().getPieceRandomizer();
        if ("pure_random".equalsIgnoreCase(randomizer)) {
            brickGenerator = new RandomBrickGenerator();
        } else {
            brickGenerator = new SevenBagBrickGenerator();
        }
        brickRotator = new BrickRotator();
        score = new Score();
        holdManager = new HoldManager();
        garbageManager = new GarbageManager(width, height);
        mementoAdapter = new BoardMementoAdapter(brickRotator, brickGenerator, score, holdManager);
    }

    /**
     * Moves the falling brick one position down.
     *
     * @return true if successful, false if collision (brick lands)
     */
    @Override
    public boolean moveBrickDown() {
        if (currentOffset == null) {
            return false;
        }
        
        Point newPosition = new Point(currentOffset);
        newPosition.translate(0, 1);
        
        if (newPosition.getY() < 0 || newPosition.getY() >= height) {
            return false;
        }
        
        boolean conflict = MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) newPosition.getX(), (int) newPosition.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = newPosition;
            return true;
        }
    }

    /**
     * Instantly drops the currently falling brick to the bottom of the board.
     * This is the hard drop functionality that moves the brick as far down as possible.
     *
     * @return the number of positions the brick was moved down
     */
    @Override
    public int hardDropBrick() {
        if (currentOffset == null) {
            return 0; // No brick to drop
        }
        
        int dropDistance = 0;
        
        while (true) {
            Point testPosition = new Point(currentOffset);
            testPosition.translate(0, 1);
            
            if (testPosition.getY() < 0 || testPosition.getY() >= height) {
                break;
            }
            
            boolean conflict = MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) testPosition.getX(), (int) testPosition.getY());
            if (conflict) {
                break;
            }
            
            currentOffset = testPosition;
            dropDistance++;
        }
        
        return dropDistance;
    }


    /**
     * Attempts to move the currently falling brick one position to the left.
     * Checks for collisions with the left wall or other placed bricks.
     *
     * @return true if the move was successful, false if a collision occurred
     */
    @Override
    public boolean moveBrickLeft() {
        Point newPosition = new Point(currentOffset);
        newPosition.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) newPosition.getX(), (int) newPosition.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = newPosition;
            return true;
        }
    }

    /**
     * Attempts to move the currently falling brick one position to the right.
     * Checks for collisions with the right wall or other placed bricks.
     *
     * @return true if the move was successful, false if a collision occurred
     */
    @Override
    public boolean moveBrickRight() {
        Point newPosition = new Point(currentOffset);
        newPosition.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) newPosition.getX(), (int) newPosition.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = newPosition;
            return true;
        }
    }

    /**
     * Attempts to rotate the currently falling brick clockwise.
     * Calculates the potential next shape and checks for collisions in the new orientation.
     *
     * @return true if the rotation was successful, false if a collision occurred
     */
    @Override
    public boolean rotateLeftBrick() {
        NextShapeInfo nextShape = brickRotator.calculateNextShapeInfo();
        boolean conflict = MatrixOperations.intersect(currentGameMatrix, nextShape.getShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
        if (conflict) {
            return false;
        } else {
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }
    }

    /**
     * Attempts to rotate the currently falling brick counterclockwise.
     * Calculates the potential previous shape and checks for collisions in the new orientation.
     *
     * @return true if the rotation was successful, false if a collision occurred.
     */
    public boolean rotateRightBrick() {
        NextShapeInfo prevShape = brickRotator.calculatePreviousShapeInfo();
        boolean conflict = MatrixOperations.intersect(currentGameMatrix, prevShape.getShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
        if (conflict) {
            return false;
        } else {
            brickRotator.setCurrentShape(prevShape.getPosition());
            return true;
        }
    }

    /**
     * Creates a new brick for the board.
     * Gets a brick from the generator, sets it in the rotator, positions it at the top/middle,
     * and checks if this initial position causes a collision (indicating game over).
     *
     * @return true if a collision occurs immediately (game over), false otherwise
     */
    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(4, 0); // Spawn so that the brick occupies the top row (index 0).
        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    /**
     * Gets the current state of the board grid.
     * Returns a copy to prevent external modification.
     *
     * @return a copy of the current board matrix
     */
    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    /**
     * Gets the current view data (brick shape, position, next brick shape).
     *
     * @return a ViewData object containing the necessary information for the GUI
     */
    @Override
    public ViewData getViewData() {
        int[][] nextBrickShape = null;
        int[][] holdBrickShape = null;
        
        try {
            Brick nextBrick = brickGenerator.getNextBrick();
            if (nextBrick != null && nextBrick.getShapeMatrix() != null && !nextBrick.getShapeMatrix().isEmpty()) {
                nextBrickShape = nextBrick.getShapeMatrix().getFirst();
            }
        } catch (Exception e) {
            // Handle any potential errors gracefully
            nextBrickShape = new int[4][4]; // Default empty shape
        }
        
        // Get hold brick shape if available
        Brick held = getHeldBrick();
        if (held != null && held.getShapeMatrix() != null && !held.getShapeMatrix().isEmpty()) {
            holdBrickShape = held.getShapeMatrix().getFirst();
        }
        
        // Calculate ghost brick Y position
        int ghostY = getGhostBrickY();
        
        return new ViewData(brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY(), ghostY, nextBrickShape, holdBrickShape);
    }

    /**
     * Merges the currently falling brick permanently onto the board grid.
     * This happens when the brick lands after moving down.
     */
    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
        enableHold(); // Re-enable hold for next brick
    }

    /**
     * Checks the board for completed rows, removes them, shifts the remaining rows down,
     * calculates the score bonus, updates the internal board matrix, and awards the score.
     *
     * @return a ClearRow object containing details about the cleared lines
     */
    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.clearCompletedRows(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        totalLinesCleared += clearRow.getLinesRemoved();

        int scoreBonus = clearRow.getScoreBonus();
        if (scoreBonus > 0) {
            this.score.add(scoreBonus);
        }

        return clearRow;
    }

    /**
     * Gets the current score object.
     *
     * @return the Score instance
     */
    @Override
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
        return holdManager.holdBrick(
            brickRotator,
                this::createNewBrick,
            () -> currentOffset = new Point(4, 0)
        );
    }
    
    /**
     * Gets the currently held brick.
     * 
     * @return the held brick, or null if no brick is held
     */
    public Brick getHeldBrick() {
        return holdManager.getHeldBrick();
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
                return nextBrick.getShapeMatrix().getFirst();
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
        holdManager.enableHold();
    }
    
    /**
     * Adds a garbage line to the bottom of the board.
     * A garbage line is a full row with one random hole.
     * This is used for Two-player mode attacks.
     * 
     * @return true if adding the garbage line causes game over
     */
    @Override
    public boolean addGarbageLine() {
        return garbageManager.addGarbageLine(currentGameMatrix, brickRotator, currentOffset);
    }

    /**
     * Removes garbage lines from the bottom of the board.
     * Scans for lines containing only garbage blocks (and empty space) and removes them.
     * 
     * @param linesToRemove the number of garbage lines to remove
     * @return the actual number of lines removed
     */
    @Override
    public int removeGarbageLines(int linesToRemove) {
        return garbageManager.removeGarbageLines(currentGameMatrix, linesToRemove);
    }

    /**
     * Resets the board state for a new game.
     * Clears the board matrix, resets the score, resets the brick generator,
     * and creates the first new brick.
     */
    @Override
    public void newGame() {
        currentGameMatrix = new int[height][width]; // Clear the board
        score.reset(); // Reset the score
        totalLinesCleared = 0; // Reset lines cleared counter
        holdManager.reset();
        brickGenerator.reset(); // Reset brick generator to ensure fresh 7-bag
        createNewBrick(); // Start the game by creating the first brick
    }
    
    @Override
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
                break;
            }
            
            ghostY = testY;
        }
        
        if (ghostY == currentY) {
            return -1;
        }
        
        return ghostY;
    }
    
    // ========== Memento Pattern Support ==========
    
    /**
     * Creates a memento containing the current game state.
     * Implements the Memento Pattern's Originator role.
     * 
     * @return a GameStateMemento containing the current game state
     */
    public GameStateMemento createMemento() {
        String generatorType = brickGenerator instanceof SevenBagBrickGenerator ? "seven_bag" : "pure_random";
        return mementoAdapter.createSnapshot(
            currentGameMatrix,
            width,
            height,
            currentOffset,
            totalLinesCleared,
            generatorType
        );
    }
    
    /**
     * Restores the game state from a memento.
     * Implements the Memento Pattern's Originator role.
     * 
     * @param memento the GameStateMemento to restore from
     * @throws IllegalArgumentException if memento is null or dimensions don't match
     */
    public void restoreFromMemento(GameStateMemento memento) {
        BoardMementoAdapter.RestorationResult result = mementoAdapter.restoreFromSnapshot(memento, width, height);
        currentGameMatrix = result.getBoardMatrix();
        currentOffset = result.getCurrentOffset();
        totalLinesCleared = result.getTotalLinesCleared();
    }

    // Helper methods removed thanks to BoardMementoAdapter encapsulation.
}