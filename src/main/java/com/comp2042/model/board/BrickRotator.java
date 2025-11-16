package com.comp2042.model.board;

import com.comp2042.model.brick.Brick;

/**
 * Manages the rotation state of a given Tetris brick.
 * Keeps track of the current shape index and provides methods to get the current shape
 * and calculate the potential next shape if a rotation occurs.
 * 
 * @author Dong, Jia.
 */
public class BrickRotator {

    private Brick brick; // The brick whose rotation state is being managed
    private int currentShape = 0; // The index of the current shape in the brick's shape list

    /**
     * Calculates the potential next shape and its index if the current brick were to rotate.
     * This method does not modify the internal 'currentShape' index of this BrickRotator instance.
     *
     * @return A NextShapeInfo object containing the potential next shape matrix and its index.
     */
    public NextShapeInfo calculateNextShapeInfo() { // Assuming renamed method
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapeMatrix().size(); // Increment and wrap around
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    /**
     * Calculates the potential previous shape and its index if the current brick were to rotate counterclockwise.
     * This method does not modify the internal 'currentShape' index of this BrickRotator instance.
     *
     * @return A NextShapeInfo object containing the potential previous shape matrix and its index.
     */
    public NextShapeInfo calculatePreviousShapeInfo() {
        int prevShape = currentShape;
        prevShape = (--prevShape + brick.getShapeMatrix().size()) % brick.getShapeMatrix().size(); // Decrement and wrap around
        return new NextShapeInfo(brick.getShapeMatrix().get(prevShape), prevShape);
    }

    /**
     * Gets the matrix representing the current rotation state of the managed brick.
     *
     * @return The current shape matrix.
     */
    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }

    /**
     * Sets the internal index representing the current rotation state.
     * This is used to apply a rotation after a collision check passes.
     *
     * @param currentShape The new index for the current shape.
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
        currentShape = 0; // Reset to the first shape of the new brick
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