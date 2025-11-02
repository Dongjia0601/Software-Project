package com.comp2042.game.themes;

import com.comp2042.game.LevelTheme;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Future Warfare theme implementation.
 * Features high-tech military colors with futuristic battlefield atmosphere.
 */
public class FutureWarfareTheme implements LevelTheme {

    private static final String THEME_ID = "future_warfare";
    private static final String THEME_NAME = "Future Warfare";
    private static final String BACKGROUND_IMAGE = "/Future_War_bg.jpg";
    private static final String BACKGROUND_MUSIC = "/themes/future_warfare_music.mp3";
    private static final String PRIMARY_COLOR = "#00FFFF"; // Cyan
    private static final String SECONDARY_COLOR = "#FF4500"; // Orange Red
    private static final String ACCENT_COLOR = "#00FF00"; // Lime
    private static final String GRADIENT_START_COLOR = "#1a1a2e"; // Dark Navy
    private static final String GRADIENT_END_COLOR = "#16213e"; // Dark Blue
    private static final String TEXT_COLOR = "#00FFFF"; // Cyan

    private final Map<String, String> soundEffects;

    /**
     * Constructs a FutureWarfareTheme instance and initializes its sound effects map.
     */
    public FutureWarfareTheme() {
        Map<String, String> seMap = new HashMap<>();
        seMap.put("line_clear", "/sounds/tech_explosion.wav");
        seMap.put("block_land", "/sounds/metal_clank.wav");
        seMap.put("level_complete", "/sounds/mission_complete.wav");
        seMap.put("level_fail", "/sounds/alarm_critical.wav");
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
        return "{\"type\":\"laser\",\"color\":\"#00FFFF\",\"density\":\"high\"}";
    }
}