package com.comp2042.model.brick;

import com.comp2042.util.MatrixOperations;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.List;

/**
 * O-shaped tetromino (square).
 * Has only 1 rotational state (rotation has no effect).
 * Color: Yellow - standard Tetris color for O-piece.
 * 
 * @author Dong, Jia.
 */
public final class OBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs an O-shaped tetromino.
     * Since O-pieces are squares, only one rotational state is needed.
     */
    public OBrick() {
        brickMatrix.add(new int[][]{
                {0, 4, 4, 0},
                {0, 4, 4, 0},
                {0, 0, 0, 0},
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
     * @return light yellow color matching standard Tetris O-piece
     */
    @Override
    public Paint getColor() {
        return Color.LIGHTYELLOW;
    }

}
