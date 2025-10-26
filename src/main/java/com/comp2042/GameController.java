package com.comp2042;

import com.comp2042.gameplay.GameState;
import com.comp2042.gameplay.PlayingState; // Import new classes
import com.comp2042.gameplay.PausedState;
import com.comp2042.gameplay.GameOverState;

/**
 * Controller managing the overall game flow, state transitions, and input event delegation.
 * 
 * <p>This class serves as the central coordinator for the Tetris game, managing the game board
 * logic and coordinating between the GUI controller and game states. It implements the State
 * Pattern to handle different game phases (playing, paused, game over) and delegates input
 * events to the appropriate state handler.</p>
 * 
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Manage game board instance and state transitions</li>
 *   <li>Coordinate between GUI controller and game logic</li>
 *   <li>Handle input event delegation to current game state</li>
 *   <li>Manage game state lifecycle (playing, paused, game over)</li>
 * </ul>
 */
public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(10, 20); // The main game board instance (width=10, height=20)
    private final GuiController viewGuiController; // The GUI controller instance
    private GameState currentState; // Hold current state

    /**
     * Constructs a GameController.
     * Initializes the board, creates the initial PlayingState, initializes the GUI view,
     * binds the score, and sets itself as the event listener for the GUI.
     *
     * @param c The GuiController instance.
     */
    public GameController(GuiController c) {
        viewGuiController = c;
        board.newGame(); // Initialize board state
        // Create the initial state (PlayingState)
        this.currentState = new PlayingState(board, viewGuiController, this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.setEventListener(this);
        
        // Ensure high score is displayed correctly for Endless Mode
        if (viewGuiController.isEndlessMode()) {
            try {
                com.comp2042.game.EndlessModeLeaderboard leaderboard = 
                    com.comp2042.game.EndlessModeLeaderboard.getInstance();
                int highScore = leaderboard.getHighScore();
                viewGuiController.updateScore(0, highScore);
            } catch (Exception e) {
                System.err.println("Error updating initial high score: " + e.getMessage());
            }
        }
    }

    /**
     * Method to allow states to change the controller's state.
     * Called by states like PlayingState when a transition is needed (e.g., to GameOverState).
     *
     * @param newState The new GameState to transition to.
     */
    public void transitionToState(GameState newState) {
        this.currentState = newState;
    }

    /**
     * Method to handle pause requests (e.g., from GUI via P key).
     * Delegates the pause request to the current state.
     */
    public void requestPause() {
        GameState newState = currentState.handlePauseRequest();
        if (newState != currentState) {
            this.currentState = newState;
        }
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
        // Delegate to current state
        return currentState.onDownEvent(event);
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
        // Delegate to current state
        return currentState.onLeftEvent(event);
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
        // Delegate to current state
        return currentState.onRightEvent(event);
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
        // Delegate to current state
        return currentState.onRotateEvent(event);
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
        // Delegate to current state
        return currentState.onRotateCCWEvent(event);
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
        DownData downData = currentState.onDownEvent(event);
        return downData != null ? downData.getViewData() : null;
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
        DownData downData = currentState.onDownEvent(event);
        return downData != null ? downData.getViewData() : null;
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
        // Delegate to board
        boolean success = board.holdBrick();
        return success ? board.getViewData() : null;
    }

    @Override
    /**
     * Handles the PAUSE event received from the GUI.
     * Delegates the event to the current GameState instance.
     *
     * @param event The MoveEvent containing event type and source.
     */
    public void onPauseEvent(MoveEvent event) {
        // Delegate to current state
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
        // Delegate to current state
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
        // Delegate to current state
        this.currentState = currentState.handleNewGameRequest();
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

    @Override
    /**
     * Handles the request to start a new game.
     * Delegates the request to the current GameState instance.
     * @deprecated Use onNewGameEvent(MoveEvent) instead.
     */
    @Deprecated
    public void createNewGame() {
        // Delegate to current state
        this.currentState = currentState.handleNewGameRequest();
    }
}