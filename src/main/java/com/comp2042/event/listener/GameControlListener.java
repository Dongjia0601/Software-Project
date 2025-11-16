package com.comp2042.event.listener;

import com.comp2042.event.MoveEvent;

/**
 * Interface for handling game control events.
 * Implements Interface Segregation Principle by separating game control
 * concerns from brick movement concerns.
 * 
 * <p>This interface focuses solely on game-level operations:
 * pause, resume, new game, and quit.</p>
 * 
 * @author Dong, Jia.
 */
public interface GameControlListener {
    
    /**
     * Handles the PAUSE event.
     *
     * @param event The MoveEvent containing event type and source.
     */
    void onPauseEvent(MoveEvent event);

    /**
     * Handles the RESUME event.
     *
     * @param event The MoveEvent containing event type and source.
     */
    void onResumeEvent(MoveEvent event);

    /**
     * Handles the NEW_GAME event.
     *
     * @param event The MoveEvent containing event type and source.
     */
    void onNewGameEvent(MoveEvent event);

    /**
     * Handles the QUIT event.
     *
     * @param event The MoveEvent containing event type and source.
     */
    void onQuitEvent(MoveEvent event);
}

