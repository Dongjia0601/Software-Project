package com.comp2042.model.state;

/**
 * Provides a minimal contract for classes that manage {@link GameState}
 * transitions. Decouples concrete controller implementations from the
 * state objects, allowing alternative session/context implementations
 * (e.g., single player, challenge modes) without rewriting the states.
 */
public interface GameStateContext {

    /**
     * Requests a transition to the supplied state.
     *
     * @param newState the new game state to activate
     */
    void transitionToState(GameState newState);
}

