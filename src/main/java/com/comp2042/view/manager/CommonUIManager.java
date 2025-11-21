package com.comp2042.view.manager;

import com.comp2042.view.panel.GameOverPanel;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

/**
 * Manages UI components shared across all game modes.
 *
 * @author Dong, Jia
 */
public class CommonUIManager {
    private final BorderPane rootPane;
    private final GridPane gamePanel;
    private final Pane brickPanel;
    private final Pane ghostPanel;
    private final Group groupNotification;
    private final GameOverPanel gameOverPanel;
    private final GridPane holdPanel;
    private final GridPane nextBrickPanel;
    private final Button muteButton;
    private final Button backToSelectionButton;

    public CommonUIManager(BorderPane rootPane, GridPane gamePanel, Pane brickPanel, Pane ghostPanel,
                          Group groupNotification, GameOverPanel gameOverPanel,
                          GridPane holdPanel, GridPane nextBrickPanel,
                          Button muteButton, Button backToSelectionButton) {
        this.rootPane = rootPane;
        this.gamePanel = gamePanel;
        this.brickPanel = brickPanel;
        this.ghostPanel = ghostPanel;
        this.groupNotification = groupNotification;
        this.gameOverPanel = gameOverPanel;
        this.holdPanel = holdPanel;
        this.nextBrickPanel = nextBrickPanel;
        this.muteButton = muteButton;
        this.backToSelectionButton = backToSelectionButton;
    }

    /**
     * Constructor for testing purposes with null components.
     */
    public CommonUIManager() {
        this(null, null, null, null, null, null, null, null, null, null);
    }

    public BorderPane getRootPane() {
        return rootPane;
    }

    public GridPane getGamePanel() {
        return gamePanel;
    }

    public Pane getBrickPanel() {
        return brickPanel;
    }

    public Pane getGhostPanel() {
        return ghostPanel;
    }

    public Group getGroupNotification() {
        return groupNotification;
    }

    public GameOverPanel getGameOverPanel() {
        return gameOverPanel;
    }

    public GridPane getHoldPanel() {
        return holdPanel;
    }

    public GridPane getNextBrickPanel() {
        return nextBrickPanel;
    }

    public Button getMuteButton() {
        return muteButton;
    }

    public Button getBackToSelectionButton() {
        return backToSelectionButton;
    }
}
