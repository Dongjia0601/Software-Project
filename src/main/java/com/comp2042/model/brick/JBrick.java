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

    /**
     * Constructs a J-shaped tetromino.
     * Initializes the four rotational states.
     */
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

    /**
     * Gets a defensive copy of the shape matrix containing all rotational states.
     * 
     * @return a deep copy of the brick's shape matrix
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }

    /**
     * Gets the color for rendering this brick.
     * 
     * @return light salmon color matching standard Tetris J-piece
     */
    @Override
    public Paint getColor() {
        return Color.LIGHTSALMON;
    }
}
