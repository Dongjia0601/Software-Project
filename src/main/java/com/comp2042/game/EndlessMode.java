package com.comp2042.game;

import com.comp2042.gameplay.GameMode;
import com.comp2042.gameplay.GameModeType;
import com.comp2042.*;
import com.comp2042.core.GameService;

/**
 * Implementation of GameMode for endless gameplay.
 * 
 * <p>In endless mode, players can play indefinitely until they lose. The game
 * continues until the board is filled and no new brick can be placed. This mode
 * focuses on achieving the highest possible score without time or level constraints.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Unlimited gameplay until game over</li>
 *   <li>Score tracking and high score recording</li>
 *   <li>Pause and resume functionality</li>
 *   <li>Game statistics and timing</li>
 * </ul>
 */
public class EndlessMode implements GameMode {
    
    private final GameService gameService;
    private final GuiController guiController;
    
    private long gameStartTime;
    private int highScore;
    private boolean gameOver;
    private boolean paused;
    private GameResult gameResult;
    
    /**
     * Constructs a new EndlessMode.
     * 
     * @param gameService the core game service for game logic
     * @param guiController the GUI controller for UI updates
     */
    public EndlessMode(GameService gameService, GuiController guiController) {
        this.gameService = gameService;
        this.guiController = guiController;
        this.gameStartTime = 0;
        this.highScore = 0;
        this.gameOver = false;
        this.paused = false;
        this.gameResult = null;
    }
    
    @Override
    public void initialize() {
        // Initialize endless mode state
        this.gameStartTime = System.currentTimeMillis();
        this.gameOver = false;
        this.paused = false;
        this.gameResult = null;
        
        // Start a new game
        gameService.startNewGame();
        
        // Set default drop speed for endless mode (medium difficulty)
        gameService.setDropSpeed(400);
        
        // Initialize UI display
        if (guiController != null) {
            guiController.updateScore(0, highScore);
            guiController.updateLines(0);
            guiController.updateLevel(1);
            guiController.updateSpeed(1);
            
            // Show EndlessMode UI (if the method exists)
            // guiController.showEndlessModeUI();
        }
        
        System.out.println("EndlessMode initialized - game started");
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
        int level = Math.max(1, (linesCleared / 10) + 1);
        guiController.updateLevel(level);
        
        // Update speed display
        int speedLevel = Math.min(10, level);
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
        
        // Start new game in service
        gameService.startNewGame();
        
        System.out.println("EndlessMode: New game started");
    }

    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public void pause() {
        if (!gameOver) {
            this.paused = true;
            System.out.println("EndlessMode: Game paused");
        }
    }

    @Override
    public void resume() {
        if (paused && !gameOver) {
            this.paused = false;
            System.out.println("EndlessMode: Game resumed");
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
     * Ends the current game and calculates final results.
     */
    private void endGame() {
        if (gameOver) {
            return; // Prevent duplicate end game calls
        }
        
        this.gameOver = true;
        long playTime = System.currentTimeMillis() - gameStartTime;
        int finalScore = gameService.getScore().getScore();
        boolean isNewHighScore = finalScore > highScore;
        
        // Update high score if new record
        if (isNewHighScore) {
            this.highScore = finalScore;
        }
        
        // Create game result
        this.gameResult = new GameResult(
            finalScore,
            highScore,
            isNewHighScore,
            GameModeType.ENDLESS,
            playTime,
            getLinesCleared(),
            0, // No level reached in endless mode
            false // Endless mode doesn't have "completion" - only game over
        );
        
        System.out.println("EndlessMode: Game ended - Score: " + finalScore + 
                          ", High Score: " + highScore + 
                          ", Time: " + (playTime / 1000) + "s");
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
