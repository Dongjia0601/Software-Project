package com.comp2042.model.board;

import com.comp2042.model.brick.Brick;
import com.comp2042.model.brick.BrickFactory;
import com.comp2042.model.board.BrickRotator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class GarbageManagerTest {

    private GarbageManager garbageManager;
    private int[][] matrix;
    private final int WIDTH = 10;
    private final int HEIGHT = 20;

    @BeforeEach
    void setUp() {
        garbageManager = new GarbageManager(WIDTH, HEIGHT);
        matrix = new int[HEIGHT][WIDTH];
    }

    @Test
    void testAddGarbageLine() {
        BrickRotator rotator = new BrickRotator();
        rotator.setBrick(BrickFactory.createBrick("I"));
        Point offset = new Point(4, 0);

        matrix[HEIGHT - 1][0] = 1;

        boolean gameOver = garbageManager.addGarbageLine(matrix, rotator, offset);

        assertFalse(gameOver, "Should not be game over unless collision");
        assertEquals(1, matrix[HEIGHT - 2][0]);
        
        int[] bottomRow = matrix[HEIGHT - 1];
        boolean hasHole = false;
        boolean hasGarbage = false;
        for (int cell : bottomRow) {
            if (cell == 0) hasHole = true;
            if (cell == 8) hasGarbage = true;
        }
        assertTrue(hasHole, "Garbage line must have at least one hole");
        assertTrue(hasGarbage, "Garbage line must have garbage blocks");
    }

    @Test
    void testAddGarbageLineGameOver() {
        BrickRotator rotator = new BrickRotator();
        rotator.setBrick(BrickFactory.createBrick("I"));
        
        // Position brick at the very bottom row where garbage will be added
        Point offset = new Point(0, HEIGHT - 1);
        boolean collision = garbageManager.addGarbageLine(matrix, rotator, offset);
        
        assertTrue(collision, "Should detect collision when garbage pushes into brick");
    }

    @Test
    void testRemoveGarbageLines() {
        for (int i = 0; i < WIDTH; i++) {
            matrix[HEIGHT - 1][i] = (i == 0) ? 0 : 8;
        }
        matrix[HEIGHT - 2][0] = 1;

        int removed = garbageManager.removeGarbageLines(matrix, 1);

        assertEquals(1, removed, "Should remove 1 line");
        assertEquals(1, matrix[HEIGHT - 1][0], "Block should have shifted down");
    }
}
