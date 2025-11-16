package com.comp2042.model.mode;

import java.util.Map;

/**
 * Defines the visual and auditory assets for a game level theme.
 *
 * <p>Used to provide distinct appearances and sounds for different levels,
 * contributing to the "New Playable Levels" requirement by enabling themed experiences.
 */
public interface LevelTheme {

    /**
     * Gets the unique identifier for this theme.
     *
     * @return the theme ID (e.g., "ancient_temple")
     */
    String getThemeId();

    /**
     * Gets the display name of this theme.
     *
     * @return the human-readable theme name (e.g., "Ancient Temple")
     */
    String getThemeName();

    /**
     * Gets the resource path to the background image.
     *
     * @return the path to background image (e.g., "/themes/ancient_temple_bg.png")
     */
    String getBackgroundImage();

    /**
     * Gets the resource path to the background music.
     *
     * @return the path to background music (e.g., "/themes/ancient_temple_music.mp3")
     */
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