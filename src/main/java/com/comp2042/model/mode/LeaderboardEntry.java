package com.comp2042.model.mode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single entry in the Endless Mode leaderboard.
 * 
 * <p>Each entry contains the player's score, lines cleared, play time and level.
 * Entries are comparable by score (descending order) for ranking purposes.</p>
 * 
 * <p>Design Pattern: Value Object - Immutable data container for leaderboard entries.</p>
 */
public class LeaderboardEntry implements Comparable<LeaderboardEntry> {
    
    private final int score;
    private final int linesCleared;
    private final String timestamp;
    private final long playTimeMs;
    private final int level;
    
    /**
     * Creates a new leaderboard entry.
     * 
     * @param score the final score achieved
     * @param linesCleared the number of lines cleared
     * @param playTimeMs the play time in milliseconds
     * @param level the final level reached
     */
    public LeaderboardEntry(int score, int linesCleared, long playTimeMs, int level) {
        this.score = score;
        this.linesCleared = linesCleared;
        this.playTimeMs = playTimeMs;
        this.level = level;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    
    /**
     * Creates a leaderboard entry from stored data.
     * Used when loading from persistent storage.
     * 
     * @param score the score
     * @param linesCleared the lines cleared
     * @param playTimeMs the play time
     * @param level the level
     * @param timestamp the timestamp string
     */
    public LeaderboardEntry(int score, int linesCleared, long playTimeMs, int level, String timestamp) {
        this.score = score;
        this.linesCleared = linesCleared;
        this.playTimeMs = playTimeMs;
        this.level = level;
        this.timestamp = timestamp;
    }
    
    public int getScore() {
        return score;
    }
    
    public int getLinesCleared() {
        return linesCleared;
    }
    
    public long getPlayTimeMs() {
        return playTimeMs;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public int getLevel() {
        return level;
    }
    
    /**
     * Gets formatted play time as MM:SS.
     * 
     * @return formatted time string
     */
    public String getFormattedPlayTime() {
        long seconds = playTimeMs / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    /**
     * Compares entries by score (descending order).
     * Higher scores come first.
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
        if (!(obj instanceof LeaderboardEntry)) return false;
        LeaderboardEntry other = (LeaderboardEntry) obj;
        return score == other.score && 
               linesCleared == other.linesCleared && 
               timestamp.equals(other.timestamp);
    }
    
    @Override
    public int hashCode() {
        return 31 * score + linesCleared + timestamp.hashCode();
    }
}

