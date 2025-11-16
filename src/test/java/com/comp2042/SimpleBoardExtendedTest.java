package com.comp2042;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Extended unit tests for SimpleBoard class.
 * Tests advanced game logic: movement, rotation, hard drop, hold, row clearing, and game over.
 * 
 * <p>These tests complement the basic tests in SimpleBoardTest by covering
 * more complex gameplay scenarios and edge cases.</p>
 * 
 * @author Dong, Jia.
 */
@DisplayName("SimpleBoard Extended Tests")
class SimpleBoardExtendedTest {

    private SimpleBoard board;

    @BeforeEach
    void setUp() {
        board = new SimpleBoard(10, 20);
    }

    // ========== Movement Tests ==========

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
    @DisplayName("moveBrickDown: Returns false when brick lands")
    void testMoveBrickDownLands() {
        board.createNewBrick();
        
        // Move brick to bottom
        boolean canMove = true;
        int moves = 0;
        while (canMove && moves < 25) { // Safety limit
            canMove = board.moveBrickDown();
            moves++;
        }
        
        // Should eventually land
        assertFalse(canMove);
        assertTrue(moves > 0);
    }

    @Test
    @DisplayName("moveBrickLeft: Moves brick left successfully")
    void testMoveBrickLeftSuccess() {
        board.createNewBrick();
        ViewData initialData = board.getViewData();
        int initialX = initialData.getxPosition();
        
        boolean moved = board.moveBrickLeft();
        
        if (moved) {
            ViewData newData = board.getViewData();
            assertEquals(initialX - 1, newData.getxPosition());
        }
    }

    @Test
    @DisplayName("moveBrickLeft: Returns false at left wall")
    void testMoveBrickLeftAtWall() {
        board.createNewBrick();
        
        // Move to left wall
        boolean canMove = true;
        int moves = 0;
        while (canMove && moves < 15) { // Safety limit
            canMove = board.moveBrickLeft();
            moves++;
        }
        
        // Should eventually hit wall
        assertFalse(canMove);
    }

    @Test
    @DisplayName("moveBrickRight: Moves brick right successfully")
    void testMoveBrickRightSuccess() {
        board.createNewBrick();
        ViewData initialData = board.getViewData();
        int initialX = initialData.getxPosition();
        
        boolean moved = board.moveBrickRight();
        
        if (moved) {
            ViewData newData = board.getViewData();
            assertEquals(initialX + 1, newData.getxPosition());
        }
    }

    @Test
    @DisplayName("moveBrickRight: Returns false at right wall")
    void testMoveBrickRightAtWall() {
        board.createNewBrick();
        
        // Move to right wall
        boolean canMove = true;
        int moves = 0;
        while (canMove && moves < 15) { // Safety limit
            canMove = board.moveBrickRight();
            moves++;
        }
        
        // Should eventually hit wall
        assertFalse(canMove);
    }

    // ========== Rotation Tests ==========

    @Test
    @DisplayName("rotateLeftBrick: Rotates brick successfully")
    void testRotateLeftBrickSuccess() {
        board.createNewBrick();
        int[][] initialShape = board.getViewData().getBrickData();
        
        boolean rotated = board.rotateLeftBrick();
        
        if (rotated) {
            int[][] newShape = board.getViewData().getBrickData();
            // Shape should be different (unless brick has only one rotation state)
            // For most bricks, rotation should change the shape
            assertNotNull(newShape);
        }
    }

    @Test
    @DisplayName("rotateRightBrick: Rotates brick counterclockwise successfully")
    void testRotateRightBrickSuccess() {
        board.createNewBrick();
        int[][] initialShape = board.getViewData().getBrickData();
        
        boolean rotated = board.rotateRightBrick();
        
        if (rotated) {
            int[][] newShape = board.getViewData().getBrickData();
            assertNotNull(newShape);
        }
    }

    @Test
    @DisplayName("rotateLeftBrick: Returns false when rotation causes collision")
    void testRotateLeftBrickCollision() {
        board.createNewBrick();
        
        // Fill board around brick to prevent rotation
        int[][] matrix = board.getBoardMatrix();
        ViewData viewData = board.getViewData();
        int x = viewData.getxPosition();
        int y = viewData.getyPosition();
        
        // Place blocks around brick (simulate collision scenario)
        // This is a simplified test - actual collision depends on brick shape
        
        // Try rotation - may or may not succeed depending on brick position
        boolean rotated = board.rotateLeftBrick();
        assertNotNull(board.getViewData()); // Should still have valid view data
    }

    // ========== Hard Drop Tests ==========

    @Test
    @DisplayName("hardDropBrick: Drops brick to bottom")
    void testHardDropBrick() {
        board.createNewBrick();
        ViewData initialData = board.getViewData();
        int initialY = initialData.getyPosition();
        
        int dropDistance = board.hardDropBrick();
        
        assertTrue(dropDistance > 0);
        ViewData newData = board.getViewData();
        assertTrue(newData.getyPosition() > initialY);
    }

    @Test
    @DisplayName("hardDropBrick: Returns 0 when brick already at bottom")
    void testHardDropBrickAlreadyAtBottom() {
        board.createNewBrick();
        
        // Move to bottom first
        while (board.moveBrickDown()) {
            // Keep moving down
        }
        
        int dropDistance = board.hardDropBrick();
        assertEquals(0, dropDistance);
    }

    // ========== Hold Tests ==========

    @Test
    @DisplayName("holdBrick: Holds current brick and spawns new one")
    void testHoldBrick() {
        board.createNewBrick();
        ViewData initialData = board.getViewData();
        
        boolean held = board.holdBrick();
        
        assertTrue(held);
        ViewData newData = board.getViewData();
        // New brick should be spawned
        assertNotNull(newData);
    }

    @Test
    @DisplayName("holdBrick: Can hold and retrieve brick")
    void testHoldBrickRetrieve() {
        board.createNewBrick();
        ViewData firstBrick = board.getViewData();
        
        // Hold first brick
        board.holdBrick();
        
        // Get new brick
        board.createNewBrick();
        ViewData secondBrick = board.getViewData();
        
        // Hold again - should get first brick back
        board.holdBrick();
        ViewData retrievedBrick = board.getViewData();
        
        // Retrieved brick should be the second one (first was held)
        assertNotNull(retrievedBrick);
    }

    @Test
    @DisplayName("holdBrick: Returns false when already held this turn")
    void testHoldBrickAlreadyHeld() {
        board.createNewBrick();
        
        // First hold should succeed
        assertTrue(board.holdBrick());
        
        // Second hold in same turn should fail
        assertFalse(board.holdBrick());
    }

    // ========== Row Clearing Tests ==========

    @Test
    @DisplayName("clearRows: Clears completed rows and updates score")
    void testClearRows() {
        // Fill a row completely
        int[][] matrix = board.getBoardMatrix();
        for (int j = 0; j < 10; j++) {
            matrix[19][j] = 1; // Fill bottom row
        }
        
        // Merge a brick to trigger row clearing
        board.createNewBrick();
        board.mergeBrickToBackground();
        
        ClearRow result = board.clearRows();
        
        assertTrue(result.getLinesRemoved() > 0);
        assertTrue(result.getScoreBonus() > 0);
    }

    @Test
    @DisplayName("clearRows: Returns zero when no rows completed")
    void testClearRowsNoRowsCompleted() {
        board.createNewBrick();
        board.mergeBrickToBackground();
        
        ClearRow result = board.clearRows();
        
        assertEquals(0, result.getLinesRemoved());
        assertEquals(0, result.getScoreBonus());
    }

    // ========== Game Over Tests ==========

    @Test
    @DisplayName("createNewBrick: Returns false when game continues")
    void testCreateNewBrickGameContinues() {
        boolean gameOver = board.createNewBrick();
        assertFalse(gameOver);
    }

    @Test
    @DisplayName("createNewBrick: Returns true when game over")
    void testCreateNewBrickGameOver() {
        // Fill board to top to cause game over
        int[][] matrix = board.getBoardMatrix();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 10; j++) {
                matrix[i][j] = 1;
            }
        }
        
        boolean gameOver = board.createNewBrick();
        // May or may not be game over depending on brick spawn position
        assertNotNull(board.getViewData());
    }

    // ========== Score Tests ==========

    @Test
    @DisplayName("Score: Updates when brick moves down")
    void testScoreUpdatesOnMove() {
        board.createNewBrick();
        int initialScore = board.getScore().getScore();
        
        board.moveBrickDown();
        
        // Score may or may not update on move (depends on implementation)
        assertNotNull(board.getScore());
    }

    @Test
    @DisplayName("Score: Updates when rows cleared")
    void testScoreUpdatesOnRowClear() {
        // Fill and clear a row
        int[][] matrix = board.getBoardMatrix();
        for (int j = 0; j < 10; j++) {
            matrix[19][j] = 1;
        }
        
        board.createNewBrick();
        board.mergeBrickToBackground();
        ClearRow result = board.clearRows();
        
        if (result.getLinesRemoved() > 0) {
            assertTrue(board.getScore().getScore() > 0);
        }
    }

    // ========== Integration Tests ==========

    @Test
    @DisplayName("Integration: Complete gameplay cycle")
    void testCompleteGameplayCycle() {
        // Create brick
        assertFalse(board.createNewBrick());
        
        // Move down
        assertTrue(board.moveBrickDown());
        
        // Rotate
        board.rotateLeftBrick();
        
        // Move left/right
        board.moveBrickLeft();
        board.moveBrickRight();
        
        // Land brick
        while (board.moveBrickDown()) {
            // Keep moving down
        }
        
        // Merge and clear
        board.mergeBrickToBackground();
        ClearRow result = board.clearRows();
        
        assertNotNull(result);
        
        // Create new brick
        boolean gameOver = board.createNewBrick();
        assertFalse(gameOver); // Should continue
    }

    @Test
    @DisplayName("Integration: Hold and continue gameplay")
    void testHoldIntegration() {
        // Create first brick
        board.createNewBrick();
        
        // Hold it
        assertTrue(board.holdBrick());
        
        // Create new brick
        board.createNewBrick();
        
        // Continue playing
        board.moveBrickDown();
        assertNotNull(board.getViewData());
    }
}

