package com.comp2042;

import com.comp2042.model.brick.Brick;
import com.comp2042.model.brick.BrickFactory;
import com.comp2042.model.board.BrickRotator;
import com.comp2042.model.board.NextShapeInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BrickRotator Tests")
class BrickRotatorTest {

    private BrickRotator rotator;
    private Brick iBrick;

    @BeforeEach
    void setUp() {
        rotator = new BrickRotator();
        iBrick = BrickFactory.createBrick("I");
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
        
        assertNotEquals(initialShape, nextInfo.getShape());
        assertArrayEquals(initialShape, rotator.getCurrentShape());
        assertEquals(1, nextInfo.getPosition());
    }

    @Test
    @DisplayName("setCurrentShape: Updates current shape")
    void testSetCurrentShape() {
        int[][] shape1 = iBrick.getShapeMatrix().get(1);
        rotator.setCurrentShape(1);
        assertArrayEquals(shape1, rotator.getCurrentShape());
    }

    @Test
    @DisplayName("setBrick: Resets to first shape and updates brick")
    void testSetBrick() {
        rotator.setCurrentShape(1);
        Brick newBrick = BrickFactory.createBrick("I");
        rotator.setBrick(newBrick);
        
        assertEquals(0, getCurrentShapeIndex());
        assertEquals(newBrick, rotator.getBrick());
    }

    private int getCurrentShapeIndex() {
        int[][] currentShape = rotator.getCurrentShape();
        var shapes = rotator.getBrick().getShapeMatrix();
        for (int i = 0; i < shapes.size(); i++) {
            if (java.util.Arrays.deepEquals(currentShape, shapes.get(i))) {
                return i;
            }
        }
        return -1;
    }
}

