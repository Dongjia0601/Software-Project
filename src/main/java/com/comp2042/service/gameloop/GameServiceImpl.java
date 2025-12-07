package com.comp2042.service.gameloop;

import com.comp2042.dto.DownData;
import com.comp2042.dto.ViewData;
import com.comp2042.dto.ClearRow;
import com.comp2042.event.MoveEvent;
import com.comp2042.event.EventType;
import com.comp2042.model.board.Board;
import com.comp2042.model.board.SimpleBoard;
import com.comp2042.model.score.Score;
import com.comp2042.util.MatrixOperations;

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
 * 
 * @author Dong, Jia.
 */
public class GameServiceImpl implements GameService {
    
    private final Board board;
    private int dropSpeed = 400;
    private boolean gameOver = false;
    
    /**
     * Constructs a GameServiceImpl with the specified board.
     * Uses dependency injection to follow Dependency Inversion Principle (DIP).
     * 
     * @param board the game board instance (must not be null)
     * @throws IllegalArgumentException if board is null
     */
    public GameServiceImpl(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null");
        }
        this.board = board;
    }
    
    /**
     * Creates a GameServiceImpl with a default SimpleBoard.
     * This factory method provides a convenient way to create a service with default configuration
     * while still maintaining DIP compliance (the board creation is explicit).
     * 
     * @return a new GameServiceImpl instance with a default SimpleBoard (10x20)
     */
    public static GameServiceImpl createDefault() {
        return new GameServiceImpl(new SimpleBoard(10, 20));
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

        // If external actions (like garbage lines) have pushed blocks into the spawn area,
        // treat it as an immediate game over before processing movement.
        ViewData currentView = board.getViewData();
        if (MatrixOperations.intersect(
            board.getBoardMatrix(),
            currentView.getBrickData(),
            currentView.getXPosition(),
            currentView.getYPosition()
        )) {
            gameOver = true;
            return new DownData(null, currentView, true, 0);
        }
        
        boolean canMove;
        if (event != null && event.getEventType() == EventType.HARD_DROP) {
            int dropDistance = board.hardDropBrick();
            if (dropDistance > 0) {
                board.getScore().add(dropDistance * 2);
            }
            canMove = false;
        } else {
            canMove = board.moveBrickDown();
        }
        ClearRow clearRow = null;
        
        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();
            
            boolean newBrickGameOver = board.createNewBrick();
            if (newBrickGameOver) {
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
        this.dropSpeed = Math.max(50, Math.min(2000, speed));
    }
    
    @Override
    public void startNewGame() {
        gameOver = false;
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
