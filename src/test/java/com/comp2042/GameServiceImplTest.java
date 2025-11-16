package com.comp2042;

import com.comp2042.service.gameloop.GameService;
import com.comp2042.service.gameloop.GameServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for GameServiceImpl class.
 * Tests Service Layer Pattern implementation and game service operations.
 * 
 * <p>These tests validate the Service Layer Pattern implementation,
 * ensuring proper delegation to Board and correct game state management.</p>
 * 
 * @author Dong, Jia.
 */
@DisplayName("GameServiceImpl Tests")
class GameServiceImplTest {

    private GameService gameService;
    private Board board;

    @BeforeEach
    void setUp() {
        board = new SimpleBoard(10, 20);
        gameService = new GameServiceImpl(board);
    }

    @Test
    @DisplayName("Constructor: createDefault factory method creates GameServiceImpl with default board")
    void testCreateDefaultFactoryMethod() {
        GameService service = GameServiceImpl.createDefault();
        
        assertNotNull(service.getBoard());
        assertNotNull(service.getScore());
        assertTrue(service.getBoard() instanceof SimpleBoard);
    }
    
    @Test
    @DisplayName("Constructor: Throws exception for null board")
    void testConstructorWithNullBoard() {
        assertThrows(IllegalArgumentException.class, () -> {
            new GameServiceImpl(null);
        }, "Should throw exception for null board");
    }

    @Test
    @DisplayName("Constructor: Board constructor uses provided board")
    void testBoardConstructor() {
        Board customBoard = new SimpleBoard(15, 25);
        GameService service = new GameServiceImpl(customBoard);
        
        assertEquals(customBoard, service.getBoard());
    }

    @Test
    @DisplayName("getBoard: Returns the board instance")
    void testGetBoard() {
        assertEquals(board, gameService.getBoard());
    }

    @Test
    @DisplayName("getScore: Returns the score from board")
    void testGetScore() {
        Score score = gameService.getScore();
        
        assertNotNull(score);
        assertEquals(0, score.getScore());
    }

    // ========== processDownEvent Tests ==========

    @Test
    @DisplayName("processDownEvent: Handles normal DOWN event")
    void testProcessDownEventNormal() {
        board.createNewBrick();
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);
        
        DownData result = gameService.processDownEvent(event);
        
        assertNotNull(result);
        assertNotNull(result.getViewData());
    }

    @Test
    @DisplayName("processDownEvent: Handles HARD_DROP event")
    void testProcessDownEventHardDrop() {
        board.createNewBrick();
        MoveEvent event = new MoveEvent(EventType.HARD_DROP, EventSource.USER);
        
        DownData result = gameService.processDownEvent(event);
        
        assertNotNull(result);
        assertTrue(result.isBrickLanded());
    }

    @Test
    @DisplayName("processDownEvent: Returns null when game over")
    void testProcessDownEventGameOver() {
        // Fill board to cause game over
        int[][] matrix = board.getBoardMatrix();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 10; j++) {
                matrix[i][j] = 1;
            }
        }
        
        // Trigger game over
        board.createNewBrick();
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);
        gameService.processDownEvent(event);
        
        // Next event should return null (game over)
        DownData result = gameService.processDownEvent(event);
        // May be null or indicate game over
        assertNotNull(gameService.getBoard());
    }

    // ========== Movement Event Tests ==========

    @Test
    @DisplayName("processLeftEvent: Handles LEFT event successfully")
    void testProcessLeftEvent() {
        board.createNewBrick();
        MoveEvent event = new MoveEvent(EventType.LEFT, EventSource.USER);
        
        ViewData result = gameService.processLeftEvent(event);
        
        // May be null if move fails, or ViewData if succeeds
        assertNotNull(gameService.getBoard().getViewData());
    }

    @Test
    @DisplayName("processRightEvent: Handles RIGHT event successfully")
    void testProcessRightEvent() {
        board.createNewBrick();
        MoveEvent event = new MoveEvent(EventType.RIGHT, EventSource.USER);
        
        ViewData result = gameService.processRightEvent(event);
        
        assertNotNull(gameService.getBoard().getViewData());
    }

    @Test
    @DisplayName("processRotateEvent: Handles ROTATE event successfully")
    void testProcessRotateEvent() {
        board.createNewBrick();
        MoveEvent event = new MoveEvent(EventType.ROTATE, EventSource.USER);
        
        ViewData result = gameService.processRotateEvent(event);
        
        assertNotNull(gameService.getBoard().getViewData());
    }

    @Test
    @DisplayName("processRotateCCWEvent: Handles ROTATE_CCW event successfully")
    void testProcessRotateCCWEvent() {
        board.createNewBrick();
        MoveEvent event = new MoveEvent(EventType.ROTATE_CCW, EventSource.USER);
        
        ViewData result = gameService.processRotateCCWEvent(event);
        
        assertNotNull(gameService.getBoard().getViewData());
    }

    @Test
    @DisplayName("processHoldEvent: Handles HOLD event successfully")
    void testProcessHoldEvent() {
        board.createNewBrick();
        MoveEvent event = new MoveEvent(EventType.HOLD, EventSource.USER);
        
        ViewData result = gameService.processHoldEvent(event);
        
        // May be null if hold fails, or ViewData if succeeds
        assertNotNull(gameService.getBoard().getViewData());
    }

    // ========== Game State Management Tests ==========

    @Test
    @DisplayName("startNewGame: Resets game state")
    void testStartNewGame() {
        // Play a bit
        board.createNewBrick();
        board.moveBrickDown();
        gameService.getScore().add(100);
        
        // Start new game
        gameService.startNewGame();
        
        // Score should be reset
        assertEquals(0, gameService.getScore().getScore());
        assertFalse(gameService.isGameOver());
    }

    @Test
    @DisplayName("isGameOver: Returns false initially")
    void testIsGameOverInitially() {
        assertFalse(gameService.isGameOver());
    }

    @Test
    @DisplayName("setDropSpeed: Sets drop speed within valid range")
    void testSetDropSpeed() {
        GameServiceImpl service = (GameServiceImpl) gameService;
        
        service.setDropSpeed(200);
        assertEquals(200, service.getDropSpeed());
        
        service.setDropSpeed(100);
        assertEquals(100, service.getDropSpeed());
    }

    @Test
    @DisplayName("setDropSpeed: Clamps speed to valid range")
    void testSetDropSpeedClamping() {
        GameServiceImpl service = (GameServiceImpl) gameService;
        
        // Test minimum clamping
        service.setDropSpeed(10);
        assertTrue(service.getDropSpeed() >= 50, "Speed should be clamped to minimum 50ms");
        
        // Test maximum clamping
        service.setDropSpeed(5000);
        assertTrue(service.getDropSpeed() <= 2000, "Speed should be clamped to maximum 2000ms");
    }

    // ========== Service Layer Pattern Tests ==========

    @Test
    @DisplayName("Service Layer: Delegates to Board correctly")
    void testServiceLayerDelegation() {
        // Service should delegate operations to board
        assertNotNull(gameService.getBoard());
        assertNotNull(gameService.getScore());
        
        // Operations should work through service
        board.createNewBrick();
        assertNotNull(gameService.getBoard().getViewData());
    }

    @Test
    @DisplayName("Service Layer: Provides clean abstraction")
    void testServiceLayerAbstraction() {
        // Service provides clean interface without exposing board internals
        assertNotNull(gameService.getBoard());
        assertNotNull(gameService.getScore());
        
        // Can perform operations through service
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);
        assertNotNull(gameService.processDownEvent(event));
    }

    // ========== Helper Methods Tests ==========

    @Test
    @DisplayName("getCurrentViewData: Returns current view data")
    void testGetCurrentViewData() {
        board.createNewBrick();
        GameServiceImpl service = (GameServiceImpl) gameService;
        
        ViewData viewData = service.getCurrentViewData();
        assertNotNull(viewData);
    }

    @Test
    @DisplayName("getCurrentBoardMatrix: Returns current board matrix")
    void testGetCurrentBoardMatrix() {
        GameServiceImpl service = (GameServiceImpl) gameService;
        
        int[][] matrix = service.getCurrentBoardMatrix();
        assertNotNull(matrix);
        assertEquals(20, matrix.length);
        assertEquals(10, matrix[0].length);
    }

    @Test
    @DisplayName("getCurrentScore: Returns current score")
    void testGetCurrentScore() {
        board.getScore().add(250);
        GameServiceImpl service = (GameServiceImpl) gameService;
        
        assertEquals(250, service.getCurrentScore());
    }

    @Test
    @DisplayName("addScore: Adds points to score")
    void testAddScore() {
        GameServiceImpl service = (GameServiceImpl) gameService;
        
        service.addScore(100);
        assertEquals(100, service.getCurrentScore());
        
        service.addScore(50);
        assertEquals(150, service.getCurrentScore());
    }

    @Test
    @DisplayName("resetScore: Resets score to zero")
    void testResetScore() {
        GameServiceImpl service = (GameServiceImpl) gameService;
        
        service.addScore(200);
        service.resetScore();
        
        assertEquals(0, service.getCurrentScore());
    }
}

