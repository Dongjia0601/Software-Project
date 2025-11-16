package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Brick implementations (IBrick, JBrick, LBrick, OBrick, SBrick, TBrick, ZBrick).
 * Tests that all brick types have valid shape matrices and proper structure.
 * 
 * <p>These tests validate that all Tetris brick implementations are correct,
 * ensuring each brick has valid shape matrices for all rotation states.</p>
 * 
 * @author Dong, Jia.
 */
@DisplayName("Brick Implementations Tests")
class BrickImplementationsTest {

    // ========== Common Brick Tests ==========

    @Test
    @DisplayName("All brick types: Have non-empty shape matrices")
    void testAllBricksHaveShapeMatrices() {
        String[] types = {"I", "J", "L", "O", "S", "T", "Z"};
        
        for (String type : types) {
            Brick brick = BrickFactory.createBrick(type);
            assertNotNull(brick.getShapeMatrix(), 
                "Brick type " + type + " should have shape matrix");
            assertFalse(brick.getShapeMatrix().isEmpty(), 
                "Brick type " + type + " should have at least one shape");
        }
    }

    @Test
    @DisplayName("All brick types: Shape matrices are non-null")
    void testAllBrickShapesNonNull() {
        String[] types = {"I", "J", "L", "O", "S", "T", "Z"};
        
        for (String type : types) {
            Brick brick = BrickFactory.createBrick(type);
            var shapes = brick.getShapeMatrix();
            
            for (int i = 0; i < shapes.size(); i++) {
                assertNotNull(shapes.get(i), 
                    "Brick type " + type + " shape " + i + " should not be null");
            }
        }
    }

    @Test
    @DisplayName("All brick types: Shape matrices are rectangular")
    void testAllBrickShapesRectangular() {
        String[] types = {"I", "J", "L", "O", "S", "T", "Z"};
        
        for (String type : types) {
            Brick brick = BrickFactory.createBrick(type);
            var shapes = brick.getShapeMatrix();
            
            for (int[][] shape : shapes) {
                assertNotNull(shape);
                assertTrue(shape.length > 0, "Shape should have rows");
                assertTrue(shape[0].length > 0, "Shape should have columns");
                
                // Check all rows have same length
                int expectedLength = shape[0].length;
                for (int[] row : shape) {
                    assertEquals(expectedLength, row.length, 
                        "All rows should have same length");
                }
            }
        }
    }

    @Test
    @DisplayName("All brick types: Shape matrices contain valid values")
    void testAllBrickShapesValidValues() {
        String[] types = {"I", "J", "L", "O", "S", "T", "Z"};
        
        for (String type : types) {
            Brick brick = BrickFactory.createBrick(type);
            var shapes = brick.getShapeMatrix();
            
            for (int[][] shape : shapes) {
                boolean hasNonZero = false;
                for (int[] row : shape) {
                    for (int cell : row) {
                        assertTrue(cell >= 0, "Cell values should be non-negative");
                        if (cell != 0) {
                            hasNonZero = true;
                        }
                    }
                }
                assertTrue(hasNonZero, 
                    "Brick type " + type + " should have at least one non-zero cell");
            }
        }
    }

    // ========== Specific Brick Tests ==========

    @Test
    @DisplayName("IBrick: Has 2 rotation states")
    void testIBrickRotations() {
        Brick brick = BrickFactory.createBrick("I");
        assertEquals(2, brick.getShapeMatrix().size(), 
            "I-brick should have 2 rotation states");
    }

    @Test
    @DisplayName("OBrick: Has 1 rotation state (square)")
    void testOBrickRotations() {
        Brick brick = BrickFactory.createBrick("O");
        assertEquals(1, brick.getShapeMatrix().size(), 
            "O-brick should have 1 rotation state (square)");
    }

    @Test
    @DisplayName("OBrick: All rotations are identical")
    void testOBrickIdenticalRotations() {
        Brick brick = BrickFactory.createBrick("O");
        var shapes = brick.getShapeMatrix();
        
        // O-brick should have only one shape (square doesn't rotate)
        assertEquals(1, shapes.size());
        
        // Verify it's a 2x2 square
        int[][] shape = shapes.get(0);
        assertTrue(shape.length >= 2);
        assertTrue(shape[0].length >= 2);
    }

    // ========== Shape Matrix Properties Tests ==========

    @Test
    @DisplayName("All bricks: getShapeMatrix returns defensive copy")
    void testGetShapeMatrixDefensiveCopy() {
        String[] types = {"I", "J", "L", "O", "S", "T", "Z"};
        
        for (String type : types) {
            Brick brick = BrickFactory.createBrick(type);
            var shapes1 = brick.getShapeMatrix();
            var shapes2 = brick.getShapeMatrix();
            
            // Should be different objects (defensive copy)
            assertNotSame(shapes1, shapes2, 
                "getShapeMatrix should return defensive copy for " + type);
            
            // But should have same content
            assertEquals(shapes1.size(), shapes2.size());
        }
    }

    @Test
    @DisplayName("All bricks: Shape matrices are immutable from external modification")
    void testShapeMatricesImmutable() {
        Brick brick = BrickFactory.createBrick("I");
        var shapes = brick.getShapeMatrix();
        
        // Try to modify (should not affect original)
        if (!shapes.isEmpty()) {
            int[][] originalShape = shapes.get(0);
            int originalValue = originalShape[0][0];
            
            // Modify the returned shape
            originalShape[0][0] = 999;
            
            // Get shape again - should be unchanged
            var newShapes = brick.getShapeMatrix();
            assertNotEquals(999, newShapes.get(0)[0][0], 
                "Modifying returned shape should not affect original");
        }
    }

    // ========== Brick Type Uniqueness Tests ==========

    @Test
    @DisplayName("All brick types: Each has unique class")
    void testBrickTypeUniqueness() {
        java.util.Map<String, Class<?>> typeToClass = new java.util.HashMap<>();
        
        String[] types = {"I", "J", "L", "O", "S", "T", "Z"};
        for (String type : types) {
            Brick brick = BrickFactory.createBrick(type);
            typeToClass.put(type, brick.getClass());
        }
        
        // All types should map to different classes
        assertEquals(7, new java.util.HashSet<>(typeToClass.values()).size(), 
            "Each brick type should have unique class");
    }

    @Test
    @DisplayName("All brick types: Consistent creation")
    void testBrickTypeConsistency() {
        String[] types = {"I", "J", "L", "O", "S", "T", "Z"};
        
        for (String type : types) {
            Brick brick1 = BrickFactory.createBrick(type);
            Brick brick2 = BrickFactory.createBrick(type);
            
            // Should be same class
            assertEquals(brick1.getClass(), brick2.getClass(), 
                "Same type should create same class: " + type);
            
            // Should have same number of shapes
            assertEquals(brick1.getShapeMatrix().size(), 
                brick2.getShapeMatrix().size(), 
                "Same type should have same number of shapes: " + type);
        }
    }
}

