package com.comp2042.model.board;

import java.awt.Point;

import com.comp2042.util.MatrixOperations;

/**
 * Manages garbage line mechanics for two-player versus mode.
 * Handles insertion of penalty rows with random holes.
 */
class GarbageManager {

    private final int width;
    private final int height;

    GarbageManager(int width, int height) {
        this.width = width;
        this.height = height;
    }

    boolean addGarbageLine(int[][] matrix, BrickRotator brickRotator, Point currentOffset) {
        // Shift all rows up by one
        for (int i = 0; i < height - 1; i++) {
            matrix[i] = matrix[i + 1].clone();
        }

        // Create a garbage line with one random hole
        int[] garbageLine = new int[width];
        int holePosition = (int) (Math.random() * width);
        for (int j = 0; j < width; j++) {
            garbageLine[j] = (j == holePosition) ? 0 : 8;
        }

        matrix[height - 1] = garbageLine;

        if (currentOffset != null && brickRotator.getCurrentShape() != null) {
            return MatrixOperations.intersect(matrix, brickRotator.getCurrentShape(),
                    (int) currentOffset.getX(), (int) currentOffset.getY());
        }
        return false;
    }

    int removeGarbageLines(int[][] matrix, int linesToRemove) {
        if (linesToRemove <= 0) {
            return 0;
        }

        int eliminated = 0;

        for (int row = 0; row < matrix.length && eliminated < linesToRemove; row++) {
            boolean isGarbageLine = true;
            int garbageBlockCount = 0;

            for (int col = 0; col < matrix[row].length; col++) {
                if (matrix[row][col] == 8) {
                    garbageBlockCount++;
                } else if (matrix[row][col] != 0) {
                    isGarbageLine = false;
                    break;
                }
            }

            if (isGarbageLine && garbageBlockCount > 0) {
                for (int r = row; r > 0; r--) {
                    matrix[r] = matrix[r - 1].clone();
                }
                matrix[0] = new int[width];
                eliminated++;
                row--;
            }
        }

        return eliminated;
    }
}

