package com.comp2042.model.board;

import com.comp2042.model.brick.Brick;
import com.comp2042.model.brick.BrickFactory;
import com.comp2042.model.board.BrickRotator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HoldManagerTest {

    private HoldManager holdManager;
    private BrickRotator rotator;
    private boolean spawnCalled;
    private boolean resetCalled;

    @BeforeEach
    void setUp() {
        holdManager = new HoldManager();
        rotator = new BrickRotator();
        spawnCalled = false;
        resetCalled = false;
    }

    private void spawnNewBrick() {
        spawnCalled = true;
    }

    private void resetPosition() {
        resetCalled = true;
    }

    @Test
    void testHoldBrickInitially() {
        Brick brick = BrickFactory.createBrick("I");
        rotator.setBrick(brick);

        boolean result = holdManager.holdBrick(rotator, this::spawnNewBrick, this::resetPosition);

        assertTrue(result, "Should successfully hold brick");
        assertEquals(brick, holdManager.getHeldBrick(), "Held brick should match");
        assertTrue(spawnCalled, "Should spawn new brick on first hold");
    }

    @Test
    void testSwapHeldBrick() {
        Brick firstBrick = BrickFactory.createBrick("I");
        rotator.setBrick(firstBrick);
        holdManager.holdBrick(rotator, this::spawnNewBrick, this::resetPosition);
        
        holdManager.enableHold();
        spawnCalled = false;
        
        Brick secondBrick = BrickFactory.createBrick("O");
        rotator.setBrick(secondBrick);

        boolean result = holdManager.holdBrick(rotator, this::spawnNewBrick, this::resetPosition);

        assertTrue(result, "Should successfully swap brick");
        assertEquals(secondBrick, holdManager.getHeldBrick(), "Held brick should be the new one");
        assertEquals(firstBrick, rotator.getBrick(), "Rotator should now have the previously held brick");
    }
    
    @Test
    void testEnableHold() {
        Brick brick = BrickFactory.createBrick("I");
        rotator.setBrick(brick);

        holdManager.holdBrick(rotator, this::spawnNewBrick, this::resetPosition);
        assertFalse(holdManager.canHold());

        holdManager.enableHold();
        assertTrue(holdManager.canHold(), "Should be able to hold after enableHold");
    }
}
