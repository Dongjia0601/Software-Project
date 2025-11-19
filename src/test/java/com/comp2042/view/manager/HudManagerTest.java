package com.comp2042.view.manager;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HudManagerTest {

    private HudManager hudManager;
    private Label scoreLabel;
    private Label highScoreLabel;
    private Label linesLabel;
    private Label levelLabel;
    private Label speedLabel;
    private Label timeLabel;
    private Label progressLabel;
    private Label timerLabel;
    private Label bestScoreLabel;
    private Label bestTimeLabel;
    private HBox starDisplay;
    private VBox bestStatsBox;

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
        }
    }

    @BeforeEach
    void setUp() {
        hudManager = new HudManager();
        scoreLabel = new Label();
        highScoreLabel = new Label();
        linesLabel = new Label();
        levelLabel = new Label();
        speedLabel = new Label();
        timeLabel = new Label();
        progressLabel = new Label();
        timerLabel = new Label();
        bestScoreLabel = new Label();
        bestTimeLabel = new Label();
        starDisplay = new HBox();
        bestStatsBox = new VBox();

        hudManager.setScoreLabel(scoreLabel);
        hudManager.setHighScoreLabel(highScoreLabel);
        hudManager.setLinesLabel(linesLabel);
        hudManager.setLevelLabel(levelLabel);
        hudManager.setSpeedLabel(speedLabel);
        hudManager.setTimeLabel(timeLabel);
        hudManager.setLeftProgressLabel(progressLabel);
        hudManager.setLeftTimerLabel(timerLabel);
        hudManager.setLeftStarDisplay(starDisplay);
        hudManager.setBestScoreLabel(bestScoreLabel);
        hudManager.setBestTimeLabel(bestTimeLabel);
        hudManager.setBestStatsBox(bestStatsBox);
    }

    @Test
    void updateScoreUpdatesLabels() {
        hudManager.updateScore(3450, 7890);
        assertEquals("3450", scoreLabel.getText());
        assertEquals("Best Score: 7890", highScoreLabel.getText());
    }

    @Test
    void updateProgressFormatsText() {
        hudManager.updateProgress(12, 20);
        assertEquals("12/20", progressLabel.getText());
        assertTrue(progressLabel.getStyle().contains("-fx-text-fill"));
    }

    @Test
    void updateLevelTimerFormatsAndStyles() {
        hudManager.updateLevelTimer(95);
        assertEquals("1:35", timerLabel.getText());

        hudManager.updateLevelTimer(25);
        assertEquals("0:25", timerLabel.getText());
        assertTrue(timerLabel.getStyle().contains("#FFA500"));

        hudManager.updateLevelTimer(5);
        assertEquals("0:05", timerLabel.getText());
        assertTrue(timerLabel.getStyle().contains("#FF0000"));
    }

    @Test
    void updateBestStatsFormatsTime() {
        hudManager.updateBestStats(5000, 123000);
        assertEquals("Best Score: 5000", bestScoreLabel.getText());
        assertEquals("Best Time: 2:03", bestTimeLabel.getText());
    }
}

