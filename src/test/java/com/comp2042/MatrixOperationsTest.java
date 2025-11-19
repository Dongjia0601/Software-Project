package com.comp2042;

import com.comp2042.util.MatrixOperations;
import com.comp2042.dto.ClearRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MatrixOperations Tests")
class MatrixOperationsTest {

    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;

    @Test
    @DisplayName("intersect: No collision when brick is in empty space")
    void testIntersectNoCollision() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{{1, 1}, {1, 1}};
        assertFalse(MatrixOperations.intersect(board, brick, 0, 0));
    }

    @Test
    @DisplayName("intersect: Collision when brick overlaps occupied cell")
    void testIntersectCollisionWithOccupiedCell() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        board[5][5] = 2;
        int[][] brick = new int[][]{{1, 1}, {1, 1}};
        assertTrue(MatrixOperations.intersect(board, brick, 4, 4));
    }

    @Test
    @DisplayName("merge: Successfully merge brick onto empty board")
    void testMergeOntoEmptyBoard() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{{1, 1}, {1, 1}};
        int[][] result = MatrixOperations.merge(board, brick, 5, 5);
        assertEquals(1, result[5][5]);
    }

    @Test
    @DisplayName("clearCompletedRows: Clear single completed row")
    void testClearSingleRow() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        for (int j = 0; j < BOARD_WIDTH; j++) {
            board[18][j] = 1;
        }
        ClearRow result = MatrixOperations.clearCompletedRows(board);
        assertEquals(1, result.getLinesRemoved());
        assertEquals(100, result.getScoreBonus());
    }
}

