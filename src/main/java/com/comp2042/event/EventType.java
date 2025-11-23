package com.comp2042.event;

/**
 * Enumeration of all possible game events in the Tetris game.
 * Defines the complete set of actions that can be triggered during gameplay.
 * 
 * @author Dong, Jia.
 */
public enum EventType {
    /** Moves the falling brick down one position. */
    DOWN,
    
    /** Moves the falling brick one position to the left. */
    LEFT,
    
    /** Moves the falling brick one position to the right. */
    RIGHT,
    
    /** Rotates the falling brick 90 degrees clockwise. */
    ROTATE,
    
    /** Rotates the falling brick 90 degrees counterclockwise. */
    ROTATE_CCW,
    
    /** Hard drop - instantly drops the brick to the bottom of the board. */
    HARD_DROP,
    
    /** Holds the current brick for later use (swaps with held brick if one exists). */
    HOLD,
    
    /** Soft drop - moves the brick down faster than normal speed. */
    SOFT_DROP,
    
    /** Pauses the game, freezing all timers and gameplay. */
    PAUSE,
    
    /** Resumes the game from a paused state. */
    RESUME,
    
    /** Starts a new game session, resetting the board and score. */
    NEW_GAME,
    
    /** Quits the current game and exits the application. */
    QUIT
}
