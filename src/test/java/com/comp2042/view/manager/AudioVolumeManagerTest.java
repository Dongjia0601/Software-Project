package com.comp2042.view.manager;

import com.comp2042.config.GameSettings;
import com.comp2042.service.audio.SoundManager;
import javafx.application.Platform;
import javafx.scene.control.Button;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AudioVolumeManagerTest {

    @TempDir
    Path tempDir;

    private AudioVolumeManager audioVolumeManager;
    private GameSettings settings;
    private Button muteButton;

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        resetGameSettingsSingleton();
        System.setProperty("user.home", tempDir.toString());
        settings = GameSettings.getInstance();
        settings.setMasterVolume(0.7);
        settings.setMusicVolume(0.5);
        settings.setSfxVolume(0.8);

        audioVolumeManager = new AudioVolumeManager(settings, SoundManager.getInstance());
        muteButton = new Button();
        audioVolumeManager.bindMuteButton(muteButton);
        audioVolumeManager.syncFromSettings();
    }

    private void resetGameSettingsSingleton() throws Exception {
        Field instanceField = GameSettings.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    @Test
    void toggleMuteSwitchesStates() {
        audioVolumeManager.toggleMute();
        assertEquals("UNMUTE (M)", muteButton.getText());
        assertEquals(0.0, settings.getMasterVolume(), 1e-6);

        audioVolumeManager.toggleMute();
        assertEquals("MUTE (M)", muteButton.getText());
        assertTrue(settings.getMasterVolume() > 0.0);
    }

    @Test
    void syncFromSettingsReflectsVolume() {
        settings.setMasterVolume(0.0);
        audioVolumeManager.syncFromSettings();
        assertEquals("UNMUTE (M)", muteButton.getText());

        settings.setMasterVolume(0.4);
        audioVolumeManager.syncFromSettings();
        assertEquals("MUTE (M)", muteButton.getText());
    }
}

