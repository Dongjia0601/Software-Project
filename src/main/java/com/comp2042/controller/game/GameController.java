package com.comp2042.controller.game;

import com.comp2042.event.listener.InputEventListener;
import com.comp2042.event.MoveEvent;
import com.comp2042.dto.DownData;
import com.comp2042.dto.ViewData;
import com.comp2042.model.board.SimpleBoard;
import com.comp2042.service.session.GameSession;
import com.comp2042.service.session.SinglePlayerGameSession;

/**
 * Central game flow controller coordinating board logic and state transitions (MVC Pattern).
 * Delegates input events to current game state, manages game lifecycle, and coordinates
 * between GUI controller and game session.
 * 
 * @author Dong, Jia.
 */
public class GameController implements InputEventListener {

    private final GameViewController viewGuiController; // The GUI controller instance
    private final GameSession gameSession;

    /**
     * Constructs a GameController and initializes the game session.
     *
     * @param viewController GameViewController instance
     */
    public GameController(GameViewController viewController) {
        viewGuiController = viewController;
        this.gameSession = new SinglePlayerGameSession(new SimpleBoard(10, 20), viewGuiController);
        this.gameSession.initialize();
        viewGuiController.bindScore(gameSession.scoreProperty());
        viewGuiController.setEventListener(this);
        
        // Ensure high score is displayed correctly for Endless Mode
        if (viewGuiController.isEndlessMode()) {
            try {
                com.comp2042.model.mode.EndlessModeLeaderboard leaderboard = 
                    com.comp2042.model.mode.EndlessModeLeaderboard.getInstance();
                int highScore = leaderboard.getHighScore();
                viewGuiController.updateScore(0, highScore);
            } catch (Exception e) {
                System.err.println("Error updating initial high score: " + e.getMessage());
            }
        }
    }

    /**
     * Method to handle pause requests (e.g., from GUI via P key).
     * Delegates the pause request to the current state.
     */
    public void requestPause() {
        gameSession.requestPause();
    }

    @Override
    /**
     * Handles the DOWN event received from the GUI.
     * Delegates the event to the current GameState instance.
     *
     * @param event The MoveEvent containing event type and source.
     * @return DownData containing view and row-clearing information from the state.
     */
    public DownData onDownEvent(MoveEvent event) {
        return gameSession.handleDown(event);
    }

    @Override
    /**
     * Handles the LEFT event received from the GUI.
     * Delegates the event to the current GameState instance.
     *
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape from the state.
     */
    public ViewData onLeftEvent(MoveEvent event) {
        return gameSession.handleLeft(event);
    }

    @Override
    /**
     * Handles the RIGHT event received from the GUI.
     * Delegates the event to the current GameState instance.
     *
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape from the state.
     */
    public ViewData onRightEvent(MoveEvent event) {
        return gameSession.handleRight(event);
    }

    @Override
    /**
     * Handles the ROTATE event received from the GUI.
     * Delegates the event to the current GameState instance.
     *
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape from the state.
     */
    public ViewData onRotateEvent(MoveEvent event) {
        return gameSession.handleRotateCW(event);
    }

    @Override
    /**
     * Handles the ROTATE_CCW event received from the GUI.
     * Delegates the event to the current GameState instance.
     *
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape from the state.
     */
    public ViewData onRotateCCWEvent(MoveEvent event) {
        return gameSession.handleRotateCCW(event);
    }

    @Override
    /**
     * Handles the HARD_DROP event received from the GUI.
     * Delegates the event to the current GameState instance.
     *
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape from the state.
     */
    public ViewData onHardDropEvent(MoveEvent event) {
        // Delegate to current state - hard drop
        return gameSession.handleHardDrop(event);
    }

    @Override
    /**
     * Handles the SOFT_DROP event received from the GUI.
     * Delegates the event to the current GameState instance.
     *
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and shape from the state.
     */
    public ViewData onSoftDropEvent(MoveEvent event) {
        // Delegate to current state - soft drop is essentially a down event
        return gameSession.handleSoftDrop(event);
    }

    @Override
    /**
     * Handles the HOLD event received from the GUI.
     * Delegates the event to the current GameState instance.
     *
     * @param event The MoveEvent containing event type and source.
     * @return ViewData containing the updated brick position and hold state from the state.
     */
    public ViewData onHoldEvent(MoveEvent event) {
        return gameSession.handleHold(event);
    }

    @Override
    /**
     * Handles the PAUSE event received from the GUI.
     * Delegates the event to the current GameState instance.
     *
     * @param event The MoveEvent containing event type and source.
     */
    public void onPauseEvent(MoveEvent event) {
        requestPause();
    }

    @Override
    /**
     * Handles the RESUME event received from the GUI.
     * Delegates the event to the current GameState instance.
     *
     * @param event The MoveEvent containing event type and source.
     */
    public void onResumeEvent(MoveEvent event) {
        requestPause(); // Toggle pause state
    }

    @Override
    /**
     * Handles the NEW_GAME event received from the GUI.
     * Delegates the event to the current GameState instance.
     *
     * @param event The MoveEvent containing event type and source.
     */
    public void onNewGameEvent(MoveEvent event) {
        gameSession.startNewGame();
    }

    @Override
    /**
     * Handles the QUIT event received from the GUI.
     * Delegates the event to the current GameState instance.
     *
     * @param event The MoveEvent containing event type and source.
     */
    public void onQuitEvent(MoveEvent event) {
        // Handle quit event - could close application or return to main menu
        System.exit(0);
    }

}