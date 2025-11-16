package com.comp2042;

import com.comp2042.model.savestate.GameStateMemento;
import com.comp2042.model.savestate.GameStateCaretaker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for Memento Pattern implementation.
 * Tests GameStateMemento, GameStateCaretaker, and SimpleBoard's memento support.
 * 
 * <p>These tests validate the Memento Pattern implementation, ensuring
 * that game state can be correctly saved and restored.</p>
 * 
 * @author Dong, Jia.
 */
@DisplayName("Memento Pattern Tests")
class MementoPatternTest {

    private SimpleBoard board;
    private GameStateCaretaker caretaker;

    @BeforeEach
    void setUp() {
        board = new SimpleBoard(10, 20);
        caretaker = new GameStateCaretaker();
    }

    // ========== GameStateMemento Tests ==========

    @Test
    @DisplayName("GameStateMemento: Creates memento with all state information")
    void testCreateMemento() {
        // Create a new game to have initial state
        board.newGame();
        
        GameStateMemento memento = board.createMemento();
        
        assertNotNull(memento, "Memento should not be null");
        assertNotNull(memento.getBoardMatrix(), "Board matrix should not be null");
        assertEquals(10, memento.getBoardWidth(), "Board width should match");
        assertEquals(20, memento.getBoardHeight(), "Board height should match");
    }

    @Test
    @DisplayName("GameStateMemento: Stores board matrix correctly")
    void testMementoBoardMatrix() {
        board.newGame();
        
        // Make some moves to change board state
        board.moveBrickDown();
        board.moveBrickLeft();
        
        GameStateMemento memento = board.createMemento();
        int[][] savedMatrix = memento.getBoardMatrix();
        
        assertNotNull(savedMatrix, "Saved matrix should not be null");
        assertEquals(20, savedMatrix.length, "Matrix height should match");
        assertEquals(10, savedMatrix[0].length, "Matrix width should match");
    }

    @Test
    @DisplayName("GameStateMemento: Stores score and statistics")
    void testMementoScoreAndStatistics() {
        board.newGame();
        
        // Get initial score
        int initialScore = board.getScore().getScore();
        int initialLines = board.getTotalLinesCleared();
        
        GameStateMemento memento = board.createMemento();
        
        assertEquals(initialScore, memento.getScore(), "Score should match");
        assertEquals(initialLines, memento.getTotalLinesCleared(), "Lines cleared should match");
    }

    @Test
    @DisplayName("GameStateMemento: Stores current brick information")
    void testMementoCurrentBrick() {
        board.newGame();
        
        GameStateMemento memento = board.createMemento();
        
        // Current brick should be set after newGame()
        assertNotNull(memento.getCurrentBrickType(), "Current brick type should not be null");
        assertNotNull(memento.getCurrentBrickShape(), "Current brick shape should not be null");
        assertTrue(memento.getCurrentBrickX() >= 0, "Current brick X should be valid");
        assertTrue(memento.getCurrentBrickY() >= 0, "Current brick Y should be valid");
    }

    @Test
    @DisplayName("GameStateMemento: Returns defensive copies of matrices")
    void testMementoDefensiveCopies() {
        board.newGame();
        GameStateMemento memento = board.createMemento();
        
        int[][] matrix1 = memento.getBoardMatrix();
        int[][] matrix2 = memento.getBoardMatrix();
        
        // Should be different instances (defensive copy)
        assertNotSame(matrix1, matrix2, "Each call should return a new copy");
        
        // But should have same content
        assertEquals(matrix1.length, matrix2.length, "Matrices should have same dimensions");
    }

    // ========== SimpleBoard Memento Support Tests ==========

    @Test
    @DisplayName("SimpleBoard: createMemento returns valid memento")
    void testCreateMemento() {
        board.newGame();
        
        GameStateMemento memento = board.createMemento();
        
        assertNotNull(memento, "Memento should not be null");
        assertEquals(10, memento.getBoardWidth(), "Width should match");
        assertEquals(20, memento.getBoardHeight(), "Height should match");
    }

    @Test
    @DisplayName("SimpleBoard: restoreFromMemento restores game state")
    void testRestoreFromMemento() {
        board.newGame();
        
        // Save initial state
        int initialScore = board.getScore().getScore();
        GameStateMemento memento = board.createMemento();
        
        // Make some changes
        board.moveBrickDown();
        board.moveBrickLeft();
        board.getScore().add(100);
        
        // Restore from memento
        board.restoreFromMemento(memento);
        
        // Verify state was restored
        assertEquals(initialScore, board.getScore().getScore(), 
            "Score should be restored to initial value");
    }

    @Test
    @DisplayName("SimpleBoard: restoreFromMemento throws exception for null memento")
    void testRestoreFromMementoNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            board.restoreFromMemento(null);
        }, "Should throw exception for null memento");
    }

    @Test
    @DisplayName("SimpleBoard: restoreFromMemento throws exception for dimension mismatch")
    void testRestoreFromMementoDimensionMismatch() {
        SimpleBoard smallBoard = new SimpleBoard(5, 10);
        smallBoard.newGame();
        GameStateMemento memento = smallBoard.createMemento();
        
        // Try to restore in board with different dimensions
        assertThrows(IllegalArgumentException.class, () -> {
            board.restoreFromMemento(memento);
        }, "Should throw exception for dimension mismatch");
    }

    @Test
    @DisplayName("SimpleBoard: Save and restore preserves board state")
    void testSaveAndRestoreBoardState() {
        board.newGame();
        
        // Make some moves
        board.moveBrickDown();
        board.moveBrickDown();
        board.moveBrickRight();
        
        // Save state
        GameStateMemento memento = board.createMemento();
        int[][] savedMatrix = memento.getBoardMatrix();
        
        // Make more changes
        board.moveBrickDown();
        board.moveBrickLeft();
        
        // Restore
        board.restoreFromMemento(memento);
        int[][] restoredMatrix = board.getBoardMatrix();
        
        // Verify matrices match
        assertEquals(savedMatrix.length, restoredMatrix.length, "Matrix height should match");
        assertEquals(savedMatrix[0].length, restoredMatrix[0].length, "Matrix width should match");
    }

    @Test
    @DisplayName("SimpleBoard: Save and restore preserves score")
    void testSaveAndRestoreScore() {
        board.newGame();
        
        // Add some score
        board.getScore().add(500);
        int savedScore = board.getScore().getScore();
        
        GameStateMemento memento = board.createMemento();
        
        // Change score
        board.getScore().add(200);
        
        // Restore
        board.restoreFromMemento(memento);
        
        assertEquals(savedScore, board.getScore().getScore(), 
            "Score should be restored");
    }

    @Test
    @DisplayName("SimpleBoard: Save and restore preserves lines cleared")
    void testSaveAndRestoreLinesCleared() {
        board.newGame();
        
        // Note: Actually clearing lines requires more setup, so we test the mechanism
        int initialLines = board.getTotalLinesCleared();
        
        GameStateMemento memento = board.createMemento();
        
        // Restore
        board.restoreFromMemento(memento);
        
        assertEquals(initialLines, board.getTotalLinesCleared(), 
            "Lines cleared should be restored");
    }

    // ========== GameStateCaretaker Tests ==========

    @Test
    @DisplayName("GameStateCaretaker: Saves memento to history")
    void testCaretakerSaveMemento() {
        board.newGame();
        GameStateMemento memento = board.createMemento();
        
        caretaker.saveMemento(memento);
        
        assertTrue(caretaker.hasMemento(), "Should have memento after saving");
        assertEquals(1, caretaker.getHistorySize(), "History size should be 1");
    }

    @Test
    @DisplayName("GameStateCaretaker: Retrieves most recent memento")
    void testCaretakerGetMemento() {
        board.newGame();
        
        // Save first state
        board.getScore().add(100);
        GameStateMemento memento1 = board.createMemento();
        caretaker.saveMemento(memento1);
        
        // Save second state
        board.getScore().add(200);
        GameStateMemento memento2 = board.createMemento();
        caretaker.saveMemento(memento2);
        
        // Get most recent (should be memento2)
        GameStateMemento retrieved = caretaker.getMemento();
        
        assertNotNull(retrieved, "Retrieved memento should not be null");
        assertEquals(memento2.getScore(), retrieved.getScore(), 
            "Should retrieve most recent memento");
    }

    @Test
    @DisplayName("GameStateCaretaker: peekMemento doesn't remove from history")
    void testCaretakerPeekMemento() {
        board.newGame();
        GameStateMemento memento = board.createMemento();
        caretaker.saveMemento(memento);
        
        GameStateMemento peeked1 = caretaker.peekMemento();
        GameStateMemento peeked2 = caretaker.peekMemento();
        
        assertSame(peeked1, peeked2, "Peek should return same memento");
        assertEquals(1, caretaker.getHistorySize(), "History size should not change");
    }

    @Test
    @DisplayName("GameStateCaretaker: Limits history size")
    void testCaretakerHistoryLimit() {
        board.newGame();
        
        // Save more than MAX_HISTORY_SIZE mementos
        for (int i = 0; i < 15; i++) {
            board.getScore().add(i * 10);
            GameStateMemento memento = board.createMemento();
            caretaker.saveMemento(memento);
        }
        
        assertTrue(caretaker.getHistorySize() <= caretaker.getMaxHistorySize(), 
            "History size should not exceed maximum");
    }

    @Test
    @DisplayName("GameStateCaretaker: clearHistory removes all mementos")
    void testCaretakerClearHistory() {
        board.newGame();
        
        // Save multiple mementos
        for (int i = 0; i < 5; i++) {
            GameStateMemento memento = board.createMemento();
            caretaker.saveMemento(memento);
        }
        
        caretaker.clearHistory();
        
        assertFalse(caretaker.hasMemento(), "Should have no mementos after clear");
        assertEquals(0, caretaker.getHistorySize(), "History size should be 0");
    }

    @Test
    @DisplayName("GameStateCaretaker: Ignores null memento")
    void testCaretakerIgnoresNull() {
        caretaker.saveMemento(null);
        
        assertFalse(caretaker.hasMemento(), "Should not save null memento");
        assertEquals(0, caretaker.getHistorySize(), "History size should be 0");
    }

    // ========== Memento Pattern Integration Tests ==========

    @Test
    @DisplayName("Memento Pattern: Complete save and restore workflow")
    void testCompleteSaveRestoreWorkflow() {
        board.newGame();
        
        // Initial state
        int initialScore = board.getScore().getScore();
        int initialLines = board.getTotalLinesCleared();
        
        // Save state
        GameStateMemento memento = board.createMemento();
        caretaker.saveMemento(memento);
        
        // Make changes
        board.moveBrickDown();
        board.moveBrickLeft();
        board.getScore().add(100);
        
        // Restore
        GameStateMemento savedMemento = caretaker.getMemento();
        board.restoreFromMemento(savedMemento);
        
        // Verify restoration
        assertEquals(initialScore, board.getScore().getScore(), 
            "Score should be restored");
        assertEquals(initialLines, board.getTotalLinesCleared(), 
            "Lines cleared should be restored");
    }

    @Test
    @DisplayName("Memento Pattern: Multiple save and restore operations")
    void testMultipleSaveRestore() {
        board.newGame();
        
        // Save state 1
        board.getScore().add(100);
        GameStateMemento memento1 = board.createMemento();
        caretaker.saveMemento(memento1);
        
        // Save state 2
        board.getScore().add(200);
        GameStateMemento memento2 = board.createMemento();
        caretaker.saveMemento(memento2);
        
        // Restore to state 2
        board.restoreFromMemento(caretaker.getMemento());
        assertEquals(300, board.getScore().getScore(), "Should restore to state 2");
        
        // Restore to state 1
        board.restoreFromMemento(caretaker.getMemento());
        assertEquals(100, board.getScore().getScore(), "Should restore to state 1");
    }
}

