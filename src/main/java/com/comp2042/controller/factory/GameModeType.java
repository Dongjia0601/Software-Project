package com.comp2042.controller.factory;

/**
 * Enumeration of available game mode types used by {@link GameModeFactory}
 * to create appropriate game mode implementations.
 * 
 * <p>Each enum value represents a distinct gameplay experience:
 * <ul>
 *   <li>{@link #ENDLESS}: Continuous gameplay with progressive difficulty</li>
 *   <li>{@link #LEVEL}: Structured progression through themed levels</li>
 *   <li>{@link #TWO_PLAYER_VS}: Competitive split-screen multiplayer</li>
 * </ul>
 * 
 * @author Dong, Jia.
 */
public enum GameModeType {

    /** Endless mode - continuous gameplay with progressive difficulty. */
    ENDLESS,

    /** Level mode - structured progression through themed levels. */
    LEVEL,

    /** Two-Player Mode - competitive split-screen gameplay. */
    TWO_PLAYER_VS
}