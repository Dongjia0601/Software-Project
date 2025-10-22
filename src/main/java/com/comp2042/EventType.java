package com.comp2042;

/**
 * Enumeration of all possible game events in the Tetris game.
 * Defines the complete set of actions that can be triggered during gameplay.
 */
public enum EventType {
    /** Move brick down one position */
    DOWN,
    
    /** Move brick left one position */
    LEFT,
    
    /** Move brick right one position */
    RIGHT,
    
    /** Rotate brick clockwise */
    ROTATE,
    
    /** Rotate brick counter-clockwise */
    ROTATE_CCW,
    
    /** Hard drop - instantly drop brick to bottom */
    HARD_DROP,
    
    /** Hold current brick for later use */
    HOLD,
    
    /** Soft drop - faster downward movement */
    SOFT_DROP,
    
    /** Pause the game */
    PAUSE,
    
    /** Resume the game */
    RESUME,
    
    /** Start a new game */
    NEW_GAME,
    
    /** Quit the current game */
    QUIT
}
