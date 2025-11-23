package com.comp2042.view.theme;

/**
 * Defines visual assets for themed levels.
 * Enables distinct level experiences with custom backgrounds and color schemes.
 * 
 * <p>This interface belongs to the View layer as it only contains presentation-related
 * data (images, colors) and does not affect game logic or physics.
 * 
 * @author Dong, Jia.
 */
public interface LevelTheme {

    /**
     * Gets the display name for this theme in the UI.
     * 
     * @return the theme name (e.g., "Ancient Temple")
     */
    String getThemeName();

    /**
     * Gets the background image resource path.
     * 
     * @return the path to the background image resource
     */
    String getBackgroundImage();

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
}

