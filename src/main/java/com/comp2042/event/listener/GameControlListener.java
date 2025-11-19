package com.comp2042.event.listener;

import com.comp2042.event.MoveEvent;

/**
 * Listener interface for game control events (Interface Segregation Principle).
 * Handles pause, resume, new game, and quit operations.
 * 
 * @author Dong, Jia.
 */
public interface GameControlListener {
    
    /**
     * Handles PAUSE event.
     *
     * @param event MoveEvent
     */
    void onPauseEvent(MoveEvent event);

    /**
     * Handles RESUME event.
     *
     * @param event MoveEvent
     */
    void onResumeEvent(MoveEvent event);

    /**
     * Handles NEW_GAME event.
     *
     * @param event MoveEvent
     */
    void onNewGameEvent(MoveEvent event);

    /**
     * Handles QUIT event.
     *
     * @param event MoveEvent
     */
    void onQuitEvent(MoveEvent event);
}

