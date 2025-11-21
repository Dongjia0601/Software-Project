package com.comp2042.view.theme;

/**
 * Concrete implementation of LevelTheme for the "Ancient Temple" theme.
 * Provides specific visual assets for the Ancient Temple level.
 */
public class AncientTempleTheme implements LevelTheme {

    private static final String THEME_NAME = "Ancient Temple";
    private static final String BACKGROUND_IMAGE = "/images/backgrounds/Ancient_temple_bg.jpg";
    private static final String PRIMARY_COLOR = "#D4AF37"; // Gold
    private static final String SECONDARY_COLOR = "#8B4513"; // Saddle Brown
    private static final String ACCENT_COLOR = "#FFD700"; // Bright Gold

    /**
     * Constructs an AncientTempleTheme instance.
     */
    public AncientTempleTheme() {
    }

    @Override
    public String getThemeName() {
        return THEME_NAME;
    }

    @Override
    public String getBackgroundImage() {
        return BACKGROUND_IMAGE;
    }

    @Override
    public String getPrimaryColor() {
        return PRIMARY_COLOR;
    }

    @Override
    public String getSecondaryColor() {
        return SECONDARY_COLOR;
    }

    @Override
    public String getAccentColor() {
        return ACCENT_COLOR;
    }
}
