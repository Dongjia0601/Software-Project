package com.comp2042.gameplay;

import com.comp2042.DownData;
import com.comp2042.MoveEvent;
import com.comp2042.ViewData;

/**
 * Interface defining methods for different game states (e.g., Playing, Paused, GameOver).
 */
public interface GameState {
    DownData onDownEvent(MoveEvent event);
    ViewData onLeftEvent(MoveEvent event);
    ViewData onRightEvent(MoveEvent event);
    ViewData onRotateEvent(MoveEvent event);
    GameState handlePauseRequest();
    GameState handleNewGameRequest();
}