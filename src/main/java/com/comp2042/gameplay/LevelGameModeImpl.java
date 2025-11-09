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

    }

    @Override
    public void update() {
        if (!levelCompleted && !levelFailed) {
            checkTimeLimit();

            if (guiController != null && !levelCompleted && !levelFailed) {
                // Time is automatically updated by GuiController's levelTimer
                // Only update progress and score here
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
                
                // Update lines display in GUI (same as endless mode)
                if (guiController != null) {
                    guiController.updateLines(gameService.getBoard().getTotalLinesCleared());
                }
                
                // Update progress display
                if (guiController != null) {
                    guiController.updateProgress(linesClearedInLevel, currentLevelMode.getTargetLines());
                }
                
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

    }

    @Override
    public boolean isGameOver() {
        // Game is over if the level is completed or failed
        return levelCompleted || levelFailed || gameService.isGameOver();
    }
    
    /**
     * Checks if the level has been completed.
     * @return true if the level is completed, false otherwise
     */
    public boolean isLevelCompleted() {
        return levelCompleted;
    }

    @Override
    public void pause() {
        // Pause logic is handled by the GameController/GuiController
        // This is just part of the GameMode contract
    }

    @Override
    public void resume() {
        // Resume logic is handled by the GameController/GuiController
        // This is just part of the GameMode contract
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
     * Handles line clear events from the game board.
     * This method is called by PlayingState when lines are cleared.
     * 
     * @param linesCleared The number of lines cleared in this event.
     */
    public void handleLineClear(int linesCleared) {
        if (linesCleared > 0 && !levelCompleted && !levelFailed) {
            this.linesClearedInLevel += linesCleared;
            
            // Update progress display
            if (guiController != null) {
                guiController.updateProgress(linesClearedInLevel, currentLevelMode.getTargetLines());
            }
            
            checkLevelCompletion();
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