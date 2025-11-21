package com.comp2042.view.manager;

import com.comp2042.view.panel.GameOverPanel;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CommonUIManager.
 */
class CommonUIManagerTest {

    private CommonUIManager commonUI;
    private BorderPane rootPane;
    private GridPane gamePanel;
    private Pane brickPanel;
    private Pane ghostPanel;
    private Group groupNotification;
    private GameOverPanel gameOverPanel;
    private GridPane holdPanel;
    private GridPane nextBrickPanel;
    private Button muteButton;
    private Button backToSelectionButton;

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
        }
    }

    @BeforeEach
    void setUp() {
        rootPane = new BorderPane();
        gamePanel = new GridPane();
        brickPanel = new Pane();
        ghostPanel = new Pane();
        groupNotification = new Group();
        gameOverPanel = new GameOverPanel();
        holdPanel = new GridPane();
        nextBrickPanel = new GridPane();
        muteButton = new Button();
        backToSelectionButton = new Button();

        commonUI = new CommonUIManager(
            rootPane, gamePanel, brickPanel, ghostPanel, groupNotification, gameOverPanel,
            holdPanel, nextBrickPanel, muteButton, backToSelectionButton
        );
    }

    @Test
    void testGetRootPane() {
        assertSame(rootPane, commonUI.getRootPane());
    }

    @Test
    void testGetGamePanel() {
        assertSame(gamePanel, commonUI.getGamePanel());
    }

    @Test
    void testGetBrickPanel() {
        assertSame(brickPanel, commonUI.getBrickPanel());
    }

    @Test
    void testGetGhostPanel() {
        assertSame(ghostPanel, commonUI.getGhostPanel());
    }

    @Test
    void testGetGroupNotification() {
        assertSame(groupNotification, commonUI.getGroupNotification());
    }

    @Test
    void testGetGameOverPanel() {
        assertSame(gameOverPanel, commonUI.getGameOverPanel());
    }

    @Test
    void testGetHoldPanel() {
        assertSame(holdPanel, commonUI.getHoldPanel());
    }

    @Test
    void testGetNextBrickPanel() {
        assertSame(nextBrickPanel, commonUI.getNextBrickPanel());
    }

    @Test
    void testGetMuteButton() {
        assertSame(muteButton, commonUI.getMuteButton());
    }

    @Test
    void testGetBackToSelectionButton() {
        assertSame(backToSelectionButton, commonUI.getBackToSelectionButton());
    }
}

