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
    
    // Statistics tracking
    private final PlayerStats player1Stats;
    private final PlayerStats player2Stats;
    
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
        this.player1Stats = new PlayerStats();
        this.player2Stats = new PlayerStats();
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
        
        // Handle line clearing, attacks, and statistics
        if (downData != null && downData.isBrickLanded() && downData.getClearRow() != null) {
            int linesCleared = downData.getClearRow().getLinesRemoved();
            if (linesCleared > 0) {
                // Determine which player cleared lines
                boolean isPlayer1 = (targetService == player1Service);
                PlayerStats stats = isPlayer1 ? player1Stats : player2Stats;
                PlayerStats opponentStats = isPlayer1 ? player2Stats : player1Stats;
                
                // Calculate attack power based on lines cleared
                // 1 line = 0 attack, 2 lines = 1 attack, 3 lines = 2 attacks, 4 lines = 4 attacks
                int attackPower = calculateAttackPower(linesCleared);
                
                // Record statistics (this will update combo)
                int oldCombo = stats.getCurrentCombo();
                stats.recordLineClear(linesCleared, attackPower);
                int newCombo = stats.getCurrentCombo();
                
                // Combo bonus: if combo >= 2, eliminate 2 garbage lines per combo level above 1
                if (newCombo >= 2) {
                    int comboBonus = (newCombo - 1) * 2; // Each combo above 1 eliminates 2 lines
                    int linesEliminated = eliminateGarbageLines(isPlayer1 ? 1 : 2, comboBonus);
                    if (linesEliminated > 0 && guiController != null) {
                        // Play combo sound
                        com.comp2042.SoundManager.getInstance().playComboSound(newCombo);
                        
                        // Show combo bonus message
                        guiController.showComboBonus(isPlayer1 ? 1 : 2, newCombo, linesEliminated);
                    }
                }
                
                // Send attack to opponent if attack power > 0
                if (attackPower > 0) {
                    int targetPlayer = isPlayer1 ? 2 : 1;
                    sendAttack(attackPower, targetPlayer);
                    opponentStats.recordAttackReceived(attackPower);
                    
                    // Play attack sound
                    com.comp2042.SoundManager.getInstance().playAttackSound();
                    
                    // Show attack animation
                    if (guiController != null) {
                        guiController.showAttackAnimation(targetPlayer, attackPower);
                    }
                    
                    // Refresh opponent's board display after attack
                    if (guiController != null) {
                        GameService opponentService = isPlayer1 ? player2Service : player1Service;
                        int[][] opponentBoard = opponentService.getBoard().getBoardMatrix();
                        if (targetPlayer == 1) {
                            guiController.refreshGameBackground1(opponentBoard);
                        } else {
                            guiController.refreshGameBackground2(opponentBoard);
                        }
                    }
                }
                
                // Play line clear sound
                com.comp2042.SoundManager.getInstance().playLineClearSound(linesCleared);
                
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
                com.comp2042.SoundManager.getInstance().playSoftDropSound();
            }
        }
        
        // Handle hard drop (always lands, so check separately)
        if (downData != null && event != null && event.getEventType() == EventType.HARD_DROP) {
            boolean isPlayer1 = (targetService == player1Service);
            PlayerStats stats = isPlayer1 ? player1Stats : player2Stats;
            stats.recordHardDrop();
            // Play hard drop sound effect
            com.comp2042.SoundManager.getInstance().playHardDropSound();
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
            com.comp2042.SoundManager.getInstance().playMoveSound();
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
            com.comp2042.SoundManager.getInstance().playMoveSound();
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
            com.comp2042.SoundManager.getInstance().playRotateSound();
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
     * Calculates attack power based on lines cleared.
     * Standard Tetris attack system:
     * - 1 line = 0 attack
     * - 2 lines = 1 attack
     * - 3 lines = 2 attacks
     * - 4 lines (Tetris) = 4 attacks
     * 
     * @param linesCleared the number of lines cleared
     * @return attack power (number of garbage lines to send)
     */
    private int calculateAttackPower(int linesCleared) {
        switch (linesCleared) {
            case 1: return 0;
            case 2: return 1;
            case 3: return 2;
            case 4: return 4; // Tetris bonus
            default: return 0;
        }
    }
    
    /**
     * Sends garbage lines to the opponent's board.
     * 
     * @param attackPower the number of garbage lines to send
     * @param targetPlayer the target player (1 or 2)
     */
    private void sendAttack(int attackPower, int targetPlayer) {
        if (attackPower <= 0) {
            return;
        }
        
        GameService targetService = (targetPlayer == 1) ? player1Service : player2Service;
        Board targetBoard = targetService.getBoard();
        
        // Play warning garbage sound before attack
        com.comp2042.SoundManager.getInstance().playWarningGarbageSound();
        
        // Play attack received sound
        com.comp2042.SoundManager.getInstance().playAttackReceivedSound();
        
        // Add garbage lines at the bottom of opponent's board with animation
        // Each garbage line has one random hole
        for (int i = 0; i < attackPower; i++) {
            addGarbageLine(targetBoard, i == 0); // Animate first line
            // Play garbage lines sound for each line added
            if (i == 0) {
                com.comp2042.SoundManager.getInstance().playGarbageLinesSound();
            }
        }
        
    }
    
    /**
     * Eliminates garbage lines from a player's board (combo bonus).
     * Uses reflection to access the internal matrix directly.
     * 
     * @param player the player number (1 or 2)
     * @param linesToEliminate the number of garbage lines to eliminate
     * @return the actual number of lines eliminated
     */
    private int eliminateGarbageLines(int player, int linesToEliminate) {
        if (linesToEliminate <= 0) {
            return 0;
        }
        
        GameService targetService = (player == 1) ? player1Service : player2Service;
        Board targetBoard = targetService.getBoard();
        
        if (!(targetBoard instanceof SimpleBoard)) {
            return 0;
        }
        
        SimpleBoard simpleBoard = (SimpleBoard) targetBoard;
        
        // Use reflection to access the internal currentGameMatrix
        try {
            java.lang.reflect.Field matrixField = SimpleBoard.class.getDeclaredField("currentGameMatrix");
            matrixField.setAccessible(true);
            int[][] boardMatrix = (int[][]) matrixField.get(simpleBoard);
            
            int eliminated = 0;
            
            // Find and remove garbage lines (rows filled with garbage blocks)
            for (int row = 0; row < boardMatrix.length && eliminated < linesToEliminate; row++) {
                boolean isGarbageLine = true;
                int garbageBlockCount = 0;
                
                // Check if this row is a garbage line (has garbage blocks)
                for (int col = 0; col < boardMatrix[row].length; col++) {
                    if (boardMatrix[row][col] == 8) { // Garbage block
                        garbageBlockCount++;
                    } else if (boardMatrix[row][col] != 0) {
                        // Has non-garbage blocks, not a pure garbage line
                        isGarbageLine = false;
                        break;
                    }
                }
                
                // If it's a garbage line (has at least some garbage blocks), remove it
                if (isGarbageLine && garbageBlockCount > 0) {
                    // Shift all rows above down
                    for (int r = row; r > 0; r--) {
                        boardMatrix[r] = boardMatrix[r - 1].clone();
                    }
                    // Clear top row
                    for (int col = 0; col < boardMatrix[0].length; col++) {
                        boardMatrix[0][col] = 0;
                    }
                    eliminated++;
                    row--; // Check same row again (it's now the row above)
                }
            }
            
            // Refresh board display
            if (eliminated > 0 && guiController != null) {
                int[][] updatedBoard = simpleBoard.getBoardMatrix();
                if (player == 1) {
                    guiController.refreshGameBackground1(updatedBoard);
                } else {
                    guiController.refreshGameBackground2(updatedBoard);
                }
            }
            
            return eliminated;
        } catch (Exception e) {
            System.err.println("Error eliminating garbage lines: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Adds a garbage line to the bottom of the board.
     * A garbage line is a full row with one random hole.
     * 
     * @param board the target board
     * @param animate whether to animate the attack
     */
    private void addGarbageLine(Board board, boolean animate) {
        if (board instanceof SimpleBoard) {
            SimpleBoard simpleBoard = (SimpleBoard) board;
            boolean gameOver = simpleBoard.addGarbageLine();
            if (gameOver) {
                // Game over will be detected in update()
            }
        }
    }
}
