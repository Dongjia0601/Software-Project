package com.comp2042.view.manager;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Manages heads-up display (HUD) components including score, level, timers,
 * and statistics. Uses {@link UIHelper} for common UI operations.
 *
 * @author Dong, Jia
 */
public class HudManager {

    // Main statistics labels
    private Label scoreLabel;
    private Label highScoreLabel;
    private Label linesLabel;
    private Label levelLabel;
    private Label speedLabel;
    private Label timeLabel;
    private Label gameTitleLabel;

    // Level mode specific labels
    private Label leftTimerLabel;
    private Label leftProgressLabel;
    private Label leftSpeedLabel;
    private HBox leftStarDisplay;

    // Best statistics labels
    private Label bestScoreLabel;
    private Label bestTimeLabel;
    private VBox bestStatsBox;

    /**
     * Constructs a HudManager.
     */
    public HudManager() {
        // Constructor is intentionally empty - components will be set via setters
    }

    // ==================== Component Setters ====================

    public void setScoreLabel(Label scoreLabel) {
        this.scoreLabel = scoreLabel;
    }

    public void setHighScoreLabel(Label highScoreLabel) {
        this.highScoreLabel = highScoreLabel;
    }

    public void setLinesLabel(Label linesLabel) {
        this.linesLabel = linesLabel;
    }

    public void setLevelLabel(Label levelLabel) {
        this.levelLabel = levelLabel;
    }

    public void setSpeedLabel(Label speedLabel) {
        this.speedLabel = speedLabel;
    }

    public void setTimeLabel(Label timeLabel) {
        this.timeLabel = timeLabel;
    }

    public void setGameTitleLabel(Label gameTitleLabel) {
        this.gameTitleLabel = gameTitleLabel;
    }

    public void setLeftTimerLabel(Label leftTimerLabel) {
        this.leftTimerLabel = leftTimerLabel;
    }

    public void setLeftProgressLabel(Label leftProgressLabel) {
        this.leftProgressLabel = leftProgressLabel;
    }

    public void setLeftSpeedLabel(Label leftSpeedLabel) {
        this.leftSpeedLabel = leftSpeedLabel;
    }

    public void setLeftStarDisplay(HBox leftStarDisplay) {
        this.leftStarDisplay = leftStarDisplay;
    }

    public void setBestScoreLabel(Label bestScoreLabel) {
        this.bestScoreLabel = bestScoreLabel;
    }

    public void setBestTimeLabel(Label bestTimeLabel) {
        this.bestTimeLabel = bestTimeLabel;
    }

    public void setBestStatsBox(VBox bestStatsBox) {
        this.bestStatsBox = bestStatsBox;
    }

    // ==================== Update Methods ====================

    public void updateScore(int currentScore, int highScore) {
        if (scoreLabel != null) {
            scoreLabel.setText(String.valueOf(currentScore));
        }
        if (highScoreLabel != null) {
            highScoreLabel.setText("Best Score: " + highScore);
        }
    }

    public void updateLines(int linesCleared) {
        if (linesLabel != null) {
            linesLabel.setText(String.valueOf(linesCleared));
        }
    }

    public void updateLevel(int level) {
        if (levelLabel != null) {
            levelLabel.setText(String.valueOf(level));
        }
    }

    public void updateSpeed(int speedLevel) {
        if (speedLabel != null) {
            speedLabel.setText(speedLevel + "x");
        }
    }

    public void updateElapsedTime(long elapsedSeconds) {
        if (timeLabel != null) {
            long minutes = elapsedSeconds / 60;
            long seconds = elapsedSeconds % 60;
            timeLabel.setText(String.format("%d:%02d", minutes, seconds));
        }
    }

    public void updateProgress(int linesClearedInLevel, int targetLines) {
        if (leftProgressLabel != null) {
            String progressText = String.format("%d/%d", linesClearedInLevel, targetLines);
            leftProgressLabel.setText(progressText);

            double progress = targetLines == 0 ? 0 : (double) linesClearedInLevel / targetLines;
            if (progress >= 1.0) {
                leftProgressLabel.setStyle("-fx-text-fill: #00FF00; -fx-font-weight: bold; -fx-font-size: 32px;");
            } else if (progress >= 0.75) {
                leftProgressLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold; -fx-font-size: 32px;");
            } else {
                leftProgressLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 32px; -fx-text-fill: #FF0000;");
            }
        }
    }

    public void updateStarDisplay(int stars) {
        if (leftStarDisplay != null) {
            leftStarDisplay.getChildren().clear();

            for (int i = 0; i < 3; i++) {
                Label star = new Label(i < stars ? "★" : "☆");
                star.setStyle("-fx-font-size: 28px; " +
                              (i < stars ? "-fx-text-fill: #FFD700;" : "-fx-text-fill: #666666;"));
                leftStarDisplay.getChildren().add(star);
            }
        }
    }

    public void updateLevelSpeedDisplay(int levelId) {
        if (leftSpeedLabel != null) {
            double speed = 1.0 + Math.max(0, levelId - 1) * 0.2;
            leftSpeedLabel.setText(String.format("%.1fx", speed));
            leftSpeedLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");
        }
    }

    public void updateLevelTimer(int timeRemainingSeconds) {
        if (leftTimerLabel != null) {
            int minutes = Math.max(0, timeRemainingSeconds) / 60;
            int seconds = Math.max(0, timeRemainingSeconds) % 60;
            leftTimerLabel.setText(String.format("%d:%02d", minutes, seconds));

            if (timeRemainingSeconds <= 10) {
                leftTimerLabel.setStyle("-fx-text-fill: #FF0000; -fx-font-weight: bold; -fx-font-size: 32px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(255, 0, 0, 0.8), 10, 0, 0, 0);");
            } else if (timeRemainingSeconds <= 30) {
                leftTimerLabel.setStyle("-fx-text-fill: #FFA500; -fx-font-weight: bold; -fx-font-size: 32px;");
            } else {
                leftTimerLabel.setStyle("-fx-text-fill: #00FF00; -fx-font-weight: bold; -fx-font-size: 32px;");
            }
        }
    }

    public void updateBestStats(int bestScore, long bestTimeMillis) {
        if (bestScoreLabel != null) {
            bestScoreLabel.setText("Best Score: " + bestScore);
        }

        if (bestTimeLabel != null) {
            if (bestTimeMillis == Long.MAX_VALUE || bestTimeMillis <= 0) {
                bestTimeLabel.setText("Best Time: --:--");
            } else {
                long bestTimeSeconds = bestTimeMillis / 1000;
                long minutes = bestTimeSeconds / 60;
                long seconds = bestTimeSeconds % 60;
                String timeText = String.format("Best Time: %d:%02d", minutes, seconds);
                bestTimeLabel.setText(timeText);
            }
        }
    }

    public void setGameTitleForLevel(int levelId) {
        if (gameTitleLabel != null) {
            gameTitleLabel.setText("Level " + levelId);
        }
    }

    public void setCustomGameTitle(String customTitle) {
        if (gameTitleLabel != null) {
            gameTitleLabel.setText(customTitle);
        }
    }

    public void resetGameTitle() {
        if (gameTitleLabel != null) {
            gameTitleLabel.setText("TETRIS");
        }
    }

    public void showBestStats() {
        if (bestStatsBox != null) {
            bestStatsBox.setVisible(true);
            bestStatsBox.setManaged(true);
        }
    }

    public void hideBestStats() {
        if (bestStatsBox != null) {
            bestStatsBox.setVisible(false);
            bestStatsBox.setManaged(false);
        }
    }

    public void resetAllStats() {
        updateScore(0, 0);
        updateLines(0);
        updateLevel(1);
        updateSpeed(1);
        updateElapsedTime(0);
        updateProgress(0, 1);
        updateStarDisplay(0);
        resetGameTitle();
    }
}

