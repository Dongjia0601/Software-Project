package com.comp2042;

/**
 * Enumeration indicating the source of a game event.
 * Helps distinguish between different types of event origins for proper handling.
 * 
 * @author Dong, Jia.
 */
public enum EventSource {
    /**
     * Event originated from direct user input (e.g., keyboard press, mouse click).
     */
    USER,
    
    /**
     * Event originated from keyboard input for player 1.
     */
    KEYBOARD_PLAYER_1,
    
    /**
     * Event originated from keyboard input for player 2.
     */
    KEYBOARD_PLAYER_2,
    
    /**
     * Event originated from keyboard input (generic).
     */
    KEYBOARD,
    
    /**
     * Event originated from an internal game process (e.g., automatic descent timer).
     */
    THREAD,
    
    /**
     * Event originated from AI player (for AI vs human modes).
     */
    AI,
    
    /**
     * Event originated from network communication (for multiplayer modes).
     */
    NETWORK,
    
    /**
     * Event originated from system-level operations (e.g., window focus, pause).
     */
    SYSTEM,
    
    /**
     * Event originated from game logic (e.g., automatic line clearing).
     */
    GAME_LOGIC
}