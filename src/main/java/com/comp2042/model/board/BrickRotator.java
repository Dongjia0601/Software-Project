package com.comp2042.model.board;

import com.comp2042.model.brick.Brick;

/**
 * Manages brick rotation state and calculates potential rotations.
 * Tracks current shape index and provides lookahead for collision checks.
 * 
 * @author Dong, Jia.
 */
public class BrickRotator {

    private Brick brick;
    private int currentShape = 0;

    /**
     * Calculates next shape for clockwise rotation without applying it.
     * Used for collision checking before rotation.
     *
     * @return NextShapeInfo with potential shape and index
     */
    public NextShapeInfo calculateNextShapeInfo() {
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    /**
     * Calculates previous shape for counterclockwise rotation without applying it.
     * Used for collision checking before rotation.
     *
     * @return NextShapeInfo with potential shape and index
     */
    public NextShapeInfo calculatePreviousShapeInfo() {
        int prevShape = currentShape;
        prevShape = (--prevShape + brick.getShapeMatrix().size()) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(prevShape), prevShape);
    }

    /**
     * Gets the current rotation state shape matrix.
     *
     * @return Current shape matrix
     */
    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }

    /**
     * Sets the rotation state index (applies a validated rotation).
     *
     * @param currentShape New shape index
     */
    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    /**
     * Assigns a new brick to manage and resets the current shape index to 0.
     *
     * @param brick The new Brick instance to manage.
     */
    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }
    
    /**
     * Gets the current brick being managed.
     *
     * @return The current Brick instance.
     */
    public Brick getBrick() {
        return brick;
    }

}