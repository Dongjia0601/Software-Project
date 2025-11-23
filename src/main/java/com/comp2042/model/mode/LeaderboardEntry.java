package com.comp2042.model.mode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Immutable leaderboard entry with score, statistics, and timestamp (Value Object Pattern).
 * Comparable by score in descending order for ranking.
 */
public class LeaderboardEntry implements Comparable<LeaderboardEntry> {
    
    private final int score;
    private final int linesCleared;
    private final String timestamp;
    private final long playTimeMs;
    private final int level;
    
    /**
     * Creates a leaderboard entry with current timestamp.
     * 
     * @param score Final score
     * @param linesCleared Lines cleared
     * @param playTimeMs Play time in ms
     * @param level Level reached
     */
    public LeaderboardEntry(int score, int linesCleared, long playTimeMs, int level) {
        this.score = score;
        this.linesCleared = linesCleared;
        this.playTimeMs = playTimeMs;
        this.level = level;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    
    /**
     * Creates entry from stored data (for loading from persistence).
     * 
     * @param score Score
     * @param linesCleared Lines cleared
     * @param playTimeMs Play time
     * @param level Level
     * @param timestamp Timestamp string
     */
    public LeaderboardEntry(int score, int linesCleared, long playTimeMs, int level, String timestamp) {
        this.score = score;
        this.linesCleared = linesCleared;
        this.playTimeMs = playTimeMs;
        this.level = level;
        this.timestamp = timestamp;
    }
    
    /**
     * Gets the final score achieved in this game session.
     * 
     * @return the score value
     */
    public int getScore() {
        return score;
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
     * Gets the total play time in milliseconds.
     * 
     * @return play time in milliseconds
     */
    public long getPlayTimeMs() {
        return playTimeMs;
    }
    
    /**
     * Gets the timestamp when this entry was created.
     * 
     * @return timestamp string in format "yyyy-MM-dd HH:mm"
     */
    public String getTimestamp() {
        return timestamp;
    }
    
    /**
     * Gets the level reached in this game session.
     * 
     * @return the level number (0 for endless mode)
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * Gets formatted play time as MM:SS.
     * 
     * @return formatted time string (e.g., "05:23")
     */
    public String getFormattedPlayTime() {
        long seconds = playTimeMs / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    /**
     * Compares entries by score in descending order.
     * Higher scores rank first in the leaderboard.
     * 
     * @param other the entry to compare with
     * @return negative if this score is higher, positive if lower, 0 if equal
     */
    @Override
    public int compareTo(LeaderboardEntry other) {
        // Descending order: higher score comes first
        return Integer.compare(other.score, this.score);
    }
    
    @Override
    public String toString() {
        return String.format("LeaderboardEntry{score=%d, lines=%d, level=%d, time=%s, date=%s}",
                score, linesCleared, level, getFormattedPlayTime(), timestamp);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof LeaderboardEntry other)) return false;
        return score == other.score && 
               linesCleared == other.linesCleared && 
               timestamp.equals(other.timestamp);
    }
    
    @Override
    public int hashCode() {
        return 31 * score + linesCleared + timestamp.hashCode();
    }
}

