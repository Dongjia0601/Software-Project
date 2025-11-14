package com.comp2042;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for performing various operations on 2D integer arrays (matrices).
 * These matrices represent the game board state and brick shapes.
 * Contains methods for collision detection, merging bricks, clearing rows, etc.
 * 
 * @author Dong, Jia.
 */
public class MatrixOperations {

    // Prevent instantiation of this utility class
    private MatrixOperations() {
    }

    /**
     * Checks if a given brick shape intersects with occupied cells on the board
     * or goes out of bounds at a specified position.
     *
     * @param matrix The current state of the game board.
     * @param brick  The shape of the brick to check (as a matrix).
     * @param x      The column offset (x-coordinate) where the brick is positioned.
     * @param y      The row offset (y-coordinate) where the brick is positioned.
     * @return true if there is an intersection or the brick is out of bounds, false otherwise.
     */
    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        // Iterate through the rows of the brick matrix
        int matrixHeight = matrix.length;
        int matrixWidth = matrixHeight > 0 ? matrix[0].length : 0;

        for (int i = 0; i < brick.length; i++) {
            // Iterate through the columns of the brick matrix for the current row
            for (int j = 0; j < brick[i].length; j++) {
                // Calculate the target coordinates on the main board matrix.
                // The brick's top-left corner is at (x, y).
                // So, the brick element at (i, j) maps to the board element at (y+i, x+j).
                int targetY = y + i; // Target row on the board
                int targetX = x + j; // Target column on the board

                // Check if the current brick cell is non-zero (part of the brick shape)
                // and if placing it at (targetX, targetY) would cause a collision.
                // Collision occurs if the target position is out of bounds or already occupied.
                if (brick[i][j] != 0) {
                    // Horizontal out-of-bounds: treat as collision
                    if (targetX < 0 || targetX >= matrixWidth) {
                        return true;
                    }

                    // Below the board: treat as collision
                    if (targetY >= matrixHeight) {
                        return true;
                    }

                    // Above the board: allow spawn buffer (no collision)
                    if (targetY < 0) {
                        continue;
                    }

                    // Occupied cell: collision
                    if (matrix[targetY][targetX] != 0) {
                        return true;
                    }
                }
            }
        }
        return false; // No intersection found
    }

    /**
     * Creates a deep copy of a 2D integer array.
     *
     * @param original The matrix to copy.
     * @return A new matrix containing the same values as the original.
     */
    public static int[][] copy(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            int[] originalRow = original[i];
            int rowLength = originalRow.length;
            copy[i] = new int[rowLength];
            System.arraycopy(originalRow, 0, copy[i], 0, rowLength);
        }
        return copy;
    }

    /**
     * Merges a brick's shape onto a copy of the board matrix at a specified position.
     * This effectively "lands" the brick on the board.
     *
     * @param filledFields The current state of the game board.
     * @param brick        The shape of the brick to merge (as a matrix).
     * @param x            The column offset (x-coordinate) where the brick is placed.
     * @param y            The row offset (y-coordinate) where the brick is placed.
     * @return A new board matrix with the brick merged onto it.
     */
    public static int[][] merge(int[][] filledFields, int[][] brick, int x, int y) {
        int[][] newMatrix = copy(filledFields); // Start with a copy of the current board

        // Iterate through the rows of the brick matrix
        for (int i = 0; i < brick.length; i++) {
            // Iterate through the columns of the brick matrix for the current row
            for (int j = 0; j < brick[i].length; j++) {
                // Calculate the target coordinates on the main board matrix.
                // The brick's top-left corner is at (x, y).
                // So, the brick element at (i, j) maps to the board element at (y+i, x+j).
                int targetY = y + i; // Target row on the board
                int targetX = x + j; // Target column on the board

                // Check if the current brick cell is non-zero (part of the brick shape)
                if (brick[i][j] != 0) {
                    // Place the brick's color value onto the board matrix at the calculated position.
                    // Add a bounds check to prevent ArrayIndexOutOfBoundsException if placement is somehow invalid.
                    if (targetY >= 0 && targetY < newMatrix.length && targetX >= 0 && targetX < newMatrix[targetY].length) {
                        newMatrix[targetY][targetX] = brick[i][j];
                    }
                }
            }
        }
        return newMatrix; // Return the updated board matrix
    }

    /**
     * Clears completed rows from the board matrix, shifts remaining rows down,
     * calculates the score bonus, and returns the results.
     *
     * @param matrix The current state of the game board matrix.
     * @return A ClearRow object containing the number of lines removed, the new matrix, and the score bonus.
     */
    public static ClearRow clearCompletedRows(final int[][] matrix) {
        // Temporary matrix to hold the new state after rows are cleared
        int[][] tmp = new int[matrix.length][matrix[0].length];
        // Deque to efficiently add rows that are not cleared
        Deque<int[]> newRows = new ArrayDeque<>();
        // List to store the indices of rows that were cleared
        List<Integer> clearedRows = new ArrayList<>();

        // Scan the matrix from top to bottom
        for (int i = 0; i < matrix.length; i++) {
            int[] tmpRow = new int[matrix[i].length];
            boolean rowToClear = true; // Assume the row is full initially

            // Check each cell in the current row
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) {
                    rowToClear = false; // Found an empty cell, row is not full
                }
                tmpRow[j] = matrix[i][j]; // Copy the cell value
            }

            if (rowToClear) {
                clearedRows.add(i); // Mark this row for removal
            } else {
                newRows.add(tmpRow); // Keep this row
            }
        }

        // Reconstruct the new matrix, placing the kept rows from bottom to top
        for (int i = matrix.length - 1; i >= 0; i--) {
            int[] row = newRows.pollLast(); // Get the next row to place (from the end of the deque)
            if (row != null) {
                tmp[i] = row; // Place the row in the new matrix
            } else {
                // If no more rows to keep, fill the remaining top rows with zeros
                break;
            }
        }

        // Calculate score bonus based on the number of lines cleared (Tetris scoring rule)
        int scoreBonus = calculateLineClearScore(clearedRows.size());

        // Return the results encapsulated in a ClearRow object
        return new ClearRow(clearedRows.size(), tmp, scoreBonus);
    }

    /**
     * Calculates the score bonus for clearing lines based on Tetris scoring rules.
     * 
     * @param linesCleared the number of lines cleared (1-4)
     * @return the score bonus points
     */
    private static int calculateLineClearScore(int linesCleared) {
        switch (linesCleared) {
            case 1: return 100;  // Single line clear
            case 2: return 300; // Double line clear 
            case 3: return 500; // Triple line clear
            case 4: return 800; // Tetris (I block)
            default: return 0;  // No lines cleared
        }
    }

    /**
     * Creates a deep copy of a List containing 2D integer arrays.
     *
     * @param list The list of matrices to copy.
     * @return A new list containing deep copies of the matrices from the original list.
     */
    public static List<int[][]> deepCopyList(List<int[][]> list) {
        return list.stream().map(MatrixOperations::copy).collect(Collectors.toList());
    }
}