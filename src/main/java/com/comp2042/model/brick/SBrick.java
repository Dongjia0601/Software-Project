package com.comp2042.model.brick;

import com.comp2042.util.MatrixOperations;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.List;

/**
 * S-shaped tetromino.
 * Has 2 unique rotational states.
 * Color: Pink - distinctive color for S-piece.
 */
public final class SBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    public SBrick() {
        brickMatrix.add(new int[][]{
                {0, 5, 5, 0},
                {5, 5, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {5, 0, 0, 0},
                {5, 5, 0, 0},
                {0, 5, 0, 0},
                {0, 0, 0, 0}
        });
    }

    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }

    @Override
    public Paint getColor() {
        return Color.LIGHTPINK;  // Pink color for S-piece
    }
}
