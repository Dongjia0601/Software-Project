package com.comp2042;

/**
 * Manages sound effects for the game.
 * Provides methods to play various sound effects like attacks, line clears, etc.
 * Currently uses system beeps - can be extended to use actual audio files.
 */
public class SoundManager {
    private static SoundManager instance;
    private boolean soundEnabled = true;
    private double volume = 0.5;
    
    private SoundManager() {
        // Initialize sound manager
    }
    
    /**
     * Gets the singleton instance of SoundManager.
     * 
     * @return the SoundManager instance
     */
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    
    /**
     * Plays an attack sound effect.
     */
    public void playAttackSound() {
        if (!soundEnabled) return;
        // Using system beep for now - can be replaced with actual audio file
        java.awt.Toolkit.getDefaultToolkit().beep();
    }
    
    /**
     * Plays a sound effect when receiving an attack.
     */
    public void playAttackReceivedSound() {
        if (!soundEnabled) return;
        // Using system beep for now - can be replaced with actual audio file
        java.awt.Toolkit.getDefaultToolkit().beep();
    }
    
    /**
     * Plays a line clear sound effect.
     */
    public void playLineClearSound() {
        if (!soundEnabled) return;
        // Using system beep for now - can be replaced with actual audio file
        java.awt.Toolkit.getDefaultToolkit().beep();
    }
    
    /**
     * Plays a combo sound effect.
     */
    public void playComboSound(int comboCount) {
        if (!soundEnabled) return;
        // Using system beep for now - can be replaced with actual audio file
        java.awt.Toolkit.getDefaultToolkit().beep();
    }
    
    /**
     * Plays a game start countdown sound.
     */
    public void playCountdownSound() {
        if (!soundEnabled) return;
        // Using system beep for now - can be replaced with actual audio file
        java.awt.Toolkit.getDefaultToolkit().beep();
    }
    
    /**
     * Plays a game start sound.
     */
    public void playGameStartSound() {
        if (!soundEnabled) return;
        // Using system beep for now - can be replaced with actual audio file
        java.awt.Toolkit.getDefaultToolkit().beep();
    }
    
    /**
     * Sets whether sounds are enabled.
     * 
     * @param enabled true to enable sounds, false to disable
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }
    
    /**
     * Gets whether sounds are enabled.
     * 
     * @return true if sounds are enabled, false otherwise
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    /**
     * Sets the volume level.
     * 
     * @param volume volume level (0.0 to 1.0)
     */
    public void setVolume(double volume) {
        this.volume = Math.max(0.0, Math.min(1.0, volume));
    }
    
    /**
     * Gets the volume level.
     * 
     * @return volume level (0.0 to 1.0)
     */
    public double getVolume() {
        return volume;
    }
}

