package com.comp2042.gameplay;

import com.comp2042.*;
import com.comp2042.core.GameService;
import com.comp2042.game.GameResult;
import com.comp2042.game.LevelMode;
import com.comp2042.game.LevelManager;

/**
 * Concrete implementation of the GameMode interface for playing themed levels.
 * This mode uses a LevelMode object to define level-specific rules and objectives.
 * It delegates core game logic to GameService and UI updates to GuiController.
 */
public class LevelGameModeImpl implements GameMode {

    private final GameService gameService;
    private final GuiController guiController;
    private final LevelManager levelManager;

    private LevelMode currentLevelMode;
    private long levelStartTime;
    private int linesClearedInLevel;
    private boolean levelCompleted;
    private boolean levelFailed;

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
        this.levelStartTime = System.currentTimeMillis();
        this.linesClearedInLevel = 0;
        this.levelCompleted = false;
        this.levelFailed = false;

        gameService.setDropSpeed(currentLevelMode.getFallSpeed());

        if (guiController != null) {
            guiController.setGameMode(false);
            guiController.showLevelModeUI();
            guiController.updateTime(currentLevelMode.getTimeLimitSeconds());
            guiController.updateProgress(linesClearedInLevel, currentLevelMode.getTargetLines());
            guiController.updateStarDisplay(0);
            guiController.updateLevelSpeedDisplay(currentLevelMode.getLevelId());

            guiController.updateBestStats(
                    currentLevelMode.getBestScore(),
                    currentLevelMode.getBestTime()
            );
        }

        System.out.println("LevelGameModeImpl initialized for level: " + currentLevelMode.getLevelName());
    }

    @Override
    public void update() {
        if (!levelCompleted && !levelFailed) {
            checkTimeLimit();

            if (guiController != null && !levelCompleted && !levelFailed) {
                guiController.updateTime(getTimeRemainingSeconds());
                guiController.updateProgress(linesClearedInLevel, currentLevelMode.getTargetLines());
                guiController.updateScore(gameService.getScore().getScore(), currentLevelMode.getBestScore());
            }
        }
    }

    @Override
    public void render() {
    }

    @Override
    public GameResult getResult() {
        long playTimeMillis = System.currentTimeMillis() - levelStartTime;
        int finalScore = gameService.getScore().getScore();
        int highScoreForLevel = currentLevelMode.getBestScore();
        boolean isNewHighScore = finalScore > highScoreForLevel;
        boolean success = levelCompleted;

        return new GameResult(
                finalScore,
                highScoreForLevel,
                isNewHighScore,
                GameModeType.LEVEL,
                playTimeMillis,
                linesClearedInLevel,
                currentLevelMode.getLevelId(),
                success
        );
    }

    @Override
    public GameModeType getType() {
        return GameModeType.LEVEL;
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        DownData downData = gameService.processDownEvent(event);

        if (downData != null && downData.getClearRow() != null) {
            int linesCleared = downData.getClearRow().getLinesRemoved();
            if (linesCleared > 0) {
                this.linesClearedInLevel += linesCleared;
                checkLevelCompletion();
            }
        }

        if (gameService.isGameOver()) {
            failLevel("Game Over: Brick could not be placed");
        }

        return downData;
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        return gameService.processLeftEvent(event);
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        return gameService.processRightEvent(event);
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        return gameService.processRotateEvent(event);
    }

    @Override
    public void startNewGame() {
        this.levelStartTime = System.currentTimeMillis();
        this.linesClearedInLevel = 0;
        this.levelCompleted = false;
        this.levelFailed = false;

        gameService.startNewGame();
        gameService.setDropSpeed(currentLevelMode.getFallSpeed());

        if (guiController != null) {
            guiController.hideLevelModeUI();
            guiController.showLevelModeUI();
            guiController.updateTime(currentLevelMode.getTimeLimitSeconds());
            guiController.updateProgress(linesClearedInLevel, currentLevelMode.getTargetLines());
            guiController.updateStarDisplay(0);
            guiController.updateLevelSpeedDisplay(currentLevelMode.getLevelId());
            guiController.updateScore(0, currentLevelMode.getBestScore());
            guiController.updateBestStats(
                    currentLevelMode.getBestScore(),
                    currentLevelMode.getBestTime()
            );
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
        if (levelCompleted || levelFailed) return;

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
        if (levelCompleted || levelFailed) return;

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