package com.comp2042.view.manager;

import com.comp2042.config.GameSettings;
import com.comp2042.service.audio.SoundManager;
import javafx.scene.control.Button;

/**
 * Handles audio-related UI actions (mute/unmute) and synchronises settings.
 */
public class AudioVolumeManager {

    private final GameSettings settings;
    private final SoundManager soundManager;

    private Button muteButton;
    private double previousVolume = 0.7;
    private boolean muted;

    public AudioVolumeManager(GameSettings settings, SoundManager soundManager) {
        this.settings = settings;
        this.soundManager = soundManager;
        syncFromSettings();
    }

    public void bindMuteButton(Button muteButton) {
        this.muteButton = muteButton;
        updateMuteButtonLabel();
    }

    /**
     * Toggles master volume mute state and persists the change.
     */
    public void toggleMute() {
        if (muted) {
            double restoredVolume = previousVolume <= 0 ? 0.7 : previousVolume;
            settings.setMasterVolume(restoredVolume);
            soundManager.setMasterVolume(restoredVolume);
            muted = false;
        } else {
            previousVolume = settings.getMasterVolume();
            settings.setMasterVolume(0.0);
            soundManager.setMasterVolume(0.0);
            muted = true;
        }
        settings.saveSettings();
        updateMuteButtonLabel();
    }

    /**
     * Re-syncs mute button state after settings dialog closes.
     */
    public void syncFromSettings() {
        double currentVolume = settings.getMasterVolume();
        muted = currentVolume <= 0.0;
        if (!muted) {
            previousVolume = currentVolume;
        }
        soundManager.setMasterVolume(currentVolume);
        updateMuteButtonLabel();
    }

    public boolean isMuted() {
        return muted;
    }

    private void updateMuteButtonLabel() {
        if (muteButton == null) {
            return;
        }
        muteButton.setText(muted ? "UNMUTE (M)" : "MUTE (M)");
    }
}

