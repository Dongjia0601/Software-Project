package com.comp2042.view.theme;

/**
 * Concrete implementation of LevelTheme for the "Sunset Village" theme.
 * Features warm sunset colors with epic urban building atmosphere.
 * Inspired by the legendary cityscape at golden hour.
 */
public class SunsetCityTheme implements LevelTheme {

    private static final String THEME_NAME = "Sunset Village";
    private static final String BACKGROUND_IMAGE = "/images/backgrounds/sunset_village_bg.jpg";
    private static final String PRIMARY_COLOR = "#E94B3C";
    private static final String SECONDARY_COLOR = "#C2452D";
    private static final String ACCENT_COLOR = "#FFA500";

    /**
     * Constructs a SunsetCityTheme instance.
     */
    public SunsetCityTheme() {
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
