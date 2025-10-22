package com.comp2042.game.themes;

import com.comp2042.game.LevelTheme;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Magic Castle theme implementation.
 * Features mystical blue and purple tones with magical atmosphere.
 */
public class MagicCastleTheme implements LevelTheme {

    private static final String THEME_ID = "magic_castle";
    private static final String THEME_NAME = "Magic Castle";
    private static final String THEME_DESCRIPTION = "Ascend to the mystical castle floating among the clouds";
    private static final String BACKGROUND_IMAGE = "/themes/magic_castle_bg.jpg";
    private static final String BACKGROUND_MUSIC = "/themes/magic_castle_music.mp3";
    private static final String PRIMARY_COLOR = "#4169E1"; // Royal Blue
    private static final String SECONDARY_COLOR = "#8A2BE2"; // Blue Violet
    private static final String ACCENT_COLOR = "#00BFFF"; // Deep Sky Blue
    private static final String GRADIENT_START_COLOR = "#1a0f2e"; // Dark Purple
    private static final String GRADIENT_END_COLOR = "#2d4a7c"; // Dark Blue
    private static final String TEXT_COLOR = "#E6E6FA"; // Lavender

    private final Map<String, String> soundEffects;

    /**
     * Constructs a MagicCastleTheme instance and initializes its sound effects map.
     */
    public MagicCastleTheme() {
        Map<String, String> seMap = new HashMap<>();
        seMap.put("line_clear", "/sounds/magic_chime.wav");
        seMap.put("block_land", "/sounds/crystal_tap.wav");
        seMap.put("level_complete", "/sounds/magic_success.wav");
        seMap.put("level_fail", "/sounds/magic_dispel.wav");
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
    public String getThemeDescription() {
        return THEME_DESCRIPTION;
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
        return "{\"type\":\"sparkle\",\"color\":\"#00BFFF\",\"density\":\"medium\"}";
    }
}