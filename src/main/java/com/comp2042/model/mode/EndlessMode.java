package com.comp2042.model.mode;

import com.comp2042.controller.factory.GameMode;
import com.comp2042.controller.factory.GameModeType;
import com.comp2042.controller.game.GuiController;
import com.comp2042.*;
import com.comp2042.dto.DownData;
import com.comp2042.dto.ViewData;
import com.comp2042.event.MoveEvent;
import com.comp2042.service.gameloop.GameService;
import javafx.application.Platform;

/**
 * Endless gameplay mode with unlimited play until game over (Strategy Pattern).
 * Tracks high scores, manages leaderboard (top 5), and provides statistics.
 * Gameplay continues until board fills completely.
 */
public class EndlessMode implements GameMode {
    
    private final GameService gameService;
    private final GuiController guiController;
    private final EndlessModeLeaderboard leaderboard;
    
    private long gameStartTime;
    private int highScore;
    private boolean gameOver;
    private boolean paused;
    private GameResult gameResult;
    private int currentRank;
    private boolean isNewHighScore;
    private int currentLevel;
    
    /**
     * Constructs a new EndlessMode.
     * 
     * @param gameService the core game service for game logic
     * @param guiController the GUI controller for UI updates
     */
    public EndlessMode(GameService gameService, GuiController guiController) {
        this.gameService = gameService;
        this.guiController = guiController;
        this.leaderboard = EndlessModeLeaderboard.getInstance();
        this.gameStartTime = 0;
        this.highScore = leaderboard.getHighScore(); // Load from leaderboard
        this.gameOver = false;
        this.currentLevel = 1;
        this.paused = false;
        this.gameResult = null;
        this.currentRank = 0;
        this.isNewHighScore = false;
    }
    
    @Override
    public void initialize() {
        // Initialize endless mode state
        this.gameStartTime = System.currentTimeMillis();
        this.gameOver = false;
        this.paused = false;
        this.gameResult = null;
        this.currentRank = 0;
        this.isNewHighScore = false;
        
        // Load current high score from leaderboard
        this.highScore = leaderboard.getHighScore();
        
        // Start a new game
        gameService.startNewGame();
        
        // Set default drop speed for endless mode (medium difficulty)
        gameService.setDropSpeed(400);
        
        // Initialize UI display
        if (guiController != null) {
            // Set single-player mode for keyboard bindings
            guiController.setGameMode(false); // Single-player mode
            
            guiController.updateScore(0, highScore);
            guiController.updateLines(0);
            guiController.updateLevel(1);
            guiController.updateSpeed(1);
            
            // Show EndlessMode UI (if the method exists)
            // guiController.showEndlessModeUI();
        }
        
    }

    @Override
    public void update() {
        // Update endless mode logic
        if (!gameOver && !paused) {
            // Check if game is over
            if (gameService.isGameOver()) {
                endGame();
            } else {
                // Update UI with current game state
                updateUI();
            }
        }
    }
    
    /**
     * Updates the UI with current game state.
     */
    private void updateUI() {
        if (guiController == null) {
            return;
        }
        
        // Update score display
        int currentScore = getCurrentScore();
        guiController.updateScore(currentScore, highScore);
        
        // Update lines cleared
        int linesCleared = getLinesCleared();
        guiController.updateLines(linesCleared);
        
        // Update level (based on lines cleared)
        this.currentLevel = Math.max(1, (linesCleared / 10) + 1);
        guiController.updateLevel(this.currentLevel);
        
        // Update speed display
        int speedLevel = Math.min(10, this.currentLevel);
        guiController.updateSpeed(speedLevel);
        
        // Update next piece display
        int[][] nextPieceData = gameService.getNextBrick();
        if (nextPieceData != null) {
            guiController.updateNextDisplay(nextPieceData);
        }
    }
    

    @Override
    public void render() {
        // Rendering is handled by GuiController
        // This method can be used for endless mode specific visual effects
    }

    @Override
    public GameResult getResult() {
        return gameResult;
    }

    @Override
    public GameModeType getType() {
        return GameModeType.ENDLESS;
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        if (gameOver || paused) {
            return null;
        }
        
        // Process down movement through game service
        DownData downData = gameService.processDownEvent(event);
        
        // Check for game over after movement
        if (gameService.isGameOver()) {
            endGame();
        }
        
        return downData;
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        if (gameOver || paused) {
            return null;
        }
        
        return gameService.processLeftEvent(event);
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        if (gameOver || paused) {
            return null;
        }
        
        return gameService.processRightEvent(event);
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        if (gameOver || paused) {
            return null;
        }
        
        return gameService.processRotateEvent(event);
    }

    @Override
    public void startNewGame() {
        // Reset endless mode state
        this.gameStartTime = System.currentTimeMillis();
        this.gameOver = false;
        this.paused = false;
        this.gameResult = null;
        this.currentLevel = 1;
        
        // Start new game in service
        gameService.startNewGame();
        
    }

    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public void pause() {
        if (!gameOver) {
            this.paused = true;
        }
    }

    @Override
    public void resume() {
        if (paused && !gameOver) {
            this.paused = false;
        }
    }

    @Override
    public int getCurrentScore() {
        return gameService.getScore().getScore();
    }

    @Override
    public int getHighScore() {
        return highScore;
    }
    
    /**
     * Gets the game start time.
     * 
     * @return the game start time in milliseconds
     */
    public long getGameStartTime() {
        return gameStartTime;
    }
    
    /**
     * Ends the current game and calculates final results.
     */
    private void endGame() {
        if (gameOver) {
            return; // Prevent duplicate end game calls
        }
        
        this.gameOver = true;
        long playTime = System.currentTimeMillis() - gameStartTime;
        int finalScore = gameService.getScore().getScore();
        int linesCleared = getLinesCleared();
        
        // Check if this is a new high score BEFORE adding to leaderboard
        this.isNewHighScore = leaderboard.isNewHighScore(finalScore);
        
        // Add entry to leaderboard and get rank (0 if not in top 5)
        this.currentRank = leaderboard.addEntry(finalScore, linesCleared, playTime, this.currentLevel);
        
        // Update high score from leaderboard
        this.highScore = leaderboard.getHighScore();
        
        // Create game result
        this.gameResult = new GameResult(
            finalScore,
            highScore,
            isNewHighScore,
            GameModeType.ENDLESS,
            playTime,
            linesCleared,
            0, // No level reached in endless mode
            false // Endless mode doesn't have "completion" - only game over
        );
    }
    
    /**
     * Gets the number of lines cleared in this game session.
     * This is a simplified calculation based on score.
     * 
     * @return estimated lines cleared
     */
    private int getLinesCleared() {
        // Simplified calculation: assume 100 points per line
        return gameService.getScore().getScore() / 100;
    }
}
