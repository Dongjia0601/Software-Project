package com.comp2042.model.mode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PlayerStats.
 * Tests statistics tracking for two-player VS mode including combos, attacks, and gameplay metrics.
 */
@DisplayName("Player Statistics Tests")
class PlayerStatsTest {

    private PlayerStats stats;

    @BeforeEach
    void setUp() {
        stats = new PlayerStats();
    }

    @Test
    @DisplayName("Constructor: Initializes with zero values")
    void testConstructor() {
        assertEquals(0, stats.getLinesCleared());
        assertEquals(0, stats.getAttacksSent());
        assertEquals(0, stats.getAttacksReceived());
        assertEquals(0, stats.getMaxCombo());
        assertEquals(0, stats.getCurrentCombo());
        assertEquals(0, stats.getTetrisCount());
        assertEquals(0, stats.getHardDrops());
        assertEquals(0, stats.getSoftDrops());
    }

    @Test
    @DisplayName("reset: Resets all statistics to zero")
    void testReset() {
        stats.recordLineClear(2, 1);
        stats.recordAttackReceived(3);
        stats.recordHardDrop();
        stats.recordSoftDrop();
        
        stats.reset();
        
        assertEquals(0, stats.getLinesCleared());
        assertEquals(0, stats.getAttacksSent());
        assertEquals(0, stats.getAttacksReceived());
        assertEquals(0, stats.getMaxCombo());
        assertEquals(0, stats.getCurrentCombo());
    }

    @Test
    @DisplayName("recordLineClear: Updates lines cleared and attacks sent")
    void testRecordLineClear() {
        stats.recordLineClear(2, 1);
        assertEquals(2, stats.getLinesCleared());
        assertEquals(1, stats.getAttacksSent());
        
        stats.recordLineClear(4, 4);
        assertEquals(6, stats.getLinesCleared());
        assertEquals(5, stats.getAttacksSent());
    }

    @Test
    @DisplayName("recordLineClear: Updates combo counter")
    void testRecordLineClearUpdatesCombo() {
        stats.recordLineClear(1, 0);
        assertEquals(1, stats.getCurrentCombo());
        assertEquals(1, stats.getMaxCombo());
        
        stats.recordLineClear(2, 1);
        assertEquals(2, stats.getCurrentCombo());
        assertEquals(2, stats.getMaxCombo());
        
        stats.recordLineClear(3, 2);
        assertEquals(3, stats.getCurrentCombo());
        assertEquals(3, stats.getMaxCombo());
    }

    @Test
    @DisplayName("recordLineClear: Tracks Tetris (4-line clears)")
    void testRecordLineClearTracksTetris() {
        stats.recordLineClear(4, 4);
        assertEquals(1, stats.getTetrisCount());
        
        stats.recordLineClear(4, 4);
        assertEquals(2, stats.getTetrisCount());
        
        stats.recordLineClear(3, 2);
        assertEquals(2, stats.getTetrisCount(), "Non-Tetris should not increment count");
    }

    @Test
    @DisplayName("recordLineClear: Zero lines resets combo")
    void testRecordLineClearZeroLinesResetsCombo() {
        stats.recordLineClear(2, 1);
        stats.recordLineClear(1, 0);
        assertEquals(2, stats.getCurrentCombo());
        
        stats.recordLineClear(0, 0);
        assertEquals(0, stats.getCurrentCombo(), "Zero lines should reset combo");
    }

    @Test
    @DisplayName("recordAttackReceived: Updates attacks received counter")
    void testRecordAttackReceived() {
        stats.recordAttackReceived(1);
        assertEquals(1, stats.getAttacksReceived());
        
        stats.recordAttackReceived(2);
        assertEquals(3, stats.getAttacksReceived());
        
        stats.recordAttackReceived(4);
        assertEquals(7, stats.getAttacksReceived());
    }

    @Test
    @DisplayName("recordHardDrop: Increments hard drop counter")
    void testRecordHardDrop() {
        stats.recordHardDrop();
        assertEquals(1, stats.getHardDrops());
        
        stats.recordHardDrop();
        stats.recordHardDrop();
        assertEquals(3, stats.getHardDrops());
    }

    @Test
    @DisplayName("recordSoftDrop: Increments soft drop counter")
    void testRecordSoftDrop() {
        stats.recordSoftDrop();
        assertEquals(1, stats.getSoftDrops());
        
        stats.recordSoftDrop();
        stats.recordSoftDrop();
        assertEquals(3, stats.getSoftDrops());
    }

    @Test
    @DisplayName("recordAllClear: Increments all clear counter")
    void testRecordAllClear() {
        stats.recordAllClear();
        assertEquals(1, stats.getAllClears());
        
        stats.recordAllClear();
        assertEquals(2, stats.getAllClears());
    }

    @Test
    @DisplayName("resetCombo: Resets current combo to zero")
    void testResetCombo() {
        stats.recordLineClear(1, 0);
        stats.recordLineClear(2, 1);
        assertEquals(2, stats.getCurrentCombo());
        
        stats.resetCombo();
        assertEquals(0, stats.getCurrentCombo());
        assertEquals(2, stats.getMaxCombo(), "Max combo should not be reset");
    }

    @Test
    @DisplayName("startGameTime: Sets game start time")
    void testStartGameTime() {
        long before = System.currentTimeMillis();
        stats.startGameTime();
        long after = System.currentTimeMillis();
        
        long gameTime = stats.getGameTimeSeconds();
        assertTrue(gameTime >= 0 && gameTime <= 1, "Game time should be near 0 after start");
    }

    @Test
    @DisplayName("getGameTimeSeconds: Returns 0 if game not started")
    void testGetGameTimeSecondsNotStarted() {
        assertEquals(0, stats.getGameTimeSeconds());
    }

    @Test
    @DisplayName("getGameTimeSeconds: Calculates elapsed time correctly")
    void testGetGameTimeSeconds() throws InterruptedException {
        stats.startGameTime();
        Thread.sleep(1100); // Sleep for 1.1 seconds
        
        long gameTime = stats.getGameTimeSeconds();
        assertTrue(gameTime >= 1 && gameTime <= 2, "Game time should be approximately 1 second");
    }

    @Test
    @DisplayName("getLPM: Returns 0 if game time is 0")
    void testGetLPMMZeroTime() {
        assertEquals(0.0, stats.getLPM());
    }

    @Test
    @DisplayName("getLPM: Calculates lines per minute correctly")
    void testGetLPM() throws InterruptedException {
        stats.startGameTime();
        stats.recordLineClear(10, 5);
        Thread.sleep(1000); // 1 second
        
        double lpm = stats.getLPM();
        // Should be approximately 600 LPM (10 lines in 1 second = 600 per minute)
        assertTrue(lpm >= 500 && lpm <= 700, "LPM should be approximately 600");
    }

    @Test
    @DisplayName("getFormattedTime: Returns formatted time string")
    void testGetFormattedTime() throws InterruptedException {
        stats.startGameTime();
        Thread.sleep(2100); // 2.1 seconds (enough to test formatting, but much faster)
        
        String formatted = stats.getFormattedTime();
        // Should match MM:SS format (will be 00:02 for 2 seconds)
        assertTrue(formatted.matches("\\d{2}:\\d{2}"), "Should match MM:SS format");
        assertNotNull(formatted);
        assertEquals(5, formatted.length(), "Formatted time should be 5 characters (MM:SS)");
    }

    @Test
    @DisplayName("getFormattedTime: Returns 00:00 if game not started")
    void testGetFormattedTimeNotStarted() {
        String formatted = stats.getFormattedTime();
        assertEquals("00:00", formatted);
    }

    @Test
    @DisplayName("Max combo persists even after resetCombo")
    void testMaxComboPersistence() {
        stats.recordLineClear(1, 0);
        stats.recordLineClear(2, 1);
        stats.recordLineClear(3, 2);
        assertEquals(3, stats.getMaxCombo());
        
        stats.resetCombo();
        assertEquals(3, stats.getMaxCombo(), "Max combo should persist after reset");
        
        stats.recordLineClear(1, 0);
        assertEquals(1, stats.getCurrentCombo());
        assertEquals(3, stats.getMaxCombo(), "Max combo should not decrease");
    }
}

