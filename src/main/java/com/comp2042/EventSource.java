package com.comp2042;

/**
 * Enum indicating the source of a game event.
 */
public enum EventSource {
    /**
     * Event originated from user input (e.g., keyboard press).
     */
    USER,
    /**
     * Event originated from an internal game process (e.g., automatic descent).
     */
    THREAD
}