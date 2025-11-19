package com.comp2042;

import com.comp2042.model.brick.Brick;
import com.comp2042.model.brick.BrickFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Brick Implementations Tests")
class BrickImplementationsTest {

    @Test
    @DisplayName("All brick types: Have non-empty shape matrices")
    void testAllBricksHaveShapeMatrices() {
        String[] types = {"I", "J", "L", "O", "S", "T", "Z"};
        for (String type : types) {
            Brick brick = BrickFactory.createBrick(type);
            assertNotNull(brick.getShapeMatrix());
            assertFalse(brick.getShapeMatrix().isEmpty());
        }
    }

    @Test
    @DisplayName("IBrick: Has 2 rotation states")
    void testIBrickRotations() {
        Brick brick = BrickFactory.createBrick("I");
        assertEquals(2, brick.getShapeMatrix().size());
    }

    @Test
    @DisplayName("OBrick: Has 1 rotation state (square)")
    void testOBrickRotations() {
        Brick brick = BrickFactory.createBrick("O");
        assertEquals(1, brick.getShapeMatrix().size());
    }

    @Test
    @DisplayName("All bricks: getShapeMatrix returns defensive copy")
    void testGetShapeMatrixDefensiveCopy() {
        Brick brick = BrickFactory.createBrick("T");
        var shapes1 = brick.getShapeMatrix();
        var shapes2 = brick.getShapeMatrix();
        assertNotSame(shapes1, shapes2);
        assertEquals(shapes1.size(), shapes2.size());
    }
}

