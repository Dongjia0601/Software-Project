package com.comp2042.controller.strategy;

import com.comp2042.model.mode.EndlessModeLeaderboard;
import com.comp2042.view.manager.CommonUIManager;
import com.comp2042.view.manager.EndlessModeUIManager;
import com.comp2042.view.manager.HudManager;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Endless Mode UI strategy implementation.
 * Handles score tracking, level/speed progression, and elapsed time.
 *
 * @author Dong, Jia
 */
public class EndlessModeUIStrategy implements GameModeUIStrategy {

    private final EndlessModeUIManager endlessUI;
    private final HudManager hudManager;
    private long gameStartTime;
    private int currentLevel = 1;
    private int currentSpeed = 1;

    public EndlessModeUIStrategy(EndlessModeUIManager endlessUI, HudManager hudManager) {
        this.endlessUI = endlessUI;
        this.hudManager = hudManager;
    }

    @Override
    public void initialize(CommonUIManager commonUI) {
        VBox bestStatsBox = endlessUI.getBestStatsBox();
        if (bestStatsBox != null) {
            bestStatsBox.setVisible(true);
            bestStatsBox.setManaged(true);
        }

        Label gameTitleLabel = endlessUI.getGameTitleLabel();
        if (gameTitleLabel != null) {
            gameTitleLabel.setText("ENDLESS MODE");
        }

        try {
            EndlessModeLeaderboard leaderboard = EndlessModeLeaderboard.getInstance();
            int highScore = leaderboard.getHighScore();
            hudManager.updateScore(0, highScore);
            hudManager.updateBestStats(highScore, 0L);
        } catch (Exception e) {
            System.err.println("Error loading endless mode stats: " + e.getMessage());
        }
    }

    @Override
    public void onGameStart() {
        gameStartTime = System.currentTimeMillis();
        currentLevel = 1;
        currentSpeed = 1;
        hudManager.updateLevel(1);
        hudManager.updateSpeed(1);
    }

    @Override
    public void onGameTick() {
        long elapsed = System.currentTimeMillis() - gameStartTime;
        long elapsedSeconds = elapsed / 1000;
        hudManager.updateElapsedTime(elapsedSeconds);
    }

    @Override
    public void updateScore(int score) {
        try {
            EndlessModeLeaderboard leaderboard = EndlessModeLeaderboard.getInstance();
            int highScore = leaderboard.getHighScore();
            hudManager.updateScore(score, highScore);
        } catch (Exception e) {
            hudManager.updateScore(score, score);
        }
    }

    @Override
    public void updateLines(int lines) {
        hudManager.updateLines(lines);

        int newLevel = (lines / 10) + 1;
        if (newLevel != currentLevel) {
            currentLevel = newLevel;
            hudManager.updateLevel(currentLevel);

            int newSpeed = Math.min(10, 1 + (currentLevel - 1) / 2);
            if (newSpeed != currentSpeed) {
                currentSpeed = newSpeed;
                hudManager.updateSpeed(currentSpeed);
            }
        }
    }

    // onPause(), onResume(), onGameOver() use default implementations from interface
}