package com.comp2042.model.mode.themes;

import com.comp2042.model.mode.LevelTheme;

import java.util.Collections;
import java.util.Map;

/**
 * Concrete implementation of LevelTheme for the "Sunset Village" theme.
 * Features warm sunset colors with epic urban building atmosphere.
 * Inspired by the legendary cityscape at golden hour.
 */
public class SunsetCityTheme implements LevelTheme {

    private static final String THEME_ID = "sunset_city";
    private static final String THEME_NAME = "Sunset Village";
    private static final String BACKGROUND_IMAGE = "/images/backgrounds/sunset_village_bg.jpg";
    private static final String BACKGROUND_MUSIC = "/themes/sunset_city_music.mp3";
    private static final String PRIMARY_COLOR = "#E94B3C";
    private static final String SECONDARY_COLOR = "#C2452D";
    private static final String ACCENT_COLOR = "#FFA500";
    private static final String GRADIENT_START_COLOR = "#2C1810";
    private static final String GRADIENT_END_COLOR = "#5D3A3A";
    private static final String TEXT_COLOR = "#FFF8DC";

    /**
     * Constructs a SunsetCityTheme instance.
     */
    public SunsetCityTheme() {
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