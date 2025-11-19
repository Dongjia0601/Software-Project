package com.comp2042.model.mode;

import java.util.Map;

/**
 * Defines visual and audio assets for themed levels.
 * Enables distinct level experiences with custom backgrounds, music, and color schemes.
 */
public interface LevelTheme {

    /** Gets the unique theme ID. */
    String getThemeId();

    /** Gets the display name for UI. */
    String getThemeName();

    /** Gets the background image resource path. */
    String getBackgroundImage();

    /** Gets the background music resource path. */
    String getBackgroundMusic();

    /**
     * Gets the primary color for this theme in CSS format.
     *
     * @return the primary color (e.g., "#D4AF37")
     */
    String getPrimaryColor();

    /**
     * Gets the secondary color for this theme in CSS format.
     *
     * @return the secondary color (e.g., "#8B4513")
     */
    String getSecondaryColor();

    /**
     * Gets the accent color for this theme in CSS format.
     *
     * @return the accent color (e.g., "#FFD700")
     */
    String getAccentColor();

    /**
     * Gets the background gradient start color.
     *
     * @return the gradient start color in CSS format
     */
    String getGradientStartColor();

    /**
     * Gets the background gradient end color.
     *
     * @return the gradient end color in CSS format
     */
    String getGradientEndColor();

    /**
     * Gets a map of sound effect names to their resource paths.
     * Common keys: "line_clear", "block_land", "game_over".
     *
     * @return map of sound effect names to paths
     */
    Map<String, String> getSoundEffects();

    /**
     * Gets the text color for this theme.
     *
     * @return the text color in CSS format
     */
    String getTextColor();
}