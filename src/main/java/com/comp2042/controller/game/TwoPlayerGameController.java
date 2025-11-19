package com.comp2042.controller.game;

import com.comp2042.service.gameloop.GameService;
import com.comp2042.model.mode.TwoPlayerMode;
import com.comp2042.dto.ViewData;
import com.comp2042.dto.DownData;
import com.comp2042.event.MoveEvent;
import com.comp2042.event.EventType;
import com.comp2042.event.EventSource;
import com.comp2042.event.listener.InputEventListener;
import com.comp2042.service.audio.SoundManager;
import com.comp2042.controller.game.twoplayer.TwoPlayerCountdownManager;
import com.comp2042.controller.game.twoplayer.TwoPlayerTimelineScheduler;

/**
 * Two-player VS mode controller coordinating dual boards and input routing (MVC Pattern).
 * Manages separate game services, timelines, rendering updates, and state transitions.
 * Handles simultaneous input from both players.
 */
public class TwoPlayerGameController implements InputEventListener {
    
    private final TwoPlayerMode gameMode;
    private final GameViewController guiController;
    private final GameService player1Service;
    private final GameService player2Service;
    
    private final TwoPlayerTimelineScheduler timelineScheduler;
    private final TwoPlayerCountdownManager countdownManager;
    
    private boolean paused = false;
    private boolean countdownActive = true; // Track countdown state to prevent input during countdown
    
    /**
     * Constructs a TwoPlayerGameController.
     * Initializes both players' game services, sets up the game mode,
     * and prepares the GUI for two-player rendering.
     * 
     * @param gameMode the TwoPlayerMode instance managing game logic
     * @param guiController the GUI controller for rendering
     */
    public TwoPlayerGameController(TwoPlayerMode gameMode, GameViewController guiController) {
        this.gameMode = gameMode;
        this.guiController = guiController;
        this.player1Service = gameMode.getPlayer1Service();
        this.player2Service = gameMode.getPlayer2Service();
        
        this.countdownManager = new TwoPlayerCountdownManager(guiController);
        this.timelineScheduler = new TwoPlayerTimelineScheduler(
            player1Service,
            player2Service,
            gameMode,
            this::handleAutoDrop,
            this::handleStatsTick
        );
        
        // Initialize game state before countdown (ensures clean start)
        // This prevents blocks from being placed during countdown
        gameMode.startNewGame();
        
        // Clear the view to show empty boards during countdown
        if (guiController != null) {
            int[][] emptyBoard1 = new int[20][10];
            int[][] emptyBoard2 = new int[20][10];
            guiController.refreshGameBackground1(emptyBoard1);
            guiController.refreshGameBackground2(emptyBoard2);
            
            // Note: We don't call refreshPlayer1Brick/refreshPlayer2Brick here
            // because the game state has been reset, so there are no bricks to display.
            // The bricks will be displayed when startGame() calls initializeTwoPlayerView().
        }
        
        // Show countdown before starting the game
        showCountdownAndStart();
    }
    
    /**
     * Shows a countdown (3-2-1) before starting the game.
     */
    private void showCountdownAndStart() {
        countdownActive = true;
        countdownManager.startCountdown(() -> {
            countdownActive = false;
            startGame();
        });
    }
    
    /**
     * Starts the game after countdown.
     * This method initializes the game state and starts the game timelines.
     * The game state has already been reset in onNewGameEvent() before the countdown,
     * so we can safely start the game here.
     */
    private void startGame() {
        // Stop countdown sound before starting game (in case it's still playing)
        SoundManager.getInstance().stopCountdownSound();
        
        // The game state has already been reset in onNewGameEvent() before countdown,
        // so we just need to initialize the view and start the timelines.
        // This ensures that no blocks can be placed during countdown.
        
        // IMPORTANT: Start game timer NOW (after countdown completes)
        // This ensures the countdown time is not included in the game time
        gameMode.getPlayer1Stats().startGameTime();
        gameMode.getPlayer2Stats().startGameTime();
        
        // Set up the GUI for two-player mode (refresh view with current state)
        initializeTwoPlayerView();
        
        // Start automatic descent timelines and stats updates
        timelineScheduler.start();
        
        // Set this controller as the event listener (only after countdown completes)
        guiController.setEventListener(this);
        
        // Play game start sound
        SoundManager.getInstance().playGameStartSound();
    }
    
    /**
     * Initializes the two-player view by setting up both game boards.
     * This method extracts board data from both players' services and
     * initializes the GUI components for dual rendering.
     */
    private void initializeTwoPlayerView() {
        // Initialize Player 1's board
        int[][] player1Board = player1Service.getBoard().getBoardMatrix();
        ViewData player1ViewData = player1Service.getBoard().getViewData();
        guiController.initPlayer1View(player1Board, player1ViewData);
        
        // Initialize Player 2's board
        int[][] player2Board = player2Service.getBoard().getBoardMatrix();
        ViewData player2ViewData = player2Service.getBoard().getViewData();
        guiController.initPlayer2View(player2Board, player2ViewData);
        
        // Update scores for both players
        updatePlayerScores();
        
        // Initialize statistics display
        guiController.updatePlayerStats(1, gameMode.getPlayer1Stats());
        guiController.updatePlayerStats(2, gameMode.getPlayer2Stats());
    }
    
    /**
     * Updates the score displays for both players.
     * This method extracts current scores from both game services and
     * updates the GUI labels accordingly.
     */
    private void updatePlayerScores() {
        int player1Score = player1Service.getScore().getScore();
        int player2Score = player2Service.getScore().getScore();
        guiController.updatePlayer1Score(player1Score);
        guiController.updatePlayer2Score(player2Score);
    }
    
    /**
     * Handles pause requests from the GUI.
     * Pauses both players' timelines and updates the game mode state.
     */
    public void requestPause() {
        if (gameMode.isGameOver()) {
            return;
        }
        
        paused = !paused;
        if (paused) {
            gameMode.pause();
            timelineScheduler.pause();
        } else {
            gameMode.resume();
            timelineScheduler.resume();
        }
    }
    
    /**
     * Checks if the game is over and handles winner determination.
     * This method should be called periodically to check for game over conditions.
     */
    public void checkGameOver() {
        gameMode.update(); // Update game mode logic
        
        if (gameMode.isGameOver()) {
            timelineScheduler.stop();
            
            // Get winner information
            int winner = gameMode.getWinner();
            int player1Score = player1Service.getScore().getScore();
            int player2Score = player2Service.getScore().getScore();
            
            // Show game over screen
            guiController.showTwoPlayerGameOver(winner, player1Score, player2Score);
        }
    }
    
    @Override
    public DownData onDownEvent(MoveEvent event) {
        // Block all input during countdown
        if (countdownActive || paused || gameMode.isGameOver()) {
            return null;
        }
        return gameMode.onDownEvent(event);
    }
    
    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        // Block all input during countdown
        if (countdownActive || paused || gameMode.isGameOver()) {
            return null;
        }
        ViewData result = gameMode.onLeftEvent(event);
        if (result != null) {
            updatePlayerView(event.getEventSource(), result);
        }
        return result;
    }
    
    @Override
    public ViewData onRightEvent(MoveEvent event) {
        // Block all input during countdown
        if (countdownActive || paused || gameMode.isGameOver()) {
            return null;
        }
        ViewData result = gameMode.onRightEvent(event);
        if (result != null) {
            updatePlayerView(event.getEventSource(), result);
        }
        return result;
    }
    
    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        // Block all input during countdown
        if (countdownActive || paused || gameMode.isGameOver()) {
            return null;
        }
        ViewData result = gameMode.onRotateEvent(event);
        if (result != null) {
            updatePlayerView(event.getEventSource(), result);
        }
        return result;
    }
    
    @Override
    public ViewData onRotateCCWEvent(MoveEvent event) {
        // Block all input during countdown
        if (countdownActive || paused || gameMode.isGameOver()) {
            return null;
        }
        ViewData result = gameMode.onRotateEvent(event);
        if (result != null) {
            updatePlayerView(event.getEventSource(), result);
        }
        return result;
    }
    
    @Override
    public ViewData onHardDropEvent(MoveEvent event) {
        // Block all input during countdown
        if (countdownActive || paused || gameMode.isGameOver()) {
            return null;
        }
        DownData downData = onDownEvent(new MoveEvent(EventType.HARD_DROP, event.getEventSource()));
        return downData != null ? downData.getViewData() : null;
    }
    
    @Override
    public ViewData onSoftDropEvent(MoveEvent event) {
        // Block all input during countdown
        if (countdownActive || paused || gameMode.isGameOver()) {
            return null;
        }
        DownData downData = onDownEvent(new MoveEvent(EventType.DOWN, event.getEventSource()));
        if (downData != null) {
            // Update score display in real-time for successful soft drops
            if (!downData.isBrickLanded()) {
                updatePlayerScores();
            }
            return downData.getViewData();
        }
        return null;
    }
    
    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        // Block all input during countdown
        if (countdownActive || paused || gameMode.isGameOver()) {
            return null;
        }
        // Process hold event through the appropriate service
        EventSource source = event.getEventSource();
        GameService targetService = (source == EventSource.KEYBOARD_PLAYER_2) ? player2Service : player1Service;
        ViewData result = targetService.processHoldEvent(event);
        if (result != null) {
            // Play hold sound effect
            SoundManager.getInstance().playHoldSound();
            updatePlayerView(source, result);
        }
        return result;
    }
    
    @Override
    public void onPauseEvent(MoveEvent event) {
        requestPause();
    }
    
    @Override
    public void onResumeEvent(MoveEvent event) {
        requestPause(); // Toggle pause
    }
    
    @Override
    public void onNewGameEvent(MoveEvent event) {
        // Reset game state
        paused = false;
        countdownActive = true; // Start countdown for new game
        
        // Stop timelines
        timelineScheduler.stop();
        
        // IMPORTANT: Reset game state BEFORE countdown to ensure clean start
        // This clears any blocks that might have been placed during previous countdown
        gameMode.startNewGame();
        
        // Clear the view to show empty boards during countdown
        if (guiController != null) {
            // Clear both players' boards visually
            int[][] emptyBoard1 = new int[20][10];
            int[][] emptyBoard2 = new int[20][10];
            guiController.refreshGameBackground1(emptyBoard1);
            guiController.refreshGameBackground2(emptyBoard2);
            
            // Note: We don't call refreshPlayer1Brick/refreshPlayer2Brick here
            // because the game state has been reset, so there are no bricks to display.
            // The bricks will be displayed when startGame() calls initializeTwoPlayerView().
        }
        
        // Show countdown before starting new game
        // The game will actually start after countdown completes
        showCountdownAndStart();
    }
    
    @Override
    public void onQuitEvent(MoveEvent event) {
        timelineScheduler.stop();
    }
    
    
    /**
     * Updates the appropriate player's view based on the event source.
     * Routes the view data update to the correct player's rendering components.
     * 
     * @param source the event source indicating which player triggered the event
     * @param viewData the updated view data to display
     */
    private void updatePlayerView(EventSource source, ViewData viewData) {
        if (source == EventSource.KEYBOARD_PLAYER_1 || source == EventSource.KEYBOARD) {
            guiController.refreshPlayer1Brick(viewData);
            updatePlayerScores();
        } else if (source == EventSource.KEYBOARD_PLAYER_2) {
            guiController.refreshPlayer2Brick(viewData);
            updatePlayerScores();
        }
    }

    private void handleAutoDrop(int playerIndex, DownData downData) {
        if (guiController != null && downData != null) {
            if (playerIndex == 1) {
                guiController.handlePlayer1DownEvent(downData);
            } else {
                guiController.handlePlayer2DownEvent(downData);
            }
        }
        checkGameOver();
    }

    private void handleStatsTick() {
        if (paused || gameMode.isGameOver() || guiController == null) {
            return;
        }
        guiController.updatePlayerStats(1, gameMode.getPlayer1Stats());
        guiController.updatePlayerStats(2, gameMode.getPlayer2Stats());
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
     * Gets the game mode instance.
     * 
     * @return the TwoPlayerMode instance
     */
    public com.comp2042.model.mode.TwoPlayerMode getGameMode() {
        return gameMode;
    }
}

