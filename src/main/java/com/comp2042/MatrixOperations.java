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
                if (brick[i][j] != 0 && (checkOutOfBound(matrix, targetX, targetY) || matrix[targetY][targetX] != 0)) {
                    return true; // Intersection found
                }
            }
        }
        return false; // No intersection found
    }

    /**
     * Helper method to check if given coordinates (targetX, targetY) are out of the matrix bounds.
     * Assumes standard matrix notation: matrix[row][column].
     *
     * @param matrix   The matrix to check against.
     * @param targetX  The column index to check.
     * @param targetY  The row index to check.
     * @return true if the coordinates are out of bounds, false otherwise.
     */
    private static boolean checkOutOfBound(int[][] matrix, int targetX, int targetY) {
        // Check if targetX and targetY are within the valid range for the matrix
        // targetY must be a valid row index (0 <= targetY < matrix.length)
        // targetX must be a valid column index for the row matrix[targetY] (0 <= targetX < matrix[targetY].length)
        // Coordinates are within bounds
        return targetX < 0 || targetY >= matrix.length || targetX >= matrix[targetY].length;
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
     * Scans the board matrix for completed rows (rows filled with non-zero values),
     * removes them, shifts the remaining rows down, calculates a score bonus,
     * and returns the results encapsulated in a ClearRow object.
     *
     * @param matrix The current state of the game board matrix.
     * @return A ClearRow object containing the number of lines removed, the new matrix, and the score bonus.
     */
    public static ClearRow checkRemoving(final int[][] matrix) {
        // Temporary matrix to hold the new state after rows are cleared
        int[][] newMatrix = new int[matrix.length][matrix[0].length];
        // Deque to efficiently add rows that are not cleared
        Deque<int[]> rowsToKeep = new ArrayDeque<>();
        // List to store the indices of rows that were cleared
        List<Integer> clearedRowIndices = new ArrayList<>();

        // Scan the matrix from top to bottom
        for (int i = 0; i < matrix.length; i++) {
            int[] currentRow = new int[matrix[i].length];
            boolean rowIsFull = true; // Assume the row is full initially

            // Check each cell in the current row
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) {
                    rowIsFull = false; // Found an empty cell, row is not full
                }
                currentRow[j] = matrix[i][j]; // Copy the cell value
            }

            if (rowIsFull) {
                clearedRowIndices.add(i); // Mark this row for removal
            } else {
                rowsToKeep.add(currentRow); // Keep this row
            }
        }

        // Reconstruct the new matrix, placing the kept rows from bottom to top
        for (int i = matrix.length - 1; i >= 0; i--) {
            int[] row = rowsToKeep.pollLast(); // Get the next row to place (from the end of the deque)
            if (row != null) {
                newMatrix[i] = row; // Place the row in the new matrix
            } else {
                // If no more rows to keep, fill the remaining top rows with zeros
                break;
            }
        }

        // Calculate score bonus based on the number of lines cleared (Tetris scoring rule)
        int scoreBonus = 50 * clearedRowIndices.size() * clearedRowIndices.size();

        // Return the results encapsulated in a ClearRow object
        return new ClearRow(clearedRowIndices.size(), newMatrix, scoreBonus);
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