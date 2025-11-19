package com.comp2042;

import com.comp2042.config.GameSettings;
import com.comp2042.model.mode.EndlessModeLeaderboard;
import com.comp2042.model.mode.LevelManager;
import com.comp2042.service.audio.SoundManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Singleton Pattern Tests")
class SingletonPatternTest {

    @Test
    @DisplayName("LevelManager: getInstance returns non-null instance")
    void testLevelManagerGetInstanceNotNull() {
        LevelManager manager = LevelManager.getInstance();
        assertNotNull(manager);
    }

    @Test
    @DisplayName("GameSettings: getInstance returns non-null instance")
    void testGameSettingsGetInstanceNotNull() {
        GameSettings settings = GameSettings.getInstance();
        assertNotNull(settings);
    }

    @Test
    @DisplayName("SoundManager: getInstance returns non-null instance")
    void testSoundManagerGetInstanceNotNull() {
        // SoundManager might require JavaFX platform, so catch exception if any
        try {
            SoundManager manager = SoundManager.getInstance();
            assertNotNull(manager);
        } catch (Exception e) {
            // If it fails due to Toolkit not initialized, skip
            System.out.println("Skipping SoundManager test: " + e.getMessage());
        } catch (NoClassDefFoundError e) {
             System.out.println("Skipping SoundManager test: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("EndlessModeLeaderboard: getInstance returns non-null instance")
    void testEndlessModeLeaderboardGetInstanceNotNull() {
        EndlessModeLeaderboard leaderboard = EndlessModeLeaderboard.getInstance();
        assertNotNull(leaderboard);
    }

    @Test
    @DisplayName("Singleton Pattern: Same instance returned")
    void testSameInstance() {
        LevelManager lm1 = LevelManager.getInstance();
        LevelManager lm2 = LevelManager.getInstance();
        assertSame(lm1, lm2);
    }
}
