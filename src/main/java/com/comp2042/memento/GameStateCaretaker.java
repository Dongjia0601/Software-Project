package com.comp2042.memento;

import java.util.Stack;

/**
 * Caretaker class for managing game state mementos.
 * Implements the Memento Pattern's Caretaker role, responsible for
 * storing and managing mementos without accessing their internal state.
 * 
 * <p>This class provides functionality for:
 * <ul>
 *   <li>Saving game state snapshots</li>
 *   <li>Restoring previous game states</li>
 *   <li>Managing a history of game states (for undo functionality)</li>
 * </ul>
 * </p>
 * 
 * <p>Uses a Stack to maintain a history of game states, allowing
 * multiple undo operations if needed.</p>
 * 
 * @author Dong, Jia.
 */
public class GameStateCaretaker {
    
    private final Stack<GameStateMemento> mementoHistory;
    private static final int MAX_HISTORY_SIZE = 10; // Limit history to prevent memory issues
    
    /**
     * Constructs a GameStateCaretaker with an empty history.
     */
    public GameStateCaretaker() {
        this.mementoHistory = new Stack<>();
    }
    
    /**
     * Saves a game state memento to the history.
     * If history is full, removes the oldest memento.
     * 
     * @param memento the game state memento to save
     */
    public void saveMemento(GameStateMemento memento) {
        if (memento == null) {
            return;
        }
        
        mementoHistory.push(memento);
        
        // Limit history size to prevent memory issues
        if (mementoHistory.size() > MAX_HISTORY_SIZE) {
            mementoHistory.remove(0); // Remove oldest
        }
    }
    
    /**
     * Retrieves and removes the most recent game state memento.
     * 
     * @return the most recent memento, or null if history is empty
     */
    public GameStateMemento getMemento() {
        if (mementoHistory.isEmpty()) {
            return null;
        }
        return mementoHistory.pop();
    }
    
    /**
     * Retrieves the most recent game state memento without removing it.
     * 
     * @return the most recent memento, or null if history is empty
     */
    public GameStateMemento peekMemento() {
        if (mementoHistory.isEmpty()) {
            return null;
        }
        return mementoHistory.peek();
    }
    
    /**
     * Checks if there are any saved mementos.
     * 
     * @return true if history is not empty, false otherwise
     */
    public boolean hasMemento() {
        return !mementoHistory.isEmpty();
    }
    
    /**
     * Gets the number of saved mementos in history.
     * 
     * @return the number of mementos
     */
    public int getHistorySize() {
        return mementoHistory.size();
    }
    
    /**
     * Clears all saved mementos from history.
     */
    public void clearHistory() {
        mementoHistory.clear();
    }
    
    /**
     * Gets the maximum history size.
     * 
     * @return the maximum number of mementos that can be stored
     */
    public int getMaxHistorySize() {
        return MAX_HISTORY_SIZE;
    }
}

