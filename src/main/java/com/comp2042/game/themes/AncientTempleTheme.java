package com.comp2042.game.themes;
import com.comp2042.game.LevelTheme;

import java.util.Collections;
import java.util.Map;

/**
 * Concrete implementation of LevelTheme for the "Ancient Temple" theme.
 * Provides specific visual and auditory assets for the Ancient Temple level.
 */
public class AncientTempleTheme implements LevelTheme {

    private static final String THEME_ID = "ancient_temple";
    private static final String THEME_NAME = "Ancient Temple";
    private static final String BACKGROUND_IMAGE = "/Ancient_temple_bg.jpg";
    private static final String BACKGROUND_MUSIC = "/themes/ancient_temple_music.mp3";
    private static final String PRIMARY_COLOR = "#D4AF37"; // Gold
    private static final String SECONDARY_COLOR = "#8B4513"; // Saddle Brown
    private static final String ACCENT_COLOR = "#FFD700"; // Bright Gold
    private static final String GRADIENT_START_COLOR = "#2C1810"; // Dark Brown
    private static final String GRADIENT_END_COLOR = "#5D4E37"; // Medium Brown
    private static final String TEXT_COLOR = "#F5E6D3"; // Light Cream

    /**
     * Constructs an AncientTempleTheme instance.
     */
    public AncientTempleTheme() {
    }

    @Override
    public String getThemeId() {
        return THEME_ID;
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
    public String getBackgroundMusic() {
        return BACKGROUND_MUSIC;
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

    @Override
    public String getGradientStartColor() {
        return GRADIENT_START_COLOR;
    }

    @Override
    public String getGradientEndColor() {
        return GRADIENT_END_COLOR;
    }

    @Override
    public Map<String, String> getSoundEffects() {
        return Collections.emptyMap();
    }

    @Override
    public String getTextColor() {
        return TEXT_COLOR;
    }
}