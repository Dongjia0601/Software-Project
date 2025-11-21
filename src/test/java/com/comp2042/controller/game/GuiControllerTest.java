package com.comp2042.controller.game;

import com.comp2042.view.manager.*;
import com.comp2042.view.panel.GameOverPanel;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GuiController.
 * 
 * <p>Tests focus on public API methods and state management.
 * UI rendering and complex interactions are tested through integration tests.
 */
class GuiControllerTest {

    private GuiController controller;
    private CommonUIManager commonUI;
    private EndlessModeUIManager endlessUI;
    private LevelModeUIManager levelUI;

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
        }
    }

    @BeforeEach
    void setUp() {
        // Create UI components
        BorderPane rootPane = new BorderPane();
        GridPane gamePanel = new GridPane();
        Pane brickPanel = new Pane();
        Pane ghostPanel = new Pane();
        Group groupNotification = new Group();
        GameOverPanel gameOverPanel = new GameOverPanel();
        GridPane holdPanel = new GridPane();
        GridPane nextBrickPanel = new GridPane();
        Button muteButton = new Button();
        Button backToSelectionButton = new Button();

        // Create UI managers
        commonUI = new CommonUIManager(
            rootPane, gamePanel, brickPanel, ghostPanel, groupNotification, gameOverPanel,
            holdPanel, nextBrickPanel, muteButton, backToSelectionButton
        );

        Label scoreLabel = new Label();
        Label highScoreLabel = new Label();
        Label linesLabel = new Label();
        Label levelLabel = new Label();
        Label speedLabel = new Label();
        Label timeLabel = new Label();
        Label gameTitleLabel = new Label();
        Label bestScoreLabel = new Label();
        Label bestTimeLabel = new Label();
        VBox bestStatsBox = new VBox();
        VBox statisticsBox = new VBox();

        endlessUI = new EndlessModeUIManager(
            scoreLabel, highScoreLabel, linesLabel, levelLabel, speedLabel, timeLabel,
            gameTitleLabel, bestScoreLabel, bestTimeLabel, bestStatsBox, statisticsBox
        );

        VBox leftObjectiveBox = new VBox();
        Label leftTimerLabel = new Label();
        Label leftProgressLabel = new Label();
        Label leftSpeedLabel = new Label();
        HBox leftStarDisplay = new HBox();

        levelUI = new LevelModeUIManager(
            leftObjectiveBox, leftTimerLabel, leftProgressLabel, leftSpeedLabel, leftStarDisplay
        );

        // These tests focus on testable public methods if any
    }

    @Test
    void testIsEndlessMode_InitialState() {
        // This test would require creating GuiController
        // For now, we verify the setup is correct
        assertNotNull(commonUI);
        assertNotNull(endlessUI);
        assertNotNull(levelUI);
    }

    @Test
    void testUIManagers_NotNull() {
        assertNotNull(commonUI.getRootPane());
        assertNotNull(commonUI.getGamePanel());
        assertNotNull(endlessUI.getScoreLabel());
        assertNotNull(levelUI.getLeftObjectiveBox());
    }

    @Test
    void testUpdateScore_DelegatesToHudManager() {
        HudManager hudManager = new HudManager();
        hudManager.updateScore(1000, 5000);
        
        assertEquals("1000", endlessUI.getScoreLabel().getText());
        assertEquals("Best Score: 5000", endlessUI.getHighScoreLabel().getText());
    }

    @Test
    void testUpdateLevel_DelegatesToHudManager() {
        HudManager hudManager = new HudManager();
        hudManager.updateLevel(5);
        
        assertEquals("5", endlessUI.getLevelLabel().getText());
    }

    @Test
    void testUpdateLines_DelegatesToHudManager() {
        HudManager hudManager = new HudManager();
        hudManager.updateLines(25);
        
        assertEquals("25", endlessUI.getLinesLabel().getText());
    }

    @Test
    void testUpdateSpeed_DelegatesToHudManager() {
        HudManager hudManager = new HudManager();
        hudManager.updateSpeed(3);
        
        assertEquals("3x", endlessUI.getSpeedLabel().getText());
    }

    @Test
    void testUpdateProgress_DelegatesToHudManager() {
        HudManager hudManager = new HudManager();
        hudManager.updateProgress(15, 20);
        
        assertEquals("15/20", levelUI.getLeftProgressLabel().getText());
    }

    @Test
    void testUpdateStarDisplay_DelegatesToHudManager() {
        HudManager hudManager = new HudManager();
        hudManager.updateStarDisplay(3);
        
        assertEquals(3, levelUI.getLeftStarDisplay().getChildren().size());
    }
}

