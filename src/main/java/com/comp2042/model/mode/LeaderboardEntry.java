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
    
    /** Gets formatted play time as MM:SS. */
    public String getFormattedPlayTime() {
        long seconds = playTimeMs / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    /** Compares by score (descending) - higher scores rank first. */
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

