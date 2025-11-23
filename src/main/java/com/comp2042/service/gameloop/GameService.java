package com.comp2042.service.gameloop;

import com.comp2042.model.board.Board;
import com.comp2042.model.score.Score;
import com.comp2042.dto.ViewData;
import com.comp2042.dto.DownData;
import com.comp2042.event.MoveEvent;

/**
 * Service layer interface for game logic operations (Service Layer Pattern).
 * Provides clean abstraction between controllers and game implementation.
 * Handles event processing, state management, and timing control.
 * 
 * @author Dong, Jia.
 */
public interface GameService {

    /**
     * Gets the game board instance.
     * 
     * @return the game board
     */
    Board getBoard();

    /**
     * Gets the score tracker instance.
     * 
     * @return the score tracker
     */
    Score getScore();

    /**
     * Processes a DOWN movement event.
     * Moves the falling brick down one position and handles landing logic.
     * 
     * @param event the move event containing type and source
     * @return down data containing view information and row-clearing results
     */
    DownData processDownEvent(MoveEvent event);

    /**
     * Processes a LEFT movement event.
     * Moves the falling brick one position to the left.
     * 
     * @param event the move event containing type and source
     * @return view data containing updated brick position and shape
     */
    ViewData processLeftEvent(MoveEvent event);

    /**
     * Processes a RIGHT movement event.
     * Moves the falling brick one position to the right.
     * 
     * @param event the move event containing type and source
     * @return view data containing updated brick position and shape
     */
    ViewData processRightEvent(MoveEvent event);

    /**
     * Processes a clockwise rotation event.
     * Rotates the falling brick 90 degrees clockwise.
     * 
     * @param event the move event containing type and source
     * @return view data containing updated brick position and rotated shape
     */
    ViewData processRotateEvent(MoveEvent event);

    /**
     * Processes a counterclockwise rotation event.
     * Rotates the falling brick 90 degrees counterclockwise.
     * 
     * @param event the move event containing type and source
     * @return view data containing updated brick position and rotated shape
     */
    ViewData processRotateCCWEvent(MoveEvent event);

    /**
     * Sets the drop speed for automatic brick falling.
     * Lower values result in faster falling.
     * 
     * @param speed the new drop speed in milliseconds between automatic drops
     */
    void setDropSpeed(int speed);

    /**
     * Starts a new game session.
     * Resets the board, score, and spawns the first brick.
     */
    void startNewGame();

    /**
     * Checks if the game has ended.
     * Game over occurs when a new brick cannot be spawned.
     * 
     * @return true if the game is over, false otherwise
     */
    boolean isGameOver();
    
    /**
     * Processes a hold event.
     * Swaps the current brick with the held brick, or stores the current brick if none is held.
     * 
     * @param event the move event containing type and source
     * @return view data containing updated brick position and hold state
     */
    ViewData processHoldEvent(MoveEvent event);
    
    /**
     * Gets the next brick shape data for preview display.
     * 
     * @return the next brick shape matrix, or null if unavailable
     */
    int[][] getNextBrick();
}