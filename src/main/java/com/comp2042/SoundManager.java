package com.comp2042;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Manages sound effects and background music for the game.
 * Provides methods to play various sound effects like attacks, line clears, etc.
 * Uses JavaFX Media API to load and play audio files from resources.
 * Optimized to prevent performance issues by throttling frequent sounds and properly disposing MediaPlayers.
 */
public class SoundManager {
    private static SoundManager instance;
    private boolean soundEnabled = true;
    private double volume = 0.5;
    private double musicVolume = 0.3; // Background music volume (lower than SFX)
    
    // Cache for loaded media files
    private Map<String, Media> mediaCache = new HashMap<>();
    
    // Background music player
    private MediaPlayer backgroundMusicPlayer;
    
    // Throttling for move sound (prevent too frequent playback)
    private AtomicLong lastMoveSoundTime = new AtomicLong(0);
    private static final long MOVE_SOUND_THROTTLE_MS = 120; 
    
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
     * Loads a media file from resources.
     * 
     * @param resourcePath the path to the audio file in resources
     * @return the loaded Media object, or null if loading fails
     */
    private Media loadMedia(String resourcePath) {
        if (mediaCache.containsKey(resourcePath)) {
            return mediaCache.get(resourcePath);
        }
        
        try {
            URI uri = getClass().getResource("/" + resourcePath).toURI();
            Media media = new Media(uri.toString());
            mediaCache.put(resourcePath, media);
            return media;
        } catch (Exception e) {
            System.err.println("Failed to load audio file: " + resourcePath + " - " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Plays a sound effect from a resource path.
     * Automatically disposes the MediaPlayer after playback to prevent memory leaks.
     * 
     * @param resourcePath the path to the audio file in resources
     */
    private void playSound(String resourcePath) {
        if (!soundEnabled) return;
        
        Media media = loadMedia(resourcePath);
        if (media == null) return;
        
        // Check if we're already on JavaFX thread to avoid unnecessary Platform.runLater() calls
        if (Platform.isFxApplicationThread()) {
            // Already on JavaFX thread, play directly
            playSoundOnFxThread(media);
        } else {
            // Not on JavaFX thread, use Platform.runLater()
            Platform.runLater(() -> playSoundOnFxThread(media));
        }
    }
    
    /**
     * Plays a sound effect on the JavaFX application thread.
     * This method should only be called from the JavaFX thread.
     * 
     * @param media the Media object to play
     */
    private void playSoundOnFxThread(Media media) {
        try {
            MediaPlayer player = new MediaPlayer(media);
            player.setVolume(volume);
            
            // Auto-dispose player when finished to prevent memory leaks
            player.setOnEndOfMedia(() -> {
                player.dispose();
            });
            
            // Handle errors
            player.setOnError(() -> {
                System.err.println("MediaPlayer error: " + player.getError());
                player.dispose();
            });
            
            player.play();
        } catch (Exception e) {
            System.err.println("Failed to play sound: " + e.getMessage());
        }
    }
    
    /**
     * Plays a sound effect with throttling to prevent too frequent playback.
     * Used for sounds that can be triggered very frequently (like move sounds).
     * 
     * @param resourcePath the path to the audio file in resources
     * @param throttleMs minimum milliseconds between plays
     */
    private void playSoundThrottled(String resourcePath, long throttleMs) {
        if (!soundEnabled) return;
        
        long currentTime = System.currentTimeMillis();
        long lastTime = lastMoveSoundTime.get();
        
        // Throttle: only play if enough time has passed
        if (currentTime - lastTime < throttleMs) {
            return; // Skip this playback
        }
        
        // Update last play time
        if (lastMoveSoundTime.compareAndSet(lastTime, currentTime)) {
            playSound(resourcePath);
        }
    }
    
    /**
     * Plays an attack sound effect (for VS mode).
     */
    public void playAttackSound() {
        // Using GarbageLinesSFX for attack sound
        playSound("audio/GarbageLinesSFX.mp3");
    }
    
    /**
     * Plays a sound effect when receiving an attack (for VS mode).
     */
    public void playAttackReceivedSound() {
        // Using WarningGarbageSFX for attack received sound
        playSound("audio/WarningGarbageSFX.mp3");
    }
    
    /**
     * Plays a line clear sound effect.
     */
    public void playLineClearSound() {
        playSound("audio/ClearLinesSFX.mp3");
    }
    
    /**
     * Plays a combo sound effect.
     * 
     * @param comboCount the combo count (currently uses same sound for all combos)
     */
    public void playComboSound(int comboCount) {
        // Using ClearLinesSFX for combo sound (can be enhanced later)
        playSound("audio/ClearLinesSFX.mp3");
    }
    
    /**
     * Plays a game start countdown sound.
     */
    public void playCountdownSound() {
        // Using ClickButtonSFX for countdown sound
        playSound("audio/ClickButtonSFX.mp3");
    }
    
    /**
     * Plays a game start sound.
     */
    public void playGameStartSound() {
        // Using ClickButtonSFX for game start sound
        playSound("audio/ClickButtonSFX.mp3");
    }
    
    /**
     * Plays a soft drop sound effect.
     * Uses throttling to prevent too frequent playback during rapid soft drops.
     */
    public void playSoftDropSound() {
        playSoundThrottled("audio/SoftDropSFX.mp3", MOVE_SOUND_THROTTLE_MS);
    }
    
    /**
     * Plays a hard drop sound effect.
     * Uses HardDropSFX for hard drop action.
     */
    public void playHardDropSound() {
        playSound("audio/HardDropSFX.mp3");
    }
    
    /**
     * Plays a hold brick sound effect.
     * Uses ClickButtonSFX for consistency with other game actions.
     */
    public void playHoldSound() {
        playSound("audio/ClickButtonSFX.mp3");
    }
    
    /**
     * Plays a move brick sound effect.
     * Uses throttling to prevent too frequent playback during rapid movements.
     * Uses ClickButtonSFX for consistency with other game actions.
     */
    public void playMoveSound() {
        playSoundThrottled("audio/ClickButtonSFX.mp3", MOVE_SOUND_THROTTLE_MS);
    }
    
    /**
     * Plays a rotate brick sound effect.
     * Uses throttling to prevent too frequent playback during rapid rotations.
     * Uses ClickButtonSFX for consistency with other game actions.
     */
    public void playRotateSound() {
        playSoundThrottled("audio/ClickButtonSFX.mp3", MOVE_SOUND_THROTTLE_MS);
    }
    
    /**
     * Plays a pause/resume sound effect.
     * Uses ClickButtonSFX for consistency with other game actions.
     */
    public void playPauseResumeSound() {
        playSound("audio/ClickButtonSFX.mp3");
    }
    
    /**
     * Plays a garbage lines sound effect (for VS mode).
     */
    public void playGarbageLinesSound() {
        playSound("audio/GarbageLinesSFX.mp3");
    }
    
    /**
     * Plays a warning garbage sound effect (for VS mode).
     */
    public void playWarningGarbageSound() {
        playSound("audio/WarningGarbageSFX.mp3");
    }
    
    /**
     * Plays a level win sound effect.
     */
    public void playLevelWinSound() {
        playSound("audio/LevelWinSFX.mp3");
    }
    
    /**
     * Plays a level failed sound effect.
     */
    public void playLevelFailedSound() {
        playSound("audio/LevelFailedSFX.mp3");
    }
    
    /**
     * Plays a button click sound effect.
     */
    public void playButtonClickSound() {
        playSound("audio/ClickButtonSFX.mp3");
    }
    
    /**
     * Starts playing background music in a loop.
     */
    public void playBackgroundMusic() {
        if (!soundEnabled) return;
        
        // Ensure audio operations run on JavaFX thread
        Platform.runLater(() -> {
            // Stop any existing background music directly (we're already on JavaFX thread)
            if (backgroundMusicPlayer != null) {
                backgroundMusicPlayer.stop();
                backgroundMusicPlayer.dispose();
                backgroundMusicPlayer = null;
            }
            
            Media media = loadMedia("audio/BackgroundMusic.mp3");
            if (media == null) return;
            
            try {
                backgroundMusicPlayer = new MediaPlayer(media);
                backgroundMusicPlayer.setVolume(musicVolume);
                backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop indefinitely
                
                // Handle errors
                backgroundMusicPlayer.setOnError(() -> {
                    System.err.println("Background music error: " + backgroundMusicPlayer.getError());
                });
                
                backgroundMusicPlayer.play();
            } catch (Exception e) {
                System.err.println("Failed to play background music: " + e.getMessage());
            }
        });
    }
    
    /**
     * Starts playing level background music in a loop.
     * Used when entering level mode (levels 1-5).
     */
    public void playLevelBackgroundMusic() {
        if (!soundEnabled) return;
        
        // Ensure audio operations run on JavaFX thread
        if (Platform.isFxApplicationThread()) {
            playLevelBackgroundMusicOnFxThread();
        } else {
            Platform.runLater(() -> playLevelBackgroundMusicOnFxThread());
        }
    }
    
    /**
     * Plays level background music on the JavaFX application thread.
     * This method should only be called from the JavaFX thread.
     */
    private void playLevelBackgroundMusicOnFxThread() {
        // Stop any existing background music
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
            backgroundMusicPlayer.dispose();
            backgroundMusicPlayer = null;
        }
        
        Media media = loadMedia("audio/LevelBackgroundMusic.mp3");
        if (media == null) return;
        
        try {
            backgroundMusicPlayer = new MediaPlayer(media);
            backgroundMusicPlayer.setVolume(musicVolume);
            backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop indefinitely
            
            // Handle errors
            backgroundMusicPlayer.setOnError(() -> {
                System.err.println("Level background music error: " + backgroundMusicPlayer.getError());
            });
            
            backgroundMusicPlayer.play();
        } catch (Exception e) {
            System.err.println("Failed to play level background music: " + e.getMessage());
        }
    }
    
    /**
     * Stops the background music.
     */
    public void stopBackgroundMusic() {
        Platform.runLater(() -> {
            if (backgroundMusicPlayer != null) {
                backgroundMusicPlayer.stop();
                backgroundMusicPlayer.dispose();
                backgroundMusicPlayer = null;
            }
        });
    }
    
    /**
     * Pauses the background music.
     */
    public void pauseBackgroundMusic() {
        Platform.runLater(() -> {
            if (backgroundMusicPlayer != null) {
                backgroundMusicPlayer.pause();
            }
        });
    }
    
    /**
     * Resumes the background music.
     */
    public void resumeBackgroundMusic() {
        Platform.runLater(() -> {
            if (backgroundMusicPlayer != null && soundEnabled) {
                backgroundMusicPlayer.play();
            }
        });
    }
    
    /**
     * Sets whether sounds are enabled.
     * 
     * @param enabled true to enable sounds, false to disable
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            stopBackgroundMusic();
        } else if (backgroundMusicPlayer == null) {
            playBackgroundMusic();
        } else {
            resumeBackgroundMusic();
        }
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
     * Sets the volume level for sound effects.
     * 
     * @param volume volume level (0.0 to 1.0)
     */
    public void setVolume(double volume) {
        this.volume = Math.max(0.0, Math.min(1.0, volume));
    }
    
    /**
     * Gets the volume level for sound effects.
     * 
     * @return volume level (0.0 to 1.0)
     */
    public double getVolume() {
        return volume;
    }
    
    /**
     * Sets the volume level for background music.
     * 
     * @param musicVolume music volume level (0.0 to 1.0)
     */
    public void setMusicVolume(double musicVolume) {
        this.musicVolume = Math.max(0.0, Math.min(1.0, musicVolume));
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.setVolume(this.musicVolume);
        }
    }
    
    /**
     * Gets the volume level for background music.
     * 
     * @return music volume level (0.0 to 1.0)
     */
    public double getMusicVolume() {
        return musicVolume;
    }
}

