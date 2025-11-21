package com.comp2042.view.manager;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LevelModeUIManager.
 */
class LevelModeUIManagerTest {

    private LevelModeUIManager levelUI;
    private VBox leftObjectiveBox;
    private Label leftTimerLabel;
    private Label leftProgressLabel;
    private Label leftSpeedLabel;
    private HBox leftStarDisplay;

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
        }
    }

    @BeforeEach
    void setUp() {
        leftObjectiveBox = new VBox();
        leftTimerLabel = new Label();
        leftProgressLabel = new Label();
        leftSpeedLabel = new Label();
        leftStarDisplay = new HBox();

        levelUI = new LevelModeUIManager(
            leftObjectiveBox, leftTimerLabel, leftProgressLabel, leftSpeedLabel, leftStarDisplay
        );
    }

    @Test
    void testGetLeftObjectiveBox() {
        assertSame(leftObjectiveBox, levelUI.getLeftObjectiveBox());
    }

    @Test
    void testGetLeftTimerLabel() {
        assertSame(leftTimerLabel, levelUI.getLeftTimerLabel());
    }

    @Test
    void testGetLeftProgressLabel() {
        assertSame(leftProgressLabel, levelUI.getLeftProgressLabel());
    }

    @Test
    void testGetLeftSpeedLabel() {
        assertSame(leftSpeedLabel, levelUI.getLeftSpeedLabel());
    }

    @Test
    void testGetLeftStarDisplay() {
        assertSame(leftStarDisplay, levelUI.getLeftStarDisplay());
    }
}

