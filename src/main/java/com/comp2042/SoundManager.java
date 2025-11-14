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
 * 
 * <p>Supports different background music for different game modes:
 * <ul>
 *   <li>Main Menu: MainMenuBGM.mp3</li>
 *   <li>Endless Mode: EndlessBGM.mp3</li>
 *   <li>Two-Player Mode: TwoPlayerBGM.mp3</li>
 *   <li>Level 1 & 3: Level13BGM.mp3</li>
 *   <li>Level 2 & 5: Level25BGM.mp3</li>
 *   <li>Level 4: Level4BGM.mp3</li>
 * </ul>
 * </p>
 * 
 * @author Dong, Jia.
 */
public class SoundManager {
    private static SoundManager instance;
    private boolean soundEnabled = true;
    private double masterVolume = 1.0; // Master volume (affects all audio)
    private double sfxVolume = 0.8; // Sound effects volume (before master)
    private double musicVolume = 0.5; // Background music volume (before master)
    
    // Cache for loaded media files
    private Map<String, Media> mediaCache = new HashMap<>();
    
    // Background music player
    private MediaPlayer backgroundMusicPlayer;
    
    // Countdown sound player (to prevent duplicate playback)
    private MediaPlayer countdownSoundPlayer;
    private boolean isCountdownSoundPlaying = false;
    
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
            // Apply master volume and SFX volume: final volume = masterVolume * sfxVolume
            player.setVolume(masterVolume * sfxVolume);
            
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
     * Plays a line clear sound effect based on the number of lines cleared.
     * Uses different sound effects for 1, 2, 3, or 4 lines cleared.
     * 
     * @param linesCleared the number of lines cleared (1-4)
     * @author Dong, Jia.
     */
    public void playLineClearSound(int linesCleared) {
        String soundFile;
        switch (linesCleared) {
            case 1:
                soundFile = "audio/CLear1SFX.mp3";
                break;
            case 2:
                soundFile = "audio/Clear2SFX.mp3";
                break;
            case 3:
                soundFile = "audio/CLear3SFX.mp3";
                break;
            case 4:
                soundFile = "audio/CLear4SFX.mp3";
                break;
            default:
                // Fallback to single line clear sound
                soundFile = "audio/CLear1SFX.mp3";
                break;
        }
        playSound(soundFile);
    }
    
    /**
     * Plays a line clear sound effect (default - single line).
     * Maintains backward compatibility.
     */
    public void playLineClearSound() {
        playLineClearSound(1);
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
     * Uses a dedicated MediaPlayer to prevent duplicate playback.
     */
    public void playCountdownSound() {
        if (!soundEnabled) return;
        
        // Stop and dispose previous countdown sound if still playing
        if (countdownSoundPlayer != null) {
            countdownSoundPlayer.stop();
            countdownSoundPlayer.dispose();
            countdownSoundPlayer = null;
        }
        
        // Reset flag when stopping previous sound
        isCountdownSoundPlaying = false;
        
        Media media = loadMedia("audio/RaceCountdownSFX.mp3");
        if (media == null) return;
        
        // Set flag to prevent duplicate calls
        isCountdownSoundPlaying = true;
        
        // Check if we're already on JavaFX thread
        if (Platform.isFxApplicationThread()) {
            playCountdownSoundOnFxThread(media);
        } else {
            Platform.runLater(() -> playCountdownSoundOnFxThread(media));
        }
    }
    
    /**
     * Plays countdown sound on the JavaFX application thread.
     * This method should only be called from the JavaFX thread.
     * 
     * @param media the Media object to play
     */
    private void playCountdownSoundOnFxThread(Media media) {
        try {
            countdownSoundPlayer = new MediaPlayer(media);
            updateCountdownPlayerVolume();
            
            // Auto-dispose player when finished
            countdownSoundPlayer.setOnEndOfMedia(() -> {
                if (countdownSoundPlayer != null) {
                    countdownSoundPlayer.dispose();
                    countdownSoundPlayer = null;
                }
                isCountdownSoundPlaying = false;
            });
            
            // Handle errors
            countdownSoundPlayer.setOnError(() -> {
                System.err.println("Countdown sound error: " + countdownSoundPlayer.getError());
                if (countdownSoundPlayer != null) {
                    countdownSoundPlayer.dispose();
                    countdownSoundPlayer = null;
                }
                isCountdownSoundPlaying = false;
            });
            
            countdownSoundPlayer.play();
        } catch (Exception e) {
            System.err.println("Failed to play countdown sound: " + e.getMessage());
            if (countdownSoundPlayer != null) {
                countdownSoundPlayer.dispose();
                countdownSoundPlayer = null;
            }
            isCountdownSoundPlaying = false;
        }
    }
    
    /**
     * Stops the countdown sound if it's currently playing.
     * Called when countdown ends to prevent sound from continuing into gameplay.
     */
    public void stopCountdownSound() {
        if (countdownSoundPlayer != null) {
            countdownSoundPlayer.stop();
            countdownSoundPlayer.dispose();
            countdownSoundPlayer = null;
        }
        isCountdownSoundPlaying = false;
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
     * Uses HoldSFX for hold actions.
     * 
     * @author Dong, Jia.
     */
    public void playHoldSound() {
        playSound("audio/HoldSFX.mp3");
    }
    
    /**
     * Plays a move brick sound effect.
     * Uses throttling to prevent too frequent playback during rapid movements.
     * Uses MoveBrickSFX for move actions.
     * 
     * @author Dong, Jia.
     */
    public void playMoveSound() {
        playSoundThrottled("audio/MoveBrickSFX.mp3", MOVE_SOUND_THROTTLE_MS);
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
     * Uses GameWinSFX for level completion.
     * 
     * @author Dong, Jia.
     */
    public void playLevelWinSound() {
        playSound("audio/GameWinSFX.mp3");
    }
    
    /**
     * Plays a level failed sound effect.
     * 
     * @author Dong, Jia.
     */
    public void playLevelFailedSound() {
        playSound("audio/LevelFailedSFX.mp3");
    }
    
    /**
     * Plays a game over sound effect.
     * Used for general game over scenarios.
     * 
     * @author Dong, Jia.
     */
    public void playGameOverSound() {
        playSound("audio/GameOverSFX.mp3");
    }
    
    /**
     * Plays an endless mode game over sound effect.
     * Used specifically for endless mode game over.
     * 
     * @author Dong, Jia.
     */
    public void playEndlessGameOverSound() {
        playSound("audio/EndlessGameOverSFX.mp3");
    }
    
    /**
     * Plays a button click sound effect.
     */
    public void playButtonClickSound() {
        playSound("audio/ClickButtonSFX.mp3");
    }
    
    /**
     * Starts playing main menu background music in a loop.
     * Used when the main menu is displayed.
     * 
     * @author Dong, Jia.
     */
    public void playMainMenuBackgroundMusic() {
        playBackgroundMusicFromFile("audio/MainMenuBGM.mp3");
    }
    
    /**
     * Starts playing endless mode background music in a loop.
     * Used when entering endless mode.
     * 
     * @author Dong, Jia.
     */
    public void playEndlessBackgroundMusic() {
        playBackgroundMusicFromFile("audio/EndlessBGM.mp3");
    }
    
    /**
     * Starts playing two-player mode background music in a loop.
     * Used when entering two-player VS mode.
     * 
     * @author Dong, Jia.
     */
    public void playTwoPlayerBackgroundMusic() {
        playBackgroundMusicFromFile("audio/TwoPlayerBGM.mp3");
    }
    
    /**
     * Starts playing background music in a loop.
     * Maintains backward compatibility - defaults to main menu music.
     * 
     * @author Dong, Jia.
     */
    public void playBackgroundMusic() {
        playMainMenuBackgroundMusic();
    }
    
    /**
     * Internal method to play background music from a specific file.
     * 
     * @param musicFile the path to the music file
     * @author Dong, Jia.
     */
    private void playBackgroundMusicFromFile(String musicFile) {
        if (!soundEnabled) return;
        
        // Ensure audio operations run on JavaFX thread
        Platform.runLater(() -> {
            // Stop any existing background music directly (we're already on JavaFX thread)
            if (backgroundMusicPlayer != null) {
                backgroundMusicPlayer.stop();
                backgroundMusicPlayer.dispose();
                backgroundMusicPlayer = null;
            }
            
            Media media = loadMedia(musicFile);
            if (media == null) return;
            
            try {
                backgroundMusicPlayer = new MediaPlayer(media);
                // Apply master volume and music volume: final volume = masterVolume * musicVolume
                backgroundMusicPlayer.setVolume(masterVolume * musicVolume);
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
     * Starts playing level background music in a loop based on level number.
     * Used when entering level mode.
     * 
     * <p>Music mapping:
     * <ul>
     *   <li>Level 1 & 3: Level13BGM.mp3</li>
     *   <li>Level 2 & 5: Level25BGM.mp3</li>
     *   <li>Level 4: Level4BGM.mp3</li>
     * </ul>
     * </p>
     * 
     * @param levelId the level ID (1-5)
     * @author Dong, Jia.
     */
    public void playLevelBackgroundMusic(int levelId) {
        String musicFile;
        if (levelId == 1 || levelId == 3) {
            musicFile = "audio/Level13BGM.mp3";
        } else if (levelId == 2 || levelId == 5) {
            musicFile = "audio/Level25BGM.mp3";
        } else if (levelId == 4) {
            musicFile = "audio/Level4BGM.mp3";
        } else {
            // Default to Level13BGM for invalid level IDs
            musicFile = "audio/Level13BGM.mp3";
        }
        playBackgroundMusicFromFile(musicFile);
    }
    
    /**
     * Starts playing level background music in a loop.
     * Defaults to Level13BGM (level 1 music) for backward compatibility.
     * 
     * @author Dong, Jia.
     */
    public void playLevelBackgroundMusic() {
        playLevelBackgroundMusic(1); // Default to level 1 music (Level13BGM)
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
     * Sets the master volume level (affects all audio).
     * 
     * @param masterVolume master volume level (0.0 to 1.0)
     */
    public void setMasterVolume(double masterVolume) {
        this.masterVolume = Math.max(0.0, Math.min(1.0, masterVolume));
        // Update background music volume immediately
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.setVolume(this.masterVolume * this.musicVolume);
        }
        updateCountdownPlayerVolume();
    }
    
    /**
     * Gets the master volume level.
     * 
     * @return master volume level (0.0 to 1.0)
     */
    public double getMasterVolume() {
        return masterVolume;
    }
    
    /**
     * Sets the volume level for sound effects (before master volume).
     * 
     * @param sfxVolume sound effects volume level (0.0 to 1.0)
     */
    public void setSfxVolume(double sfxVolume) {
        this.sfxVolume = Math.max(0.0, Math.min(1.0, sfxVolume));
        updateCountdownPlayerVolume();
    }
    
    /**
     * Gets the volume level for sound effects (before master volume).
     * 
     * @return sound effects volume level (0.0 to 1.0)
     */
    public double getSfxVolume() {
        return sfxVolume;
    }
    
    /**
     * Sets the volume level for background music (before master volume).
     * 
     * @param musicVolume music volume level (0.0 to 1.0)
     */
    public void setMusicVolume(double musicVolume) {
        this.musicVolume = Math.max(0.0, Math.min(1.0, musicVolume));
        // Update background music volume immediately
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.setVolume(this.masterVolume * this.musicVolume);
        }
    }
    
    /**
     * Gets the volume level for background music (before master volume).
     * 
     * @return music volume level (0.0 to 1.0)
     */
    public double getMusicVolume() {
        return musicVolume;
    }
    
    /**
     * Sets all volume levels at once from GameSettings.
     * This is the recommended way to update volumes from settings.
     * 
     * @param masterVolume master volume level (0.0 to 1.0)
     * @param musicVolume music volume level (0.0 to 1.0)
     * @param sfxVolume sound effects volume level (0.0 to 1.0)
     */
    public void setVolumes(double masterVolume, double musicVolume, double sfxVolume) {
        this.masterVolume = Math.max(0.0, Math.min(1.0, masterVolume));
        this.musicVolume = Math.max(0.0, Math.min(1.0, musicVolume));
        this.sfxVolume = Math.max(0.0, Math.min(1.0, sfxVolume));
        // Update background music volume immediately
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.setVolume(this.masterVolume * this.musicVolume);
        }
        updateCountdownPlayerVolume();
    }

    private void updateCountdownPlayerVolume() {
        if (countdownSoundPlayer != null) {
            countdownSoundPlayer.setVolume(this.masterVolume * this.sfxVolume);
        }
    }
}

