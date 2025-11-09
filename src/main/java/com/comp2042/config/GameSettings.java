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
    
    private File getSettingsFile() {
        File dir = new File(SETTINGS_DIR);
        if (!dir.exists()) {
            // Best-effort directory creation for cross-platform user config storage
            dir.mkdirs();
        }
        return new File(dir, SETTINGS_FILE_NAME);
    }
    
    // Audio settings
    private double masterVolume;
    private double musicVolume;
    private double sfxVolume;
    
    // Gameplay settings
    private int defaultDifficulty;
    private String pieceRandomizer; // "seven_bag" or "pure_random"
    
    // Default values
    private static final double DEFAULT_MASTER_VOLUME = 0.7;
    private static final double DEFAULT_MUSIC_VOLUME = 0.5;
    private static final double DEFAULT_SFX_VOLUME = 0.8;
    private static final int DEFAULT_DIFFICULTY = 1;
    private static final String DEFAULT_RANDOMIZER = "seven_bag";
    
    /**
     * Private constructor for Singleton pattern.
     */
    private GameSettings() {
        resetToDefaults();
        loadSettings();
    }
    
    /**
     * Gets the singleton instance of GameSettings.
     * @return the game settings instance
     */
    public static GameSettings getInstance() {
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
     * Saves current settings to file.
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
     * Loads settings from file.
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
    
    // Getters and setters
    
    public double getMasterVolume() {
        return masterVolume;
    }
    
    public void setMasterVolume(double masterVolume) {
        this.masterVolume = Math.max(0.0, Math.min(1.0, masterVolume));
    }
    
    public double getMusicVolume() {
        return musicVolume;
    }
    
    public void setMusicVolume(double musicVolume) {
        this.musicVolume = Math.max(0.0, Math.min(1.0, musicVolume));
    }
    
    public double getSfxVolume() {
        return sfxVolume;
    }
    
    public void setSfxVolume(double sfxVolume) {
        this.sfxVolume = Math.max(0.0, Math.min(1.0, sfxVolume));
    }
    
    public int getDefaultDifficulty() {
        return defaultDifficulty;
    }
    
    public void setDefaultDifficulty(int defaultDifficulty) {
        this.defaultDifficulty = Math.max(1, Math.min(10, defaultDifficulty));
    }

    public String getPieceRandomizer() {
        return pieceRandomizer;
    }

    public void setPieceRandomizer(String pieceRandomizer) {
        if (pieceRandomizer == null) {
            this.pieceRandomizer = DEFAULT_RANDOMIZER;
        } else {
            String v = pieceRandomizer.toLowerCase();
            this.pieceRandomizer = ("pure_random".equals(v) ? "pure_random" : "seven_bag");
        }
    }
}