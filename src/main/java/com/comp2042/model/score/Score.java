package com.comp2042.model.score;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Game score manager with JavaFX property binding support.
 * Enables automatic GUI updates when score changes.
 * 
 * @author Dong, Jia.
 */
public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);

    /**
     * Gets the score property for GUI binding.
     *
     * @return IntegerProperty for score
     */
    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * Adds points to the current score.
     *
     * @param i Amount to add
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
     * @return Current score
     */
    public int getScore() {
        return score.getValue();
    }
}