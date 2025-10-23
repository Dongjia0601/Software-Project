package com.comp2042;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SimpleBoard class.
 * Tests the game board logic and validates the improvements made.
 */
class SimpleBoardTest {

    private SimpleBoard board;

    @BeforeEach
    void setUp() {
        board = new SimpleBoard(10, 20);
    }

    @Test
    void testConstructorWithValidDimensions() {
        // Test that constructor works with valid dimensions
        SimpleBoard testBoard = new SimpleBoard(10, 20);
        assertNotNull(testBoard);
        assertNotNull(testBoard.getBoardMatrix());
        assertEquals(10, testBoard.getBoardMatrix().length);
        assertEquals(20, testBoard.getBoardMatrix()[0].length);
    }

    @Test
    void testConstructorWithInvalidWidth() {
        // Test that constructor throws exception for invalid width
        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleBoard(0, 20);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleBoard(-5, 20);
        });
    }

    @Test
    void testConstructorWithInvalidHeight() {
        // Test that constructor throws exception for invalid height
        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleBoard(10, 0);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleBoard(10, -3);
        });
    }

    @Test
    void testMoveBrickDownWithNoBrick() {
        // Test that moveBrickDown returns false when no brick is present
        boolean result = board.moveBrickDown();
        assertFalse(result);
    }

    @Test
    void testCreateNewBrick() {
        // Test that creating a new brick works correctly
        boolean gameOver = board.createNewBrick();
        assertFalse(gameOver); // First brick should not cause game over
        
        // Verify that a brick was created
        assertNotNull(board.getViewData());
    }

    @Test
    void testBoardInitialization() {
        // Test that board is properly initialized
        int[][] matrix = board.getBoardMatrix();
        assertNotNull(matrix);
        assertEquals(10, matrix.length);
        assertEquals(20, matrix[0].length);
        
        // Verify all cells are initially empty (0)
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 20; j++) {
                assertEquals(0, matrix[i][j]);
            }
        }
    }

    @Test
    void testScoreInitialization() {
        // Test that score is properly initialized
        Score score = board.getScore();
        assertNotNull(score);
        assertEquals(0, score.scoreProperty().get());
    }
}
