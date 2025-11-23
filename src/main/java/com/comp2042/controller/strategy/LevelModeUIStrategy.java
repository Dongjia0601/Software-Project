package com.comp2042.controller.strategy;

import com.comp2042.model.mode.LevelMode;
import com.comp2042.view.manager.CommonUIManager;
import com.comp2042.view.manager.HudManager;
import com.comp2042.view.manager.LevelModeUIManager;
import javafx.scene.layout.VBox;

/**
 * Level Mode UI strategy implementation.
 * Handles progress tracking, star rating, and level-specific displays.
 *
 * @author Dong, Jia
 */
public class LevelModeUIStrategy implements GameModeUIStrategy {

    private final LevelModeUIManager levelUI;
    private final HudManager hudManager;
    private LevelMode currentLevel;
    private int targetLines;

    public LevelModeUIStrategy(LevelModeUIManager levelUI, HudManager hudManager, LevelMode level) {
        this.levelUI = levelUI;
        this.hudManager = hudManager;
        this.currentLevel = level;
        this.targetLines = level != null ? level.getTargetLines() : 10;
    }

    @Override
    public void initialize(CommonUIManager commonUI) {
        VBox leftObjectiveBox = levelUI.getLeftObjectiveBox();
        if (leftObjectiveBox != null) {
            leftObjectiveBox.setVisible(true);
            leftObjectiveBox.setManaged(true);
        }

        if (currentLevel != null) {
            hudManager.setGameTitleForLevel(currentLevel.getLevelId());
            hudManager.updateProgress(0, targetLines);
            hudManager.updateLevelSpeedDisplay(currentLevel.getLevelId());
            hudManager.updateStarDisplay(0);
        }
    }

    @Override
    public void onGameStart() {
        hudManager.updateProgress(0, targetLines);
    }

    // onGameTick(), updateBrickDisplay(), updateScore() use default implementations from interface

    @Override
    public void updateLines(int lines) {
        hudManager.updateProgress(lines, targetLines);

        int stars = calculateStars(lines, targetLines);
        hudManager.updateStarDisplay(stars);
    }

    // onPause(), onResume(), onGameOver() use default implementations from interface

    private int calculateStars(int lines, int target) {
        if (target == 0) return 0;
        double ratio = (double) lines / target;
        if (ratio >= 1.5) return 3;
        if (ratio >= 1.2) return 2;
        if (ratio >= 1.0) return 1;
        return 0;
    }

    public void setCurrentLevel(LevelMode level) {
        this.currentLevel = level;
        this.targetLines = level != null ? level.getTargetLines() : 10;
    }
}