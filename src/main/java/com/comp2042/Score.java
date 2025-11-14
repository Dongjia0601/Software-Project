package com.comp2042;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Manages the game score using a JavaFX IntegerProperty for binding to the GUI.
 * 
 * @author Dong, Jia.
 */
public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0); // The current score property

    /**
     * Gets the underlying IntegerProperty representing the score.
     * This allows the GUI to bind to the score value for automatic updates.
     *
     * @return The IntegerProperty for the score.
     */
    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * Adds a specified amount to the current score.
     *
     * @param i The amount to add to the score.
     */
    public void add(int i){
        score.setValue(score.getValue() + i);
    }

    /**
     * Resets the score back to zero.
     */
    public void reset() {
        score.setValue(0);
    }

    /**
     * Gets the current score value.
     *
     * @return The current score value.
     */
    public int getScore() {
        return score.getValue();
    }
}