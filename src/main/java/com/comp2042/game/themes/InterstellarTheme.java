package com.comp2042.game.themes;

import com.comp2042.game.LevelTheme;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Interstellar (Space Odyssey) theme implementation.
 * Features deep space colors with cosmic atmosphere.
 */
public class InterstellarTheme implements LevelTheme {

    private static final String THEME_ID = "interstellar";
    private static final String THEME_NAME = "Interstellar Odyssey";
    private static final String BACKGROUND_IMAGE = "/interstellar_bg.jpg";
    private static final String BACKGROUND_MUSIC = "/themes/interstellar_music.mp3";
    private static final String PRIMARY_COLOR = "#9370DB"; // Medium Purple
    private static final String SECONDARY_COLOR = "#4B0082"; // Indigo
    private static final String ACCENT_COLOR = "#FF69B4"; // Hot Pink
    private static final String GRADIENT_START_COLOR = "#0a0a1a"; // Almost Black
    private static final String GRADIENT_END_COLOR = "#1e1e3f"; // Dark Purple
    private static final String TEXT_COLOR = "#B0E0E6"; // Powder Blue

    private final Map<String, String> soundEffects;

    /**
     * Constructs an InterstellarTheme instance and initializes its sound effects map.
     */
    public InterstellarTheme() {
        Map<String, String> seMap = new HashMap<>();
        seMap.put("line_clear", "/sounds/cosmic_burst.wav");
        seMap.put("block_land", "/sounds/space_thud.wav");
        seMap.put("level_complete", "/sounds/space_triumph.wav");
        seMap.put("level_fail", "/sounds/void_echo.wav");
        this.soundEffects = Collections.unmodifiableMap(seMap);
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
        return soundEffects; // Return unmodifiable map
    }

    @Override
    public String getTextColor() {
        return TEXT_COLOR;
    }

    @Override
    public boolean hasParticleEffects() {
        return true; // Enable particle effects for this theme
    }

    @Override
    public String getParticleConfig() {
        return "{\"type\":\"stars\",\"color\":\"#FFFFFF\",\"density\":\"high\"}";
    }
}