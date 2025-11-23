package com.comp2042.service.timeline;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;

/**
 * Centralized manager for all game-related timelines (game loop, elapsed time, level countdown).
 * Provides unified pause/resume/stop operations.
 *
 * @author Dong, Jia
 */
public class GameTimelineManager {
    private Timeline gameTimeline;
    private Timeline elapsedTimeTimer;
    private Timeline levelTimer;
    private Runnable moveDownCallback;
    private Runnable timeUpdateCallback;
    private java.util.function.IntConsumer levelTickCallback;

    /**
     * Constructs a manager without callbacks. Use setCallbacks() before starting timelines.
     */
    public GameTimelineManager() {
        initializeTimelines();
    }

    /**
     * Constructs a GameTimelineManager with custom callbacks.
     * 
     * @param moveDownCallback game loop callback
     * @param timeUpdateCallback elapsed time callback
     * @param levelTickCallback level countdown callback
     */
    public GameTimelineManager(Runnable moveDownCallback, Runnable timeUpdateCallback, 
                              java.util.function.IntConsumer levelTickCallback) {
        this.moveDownCallback = moveDownCallback;
        this.timeUpdateCallback = timeUpdateCallback;
        this.levelTickCallback = levelTickCallback;
        initializeTimelines();
    }

    private void initializeTimelines() {
        gameTimeline = new Timeline();
        gameTimeline.setCycleCount(Timeline.INDEFINITE);

        elapsedTimeTimer = new Timeline();
        elapsedTimeTimer.setCycleCount(Timeline.INDEFINITE);
        if (timeUpdateCallback != null) {
            elapsedTimeTimer.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e -> timeUpdateCallback.run()));
        }

        levelTimer = new Timeline();
        levelTimer.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Sets or updates all callbacks.
     * @param moveDownCallback game loop callback (can be null)
     * @param timeUpdateCallback elapsed time callback (can be null)
     * @param levelTickCallback level countdown callback (can be null)
     */
    public void setCallbacks(Runnable moveDownCallback, Runnable timeUpdateCallback, 
                            java.util.function.IntConsumer levelTickCallback) {
        this.moveDownCallback = moveDownCallback;
        this.timeUpdateCallback = timeUpdateCallback;
        this.levelTickCallback = levelTickCallback;

        if (elapsedTimeTimer != null) {
            elapsedTimeTimer.getKeyFrames().clear();
        if (timeUpdateCallback != null) {
            elapsedTimeTimer.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e -> timeUpdateCallback.run()));
        }
    }
    }

    /**
     * Starts the game loop with specified speed.
     * @param speed drop speed in milliseconds (must be positive)
     * @throws IllegalArgumentException if speed &lt;= 0
     * @throws IllegalStateException if gameTimeline or moveDownCallback is null
     */
    public void startGameLoop(int speed) {
        if (speed <= 0) {
            throw new IllegalArgumentException("Game loop speed must be positive, got: " + speed);
        }
        if (moveDownCallback == null) {
            throw new IllegalStateException("GameTimelineManager: moveDownCallback is null, set callbacks before starting");
        }
        if (gameTimeline == null) {
            if (Platform.isFxApplicationThread()) {
                initializeTimelines();
            } else {
                throw new IllegalStateException("GameTimelineManager: gameTimeline is null and not on FX thread, cannot start game loop");
            }
        }
        if (Platform.isFxApplicationThread()) {
            startGameLoopInternal(speed);
        } else {
            Platform.runLater(() -> startGameLoopInternal(speed));
        }
    }
    
    private void startGameLoopInternal(int speed) {
        if (gameTimeline == null) {
            initializeTimelines();
        }
        if (gameTimeline != null) {
            gameTimeline.stop();
            gameTimeline.getKeyFrames().clear();
            gameTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(speed), e -> moveDownCallback.run()));
            gameTimeline.play();
        }
    }

    /**
     * Stops the game loop timeline.
     * Prevents automatic brick falling until restarted.
     */
    public void stopGameLoop() {
        if (gameTimeline != null) {
            gameTimeline.stop();
        }
    }

    /**
     * Updates game loop speed without stopping if running.
     * @param newSpeed new drop speed in milliseconds (must be positive)
     * @throws IllegalArgumentException if newSpeed &lt;= 0
     * @throws IllegalStateException if gameTimeline or moveDownCallback is null
     */
    public void updateGameLoopSpeed(int newSpeed) {
        if (newSpeed <= 0) {
            throw new IllegalArgumentException("Game loop speed must be positive, got: " + newSpeed);
        }
        if (gameTimeline == null) {
            throw new IllegalStateException("GameTimelineManager: gameTimeline is null");
        }
        if (moveDownCallback == null) {
            throw new IllegalStateException("GameTimelineManager: moveDownCallback is null, set callbacks first");
        }
        boolean wasRunning = gameTimeline.getStatus() == javafx.animation.Animation.Status.RUNNING;
        gameTimeline.stop();
        gameTimeline.getKeyFrames().clear();
        gameTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(newSpeed), e -> moveDownCallback.run()));
        if (wasRunning) {
            gameTimeline.play();
        }
    }

    /**
     * Starts elapsed time timer.
     * @param startTime start time in milliseconds (reserved for future use)
     * @throws IllegalStateException if elapsedTimeTimer or timeUpdateCallback is null
     */
    public void startElapsedTimer(long startTime) {
        if (elapsedTimeTimer == null) {
            throw new IllegalStateException("GameTimelineManager: elapsedTimeTimer is null");
        }
        if (timeUpdateCallback == null) {
            throw new IllegalStateException("GameTimelineManager: timeUpdateCallback is null, set callbacks before starting");
        }
        elapsedTimeTimer.getKeyFrames().clear();
        elapsedTimeTimer.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e -> timeUpdateCallback.run()));
        elapsedTimeTimer.play();
    }

    /**
     * Stops the elapsed time timer.
     * Prevents further time updates until restarted.
     */
    public void stopElapsedTimer() {
        if (elapsedTimeTimer != null) {
            elapsedTimeTimer.stop();
        }
    }

    private int currentCountdownSeconds;

    /**
     * Starts level countdown timer. Auto-stops at 0.
     * @param remainingSeconds initial remaining seconds (must be non-negative)
     * @throws IllegalArgumentException if remainingSeconds &lt; 0
     * @throws IllegalStateException if levelTimer or levelTickCallback is null
     */
    public void startLevelCountdown(int remainingSeconds) {
        if (remainingSeconds < 0) {
            throw new IllegalArgumentException("Remaining seconds must be non-negative, got: " + remainingSeconds);
        }
        if (levelTimer == null) {
            throw new IllegalStateException("GameTimelineManager: levelTimer is null");
        }
        if (levelTickCallback == null) {
            throw new IllegalStateException("GameTimelineManager: levelTickCallback is null, set callbacks before starting");
        }
        currentCountdownSeconds = remainingSeconds;
        levelTimer.stop();
        levelTimer.getKeyFrames().clear();
        levelTimer.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e -> {
            currentCountdownSeconds--;
            levelTickCallback.accept(currentCountdownSeconds);
            if (currentCountdownSeconds <= 0) {
                levelTimer.stop();
            }
        }));
        levelTimer.play();
    }

    /**
     * Stops the level countdown timer.
     * Prevents further countdown updates until restarted.
     */
    public void stopLevelTimer() {
        if (levelTimer != null) {
            levelTimer.stop();
        }
    }

    /**
     * Pauses all running timelines (game loop, elapsed time, level countdown).
     * Game state is preserved and can be resumed.
     */
    public void pauseAll() {
        if (gameTimeline != null) {
            gameTimeline.pause();
        }
        if (elapsedTimeTimer != null) {
            elapsedTimeTimer.pause();
        }
        if (levelTimer != null) {
            levelTimer.pause();
    }
    }

    /**
     * Resumes all paused timelines (game loop, elapsed time, level countdown).
     * Restores gameplay from paused state.
     */
    public void resumeAll() {
        if (gameTimeline != null) {
            gameTimeline.play();
        }
        if (elapsedTimeTimer != null) {
            elapsedTimeTimer.play();
        }
        if (levelTimer != null) {
            levelTimer.play();
    }
    }

    /**
     * Stops all timelines completely. Must be called on JavaFX Application Thread.
     */
    public void stopAll() {
        if (Platform.isFxApplicationThread()) {
            stopAllInternal();
        } else {
            Platform.runLater(this::stopAllInternal);
        }
    }
    
    private void stopAllInternal() {
        if (gameTimeline != null) {
            try {
                gameTimeline.stop();
            } catch (Exception e) {
                // Ignore errors during cleanup
            }
            gameTimeline = null;
        }
        if (elapsedTimeTimer != null) {
            try {
                elapsedTimeTimer.stop();
            } catch (Exception e) {
                // Ignore errors during cleanup
            }
            elapsedTimeTimer = null;
        }
        if (levelTimer != null) {
            try {
                levelTimer.stop();
            } catch (Exception e) {
                // Ignore errors during cleanup
            }
            levelTimer = null;
        }
    }
    
    /**
     * Reinitializes all timelines. Call this after stopAll() to create fresh Timeline instances.
     * Must be called on JavaFX Application Thread.
     */
    public void reinitializeTimelines() {
        if (!Platform.isFxApplicationThread()) {
            throw new IllegalStateException("reinitializeTimelines() must be called on JavaFX Application Thread");
        }
        
        stopAllInternal();
        initializeTimelines();
    }

    /**
     * Checks if the game loop timeline is currently running.
     * 
     * @return true if the game loop is running, false otherwise
     */
    public boolean isGameLoopRunning() {
        return gameTimeline != null && gameTimeline.getStatus() == javafx.animation.Animation.Status.RUNNING;
    }
}
