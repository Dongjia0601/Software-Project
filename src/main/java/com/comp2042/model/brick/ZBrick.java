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

    /**
     * Constructs a Z-shaped tetromino.
     * Initializes the two rotational states.
     */
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
     * @return burlywood color matching standard Tetris Z-piece
     */
    @Override
    public Paint getColor() {
        return Color.BURLYWOOD;
    }
}
