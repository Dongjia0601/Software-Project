package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.IBrick;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BrickRotator class.
 * Tests rotation logic, shape transitions, and boundary conditions.
 * 
 * <p>These tests validate the rotation mechanics that are critical for
 * proper Tetris gameplay, including clockwise and counterclockwise rotations.</p>
 * 
 * @author Dong, Jia.
 */
@DisplayName("BrickRotator Tests")
class BrickRotatorTest {

    private BrickRotator rotator;
    private Brick iBrick;

    @BeforeEach
    void setUp() {
        rotator = new BrickRotator();
        iBrick = new IBrick();
        rotator.setBrick(iBrick);
    }

    @Test
    @DisplayName("Initial state: Current shape is first shape")
    void testInitialState() {
        int[][] currentShape = rotator.getCurrentShape();
        int[][] expectedFirstShape = iBrick.getShapeMatrix().get(0);
        
        assertArrayEquals(expectedFirstShape, currentShape);
    }

    @Test
    @DisplayName("calculateNextShapeInfo: Returns next shape without modifying state")
    void testCalculateNextShapeInfo() {
        int[][] initialShape = rotator.getCurrentShape();
        NextShapeInfo nextInfo = rotator.calculateNextShapeInfo();
        
        // Verify next shape is different
        assertNotEquals(initialShape, nextInfo.getShape());
        
        // Verify current state unchanged (calculateNextShapeInfo doesn't modify)
        assertArrayEquals(initialShape, rotator.getCurrentShape());
        
        // Verify next shape index is correct
        assertEquals(1, nextInfo.getPosition());
    }

    @Test
    @DisplayName("calculateNextShapeInfo: Wraps around to first shape")
    void testCalculateNextShapeWrapsAround() {
        // I-brick has 2 shapes, so after shape 0, next is shape 1
        // After shape 1, should wrap to shape 0
        
        // Start at shape 0
        assertEquals(0, getCurrentShapeIndex());
        
        // Move to shape 1
        rotator.setCurrentShape(1);
        NextShapeInfo nextInfo = rotator.calculateNextShapeInfo();
        
        // Should wrap to shape 0
        assertEquals(0, nextInfo.getPosition());
    }

    @Test
    @DisplayName("calculatePreviousShapeInfo: Returns previous shape without modifying state")
    void testCalculatePreviousShapeInfo() {
        // Set to shape 1
        rotator.setCurrentShape(1);
        int[][] currentShape = rotator.getCurrentShape();
        
        NextShapeInfo prevInfo = rotator.calculatePreviousShapeInfo();
        
        // Verify previous shape is different
        assertNotEquals(currentShape, prevInfo.getShape());
        
        // Verify current state unchanged
        assertArrayEquals(currentShape, rotator.getCurrentShape());
        
        // Verify previous shape index is correct
        assertEquals(0, prevInfo.getPosition());
    }

    @Test
    @DisplayName("calculatePreviousShapeInfo: Wraps around to last shape")
    void testCalculatePreviousShapeWrapsAround() {
        // Start at shape 0
        assertEquals(0, getCurrentShapeIndex());
        
        NextShapeInfo prevInfo = rotator.calculatePreviousShapeInfo();
        
        // Should wrap to last shape (shape 1 for I-brick)
        int totalShapes = iBrick.getShapeMatrix().size();
        assertEquals(totalShapes - 1, prevInfo.getPosition());
    }

    @Test
    @DisplayName("setCurrentShape: Updates current shape")
    void testSetCurrentShape() {
        int[][] shape0 = iBrick.getShapeMatrix().get(0);
        int[][] shape1 = iBrick.getShapeMatrix().get(1);
        
        // Initially at shape 0
        assertArrayEquals(shape0, rotator.getCurrentShape());
        
        // Set to shape 1
        rotator.setCurrentShape(1);
        assertArrayEquals(shape1, rotator.getCurrentShape());
    }

    @Test
    @DisplayName("setBrick: Resets to first shape and updates brick")
    void testSetBrick() {
        // Rotate to shape 1
        rotator.setCurrentShape(1);
        assertEquals(1, getCurrentShapeIndex());
        
        // Set new brick (should reset to shape 0)
        Brick newBrick = new IBrick();
        rotator.setBrick(newBrick);
        
        // Should be at shape 0
        assertEquals(0, getCurrentShapeIndex());
        
        // Should have new brick
        assertEquals(newBrick, rotator.getBrick());
    }

    @Test
    @DisplayName("Rotation cycle: Complete rotation through all shapes")
    void testCompleteRotationCycle() {
        int totalShapes = iBrick.getShapeMatrix().size();
        
        // Rotate through all shapes forward
        for (int i = 0; i < totalShapes; i++) {
            assertEquals(i, getCurrentShapeIndex());
            NextShapeInfo next = rotator.calculateNextShapeInfo();
            rotator.setCurrentShape(next.getPosition());
        }
        
        // Should wrap back to shape 0
        assertEquals(0, getCurrentShapeIndex());
    }

    @Test
    @DisplayName("Rotation cycle: Complete rotation backwards")
    void testCompleteRotationCycleBackwards() {
        int totalShapes = iBrick.getShapeMatrix().size();
        
        // Rotate backwards through all shapes
        for (int i = 0; i < totalShapes; i++) {
            int currentIndex = getCurrentShapeIndex();
            NextShapeInfo prev = rotator.calculatePreviousShapeInfo();
            rotator.setCurrentShape(prev.getPosition());
            
            // Verify we moved to previous shape
            int expectedIndex = (currentIndex - 1 + totalShapes) % totalShapes;
            assertEquals(expectedIndex, getCurrentShapeIndex());
        }
    }

    @Test
    @DisplayName("getCurrentShape: Returns correct shape matrix")
    void testGetCurrentShape() {
        int[][] expectedShape0 = iBrick.getShapeMatrix().get(0);
        int[][] actualShape = rotator.getCurrentShape();
        
        assertArrayEquals(expectedShape0, actualShape);
        
        // Change shape and verify
        rotator.setCurrentShape(1);
        int[][] expectedShape1 = iBrick.getShapeMatrix().get(1);
        actualShape = rotator.getCurrentShape();
        
        assertArrayEquals(expectedShape1, actualShape);
    }

    @Test
    @DisplayName("getBrick: Returns current brick")
    void testGetBrick() {
        assertEquals(iBrick, rotator.getBrick());
        
        Brick newBrick = new IBrick();
        rotator.setBrick(newBrick);
        assertEquals(newBrick, rotator.getBrick());
    }

    @Test
    @DisplayName("Multiple rotations: State consistency")
    void testMultipleRotationsStateConsistency() {
        // Perform multiple forward rotations
        for (int i = 0; i < 10; i++) {
            NextShapeInfo next = rotator.calculateNextShapeInfo();
            rotator.setCurrentShape(next.getPosition());
            
            // Verify state is consistent
            int[][] currentShape = rotator.getCurrentShape();
            int expectedIndex = (i + 1) % iBrick.getShapeMatrix().size();
            int[][] expectedShape = iBrick.getShapeMatrix().get(expectedIndex);
            
            assertArrayEquals(expectedShape, currentShape);
        }
    }

    // ========== Helper Methods ==========

    /**
     * Helper method to get the current shape index by comparing with brick shapes.
     */
    private int getCurrentShapeIndex() {
        int[][] currentShape = rotator.getCurrentShape();
        var shapes = rotator.getBrick().getShapeMatrix();
        
        for (int i = 0; i < shapes.size(); i++) {
            if (arraysEqual(currentShape, shapes.get(i))) {
                return i;
            }
        }
        return -1; // Not found
    }

    /**
     * Helper method to compare two 2D arrays.
     */
    private boolean arraysEqual(int[][] a, int[][] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (a[i].length != b[i].length) return false;
            for (int j = 0; j < a[i].length; j++) {
                if (a[i][j] != b[i][j]) return false;
            }
        }
        return true;
    }
}

