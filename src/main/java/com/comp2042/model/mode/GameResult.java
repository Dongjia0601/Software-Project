package com.comp2042.model.mode;

import com.comp2042.controller.factory.GameModeType;

/**
 * Immutable game session result with score, statistics, and completion status.
 * Supports all game modes with mode-specific metrics.
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

    /** Gets final score. */
    public int getFinalScore() {
        return finalScore;
    }

    /** Gets high score for this mode. */
    public int getHighScore() {
        return highScore;
    }

    /** Checks if new high score. */
    public boolean isNewHighScore() {
        return isNewHighScore;
    }

    /** Gets game mode. */
    public GameModeType getGameMode() {
        return gameMode;
    }

    /** Gets play time in milliseconds. */
    public long getPlayTime() {
        return playTime;
    }

    /** Gets lines cleared count. */
    public int getLinesCleared() {
        return linesCleared;
    }

    /** Gets level reached (0 for endless). */
    public int getLevelReached() {
        return levelReached;
    }

    /** Checks if completed successfully. */
    public boolean isCompleted() {
        return completed;
    }

    /** Gets formatted play time as MM:SS. */
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