// File: src/main/java/com/comp2042/game/LevelManager.java
package com.comp2042.game;

import com.comp2042.game.themes.*;

import java.util.*;
import java.util.prefs.Preferences;

/**
 * Singleton manager for themed levels.
 * Handles level initialization, progression, and persistence.
 *
 * <p>This class manages a collection of LevelMode instances, tracks player progress,
 * unlocks subsequent levels based on completion, and saves/loads progress to/from disk.
 * It directly supports the Additions (25%) - New Playable Levels requirement.
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
     * Private constructor for singleton pattern.
     * Initializes levels and loads progress.
     */
    private LevelManager() {
        this.levels = new ArrayList<>();
        this.prefs = Preferences.userRoot().node(PREF_NODE);
        initializeLevels();
        loadProgress();
    }

    /**
     * Gets the singleton instance.
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
                600,    // fallSpeed (ms) - Slow
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
                500,    // fallSpeed (ms) - Slow-Medium
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
                400,    // fallSpeed (ms) - Medium
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
                250,    // fallSpeed (ms) - Fast
                8,      // targetLines
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
                150,    // fallSpeed (ms) - Very Fast
                6,      // targetLines
                60,     // timeLimitSeconds (1 minute)
                1000,   // baseTargetScore
                2000,   // twoStarScore
                3000,   // threeStarScore
                50      // threeStarTime (50 seconds)
        ));

        System.out.println("LevelManager: Initialized " + levels.size() + " themed levels");
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
     */
    public void completeLevel(int levelId, int score, int linesCleared,
                              long completionTimeMillis, boolean success) {
        LevelMode level = getLevel(levelId);
        if (level == null) {
            return;
        }

        int completionTimeSeconds = (int) (completionTimeMillis / 1000);
        // Calculate stars based on level's own logic
        int stars = level.calculateStars(score, linesCleared, completionTimeSeconds, success);

        // Update the level's best performance
        level.updateBest(score, stars, completionTimeMillis);

        // Unlock next level if earned at least 1 star
        if (stars > 0) {
            unlockNextLevel(levelId);
        }

        // Save progress after completion
        saveProgress();

        System.out.println(String.format(
                "=== Level Manager: Level %d Completed ===", levelId
        ));
        System.out.println(String.format(
                "  Stars: %d, Score: %d, Time: %ds",
                stars, score, completionTimeSeconds
        ));
        System.out.println(String.format(
                "  Best Stars: %d, Best Score: %d",
                level.getBestStars(), level.getBestScore()
        ));
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
                System.out.println("Unlocked level " + (currentLevelId + 1));
            }
        }
    }

    /**
     * Manually unlocks a level (for testing/debugging).
     *
     * @param levelId the level ID to unlock
     */
    public void unlockLevel(int levelId) {
        LevelMode level = getLevel(levelId);
        if (level != null) {
            level.unlock();
            saveProgress(); // Save after manual unlock
        }
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
        System.out.println("Level progress reset - all levels locked except Level 1, all stars and scores cleared");
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
            prefs.flush(); // Force write to backing store

            System.out.println("Level progress data cleared from Preferences");
        } catch (Exception e) {
            System.err.println("Failed to clear level progress  " + e.getMessage());
            e.printStackTrace();
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
            System.out.println("Level progress saved using Preferences API");
        } catch (Exception e) {
            System.err.println("Failed to save level progress: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads level progress from persistent storage (Preferences API).
     * Directly supports Additions (25%) - New Playable Levels (Persistence).
     */
    private void loadProgress() {
        try {
            boolean anyProgressFound = false;
            for (LevelMode level : levels) {
                int levelId = level.getLevelId();
                String keyPrefix = PROGRESS_KEY_PREFIX + levelId + "_";

                boolean unlocked = prefs.getBoolean(keyPrefix + "unlocked", levelId == 1); // Default: Level 1 unlocked
                int bestStars = prefs.getInt(keyPrefix + "bestStars", 0);
                int bestScore = prefs.getInt(keyPrefix + "bestScore", 0);
                long bestTime = prefs.getLong(keyPrefix + "bestTime", Long.MAX_VALUE);

                if (unlocked || bestStars > 0 || bestScore > 0 || bestTime != Long.MAX_VALUE) {
                    anyProgressFound = true;
                }

                // Apply loaded state
                if (unlocked) {
                    level.unlock();
                } else {
                    level.lock();
                }
                level.updateBest(bestScore, bestStars, bestTime);
            }

            if (anyProgressFound) {
                System.out.println("Level progress loaded from Preferences API");
            } else {
                System.out.println("No saved progress found, starting fresh");
            }
        } catch (Exception e) {
            System.err.println("Failed to load level progress: " + e.getMessage());
            // Start fresh on load error
        }
    }
}