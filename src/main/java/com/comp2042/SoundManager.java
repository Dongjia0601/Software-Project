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
 *   <li>Level 1, 3 & 5: Level135BGM.mp3</li>
 *   <li>Level 2 & 4: Level24BGM.mp3</li>
 * </ul>
 * </p>
 * 
 * @author Dong, Jia.
 */
public class SoundManager {
    private static SoundManager instance;
    private boolean soundEnabled = true;
    private double masterVolume = 1.0;
    private double sfxVolume = 0.8;
    private double musicVolume = 0.5;
    
    private Map<String, Media> mediaCache = new HashMap<>();
    private MediaPlayer backgroundMusicPlayer;
    private MediaPlayer countdownSoundPlayer;
    private boolean isCountdownSoundPlaying = false;
    private AtomicLong lastMoveSoundTime = new AtomicLong(0);
    private static final long MOVE_SOUND_THROTTLE_MS = 120;
    
    private SoundManager() {
    }
    
    /**
     * Gets the singleton instance of SoundManager.
     * Uses synchronized method to ensure thread-safe lazy initialization.
     * 
     * @return the SoundManager instance
     */
    public static synchronized SoundManager getInstance() {
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
    
    private void playSound(String resourcePath) {
        if (!soundEnabled) return;
        
            Media media = loadMedia(resourcePath);
        if (media == null) return;
        
        if (Platform.isFxApplicationThread()) {
            playSoundOnFxThread(media);
        } else {
            Platform.runLater(() -> playSoundOnFxThread(media));
        }
    }
    
    private void playSoundOnFxThread(Media media) {
        try {
            MediaPlayer player = new MediaPlayer(media);
            player.setVolume(masterVolume * sfxVolume);
            
            player.setOnEndOfMedia(() -> player.dispose());
            player.setOnError(() -> {
                System.err.println("MediaPlayer error: " + player.getError());
                player.dispose();
            });
            
            player.play();
        } catch (Exception e) {
            System.err.println("Failed to play sound: " + e.getMessage());
        }
    }
    
    private void playSoundThrottled(String resourcePath, long throttleMs) {
        if (!soundEnabled) return;
        
        long currentTime = System.currentTimeMillis();
        long lastTime = lastMoveSoundTime.get();
        
        if (currentTime - lastTime < throttleMs) {
            return;
        }
        
        if (lastMoveSoundTime.compareAndSet(lastTime, currentTime)) {
            playSound(resourcePath);
        }
    }
    
    /**
     * Plays an attack sound effect (for Two-Player Mode).
     */
    public void playAttackSound() {
        playSound("audio/GarbageLinesSFX.mp3");
    }
    
    /**
     * Plays a sound effect when receiving an attack (for Two-Player Mode).
     */
    public void playAttackReceivedSound() {
        playSound("audio/WarningGarbageSFX.mp3");
    }
    
    /**
     * Plays a line clear sound effect based on the number of lines cleared.
     * Uses different sound effects for 1, 2, 3, or 4 lines cleared.
     * 
     * @param linesCleared the number of lines cleared (1-4)
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
     * @param comboCount the combo count
     */
    public void playComboSound(int comboCount) {
        playSound("audio/CLear4SFX.mp3");
    }
    
    /**
     * Plays a game start countdown sound.
     * Uses a dedicated MediaPlayer to prevent duplicate playback.
     */
    public void playCountdownSound() {
        if (!soundEnabled) return;
        
        if (countdownSoundPlayer != null) {
            countdownSoundPlayer.stop();
            countdownSoundPlayer.dispose();
            countdownSoundPlayer = null;
        }
        
        isCountdownSoundPlaying = false;
        Media media = loadMedia("audio/RaceCountdownSFX.mp3");
        if (media == null) return;
        
        isCountdownSoundPlaying = true;
        
        if (Platform.isFxApplicationThread()) {
            playCountdownSoundOnFxThread(media);
        } else {
            Platform.runLater(() -> playCountdownSoundOnFxThread(media));
        }
    }
    
    private void playCountdownSoundOnFxThread(Media media) {
        try {
            countdownSoundPlayer = new MediaPlayer(media);
            updateCountdownPlayerVolume();
            
            countdownSoundPlayer.setOnEndOfMedia(() -> {
                if (countdownSoundPlayer != null) {
                    countdownSoundPlayer.dispose();
                    countdownSoundPlayer = null;
                }
                isCountdownSoundPlaying = false;
            });
            
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
     */
    public void playHardDropSound() {
        playSound("audio/HardDropSFX.mp3");
    }
    
    /**
     * Plays a hold brick sound effect.
     */
    public void playHoldSound() {
        playSound("audio/HoldSFX.mp3");
    }
    
    /**
     * Plays a move brick sound effect.
     * Uses throttling to prevent too frequent playback during rapid movements.
     */
    public void playMoveSound() {
        playSoundThrottled("audio/MoveBrickSFX.mp3", MOVE_SOUND_THROTTLE_MS);
    }
    
    /**
     * Plays a rotate brick sound effect.
     * Uses throttling to prevent too frequent playback during rapid rotations.
     */
    public void playRotateSound() {
        playSoundThrottled("audio/RotateSFX.mp3", MOVE_SOUND_THROTTLE_MS);
    }
    
    /**
     * Plays a pause/resume sound effect.
     */
    public void playPauseResumeSound() {
        playSound("audio/ClickButtonSFX.mp3");
    }
    
    /**
     * Plays a garbage lines sound effect (for Two-Player Mode).
     */
    public void playGarbageLinesSound() {
        playSound("audio/GarbageLinesSFX.mp3");
    }
    
    /**
     * Plays a warning garbage sound effect (for Two-Player Mode).
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
     * Plays a game over sound effect.
     * Used for general game over scenarios.
     */
    public void playGameOverSound() {
        playSound("audio/EndlessGameOverSFX.mp3");
    }
    
    /**
     * Plays an endless mode game over sound effect.
     * Used specifically for endless mode game over.
     */
    public void playEndlessGameOverSound() {
        playSound("audio/EndlessGameOverSFX.mp3");
    }
    
    /**
     * Plays an endless mode new record sound effect.
     * Used when a new high score is achieved in endless mode.
     */
    public void playEndlessNewRecordSound() {
        playSound("audio/EndlessGameNewRecordSFX.mp3");
    }
    
    /**
     * Plays a two-player mode game over sound effect.
     * Used when Two-Player Mode game ends.
     */
    public void playTwoPlayerGameOverSound() {
        playSound("audio/TwoPlayerGameOverSFX.mp3");
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
     */
    public void playMainMenuBackgroundMusic() {
        playBackgroundMusicFromFile("audio/MainMenuBGM.mp3");
    }
    
    /**
     * Starts playing endless mode background music in a loop.
     * Used when entering endless mode.
     */
    public void playEndlessBackgroundMusic() {
        playBackgroundMusicFromFile("audio/EndlessBGM.mp3");
    }
    
    /**
     * Starts playing two-player mode background music in a loop.
     * Used when entering Two-Player Mode.
     */
    public void playTwoPlayerBackgroundMusic() {
        playBackgroundMusicFromFile("audio/TwoPlayerBGM.mp3");
    }
    
    /**
     * Starts playing background music in a loop.
     * Maintains backward compatibility - defaults to main menu music.
     */
    public void playBackgroundMusic() {
        playMainMenuBackgroundMusic();
    }
    
    private void playBackgroundMusicFromFile(String musicFile) {
        if (!soundEnabled) return;
        
        Platform.runLater(() -> {
            if (backgroundMusicPlayer != null) {
                backgroundMusicPlayer.stop();
                backgroundMusicPlayer.dispose();
                backgroundMusicPlayer = null;
            }
            
            Media media = loadMedia(musicFile);
            if (media == null) return;
            
            try {
                backgroundMusicPlayer = new MediaPlayer(media);
                backgroundMusicPlayer.setVolume(masterVolume * musicVolume);
                backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                
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
     *   <li>Level 1, 3 & 5: Level135BGM.mp3</li>
     *   <li>Level 2 & 4: Level24BGM.mp3</li>
     * </ul>
     * </p>
     * 
     * @param levelId the level ID (1-5)
     */
    public void playLevelBackgroundMusic(int levelId) {
        String musicFile;
        if (levelId == 1 || levelId == 3 || levelId == 5) {
            musicFile = "audio/Level135BGM.mp3";
        } else if (levelId == 2 || levelId == 4) {
            musicFile = "audio/Level24BGM.mp3";
        } else {
            musicFile = "audio/Level135BGM.mp3";
        }
        playBackgroundMusicFromFile(musicFile);
    }
    
    /**
     * Starts playing level background music in a loop.
     * Defaults to Level135BGM (level 1 music) for backward compatibility.
     */
    public void playLevelBackgroundMusic() {
        playLevelBackgroundMusic(1);
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

