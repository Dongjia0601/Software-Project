package com.comp2042.controller.game.twoplayer;

import com.comp2042.service.gameloop.GameService;
import com.comp2042.model.mode.TwoPlayerMode;
import com.comp2042.dto.ViewData;
import com.comp2042.dto.DownData;
import com.comp2042.event.MoveEvent;
import com.comp2042.event.EventType;
import com.comp2042.event.EventSource;
import com.comp2042.event.listener.InputEventListener;
import com.comp2042.service.audio.SoundManager;
import com.comp2042.controller.game.GuiController;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Two-player mode controller coordinating dual boards and input routing.
 * Manages separate game services, timelines, rendering updates, and state transitions.
 */
public class TwoPlayerGameController implements InputEventListener {
    
    private static final Logger logger = Logger.getLogger(TwoPlayerGameController.class.getName());
    
    private final TwoPlayerMode gameMode;
    private final GuiController guiController;
    private final GameService player1Service;
    private final GameService player2Service;
    
    private final TwoPlayerTimelineScheduler timelineScheduler;
    private final TwoPlayerCountdownManager countdownManager;
    
    private boolean paused = false;
    private boolean countdownActive = true; // Track countdown state to prevent input during countdown
    
    /**
     * Constructs a TwoPlayerGameController.
     * 
     * @param gameMode the two-player game mode instance
     * @param guiController the GUI controller for rendering
     */
    public TwoPlayerGameController(TwoPlayerMode gameMode, GuiController guiController) {
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
        
        gameMode.startNewGame();
        
        if (guiController != null) {
            int[][] emptyBoard1 = new int[20][10];
            int[][] emptyBoard2 = new int[20][10];
            guiController.refreshGameBackground1(emptyBoard1);
            guiController.refreshGameBackground2(emptyBoard2);
        }
        
        showCountdownAndStart();
    }
    
    /**
     * Shows countdown before starting the game.
     */
    private void showCountdownAndStart() {
        countdownActive = true;
        countdownManager.startCountdown(() -> {
            countdownActive = false;
            try {
                startGame();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error starting game", e);
            }
        });
    }
    
    /**
     * Starts the game after countdown completes.
     */
    private void startGame() {
        SoundManager.getInstance().stopCountdownSound();
        
        if (gameMode != null) {
            gameMode.getPlayer1Stats().startGameTime();
            gameMode.getPlayer2Stats().startGameTime();
        } else {
            logger.severe("gameMode is null");
        }
        
        if (guiController != null) {
            initializeTwoPlayerView();
        } else {
            logger.severe("guiController is null");
        }
        
        if (timelineScheduler != null) {
            timelineScheduler.start();
        } else {
            logger.severe("timelineScheduler is null");
        }
        
        if (guiController != null) {
            guiController.setEventListener(this);
        }
        
        SoundManager.getInstance().playGameStartSound();
    }
    
    /**
     * Initializes the two-player view by setting up both game boards.
     */
    private void initializeTwoPlayerView() {
        // Initialize Player 1's board
        int[][] player1Board = player1Service.getBoard().getBoardMatrix();
        ViewData player1ViewData = player1Service.getBoard().getViewData();
        if (player1Board == null) {
            logger.warning("player1Board is null");
        }
        if (player1ViewData == null) {
            logger.warning("player1ViewData is null");
        }
        guiController.initPlayer1View(player1Board, player1ViewData);
        
        // Initialize Player 2's board
        int[][] player2Board = player2Service.getBoard().getBoardMatrix();
        ViewData player2ViewData = player2Service.getBoard().getViewData();
        if (player2Board == null) {
            logger.warning("player2Board is null");
        }
        if (player2ViewData == null) {
            logger.warning("player2ViewData is null");
        }
        guiController.initPlayer2View(player2Board, player2ViewData);
        
        // Update scores for both players
        updatePlayerScores();
        
        // Initialize statistics display
        guiController.updatePlayerStats(1, gameMode.getPlayer1Stats());
        guiController.updatePlayerStats(2, gameMode.getPlayer2Stats());
    }
    
    /**
     * Updates the score displays for both players.
     */
    private void updatePlayerScores() {
        int player1Score = player1Service.getScore().getScore();
        int player2Score = player2Service.getScore().getScore();
        guiController.updatePlayer1Score(player1Score);
        guiController.updatePlayer2Score(player2Score);
    }
    
    /**
     * Handles pause requests from the GUI.
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
    
    /**
     * Stops all timelines managed by this controller.
     */
    public void stopTimelines() {
        if (timelineScheduler != null) {
            timelineScheduler.stop();
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
        if (countdownActive || paused || gameMode.isGameOver()) {
            return null;
        }
        EventSource source = event.getEventSource();
        GameService targetService = (source == EventSource.KEYBOARD_PLAYER_2) ? player2Service : player1Service;
        ViewData result = targetService.processRotateCCWEvent(event);
        if (result != null) {
            SoundManager.getInstance().playRotateSound();
            updatePlayerView(source, result);
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
        if (countdownActive || paused || gameMode.isGameOver()) {
            return null;
        }
        DownData downData = onDownEvent(new MoveEvent(EventType.DOWN, event.getEventSource()));
        if (downData != null) {
            if (!downData.isBrickLanded()) {
                updatePlayerScores();
            }
            return downData.getViewData();
        }
        return null;
    }
    
    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        if (countdownActive || paused || gameMode.isGameOver()) {
            return null;
        }
        EventSource source = event.getEventSource();
        GameService targetService = (source == EventSource.KEYBOARD_PLAYER_2) ? player2Service : player1Service;
        ViewData result = targetService.processHoldEvent(event);
        if (result != null) {
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
        requestPause();
    }
    
    @Override
    public void onNewGameEvent(MoveEvent event) {
        paused = false;
        countdownActive = true;
        
        timelineScheduler.stop();
        
        gameMode.startNewGame();
        
        if (guiController != null) {
            int[][] emptyBoard1 = new int[20][10];
            int[][] emptyBoard2 = new int[20][10];
            guiController.refreshGameBackground1(emptyBoard1);
            guiController.refreshGameBackground2(emptyBoard2);
        }
        
        showCountdownAndStart();
    }
    
    @Override
    public void onQuitEvent(MoveEvent event) {
        timelineScheduler.stop();
    }
    
    
    /**
     * Updates the appropriate player's view based on the event source.
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
     * Returns the game service for player 1.
     * 
     * @return the game service for player 1
     */
    public GameService getPlayer1Service() {
        return player1Service;
    }
    
    /**
     * Returns the game service for player 2.
     * 
     * @return the game service for player 2
     */
    public GameService getPlayer2Service() {
        return player2Service;
    }
    
    /**
     * Returns the game mode instance.
     * 
     * @return the two-player game mode instance
     */
    public com.comp2042.model.mode.TwoPlayerMode getGameMode() {
        return gameMode;
    }
}

