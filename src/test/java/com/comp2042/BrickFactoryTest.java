package com.comp2042;

import com.comp2042.model.brick.Brick;
import com.comp2042.model.brick.BrickFactory;
import com.comp2042.model.brick.BrickGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BrickFactory Tests")
class BrickFactoryTest {

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
        
        assertEquals(brick1.getClass(), brick2.getClass());
        assertEquals(brick3.getClass(), brick4.getClass());
    }

    @Test
    @DisplayName("createBrick: Throws exception for unsupported brick type")
    void testCreateBrickUnsupportedType() {
        assertThrows(IllegalArgumentException.class, () -> BrickFactory.createBrick("X"));
        assertThrows(IllegalArgumentException.class, () -> BrickFactory.createBrick("INVALID"));
    }

    @Test
    @DisplayName("createBrick: Throws exception for null brick type")
    void testCreateBrickNullType() {
        assertThrows(NullPointerException.class, () -> BrickFactory.createBrick(null));
    }

    @Test
    @DisplayName("createRandomBrick: Creates valid brick")
    void testCreateRandomBrick() {
        Brick brick = BrickFactory.createRandomBrick();
        assertNotNull(brick);
        assertNotNull(brick.getShapeMatrix());
    }

    @Test
    @DisplayName("createRandomBrickGenerator: Creates RandomBrickGenerator")
    void testCreateRandomBrickGenerator() {
        BrickGenerator generator = BrickFactory.createRandomBrickGenerator();
        assertNotNull(generator);
    }

    @Test
    @DisplayName("createSevenBagBrickGenerator: Creates SevenBagBrickGenerator")
    void testCreateSevenBagBrickGenerator() {
        BrickGenerator generator = BrickFactory.createSevenBagBrickGenerator();
        assertNotNull(generator);
    }

    @Test
    @DisplayName("getAvailableBrickTypes: Returns all 7 brick types")
    void testGetAvailableBrickTypes() {
        String[] types = BrickFactory.getAvailableBrickTypes();
        assertNotNull(types);
        assertEquals(7, types.length);
    }

    @Test
    @DisplayName("isBrickTypeSupported: Returns true for all valid types")
    void testIsBrickTypeSupportedValid() {
        String[] validTypes = {"I", "J", "L", "O", "S", "T", "Z"};
        for (String type : validTypes) {
            assertTrue(BrickFactory.isBrickTypeSupported(type));
            assertTrue(BrickFactory.isBrickTypeSupported(type.toLowerCase()));
        }
    }
}