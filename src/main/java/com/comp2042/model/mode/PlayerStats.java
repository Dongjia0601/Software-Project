package com.comp2042.model.mode;

/**
 * Player statistics tracker for two-player VS mode.
 * Tracks lines cleared, attacks sent/received, combos, and detailed gameplay metrics.
 */
public class PlayerStats {
    
    private int linesCleared = 0;
    private int attacksSent = 0;        // Number of attacks sent to opponent
    private int attacksReceived = 0;   // Number of attacks received from opponent
    private int maxCombo = 0;          // Maximum combo streak
    private int currentCombo = 0;      // Current combo streak
    private long gameStartTime = 0;    // Game start time in milliseconds
    private int tetrisCount = 0;      // Number of 4-line clears (Tetris)
    private int allClears = 0;         // Number of all clears (board cleared completely)
    private int hardDrops = 0;         // Number of hard drops performed
    private int softDrops = 0;         // Number of soft drops performed
    
    /** Constructs PlayerStats with initial values. */
    public PlayerStats() {
        reset();
    }
    
    /**
     * Resets all statistics to initial values.
     * Note: gameStartTime should be set after countdown using startGameTime().
     */
    public void reset() {
        linesCleared = 0;
        attacksSent = 0;
        attacksReceived = 0;
        maxCombo = 0;
        currentCombo = 0;
        gameStartTime = 0;
        tetrisCount = 0;
        allClears = 0;
        hardDrops = 0;
        softDrops = 0;
    }
    
    /** Starts the game timer after countdown completes. */
    public void startGameTime() {
        gameStartTime = System.currentTimeMillis();
    }
    
    /**
     * Records a line clear event.
     * 
     * @param lines the number of lines cleared
     * @param attackPower the attack power generated (lines sent to opponent)
     */
    public void recordLineClear(int lines, int attackPower) {
        linesCleared += lines;
        if (attackPower > 0) {
            attacksSent += attackPower;
        }
        
        // Update combo
        if (lines > 0) {
            currentCombo++;
            maxCombo = Math.max(maxCombo, currentCombo);
        } else {
            currentCombo = 0; // Reset combo if no lines cleared
        }
        
        // Track Tetris (4-line clear)
        if (lines == 4) {
            tetrisCount++;
        }
    }
    
    /**
     * Records an attack received from opponent.
     * 
     * @param lines the number of garbage lines received
     */
    public void recordAttackReceived(int lines) {
        attacksReceived += lines;
    }
    
    /**
     * Records a hard drop.
     */
    public void recordHardDrop() {
        hardDrops++;
    }
    
    /**
     * Records a soft drop.
     */
    public void recordSoftDrop() {
        softDrops++;
    }
    
    /**
     * Records an all clear (board completely cleared).
     */
    public void recordAllClear() {
        allClears++;
    }
    
    /**
     * Resets the current combo streak.
     */
    public void resetCombo() {
        currentCombo = 0;
    }
    
    /**
     * Gets the total number of lines cleared.
     * 
     * @return lines cleared
     */
    public int getLinesCleared() {
        return linesCleared;
    }
    
    /**
     * Gets the total number of attacks sent.
     * 
     * @return attacks sent
     */
    public int getAttacksSent() {
        return attacksSent;
    }
    
    /**
     * Gets the total number of attacks received.
     * 
     * @return attacks received
     */
    public int getAttacksReceived() {
        return attacksReceived;
    }
    
    /**
     * Gets the maximum combo achieved.
     * 
     * @return maximum combo
     */
    public int getMaxCombo() {
        return maxCombo;
    }
    
    /**
     * Gets the current combo streak.
     * 
     * @return current combo
     */
    public int getCurrentCombo() {
        return currentCombo;
    }
    
    /**
     * Gets the game time in seconds.
     * 
     * @return game time in seconds
     */
    public long getGameTimeSeconds() {
        if (gameStartTime == 0) {
            return 0;
        }
        return (System.currentTimeMillis() - gameStartTime) / 1000;
    }
    
    /**
     * Gets the number of Tetris clears (4-line clears).
     * 
     * @return Tetris count
     */
    public int getTetrisCount() {
        return tetrisCount;
    }
    
    /**
     * Gets the number of all clears.
     * 
     * @return all clear count
     */
    public int getAllClears() {
        return allClears;
    }
    
    /**
     * Gets the number of hard drops performed.
     * 
     * @return hard drop count
     */
    public int getHardDrops() {
        return hardDrops;
    }
    
    /**
     * Gets the number of soft drops performed.
     * 
     * @return soft drop count
     */
    public int getSoftDrops() {
        return softDrops;
    }
    
    /**
     * Gets the lines cleared per minute (LPM) rate.
     * 
     * @return LPM rate, or 0 if game time is 0
     */
    public double getLPM() {
        long timeSeconds = getGameTimeSeconds();
        if (timeSeconds == 0) {
            return 0.0;
        }
        return (linesCleared * 60.0) / timeSeconds;
    }
    
    /**
     * Gets a formatted string representation of game time.
     * 
     * @return formatted time string (MM:SS)
     */
    public String getFormattedTime() {
        long seconds = getGameTimeSeconds();
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

