package com.comp2042.util;

import com.comp2042.dto.ClearRow;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for matrix operations on the game board and brick shapes.
 * Provides collision detection, merging, and row clearing functionality.
 * 
 * @author Dong, Jia.
 */
public class MatrixOperations {

    // Prevent instantiation of this utility class
    private MatrixOperations() {
    }

    /**
     * Checks if a brick collides with the board or boundaries at the given position.
     * Allows spawn buffer above the board (negative y coordinates).
     *
     * @param matrix The game board state
     * @param brick  The brick shape to check
     * @param x      Column position
     * @param y      Row position
     * @return true if collision detected, false otherwise
     */
    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        // Null and empty matrix checks
        if (matrix == null || matrix.length == 0) {
            return false; // Empty board, no collision
        }
        if (brick == null || brick.length == 0) {
            return false; // Empty brick, no collision
        }
        
        int matrixHeight = matrix.length;
        int matrixWidth = (matrixHeight > 0 && matrix[0] != null) ? matrix[0].length : 0;

        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                // Calculate target coordinates: brick element (i, j) maps to board (y+i, x+j)
                int targetY = y + i;
                int targetX = x + j;

                if (brick[i][j] != 0) {
                    if (targetX < 0 || targetX >= matrixWidth) {
                        return true;
                    }

                    if (targetY >= matrixHeight) {
                        return true;
                    }

                    if (targetY < 0) {
                        continue;
                    }

                    if (matrix[targetY][targetX] != 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Creates a deep copy of a matrix.
     *
     * @param original The matrix to copy
     * @return A new matrix with copied values
     */
    public static int[][] copy(int[][] original) {
        if (original == null) {
            return null;
        }
        if (original.length == 0) {
            return new int[0][];
        }
        
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            int[] originalRow = original[i];
            if (originalRow == null) {
                copy[i] = null;
            } else {
                int rowLength = originalRow.length;
                copy[i] = new int[rowLength];
                System.arraycopy(originalRow, 0, copy[i], 0, rowLength);
            }
        }
        return copy;
    }

    /**
     * Merges a brick onto the board at the specified position.
     * Returns a new matrix without modifying the original.
     *
     * @param filledFields The current game board
     * @param brick        The brick shape to place
     * @param x            Column position
     * @param y            Row position
     * @return New board matrix with the brick merged
     */
    public static int[][] merge(int[][] filledFields, int[][] brick, int x, int y) {
        if (filledFields == null) {
            throw new IllegalArgumentException("filledFields cannot be null");
        }
        if (brick == null) {
            return copy(filledFields); // No brick to merge
        }
        
        int[][] newMatrix = copy(filledFields);

        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                // Calculate target coordinates: brick element (i, j) maps to board (y+i, x+j)
                int targetY = y + i;
                int targetX = x + j;

                if (brick[i][j] != 0) {
                    // Bounds check to prevent ArrayIndexOutOfBoundsException
                    if (targetY >= 0 && targetY < newMatrix.length && targetX >= 0 && targetX < newMatrix[targetY].length) {
                        newMatrix[targetY][targetX] = brick[i][j];
                    }
                }
            }
        }
        return newMatrix;
    }

    /**
     * Clears completed rows, shifts remaining rows down, and calculates score bonus.
     *
     * @param matrix The current game board
     * @return ClearRow object with lines cleared, new matrix, and score bonus
     */
    public static ClearRow clearCompletedRows(final int[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            return new ClearRow(0, new int[0][], 0, new ArrayList<>());
        }
        if (matrix[0] == null || matrix[0].length == 0) {
            return new ClearRow(0, new int[matrix.length][], 0, new ArrayList<>());
        }
        
        int[][] clearedMatrix = new int[matrix.length][matrix[0].length];
        Deque<int[]> newRows = new ArrayDeque<>();
        List<Integer> clearedRows = new ArrayList<>();

        for (int i = 0; i < matrix.length; i++) {
            int[] currentRow = new int[matrix[i].length];
            boolean rowToClear = true;

            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) {
                    rowToClear = false;
                }
                currentRow[j] = matrix[i][j];
            }

            if (rowToClear) {
                clearedRows.add(i);
            } else {
                newRows.add(currentRow);
            }
        }

        // Reconstruct matrix by placing kept rows from bottom to top
        for (int i = matrix.length - 1; i >= 0; i--) {
            int[] row = newRows.pollLast();
            clearedMatrix[i] = Objects.requireNonNullElseGet(row, () -> new int[matrix[0].length]);
        }

        int scoreBonus = calculateLineClearScore(clearedRows.size());
        return new ClearRow(clearedRows.size(), clearedMatrix, scoreBonus, clearedRows);
    }

    private static final int SINGLE_LINE_SCORE = 100;
    private static final int DOUBLE_LINE_SCORE = 300;
    private static final int TRIPLE_LINE_SCORE = 500;
    private static final int TETRIS_SCORE = 800;  // 4-line clear bonus
    private static final int NO_LINES_SCORE = 0;
    
    /**
     * Calculates score bonus based on standard Tetris scoring rules.
     * Uses named constants for better maintainability and game balance tuning.
     * 
     * @param linesCleared Number of lines cleared (1-4)
     * @return Score bonus points
     */
    private static int calculateLineClearScore(int linesCleared) {
        return switch (linesCleared) {
            case 1 -> SINGLE_LINE_SCORE;
            case 2 -> DOUBLE_LINE_SCORE;
            case 3 -> TRIPLE_LINE_SCORE;
            case 4 -> TETRIS_SCORE;
            default -> NO_LINES_SCORE;
        };
    }

    /**
     * Creates a deep copy of a list of matrices.
     *
     * @param list The list to copy
     * @return New list with deep-copied matrices
     */
    public static List<int[][]> deepCopyList(List<int[][]> list) {
        return list.stream().map(MatrixOperations::copy).collect(Collectors.toList());
    }
}