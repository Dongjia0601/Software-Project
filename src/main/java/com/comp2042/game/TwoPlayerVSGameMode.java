package com.comp2042.game;

import com.comp2042.gameplay.GameMode;
import com.comp2042.gameplay.GameModeType;
import com.comp2042.*;
import com.comp2042.core.GameService;

/**
 * Implementation of GameMode for two-player VS gameplay.
 * In this mode, two players compete simultaneously on separate game boards.
 * The game continues until one player loses, and the winner is determined
 * by who survives longer or achieves a higher score.
 * 
 * <p>This mode supports the Additions (25%) requirement by providing
 * a competitive multiplayer experience with separate game states for each player.</p>
 */
public class TwoPlayerVSGameMode implements GameMode {
    
    private final GameService player1Service;
    private final GameService player2Service;
    private final GuiController guiController;
    
    private long gameStartTime;
    private int player1HighScore;
    private int player2HighScore;
    private boolean gameOver;
    private boolean paused;
    private GameResult gameResult;
    private int winner; // 1 for player1, 2 for player2, 0 for no winner yet
    
    /**
     * Constructs a new TwoPlayerVSGameMode.
     * 
     * @param player1Service the game service for player 1
     * @param player2Service the game service for player 2
     * @param guiController the GUI controller for UI updates
     */
    public TwoPlayerVSGameMode(GameService player1Service, GameService player2Service, GuiController guiController) {
        this.player1Service = player1Service;
        this.player2Service = player2Service;
        this.guiController = guiController;
        this.gameStartTime = 0;
        this.player1HighScore = 0;
        this.player2HighScore = 0;
        this.gameOver = false;
        this.paused = false;
        this.gameResult = null;
        this.winner = 0;
    }
    
    @Override
    public void initialize() {
        // Initialize VS mode state
        this.gameStartTime = System.currentTimeMillis();
        this.gameOver = false;
        this.paused = false;
        this.gameResult = null;
        this.winner = 0;
        
        // Start new games for both players
        player1Service.startNewGame();
        player2Service.startNewGame();
        
        // Set different drop speeds for competitive balance
        player1Service.setDropSpeed(400);
        player2Service.setDropSpeed(400);
        
        System.out.println("TwoPlayerVSGameMode initialized - both players ready");
    }

    @Override
    public void update() {
        // Update VS mode logic
        if (!gameOver && !paused) {
            // Check if either player is game over
            boolean player1GameOver = player1Service.isGameOver();
            boolean player2GameOver = player2Service.isGameOver();
            
            if (player1GameOver && player2GameOver) {
                // Both players lost - tie game
                endGame(0);
            } else if (player1GameOver) {
                // Player 1 lost, Player 2 wins
                endGame(2);
            } else if (player2GameOver) {
                // Player 2 lost, Player 1 wins
                endGame(1);
            }
        }
    }

    @Override
    public void render() {
        // Rendering is handled by GuiController
        // This method can be used for VS mode specific visual effects
    }

    @Override
    public GameResult getResult() {
        return gameResult;
    }

    @Override
    public GameModeType getType() {
        return GameModeType.TWO_PLAYER_VS;
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        if (gameOver || paused) {
            return null;
        }
        
        // Determine which player the event is for based on event source
        GameService targetService = getTargetService(event);
        if (targetService == null) {
            return null;
        }
        
        // Process down movement through appropriate service
        DownData downData = targetService.processDownEvent(event);
        
        // Check for game over after movement
        if (targetService.isGameOver()) {
            update();
        }
        
        return downData;
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        if (gameOver || paused) {
            return null;
        }
        
        GameService targetService = getTargetService(event);
        if (targetService == null) {
            return null;
        }
        
        return targetService.processLeftEvent(event);
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        if (gameOver || paused) {
            return null;
        }
        
        GameService targetService = getTargetService(event);
        if (targetService == null) {
            return null;
        }
        
        return targetService.processRightEvent(event);
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        if (gameOver || paused) {
            return null;
        }
        
        GameService targetService = getTargetService(event);
        if (targetService == null) {
            return null;
        }
        
        return targetService.processRotateEvent(event);
    }

    @Override
    public void startNewGame() {
        // Reset VS mode state
        this.gameStartTime = System.currentTimeMillis();
        this.gameOver = false;
        this.paused = false;
        this.gameResult = null;
        this.winner = 0;
        
        // Start new games for both players
        player1Service.startNewGame();
        player2Service.startNewGame();
        
        System.out.println("TwoPlayerVSGameMode: New game started for both players");
    }

    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public void pause() {
        if (!gameOver) {
            this.paused = true;
            System.out.println("TwoPlayerVSGameMode: Game paused");
        }
    }

    @Override
    public void resume() {
        if (paused && !gameOver) {
            this.paused = false;
            System.out.println("TwoPlayerVSGameMode: Game resumed");
        }
    }

    @Override
    public int getCurrentScore() {
        // Return the combined score of both players
        return player1Service.getScore().getScore() + player2Service.getScore().getScore();
    }

    @Override
    public int getHighScore() {
        // Return the higher of the two players' high scores
        return Math.max(player1HighScore, player2HighScore);
    }
    
    /**
     * Gets the target service based on the event source.
     * This determines which player the input event is intended for.
     * 
     * @param event the move event
     * @return the appropriate game service, or null if event source is unknown
     */
    private GameService getTargetService(MoveEvent event) {
        // For now, we'll use a simple approach based on event source
        // In a more sophisticated implementation, this could be based on
        // keyboard layout, controller assignment, or other input mapping
        EventSource source = event.getEventSource();
        
        // Simple mapping: assume player 1 uses default source, player 2 uses alternative
        if (source == EventSource.KEYBOARD_PLAYER_1 || source == EventSource.KEYBOARD) {
            return player1Service;
        } else if (source == EventSource.KEYBOARD_PLAYER_2) {
            return player2Service;
        }
        
        // Default to player 1 if source is unknown
        return player1Service;
    }
    
    /**
     * Ends the current game and calculates final results.
     * 
     * @param winner the winner (1 for player 1, 2 for player 2, 0 for tie)
     */
    private void endGame(int winner) {
        if (gameOver) {
            return; // Prevent duplicate end game calls
        }
        
        this.gameOver = true;
        this.winner = winner;
        long playTime = System.currentTimeMillis() - gameStartTime;
        
        int player1Score = player1Service.getScore().getScore();
        int player2Score = player2Service.getScore().getScore();
        int finalScore = Math.max(player1Score, player2Score);
        
        // Update high scores
        boolean player1NewHigh = player1Score > player1HighScore;
        boolean player2NewHigh = player2Score > player2HighScore;
        if (player1NewHigh) {
            this.player1HighScore = player1Score;
        }
        if (player2NewHigh) {
            this.player2HighScore = player2Score;
        }
        
        // Create game result
        this.gameResult = new GameResult(
            finalScore,
            getHighScore(),
            player1NewHigh || player2NewHigh,
            GameModeType.TWO_PLAYER_VS,
            playTime,
            getTotalLinesCleared(),
            0, // No level concept in VS mode
            winner > 0 // Completed if someone won
        );
        
        String winnerText = winner == 0 ? "Tie Game" : 
                           winner == 1 ? "Player 1 Wins!" : "Player 2 Wins!";
        
        System.out.println("TwoPlayerVSGameMode: Game ended - " + winnerText + 
                         ", P1 Score: " + player1Score + 
                         ", P2 Score: " + player2Score + 
                         ", Time: " + (playTime / 1000) + "s");
    }
    
    /**
     * Gets the total number of lines cleared by both players.
     * 
     * @return total lines cleared
     */
    private int getTotalLinesCleared() {
        // This is a simplified calculation based on scores
        // In a real implementation, you'd track actual lines cleared
        return (player1Service.getScore().getScore() + player2Service.getScore().getScore()) / 100;
    }
    
    /**
     * Gets the winner of the current game.
     * 
     * @return 1 for player 1, 2 for player 2, 0 for tie or no winner yet
     */
    public int getWinner() {
        return winner;
    }
    
    /**
     * Gets the current score for a specific player.
     * 
     * @param player the player number (1 or 2)
     * @return the player's current score
     */
    public int getPlayerScore(int player) {
        if (player == 1) {
            return player1Service.getScore().getScore();
        } else if (player == 2) {
            return player2Service.getScore().getScore();
        }
        return 0;
    }
}
