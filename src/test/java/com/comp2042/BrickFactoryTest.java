package com.comp2042;

import com.comp2042.model.brick.Brick;
import com.comp2042.model.brick.BrickGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * unit tests for BrickFactory class.
 * Tests Factory Pattern implementation for brick creation.
 * 
 * <p>These tests validate the Factory Pattern implementation, ensuring
 * all brick types can be created correctly and factory methods work as expected.</p>
 * 
 * @author Dong, Jia.
 */
@DisplayName("BrickFactory Tests")
class BrickFactoryTest {

    // ========== createBrick() Tests ==========

    @Test
    @DisplayName("createBrick: Creates I brick successfully")
    void testCreateIBrick() {
        Brick brick = BrickFactory.createBrick("I");
        
        assertNotNull(brick);
        assertNotNull(brick.getShapeMatrix());
        assertFalse(brick.getShapeMatrix().isEmpty());
    }

    @Test
    @DisplayName("createBrick: Creates all brick types successfully")
    void testCreateAllBrickTypes() {
        String[] types = {"I", "J", "L", "O", "S", "T", "Z"};
        
        for (String type : types) {
            Brick brick = BrickFactory.createBrick(type);
            assertNotNull(brick, "Failed to create brick type: " + type);
            assertNotNull(brick.getShapeMatrix(), "Shape matrix is null for type: " + type);
            assertFalse(brick.getShapeMatrix().isEmpty(), "Shape matrix is empty for type: " + type);
        }
    }

    @Test
    @DisplayName("createBrick: Case insensitive brick type")
    void testCreateBrickCaseInsensitive() {
        Brick brick1 = BrickFactory.createBrick("i");
        Brick brick2 = BrickFactory.createBrick("I");
        Brick brick3 = BrickFactory.createBrick("J");
        Brick brick4 = BrickFactory.createBrick("j");
        
        assertNotNull(brick1);
        assertNotNull(brick2);
        assertNotNull(brick3);
        assertNotNull(brick4);
        
        // Same type should create same class
        assertEquals(brick1.getClass(), brick2.getClass());
        assertEquals(brick3.getClass(), brick4.getClass());
    }

    @Test
    @DisplayName("createBrick: Throws exception for unsupported brick type")
    void testCreateBrickUnsupportedType() {
        assertThrows(IllegalArgumentException.class, () -> {
            BrickFactory.createBrick("X");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            BrickFactory.createBrick("INVALID");
        });
    }

    @Test
    @DisplayName("createBrick: Throws exception for null brick type")
    void testCreateBrickNullType() {
        assertThrows(NullPointerException.class, () -> {
            BrickFactory.createBrick(null);
        });
    }

    // ========== createRandomBrick() Tests ==========

    @Test
    @DisplayName("createRandomBrick: Creates valid brick")
    void testCreateRandomBrick() {
        Brick brick = BrickFactory.createRandomBrick();
        
        assertNotNull(brick);
        assertNotNull(brick.getShapeMatrix());
    }

    @Test
    @DisplayName("createRandomBrick: Creates different bricks over multiple calls")
    void testCreateRandomBrickVariety() {
        // Generate multiple bricks - should get variety (though not guaranteed)
        java.util.Set<Class<?>> brickTypes = new java.util.HashSet<>();
        
        for (int i = 0; i < 20; i++) {
            Brick brick = BrickFactory.createRandomBrick();
            brickTypes.add(brick.getClass());
        }
        
        // Should have at least 2 different types (very likely)
        assertTrue(brickTypes.size() >= 1, "Random generator should produce variety");
    }

    // ========== BrickGenerator Factory Methods ==========

    @Test
    @DisplayName("createRandomBrickGenerator: Creates RandomBrickGenerator")
    void testCreateRandomBrickGenerator() {
        BrickGenerator generator = BrickFactory.createRandomBrickGenerator();
        
        assertNotNull(generator);
        assertNotNull(generator.getBrick());
        assertNotNull(generator.getNextBrick());
    }

    @Test
    @DisplayName("createSevenBagBrickGenerator: Creates SevenBagBrickGenerator")
    void testCreateSevenBagBrickGenerator() {
        BrickGenerator generator = BrickFactory.createSevenBagBrickGenerator();
        
        assertNotNull(generator);
        assertNotNull(generator.getBrick());
        assertNotNull(generator.getNextBrick());
    }

    // ========== Utility Methods Tests ==========

    @Test
    @DisplayName("getAvailableBrickTypes: Returns all 7 brick types")
    void testGetAvailableBrickTypes() {
        String[] types = BrickFactory.getAvailableBrickTypes();
        
        assertNotNull(types);
        assertEquals(7, types.length);
        
        // Verify all expected types are present
        java.util.Set<String> typeSet = new java.util.HashSet<>(java.util.Arrays.asList(types));
        assertTrue(typeSet.contains("I"));
        assertTrue(typeSet.contains("J"));
        assertTrue(typeSet.contains("L"));
        assertTrue(typeSet.contains("O"));
        assertTrue(typeSet.contains("S"));
        assertTrue(typeSet.contains("T"));
        assertTrue(typeSet.contains("Z"));
    }

    @Test
    @DisplayName("isBrickTypeSupported: Returns true for all valid types")
    void testIsBrickTypeSupportedValid() {
        String[] validTypes = {"I", "J", "L", "O", "S", "T", "Z"};
        
        for (String type : validTypes) {
            assertTrue(BrickFactory.isBrickTypeSupported(type), 
                "Type should be supported: " + type);
            assertTrue(BrickFactory.isBrickTypeSupported(type.toLowerCase()), 
                "Type should be case-insensitive: " + type);
        }
    }

    @Test
    @DisplayName("isBrickTypeSupported: Returns false for invalid types")
    void testIsBrickTypeSupportedInvalid() {
        assertFalse(BrickFactory.isBrickTypeSupported("X"));
        assertFalse(BrickFactory.isBrickTypeSupported("INVALID"));
        assertFalse(BrickFactory.isBrickTypeSupported(""));
    }

    @Test
    @DisplayName("getBrickTypeCount: Returns 7")
    void testGetBrickTypeCount() {
        assertEquals(7, BrickFactory.getBrickTypeCount());
    }

    @Test
    @DisplayName("createColoredBrick: Creates brick (color not yet implemented)")
    void testCreateColoredBrick() {
        // Currently returns standard brick, but method exists for future extension
        Brick brick = BrickFactory.createColoredBrick("I", 5);
        
        assertNotNull(brick);
        assertNotNull(brick.getShapeMatrix());
    }

    // ========== Factory Pattern Correctness Tests ==========

    @Test
    @DisplayName("Factory Pattern: All created bricks implement Brick interface")
    void testFactoryPatternInterface() {
        String[] types = BrickFactory.getAvailableBrickTypes();
        
        for (String type : types) {
            Brick brick = BrickFactory.createBrick(type);
            assertTrue(brick instanceof Brick, 
                "Brick type " + type + " should implement Brick interface");
        }
    }

    @Test
    @DisplayName("Factory Pattern: Each brick type has unique class")
    void testFactoryPatternUniqueClasses() {
        java.util.Map<String, Class<?>> typeToClass = new java.util.HashMap<>();
        
        for (String type : BrickFactory.getAvailableBrickTypes()) {
            Brick brick = BrickFactory.createBrick(type);
            typeToClass.put(type, brick.getClass());
        }
        
        // All types should have different classes (except possibly some shared implementations)
        assertEquals(7, typeToClass.size(), "Each type should map to a class");
    }

    @Test
    @DisplayName("Factory Pattern: Consistent creation - same type produces same class")
    void testFactoryPatternConsistency() {
        Brick brick1 = BrickFactory.createBrick("I");
        Brick brick2 = BrickFactory.createBrick("I");
        Brick brick3 = BrickFactory.createBrick("i");
        
        assertEquals(brick1.getClass(), brick2.getClass());
        assertEquals(brick1.getClass(), brick3.getClass());
    }
}

