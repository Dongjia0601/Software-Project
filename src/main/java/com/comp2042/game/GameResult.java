package com.comp2042.game;

import com.comp2042.gameplay.GameModeType;

/**
 * Represents the result of a completed game session.
 * Contains information about the final score, completion status,
 * and any additional metrics specific to the game mode.
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
     * Constructs a new game result with the specified parameters.
     * @param finalScore the final score achieved
     * @param highScore the high score for this mode
     * @param isNewHighScore whether this is a new high score
     * @param gameMode the game mode that was played
     * @param playTime the total play time in milliseconds
     * @param linesCleared the number of lines cleared
     * @param levelReached the level reached (0 for endless mode)
     * @param completed whether the game was completed successfully
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
     * Gets the final score achieved in this game.
     * @return the final score
     */
    public int getFinalScore() {
        return finalScore;
    }

    /**
     * Gets the high score for this game mode.
     * @return the high score
     */
    public int getHighScore() {
        return highScore;
    }

    /**
     * Checks if this game achieved a new high score.
     * @return true if this is a new high score
     */
    public boolean isNewHighScore() {
        return isNewHighScore;
    }

    /**
     * Gets the game mode that was played.
     * @return the game mode type
     */
    public GameModeType getGameMode() {
        return gameMode;
    }

    /**
     * Gets the total play time in milliseconds.
     * @return the play time
     */
    public long getPlayTime() {
        return playTime;
    }

    /**
     * Gets the number of lines cleared.
     * @return the lines cleared count
     */
    public int getLinesCleared() {
        return linesCleared;
    }

    /**
     * Gets the level reached (0 for endless mode).
     * @return the level reached
     */
    public int getLevelReached() {
        return levelReached;
    }

    /**
     * Checks if the game was completed successfully.
     * @return true if completed, false if game over
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Gets a formatted play time string.
     * @return the play time formatted as MM:SS
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