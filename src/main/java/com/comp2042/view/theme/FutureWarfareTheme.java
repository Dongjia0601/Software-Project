package com.comp2042.view.theme;

/**
 * Future Warfare theme implementation.
 * Features high-tech military colors with futuristic battlefield atmosphere.
 */
public class FutureWarfareTheme implements LevelTheme {

    private static final String THEME_NAME = "Future Warfare";
    private static final String BACKGROUND_IMAGE = "/images/backgrounds/Future_War_bg.jpg";
    private static final String BASE_COLOR = "#00CC99";
    private static final String PRIMARY_COLOR = BASE_COLOR;
    private static final String SECONDARY_COLOR = "#009977";
    private static final String ACCENT_COLOR = "#00E6AA";

    /**
     * Constructs a FutureWarfareTheme instance.
     */
    public FutureWarfareTheme() {
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
