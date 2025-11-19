package com.comp2042.config;

import java.io.*;
import java.util.Properties;

/**
 * Game settings manager that handles all configurable game options.
 * Supports saving and loading settings from a properties file.
 * 
 * <p>This class follows the Singleton pattern to ensure only one
 * settings instance exists throughout the application.
 * 
 * @author Dong, Jia.
 */
public class GameSettings {
    
    private static GameSettings instance;
    private static final String SETTINGS_DIR = System.getProperty("user.home") + File.separator + ".tetris";
    private static final String SETTINGS_FILE_NAME = "tetris_settings.properties";
    
    /**
     * Gets the settings file path in the user's home directory.
     * Creates the .tetris directory if it doesn't exist.
     * 
     * @return the settings file in ~/.tetris/
     */
    private File getSettingsFile() {
        File dir = new File(SETTINGS_DIR);
        if (!dir.exists()) {
            // Best-effort directory creation for cross-platform user config storage
            dir.mkdirs();
        }
        return new File(dir, SETTINGS_FILE_NAME);
    }
    
    /** Master volume level (0.0-1.0) */
    private double masterVolume;
    /** Background music volume (0.0-1.0) */
    private double musicVolume;
    /** Sound effects volume (0.0-1.0) */
    private double sfxVolume;
    
    /** Default difficulty level (1-10) */
    private int defaultDifficulty;
    /** Piece randomization algorithm: "seven_bag" or "pure_random" */
    private String pieceRandomizer;
    
    // Default values
    private static final double DEFAULT_MASTER_VOLUME = 0.7;
    private static final double DEFAULT_MUSIC_VOLUME = 0.5;
    private static final double DEFAULT_SFX_VOLUME = 0.8;
    private static final int DEFAULT_DIFFICULTY = 1;
    private static final String DEFAULT_RANDOMIZER = "seven_bag";
    
    /**
     * Private constructor for Singleton pattern.
     * Initializes settings with defaults and attempts to load from file.
     */
    private GameSettings() {
        resetToDefaults();
        loadSettings();
    }
    
    /**
     * Gets the singleton instance of GameSettings.
     * Uses synchronized method to ensure thread-safe lazy initialization.
     * 
     * @return the game settings instance
     */
    public static synchronized GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }
    
    /**
     * Resets all settings to their default values.
     */
    public void resetToDefaults() {
        this.masterVolume = DEFAULT_MASTER_VOLUME;
        this.musicVolume = DEFAULT_MUSIC_VOLUME;
        this.sfxVolume = DEFAULT_SFX_VOLUME;
        this.defaultDifficulty = DEFAULT_DIFFICULTY;
        this.pieceRandomizer = DEFAULT_RANDOMIZER;
    }
    
    /**
     * Persists current settings to the properties file.
     * 
     * @return true if save was successful, false otherwise
     */
    public boolean saveSettings() {
        Properties props = new Properties();
        
        props.setProperty("masterVolume", String.valueOf(masterVolume));
        props.setProperty("musicVolume", String.valueOf(musicVolume));
        props.setProperty("sfxVolume", String.valueOf(sfxVolume));
        props.setProperty("defaultDifficulty", String.valueOf(defaultDifficulty));
        props.setProperty("pieceRandomizer", pieceRandomizer);
        
        File settingsFile = getSettingsFile();
        try (FileOutputStream out = new FileOutputStream(settingsFile)) {
            props.store(out, "Tetris Game Settings");
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save settings: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Loads settings from the properties file.
     * Falls back to defaults if file is missing or corrupted.
     * 
     * @return true if load was successful, false otherwise
     */
    public boolean loadSettings() {
        File file = getSettingsFile();
        if (!file.exists()) {
            return false;
        }
        
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(file)) {
            props.load(in);
            
            masterVolume = Double.parseDouble(props.getProperty("masterVolume", String.valueOf(DEFAULT_MASTER_VOLUME)));
            musicVolume = Double.parseDouble(props.getProperty("musicVolume", String.valueOf(DEFAULT_MUSIC_VOLUME)));
            sfxVolume = Double.parseDouble(props.getProperty("sfxVolume", String.valueOf(DEFAULT_SFX_VOLUME)));
            defaultDifficulty = Integer.parseInt(props.getProperty("defaultDifficulty", String.valueOf(DEFAULT_DIFFICULTY)));
            pieceRandomizer = props.getProperty("pieceRandomizer", DEFAULT_RANDOMIZER);
            
            return true;
        } catch (IOException | NumberFormatException e) {
            System.err.println("Failed to load settings: " + e.getMessage());
            resetToDefaults();
            return false;
        }
    }
    
    /**
     * Gets the master volume level.
     * 
     * @return volume level between 0.0 and 1.0
     */
    public double getMasterVolume() {
        return masterVolume;
    }
    
    /**
     * Sets the master volume level. Value is clamped to [0.0, 1.0].
     * 
     * @param masterVolume the volume level to set
     */
    public void setMasterVolume(double masterVolume) {
        this.masterVolume = Math.max(0.0, Math.min(1.0, masterVolume));
    }
    
    /**
     * Gets the background music volume level.
     * 
     * @return volume level between 0.0 and 1.0
     */
    public double getMusicVolume() {
        return musicVolume;
    }
    
    /**
     * Sets the music volume level. Value is clamped to [0.0, 1.0].
     * 
     * @param musicVolume the volume level to set
     */
    public void setMusicVolume(double musicVolume) {
        this.musicVolume = Math.max(0.0, Math.min(1.0, musicVolume));
    }
    
    /**
     * Gets the sound effects volume level.
     * 
     * @return volume level between 0.0 and 1.0
     */
    public double getSfxVolume() {
        return sfxVolume;
    }
    
    /**
     * Sets the sound effects volume level. Value is clamped to [0.0, 1.0].
     * 
     * @param sfxVolume the volume level to set
     */
    public void setSfxVolume(double sfxVolume) {
        this.sfxVolume = Math.max(0.0, Math.min(1.0, sfxVolume));
    }
    
    /**
     * Gets the default difficulty level.
     * 
     * @return difficulty level between 1 and 10
     */
    public int getDefaultDifficulty() {
        return defaultDifficulty;
    }
    
    /**
     * Sets the default difficulty level. Value is clamped to [1, 10].
     * 
     * @param defaultDifficulty the difficulty level to set
     */
    public void setDefaultDifficulty(int defaultDifficulty) {
        this.defaultDifficulty = Math.max(1, Math.min(10, defaultDifficulty));
    }

    /**
     * Gets the piece randomization algorithm.
     * 
     * @return "seven_bag" or "pure_random"
     */
    public String getPieceRandomizer() {
        return pieceRandomizer;
    }

    /**
     * Sets the piece randomization algorithm.
     * Only "seven_bag" and "pure_random" are valid; defaults to "seven_bag" otherwise.
     * 
     * @param pieceRandomizer the algorithm to use
     */
    public void setPieceRandomizer(String pieceRandomizer) {
        if (pieceRandomizer == null) {
            this.pieceRandomizer = DEFAULT_RANDOMIZER;
        } else {
            String v = pieceRandomizer.toLowerCase();
            this.pieceRandomizer = ("pure_random".equals(v) ? "pure_random" : "seven_bag");
        }
    }
}