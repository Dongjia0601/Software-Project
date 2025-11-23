package com.comp2042.model.brick;

import com.comp2042.util.MatrixOperations;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.List;

/**
 * I-shaped tetromino (straight line piece).
 * Has 2 unique rotational states.
 * Color: Cyan (light blue) - standard Tetris color for I-piece.
 * 
 * @author Dong, Jia.
 */
public final class IBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs an I-shaped tetromino.
     * Initializes the two rotational states: horizontal and vertical.
     */
    public IBrick() {
        brickMatrix.add(new int[][]{
                {1, 1, 1, 1},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0}
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
     * @return light blue (cyan) color matching standard Tetris I-piece
     */
    @Override
    public Paint getColor() {
        return Color.LIGHTBLUE;
    }

}
