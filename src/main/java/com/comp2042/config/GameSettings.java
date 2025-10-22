package com.comp2042.config;

import java.io.*;
import java.util.Properties;

/**
 * Configuration management for the Tetris game.
 * Handles loading and saving of game settings including audio, graphics,
 * and gameplay preferences.
 * 
 * <p>This class provides a centralized way to manage game configuration
 * with persistent storage in a properties file.</p>
 * 
 * @author Dong, Jia.
 */
public class GameSettings {
    
    private static final String CONFIG_FILE = "tetris.properties";
    private static final String CONFIG_DIR = System.getProperty("user.home") + File.separator + ".tetris";
    
    private static GameSettings instance;
    private Properties properties;
    
    // Default settings
    private static final String DEFAULT_MUSIC_VOLUME = "0.7";
    private static final String DEFAULT_SFX_VOLUME = "0.8";
    private static final String DEFAULT_GHOST_PIECE = "true";
    private static final String DEFAULT_THEME = "cosmic";
    
    /**
     * Private constructor for singleton pattern.
     */
    private GameSettings() {
        properties = new Properties();
        loadSettings();
    }
    
    /**
     * Gets the singleton instance of GameSettings.
     * 
     * @return the GameSettings instance
     */
    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }
    
    /**
     * Loads settings from the configuration file.
     * Creates default settings if the file doesn't exist.
     */
    private void loadSettings() {
        File configFile = new File(CONFIG_DIR, CONFIG_FILE);
        
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
            } catch (IOException e) {
                System.err.println("Error loading settings: " + e.getMessage());
                setDefaultSettings();
            }
        } else {
            setDefaultSettings();
        }
    }
    
    /**
     * Saves current settings to the configuration file.
     */
    public void saveSettings() {
        File configDir = new File(CONFIG_DIR);
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        
        File configFile = new File(CONFIG_DIR, CONFIG_FILE);
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            properties.store(fos, "Tetris Game Settings");
        } catch (IOException e) {
            System.err.println("Error saving settings: " + e.getMessage());
        }
    }
    
    /**
     * Sets default settings.
     */
    private void setDefaultSettings() {
        properties.setProperty("music.volume", DEFAULT_MUSIC_VOLUME);
        properties.setProperty("sfx.volume", DEFAULT_SFX_VOLUME);
        properties.setProperty("gameplay.ghost_piece", DEFAULT_GHOST_PIECE);
        properties.setProperty("theme.name", DEFAULT_THEME);
    }
    
    // Music settings
    public double getMusicVolume() {
        return Double.parseDouble(properties.getProperty("music.volume", DEFAULT_MUSIC_VOLUME));
    }
    
    public void setMusicVolume(double volume) {
        properties.setProperty("music.volume", String.valueOf(Math.max(0.0, Math.min(1.0, volume))));
    }
    
    // Sound effects settings
    public double getSfxVolume() {
        return Double.parseDouble(properties.getProperty("sfx.volume", DEFAULT_SFX_VOLUME));
    }
    
    public void setSfxVolume(double volume) {
        properties.setProperty("sfx.volume", String.valueOf(Math.max(0.0, Math.min(1.0, volume))));
    }
    
    // Gameplay settings
    public boolean isGhostPieceEnabled() {
        return Boolean.parseBoolean(properties.getProperty("gameplay.ghost_piece", DEFAULT_GHOST_PIECE));
    }
    
    public void setGhostPieceEnabled(boolean enabled) {
        properties.setProperty("gameplay.ghost_piece", String.valueOf(enabled));
    }
    
    // Theme settings
    public String getTheme() {
        return properties.getProperty("theme.name", DEFAULT_THEME);
    }
    
    public void setTheme(String theme) {
        properties.setProperty("theme.name", theme);
    }
    
    /**
     * Resets all settings to default values.
     */
    public void resetToDefaults() {
        setDefaultSettings();
        saveSettings();
    }
    
    /**
     * Gets a string property with a default value.
     * 
     * @param key the property key
     * @param defaultValue the default value
     * @return the property value or default if not found
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Sets a string property.
     * 
     * @param key the property key
     * @param value the property value
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
}
