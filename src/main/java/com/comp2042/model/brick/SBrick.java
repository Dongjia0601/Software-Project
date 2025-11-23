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

    /**
     * Constructs an S-shaped tetromino.
     * Initializes the two rotational states.
     */
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
     * @return light pink color matching standard Tetris S-piece
     */
    @Override
    public Paint getColor() {
        return Color.LIGHTPINK;
    }
}
