package com.comp2042.model.brick;

import javafx.scene.paint.Paint;
import java.util.List;

/**
 * Interface representing a Tetris brick (Tetromino).
 * Provides access to the brick's rotational states as shape matrices.
 * Follows Strategy Pattern: each brick type knows its own display color (polymorphic behavior).
 * 
 * @author Dong, Jia.
 */
public interface Brick {

    /**
     * Gets all rotational states of this brick as shape matrices.
     * Each matrix represents one possible orientation.
     *
     * @return List of shape matrices for each rotation
     */
    List<int[][]> getShapeMatrix();
    
    /**
     * Gets the display color for this brick type (Strategy Pattern).
     * Each brick implementation defines its own color, eliminating the need
     * for switch statements based on type codes.
     *
     * @return Paint color for this brick type
     */
    Paint getColor();
}