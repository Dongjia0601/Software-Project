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

    /** Gets the game board. */
    Board getBoard();

    /** Gets the score tracker. */
    Score getScore();

    /** Processes DOWN event. */
    DownData processDownEvent(MoveEvent event);

    /** Processes LEFT event. */
    ViewData processLeftEvent(MoveEvent event);

    /** Processes RIGHT event. */
    ViewData processRightEvent(MoveEvent event);

    /**
     * Processes a clockwise rotation event.
     * @param event the move event
     * @return the view data result
     */
    ViewData processRotateEvent(MoveEvent event);

    /**
     * Processes a counterclockwise rotation event.
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