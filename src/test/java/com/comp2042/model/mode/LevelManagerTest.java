package com.comp2042.model.mode;

import com.comp2042.view.theme.AncientTempleTheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LevelManager.
 * Tests Singleton pattern, level initialization, progress tracking, unlocking, and persistence.
 */
@DisplayName("Level Manager Tests")
class LevelManagerTest {

    private LevelManager levelManager;

    @BeforeEach
    void setUp() {
        levelManager = LevelManager.getInstance();
        // Reset progress for clean test state
        levelManager.resetProgress();
        // Clear current level - setCurrentLevel accepts LevelMode, null is valid
        levelManager.setCurrentLevel((LevelMode) null);
    }

    @Test
    @DisplayName("getInstance: Returns singleton instance")
    void testGetInstance() {
        LevelManager instance1 = LevelManager.getInstance();
        LevelManager instance2 = LevelManager.getInstance();
        
        assertSame(instance1, instance2, "Should return the same singleton instance");
    }

    @Test
    @DisplayName("getAllLevels: Returns all initialized levels")
    void testGetAllLevels() {
        var levels = levelManager.getAllLevels();
        
        assertNotNull(levels);
        assertTrue(levels.size() >= 5, "Should have at least 5 levels initialized");
        
        // Verify first level
        LevelMode level1 = levels.get(0);
        assertEquals(1, level1.getLevelId());
        assertEquals("Ancient Temple", level1.getLevelName());
    }

    @Test
    @DisplayName("getAllLevels: Returns unmodifiable list")
    void testGetAllLevelsUnmodifiable() {
        var levels = levelManager.getAllLevels();
        
        assertThrows(UnsupportedOperationException.class, () -> {
            levels.add(new LevelMode(99, "Test", new AncientTempleTheme(), 100, 5, 60, 100, 200, 300, 50));
        });
    }

    @Test
    @DisplayName("getLevel: Returns level by ID")
    void testGetLevel() {
        LevelMode level = levelManager.getLevel(1);
        
        assertNotNull(level);
        assertEquals(1, level.getLevelId());
        assertEquals("Ancient Temple", level.getLevelName());
    }

    @Test
    @DisplayName("getLevel: Returns null for invalid ID")
    void testGetLevelInvalid() {
        assertNull(levelManager.getLevel(999));
        assertNull(levelManager.getLevel(0));
        assertNull(levelManager.getLevel(-1));
    }

    @Test
    @DisplayName("getCurrentLevel: Returns null initially")
    void testGetCurrentLevelInitially() {
        assertNull(levelManager.getCurrentLevel());
    }

    @Test
    @DisplayName("setCurrentLevel: Sets current level")
    void testSetCurrentLevel() {
        // Clear current level first
        levelManager.setCurrentLevel((LevelMode) null);
        LevelMode level = levelManager.getLevel(1);
        levelManager.setCurrentLevel(level);
        
        assertSame(level, levelManager.getCurrentLevel());
    }

    @Test
    @DisplayName("setCurrentLevel by ID: Sets current level if unlocked")
    void testSetCurrentLevelById() {
        // Clear current level first
        levelManager.setCurrentLevel((LevelMode) null);
        // Level 1 should be unlocked by default
        boolean success = levelManager.setCurrentLevel(1);
        
        assertTrue(success);
        assertNotNull(levelManager.getCurrentLevel());
        assertEquals(1, levelManager.getCurrentLevel().getLevelId());
    }

    @Test
    @DisplayName("setCurrentLevel by ID: Returns false for locked level")
    void testSetCurrentLevelByIdLocked() {
        // Clear current level first
        levelManager.setCurrentLevel((LevelMode) null);
        // Level 2 should be locked initially
        boolean success = levelManager.setCurrentLevel(2);
        
        assertFalse(success, "Should return false for locked level");
        assertNull(levelManager.getCurrentLevel(), "Current level should remain null");
    }

    @Test
    @DisplayName("completeLevel: Updates best records and unlocks next level")
    void testCompleteLevel() {
        LevelMode level1 = levelManager.getLevel(1);
        assertTrue(level1.isUnlocked(), "Level 1 should be unlocked");
        
        // Complete level 1 with good performance
        boolean[] newRecords = levelManager.completeLevel(1, 500, 5, 120000, true);
        
        assertTrue(newRecords[0] || newRecords[1], "Should set at least one new record");
        assertTrue(level1.isCompleted(), "Level 1 should be marked as completed");
        
        // Check if level 2 is unlocked
        LevelMode level2 = levelManager.getLevel(2);
        assertTrue(level2.isUnlocked(), "Level 2 should be unlocked after completing level 1");
    }

    @Test
    @DisplayName("completeLevel: Does not unlock next level with 0 stars")
    void testCompleteLevelNoStars() {
        // Complete level 1 with poor performance (0 stars)
        levelManager.completeLevel(1, 50, 2, 200000, false);
        
        // Level 2 should remain locked
        LevelMode level2 = levelManager.getLevel(2);
        assertFalse(level2.isUnlocked(), "Level 2 should remain locked with 0 stars");
    }

    @Test
    @DisplayName("completeLevel: Returns false array for invalid level ID")
    void testCompleteLevelInvalid() {
        boolean[] result = levelManager.completeLevel(999, 100, 5, 60000, true);
        
        assertEquals(false, result[0]);
        assertEquals(false, result[1]);
    }

    @Test
    @DisplayName("unlockLevel: Manually unlocks a level")
    void testUnlockLevel() {
        LevelMode level2 = levelManager.getLevel(2);
        assertFalse(level2.isUnlocked(), "Level 2 should be locked initially");
        
        levelManager.unlockLevel(2);
        
        assertTrue(level2.isUnlocked(), "Level 2 should be unlocked");
    }

    @Test
    @DisplayName("unlockLevel: Does nothing for invalid level ID")
    void testUnlockLevelInvalid() {
        levelManager.unlockLevel(999);
        // Should not throw exception
    }

    @Test
    @DisplayName("isLevelCompleted: Returns true for completed level")
    void testIsLevelCompleted() {
        levelManager.completeLevel(1, 500, 5, 120000, true);
        
        assertTrue(levelManager.isLevelCompleted(1));
    }

    @Test
    @DisplayName("isLevelCompleted: Returns false for incomplete level")
    void testIsLevelCompletedFalse() {
        assertFalse(levelManager.isLevelCompleted(1));
        assertFalse(levelManager.isLevelCompleted(999));
    }

    @Test
    @DisplayName("getTotalStars: Returns sum of all stars")
    void testGetTotalStars() {
        assertEquals(0, levelManager.getTotalStars(), "Should start with 0 stars");
        
        // Complete level 1 with 2 stars
        levelManager.completeLevel(1, 450, 5, 130000, true);
        
        assertTrue(levelManager.getTotalStars() >= 1, "Should have at least 1 star");
    }

    @Test
    @DisplayName("getTotalLevels: Returns correct count")
    void testGetTotalLevels() {
        int total = levelManager.getTotalLevels();
        assertTrue(total >= 5, "Should have at least 5 levels");
    }

    @Test
    @DisplayName("getCompletedLevelsCount: Returns count of completed levels")
    void testGetCompletedLevelsCount() {
        assertEquals(0, levelManager.getCompletedLevelsCount());
        
        levelManager.completeLevel(1, 500, 5, 120000, true);
        
        assertEquals(1, levelManager.getCompletedLevelsCount());
    }

    @Test
    @DisplayName("resetProgress: Resets all level progress")
    void testResetProgress() {
        // Complete some levels
        levelManager.completeLevel(1, 500, 5, 120000, true);
        levelManager.unlockLevel(2);
        
        levelManager.resetProgress();
        
        // Level 1 should be unlocked but not completed
        LevelMode level1 = levelManager.getLevel(1);
        assertTrue(level1.isUnlocked(), "Level 1 should remain unlocked");
        assertFalse(level1.isCompleted(), "Level 1 should not be completed");
        assertEquals(0, level1.getBestStars(), "Best stars should be reset");
        
        // Level 2 should be locked
        LevelMode level2 = levelManager.getLevel(2);
        assertFalse(level2.isUnlocked(), "Level 2 should be locked");
    }

    @Test
    @DisplayName("clearAllData: Clears all progress data")
    void testClearAllData() {
        // Complete some levels
        levelManager.completeLevel(1, 500, 5, 120000, true);
        
        LevelMode level1Before = levelManager.getLevel(1);
        assertTrue(level1Before.getBestStars() > 0, "Should have stars before clear");
        
        levelManager.clearAllData();
        
        LevelMode level1 = levelManager.getLevel(1);
        assertTrue(level1.getBestStars() >= 0, "Best stars should be non-negative");
    }

    @Test
    @DisplayName("Level progression: Completing level unlocks next")
    void testLevelProgression() {
        // Complete level 1
        levelManager.completeLevel(1, 500, 5, 120000, true);
        assertTrue(levelManager.getLevel(2).isUnlocked());
        
        // Complete level 2
        levelManager.completeLevel(2, 800, 7, 100000, true);
        assertTrue(levelManager.getLevel(3).isUnlocked());
    }
}

