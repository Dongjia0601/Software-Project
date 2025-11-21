package com.comp2042.view.manager;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EndlessModeUIManager.
 */
class EndlessModeUIManagerTest {

    private EndlessModeUIManager endlessUI;
    private Label scoreLabel;
    private Label highScoreLabel;
    private Label linesLabel;
    private Label levelLabel;
    private Label speedLabel;
    private Label timeLabel;
    private Label gameTitleLabel;
    private Label bestScoreLabel;
    private Label bestTimeLabel;
    private VBox bestStatsBox;
    private VBox statisticsBox;

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
        }
    }

    @BeforeEach
    void setUp() {
        scoreLabel = new Label();
        highScoreLabel = new Label();
        linesLabel = new Label();
        levelLabel = new Label();
        speedLabel = new Label();
        timeLabel = new Label();
        gameTitleLabel = new Label();
        bestScoreLabel = new Label();
        bestTimeLabel = new Label();
        bestStatsBox = new VBox();
        statisticsBox = new VBox();

        endlessUI = new EndlessModeUIManager(
            scoreLabel, highScoreLabel, linesLabel, levelLabel, speedLabel, timeLabel,
            gameTitleLabel, bestScoreLabel, bestTimeLabel, bestStatsBox, statisticsBox
        );
    }

    @Test
    void testGetScoreLabel() {
        assertSame(scoreLabel, endlessUI.getScoreLabel());
    }

    @Test
    void testGetHighScoreLabel() {
        assertSame(highScoreLabel, endlessUI.getHighScoreLabel());
    }

    @Test
    void testGetLinesLabel() {
        assertSame(linesLabel, endlessUI.getLinesLabel());
    }

    @Test
    void testGetLevelLabel() {
        assertSame(levelLabel, endlessUI.getLevelLabel());
    }

    @Test
    void testGetSpeedLabel() {
        assertSame(speedLabel, endlessUI.getSpeedLabel());
    }

    @Test
    void testGetTimeLabel() {
        assertSame(timeLabel, endlessUI.getTimeLabel());
    }

    @Test
    void testGetGameTitleLabel() {
        assertSame(gameTitleLabel, endlessUI.getGameTitleLabel());
    }

    @Test
    void testGetBestScoreLabel() {
        assertSame(bestScoreLabel, endlessUI.getBestScoreLabel());
    }

    @Test
    void testGetBestTimeLabel() {
        assertSame(bestTimeLabel, endlessUI.getBestTimeLabel());
    }

    @Test
    void testGetBestStatsBox() {
        assertSame(bestStatsBox, endlessUI.getBestStatsBox());
    }

    @Test
    void testGetStatisticsBox() {
        assertSame(statisticsBox, endlessUI.getStatisticsBox());
    }
}

