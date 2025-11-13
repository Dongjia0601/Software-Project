package com.comp2042.game.themes;

import com.comp2042.game.LevelTheme;

import java.util.Collections;
import java.util.Map;

/**
 * Future Warfare theme implementation.
 * Features high-tech military colors with futuristic battlefield atmosphere.
 */
public class FutureWarfareTheme implements LevelTheme {

    private static final String THEME_ID = "future_warfare";
    private static final String THEME_NAME = "Future Warfare";
    private static final String BACKGROUND_IMAGE = "/images/backgrounds/Future_War_bg.jpg";
    private static final String BACKGROUND_MUSIC = "/themes/future_warfare_music.mp3";
    private static final String BASE_COLOR = "#00CC99";
    private static final String PRIMARY_COLOR = BASE_COLOR;
    private static final String SECONDARY_COLOR = "#009977";
    private static final String ACCENT_COLOR = "#00E6AA";
    private static final String GRADIENT_START_COLOR = "#003F32";
    private static final String GRADIENT_END_COLOR = "#001F1A";
    private static final String TEXT_COLOR = "#66FFD6";

    /**
     * Constructs a FutureWarfareTheme instance.
     */
    public FutureWarfareTheme() {
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