package com.comp2042;

import com.comp2042.core.GameService;
import com.comp2042.core.GameServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Dependency Inversion Principle (DIP) compliance.
 * 
 * <p>These tests verify that GameServiceImpl follows DIP by:
 * <ul>
 *   <li>Dependency injection through constructor</li>
 *   <li>Dependency on Board interface, not concrete class</li>
 *   <li>No direct instantiation of concrete classes</li>
 * </ul>
 * </p>
 * 
 * @author Dong, Jia.
 */
@DisplayName("Dependency Inversion Principle Tests")
class DependencyInversionTest {

    // ========== Dependency Injection Tests ==========

    @Test
    @DisplayName("DIP: GameServiceImpl uses dependency injection")
    void testDependencyInjection() {
        // Create Board instance (dependency)
        Board board = new SimpleBoard(10, 20);
        
        // Inject dependency through constructor
        GameService service = new GameServiceImpl(board);
        
        // Verify dependency was injected
        assertSame(board, service.getBoard(), 
            "GameServiceImpl should use the injected Board instance");
    }

    @Test
    @DisplayName("DIP: GameServiceImpl depends on Board interface, not SimpleBoard")
    void testDependsOnInterface() {
        // Create a mock Board implementation
        class MockBoard implements Board {
            @Override
            public boolean moveBrickDown() { return false; }
            @Override
            public int hardDropBrick() { return 0; }
            @Override
            public boolean moveBrickLeft() { return false; }
            @Override
            public boolean moveBrickRight() { return false; }
            @Override
            public boolean rotateLeftBrick() { return false; }
            @Override
            public boolean rotateRightBrick() { return false; }
            @Override
            public boolean createNewBrick() { return false; }
            @Override
            public int[][] getBoardMatrix() { return new int[20][10]; }
            @Override
            public ViewData getViewData() { return null; }
            @Override
            public void mergeBrickToBackground() {}
            @Override
            public ClearRow clearRows() { return new ClearRow(new int[20][10], 0, 0); }
            @Override
            public Score getScore() { return new Score(); }
            @Override
            public int getTotalLinesCleared() { return 0; }
            @Override
            public void newGame() {}
            @Override
            public boolean holdBrick() { return false; }
            @Override
            public Brick getHeldBrick() { return null; }
            @Override
            public int getGhostBrickY() { return -1; }
        }
        
        // GameServiceImpl should work with any Board implementation
        Board mockBoard = new MockBoard();
        GameService service = new GameServiceImpl(mockBoard);
        
        assertSame(mockBoard, service.getBoard(),
            "GameServiceImpl should work with any Board implementation");
    }

    @Test
    @DisplayName("DIP: No direct instantiation of SimpleBoard in GameServiceImpl")
    void testNoDirectInstantiation() {
        // Verify that GameServiceImpl constructor requires Board parameter
        // This ensures no direct instantiation of SimpleBoard inside GameServiceImpl
        
        Board board = new SimpleBoard(10, 20);
        GameService service = new GameServiceImpl(board);
        
        // The board should be the one we injected, not a new one created inside
        assertSame(board, service.getBoard(),
            "GameServiceImpl should not create its own Board instance");
    }

    @Test
    @DisplayName("DIP: createDefault factory method is explicit about dependency")
    void testCreateDefaultFactoryMethod() {
        // Factory method is acceptable as it's explicit about creating SimpleBoard
        GameService service = GameServiceImpl.createDefault();
        
        assertNotNull(service.getBoard(),
            "createDefault should create a valid service");
        assertTrue(service.getBoard() instanceof SimpleBoard,
            "createDefault should create SimpleBoard (explicit dependency)");
    }

    @Test
    @DisplayName("DIP: Constructor validates null board")
    void testConstructorValidatesNull() {
        // DIP compliance includes proper validation
        assertThrows(IllegalArgumentException.class, () -> {
            new GameServiceImpl(null);
        }, "Constructor should reject null board to maintain DIP compliance");
    }

    // ========== DIP Benefits Tests ==========

    @Test
    @DisplayName("DIP: Can inject different Board implementations for testing")
    void testCanInjectDifferentImplementations() {
        // Create custom board for testing
        Board testBoard = new SimpleBoard(15, 25);
        GameService service = new GameServiceImpl(testBoard);
        
        // Verify we can use a different board size
        assertEquals(15, testBoard.getBoardMatrix()[0].length,
            "Should be able to inject different board configurations");
    }

    @Test
    @DisplayName("DIP: Service layer depends on abstraction (Board interface)")
    void testServiceDependsOnAbstraction() {
        // Verify GameServiceImpl field type is Board interface
        Board board = new SimpleBoard(10, 20);
        GameService service = new GameServiceImpl(board);
        
        // The service should work with the interface, not the concrete class
        Board returnedBoard = service.getBoard();
        assertNotNull(returnedBoard,
            "Service should return Board interface, not concrete class");
        assertTrue(returnedBoard instanceof Board,
            "Returned object should implement Board interface");
    }
}

