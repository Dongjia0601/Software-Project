package com.comp2042.controller.strategy;

import com.comp2042.model.mode.LevelMode;
import com.comp2042.view.manager.CommonUIManager;
import com.comp2042.view.manager.EndlessModeUIManager;
import com.comp2042.view.manager.HudManager;
import com.comp2042.view.manager.LevelModeUIManager;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LevelModeUIStrategy.
 */
class LevelModeUIStrategyTest {

    private LevelModeUIStrategy strategy;
    private LevelModeUIManager levelUI;
    private HudManager hudManager;
    private CommonUIManager commonUI;
    private LevelMode level;
    private VBox leftObjectiveBox;
    private Label progressLabel;
    private HBox starDisplay;
    private Label gameTitleLabel;

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
        }
    }

    @BeforeEach
    void setUp() {
        leftObjectiveBox = new VBox();
        progressLabel = new Label();
        starDisplay = new HBox();
        gameTitleLabel = new Label();
        
        levelUI = new LevelModeUIManager(
            leftObjectiveBox, new Label(), progressLabel, new Label(), starDisplay
        );
        
        EndlessModeUIManager endlessUI = new EndlessModeUIManager(
            new Label(), new Label(), new Label(), new Label(), new Label(), new Label(),
            gameTitleLabel, new Label(), new Label(), new VBox(), new VBox()
        );
        
        hudManager = new HudManager(endlessUI, levelUI);
        commonUI = new CommonUIManager(
            null, null, null, null, null, null, null, null, null
        );
        
        // Create a simple LevelMode for testing
        com.comp2042.view.theme.LevelTheme theme = new com.comp2042.view.theme.AncientTempleTheme();
        level = new LevelMode(1, "Test Level", theme, 600, 20, 60, 200, 400, 600, 150);
        strategy = new LevelModeUIStrategy(levelUI, hudManager, level);
    }

    @Test
    void testInitialize() {
        strategy.initialize(commonUI);
        
        assertTrue(leftObjectiveBox.isVisible());
        assertTrue(leftObjectiveBox.isManaged());
        assertEquals("Level 1", gameTitleLabel.getText());
        assertEquals("0/20", progressLabel.getText());
        assertEquals(0, starDisplay.getChildren().size());
    }

    @Test
    void testInitialize_WithNullLevel() {
        LevelModeUIStrategy nullLevelStrategy = new LevelModeUIStrategy(levelUI, hudManager, null);
        nullLevelStrategy.initialize(commonUI);
        
        // Should not throw exception
        assertTrue(leftObjectiveBox.isVisible());
    }

    @Test
    void testOnGameStart() {
        strategy.onGameStart();
        
        assertEquals("0/20", progressLabel.getText());
    }

    @Test
    void testUpdateLines() {
        strategy.updateLines(10);
        
        assertEquals("10/20", progressLabel.getText());
        assertEquals(3, starDisplay.getChildren().size()); // Should have 3 stars
    }

    @Test
    void testUpdateLines_StarCalculation_0Stars() {
        strategy.updateLines(15); // 15/20 = 0.75, less than 1.0
        
        assertEquals(3, starDisplay.getChildren().size());
        Label star0 = (Label) starDisplay.getChildren().get(0);
        assertEquals("☆", star0.getText()); // 0 stars
    }

    @Test
    void testUpdateLines_StarCalculation_1Star() {
        strategy.updateLines(20); // 20/20 = 1.0
        
        assertEquals(3, starDisplay.getChildren().size());
        Label star0 = (Label) starDisplay.getChildren().get(0);
        assertEquals("★", star0.getText()); // 1 star
        Label star1 = (Label) starDisplay.getChildren().get(1);
        assertEquals("☆", star1.getText());
    }

    @Test
    void testUpdateLines_StarCalculation_2Stars() {
        strategy.updateLines(24); // 24/20 = 1.2
        
        assertEquals(3, starDisplay.getChildren().size());
        Label star0 = (Label) starDisplay.getChildren().get(0);
        Label star1 = (Label) starDisplay.getChildren().get(1);
        assertEquals("★", star0.getText()); // 2 stars
        assertEquals("★", star1.getText());
        Label star2 = (Label) starDisplay.getChildren().get(2);
        assertEquals("☆", star2.getText());
    }

    @Test
    void testUpdateLines_StarCalculation_3Stars() {
        strategy.updateLines(30); // 30/20 = 1.5
        
        assertEquals(3, starDisplay.getChildren().size());
        Label star0 = (Label) starDisplay.getChildren().get(0);
        Label star1 = (Label) starDisplay.getChildren().get(1);
        Label star2 = (Label) starDisplay.getChildren().get(2);
        assertEquals("★", star0.getText()); // 3 stars
        assertEquals("★", star1.getText());
        assertEquals("★", star2.getText());
    }

    @Test
    void testSetCurrentLevel() {
        com.comp2042.view.theme.LevelTheme theme = new com.comp2042.view.theme.AncientTempleTheme();
        LevelMode newLevel = new LevelMode(2, "Test Level 2", theme, 500, 30, 90, 300, 600, 900, 200);
        strategy.setCurrentLevel(newLevel);
        strategy.updateLines(15);
        
        assertEquals("15/30", progressLabel.getText());
    }

    @Test
    void testSetCurrentLevel_Null() {
        strategy.setCurrentLevel(null);
        strategy.updateLines(10);
        
        // Should use default targetLines of 10
        assertEquals("10/10", progressLabel.getText());
    }
}

