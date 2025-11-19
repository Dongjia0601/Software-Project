package com.comp2042.controller.factory;

/**
 * Enum defining available game mode types for GameModeFactory.
 * Each type includes UI display name and description.
 * 
 * @author Dong, Jia.
 */
public enum GameModeType {

    /** Endless mode - continuous gameplay with progressive difficulty. */
    ENDLESS("Endless Mode", "Continuous gameplay with increasing difficulty"),

    /** Level mode - structured progression through themed levels. */
    LEVEL("Level Mode", "Progressive levels with unique challenges"),

    /** Two player versus mode - competitive split-screen gameplay. */
    TWO_PLAYER_VS("Two Player VS", "Human vs Human competitive mode");

    private final String displayName;
    private final String description;

    /**
     * Constructs a game mode type.
     * @param displayName UI display name
     * @param description Mode description
     */
    GameModeType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /** Gets the display name. */
    public String getDisplayName() {
        return displayName;
    }

    /** Gets the description. */
    public String getDescription() {
        return description;
    }

    /** Gets the count of available game modes. */
    public static int getModeCount() {
        return values().length;
    }
}