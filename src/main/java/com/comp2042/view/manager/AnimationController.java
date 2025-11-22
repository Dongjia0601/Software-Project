package com.comp2042.view.manager;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import com.comp2042.service.audio.SoundManager;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Responsible for managing all animations and visual effects in the game.
 * 
 * <p>This class handles timeline-based animations, notifications, countdown sequences,
 * attack animations, combo bonuses, and other visual effects.</p>
 * 
 * <p><strong>Responsibilities:</strong></p>
 * <ul>
 *   <li>Manage game loop timelines (automatic brick dropping)</li>
 *   <li>Display level-up notifications</li>
 *   <li>Show countdown sequences</li>
 *   <li>Render attack animations (screen shake, flash, shockwave)</li>
 *   <li>Display combo bonus notifications</li>
 *   <li>Manage timer animations (elapsed time, level timer)</li>
 * </ul>
 * 
 * <p><strong>Design Pattern:</strong> Extracted from GuiController to adhere to Single Responsibility Principle (SRP)</p>
 * 
 * @author Dong, Jia
 * @version Phase 3 - SRP Refactoring
 */
public class AnimationController {
    
    // Timeline constants
    private static final int DEFAULT_DROP_DURATION_MS = 400;
    
    // Timeline references
    private Timeline gameLoopTimeline; // Main game loop for brick dropping
    private Timeline timerTimeline; // Timer for elapsed time display
    private Timeline levelTimeline; // Timer for level mode time limits
    private Timeline countdownTimeline; // Countdown before game start
    
    // UI component references for animations
    private Group notificationGroup; // Group to hold notification panels
    private BorderPane rootPane; // Root pane for overlay animations
    
    // Countdown state
    private StackPane countdownOverlay1;
    private StackPane countdownOverlay2;
    private Pane countdownParent1;
    private Pane countdownParent2;
    private Runnable countdownCallback;
    
    /**
     * Constructs an AnimationController.
     * 
     * @param notificationGroup the group for displaying notifications
     * @param rootPane the root pane for overlay animations
     */
    public AnimationController(Group notificationGroup, BorderPane rootPane) {
        this.notificationGroup = notificationGroup;
        this.rootPane = rootPane;
    }
    
    /**
     * Creates and starts the main game loop timeline.
     * 
     * @param moveDownHandler the event handler for automatic downward movement
     * @param durationMs the duration between drops in milliseconds
     */
    public void startGameLoop(EventHandler<ActionEvent> moveDownHandler, int durationMs) {
        stopGameLoop();
        
        gameLoopTimeline = new Timeline(new KeyFrame(
            Duration.millis(durationMs),
            moveDownHandler
        ));
        gameLoopTimeline.setCycleCount(Timeline.INDEFINITE);
        gameLoopTimeline.play();
    }
    
    /**
     * Stops the main game loop timeline.
     */
    public void stopGameLoop() {
        if (gameLoopTimeline != null) {
            gameLoopTimeline.stop();
        }
    }
    
    /**
     * Pauses the main game loop.
     */
    public void pauseGameLoop() {
        if (gameLoopTimeline != null) {
            gameLoopTimeline.pause();
        }
    }
    
    /**
     * Resumes the main game loop.
     */
    public void resumeGameLoop() {
        if (gameLoopTimeline != null && gameLoopTimeline.getStatus() == Animation.Status.PAUSED) {
            gameLoopTimeline.play();
        }
    }
    
    /**
     * Updates the game loop speed.
     * 
     * @param newDurationMs the new duration between drops in milliseconds
     * @param moveDownHandler the event handler for automatic downward movement
     */
    public void updateGameLoopSpeed(int newDurationMs, EventHandler<ActionEvent> moveDownHandler) {
        boolean wasRunning = gameLoopTimeline != null && 
                             gameLoopTimeline.getStatus() == Animation.Status.RUNNING;
        
        startGameLoop(moveDownHandler, newDurationMs);
        
        if (!wasRunning) {
            pauseGameLoop();
        }
    }
    
    /**
     * Starts the elapsed time timer.
     * 
     * @param updateHandler the event handler called every second
     */
    public void startTimer(EventHandler<ActionEvent> updateHandler) {
        stopTimer();
        
        timerTimeline = new Timeline(new KeyFrame(
            Duration.seconds(1),
            updateHandler
        ));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
    }
    
    /**
     * Stops the elapsed time timer.
     */
    public void stopTimer() {
        if (timerTimeline != null) {
            timerTimeline.stop();
        }
    }
    
    /**
     * Starts the level mode timer.
     * 
     * @param updateHandler the event handler called every second
     */
    public void startLevelTimer(EventHandler<ActionEvent> updateHandler) {
        stopLevelTimer();
        
        levelTimeline = new Timeline(new KeyFrame(
            Duration.seconds(1),
            updateHandler
        ));
        levelTimeline.setCycleCount(Timeline.INDEFINITE);
        levelTimeline.play();
    }
    
    /**
     * Stops the level mode timer.
     */
    public void stopLevelTimer() {
        if (levelTimeline != null) {
            levelTimeline.stop();
        }
    }
    
    /**
     * Stops all timelines (game loop, timers, countdown).
     */
    public void stopAllTimelines() {
        stopGameLoop();
        stopTimer();
        stopLevelTimer();
        stopCountdown();
    }
    
    /**
     * Shows a level-up notification.
     * 
     * @param newLevel the new level number
     */
    public void showLevelUpNotification(int newLevel) {
        if (notificationGroup == null) {
            return;
        }
        
        // Create level up notification label
        Label levelUpLabel = new Label("LEVEL UP: " + newLevel);
        levelUpLabel.setStyle(
            "-fx-font-size: 48px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #FFD700; " +
            "-fx-effect: dropshadow(gaussian, rgba(255, 215, 0, 0.8), 15, 0, 0, 0);"
        );
        
        // Add to notification group
        notificationGroup.getChildren().add(levelUpLabel);
        
        // Remove after 3 seconds
        Timeline removeTimeline = new Timeline(new KeyFrame(
            Duration.seconds(3),
            ae -> notificationGroup.getChildren().remove(levelUpLabel)
        ));
        removeTimeline.play();
    }
    
    /**
     * Shows a countdown (3-2-1) before starting the game.
     * Designed for two-player mode with separate countdown displays.
     * 
     * @param gamePanel1 player 1's game panel
     * @param gamePanel2 player 2's game panel
     * @param boardBackground1 player 1's board background
     * @param boardBackground2 player 2's board background
     * @param onComplete callback to execute when countdown completes
     */
    public void showCountdown(GridPane gamePanel1, GridPane gamePanel2, 
                             Pane boardBackground1, Pane boardBackground2,
                             Runnable onComplete) {
        if (rootPane == null || gamePanel1 == null || gamePanel2 == null) {
            if (onComplete != null) {
                Platform.runLater(() -> {
                    PauseTransition retryDelay = new PauseTransition(Duration.millis(200));
                    retryDelay.setOnFinished(e -> onComplete.run());
                    retryDelay.play();
                });
            }
            return;
        }
        
        // Stop any existing countdown
        stopCountdown();
        
        // Remove any existing overlays
        cleanupCountdownOverlays();
        
        // Save callback for later use
        countdownCallback = onComplete;
        
        // Create countdown labels for each player
        Label countdownLabel1 = createCountdownLabel("#4ECDC4", "rgba(78, 205, 196, 1.0)");
        Label countdownLabel2 = createCountdownLabel("#FF6B6B", "rgba(255, 107, 107, 1.0)");
        
        // Create overlays for each game panel
        StackPane overlay1 = createCountdownOverlay(countdownLabel1);
        StackPane overlay2 = createCountdownOverlay(countdownLabel2);
        
        // Find parent containers for game panels
        Node parent1 = gamePanel1.getParent();
        Node parent2 = gamePanel2.getParent();
        
        if (parent1 instanceof Pane && parent2 instanceof Pane) {
            Pane pane1 = (Pane) parent1;
            Pane pane2 = (Pane) parent2;
            
            // Save parent references
            countdownParent1 = pane1;
            countdownParent2 = pane2;
            countdownOverlay1 = overlay1;
            countdownOverlay2 = overlay2;
            
            // Add overlays to parent containers
            pane1.getChildren().add(overlay1);
            pane2.getChildren().add(overlay2);
            
            // Bind overlay size to cover entire board background
            if (boardBackground1 != null) {
                bindOverlayToBackground(overlay1, boardBackground1);
            }
            if (boardBackground2 != null) {
                bindOverlayToBackground(overlay2, boardBackground2);
            }
            
            // Create countdown sequence
            countdownTimeline = new Timeline();
            int[] countdownValues = {3, 2, 1};
            
            for (int i = 0; i < countdownValues.length; i++) {
                int value = countdownValues[i];
                countdownTimeline.getKeyFrames().add(new KeyFrame(
                    Duration.seconds(i),
                    e -> {
                        countdownLabel1.setText(String.valueOf(value));
                        countdownLabel2.setText(String.valueOf(value));
                        // Play countdown sound effect
                        SoundManager.getInstance().playCountdownSound();
                    }
                ));
            }
            
            // Final "GO!" and cleanup
            countdownTimeline.getKeyFrames().add(new KeyFrame(
                Duration.seconds(3),
                e -> {
                    countdownLabel1.setText("GO!");
                    countdownLabel2.setText("GO!");
                }
            ));
            
            countdownTimeline.getKeyFrames().add(new KeyFrame(
                Duration.seconds(3.5),
                e -> {
                    cleanupCountdownOverlays();
                    if (countdownCallback != null) {
                        countdownCallback.run();
                        countdownCallback = null;
                    }
                }
            ));
            
            countdownTimeline.play();
        }
    }
    
    /**
     * Creates a countdown label with specified styling.
     */
    private Label createCountdownLabel(String textColor, String shadowColor) {
        Label label = new Label();
        label.setFont(Font.font("Arial", FontWeight.BOLD, 80));
        label.setStyle(
            "-fx-text-fill: " + textColor + "; " +
            "-fx-effect: dropshadow(gaussian, " + shadowColor + ", 30, 0, 0, 0); " +
            "-fx-alignment: center;"
        );
        label.setAlignment(Pos.CENTER);
        label.setMouseTransparent(true);
        return label;
    }
    
    /**
     * Creates a countdown overlay pane.
     */
    private StackPane createCountdownOverlay(Label countdownLabel) {
        StackPane overlay = new StackPane();
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.55); -fx-background-radius: 8;");
        overlay.getChildren().add(countdownLabel);
        overlay.setMouseTransparent(true);
        return overlay;
    }
    
    /**
     * Binds overlay dimensions to board background.
     */
    private void bindOverlayToBackground(StackPane overlay, Pane boardBackground) {
        overlay.prefWidthProperty().bind(boardBackground.widthProperty().subtract(20));
        overlay.prefHeightProperty().bind(boardBackground.heightProperty().subtract(20));
        overlay.layoutXProperty().bind(boardBackground.layoutXProperty().add(10));
        overlay.layoutYProperty().bind(boardBackground.layoutYProperty().add(10));
    }
    
    /**
     * Stops the countdown animation.
     */
    public void stopCountdown() {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
            countdownTimeline = null;
            SoundManager.getInstance().stopCountdownSound();
        }
    }
    
    /**
     * Cleans up countdown overlays.
     */
    private void cleanupCountdownOverlays() {
        if (countdownOverlay1 != null && countdownParent1 != null) {
            countdownParent1.getChildren().remove(countdownOverlay1);
        }
        if (countdownOverlay2 != null && countdownParent2 != null) {
            countdownParent2.getChildren().remove(countdownOverlay2);
        }
    }
    
    /**
     * Shows an enhanced attack animation on the specified player's board.
     * Includes shockwave, screen shake, and flash effects.
     * 
     * @param player the player number (1 or 2)
     * @param attackPower the number of lines being attacked
     * @param boardBackground player's board background pane
     * @param gamePanel player's game panel
     */
    public void showAttackAnimation(int player, int attackPower, Pane boardBackground, Pane gamePanel) {
        if (rootPane == null || boardBackground == null || gamePanel == null) {
            return;
        }
        
        // Calculate animation intensity based on attack power
        double intensity = Math.min(attackPower / 4.0, 1.0); // Max intensity at 4+ lines
        long duration = (long)(300 + intensity * 200); // 300-500ms based on power
        
        // Get board container (parent of boardBackground)
        Pane boardContainer = (Pane) boardBackground.getParent();
        if (boardContainer == null) {
            return;
        }
        
        // 1. Screen shake effect
        animateScreenShake(boardContainer, intensity, duration);
        
        // 2. Flash overlay effect
        animateFlashOverlay(player, boardContainer, boardBackground, intensity, duration);
        
        // 3. Shockwave effect
        animateShockwave(boardContainer, boardBackground, intensity, duration);
    }
    
    /**
     * Animates screen shake effect.
     */
    private void animateScreenShake(Pane container, double intensity, long duration) {
        double shakeAmount = 3 + intensity * 5; // 3-8 pixels based on intensity
        double originalX = container.getLayoutX();
        double originalY = container.getLayoutY();
        
        Timeline shakeTimeline = new Timeline();
        int shakeCount = 8;
        for (int i = 0; i < shakeCount; i++) {
            double offsetX = (Math.random() - 0.5) * shakeAmount * 2;
            double offsetY = (Math.random() - 0.5) * shakeAmount * 2;
            KeyFrame keyFrame = new KeyFrame(
                Duration.millis(i * duration / shakeCount),
                e -> {
                    container.setLayoutX(originalX + offsetX);
                    container.setLayoutY(originalY + offsetY);
                }
            );
            shakeTimeline.getKeyFrames().add(keyFrame);
        }
        // Return to original position
        shakeTimeline.getKeyFrames().add(new KeyFrame(
            Duration.millis(duration),
            e -> {
                container.setLayoutX(originalX);
                container.setLayoutY(originalY);
            }
        ));
        shakeTimeline.play();
    }
    
    /**
     * Animates flash overlay effect.
     */
    private void animateFlashOverlay(int player, Pane container, Pane boardBackground, 
                                     double intensity, long duration) {
        Rectangle flashOverlay = new Rectangle();
        flashOverlay.setWidth(boardBackground.getWidth());
        flashOverlay.setHeight(boardBackground.getHeight());
        flashOverlay.setFill(player == 1 ? 
            Color.rgb(255, 107, 107, 0.4 + intensity * 0.3) : 
            Color.rgb(78, 205, 196, 0.4 + intensity * 0.3));
        flashOverlay.setMouseTransparent(true);
        flashOverlay.setLayoutX(boardBackground.getLayoutX());
        flashOverlay.setLayoutY(boardBackground.getLayoutY());
        
        container.getChildren().add(flashOverlay);
        
        // Flash animation with multiple pulses
        FadeTransition flash1 = new FadeTransition(Duration.millis(duration / 3), flashOverlay);
        flash1.setFromValue(0.7 + intensity * 0.3);
        flash1.setToValue(0.2);
        
        FadeTransition flash2 = new FadeTransition(Duration.millis(duration / 3), flashOverlay);
        flash2.setFromValue(0.3);
        flash2.setToValue(0.1);
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(duration / 3), flashOverlay);
        fadeOut.setFromValue(0.1);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> container.getChildren().remove(flashOverlay));
        
        SequentialTransition flashSequence = new SequentialTransition(flash1, flash2, fadeOut);
        flashSequence.play();
    }
    
    /**
     * Animates shockwave ripple effect.
     */
    private void animateShockwave(Pane container, Pane boardBackground, double intensity, long duration) {
        Circle shockwave = new Circle();
        shockwave.setFill(Color.TRANSPARENT);
        shockwave.setStroke(Color.rgb(255, 255, 255, 0.8));
        shockwave.setStrokeWidth(3 + intensity * 2);
        shockwave.setCenterX(boardBackground.getLayoutX() + boardBackground.getWidth() / 2);
        shockwave.setCenterY(boardBackground.getLayoutY() + boardBackground.getHeight() / 2);
        shockwave.setRadius(0);
        shockwave.setMouseTransparent(true);
        
        container.getChildren().add(shockwave);
        
        // Expand and fade out
        double maxRadius = Math.max(boardBackground.getWidth(), boardBackground.getHeight()) / 2;
        ScaleTransition expand = new ScaleTransition(Duration.millis(duration), shockwave);
        expand.setToX(maxRadius / 10);
        expand.setToY(maxRadius / 10);
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(duration), shockwave);
        fadeOut.setFromValue(0.8);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> container.getChildren().remove(shockwave));
        
        ParallelTransition shockwaveAnim = new ParallelTransition(expand, fadeOut);
        shockwaveAnim.play();
    }
    
    /**
     * Shows a combo bonus notification.
     * 
     * @param player the player number (1 or 2)
     * @param combo the combo count
     * @param linesEliminated the number of lines eliminated
     */
    public void showComboBonus(int player, int combo, int linesEliminated) {
        if (rootPane == null) {
            return;
        }
        
        // Create combo bonus label
        Label comboLabel = new Label("COMBO x" + combo + "!\n" + linesEliminated + " lines eliminated!");
        comboLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        comboLabel.setStyle(
            "-fx-text-fill: #FFD700; " +
            "-fx-effect: dropshadow(gaussian, rgba(255, 215, 0, 1.0), 20, 0, 0, 0); " +
            "-fx-alignment: center; " +
            "-fx-text-alignment: center;"
        );
        comboLabel.setAlignment(Pos.CENTER);
        comboLabel.setMouseTransparent(true);
        
        // Position label over player board
        StackPane comboOverlay = new StackPane();
        comboOverlay.setAlignment(Pos.CENTER);
        comboOverlay.getChildren().add(comboLabel);
        comboOverlay.setMouseTransparent(true);
        
        // Add to root pane
        rootPane.getChildren().add(comboOverlay);
        
        // Animate: scale up, then fade out
        comboLabel.setScaleX(0.5);
        comboLabel.setScaleY(0.5);
        comboLabel.setOpacity(0.0);
        
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(300), comboLabel);
        scaleUp.setToX(1.2);
        scaleUp.setToY(1.2);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), comboLabel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        ParallelTransition appear = new ParallelTransition(scaleUp, fadeIn);
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), comboLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.millis(1000));
        
        SequentialTransition sequence = new SequentialTransition(appear, fadeOut);
        sequence.setOnFinished(e -> rootPane.getChildren().remove(comboOverlay));
        sequence.play();
    }

    /**
     * Animates row clearing with a smooth fade-out effect.
     * Uses a single ParallelTransition for all cleared rows to ensure optimal performance.
     * Performance optimized: Uses simple FadeTransition instead of complex nested transitions
     * to avoid creating too many animation objects (which caused lag with multiple rows).
     *
     * @param displayMatrix The grid of Rectangles representing the board
     * @param clearedRows List of row indices to clear (before clearing, so these are the actual row indices)
     * @param onComplete Callback to run when animation finishes
     */
    public void animateRowClear(Rectangle[][] displayMatrix, List<Integer> clearedRows, Runnable onComplete) {
        if (displayMatrix == null || clearedRows == null || clearedRows.isEmpty()) {
            if (onComplete != null) onComplete.run();
            return;
        }

        // Collect all rectangles in cleared rows
        List<Rectangle> rectanglesToAnimate = new ArrayList<>();
                for (int row : clearedRows) {
                    if (row >= 0 && row < displayMatrix.length) {
                for (int col = 0; col < displayMatrix[row].length; col++) {
                    Rectangle rect = displayMatrix[row][col];
                    if (rect != null && rect.getFill() != Color.TRANSPARENT) {
                        rectanglesToAnimate.add(rect);
                    }
                }
            }
        }

        if (rectanglesToAnimate.isEmpty()) {
            if (onComplete != null) onComplete.run();
            return;
        }

        // Use single FadeTransition for all rectangles - much better performance
        // Create one transition that animates all rectangles together
        List<FadeTransition> fadeTransitions = new ArrayList<>();
        
        for (Rectangle rect : rectanglesToAnimate) {
            // Reset any previous animation state
            rect.setOpacity(1.0);
            rect.setScaleX(1.0);
            rect.setScaleY(1.0);
            
            // Simple fade-out transition - lightweight and smooth
            FadeTransition fade = new FadeTransition(Duration.millis(150), rect);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fadeTransitions.add(fade);
        }

        // Use ParallelTransition to animate all rectangles simultaneously
        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransitions);
        
        // When animation completes, reset all rectangles and call callback
        parallelTransition.setOnFinished(e -> {
            // Reset all rectangles to ensure clean state
            for (Rectangle rect : rectanglesToAnimate) {
                if (rect != null) {
                    rect.setOpacity(1.0);
                    rect.setScaleX(1.0);
                    rect.setScaleY(1.0);
                }
            }
            if (onComplete != null) onComplete.run();
        });
        
        parallelTransition.play();
    }
}

