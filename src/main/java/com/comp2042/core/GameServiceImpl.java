package com.comp2042.core;

import com.comp2042.*;

/**
 * Implementation of the GameService interface.
 * Provides the core game logic operations by delegating to a Board instance.
 * 
 * <p>This service acts as a facade for the game board operations,
 * providing a clean interface for game mode implementations.</p>
 */
public class GameServiceImpl implements GameService {
    
    private final Board board;
    private int dropSpeed = 400; // Default drop speed in milliseconds
    
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
        this.board = new SimpleBoard(25, 10);
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
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        
        if (!canMove) {
            // Brick landed, merge to background and clear rows
            board.mergeBrickToBackground();
            clearRow = board.clearRows();
            
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
            }
            
            // Create new brick
            boolean gameOver = board.createNewBrick();
            if (gameOver) {
                // Game over logic could be handled here
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
    public void setDropSpeed(int speed) {
        this.dropSpeed = Math.max(50, Math.min(2000, speed)); // Clamp between 50ms and 2000ms
    }
    
    @Override
    public void startNewGame() {
        board.newGame();
    }
    
    @Override
    public boolean isGameOver() {
        // This is a simplified implementation
        // In a real game, you might want to check for specific game over conditions
        return false;
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
        // This would require access to the board's internal state
        // For now, we'll return true as a placeholder
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
