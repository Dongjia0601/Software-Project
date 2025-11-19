package com.comp2042.view.manager;

import com.comp2042.service.audio.SoundManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Manages countdown overlays and transitions for starting two-player games.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Render synchronised countdown overlays on both player boards</li>
 *     <li>Coordinate audio playback and callback execution</li>
 *     <li>Provide pause/cleanup hooks for modal overlays (Help/Settings)</li>
 * </ul>
 */
public class CountdownManager {

    private final SoundManager soundManager;

    private Timeline timeline;
    private Runnable onComplete;
    private StackPane overlay1;
    private StackPane overlay2;
    private Pane parent1;
    private Pane parent2;

    public CountdownManager(SoundManager soundManager) {
        this.soundManager = soundManager;
    }

    public boolean isRunning() {
        return timeline != null && timeline.getStatus() == Animation.Status.RUNNING;
    }

    /**
     * Shows a two-player countdown overlay.
     *
     * @param rootPane the root pane (used for retry scheduling)
     * @param gamePanel1 player 1 board grid
     * @param gamePanel2 player 2 board grid
     * @param boardBackground1 player 1 background pane
     * @param boardBackground2 player 2 background pane
     * @param callback callback executed once countdown finishes
     */
    public void showTwoPlayerCountdown(
        BorderPane rootPane,
        GridPane gamePanel1,
        GridPane gamePanel2,
        Pane boardBackground1,
        Pane boardBackground2,
        Runnable callback
    ) {
        if (rootPane == null || gamePanel1 == null || gamePanel2 == null) {
            scheduleRetry(() -> showTwoPlayerCountdown(rootPane, gamePanel1, gamePanel2,
                boardBackground1, boardBackground2, callback));
            return;
        }

        if (gamePanel1.getParent() == null || gamePanel2.getParent() == null) {
            scheduleRetry(() -> showTwoPlayerCountdown(rootPane, gamePanel1, gamePanel2,
                boardBackground1, boardBackground2, callback));
            return;
        }

        cancelCountdownInternal();
        this.onComplete = callback;

        // Create overlays and bind to panels
        Label countdownLabel1 = createCountdownLabel("#4ECDC4", "rgba(78, 205, 196, 1.0)");
        Label countdownLabel2 = createCountdownLabel("#FF6B6B", "rgba(255, 107, 107, 1.0)");

        overlay1 = createOverlay(countdownLabel1);
        overlay2 = createOverlay(countdownLabel2);

        parent1 = attachOverlay(gamePanel1, boardBackground1, overlay1);
        parent2 = attachOverlay(gamePanel2, boardBackground2, overlay2);

        timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(0), e -> soundManager.playCountdownSound()));

        for (int i = 3; i >= 1; i--) {
            final int count = i;
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis((3 - i) * 1000), e -> {
                countdownLabel1.setText(String.valueOf(count));
                countdownLabel2.setText(String.valueOf(count));
            }));
        }

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(3000), e -> {
            countdownLabel1.setText("Start!");
            countdownLabel2.setText("Start!");
        }));

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(4000), e -> {
            detachOverlays();
            soundManager.stopCountdownSound();
            Timeline finishedTimeline = timeline;
            timeline = null;
            if (finishedTimeline != null) {
                finishedTimeline.stop();
            }
            Runnable completed = onComplete;
            onComplete = null;
            if (completed != null) {
                completed.run();
            }
        }));

        timeline.play();
    }

    /**
     * Pauses the countdown, returning the callback that would have been executed.
     *
     * @return the stored callback (may be null if none was set or countdown not running)
     */
    public Runnable pauseCountdown() {
        Runnable callback = onComplete;
        cancelCountdownInternal();
        return callback;
    }

    /**
     * Cancels any running countdown without returning the callback.
     */
    public void cancelCountdown() {
        cancelCountdownInternal();
        onComplete = null;
    }

    private void cancelCountdownInternal() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
        soundManager.stopCountdownSound();
        detachOverlays();
    }

    private void detachOverlays() {
        if (parent1 != null && overlay1 != null) {
            parent1.getChildren().remove(overlay1);
        }
        if (parent2 != null && overlay2 != null) {
            parent2.getChildren().remove(overlay2);
        }
        overlay1 = null;
        overlay2 = null;
        parent1 = null;
        parent2 = null;
    }

    private void scheduleRetry(Runnable retry) {
        if (retry == null) {
            return;
        }
        Platform.runLater(() -> {
            Timeline delay = new Timeline(new KeyFrame(Duration.millis(200), e -> retry.run()));
            delay.play();
        });
    }

    private Label createCountdownLabel(String textColor, String glowColor) {
        Label label = new Label();
        label.setFont(Font.font("Arial", FontWeight.BOLD, 80));
        label.setStyle("-fx-text-fill: " + textColor + "; " +
                       "-fx-effect: dropshadow(gaussian, " + glowColor + ", 30, 0, 0, 0); " +
                       "-fx-alignment: center;");
        label.setAlignment(Pos.CENTER);
        label.setMouseTransparent(true);
        return label;
    }

    private StackPane createOverlay(Label label) {
        StackPane overlay = new StackPane();
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.55); -fx-background-radius: 8;");
        overlay.getChildren().add(label);
        overlay.setMouseTransparent(true);
        return overlay;
    }

    private Pane attachOverlay(GridPane gamePanel, Pane boardBackground, StackPane overlay) {
        Node parentNode = gamePanel.getParent();
        if (!(parentNode instanceof Pane)) {
            return null;
        }
        Pane parent = (Pane) parentNode;
        parent.getChildren().add(overlay);

        if (boardBackground != null) {
            overlay.prefWidthProperty().bind(boardBackground.widthProperty().subtract(20));
            overlay.prefHeightProperty().bind(boardBackground.heightProperty().subtract(20));
            overlay.layoutXProperty().bind(boardBackground.layoutXProperty().add(10));
            overlay.layoutYProperty().bind(boardBackground.layoutYProperty().add(10));
        } else {
            overlay.prefWidthProperty().bind(gamePanel.widthProperty());
            overlay.prefHeightProperty().bind(gamePanel.heightProperty());
            overlay.layoutXProperty().bind(gamePanel.layoutXProperty());
            overlay.layoutYProperty().bind(gamePanel.layoutYProperty());
        }
        return parent;
    }
}

