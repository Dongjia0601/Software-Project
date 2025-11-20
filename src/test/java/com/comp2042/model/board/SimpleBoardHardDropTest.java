package com.comp2042.model.board;

import com.comp2042.model.brick.BrickFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleBoardHardDropTest {

    private SimpleBoard board;
    private final int WIDTH = 10;
    private final int HEIGHT = 20;

    @BeforeEach
    void setUp() {
        board = new SimpleBoard(WIDTH, HEIGHT);
        board.createNewBrick();
    }

    @Test
    void testHardDropEmptyBoard() {
        int dropped = board.hardDropBrick();
        assertTrue(dropped > 0, "Should drop some distance");
        boolean moved = board.moveBrickDown();
        assertFalse(moved, "Should be at bottom after hard drop");
    }

    @Test
    void testGhostBrickY() {
        int ghostY = board.getGhostBrickY();
        assertTrue(ghostY > 0, "Ghost Y should be positive");
        
        int dropped = board.hardDropBrick();
        int finalY = board.getViewData().getYPosition();
        
        assertEquals(ghostY, finalY, "Ghost brick Y should match hard drop destination");
    }
}
