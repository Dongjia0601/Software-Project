package com.comp2042.game;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages the leaderboard for Endless Mode.
 * 
 * <p>This class handles persistent storage of the top 5 high scores,
 * including score, lines cleared, play time, and timestamp. Data is
 * stored in a CSV file in the user's home directory.</p>
 * 
 * <p>Design Pattern: Singleton - Ensures only one leaderboard instance
 * manages the persistent data throughout the application lifecycle.</p>
 * 
 * <p>Key Features:</p>
 * <ul>
 *   <li>Top 5 score tracking</li>
 *   <li>Persistent storage using CSV format</li>
 *   <li>High score detection</li>
 *   <li>Ranking calculation</li>
 * </ul>
 */
public class EndlessModeLeaderboard {
    
    private static final int MAX_ENTRIES = 5;
    private static final String LEADERBOARD_FILE = "tetris_endless_leaderboard.csv";
    private static EndlessModeLeaderboard instance;
    
    private final List<LeaderboardEntry> entries;
    private final Path leaderboardPath;
    
    /**
     * Private constructor for singleton pattern.
     * Initializes the leaderboard and loads existing data.
     */
    private EndlessModeLeaderboard() {
        this.entries = new ArrayList<>();
        
        // Store leaderboard in user's home directory
        String userHome = System.getProperty("user.home");
        this.leaderboardPath = Paths.get(userHome, ".tetris", LEADERBOARD_FILE);
        
        // Create directory if it doesn't exist
        try {
            Files.createDirectories(leaderboardPath.getParent());
        } catch (IOException e) {
            System.err.println("Failed to create leaderboard directory: " + e.getMessage());
        }
        
        loadLeaderboard();
    }
    
    /**
     * Gets the singleton instance of the leaderboard.
     * 
     * @return the leaderboard instance
     */
    public static synchronized EndlessModeLeaderboard getInstance() {
        if (instance == null) {
            instance = new EndlessModeLeaderboard();
        }
        return instance;
    }
    
    /**
     * Attempts to add a new entry to the leaderboard.
     * Only adds if the score qualifies for top 5.
     * 
     * @param score the final score
     * @param linesCleared the lines cleared
     * @param playTimeMs the play time in milliseconds
     * @return the rank (1-5) if entry was added, 0 if not in top 5
     */
    public synchronized int addEntry(int score, int linesCleared, long playTimeMs) {
        LeaderboardEntry newEntry = new LeaderboardEntry(score, linesCleared, playTimeMs);
        
        // Add the new entry
        entries.add(newEntry);
        
        // Sort in descending order by score
        Collections.sort(entries);
        
        // Find the rank of the new entry
        int rank = 0;
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).equals(newEntry)) {
                rank = i + 1;
                break;
            }
        }
        
        // Keep only top 5 entries
        if (entries.size() > MAX_ENTRIES) {
            entries.subList(MAX_ENTRIES, entries.size()).clear();
        }
        
        // Save to file
        saveLeaderboard();
        
        // Return rank if in top 5, otherwise 0
        return rank <= MAX_ENTRIES ? rank : 0;
    }
    
    /**
     * Checks if a score would qualify for the leaderboard.
     * 
     * @param score the score to check
     * @return true if the score would be in top 5
     */
    public synchronized boolean wouldQualify(int score) {
        if (entries.size() < MAX_ENTRIES) {
            return true; // Always qualifies if not full
        }
        return score > entries.get(MAX_ENTRIES - 1).getScore();
    }
    
    /**
     * Checks if a score is a new high score (rank #1).
     * 
     * @param score the score to check
     * @return true if this would be the highest score
     */
    public synchronized boolean isNewHighScore(int score) {
        return entries.isEmpty() || score > entries.get(0).getScore();
    }
    
    /**
     * Gets the current high score.
     * 
     * @return the highest score, or 0 if no entries
     */
    public synchronized int getHighScore() {
        return entries.isEmpty() ? 0 : entries.get(0).getScore();
    }
    
    /**
     * Gets the top entries (up to 5).
     * 
     * @return unmodifiable list of leaderboard entries
     */
    public synchronized List<LeaderboardEntry> getTopEntries() {
        return Collections.unmodifiableList(entries);
    }
    
    /**
     * Gets the number of entries in the leaderboard.
     * 
     * @return the number of entries (0-5)
     */
    public synchronized int getEntryCount() {
        return entries.size();
    }
    
    /**
     * Clears all leaderboard entries.
     * Useful for testing or reset functionality.
     */
    public synchronized void clearLeaderboard() {
        entries.clear();
        saveLeaderboard();
    }
    
    /**
     * Loads the leaderboard from the CSV file.
     */
    private void loadLeaderboard() {
        entries.clear();
        
        if (!Files.exists(leaderboardPath)) {
            System.out.println("No existing leaderboard found. Starting fresh.");
            return;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(leaderboardPath)) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                // Skip header line
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    try {
                        int score = Integer.parseInt(parts[0].trim());
                        int lines = Integer.parseInt(parts[1].trim());
                        long time = Long.parseLong(parts[2].trim());
                        String timestamp = parts[3].trim();
                        
                        entries.add(new LeaderboardEntry(score, lines, time, timestamp));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid leaderboard entry: " + line);
                    }
                }
            }
            
            // Sort entries just in case
            Collections.sort(entries);
            
        } catch (IOException e) {
            System.err.println("Failed to load leaderboard: " + e.getMessage());
        }
    }
    
    /**
     * Saves the leaderboard to the CSV file.
     */
    private void saveLeaderboard() {
        try (BufferedWriter writer = Files.newBufferedWriter(leaderboardPath)) {
            // Write header
            writer.write("score,lines,time_ms,timestamp");
            writer.newLine();
            
            // Write entries
            for (LeaderboardEntry entry : entries) {
                writer.write(String.format("%d,%d,%d,%s",
                        entry.getScore(),
                        entry.getLinesCleared(),
                        entry.getPlayTimeMs(),
                        entry.getTimestamp()));
                writer.newLine();
            }
            
            System.out.println("Saved " + entries.size() + " leaderboard entries.");
            
        } catch (IOException e) {
            System.err.println("Failed to save leaderboard: " + e.getMessage());
        }
    }
    
    @Override
    public String toString() {
        return "EndlessModeLeaderboard{" +
                "entries=" + entries.size() +
                ", highScore=" + getHighScore() +
                '}';
    }
}

