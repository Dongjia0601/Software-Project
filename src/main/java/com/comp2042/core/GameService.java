package com.comp2042.core;

import com.comp2042.Board;
import com.comp2042.Score;
import com.comp2042.ViewData;
import com.comp2042.DownData;
import com.comp2042.MoveEvent;

/**
 * Interface defining the contract for game service operations.
 * This interface provides methods for handling game logic, movement events,
 * and game state management.
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
}