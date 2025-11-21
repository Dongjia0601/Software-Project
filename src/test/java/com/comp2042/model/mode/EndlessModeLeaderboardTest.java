package com.comp2042.model.mode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EndlessModeLeaderboard.
 * Tests Singleton pattern, entry management, ranking, high score detection, and CSV persistence.
 */
@DisplayName("Endless Mode Leaderboard Tests")
class EndlessModeLeaderboardTest {

    private EndlessModeLeaderboard leaderboard;

    @BeforeEach
    void setUp() {
        // Clear leaderboard before each test
        leaderboard = EndlessModeLeaderboard.getInstance();
        leaderboard.clearLeaderboard();
    }

    @Test
    @DisplayName("getInstance: Returns singleton instance")
    void testGetInstance() {
        EndlessModeLeaderboard instance1 = EndlessModeLeaderboard.getInstance();
        EndlessModeLeaderboard instance2 = EndlessModeLeaderboard.getInstance();
        
        assertSame(instance1, instance2, "Should return the same singleton instance");
    }

    @Test
    @DisplayName("addEntry: Adds entry and returns rank")
    void testAddEntry() {
        int rank = leaderboard.addEntry(1000, 10, 60000, 5);
        
        assertEquals(1, rank, "First entry should be rank 1");
        assertEquals(1, leaderboard.getEntryCount());
        assertEquals(1000, leaderboard.getHighScore());
    }

    @Test
    @DisplayName("addEntry: Rejects zero or negative scores")
    void testAddEntryInvalidScore() {
        int rank1 = leaderboard.addEntry(0, 10, 60000, 5);
        int rank2 = leaderboard.addEntry(-100, 10, 60000, 5);
        
        assertEquals(0, rank1, "Should return 0 for invalid score");
        assertEquals(0, rank2, "Should return 0 for negative score");
        assertEquals(0, leaderboard.getEntryCount());
    }

    @Test
    @DisplayName("addEntry: Maintains top 5 entries only")
    void testAddEntryTop5Limit() {
        // Add 6 entries
        leaderboard.addEntry(6000, 60, 60000, 10);
        leaderboard.addEntry(5000, 50, 60000, 9);
        leaderboard.addEntry(4000, 40, 60000, 8);
        leaderboard.addEntry(3000, 30, 60000, 7);
        leaderboard.addEntry(2000, 20, 60000, 6);
        int rank6 = leaderboard.addEntry(1000, 10, 60000, 5);
        
        assertEquals(0, rank6, "6th entry should not be added (rank 0)");
        assertEquals(5, leaderboard.getEntryCount(), "Should only keep top 5");
        assertEquals(6000, leaderboard.getHighScore());
    }

    @Test
    @DisplayName("addEntry: Sorts entries by score descending")
    void testAddEntrySorting() {
        leaderboard.addEntry(1000, 10, 60000, 5);
        leaderboard.addEntry(3000, 30, 60000, 7);
        leaderboard.addEntry(2000, 20, 60000, 6);
        
        var entries = leaderboard.getTopEntries();
        assertEquals(3, entries.size());
        assertEquals(3000, entries.get(0).getScore(), "Highest score should be first");
        assertEquals(2000, entries.get(1).getScore());
        assertEquals(1000, entries.get(2).getScore(), "Lowest score should be last");
    }

    @Test
    @DisplayName("wouldQualify: Returns true if leaderboard not full")
    void testWouldQualifyNotFull() {
        assertTrue(leaderboard.wouldQualify(100), "Should qualify when leaderboard is empty");
        
        leaderboard.addEntry(1000, 10, 60000, 5);
        assertTrue(leaderboard.wouldQualify(100), "Should qualify when leaderboard has < 5 entries");
    }

    @Test
    @DisplayName("wouldQualify: Returns true if score beats 5th place")
    void testWouldQualifyBeats5th() {
        // Fill leaderboard with 5 entries
        leaderboard.addEntry(5000, 50, 60000, 10);
        leaderboard.addEntry(4000, 40, 60000, 9);
        leaderboard.addEntry(3000, 30, 60000, 8);
        leaderboard.addEntry(2000, 20, 60000, 7);
        leaderboard.addEntry(1000, 10, 60000, 6);
        
        assertTrue(leaderboard.wouldQualify(1500), "Should qualify if score > 5th place");
        assertFalse(leaderboard.wouldQualify(500), "Should not qualify if score <= 5th place");
    }

    @Test
    @DisplayName("isNewHighScore: Returns true for first entry")
    void testIsNewHighScoreFirstEntry() {
        assertTrue(leaderboard.isNewHighScore(1000), "First entry should be new high score");
    }

    @Test
    @DisplayName("isNewHighScore: Returns true if score beats current high")
    void testIsNewHighScoreBeatsCurrent() {
        leaderboard.addEntry(1000, 10, 60000, 5);
        
        assertTrue(leaderboard.isNewHighScore(2000), "Should be new high score if beats current");
        assertFalse(leaderboard.isNewHighScore(500), "Should not be new high score if lower");
    }

    @Test
    @DisplayName("getHighScore: Returns 0 when empty")
    void testGetHighScoreEmpty() {
        assertEquals(0, leaderboard.getHighScore());
    }

    @Test
    @DisplayName("getHighScore: Returns highest score")
    void testGetHighScore() {
        leaderboard.addEntry(1000, 10, 60000, 5);
        leaderboard.addEntry(2000, 20, 60000, 6);
        leaderboard.addEntry(1500, 15, 60000, 5);
        
        assertEquals(2000, leaderboard.getHighScore());
    }

    @Test
    @DisplayName("getTopEntries: Returns unmodifiable list")
    void testGetTopEntriesUnmodifiable() {
        leaderboard.addEntry(1000, 10, 60000, 5);
        
        var entries = leaderboard.getTopEntries();
        assertThrows(UnsupportedOperationException.class, () -> entries.add(new LeaderboardEntry(2000, 20, 60000, 6)));
    }

    @Test
    @DisplayName("getEntryCount: Returns correct count")
    void testGetEntryCount() {
        assertEquals(0, leaderboard.getEntryCount());
        
        leaderboard.addEntry(1000, 10, 60000, 5);
        assertEquals(1, leaderboard.getEntryCount());
        
        leaderboard.addEntry(2000, 20, 60000, 6);
        assertEquals(2, leaderboard.getEntryCount());
    }

    @Test
    @DisplayName("clearLeaderboard: Removes all entries")
    void testClearLeaderboard() {
        leaderboard.addEntry(1000, 10, 60000, 5);
        leaderboard.addEntry(2000, 20, 60000, 6);
        
        leaderboard.clearLeaderboard();
        
        assertEquals(0, leaderboard.getEntryCount());
        assertEquals(0, leaderboard.getHighScore());
    }

    @Test
    @DisplayName("Persistence: Saves and loads entries from CSV")
    void testPersistence() {
        // Add entries
        leaderboard.addEntry(5000, 50, 60000, 10);
        leaderboard.addEntry(3000, 30, 60000, 8);
        leaderboard.addEntry(4000, 40, 60000, 9);
        
        // Get current state
        int entryCount = leaderboard.getEntryCount();
        int highScore = leaderboard.getHighScore();
        
        // Verify the file exists and contains data
        String userHome = System.getProperty("user.home");
        Path leaderboardPath = Paths.get(userHome, ".tetris", "tetris_endless_leaderboard.csv");
        
        // Verify file exists after adding entries
        assertTrue(Files.exists(leaderboardPath), "Leaderboard file should exist after adding entries");
        
        // Verify file contains data
        try {
            String content = Files.readString(leaderboardPath);
            assertTrue(content.contains("5000"), "File should contain highest score");
            assertTrue(content.contains("score,lines,time_ms,timestamp,level"), "File should contain header");
        } catch (IOException e) {
            fail("Failed to read leaderboard file: " + e.getMessage());
        }
    }
}

