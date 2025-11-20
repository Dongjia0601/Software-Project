package com.comp2042.model.brick;

import com.comp2042.util.MatrixOperations;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.List;

/**
 * J-shaped tetromino.
 * Has 4 unique rotational states.
 * Color: Salmon (orange-pink) - distinctive color for J-piece.
 */
public final class JBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    public JBrick() {
        brickMatrix.add(new int[][]{
                {2, 2, 2, 0},
                {0, 0, 2, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 2, 2, 0},
                {0, 2, 0, 0},
                {0, 2, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 2, 0, 0},
                {0, 2, 2, 2},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 2, 0},
                {0, 0, 2, 0},
                {0, 2, 2, 0},
                {0, 0, 0, 0}
        });
    }

    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }

    @Override
    public Paint getColor() {
        return Color.LIGHTSALMON;  // Salmon color for J-piece
    }
}
