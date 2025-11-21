package com.comp2042.view.manager;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Manages UI components specific to Level Mode.
 *
 * @author Dong, Jia
 */
public class LevelModeUIManager {
    private final VBox leftObjectiveBox;
    private final Label leftTimerLabel;
    private final Label leftProgressLabel;
    private final Label leftSpeedLabel;
    private final HBox leftStarDisplay;

    public LevelModeUIManager(VBox leftObjectiveBox, Label leftTimerLabel,
                             Label leftProgressLabel, Label leftSpeedLabel,
                             HBox leftStarDisplay) {
        this.leftObjectiveBox = leftObjectiveBox;
        this.leftTimerLabel = leftTimerLabel;
        this.leftProgressLabel = leftProgressLabel;
        this.leftSpeedLabel = leftSpeedLabel;
        this.leftStarDisplay = leftStarDisplay;
    }

    public VBox getLeftObjectiveBox() {
        return leftObjectiveBox;
    }

    public Label getLeftTimerLabel() {
        return leftTimerLabel;
    }

    public Label getLeftProgressLabel() {
        return leftProgressLabel;
    }

    public Label getLeftSpeedLabel() {
        return leftSpeedLabel;
    }

    public HBox getLeftStarDisplay() {
        return leftStarDisplay;
    }
}
