package com.comp2042.view.manager;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Manages UI components specific to Endless Mode.
 *
 * @author Dong, Jia
 */
public class EndlessModeUIManager {
    private final Label scoreLabel;
    private final Label highScoreLabel;
    private final Label linesLabel;
    private final Label levelLabel;
    private final Label speedLabel;
    private final Label timeLabel;
    private final Label gameTitleLabel;
    private final Label bestScoreLabel;
    private final Label bestTimeLabel;
    private final VBox bestStatsBox;
    private final VBox statisticsBox;

    public EndlessModeUIManager(Label scoreLabel, Label highScoreLabel, Label linesLabel,
                               Label levelLabel, Label speedLabel, Label timeLabel,
                               Label gameTitleLabel, Label bestScoreLabel, Label bestTimeLabel,
                               VBox bestStatsBox, VBox statisticsBox) {
        this.scoreLabel = scoreLabel;
        this.highScoreLabel = highScoreLabel;
        this.linesLabel = linesLabel;
        this.levelLabel = levelLabel;
        this.speedLabel = speedLabel;
        this.timeLabel = timeLabel;
        this.gameTitleLabel = gameTitleLabel;
        this.bestScoreLabel = bestScoreLabel;
        this.bestTimeLabel = bestTimeLabel;
        this.bestStatsBox = bestStatsBox;
        this.statisticsBox = statisticsBox;
    }

    public Label getScoreLabel() {
        return scoreLabel;
    }

    public Label getHighScoreLabel() {
        return highScoreLabel;
    }

    public Label getLinesLabel() {
        return linesLabel;
    }

    public Label getLevelLabel() {
        return levelLabel;
    }

    public Label getSpeedLabel() {
        return speedLabel;
    }

    public Label getTimeLabel() {
        return timeLabel;
    }

    public Label getGameTitleLabel() {
        return gameTitleLabel;
    }

    public Label getBestScoreLabel() {
        return bestScoreLabel;
    }

    public Label getBestTimeLabel() {
        return bestTimeLabel;
    }

    public VBox getBestStatsBox() {
        return bestStatsBox;
    }

    public VBox getStatisticsBox() {
        return statisticsBox;
    }
}
