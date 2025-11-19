package com.comp2042.model.savestate;

import java.util.Stack;

/**
 * Caretaker managing game state memento history (Memento Pattern).
 * Stores and restores snapshots without accessing their internal state.
 * Maintains limited history stack (max 10) for undo operations.
 * 
 * @author Dong, Jia.
 */
public class GameStateCaretaker {
    
    private final Stack<GameStateMemento> mementoHistory;
    private static final int MAX_HISTORY_SIZE = 10; // Limit history to prevent memory issues
    
    /** Constructs a GameStateCaretaker with empty history. */
    public GameStateCaretaker() {
        this.mementoHistory = new Stack<>();
    }
    
    /**
     * Saves a memento to history. Removes oldest if exceeding max size.
     * 
     * @param memento Memento to save
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

