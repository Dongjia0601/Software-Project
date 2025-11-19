package com.comp2042.model.state;

/**
 * Context interface for managing GameState transitions (State Pattern).
 * Decouples state objects from controller implementations, enabling flexible session types.
 */
public interface GameStateContext {

    /**
     * Transitions to a new game state.
     *
     * @param newState State to activate
     */
    void transitionToState(GameState newState);
}

