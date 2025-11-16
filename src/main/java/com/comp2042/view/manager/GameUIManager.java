package com.comp2042.view.manager;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Responsible for updating all UI components (labels, statistics panels).
 * 
 * <p>This class manages the display of game statistics and information on the UI,
 * including score, lines cleared, level, speed, time, progress, and star displays.</p>
 * 
 * <p><strong>Responsibilities:</strong></p>
 * <ul>
 *   <li>Update score and high score displays</li>
 *   <li>Update lines cleared counter</li>
 *   <li>Update level and speed displays</li>
 *   <li>Update elapsed time and countdown timers</li>
 *   <li>Update level mode progress indicators</li>
 *   <li>Update star ratings</li>
 *   <li>Update best statistics displays</li>
 * </ul>
 * 
 * <p><strong>Design Pattern:</strong> Extracted from GuiController to adhere to Single Responsibility Principle (SRP)</p>
 * 
 * @author Dong, Jia
 * @version Phase 3 - SRP Refactoring
 */
public class GameUIManager {
    
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
     * Constructs a GameUIManager.
     */
    public GameUIManager() {
        // Constructor is intentionally empty - components will be set via setters
    }
    
    // ==================== Component Setters ====================
    
    /**
     * Sets the score label component.
     * 
     * @param scoreLabel the score label
     */
    public void setScoreLabel(Label scoreLabel) {
        this.scoreLabel = scoreLabel;
    }
    
    /**
     * Sets the high score label component.
     * 
     * @param highScoreLabel the high score label
     */
    public void setHighScoreLabel(Label highScoreLabel) {
        this.highScoreLabel = highScoreLabel;
    }
    
    /**
     * Sets the lines label component.
     * 
     * @param linesLabel the lines label
     */
    public void setLinesLabel(Label linesLabel) {
        this.linesLabel = linesLabel;
    }
    
    /**
     * Sets the level label component.
     * 
     * @param levelLabel the level label
     */
    public void setLevelLabel(Label levelLabel) {
        this.levelLabel = levelLabel;
    }
    
    /**
     * Sets the speed label component.
     * 
     * @param speedLabel the speed label
     */
    public void setSpeedLabel(Label speedLabel) {
        this.speedLabel = speedLabel;
    }
    
    /**
     * Sets the time label component.
     * 
     * @param timeLabel the time label
     */
    public void setTimeLabel(Label timeLabel) {
        this.timeLabel = timeLabel;
    }
    
    /**
     * Sets the game title label component.
     * 
     * @param gameTitleLabel the game title label
     */
    public void setGameTitleLabel(Label gameTitleLabel) {
        this.gameTitleLabel = gameTitleLabel;
    }
    
    /**
     * Sets the level mode timer label component.
     * 
     * @param leftTimerLabel the timer label
     */
    public void setLeftTimerLabel(Label leftTimerLabel) {
        this.leftTimerLabel = leftTimerLabel;
    }
    
    /**
     * Sets the level mode progress label component.
     * 
     * @param leftProgressLabel the progress label
     */
    public void setLeftProgressLabel(Label leftProgressLabel) {
        this.leftProgressLabel = leftProgressLabel;
    }
    
    /**
     * Sets the level mode speed label component.
     * 
     * @param leftSpeedLabel the speed label
     */
    public void setLeftSpeedLabel(Label leftSpeedLabel) {
        this.leftSpeedLabel = leftSpeedLabel;
    }
    
    /**
     * Sets the level mode star display component.
     * 
     * @param leftStarDisplay the star display container
     */
    public void setLeftStarDisplay(HBox leftStarDisplay) {
        this.leftStarDisplay = leftStarDisplay;
    }
    
    /**
     * Sets the best score label component.
     * 
     * @param bestScoreLabel the best score label
     */
    public void setBestScoreLabel(Label bestScoreLabel) {
        this.bestScoreLabel = bestScoreLabel;
    }
    
    /**
     * Sets the best time label component.
     * 
     * @param bestTimeLabel the best time label
     */
    public void setBestTimeLabel(Label bestTimeLabel) {
        this.bestTimeLabel = bestTimeLabel;
    }
    
    /**
     * Sets the best statistics box component.
     * 
     * @param bestStatsBox the best stats container
     */
    public void setBestStatsBox(VBox bestStatsBox) {
        this.bestStatsBox = bestStatsBox;
    }
    
    // ==================== Update Methods ====================
    
    /**
     * Updates the score display.
     * 
     * @param currentScore the current score
     * @param highScore the high score
     */
    public void updateScore(int currentScore, int highScore) {
        if (scoreLabel != null) {
            scoreLabel.setText(String.valueOf(currentScore));
        }
        if (highScoreLabel != null) {
            highScoreLabel.setText("Best Score: " + highScore);
        }
    }
    
    /**
     * Updates the lines cleared display.
     * 
     * @param linesCleared the number of lines cleared
     */
    public void updateLines(int linesCleared) {
        if (linesLabel != null) {
            linesLabel.setText(String.valueOf(linesCleared));
        }
    }
    
    /**
     * Updates the level display.
     * 
     * @param level the current level
     */
    public void updateLevel(int level) {
        if (levelLabel != null) {
            levelLabel.setText(String.valueOf(level));
        }
    }
    
    /**
     * Updates the speed display.
     * 
     * @param speedLevel the current speed level
     */
    public void updateSpeed(int speedLevel) {
        if (speedLabel != null) {
            speedLabel.setText(speedLevel + "x");
        }
    }
    
    /**
     * Updates the elapsed time display.
     * 
     * @param elapsedSeconds the elapsed time in seconds
     */
    public void updateTime(int elapsedSeconds) {
        if (timeLabel != null) {
            int minutes = elapsedSeconds / 60;
            int seconds = elapsedSeconds % 60;
            timeLabel.setText(String.format("%d:%02d", minutes, seconds));
        }
    }
    
    /**
     * Updates the level mode progress display.
     * 
     * @param linesClearedInLevel the lines cleared in this level
     * @param targetLines the target lines for this level
     */
    public void updateProgress(int linesClearedInLevel, int targetLines) {
        if (leftProgressLabel != null) {
            String progressText = String.format("%d/%d", linesClearedInLevel, targetLines);
            leftProgressLabel.setText(progressText);
            
            // Change color based on progress
            double progress = (double) linesClearedInLevel / targetLines;
            if (progress >= 1.0) {
                leftProgressLabel.setStyle("-fx-text-fill: #00FF00; -fx-font-weight: bold; -fx-font-size: 32px;");
            } else if (progress >= 0.75) {
                leftProgressLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold; -fx-font-size: 32px;");
            } else {
                leftProgressLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 32px; -fx-text-fill: #FF0000;");
            }
        }
    }
    
    /**
     * Updates the star display for level mode.
     * 
     * @param stars the number of stars earned (0-3)
     */
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
    
    /**
     * Updates the level mode speed display.
     * 
     * @param levelId the level ID
     */
    public void updateLevelSpeedDisplay(int levelId) {
        if (leftSpeedLabel != null) {
            // Calculate speed based on level ID (simple display)
            double speed = 1.0 + (levelId - 1) * 0.2;
            leftSpeedLabel.setText(String.format("%.1fx", speed));
            leftSpeedLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");
        }
    }
    
    /**
     * Updates the level mode time limit display.
     * 
     * @param timeRemainingSeconds the time remaining in seconds
     */
    public void updateLevelTimer(int timeRemainingSeconds) {
        if (leftTimerLabel != null) {
            int minutes = timeRemainingSeconds / 60;
            int seconds = timeRemainingSeconds % 60;
            leftTimerLabel.setText(String.format("%d:%02d", minutes, seconds));
            
            // Change color based on remaining time (urgent warning)
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
    
    /**
     * Updates the best statistics display (score and time).
     * 
     * @param bestScore the best score achieved
     * @param bestTimeMillis the best time in milliseconds
     */
    public void updateBestStats(int bestScore, long bestTimeMillis) {
        if (bestScoreLabel != null) {
            bestScoreLabel.setText("Best Score: " + bestScore);
        }
        
        if (bestTimeLabel != null) {
            if (bestTimeMillis == Long.MAX_VALUE || bestTimeMillis <= 0) {
                bestTimeLabel.setText("Best Time: --:--");
            } else {
                int bestTimeSeconds = (int) (bestTimeMillis / 1000);
                int minutes = bestTimeSeconds / 60;
                int seconds = bestTimeSeconds % 60;
                String timeText = String.format("Best Time: %d:%02d", minutes, seconds);
                bestTimeLabel.setText(timeText);
            }
        }
    }
    
    /**
     * Sets the game title to indicate the active level.
     * 
     * @param levelId the numeric level identifier to display
     */
    public void setGameTitleForLevel(int levelId) {
        if (gameTitleLabel != null) {
            gameTitleLabel.setText("Level " + levelId);
        }
    }
    
    /**
     * Resets the game title to default.
     */
    public void resetGameTitle() {
        if (gameTitleLabel != null) {
            gameTitleLabel.setText("TETRIS");
        }
    }
    
    /**
     * Shows the best statistics box.
     */
    public void showBestStats() {
        if (bestStatsBox != null) {
            bestStatsBox.setVisible(true);
            bestStatsBox.setManaged(true);
        }
    }
    
    /**
     * Hides the best statistics box.
     */
    public void hideBestStats() {
        if (bestStatsBox != null) {
            bestStatsBox.setVisible(false);
            bestStatsBox.setManaged(false);
        }
    }
    
    /**
     * Resets all statistics displays to default values.
     */
    public void resetAllStats() {
        updateScore(0, 0);
        updateLines(0);
        updateLevel(1);
        updateSpeed(1);
        updateTime(0);
        updateProgress(0, 1);
        updateStarDisplay(0);
        resetGameTitle();
    }
}

