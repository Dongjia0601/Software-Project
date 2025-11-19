package com.comp2042.model.mode;

import com.comp2042.controller.factory.GameMode;
import com.comp2042.controller.factory.GameModeType;
import com.comp2042.controller.game.GameViewController;
import com.comp2042.*;
import com.comp2042.service.gameloop.GameService;
import com.comp2042.dto.DownData;
import com.comp2042.dto.ViewData;
import com.comp2042.event.MoveEvent;
import com.comp2042.event.EventType;
import com.comp2042.event.EventSource;
import com.comp2042.model.board.Board;
import com.comp2042.service.audio.SoundManager;

/**
 * Competitive two-player mode with simultaneous play on separate boards (Strategy Pattern).
 * Winner determined by survival or score. Includes attack/garbage mechanics and stat tracking.
 * Supports Additions (25%) requirement with head-to-head multiplayer.
 */
public class TwoPlayerMode implements GameMode {
    
    private final GameService player1Service;
    private final GameService player2Service;
    private final GameViewController guiController;
    
    private long gameStartTime;
    private int player1HighScore;
    private int player2HighScore;
    private boolean gameOver;
    private boolean paused;
    private GameResult gameResult;
    private int winner; // 1 for player1, 2 for player2, 0 for no winner yet
    
    // Statistics tracking
    private final PlayerStats player1Stats;
    private final PlayerStats player2Stats;
    
    // Game Mechanics (SRP: Logic delegated to mechanics class)
    private final TwoPlayerModeMechanics mechanics;
    
    /**
     * Constructs a TwoPlayerMode.
     *
     * @param player1Service Player 1 game service
     * @param player2Service Player 2 game service
     * @param guiController the GUI controller for UI updates
     */
    public TwoPlayerMode(GameService player1Service, GameService player2Service, GameViewController guiController) {
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
        this.player1Stats = new PlayerStats();
        this.player2Stats = new PlayerStats();
        this.mechanics = new TwoPlayerModeMechanics();
    }
    
    @Override
    public void initialize() {
        // Initialize VS mode state
        this.gameStartTime = System.currentTimeMillis();
        this.gameOver = false;
        this.paused = false;
        this.gameResult = null;
        this.winner = 0;
        
        // Reset statistics
        player1Stats.reset();
        player2Stats.reset();
        
        // Set two-player mode for keyboard bindings
        if (guiController != null) {
            guiController.setGameMode(true); // Two-player mode
        }
        
        // Start new games for both players
        player1Service.startNewGame();
        player2Service.startNewGame();
        
        // Set different drop speeds for competitive balance
        player1Service.setDropSpeed(400);
        player2Service.setDropSpeed(400);
        
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
        // Rendering is handled by GameViewController
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
        
        // Handle line clearing, attacks, and statistics
        if (downData != null && downData.isBrickLanded() && downData.getClearRow() != null) {
            int linesCleared = downData.getClearRow().getLinesRemoved();
            if (linesCleared > 0) {
                // Determine which player cleared lines
                boolean isPlayer1 = (targetService == player1Service);
                PlayerStats stats = isPlayer1 ? player1Stats : player2Stats;
                PlayerStats opponentStats = isPlayer1 ? player2Stats : player1Stats;
                
                // Calculate attack power based on lines cleared
                int attackPower = mechanics.calculateAttackPower(linesCleared);
                
                // Record statistics (this will update combo)
                stats.recordLineClear(linesCleared, attackPower);
                int newCombo = stats.getCurrentCombo();
                
                // Combo bonus: if combo >= 2, eliminate garbage lines
                if (newCombo >= 2) {
                    // Use mechanics to process defense
                    int linesEliminated = mechanics.processComboDefense(targetService, newCombo);
                    
                    if (linesEliminated > 0 && guiController != null) {
                        // Play combo sound
                        SoundManager.getInstance().playComboSound(newCombo);
                        
                        // Show combo bonus message
                        guiController.showComboBonus(isPlayer1 ? 1 : 2, newCombo, linesEliminated);
                        
                        // Refresh board display after lines removed
                        int[][] updatedBoard = targetService.getBoard().getBoardMatrix();
                        if (isPlayer1) {
                            guiController.refreshGameBackground1(updatedBoard);
                        } else {
                            guiController.refreshGameBackground2(updatedBoard);
                        }
                    }
                }
                
                // Send attack to opponent if attack power > 0
                if (attackPower > 0) {
                    int targetPlayer = isPlayer1 ? 2 : 1;
                    GameService opponentService = isPlayer1 ? player2Service : player1Service;
                    
                    // Play warning sound
                    SoundManager.getInstance().playWarningGarbageSound();
                    // Play attack received sound
                    SoundManager.getInstance().playAttackReceivedSound();
                    // Play garbage lines sound
                    SoundManager.getInstance().playGarbageLinesSound();
                    
                    // Apply attack via mechanics
                    mechanics.sendAttack(opponentService, attackPower);
                    
                    opponentStats.recordAttackReceived(attackPower);
                    
                    // Play attack sound
                    SoundManager.getInstance().playAttackSound();
                    
                    // Show attack animation
                    if (guiController != null) {
                        guiController.showAttackAnimation(targetPlayer, attackPower);
                        
                        // Refresh opponent's board display after attack
                        int[][] opponentBoard = opponentService.getBoard().getBoardMatrix();
                        if (targetPlayer == 1) {
                            guiController.refreshGameBackground1(opponentBoard);
                        } else {
                            guiController.refreshGameBackground2(opponentBoard);
                        }
                    }
                }
                
                // Play line clear sound
                SoundManager.getInstance().playLineClearSound(linesCleared);
                
                // Update GUI with statistics
                if (guiController != null) {
                    guiController.updatePlayerStats(isPlayer1 ? 1 : 2, stats);
                    guiController.updatePlayerStats(isPlayer1 ? 2 : 1, opponentStats);
                }
            } else {
                // No lines cleared - reset combo
                boolean isPlayer1 = (targetService == player1Service);
                PlayerStats stats = isPlayer1 ? player1Stats : player2Stats;
                stats.resetCombo();
            }
        }
        
        // Handle soft drop scoring and statistics (only when brick hasn't landed)
        if (downData != null && !downData.isBrickLanded() && event != null) {
            EventSource source = event.getEventSource();
            EventType type = event.getEventType();
            
            // Track soft drops
            if (type == EventType.DOWN && (source == EventSource.KEYBOARD_PLAYER_1 || 
                source == EventSource.KEYBOARD_PLAYER_2 || source == EventSource.KEYBOARD)) {
                targetService.getScore().add(1);
                boolean isPlayer1 = (targetService == player1Service);
                PlayerStats stats = isPlayer1 ? player1Stats : player2Stats;
                stats.recordSoftDrop();
                // Play soft drop sound effect
                SoundManager.getInstance().playSoftDropSound();
            }
        }
        
        // Handle hard drop (always lands, so check separately)
        if (downData != null && event != null && event.getEventType() == EventType.HARD_DROP) {
            boolean isPlayer1 = (targetService == player1Service);
            PlayerStats stats = isPlayer1 ? player1Stats : player2Stats;
            stats.recordHardDrop();
            // Play hard drop sound effect
            SoundManager.getInstance().playHardDropSound();
        }
        
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
        
        ViewData result = targetService.processLeftEvent(event);
        if (result != null) {
            // Play move sound effect
            SoundManager.getInstance().playMoveSound();
        }
        return result;
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
        
        ViewData result = targetService.processRightEvent(event);
        if (result != null) {
            // Play move sound effect
            SoundManager.getInstance().playMoveSound();
        }
        return result;
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
        
        ViewData result = targetService.processRotateEvent(event);
        if (result != null) {
            // Play rotate sound effect
            SoundManager.getInstance().playRotateSound();
        }
        return result;
    }

    @Override
    public void startNewGame() {
        // Reset VS mode state
        this.gameStartTime = System.currentTimeMillis();
        this.gameOver = false;
        this.paused = false;
        this.gameResult = null;
        this.winner = 0;
        
        // Reset statistics
        player1Stats.reset();
        player2Stats.reset();
        
        // Start new games for both players
        player1Service.startNewGame();
        player2Service.startNewGame();
        
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
        // Determines target service based on event source
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
    
    /**
     * Gets the game service for player 1.
     * 
     * @return the game service for player 1
     */
    public GameService getPlayer1Service() {
        return player1Service;
    }
    
    /**
     * Gets the game service for player 2.
     * 
     * @return the game service for player 2
     */
    public GameService getPlayer2Service() {
        return player2Service;
    }
    
    /**
     * Gets the statistics for player 1.
     * 
     * @return player 1 statistics
     */
    public PlayerStats getPlayer1Stats() {
        return player1Stats;
    }
    
    /**
     * Gets the statistics for player 2.
     * 
     * @return player 2 statistics
     */
    public PlayerStats getPlayer2Stats() {
        return player2Stats;
    }
    
    /**
     * Gets the game start time in milliseconds.
     * 
     * @return the game start time
     */
    public long getGameStartTime() {
        return gameStartTime;
    }
    
    /**
     * Gets player 1's high score.
     * 
     * @return player 1's high score
     */
    public int getPlayer1HighScore() {
        return player1HighScore;
    }
    
    /**
     * Gets player 2's high score.
     * 
     * @return player 2's high score
     */
    public int getPlayer2HighScore() {
        return player2HighScore;
    }
    
    /**
     * Checks if the game is currently paused.
     * 
     * @return true if paused, false otherwise
     */
    public boolean isPaused() {
        return paused;
    }
}
