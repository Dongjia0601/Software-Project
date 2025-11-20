package com.comp2042.model.brick;

import com.comp2042.util.MatrixOperations;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.List;

/**
 * Z-shaped tetromino.
 * Has 2 unique rotational states.
 * Color: Burlywood (tan/brown) - distinctive color for Z-piece.
 */
public final class ZBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    public ZBrick() {
        brickMatrix.add(new int[][]{
                {7, 7, 0, 0},
                {0, 7, 7, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 7, 0, 0},
                {7, 7, 0, 0},
                {7, 0, 0, 0},
                {0, 0, 0, 0}
        });
    }

    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }

    @Override
    public Paint getColor() {
        return Color.BURLYWOOD;  // Burlywood color for Z-piece
    }
}
