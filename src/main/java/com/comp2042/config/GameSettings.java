package com.comp2042.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Centralized configuration manager for game settings.
 * Handles persistence of user preferences including audio volumes, difficulty,
 * and piece randomization algorithm to a properties file in the user's home directory.
 * 
 * <p>Implements the Singleton pattern to provide a single, globally accessible
 * settings instance throughout the application lifecycle. Settings are automatically
 * loaded on initialization and can be persisted via {@link #saveSettings()}.
 * 
 * <p>Settings are stored in {@code ~/.tetris/tetris_settings.properties} for
 * cross-platform compatibility.
 * 
 * @author Dong, Jia.
 */
public class GameSettings {
    
    private static GameSettings instance;
    private static final String SETTINGS_DIR = getSettingsDirectory();
    private static final String SETTINGS_FILE_NAME = "tetris_settings.properties";
    
    /**
     * Gets the settings directory path, with fallback if user.home is not available.
     * 
     * @return the settings directory path
     */
    private static String getSettingsDirectory() {
        String userHome = System.getProperty("user.home");
        if (userHome == null || userHome.isEmpty()) {
            // Fallback to current directory if user.home is not available
            System.err.println("Warning: user.home system property not available, using current directory for settings");
            return "." + File.separator + ".tetris";
        }
        return userHome + File.separator + ".tetris";
    }
    
    /**
     * Retrieves the settings file path in the user's home directory.
     * Automatically creates the {@code .tetris} directory if it does not exist.
     * 
     * @return the settings file located at {@code ~/.tetris/tetris_settings.properties}
     */
    private File getSettingsFile() {
        File dir = new File(SETTINGS_DIR);
        if (!dir.exists()) {
            // Best-effort directory creation for cross-platform user config storage
            if (!dir.mkdirs()) {
                System.err.println("Warning: Failed to create settings directory: " + SETTINGS_DIR);
            }
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
    
    private static final double DEFAULT_MASTER_VOLUME = 0.7;
    private static final double DEFAULT_MUSIC_VOLUME = 0.5;
    private static final double DEFAULT_SFX_VOLUME = 0.8;
    private static final int DEFAULT_DIFFICULTY = 1;
    private static final String DEFAULT_RANDOMIZER = "seven_bag";
    
    /**
     * Private constructor enforcing Singleton pattern.
     * Initializes all settings to default values and attempts to load
     * previously saved settings from the properties file.
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