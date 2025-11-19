package com.comp2042.model.state;

import com.comp2042.dto.DownData;
import com.comp2042.event.MoveEvent;
import com.comp2042.dto.ViewData;

/**
 * State interface for different game states (State Pattern).
 * Each state (Playing, Paused, GameOver) handles events according to its specific rules.
 * 
 * @author Dong, Jia.
 */
public interface GameState {
    /**
     * Handles DOWN event.
     * @param event MoveEvent
     * @return DownData, or null if not allowed in current state
     */
    DownData onDownEvent(MoveEvent event);

    /**
     * Handles LEFT event.
     * @param event MoveEvent
     * @return ViewData, or null if not allowed in current state
     */
    ViewData onLeftEvent(MoveEvent event);

    /**
     * Handles RIGHT event.
     * @param event MoveEvent
     * @return ViewData, or null if not allowed in current state
     */
    ViewData onRightEvent(MoveEvent event);

    /**
     * Handles ROTATE event.
     * @param event MoveEvent
     * @return ViewData, or null if not allowed in current state
     */
    ViewData onRotateEvent(MoveEvent event);

    /**
     * Handles ROTATE_CCW event.
     * @param event MoveEvent
     * @return ViewData, or null if not allowed in current state
     */
    ViewData onRotateCCWEvent(MoveEvent event);

    /**
     * Handles pause/unpause request.
     * @return Next GameState (e.g., Playing -> Paused)
     */
    GameState handlePauseRequest();

    /**
     * Handles new game request.
     * @return New PlayingState
     */
    GameState handleNewGameRequest();
}