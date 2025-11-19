package com.comp2042;

import com.comp2042.model.board.SimpleBoard;
import com.comp2042.dto.ViewData;
import com.comp2042.dto.ClearRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SimpleBoard Extended Tests")
class SimpleBoardExtendedTest {

    private SimpleBoard board;

    @BeforeEach
    void setUp() {
        board = new SimpleBoard(10, 20);
    }

    @Test
    @DisplayName("moveBrickDown: Moves brick down successfully")
    void testMoveBrickDownSuccess() {
        board.createNewBrick();
        ViewData initialData = board.getViewData();
        int initialY = initialData.getyPosition();
        
        boolean moved = board.moveBrickDown();
        
        assertTrue(moved);
        ViewData newData = board.getViewData();
        assertEquals(initialY + 1, newData.getyPosition());
    }

    @Test
    @DisplayName("rotateLeftBrick: Rotates brick successfully")
    void testRotateLeftBrickSuccess() {
        board.createNewBrick();
        boolean rotated = board.rotateLeftBrick();
        if (rotated) {
            assertNotNull(board.getViewData().getBrickData());
        }
    }

    @Test
    @DisplayName("hardDropBrick: Drops brick to bottom")
    void testHardDropBrick() {
        board.createNewBrick();
        int dropDistance = board.hardDropBrick();
        assertTrue(dropDistance > 0);
    }

    @Test
    @DisplayName("holdBrick: Holds current brick and spawns new one")
    void testHoldBrick() {
        board.createNewBrick();
        boolean held = board.holdBrick();
        assertTrue(held);
        assertNotNull(board.getViewData());
    }

    @Test
    @DisplayName("clearRows: Clears completed rows and updates score")
    void testClearRows() {
        int[][] matrix = board.getBoardMatrix();
        for (int j = 0; j < 10; j++) {
            matrix[19][j] = 1;
        }
        
        board.createNewBrick();
        board.mergeBrickToBackground();
        ClearRow result = board.clearRows();
        
        assertTrue(result.getLinesRemoved() > 0);
        assertTrue(result.getScoreBonus() > 0);
    }
}

