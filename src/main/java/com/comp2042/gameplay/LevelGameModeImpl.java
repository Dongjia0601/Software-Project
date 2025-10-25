package com.comp2042.gameplay;

import com.comp2042.*;
import com.comp2042.core.GameService;
import com.comp2042.game.GameResult;
import com.comp2042.game.LevelMode; // Import the data class
import com.comp2042.game.LevelManager; // Import the manager

/**
 * Concrete implementation of the GameMode interface for playing themed levels.
 * This mode uses a LevelMode object to define level-specific rules and objectives.
 * It delegates core game logic to GameService and UI updates to GuiController.
 *
 */
public class LevelGameModeImpl implements GameMode { // Implement the GameMode interface

    private final GameService gameService; // Core game logic service
    private final GuiController guiController; // UI controller
    private final LevelManager levelManager; // Level manager for persistence/unlocking

    private LevelMode currentLevelMode; // The specific level being played (data object)
    private long levelStartTime; // Timestamp when the level started
    private int linesClearedInLevel; // Lines cleared during this level play session
    private boolean levelCompleted; // Flag indicating if the level was completed successfully
    private boolean levelFailed; // Flag indicating if the level failed (time up, game over)

    /**
     * Constructs a new LevelGameModeImpl.
     *
     * @param gameService The core game service.
     * @param guiController The GUI controller for UI updates.
     * @param levelManager The level manager for accessing level data and persistence.
     * @param levelMode The specific LevelMode data object defining this level's rules.
     */
    public LevelGameModeImpl(GameService gameService, GuiController guiController, LevelManager levelManager, LevelMode levelMode) {
        this.gameService = gameService;
        this.guiController = guiController;
        this.levelManager = levelManager;
        this.currentLevelMode = levelMode; // Assign the level data
        this.levelStartTime = 0;
        this.linesClearedInLevel = 0;
        this.levelCompleted = false;
        this.levelFailed = false;
    }

    @Override
    public void initialize() {
        // Initialize level-specific state
        this.levelStartTime = System.currentTimeMillis();
        this.linesClearedInLevel = 0;
        this.levelCompleted = false;
        this.levelFailed = false;

        // Apply level-specific settings (e.g., fall speed) via the GameService
        // The GuiController should listen to the GameService for these updates
        gameService.setDropSpeed(currentLevelMode.getFallSpeed());

        // Inform the GuiController about the level mode start
        // The GuiController can then update its UI accordingly (timer, progress, best stats)
        if (guiController != null) {
            // Set single-player mode for keyboard bindings
            guiController.setGameMode(false); // Single-player mode
            
            guiController.showLevelModeUI(); // Show level-specific UI elements
            guiController.updateTime(currentLevelMode.getTimeLimitSeconds()); // Set initial timer
            guiController.updateProgress(linesClearedInLevel, currentLevelMode.getTargetLines()); // Set initial progress
            guiController.updateStarDisplay(0); // Reset star display
            guiController.updateLevelSpeedDisplay(currentLevelMode.getLevelId()); // Show level speed

            // Update best stats display with current level's best records
            guiController.updateBestStats(
                    currentLevelMode.getBestScore(),
                    currentLevelMode.getBestTime()
            );
        }

        System.out.println("LevelGameModeImpl initialized for level: " + currentLevelMode.getLevelName());
    }

    @Override
    public void update() {
        // Update level-specific logic (e.g., check time limit)
        if (!levelCompleted && !levelFailed) {
            checkTimeLimit();

            // Update UI elements like timer and progress if the level is still active
            if (guiController != null && !levelCompleted && !levelFailed) {
                guiController.updateTime(getTimeRemainingSeconds());
                guiController.updateProgress(linesClearedInLevel, currentLevelMode.getTargetLines());
                // Update score display with current score and level's best score
                guiController.updateScore(gameService.getScore().getScore(), currentLevelMode.getBestScore());
            }
        }
    }

    @Override
    public void render() {
        // Rendering is primarily handled by GuiController based on data/events
        // This method might be used for level-specific overlays or effects in the future
        // For now, it's a placeholder
    }

    @Override
    public GameResult getResult() {
        // Calculate final result based on level play session
        long playTimeMillis = System.currentTimeMillis() - levelStartTime;
        int finalScore = gameService.getScore().getScore();
        int highScoreForLevel = currentLevelMode.getBestScore(); // Get level's persisted high score
        boolean isNewHighScore = finalScore > highScoreForLevel;
        boolean success = levelCompleted; // Success means level completed, not just game over

        return new GameResult(
                finalScore,
                highScoreForLevel,
                isNewHighScore,
                GameModeType.LEVEL, // Indicate this is a level mode result
                playTimeMillis,
                linesClearedInLevel,
                currentLevelMode.getLevelId(), // Report the specific level played
                success
        );
    }

    @Override
    public GameModeType getType() {
        return GameModeType.LEVEL; // This implementation is for LEVEL mode
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        // Delegate to GameService
        DownData downData = gameService.processDownEvent(event);

        // Check for line clears and update level progress
        if (downData != null && downData.getClearRow() != null) {
            int linesCleared = downData.getClearRow().getLinesRemoved();
            if (linesCleared > 0) {
                this.linesClearedInLevel += linesCleared;
                checkLevelCompletion(); // Check if target lines are met
            }
        }

        // Check for game over from the core service
        if (gameService.isGameOver()) {
            failLevel("Game Over: Brick could not be placed");
        }

        return downData;
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        // Delegate to GameService
        return gameService.processLeftEvent(event);
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        // Delegate to GameService
        return gameService.processRightEvent(event);
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        // Delegate to GameService
        return gameService.processRotateEvent(event);
    }

    @Override
    public void startNewGame() {
        // Reset level-specific state for a retry
        this.levelStartTime = System.currentTimeMillis();
        this.linesClearedInLevel = 0;
        this.levelCompleted = false;
        this.levelFailed = false;

        // Reset the core game service
        gameService.startNewGame();

        // Re-apply level settings
        gameService.setDropSpeed(currentLevelMode.getFallSpeed());

        // Update UI for new game
        if (guiController != null) {
            guiController.hideLevelModeUI(); // Temporarily hide to reset
            guiController.showLevelModeUI(); // Show again
            guiController.updateTime(currentLevelMode.getTimeLimitSeconds());
            guiController.updateProgress(linesClearedInLevel, currentLevelMode.getTargetLines());
            guiController.updateStarDisplay(0);
            guiController.updateLevelSpeedDisplay(currentLevelMode.getLevelId());
            guiController.updateScore(0, currentLevelMode.getBestScore()); // Reset score display
            guiController.updateBestStats(
                    currentLevelMode.getBestScore(),
                    currentLevelMode.getBestTime()
            ); // Reset best stats display
        }

        System.out.println("LevelGameModeImpl: New game started for level: " + currentLevelMode.getLevelName());
    }

    @Override
    public boolean isGameOver() {
        // Game is over if the level is completed or failed
        return levelCompleted || levelFailed || gameService.isGameOver();
    }

    @Override
    public void pause() {
        // Pause logic is handled by the GameController/GuiController
        // This is just part of the GameMode contract
        System.out.println("LevelGameModeImpl: Pause requested");
    }

    @Override
    public void resume() {
        // Resume logic is handled by the GameController/GuiController
        // This is just part of the GameMode contract
        System.out.println("LevelGameModeImpl: Resume requested");
    }

    @Override
    public int getCurrentScore() {
        return gameService.getScore().getScore();
    }

    @Override
    public int getHighScore() {
        // Return the high score for this specific level
        return currentLevelMode.getBestScore();
    }

    // ==================== Level-Specific Logic Helpers ====================

    /**
     * Checks if the level's time limit has been exceeded.
     */
    private void checkTimeLimit() {
        long elapsedMillis = System.currentTimeMillis() - levelStartTime;
        if (elapsedMillis >= currentLevelMode.getTimeLimitMillis()) {
            failLevel("Time limit exceeded");
        }
    }

    /**
     * Checks if the level's completion conditions (target lines) are met.
     */
    private void checkLevelCompletion() {
        if (linesClearedInLevel >= currentLevelMode.getTargetLines()) {
            completeLevel();
        }
    }

    /**
     * Marks the level as completed successfully.
     */
    private void completeLevel() {
        if (levelCompleted || levelFailed) return; // Prevent duplicate completion

        levelCompleted = true;
        long completionTimeMillis = System.currentTimeMillis() - levelStartTime;
        int finalScore = gameService.getScore().getScore();

        // Calculate stars based on the LevelMode's logic
        int stars = currentLevelMode.calculateStars(
                finalScore,
                linesClearedInLevel,
                (int) (completionTimeMillis / 1000),
                true // success
        );

        // Report completion to LevelManager for persistence and unlocking
        levelManager.completeLevel(
                currentLevelMode.getLevelId(),
                finalScore,
                linesClearedInLevel,
                completionTimeMillis,
                true // success
        );

        System.out.println("LevelGameModeImpl: Level completed! ID=" + currentLevelMode.getLevelId() +
                ", Score=" + finalScore + ", Lines=" + linesClearedInLevel +
                ", Time=" + (completionTimeMillis / 1000) + "s, Stars=" + stars);

        // Update UI to reflect completion (handled by GuiController/GameController based on isGameOver)
    }

    /**
     * Marks the level as failed.
     * @param reason The reason for failure (e.g., "Time limit exceeded", "Game Over").
     */
    private void failLevel(String reason) {
        if (levelCompleted || levelFailed) return; // Prevent duplicate failure

        levelFailed = true;
        long completionTimeMillis = System.currentTimeMillis() - levelStartTime;
        int finalScore = gameService.getScore().getScore();

        // Report failure (0 stars) to LevelManager for persistence
        levelManager.completeLevel(
                currentLevelMode.getLevelId(),
                finalScore,
                linesClearedInLevel,
                completionTimeMillis,
                false // success = false
        );

        System.out.println("LevelGameModeImpl: Level failed! ID=" + currentLevelMode.getLevelId() +
                ", Reason=" + reason + ", Score=" + finalScore +
                ", Lines=" + linesClearedInLevel + ", Time=" + (completionTimeMillis / 1000) + "s");

        // Update UI to reflect failure (handled by GuiController/GameController based on isGameOver)
    }

    /**
     * Gets the time remaining in seconds.
     * @return Time remaining in seconds, or 0 if time is up/negative.
     */
    private int getTimeRemainingSeconds() {
        long elapsedMillis = System.currentTimeMillis() - levelStartTime;
        long remainingMillis = currentLevelMode.getTimeLimitMillis() - elapsedMillis;
        return Math.max(0, (int) (remainingMillis / 1000));
    }

    /**
     * Gets the current level mode data object.
     * @return The LevelMode object.
     */
    public LevelMode getCurrentLevelMode() {
        return currentLevelMode;
    }
}