package com.comp2042.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GameSettings.
 * Tests Singleton pattern, default values, getters/setters, value clamping, and file persistence.
 */
@DisplayName("Game Settings Tests")
class GameSettingsTest {

    private GameSettings settings;
    private File settingsFile;

    @BeforeEach
    void setUp() {
        settings = GameSettings.getInstance();
        String settingsDir = System.getProperty("user.home") + File.separator + ".tetris";
        settingsFile = new File(settingsDir, "tetris_settings.properties");
    }

    @Test
    @DisplayName("getInstance: Returns singleton instance")
    void testGetInstance() {
        GameSettings instance1 = GameSettings.getInstance();
        GameSettings instance2 = GameSettings.getInstance();
        
        assertSame(instance1, instance2, "Should return the same singleton instance");
    }

    @Test
    @DisplayName("resetToDefaults: Resets all settings to default values")
    void testResetToDefaults() {
        // Change some values
        settings.setMasterVolume(0.9);
        settings.setMusicVolume(0.3);
        settings.setDefaultDifficulty(5);
        
        settings.resetToDefaults();
        
        assertEquals(0.7, settings.getMasterVolume(), 0.001);
        assertEquals(0.5, settings.getMusicVolume(), 0.001);
        assertEquals(0.8, settings.getSfxVolume(), 0.001);
        assertEquals(1, settings.getDefaultDifficulty());
        assertEquals("seven_bag", settings.getPieceRandomizer());
    }

    @Test
    @DisplayName("getMasterVolume: Returns current master volume")
    void testGetMasterVolume() {
        double volume = settings.getMasterVolume();
        assertTrue(volume >= 0.0 && volume <= 1.0, "Volume should be in range [0.0, 1.0]");
    }

    @Test
    @DisplayName("setMasterVolume: Sets volume and clamps to valid range")
    void testSetMasterVolume() {
        settings.setMasterVolume(0.5);
        assertEquals(0.5, settings.getMasterVolume(), 0.001);
        
        // Test clamping
        settings.setMasterVolume(-0.1);
        assertEquals(0.0, settings.getMasterVolume(), 0.001, "Should clamp to 0.0");
        
        settings.setMasterVolume(1.5);
        assertEquals(1.0, settings.getMasterVolume(), 0.001, "Should clamp to 1.0");
    }

    @Test
    @DisplayName("getMusicVolume: Returns current music volume")
    void testGetMusicVolume() {
        double volume = settings.getMusicVolume();
        assertTrue(volume >= 0.0 && volume <= 1.0);
    }

    @Test
    @DisplayName("setMusicVolume: Sets volume and clamps to valid range")
    void testSetMusicVolume() {
        settings.setMusicVolume(0.6);
        assertEquals(0.6, settings.getMusicVolume(), 0.001);
        
        settings.setMusicVolume(-0.5);
        assertEquals(0.0, settings.getMusicVolume(), 0.001);
        
        settings.setMusicVolume(2.0);
        assertEquals(1.0, settings.getMusicVolume(), 0.001);
    }

    @Test
    @DisplayName("getSfxVolume: Returns current SFX volume")
    void testGetSfxVolume() {
        double volume = settings.getSfxVolume();
        assertTrue(volume >= 0.0 && volume <= 1.0);
    }

    @Test
    @DisplayName("setSfxVolume: Sets volume and clamps to valid range")
    void testSetSfxVolume() {
        settings.setSfxVolume(0.7);
        assertEquals(0.7, settings.getSfxVolume(), 0.001);
        
        settings.setSfxVolume(-1.0);
        assertEquals(0.0, settings.getSfxVolume(), 0.001);
        
        settings.setSfxVolume(5.0);
        assertEquals(1.0, settings.getSfxVolume(), 0.001);
    }

    @Test
    @DisplayName("getDefaultDifficulty: Returns current difficulty")
    void testGetDefaultDifficulty() {
        int difficulty = settings.getDefaultDifficulty();
        assertTrue(difficulty >= 1 && difficulty <= 10);
    }

    @Test
    @DisplayName("setDefaultDifficulty: Sets difficulty and clamps to valid range")
    void testSetDefaultDifficulty() {
        settings.setDefaultDifficulty(5);
        assertEquals(5, settings.getDefaultDifficulty());
        
        settings.setDefaultDifficulty(0);
        assertEquals(1, settings.getDefaultDifficulty(), "Should clamp to 1");
        
        settings.setDefaultDifficulty(15);
        assertEquals(10, settings.getDefaultDifficulty(), "Should clamp to 10");
    }

    @Test
    @DisplayName("getPieceRandomizer: Returns current randomizer")
    void testGetPieceRandomizer() {
        String randomizer = settings.getPieceRandomizer();
        assertTrue("seven_bag".equals(randomizer) || "pure_random".equals(randomizer));
    }

    @Test
    @DisplayName("setPieceRandomizer: Sets randomizer with validation")
    void testSetPieceRandomizer() {
        settings.setPieceRandomizer("pure_random");
        assertEquals("pure_random", settings.getPieceRandomizer());
        
        settings.setPieceRandomizer("seven_bag");
        assertEquals("seven_bag", settings.getPieceRandomizer());
        
        // Invalid values should default to seven_bag
        settings.setPieceRandomizer("invalid");
        assertEquals("seven_bag", settings.getPieceRandomizer());
        
        settings.setPieceRandomizer(null);
        assertEquals("seven_bag", settings.getPieceRandomizer());
    }

    @Test
    @DisplayName("setPieceRandomizer: Case insensitive")
    void testSetPieceRandomizerCaseInsensitive() {
        settings.setPieceRandomizer("PURE_RANDOM");
        assertEquals("pure_random", settings.getPieceRandomizer());
        
        settings.setPieceRandomizer("SEVEN_BAG");
        assertEquals("seven_bag", settings.getPieceRandomizer());
    }

    @Test
    @DisplayName("saveSettings: Saves settings to file")
    void testSaveSettings() {
        // Set some values
        settings.setMasterVolume(0.8);
        settings.setMusicVolume(0.6);
        settings.setSfxVolume(0.9);
        settings.setDefaultDifficulty(3);
        settings.setPieceRandomizer("pure_random");
        
        boolean success = settings.saveSettings();
        
        assertTrue(success, "Save should succeed");
        assertTrue(settingsFile.exists(), "Settings file should exist after save");
    }

    @Test
    @DisplayName("loadSettings: Loads settings from file")
    void testLoadSettings() {
        // Save settings first
        settings.setMasterVolume(0.85);
        settings.setMusicVolume(0.65);
        settings.setSfxVolume(0.95);
        settings.setDefaultDifficulty(4);
        settings.setPieceRandomizer("pure_random");
        settings.saveSettings();
        
        // Reset to defaults
        settings.resetToDefaults();
        assertEquals(0.7, settings.getMasterVolume(), 0.001);
        
        // Load settings
        boolean success = settings.loadSettings();
        
        assertTrue(success, "Load should succeed if file exists");
    }

    @Test
    @DisplayName("loadSettings: Returns false if file does not exist")
    void testLoadSettingsNoFile() {
        // Delete file if it exists
        if (settingsFile.exists()) {
            settingsFile.delete();
        }
        
        boolean success = settings.loadSettings();
        
        assertFalse(success, "Should return false if file does not exist");
    }

    @Test
    @DisplayName("Persistence: Save and load round-trip")
    void testPersistenceRoundTrip() {
        // Set values
        double masterVol = 0.75;
        double musicVol = 0.55;
        double sfxVol = 0.85;
        int difficulty = 2;
        String randomizer = "pure_random";
        
        settings.setMasterVolume(masterVol);
        settings.setMusicVolume(musicVol);
        settings.setSfxVolume(sfxVol);
        settings.setDefaultDifficulty(difficulty);
        settings.setPieceRandomizer(randomizer);
        
        // Save
        assertTrue(settings.saveSettings());
        
        // Reset
        settings.resetToDefaults();
        
        // Load
        assertTrue(settings.loadSettings());
        
        // Verify values
        assertEquals(masterVol, settings.getMasterVolume(), 0.001);
        assertEquals(musicVol, settings.getMusicVolume(), 0.001);
        assertEquals(sfxVol, settings.getSfxVolume(), 0.001);
        assertEquals(difficulty, settings.getDefaultDifficulty());
        assertEquals(randomizer, settings.getPieceRandomizer());
    }

    @Test
    @DisplayName("File format: Verifies properties file structure")
    void testFileFormat() throws IOException {
        // Save settings
        settings.setMasterVolume(0.8);
        settings.setMusicVolume(0.6);
        settings.setSfxVolume(0.9);
        settings.setDefaultDifficulty(3);
        settings.setPieceRandomizer("pure_random");
        settings.saveSettings();
        
        // Read file
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(settingsFile)) {
            props.load(in);
        }
        
        // Verify all properties are present
        assertTrue(props.containsKey("masterVolume"));
        assertTrue(props.containsKey("musicVolume"));
        assertTrue(props.containsKey("sfxVolume"));
        assertTrue(props.containsKey("defaultDifficulty"));
        assertTrue(props.containsKey("pieceRandomizer"));
        
        // Verify values
        assertEquals("0.8", props.getProperty("masterVolume"));
        assertEquals("0.6", props.getProperty("musicVolume"));
        assertEquals("0.9", props.getProperty("sfxVolume"));
        assertEquals("3", props.getProperty("defaultDifficulty"));
        assertEquals("pure_random", props.getProperty("pieceRandomizer"));
    }
}

