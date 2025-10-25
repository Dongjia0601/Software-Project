package com.comp2042.core;

import com.comp2042.Board;
import com.comp2042.Score;
import com.comp2042.ViewData;
import com.comp2042.DownData;
import com.comp2042.MoveEvent;

/**
 * Interface defining the contract for game service operations.
 * 
 * <p>This interface provides a clean abstraction for game logic operations,
 * movement event handling, and game state management. It serves as the main
 * contract between the game controller and the underlying game implementation.</p>
 * 
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Provide access to game board and score objects</li>
 *   <li>Handle movement and rotation events</li>
 *   <li>Manage game state transitions</li>
 *   <li>Control game timing and speed</li>
 * </ul>
 */
public interface GameService {

    /**
     * Gets the game board.
     * @return the game board instance
     */
    Board getBoard();

    /**
     * Gets the score object.
     * @return the score instance
     */
    Score getScore();

    /**
     * Processes a down movement event.
     * @param event the move event
     * @return the down data result
     */
    DownData processDownEvent(MoveEvent event);

    /**
     * Processes a left movement event.
     * @param event the move event
     * @return the view data result
     */
    ViewData processLeftEvent(MoveEvent event);

    /**
     * Processes a right movement event.
     * @param event the move event
     * @return the view data result
     */
    ViewData processRightEvent(MoveEvent event);

    /**
     * Processes a clockwise rotation event.
     * @param event the move event
     * @return the view data result
     */
    ViewData processRotateEvent(MoveEvent event);

    /**
     * Processes a counter-clockwise rotation event.
     * @param event the move event
     * @return the view data result
     */
    ViewData processRotateCCWEvent(MoveEvent event);

    /**
     * Sets the drop speed for the game.
     * @param speed the new drop speed in milliseconds
     */
    void setDropSpeed(int speed);

    /**
     * Starts a new game.
     */
    void startNewGame();

    /**
     * Checks if the game is over.
     * @return true if the game is over, false otherwise
     */
    boolean isGameOver();
    
    /**
     * Processes a hold event.
     * @param event the move event
     * @return the view data result
     */
    ViewData processHoldEvent(MoveEvent event);
    
    /**
     * Gets the next brick shape data.
     * @return the next brick shape matrix
     */
    int[][] getNextBrick();
}