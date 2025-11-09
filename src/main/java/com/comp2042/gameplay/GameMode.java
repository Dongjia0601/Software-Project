package com.comp2042.gameplay;

import com.comp2042.*;
import com.comp2042.game.GameResult; // Assuming GameResult is defined

/**
 * Interface defining the contract for all game modes in the Tetris game.
 * 
 * <p>This interface follows the Strategy pattern, allowing different game modes
 * (Endless, Level, Two Player VS) to be implemented with their
 * own specific behaviors while maintaining a consistent interface for the game
 * controller to interact with.</p>
 * 
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Define common game mode lifecycle (initialize, update, render)</li>
 *   <li>Handle input events consistently across all modes</li>
 *   <li>Provide game state information and results</li>
 *   <li>Support different gameplay variations</li>
 * </ul>
 */
public interface GameMode {

    /**
     * Initializes the game mode with any required setup.
     * This method should be called before any other operations.
     */
    void initialize();

    /**
     * Updates the game state for the current frame.
     * This method handles the main game loop logic.
     */
    void update();

    /**
     * Renders the current game state to the display.
     * This method handles all visual updates.
     */
    void render();

    /**
     * Gets the result of the current game session.
     *
     * @return the game result containing score, completion status, and other metrics.
     *         Returns null if the game has not been initialized or completed.
     */
    GameResult getResult();

    /**
     * Gets the type of this game mode.
     *
     * @return the game mode type, never null
     */
    GameModeType getType();

    /**
     * Handles the down movement event.
     *
     * @param event the move event containing movement information and source
     * @return the down data result containing view data and line clear information.
     *         Returns null if the game is paused or the event cannot be processed.
     */
    DownData onDownEvent(MoveEvent event);

    /**
     * Handles the left movement event.
     *
     * @param event the move event containing movement information and source
     * @return the view data result containing updated brick position and board state.
     *         Returns null if the game is paused or the movement is invalid.
     */
    ViewData onLeftEvent(MoveEvent event);

    /**
     * Handles the right movement event.
     *
     * @param event the move event containing movement information and source
     * @return the view data result containing updated brick position and board state.
     *         Returns null if the game is paused or the movement is invalid.
     */
    ViewData onRightEvent(MoveEvent event);

    /**
     * Handles the rotation event.
     *
     * @param event the move event containing rotation information and source
     * @return the view data result containing updated brick rotation and board state.
     *         Returns null if the game is paused or the rotation is invalid.
     */
    ViewData onRotateEvent(MoveEvent event);

    /**
     * Starts a new game in this mode.
     *
     * <p>This method performs the following operations:</p>
     * <ul>
     *   <li>Resets the game state to initial conditions</li>
     *   <li>Clears the game board</li>
     *   <li>Initializes scoring and progression systems</li>
     *   <li>Creates the first falling piece</li>
     *   <li>Starts the game timer</li>
     * </ul>
     */
    void startNewGame();

    /**
     * Checks if the game is over.
     *
     * <p>A game is considered over when:</p>
     * <ul>
     *   <li>A new piece cannot be placed on the board</li>
     *   <li>The player reaches a losing condition</li>
     *   <li>The game mode specific end condition is met</li>
     * </ul>
     *
     * @return true if the game is over, false otherwise
     */
    boolean isGameOver();

    /**
     * Pauses the game.
     *
     * <p>When paused, the game will:</p>
     * <ul>
     *   <li>Stop the game timer and piece falling</li>
     *   <li>Disable input processing</li>
     *   <li>Maintain the current game state</li>
     *   <li>Show pause indicators in the UI</li>
     * </ul>
     */
    void pause();

    /**
     * Resumes the game from a paused state.
     *
     * <p>When resumed, the game will:</p>
     * <ul>
     *   <li>Restart the game timer and piece falling</li>
     *   <li>Re-enable input processing</li>
     *   <li>Continue from the previous game state</li>
     *   <li>Hide pause indicators in the UI</li>
     * </ul>
     */
    void resume();

    /**
     * Gets the current score for this game session.
     *
     * @return the current score as an integer value, never negative
     */
    int getCurrentScore();

    /**
     * Gets the high score for this game mode.
     *
     * <p>The high score represents the best score achieved in this specific
     * game mode across all previous game sessions.</p>
     *
     * @return the high score as an integer value, never negative.
     *         Returns 0 if no high score has been set yet.
     */
    int getHighScore();
}