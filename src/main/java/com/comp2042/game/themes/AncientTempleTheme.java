package com.comp2042.game.themes;
import com.comp2042.game.LevelTheme;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Concrete implementation of LevelTheme for the "Ancient Temple" theme.
 * Provides specific visual and auditory assets for the Ancient Temple level.
 */
public class AncientTempleTheme implements LevelTheme {

    private static final String THEME_ID = "ancient_temple";
    private static final String THEME_NAME = "Ancient Temple";
    private static final String THEME_DESCRIPTION = "Explore the mysterious ruins of an ancient civilization";
    private static final String BACKGROUND_IMAGE = "/Ancient_temple_bg.jpg";
    private static final String BACKGROUND_MUSIC = "/themes/ancient_temple_music.mp3";
    private static final String PRIMARY_COLOR = "#D4AF37"; // Gold
    private static final String SECONDARY_COLOR = "#8B4513"; // Saddle Brown
    private static final String ACCENT_COLOR = "#FFD700"; // Bright Gold
    private static final String GRADIENT_START_COLOR = "#2C1810"; // Dark Brown
    private static final String GRADIENT_END_COLOR = "#5D4E37"; // Medium Brown
    private static final String TEXT_COLOR = "#F5E6D3"; // Light Cream

    private final Map<String, String> soundEffects;

    /**
     * Constructs an AncientTempleTheme instance and initializes its sound effects map.
     */
    public AncientTempleTheme() {
        Map<String, String> seMap = new HashMap<>();
        seMap.put("line_clear", "/sounds/ancient_stone_break.wav");
        seMap.put("block_land", "/sounds/stone_thud.wav");
        seMap.put("level_complete", "/sounds/ancient_gong.wav");
        seMap.put("level_fail", "/sounds/ancient_rumble.wav");
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
        return "{\"type\":\"dust\",\"color\":\"#D4AF37\",\"density\":\"low\"}";
    }
}