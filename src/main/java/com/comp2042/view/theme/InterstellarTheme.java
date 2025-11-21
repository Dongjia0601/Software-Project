package com.comp2042.view.theme;

/**
 * Interstellar (Space Odyssey) theme implementation.
 * Features deep space colors with cosmic atmosphere.
 */
public class InterstellarTheme implements LevelTheme {

    private static final String THEME_NAME = "Interstellar Odyssey";
    private static final String BACKGROUND_IMAGE = "/images/backgrounds/interstellar_bg.jpg";
    private static final String BASE_COLOR = "#1F4CFF";
    private static final String PRIMARY_COLOR = BASE_COLOR;
    private static final String SECONDARY_COLOR = BASE_COLOR;
    private static final String ACCENT_COLOR = "#6A8CFF";

    /**
     * Constructs an InterstellarTheme instance.
     */
    public InterstellarTheme() {
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
