package com.comp2042.gameplay;

/**
 * Enumeration defining the different types of game modes available in the Tetris game.
 * This enum is used by the GameModeFactory to create appropriate game mode instances.
 * 
 * @author Dong, Jia.
 */
public enum GameModeType {

    /**
     * Endless mode - continuous gameplay with progressive difficulty.
     * Focuses on achieving the highest possible score.
     */
    ENDLESS("Endless Mode", "Continuous gameplay with increasing difficulty"),

    /**
     * Level mode - structured progression through predefined themed levels.
     * Each level has specific objectives and challenges.
     */
    LEVEL("Level Mode", "Progressive levels with unique challenges"),

    /**
     * Two player versus mode - human vs human competitive gameplay.
     * Features split-screen and battle mechanics.
     */
    TWO_PLAYER_VS("Two Player VS", "Human vs Human competitive mode");

    private final String displayName;
    private final String description;

    /**
     * Constructs a game mode type with display name and description.
     * @param displayName the name to display in the UI
     * @param description a brief description of the mode
     */
    GameModeType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Gets the display name for this game mode type.
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the description for this game mode type.
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the number of available game mode types.
     * @return the number of game mode types
     */
    public static int getModeCount() {
        return values().length;
    }
}