package com.comp2042.core;

import com.comp2042.*;

/**
 * Implementation of the GameService interface.
 * 
 * <p>This class provides the core game logic operations by delegating to a Board instance.
 * It acts as a facade for the game board operations, providing a clean interface for
 * game mode implementations and ensuring proper separation of concerns.</p>
 * 
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Delegate game operations to the underlying Board instance</li>
 *   <li>Handle movement and rotation event processing</li>
 *   <li>Manage game state transitions and scoring</li>
 *   <li>Provide clean abstraction for game mode implementations</li>
 * </ul>
 */
public class GameServiceImpl implements GameService {
    
    private final Board board;
    private int dropSpeed = 400; // Default drop speed in milliseconds
    private boolean gameOver = false; // Track game over state
    
    /**
     * Constructs a GameServiceImpl with the specified board.
     * 
     * @param board the game board instance
     */
    public GameServiceImpl(Board board) {
        this.board = board;
    }
    
    /**
     * Constructs a GameServiceImpl with a default SimpleBoard.
     */
    public GameServiceImpl() {
        this.board = new SimpleBoard(10, 20);
    }
    
    @Override
    public Board getBoard() {
        return board;
    }
    
    @Override
    public Score getScore() {
        return board.getScore();
    }
    
    @Override
    public DownData processDownEvent(MoveEvent event) {
        if (gameOver) {
            return null;
        }
        
        boolean canMove;
        // Support hard drop (instant drop to bottom)
        if (event != null && event.getEventType() == EventType.HARD_DROP) {
            int dropDistance = board.hardDropBrick();
            if (dropDistance > 0) {
                // Match endless mode: 2 points per row hard-dropped
                board.getScore().add(dropDistance * 2);
            }
            // Force landing path below
            canMove = false;
        } else {
            // Soft drop: move down one cell
            canMove = board.moveBrickDown();
            // Note: Soft drop score is handled at the mode layer (PlayingState, TwoPlayerVSGameMode, etc.)
        }
        ClearRow clearRow = null;
        
        if (!canMove) {
            // Brick landed, merge to background and clear rows
            board.mergeBrickToBackground();
            clearRow = board.clearRows();
            
            // Note: Score is already added in SimpleBoard.clearRows(), so no need to add it here again
            
            // Create new brick - this returns true if game over (collision at spawn)
            boolean newBrickGameOver = board.createNewBrick();
            if (newBrickGameOver) {
                // Game over - player reached the top
                gameOver = true;
                return new DownData(clearRow, board.getViewData(), true, 0);
            }
        }
        
        return new DownData(clearRow, board.getViewData(), !canMove, 0);
    }
    
    @Override
    public ViewData processLeftEvent(MoveEvent event) {
        boolean success = board.moveBrickLeft();
        return success ? board.getViewData() : null;
    }
    
    @Override
    public ViewData processRightEvent(MoveEvent event) {
        boolean success = board.moveBrickRight();
        return success ? board.getViewData() : null;
    }
    
    @Override
    public ViewData processRotateEvent(MoveEvent event) {
        boolean success = board.rotateLeftBrick();
        return success ? board.getViewData() : null;
    }
    
    @Override
    public ViewData processRotateCCWEvent(MoveEvent event) {
        boolean success = board.rotateRightBrick();
        return success ? board.getViewData() : null;
    }
    
    @Override
    public void setDropSpeed(int speed) {
        this.dropSpeed = Math.max(50, Math.min(2000, speed)); // Clamp between 50ms and 2000ms
    }
    
    @Override
    public void startNewGame() {
        gameOver = false; // Reset game over state
        board.newGame();
    }
    
    @Override
    public boolean isGameOver() {
        return gameOver;
    }
    
    @Override
    public ViewData processHoldEvent(MoveEvent event) {
        boolean success = board.holdBrick();
        return success ? board.getViewData() : null;
    }
    
    @Override
    public int[][] getNextBrick() {
        // Get next brick from the board's brick generator
        if (board instanceof SimpleBoard) {
            return ((SimpleBoard) board).getNextBrick();
        }
        return null;
    }
    
    /**
     * Gets the current drop speed.
     * 
     * @return the current drop speed in milliseconds
     */
    public int getDropSpeed() {
        return dropSpeed;
    }
    
    /**
     * Gets the current game state as ViewData.
     * 
     * @return the current view data
     */
    public ViewData getCurrentViewData() {
        return board.getViewData();
    }
    
    /**
     * Gets the current board matrix.
     * 
     * @return the current board matrix
     */
    public int[][] getCurrentBoardMatrix() {
        return board.getBoardMatrix();
    }
    
    /**
     * Checks if the current brick can move down.
     * 
     * @return true if the brick can move down, false otherwise
     */
    public boolean canMoveDown() {
        // Movement validation is handled at the board level
        return true;
    }
    
    /**
     * Gets the current score value.
     * 
     * @return the current score
     */
    public int getCurrentScore() {
        return board.getScore().getScore();
    }
    
    /**
     * Adds points to the current score.
     * 
     * @param points the points to add
     */
    public void addScore(int points) {
        board.getScore().add(points);
    }
    
    /**
     * Resets the score to zero.
     */
    public void resetScore() {
        board.getScore().reset();
    }
}
