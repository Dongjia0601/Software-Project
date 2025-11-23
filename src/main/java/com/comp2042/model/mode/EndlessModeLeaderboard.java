package com.comp2042.model.mode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Endless Mode leaderboard managing top 5 scores with CSV persistence (Singleton Pattern).
 * Tracks score, lines cleared, play time, and timestamp. Provides ranking and high score detection.
 * Stored in user home directory (.tetris/).
 */
public class EndlessModeLeaderboard {
    
    private static final int MAX_ENTRIES = 5;
    private static final String LEADERBOARD_FILE = "tetris_endless_leaderboard.csv";
    private static EndlessModeLeaderboard instance;
    
    private final List<LeaderboardEntry> entries;
    private final Path leaderboardPath;
    
    /**
     * Private constructor enforcing Singleton pattern.
     * Initializes the leaderboard and loads existing entries from file.
     */
    private EndlessModeLeaderboard() {
        this.entries = new ArrayList<>();
        
        // Store leaderboard in user's home directory
        String userHome = System.getProperty("user.home");
        if (userHome == null || userHome.isEmpty()) {
            // Fallback to current directory if user.home is not available
            System.err.println("Warning: user.home system property not available, using current directory for leaderboard");
            this.leaderboardPath = Paths.get(".tetris", LEADERBOARD_FILE);
        } else {
            this.leaderboardPath = Paths.get(userHome, ".tetris", LEADERBOARD_FILE);
        }
        
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
     * @param level the final level
     * @return the rank (1-5) if entry was added, 0 if not in top 5
     */
    public synchronized int addEntry(int score, int linesCleared, long playTimeMs, int level) {
        // Don't add entries with 0 or negative scores
        if (score <= 0) {
            return 0;
        }
        
        LeaderboardEntry newEntry = new LeaderboardEntry(score, linesCleared, playTimeMs, level);
        
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
        // Safety check: ensure we have enough entries before accessing
        if (entries.isEmpty() || entries.size() < MAX_ENTRIES) {
            return true;
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
        return entries.isEmpty() || score > entries.getFirst().getScore();
    }
    
    /**
     * Gets the current high score.
     * 
     * @return the highest score, or 0 if no entries
     */
    public synchronized int getHighScore() {
        return entries.isEmpty() ? 0 : entries.getFirst().getScore();
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
                        int level = 1;
                        if (parts.length >= 5) {
                            level = Integer.parseInt(parts[4].trim());
                        }
                        
                        // Only add entries with positive scores
                        if (score > 0) {
                            entries.add(new LeaderboardEntry(score, lines, time, level, timestamp));
                        }
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
            writer.write("score,lines,time_ms,timestamp,level");
            writer.newLine();
            
            // Write entries
            for (LeaderboardEntry entry : entries) {
                writer.write(String.format("%d,%d,%d,%s,%d",
                        entry.getScore(),
                        entry.getLinesCleared(),
                        entry.getPlayTimeMs(),
                        entry.getTimestamp(),
                        entry.getLevel()));
                writer.newLine();
            }
            
            
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

