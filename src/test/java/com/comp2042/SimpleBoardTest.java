package com.comp2042;

import com.comp2042.model.board.SimpleBoard;
import com.comp2042.model.score.Score;
import com.comp2042.dto.ViewData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class SimpleBoardTest {

    private SimpleBoard board;

    @BeforeEach
    void setUp() {
        board = new SimpleBoard(10, 20);
    }

    @Test
    void testConstructorWithValidDimensions() {
        SimpleBoard testBoard = new SimpleBoard(10, 20);
        assertNotNull(testBoard);
        assertNotNull(testBoard.getBoardMatrix());
        assertEquals(20, testBoard.getBoardMatrix().length);
        assertEquals(10, testBoard.getBoardMatrix()[0].length);
    }

    @Test
    void testConstructorWithInvalidWidth() {
        assertThrows(IllegalArgumentException.class, () -> new SimpleBoard(0, 20));
        assertThrows(IllegalArgumentException.class, () -> new SimpleBoard(-5, 20));
    }

    @Test
    void testConstructorWithInvalidHeight() {
        assertThrows(IllegalArgumentException.class, () -> new SimpleBoard(10, 0));
        assertThrows(IllegalArgumentException.class, () -> new SimpleBoard(10, -3));
    }

    @Test
    void testMoveBrickDownWithNoBrick() {
        boolean result = board.moveBrickDown();
        assertFalse(result);
    }

    @Test
    void testCreateNewBrick() {
        boolean gameOver = board.createNewBrick();
        assertFalse(gameOver);
        assertNotNull(board.getViewData());
    }

    @Test
    void testBoardInitialization() {
        int[][] matrix = board.getBoardMatrix();
        assertNotNull(matrix);
        assertEquals(20, matrix.length);
        assertEquals(10, matrix[0].length);
        
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                assertEquals(0, matrix[i][j]);
            }
        }
    }
}

