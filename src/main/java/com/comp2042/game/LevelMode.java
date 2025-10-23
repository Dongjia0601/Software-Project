package com.comp2042.game;

/**
 * Represents the configuration data for a single level within the Level-based game mode.
 * 
 * <p>This class encapsulates all level-specific parameters including theme, difficulty
 * settings, scoring targets, and player progress. It serves as a data container that
 * defines the rules and objectives for a particular level instance in the level-based
 * game mode.</p>
 * 
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Define level difficulty parameters (speed, lines, time limits)</li>
 *   <li>Set scoring targets and star rating criteria</li>
 *   <li>Track player progress and achievements</li>
 *   <li>Manage level unlocking and completion status</li>
 * </ul>
 */
public class LevelMode {

    private final int levelId;
    private final String levelName;
    private final LevelTheme theme;

    // Difficulty parameters
    private final int fallSpeed;          // ms per row drop
    private final int targetLines;        // lines to clear for completion
    private final int timeLimitSeconds;   // time limit in seconds

    // Scoring parameters
    private final int baseTargetScore;    // minimum score needed for 1 star
    private final int threeStarTime;      // seconds needed for 3 stars (time-based)
    private final int threeStarScore;     // score needed for 3 stars (score-based)
    private final int twoStarScore;       // score needed for 2 stars

    // Player progress (persistent state)
    private boolean unlocked;
    private int bestStars;
    private int bestScore;
    private long bestTime;                // best completion time in milliseconds

    /**
     * Constructs a new LevelMode configuration.
     *
     * @param levelId unique level identifier (1-based)
     * @param levelName display name of the level
     * @param theme the theme for this level
     * @param fallSpeed initial fall speed in milliseconds
     * @param targetLines number of lines to clear
     * @param timeLimitSeconds time limit in seconds
     * @param baseTargetScore minimum score for 1 star
     * @param twoStarScore score needed for 2 stars
     * @param threeStarScore score needed for 3 stars
     * @param threeStarTime time in seconds for 3-star rating (time-based)
     */
    public LevelMode(int levelId, String levelName, LevelTheme theme,
                     int fallSpeed, int targetLines, int timeLimitSeconds,
                     int baseTargetScore, int twoStarScore, int threeStarScore,
                     int threeStarTime) {
        this.levelId = levelId;
        this.levelName = levelName;
        this.theme = theme;
        this.fallSpeed = fallSpeed;
        this.targetLines = targetLines;
        this.timeLimitSeconds = timeLimitSeconds;
        this.baseTargetScore = baseTargetScore;
        this.twoStarScore = twoStarScore;
        this.threeStarScore = threeStarScore;
        this.threeStarTime = threeStarTime;

        // Default state
        this.unlocked = (levelId == 1); // Level 1 is always unlocked
        this.bestStars = 0;
        this.bestScore = 0;
        this.bestTime = Long.MAX_VALUE; // Represents no time recorded
    }

    // Getters

    /**
     * Gets the unique level identifier.
     * @return the 1-based level ID
     */
    public int getLevelId() {
        return levelId;
    }

    /**
     * Gets the display name of this level.
     * @return the level name
     */
    public String getLevelName() {
        return levelName;
    }

    /**
     * Gets the theme associated with this level.
     * @return the LevelTheme instance
     */
    public LevelTheme getTheme() {
        return theme;
    }

    /**
     * Gets the fall speed for this level in milliseconds.
     * @return the fall speed
     */
    public int getFallSpeed() {
        return fallSpeed;
    }

    /**
     * Gets the target number of lines to clear.
     * @return the target lines
     */
    public int getTargetLines() {
        return targetLines;
    }

    /**
     * Gets the time limit for this level in seconds.
     * @return the time limit
     */
    public int getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    /**
     * Gets the time limit for this level in milliseconds.
     * @return the time limit in milliseconds
     */
    public long getTimeLimitMillis() {
        return timeLimitSeconds * 1000L;
    }

    /**
     * Gets the base target score needed to earn 1 star.
     * @return the base target score
     */
    public int getBaseTargetScore() {
        return baseTargetScore;
    }

    /**
     * Gets the score needed to earn 2 stars.
     * @return the 2-star score threshold
     */
    public int getTwoStarScore() {
        return twoStarScore;
    }

    /**
     * Gets the score needed to earn 3 stars.
     * @return the 3-star score threshold
     */
    public int getThreeStarScore() {
        return threeStarScore;
    }

    /**
     * Gets the time (in seconds) needed to earn 3 stars based on speed.
     * @return the 3-star time threshold
     */
    public int getThreeStarTime() {
        return threeStarTime;
    }

    /**
     * Checks if this level is unlocked for play.
     * @return true if unlocked, false otherwise
     */
    public boolean isUnlocked() {
        return unlocked;
    }

    /**
     * Gets the highest star rating achieved for this level.
     * @return the best star rating (0-3)
     */
    public int getBestStars() {
        return bestStars;
    }

    /**
     * Gets the highest score achieved for this level.
     * @return the best score
     */
    public int getBestScore() {
        return bestScore;
    }

    /**
     * Gets the best completion time for this level.
     * @return the best time in milliseconds, or Long.MAX_VALUE if not completed
     */
    public long getBestTime() {
        return bestTime;
    }

    /**
     * Checks if this level has been completed (earned at least 1 star).
     * @return true if completed, false otherwise
     */
    public boolean isCompleted() {
        return bestStars > 0;
    }

    // State modifiers (for progress tracking)

    /**
     * Unlocks this level.
     */
    public void unlock() {
        this.unlocked = true;
    }

    /**
     * Locks this level.
     */
    public void lock() {
        this.unlocked = false;
    }

    /**
     * Updates the best score and time if new records are set.
     *
     * @param score the score achieved
     * @param stars the number of stars earned
     * @param timeMillis the completion time in milliseconds
     */
    public void updateBest(int score, int stars, long timeMillis) {
        if (stars > this.bestStars) {
            this.bestStars = stars;
        }
        if (score > this.bestScore) {
            this.bestScore = score;
        }
        // Update best time only if it's faster (smaller) and valid
        if (timeMillis < this.bestTime && timeMillis > 0) {
            this.bestTime = timeMillis;
        }
    }

    /**
     * Resets all best records to initial state.
     * Used when resetting progress.
     */
    public void resetBest() {
        this.bestScore = 0;
        this.bestStars = 0;
        this.bestTime = Long.MAX_VALUE;
    }

    /**
     * Calculates the number of stars earned based on performance.
     * This method should be called after a level is completed.
     *
     * @param score final score
     * @param linesCleared number of lines cleared
     * @param completionTimeSeconds completion time in seconds
     * @param success whether the level was completed successfully (met target lines within time)
     * @return number of stars (0-3)
     */
    public int calculateStars(int score, int linesCleared, int completionTimeSeconds, boolean success) {
        // Failed to complete basic objective
        if (!success || linesCleared < targetLines) {
            return 0;
        }

        int starsEarned = 0;

        // 1 star: completed within time limit and reached target lines
        if (score >= baseTargetScore) {
            starsEarned = 1;

            // Check for 2 stars: good score
            if (score >= twoStarScore) {
                starsEarned = 2;

                // Check for 3 stars: high score AND fast completion
                if (score >= threeStarScore && completionTimeSeconds <= threeStarTime) {
                    starsEarned = 3;
                }
            }
        }

        return starsEarned;
    }

    /**
     * Gets a difficulty rating string based on fall speed.
     * @return difficulty rating (e.g., "Easy", "Medium", "Hard")
     */
    public String getDifficulty() {
        if (fallSpeed >= 500) {
            return "Easy";
        } else if (fallSpeed >= 300) {
            return "Medium";
        } else if (fallSpeed >= 200) {
            return "Hard";
        } else {
            return "Expert";
        }
    }

    @Override
    public String toString() {
        return String.format("Level %d: %s [%s] - %s",
                levelId, levelName, getDifficulty(), theme.getThemeName());
    }
}