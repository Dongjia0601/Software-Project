package com.comp2042;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for MatrixOperations class.
 * Tests collision detection, matrix merging, row clearing, and score calculation.
 * 
 * <p>These tests validate the core game logic operations that are critical for
 * proper Tetris gameplay, including edge cases and boundary conditions.</p>
 * 
 * @author Dong, Jia.
 */
@DisplayName("MatrixOperations Tests")
class MatrixOperationsTest {

    // Test fixtures: standard 10x20 game board
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;

    // ========== intersect() Tests ==========

    @Test
    @DisplayName("intersect: No collision when brick is in empty space")
    void testIntersectNoCollision() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{
            {1, 1},
            {1, 1}
        };
        
        // Place brick at (0, 0) - should not collide
        assertFalse(MatrixOperations.intersect(board, brick, 0, 0));
    }

    @Test
    @DisplayName("intersect: Collision when brick overlaps occupied cell")
    void testIntersectCollisionWithOccupiedCell() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        board[5][5] = 2; // Place a block at (5, 5)
        
        int[][] brick = new int[][]{
            {1, 1},
            {1, 1}
        };
        
        // Place brick at (4, 4) - should overlap with occupied cell at (5, 5)
        assertTrue(MatrixOperations.intersect(board, brick, 4, 4));
    }

    @Test
    @DisplayName("intersect: Collision when brick goes out of bounds to the right")
    void testIntersectOutOfBoundsRight() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{
            {1, 1, 1}
        };
        
        // Place brick at x=8, which would extend to x=10 (out of bounds for width=10)
        assertTrue(MatrixOperations.intersect(board, brick, 8, 0));
    }

    @Test
    @DisplayName("intersect: Collision when brick goes out of bounds to the left")
    void testIntersectOutOfBoundsLeft() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{
            {1, 1}
        };
        
        // Place brick at x=-1 (out of bounds)
        assertTrue(MatrixOperations.intersect(board, brick, -1, 0));
    }

    @Test
    @DisplayName("intersect: Collision when brick goes below board")
    void testIntersectOutOfBoundsBottom() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{
            {1},
            {1}
        };
        
        // Place brick at y=19, which would extend to y=20 (out of bounds for height=20)
        assertTrue(MatrixOperations.intersect(board, brick, 0, 19));
    }

    @Test
    @DisplayName("intersect: No collision when brick extends above board (spawn buffer)")
    void testIntersectAboveBoardAllowed() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{
            {1, 1, 1, 1}
        };
        
        // Place brick at y=-1 (above board) - should be allowed for spawn buffer
        assertFalse(MatrixOperations.intersect(board, brick, 0, -1));
    }

    @Test
    @DisplayName("intersect: Empty brick (all zeros) should not collide")
    void testIntersectEmptyBrick() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        board[5][5] = 2; // Place a block
        
        int[][] brick = new int[][]{
            {0, 0},
            {0, 0}
        };
        
        // Empty brick should not collide even if placed on occupied cell
        assertFalse(MatrixOperations.intersect(board, brick, 4, 4));
    }

    @Test
    @DisplayName("intersect: Partial brick overlap detection")
    void testIntersectPartialOverlap() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        board[3][3] = 2; // Place a block
        
        int[][] brick = new int[][]{
            {1, 1, 1},
            {1, 0, 1},
            {1, 1, 1}
        };
        
        // Place brick so that only one cell overlaps
        assertTrue(MatrixOperations.intersect(board, brick, 2, 2));
    }

    // ========== merge() Tests ==========

    @Test
    @DisplayName("merge: Successfully merge brick onto empty board")
    void testMergeOntoEmptyBoard() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{
            {1, 1},
            {1, 1}
        };
        
        int[][] result = MatrixOperations.merge(board, brick, 5, 5);
        
        // Verify brick was merged
        assertEquals(1, result[5][5]);
        assertEquals(1, result[5][6]);
        assertEquals(1, result[6][5]);
        assertEquals(1, result[6][6]);
        
        // Verify original board was not modified
        assertEquals(0, board[5][5]);
    }

    @Test
    @DisplayName("merge: Merge preserves existing board blocks")
    void testMergePreservesExistingBlocks() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        board[0][0] = 2; // Existing block
        
        int[][] brick = new int[][]{
            {1, 1}
        };
        
        int[][] result = MatrixOperations.merge(board, brick, 5, 5);
        
        // Verify existing block is preserved
        assertEquals(2, result[0][0]);
        
        // Verify brick was merged at new location
        assertEquals(1, result[5][5]);
        assertEquals(1, result[5][6]);
    }

    @Test
    @DisplayName("merge: Merge with zero cells in brick (should not overwrite)")
    void testMergeWithZeroCells() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        board[5][5] = 2; // Existing block
        
        int[][] brick = new int[][]{
            {1, 0},
            {0, 1}
        };
        
        int[][] result = MatrixOperations.merge(board, brick, 4, 4);
        
        // Verify zero cells don't overwrite
        assertEquals(2, result[5][5]); // Should remain unchanged
        
        // Verify non-zero cells are merged
        assertEquals(1, result[4][4]);
        assertEquals(1, result[5][6]);
    }

    @Test
    @DisplayName("merge: Returns deep copy, original board unchanged")
    void testMergeReturnsDeepCopy() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int[][] brick = new int[][]{
            {1}
        };
        
        int[][] result = MatrixOperations.merge(board, brick, 0, 0);
        
        // Modify result
        result[0][0] = 999;
        
        // Original should be unchanged
        assertEquals(0, board[0][0]);
    }

    // ========== clearCompletedRows() Tests ==========

    @Test
    @DisplayName("clearCompletedRows: Clear single completed row")
    void testClearSingleRow() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        
        // Fill row 18 completely
        for (int j = 0; j < BOARD_WIDTH; j++) {
            board[18][j] = 1;
        }
        
        // Add some blocks in row 17 (not full)
        board[17][0] = 1;
        board[17][1] = 1;
        
        ClearRow result = MatrixOperations.clearCompletedRows(board);
        
        assertEquals(1, result.getLinesRemoved());
        assertEquals(100, result.getScoreBonus()); // Single line = 100 points
        
        // Verify row 18 is now empty
        for (int j = 0; j < BOARD_WIDTH; j++) {
            assertEquals(0, result.getNewMatrix()[18][j]);
        }
        
        // Verify row 17 blocks shifted down to row 18
        assertEquals(1, result.getNewMatrix()[18][0]);
        assertEquals(1, result.getNewMatrix()[18][1]);
    }

    @Test
    @DisplayName("clearCompletedRows: Clear multiple completed rows (Tetris)")
    void testClearMultipleRows() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        
        // Fill rows 16, 17, 18, 19 completely
        for (int i = 16; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                board[i][j] = 1;
            }
        }
        
        // Add some blocks in row 15 (not full)
        board[15][0] = 2;
        
        ClearRow result = MatrixOperations.clearCompletedRows(board);
        
        assertEquals(4, result.getLinesRemoved());
        assertEquals(800, result.getScoreBonus()); // Tetris = 800 points
        
        // Verify all cleared rows are now empty
        for (int i = 16; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                assertEquals(0, result.getNewMatrix()[i][j]);
            }
        }
        
        // Verify row 15 block shifted down
        assertEquals(2, result.getNewMatrix()[19][0]);
    }

    @Test
    @DisplayName("clearCompletedRows: No rows cleared when no rows are full")
    void testClearNoRows() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        
        // Add some partial blocks
        board[18][0] = 1;
        board[18][1] = 1;
        board[19][5] = 2;
        
        ClearRow result = MatrixOperations.clearCompletedRows(board);
        
        assertEquals(0, result.getLinesRemoved());
        assertEquals(0, result.getScoreBonus());
        
        // Verify blocks remain in same positions
        assertEquals(1, result.getNewMatrix()[18][0]);
        assertEquals(1, result.getNewMatrix()[18][1]);
        assertEquals(2, result.getNewMatrix()[19][5]);
    }

    @Test
    @DisplayName("clearCompletedRows: Clear rows with different colors")
    void testClearRowsWithDifferentColors() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        
        // Fill row 18 with mixed colors
        for (int j = 0; j < BOARD_WIDTH; j++) {
            board[18][j] = (j % 2 == 0) ? 1 : 2;
        }
        
        ClearRow result = MatrixOperations.clearCompletedRows(board);
        
        assertEquals(1, result.getLinesRemoved());
        
        // Verify row is cleared
        for (int j = 0; j < BOARD_WIDTH; j++) {
            assertEquals(0, result.getNewMatrix()[18][j]);
        }
    }

    @Test
    @DisplayName("clearCompletedRows: Score calculation for different line counts")
    void testScoreCalculation() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        
        // Test 1 line
        fillRow(board, 18, 1);
        ClearRow result1 = MatrixOperations.clearCompletedRows(board);
        assertEquals(100, result1.getScoreBonus());
        
        // Test 2 lines
        board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        fillRow(board, 17, 1);
        fillRow(board, 18, 1);
        ClearRow result2 = MatrixOperations.clearCompletedRows(board);
        assertEquals(300, result2.getScoreBonus());
        
        // Test 3 lines
        board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        fillRow(board, 16, 1);
        fillRow(board, 17, 1);
        fillRow(board, 18, 1);
        ClearRow result3 = MatrixOperations.clearCompletedRows(board);
        assertEquals(500, result3.getScoreBonus());
        
        // Test 4 lines (Tetris)
        board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        fillRow(board, 15, 1);
        fillRow(board, 16, 1);
        fillRow(board, 17, 1);
        fillRow(board, 18, 1);
        ClearRow result4 = MatrixOperations.clearCompletedRows(board);
        assertEquals(800, result4.getScoreBonus());
    }

    @Test
    @DisplayName("clearCompletedRows: Non-contiguous rows cleared correctly")
    void testClearNonContiguousRows() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        
        // Fill rows 15, 17, 19 (skip 16, 18)
        fillRow(board, 15, 1);
        fillRow(board, 17, 1);
        fillRow(board, 19, 1);
        
        // Add partial block in row 16
        board[16][0] = 2;
        
        ClearRow result = MatrixOperations.clearCompletedRows(board);
        
        assertEquals(3, result.getLinesRemoved());
        assertEquals(500, result.getScoreBonus()); // 3 lines = 500 points
        
        // Verify cleared rows are empty
        for (int j = 0; j < BOARD_WIDTH; j++) {
            assertEquals(0, result.getNewMatrix()[15][j]);
            assertEquals(0, result.getNewMatrix()[17][j]);
            assertEquals(0, result.getNewMatrix()[19][j]);
        }
        
        // Verify row 16 block shifted down appropriately
        assertEquals(2, result.getNewMatrix()[17][0]);
    }

    // ========== copy() Tests ==========

    @Test
    @DisplayName("copy: Creates deep copy of matrix")
    void testCopyCreatesDeepCopy() {
        int[][] original = new int[][]{
            {1, 2, 3},
            {4, 5, 6}
        };
        
        int[][] copy = MatrixOperations.copy(original);
        
        // Modify copy
        copy[0][0] = 999;
        
        // Original should be unchanged
        assertEquals(1, original[0][0]);
        assertEquals(999, copy[0][0]);
    }

    @Test
    @DisplayName("copy: Handles empty matrix")
    void testCopyEmptyMatrix() {
        int[][] original = new int[0][0];
        int[][] copy = MatrixOperations.copy(original);
        
        assertNotNull(copy);
        assertEquals(0, copy.length);
    }

    @Test
    @DisplayName("copy: Handles jagged matrix")
    void testCopyJaggedMatrix() {
        int[][] original = new int[][]{
            {1, 2},
            {3, 4, 5},
            {6}
        };
        
        int[][] copy = MatrixOperations.copy(original);
        
        assertEquals(original.length, copy.length);
        assertEquals(original[0].length, copy[0].length);
        assertEquals(original[1].length, copy[1].length);
        assertEquals(original[2].length, copy[2].length);
        
        // Verify values
        assertEquals(1, copy[0][0]);
        assertEquals(5, copy[1][2]);
        assertEquals(6, copy[2][0]);
    }

    // ========== Helper Methods ==========

    /**
     * Helper method to fill a row completely with a given value.
     */
    private void fillRow(int[][] board, int row, int value) {
        for (int j = 0; j < board[row].length; j++) {
            board[row][j] = value;
        }
    }
}

