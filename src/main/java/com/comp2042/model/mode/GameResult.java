package com.comp2042.model.mode;

import com.comp2042.controller.factory.GameModeType;

/**
 * Immutable game session result with score, statistics, and completion status.
 * Supports all game modes with mode-specific metrics.
 * 
 * @author Dong, Jia.
 */
public class GameResult {

    private final int finalScore;
    private final int highScore;
    private final boolean isNewHighScore;
    private final GameModeType gameMode;
    private final long playTime;
    private final int linesCleared;
    private final int levelReached;
    private final boolean completed;

    /**
     * Constructs a game result.
     * @param finalScore Final score achieved
     * @param highScore High score for this mode
     * @param isNewHighScore Whether new high score
     * @param gameMode Game mode played
     * @param playTime Play time in milliseconds
     * @param linesCleared Lines cleared count
     * @param levelReached Level reached (0 for endless)
     * @param completed Whether completed successfully
     */
    public GameResult(int finalScore, int highScore, boolean isNewHighScore,
                      GameModeType gameMode, long playTime, int linesCleared,
                      int levelReached, boolean completed) {
        this.finalScore = finalScore;
        this.highScore = highScore;
        this.isNewHighScore = isNewHighScore;
        this.gameMode = gameMode;
        this.playTime = playTime;
        this.linesCleared = linesCleared;
        this.levelReached = levelReached;
        this.completed = completed;
    }

    /**
     * Gets the final score achieved in this game session.
     * 
     * @return the final score value
     */
    public int getFinalScore() {
        return finalScore;
    }

    /**
     * Gets the high score for this game mode.
     * 
     * @return the high score value
     */
    public int getHighScore() {
        return highScore;
    }

    /**
     * Checks if this game session achieved a new high score.
     * 
     * @return true if this is a new high score, false otherwise
     */
    public boolean isNewHighScore() {
        return isNewHighScore;
    }

    /**
     * Gets the game mode that was played.
     * 
     * @return the game mode type
     */
    public GameModeType getGameMode() {
        return gameMode;
    }

    /**
     * Gets the total play time in milliseconds.
     * 
     * @return play time in milliseconds
     */
    public long getPlayTime() {
        return playTime;
    }

    /**
     * Gets the total number of lines cleared in this game session.
     * 
     * @return the lines cleared count
     */
    public int getLinesCleared() {
        return linesCleared;
    }

    /**
     * Gets the level reached in this game session.
     * 
     * @return the level number (0 for endless mode)
     * @apiNote Reserved for future use - not currently invoked. Level information
     *          is available through other means in the current implementation.
     */
    public int getLevelReached() {
        return levelReached;
    }

    /**
     * Checks if the game was completed successfully.
     * 
     * @return true if completed, false if game over
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Gets formatted play time as MM:SS.
     * 
     * @return formatted time string (e.g., "05:23")
     */
    public String getFormattedPlayTime() {
        long seconds = playTime / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public String toString() {
        return String.format("GameResult{score=%d, highScore=%d, newHigh=%s, mode=%s, time=%s, lines=%d, level=%d, completed=%s}",
                finalScore, highScore, isNewHighScore, gameMode, getFormattedPlayTime(), linesCleared, levelReached, completed);
    }
}