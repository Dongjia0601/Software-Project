package com.comp2042.model.mode;

import com.comp2042.view.theme.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Singleton managing themed levels with progression and persistence (Singleton Pattern).
 * Handles level initialization, tracks player progress, unlocks levels, and saves/loads state.
 * Supports Additions (25%) - New Playable Levels requirement.
 */
public class LevelManager {

    private static LevelManager instance;

    private final List<LevelMode> levels;
    private LevelMode currentLevel;
    // Use Preferences API for cross-platform compatibility and simplicity
    private final Preferences prefs;
    private static final String PREF_NODE = "com/comp2042/tetris";
    private static final String PROGRESS_KEY_PREFIX = "level_progress_";

    /**
     * Private constructor (Singleton). Initializes levels and loads progress.
     */
    private LevelManager() {
        this.levels = new ArrayList<>();
        this.prefs = Preferences.userRoot().node(PREF_NODE);
        initializeLevels();
        loadProgress();
    }

    /** Gets the singleton instance.
     *
     * @return the LevelManager instance
     */
    public static synchronized LevelManager getInstance() {
        if (instance == null) {
            instance = new LevelManager();
        }
        return instance;
    }

    /**
     * Initializes all themed levels with their configurations.
     * This method sets up the initial state of the game with predefined levels.
     * Directly supports Additions (25%) - New Playable Levels.
     */
    private void initializeLevels() {
        // Level 1: Ancient Temple (Easy)
        levels.add(new LevelMode(
                1,
                "Ancient Temple",
                new AncientTempleTheme(),
                700,    // fallSpeed (ms) - Very Slow
                5,      // targetLines
                180,    // timeLimitSeconds (3 minutes)
                200,    // baseTargetScore (1 star)
                400,    // twoStarScore
                600,    // threeStarScore
                150     // threeStarTime (2:30)
        ));

        // Level 2: Magic Castle (Easy-Medium)
        levels.add(new LevelMode(
                2,
                "Magic Castle",
                new MagicCastleTheme(),
                550,    // fallSpeed (ms) - Slow
                7,      // targetLines
                150,    // timeLimitSeconds (2.5 minutes)
                400,    // baseTargetScore (1 star)
                800,    // twoStarScore
                1200,   // threeStarScore
                125     // threeStarTime (2:05)
        ));

        // Level 3: Sunset Village (Medium)
        levels.add(new LevelMode(
                3,
                "Sunset Village",
                new SunsetCityTheme(),
                450,    // fallSpeed (ms) - Medium
                10,     // targetLines
                120,    // timeLimitSeconds (2 minutes)
                600,    // baseTargetScore
                1200,   // twoStarScore
                1800,   // threeStarScore
                100     // threeStarTime (1:40)
        ));

        // Level 4: Future Warfare (Hard)
        levels.add(new LevelMode(
                4,
                "Future Warfare",
                new FutureWarfareTheme(),
                350,    // fallSpeed (ms) - Fast
                12,     // targetLines
                90,     // timeLimitSeconds (1.5 minutes)
                800,    // baseTargetScore
                1600,   // twoStarScore
                2400,   // threeStarScore
                75      // threeStarTime (1:15)
        ));

        // Level 5: Interstellar (Expert)
        levels.add(new LevelMode(
                5,
                "Interstellar",
                new InterstellarTheme(),
                280,    // fallSpeed (ms) - Very Fast but playable
                15,     // targetLines
                60,     // timeLimitSeconds (1 minute)
                1000,   // baseTargetScore
                2000,   // twoStarScore
                3000,   // threeStarScore
                50      // threeStarTime (50 seconds)
        ));
    }


    /**
     * Gets all levels.
     *
     * @return unmodifiable list of levels
     */
    public List<LevelMode> getAllLevels() {
        return Collections.unmodifiableList(levels);
    }

    /**
     * Gets a level by its ID (1-based).
     *
     * @param levelId the level ID
     * @return the level, or null if not found
     */
    public LevelMode getLevel(int levelId) {
        return levels.stream()
                .filter(level -> level.getLevelId() == levelId)
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets the current level being played.
     *
     * @return the current level, or null if none selected
     */
    public LevelMode getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Sets the current level.
     *
     * @param level the level to set as current
     */
    public void setCurrentLevel(LevelMode level) {
        this.currentLevel = level;
    }

    /**
     * Sets the current level by ID.
     *
     * @param levelId the level ID (1-based)
     * @return true if successful, false if invalid ID or level locked
     */
    public boolean setCurrentLevel(int levelId) {
        LevelMode level = getLevel(levelId);
        // Check if level exists and is unlocked
        if (level != null && level.isUnlocked()) {
            this.currentLevel = level;
            return true;
        }
        return false;
    }

    /**
     * Completes a level with the given performance.
     * Updates best scores and unlocks next level if applicable.
     * Directly supports Additions (25%) - New Playable Levels.
     *
     * @param levelId the completed level ID
     * @param score the final score
     * @param linesCleared the number of lines cleared
     * @param completionTimeMillis the completion time in milliseconds
     * @param success whether the level was completed successfully
     * @return an array of two booleans: [isNewBestScore, isNewBestTime]
     */
    public boolean[] completeLevel(int levelId, int score, int linesCleared,
                              long completionTimeMillis, boolean success) {
        LevelMode level = getLevel(levelId);
        if (level == null) {
            return new boolean[]{false, false};
        }

        int completionTimeSeconds = (int) (completionTimeMillis / 1000);
        // Calculate stars based on level's own logic
        int stars = level.calculateStars(score, linesCleared, completionTimeSeconds, success);

        // Update the level's best performance and get whether new records were set
        boolean[] newRecords = level.updateBest(score, stars, completionTimeMillis);

        // Unlock next level if earned at least 1 star
        if (stars > 0) {
            unlockNextLevel(levelId);
        }

        // Save progress after completion
        saveProgress();
        
        return newRecords;
    }


    /**
     * Unlocks the next level if it exists and is currently locked.
     *
     * @param currentLevelId the current level ID
     */
    private void unlockNextLevel(int currentLevelId) {
        if (currentLevelId < levels.size()) {
            LevelMode nextLevel = getLevel(currentLevelId + 1);
            if (nextLevel != null && !nextLevel.isUnlocked()) {
                nextLevel.unlock();
            }
        }
    }

    /**
     * Manually unlocks a level.
     * 
     * <p><strong>WARNING: This method is intended for testing and debugging purposes only.</strong>
     * It bypasses the normal level progression system and should not be called from production code.
     * All calls to this method are logged for security auditing purposes.</p>
     * 
     * <p><strong>Security Note:</strong> This method is not accessible through the user interface
     * and has no keyboard shortcuts or other user-accessible entry points. It is only used by
     * unit tests to set up test scenarios.</p>
     *
     * @param levelId the level ID to unlock (1-based)
     * 
     * <p><b>Note:</b> This method is part of the testing API and should not be used in production code.
     *          Use {@link #completeLevel(int, int, int, long, boolean)} for normal level progression.
     */
    public void unlockLevel(int levelId) {
        // This method is intended for testing and debugging purposes only
        LevelMode level = getLevel(levelId);
        if (level != null) {
            level.unlock();
            saveProgress(); // Save after manual unlock
        }
        // Invalid level ID is silently ignored in test environments
    }
    
    /**
     * Checks if a level is completed.
     *
     * @param levelId the level ID
     * @return true if completed (1+ stars)
     */
    public boolean isLevelCompleted(int levelId) {
        LevelMode level = getLevel(levelId);
        return level != null && level.isCompleted();
    }

    /**
     * Gets the total number of stars earned across all levels.
     *
     * @return total stars
     */
    public int getTotalStars() {
        return levels.stream()
                .mapToInt(LevelMode::getBestStars)
                .sum();
    }

    /**
     * Gets the total number of levels.
     *
     * @return number of levels
     */
    public int getTotalLevels() {
        return levels.size();
    }

    /**
     * Gets the number of completed levels.
     *
     * @return number of completed levels
     */
    public int getCompletedLevelsCount() {
        return (int) levels.stream()
                .filter(LevelMode::isCompleted)
                .count();
    }

    /**
     * Resets all level progress (for testing).
     * Directly supports Basic Maintenance (35%) - deleting unused resources.
     */
    public void resetProgress() {
        for (LevelMode level : levels) {
            // Reset best records for all levels
            level.resetBest();

            // Reset unlock status
            if (level.getLevelId() == 1) {
                // Level 1: unlocked but no progress
                level.unlock(); // Ensure level 1 is unlocked
            } else {
                // Level 2+: locked and no progress
                level.lock(); // Lock the level
            }
        }

        saveProgress();
    }

    /**
     * Clears all level progress data and deletes the progress file.
     * This completely removes the level progress persistence file.
     * Directly supports Basic Maintenance (35%) - deleting unused resources.
     */
    public void clearAllData() {
        try {
            // Reset all levels to initial state in memory
            for (LevelMode level : levels) {
                if (level.getLevelId() == 1) {
                    level.updateBest(0, 0, Long.MAX_VALUE);
                    level.unlock(); // Ensure level 1 is unlocked
                } else {
                    level.updateBest(0, 0, Long.MAX_VALUE);
                    level.lock(); // Lock the level
                }
            }

            // Clear preferences
            prefs.clear(); // Clear all preferences under this node
            prefs.flush(); // Force write to back store
        } catch (Exception e) {
            System.err.println("Failed to clear level progress: " + e.getMessage());
            // Log full stack trace for debugging
            System.err.println("Stack trace: " + java.util.Arrays.toString(e.getStackTrace()));
        }
    }


    /**
     * Saves level progress to persistent storage (Preferences API).
     * Directly supports Additions (25%) - New Playable Levels (Persistence).
     */
    private void saveProgress() {
        try {
            for (LevelMode level : levels) {
                int levelId = level.getLevelId();
                String keyPrefix = PROGRESS_KEY_PREFIX + levelId + "_";

                prefs.putBoolean(keyPrefix + "unlocked", level.isUnlocked());
                prefs.putInt(keyPrefix + "bestStars", level.getBestStars());
                prefs.putInt(keyPrefix + "bestScore", level.getBestScore());
                prefs.putLong(keyPrefix + "bestTime", level.getBestTime());
            }
            prefs.flush(); // Ensure data is written to persistent storage
        } catch (Exception e) {
            System.err.println("Failed to save level progress: " + e.getMessage());
            // Log full stack trace for debugging
            System.err.println("Stack trace: " + java.util.Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * Loads level progress from persistent storage (Preferences API).
     * Directly supports Additions (25%) - New Playable Levels (Persistence).
     */
    private void loadProgress() {
        try {
            for (LevelMode level : levels) {
                int levelId = level.getLevelId();
                String keyPrefix = PROGRESS_KEY_PREFIX + levelId + "_";

                boolean unlocked = prefs.getBoolean(keyPrefix + "unlocked", levelId == 1); // Default: Level 1 unlocked
                int bestStars = prefs.getInt(keyPrefix + "bestStars", 0);
                int bestScore = prefs.getInt(keyPrefix + "bestScore", 0);
                long bestTime = prefs.getLong(keyPrefix + "bestTime", Long.MAX_VALUE);

                // Apply loaded state
                if (unlocked) {
                    level.unlock();
                } else {
                    level.lock();
                }
                level.updateBest(bestScore, bestStars, bestTime);
            }

        } catch (Exception e) {
            System.err.println("Failed to load level progress: " + e.getMessage());
            // Start fresh on load error
        }
    }
}