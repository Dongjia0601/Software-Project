package com.comp2042.game.themes;

import com.comp2042.game.LevelTheme;

import java.util.Collections;
import java.util.Map;

/**
 * Magic Castle theme implementation.
 * Features mystical blue and purple tones with magical atmosphere.
 */
public class MagicCastleTheme implements LevelTheme {

    private static final String THEME_ID = "magic_castle";
    private static final String THEME_NAME = "Magic Castle";
    private static final String BACKGROUND_IMAGE = "/images/backgrounds/magic_castle_bg.jpg";
    private static final String BACKGROUND_MUSIC = "/themes/magic_castle_music.mp3";
    private static final String PRIMARY_COLOR = "#4169E1"; // Royal Blue
    private static final String SECONDARY_COLOR = "#8A2BE2"; // Blue Violet
    private static final String ACCENT_COLOR = "#00BFFF"; // Deep Sky Blue
    private static final String GRADIENT_START_COLOR = "#1a0f2e"; // Dark Purple
    private static final String GRADIENT_END_COLOR = "#2d4a7c"; // Dark Blue
    private static final String TEXT_COLOR = "#E6E6FA"; // Lavender

    /**
     * Constructs a MagicCastleTheme instance.
     */
    public MagicCastleTheme() {
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
        return Collections.emptyMap(); // No theme-specific sound effects
    }

    @Override
    public String getTextColor() {
        return TEXT_COLOR;
    }
}