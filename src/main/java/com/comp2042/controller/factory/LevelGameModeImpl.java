package com.comp2042.controller.factory;

import com.comp2042.controller.game.GuiController;
import com.comp2042.service.gameloop.GameService;
import com.comp2042.model.mode.GameResult;
import com.comp2042.model.mode.LevelMode;
import com.comp2042.model.mode.LevelManager;
import com.comp2042.dto.DownData;
import com.comp2042.dto.ViewData;
import com.comp2042.event.MoveEvent;
import com.comp2042.model.board.Board;

/**
 * Concrete implementation of GameMode for themed level gameplay.
 * 
 * <p>Manages level-specific state including time limits, target lines, completion status,
 * and star rating evaluation. Uses a LevelMode configuration object to define
 * level rules, objectives, and scoring thresholds.
 * 
 * <p>Delegates core game logic to GameService and UI synchronization to GuiController.
 * Tracks level progress and automatically triggers completion or failure conditions
 * based on time limits and target line requirements.
 * 
 * @author Dong, Jia.
 */
public class LevelGameModeImpl implements GameMode {

    private final GameService gameService;
    private final GuiController guiController;
    private final LevelManager levelManager;

    private final LevelMode currentLevelMode;
    private long levelStartTime;
    private int linesClearedInLevel;
    private boolean levelCompleted;
    private boolean levelFailed;

    /**
     * Constructs a new LevelGameModeImpl instance.
     *
     * @param gameService the core game service handling game logic, must not be null
     * @param guiController the GUI controller for UI updates, must not be null
     * @param levelManager the level manager for accessing level data and persistence, must not be null
     * @param levelMode the level configuration defining rules, objectives, and scoring thresholds, must not be null
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
            // Update GUI controller's game speed to match level's fall speed
            guiController.updateGameSpeed(currentLevelMode.getFallSpeed());

            guiController.setGameMode(false);
            guiController.showLevelModeUI();
            guiController.setGameTitleForLevel(currentLevelMode.getLevelId());
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
    public GameResult getResult() {
        // Clamp to at least 1ms to avoid zero-duration edge cases in fast tests
        long playTimeMillis = Math.max(1, System.currentTimeMillis() - levelStartTime);
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

        if (downData != null && downData.clearRow() != null) {
            int linesCleared = downData.clearRow().getLinesRemoved();
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
            failLevel();
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
            guiController.updateGameSpeed(currentLevelMode.getFallSpeed());
            guiController.hideLevelModeUI();
            guiController.showLevelModeUI();
            guiController.setGameTitleForLevel(currentLevelMode.getLevelId());
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
     * Validates whether the level's time limit has been exceeded.
     * Automatically triggers level failure if the time limit is reached.
     */
    private void checkTimeLimit() {
        long elapsedMillis = System.currentTimeMillis() - levelStartTime;
        if (elapsedMillis >= currentLevelMode.getTimeLimitMillis()) {
            failLevel();
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
     * Evaluates whether the level's completion conditions are satisfied.
     * Triggers level completion when the target number of lines has been cleared.
     */
    private void checkLevelCompletion() {
        if (linesClearedInLevel >= currentLevelMode.getTargetLines()) {
            completeLevel();
        }
    }

    /**
     * Marks the level as successfully completed.
     * Calculates performance metrics and reports completion to LevelManager
     * for persistence, star rating evaluation, and next level unlocking.
     */
    private void completeLevel() {
        if (levelCompleted || levelFailed) return;

        levelCompleted = true;
        long completionTimeMillis = System.currentTimeMillis() - levelStartTime;
        int finalScore = gameService.getScore().getScore();

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
     * Marks the level as failed due to time limit exceeded or game over condition.
     * Reports failure to LevelManager and triggers the game over screen display.
     */
    private void failLevel() {
        if (levelCompleted || levelFailed) return;

        levelFailed = true;
        long completionTimeMillis = System.currentTimeMillis() - levelStartTime;
        int finalScore = gameService.getScore().getScore();

        // Report failure (0 stars) to LevelManager for persistence
        boolean[] newRecords = levelManager.completeLevel(
                currentLevelMode.getLevelId(),
                finalScore,
                linesClearedInLevel,
                completionTimeMillis,
                false // success = false
        );

        // Trigger game over screen with board reference
        if (guiController != null) {
            guiController.showLevelGameOverScene(gameService.getBoard(), newRecords, linesClearedInLevel);
        }
    }
    
    /**
     * Gets the game board instance.
     * @return the board instance
     */
    public Board getBoard() {
        return gameService.getBoard();
    }

    /**
     * Gets the current level mode data object.
     * @return The LevelMode object.
     */
    public LevelMode getCurrentLevelMode() {
        return currentLevelMode;
    }
}