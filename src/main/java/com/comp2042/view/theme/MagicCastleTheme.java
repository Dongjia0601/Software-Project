package com.comp2042.view.theme;

/**
 * Magic Castle theme implementation.
 * Features mystical blue and purple tones with magical atmosphere.
 */
public class MagicCastleTheme implements LevelTheme {

    private static final String THEME_NAME = "Magic Castle";
    private static final String BACKGROUND_IMAGE = "/images/backgrounds/magic_castle_bg.jpg";
    private static final String BASE_COLOR = "#3A4CFF";
    private static final String PRIMARY_COLOR = BASE_COLOR;
    private static final String SECONDARY_COLOR = BASE_COLOR;
    private static final String ACCENT_COLOR = "#91A3FF";

    /**
     * Constructs a MagicCastleTheme instance.
     */
    public MagicCastleTheme() {
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
