package com.comp2042.controller.strategy;

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
 * Unit tests for EndlessModeUIStrategy.
 */
class EndlessModeUIStrategyTest {

    private EndlessModeUIStrategy strategy;
    private EndlessModeUIManager endlessUI;
    private HudManager hudManager;
    private CommonUIManager commonUI;
    private VBox bestStatsBox;
    private Label gameTitleLabel;
    private Label levelLabel;
    private Label speedLabel;

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
        }
    }

    @BeforeEach
    void setUp() {
        bestStatsBox = new VBox();
        gameTitleLabel = new Label();
        levelLabel = new Label();
        speedLabel = new Label();
        
        endlessUI = new EndlessModeUIManager(
            new Label(), new Label(), new Label(), levelLabel, speedLabel, new Label(),
            gameTitleLabel, new Label(), new Label(), bestStatsBox, new VBox()
        );
        
        LevelModeUIManager levelUI = new LevelModeUIManager(
            new VBox(), new Label(), new Label(), new Label(), new HBox()
        );
        
        hudManager = new HudManager();
        commonUI = new CommonUIManager();
        
        strategy = new EndlessModeUIStrategy(endlessUI, hudManager);
    }

    @Test
    void testInitialize() {
        strategy.initialize(commonUI);
        
        assertTrue(bestStatsBox.isVisible());
        assertTrue(bestStatsBox.isManaged());
        assertEquals("ENDLESS MODE", gameTitleLabel.getText());
    }

    @Test
    void testOnGameStart() {
        strategy.onGameStart();
        
        assertEquals("1", levelLabel.getText());
        assertEquals("1x", speedLabel.getText());
    }

    @Test
    void testOnGameTick() {
        strategy.onGameStart();
        
        // Wait a bit to ensure time passes
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        strategy.onGameTick();
        
        // Verify elapsed time was updated (label should have time format)
        Label timeLabel = endlessUI.getTimeLabel();
        assertNotNull(timeLabel.getText());
        assertTrue(timeLabel.getText().matches("\\d+:\\d{2}"));
    }

    @Test
    void testUpdateScore() {
        strategy.updateScore(5000);
        
        Label scoreLabel = endlessUI.getScoreLabel();
        assertEquals("5000", scoreLabel.getText());
    }

    @Test
    void testUpdateLines_LevelProgression() {
        strategy.onGameStart();
        
        // 10 lines should trigger level 2
        strategy.updateLines(10);
        assertEquals("2", levelLabel.getText());
        
        // 20 lines should trigger level 3
        strategy.updateLines(20);
        assertEquals("3", levelLabel.getText());
    }

    @Test
    void testUpdateLines_SpeedProgression() {
        strategy.onGameStart();
        
        // Level 1-2: speed 1x
        strategy.updateLines(10);
        assertEquals("1x", speedLabel.getText());
        
        // Level 3-4: speed 2x
        strategy.updateLines(20);
        assertEquals("2x", speedLabel.getText());
        
        // Level 5-6: speed 3x
        strategy.updateLines(40);
        assertEquals("3x", speedLabel.getText());
    }

    @Test
    void testUpdateLines_NoLevelChange() {
        strategy.onGameStart();
        strategy.updateLines(5);
        
        // Should not trigger level update (still level 1)
        assertEquals("1", levelLabel.getText());
    }

    @Test
    void testUpdateLines_MaxSpeed() {
        strategy.onGameStart();
        
        // Level 15+ should cap at speed 10x
        strategy.updateLines(140); // Level 15
        assertEquals("10x", speedLabel.getText());
        
        strategy.updateLines(200); // Level 21, still should be 10x
        assertEquals("10x", speedLabel.getText());
    }
}

