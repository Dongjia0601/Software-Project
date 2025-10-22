// File: src/main/java/com/comp2042/game/themes/SunsetCityTheme.java
package com.comp2042.game.themes;

import com.comp2042.game.LevelTheme;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Concrete implementation of LevelTheme for the "Sunset City" theme.
 * Features warm sunset colors with epic urban building atmosphere.
 * Inspired by the legendary cityscape at golden hour.
 */
public class SunsetCityTheme implements LevelTheme {

    private static final String THEME_ID = "sunset_city"; // Changed ID
    private static final String THEME_NAME = "Sunset City"; // Changed Name
    private static final String THEME_DESCRIPTION = "Build the legendary city skyline as the sun sets over the horizon"; // Changed Description
    private static final String BACKGROUND_IMAGE = "/themes/sunset_city_bg.jpg"; // Updated path
    private static final String BACKGROUND_MUSIC = "/themes/sunset_city_music.mp3"; // Updated path
    private static final String PRIMARY_COLOR = "#E94B3C"; // Sunset Red (unchanged)
    private static final String SECONDARY_COLOR = "#C2452D"; // Deep Orange Red (unchanged)
    private static final String ACCENT_COLOR = "#FFA500"; // Orange (unchanged)
    private static final String GRADIENT_START_COLOR = "#2C1810"; // Dark Brown (unchanged)
    private static final String GRADIENT_END_COLOR = "#5D3A3A"; // Dusty Rose (unchanged)
    private static final String TEXT_COLOR = "#FFF8DC"; // Cornsilk (unchanged)

    private final Map<String, String> soundEffects;

    /**
     * Constructs a SunsetCityTheme instance and initializes its sound effects map.
     */
    public SunsetCityTheme() { // Changed constructor name
        Map<String, String> seMap = new HashMap<>();
        seMap.put("line_clear", "/sounds/city_cheer.wav"); // Updated SFX path
        seMap.put("block_land", "/sounds/block_place.wav"); // Kept generic SFX
        seMap.put("level_complete", "/sounds/city_celebration.wav"); // Updated SFX path
        seMap.put("level_fail", "/sounds/city_sigh.wav"); // Updated SFX path
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
        return "{\"type\":\"confetti\",\"color\":\"multi\",\"density\":\"medium\"}";
    }
}