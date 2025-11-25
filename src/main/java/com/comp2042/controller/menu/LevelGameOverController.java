package com.comp2042.controller.menu;

import com.comp2042.service.audio.SoundManager;
import com.comp2042.model.mode.LevelManager;
import com.comp2042.model.mode.LevelMode;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Level Mode Game Over screen.
 * 
 * <p>Manages the game over interface for Level Mode, displaying completion status,
 * star ratings, best scores, and providing navigation options. Supports both
 * successful level completion and failure scenarios with appropriate visual
 * feedback and animations.</p>
 * 
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Display level completion status and statistics</li>
 *   <li>Show star ratings (0-3) based on performance</li>
 *   <li>Display best score and time records</li>
 *   <li>Provide navigation: retry, next level, back to selection, or main menu</li>
 *   <li>Handle keyboard shortcuts for quick navigation</li>
 * </ul>
 */
public class LevelGameOverController implements Initializable {
    
    // UI Components
    @FXML private BorderPane rootPane;
    @FXML private Label mainTitleLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label levelNameLabel;
    @FXML private HBox starsDisplay;
    @FXML private VBox yourScoreCard;
    @FXML private Label finalScoreLabel;
    @FXML private Label linesLabel;
    @FXML private Label timeLabel;
    @FXML private VBox bestStatsSection;
    @FXML private Label bestScoreLabel;
    @FXML private Label bestTimeLabel;
    @FXML private Button tryAgainButton;
    @FXML private Button nextLevelButton;
    @FXML private Button backToSelectionButton;
    @FXML private Button backToMenuButton;
    
    // Game data
    private int finalScore;
    private int linesCleared;
    private int targetLines;
    private long playTimeMs;
    private int stars;
    private boolean success;
    private int levelId;
    private boolean isNewBestScore;
    private boolean isNewBestTime;
    
    // Callbacks
    private Runnable onTryAgain;
    private Runnable onNextLevel;
    private Runnable onBackToSelection;
    private Runnable onBackToMenu;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load background image
        setGameOverBackground();
        
        // Set up keyboard navigation
        setupKeyboardNavigation();
        
        // Set up button hover effects
        setupButtonEffects();
        
        // Apply button styles directly via Java code
        applyButtonStyles();
        
        // Ensure SPACE does not trigger Try Again
        if (tryAgainButton != null) {
            tryAgainButton.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                if (e.getCode() == KeyCode.SPACE) {
                    e.consume();
                }
            });
        }
    }
    
    /**
     * Sets up keyboard navigation handlers for the game over screen.
     * Supports N (retry), ESCAPE (back to menu), S (back to selection), and ENTER (next level).
     */
    private void setupKeyboardNavigation() {
        if (rootPane != null) {
            rootPane.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.N && onTryAgain != null) {
                    onTryAgain.run();
                } else if (e.getCode() == KeyCode.ESCAPE && onBackToMenu != null) {
                    onBackToMenu.run();
                } else if (e.getCode() == KeyCode.S && onBackToSelection != null) {
                    onBackToSelection.run();
                } else if (e.getCode() == KeyCode.ENTER && nextLevelButton.isVisible() && onNextLevel != null) {
                    onNextLevel.run();
                }
            });
            
            rootPane.setFocusTraversable(true);
            rootPane.requestFocus();
        }
    }
    
    /**
     * Applies hover effects (glow) to all interactive buttons.
     */
    private void setupButtonEffects() {
        if (tryAgainButton != null) {
            tryAgainButton.setOnMouseEntered(e -> tryAgainButton.setEffect(new Glow(0.8)));
            tryAgainButton.setOnMouseExited(e -> tryAgainButton.setEffect(null));
        }
        
        if (nextLevelButton != null) {
            nextLevelButton.setOnMouseEntered(e -> nextLevelButton.setEffect(new Glow(0.8)));
            nextLevelButton.setOnMouseExited(e -> nextLevelButton.setEffect(null));
        }
        
        if (backToSelectionButton != null) {
            backToSelectionButton.setOnMouseEntered(e -> backToSelectionButton.setEffect(new Glow(0.8)));
            backToSelectionButton.setOnMouseExited(e -> backToSelectionButton.setEffect(null));
        }
        
        if (backToMenuButton != null) {
            backToMenuButton.setOnMouseEntered(e -> backToMenuButton.setEffect(new Glow(0.8)));
            backToMenuButton.setOnMouseExited(e -> backToMenuButton.setEffect(null));
        }
    }
    
    /**
     * Applies consistent styling to all buttons using inline CSS.
     * Uses a golden gradient theme with shadow effects.
     */
    private void applyButtonStyles() {
        String buttonStyle = 
            "-fx-background-color: linear-gradient(to bottom, #FFD700, #FFA500); " +
            "-fx-text-fill: #000000; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 20px; " +
            "-fx-background-radius: 8px; " +
            "-fx-border-radius: 8px; " +
            "-fx-border-color: rgba(255, 215, 0, 0.8); " +
            "-fx-border-width: 2px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 5, 0, 0, 2);";
        
        if (tryAgainButton != null) {
            tryAgainButton.setStyle(buttonStyle);
        }
        
        if (nextLevelButton != null) {
            nextLevelButton.setStyle(buttonStyle);
        }
        
        if (backToSelectionButton != null) {
            backToSelectionButton.setStyle(buttonStyle);
        }
        
        if (backToMenuButton != null) {
            backToMenuButton.setStyle(buttonStyle);
        }
    }
    
    /**
     * Loads and sets the game over background image.
     * Uses a centered background image with fixed dimensions (900x800).
     */
    private void setGameOverBackground() {
        try {
            javafx.scene.image.Image bgImage = new javafx.scene.image.Image(
                getClass().getClassLoader().getResourceAsStream("images/backgrounds/GameOver_bg.jpg")
            );
            
            // Set as background
            rootPane.setBackground(new javafx.scene.layout.Background(
                new javafx.scene.layout.BackgroundImage(
                    bgImage,
                    javafx.scene.layout.BackgroundRepeat.NO_REPEAT,
                    javafx.scene.layout.BackgroundRepeat.NO_REPEAT,
                    javafx.scene.layout.BackgroundPosition.CENTER,
                    new javafx.scene.layout.BackgroundSize(900, 800, false, false, false, false)
                )
            ));
        } catch (Exception e) {
            System.err.println("Error loading game over background image: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Displays the game over screen with game statistics and completion status.
     * Updates all UI components, plays appropriate sound effects, and configures
     * navigation options based on level completion status.
     *
     * @param finalScore the final score achieved in this attempt
     * @param linesCleared the number of lines cleared
     * @param targetLines the target number of lines required for completion
     * @param playTimeMs the time taken to complete or fail the level, in milliseconds
     * @param stars the number of stars earned (0-3) based on performance
     * @param success true if the level was completed successfully, false otherwise
     * @param levelId the unique identifier of the level
     * @param isNewBestScore true if this score is a new record for this level
     * @param isNewBestTime true if this completion time is a new record for this level
     */
    public void showGameOver(int finalScore, int linesCleared, int targetLines, long playTimeMs,
                             int stars, boolean success, int levelId,
                             boolean isNewBestScore, boolean isNewBestTime) {
        this.finalScore = finalScore;
        this.linesCleared = linesCleared;
        this.targetLines = targetLines;
        this.playTimeMs = playTimeMs;
        this.stars = stars;
        this.success = success;
        this.levelId = levelId;
        this.isNewBestScore = isNewBestScore;
        this.isNewBestTime = isNewBestTime;
        
        // Note: Sound effect is already played in GuiController.showLevelGameOverScene()
        // to avoid duplicate playback
        
        // Update title - show "Win the Level X!" if successful, "Level X Failed!" if failed
        if (mainTitleLabel != null) {
            if (success) {
                mainTitleLabel.setText("Win the Level " + levelId + "!");
            } else {
                mainTitleLabel.setText("Level " + levelId + " Failed!");
            }
        }
        
        // Update level name
        LevelManager levelManager = LevelManager.getInstance();
        LevelMode level = levelManager.getLevel(levelId);
        if (level != null && levelNameLabel != null) {
            levelNameLabel.setText(level.getLevelName());
            levelNameLabel.setVisible(true);
        }
        
        // Update stars display
        updateStarsDisplay(stars);
        
        // Update score
        if (finalScoreLabel != null) {
            finalScoreLabel.setText(String.valueOf(finalScore));
        }
        
        // Update lines
        if (linesLabel != null) {
            linesLabel.setText(String.format("%d/%d", linesCleared, targetLines));
        }
        
        // Update time
        if (timeLabel != null) {
            int minutes = (int) (playTimeMs / 60000);
            int seconds = (int) ((playTimeMs % 60000) / 1000);
            timeLabel.setText(String.format("%d:%02d", minutes, seconds));
        }
        
        // Update best stats - reload level from manager to get latest data
        if (level != null) {
            LevelMode refreshedLevel = levelManager.getLevel(levelId);
            if (refreshedLevel != null) {
                level = refreshedLevel; // Use refreshed level data
            }
            
            if (bestScoreLabel != null) {
                bestScoreLabel.setText(String.valueOf(level.getBestScore()));
            }
            if (bestTimeLabel != null) {
                long bestTime = level.getBestTime();
                if (bestTime > 0 && bestTime != Long.MAX_VALUE) {
                    // bestTime is stored in milliseconds, convert to seconds first
                    long bestTimeSeconds = bestTime / 1000;
                    int bestMinutes = (int) (bestTimeSeconds / 60);
                    int bestSeconds = (int) (bestTimeSeconds % 60);
                    bestTimeLabel.setText(String.format("%d:%02d", bestMinutes, bestSeconds));
                } else {
                    bestTimeLabel.setText("--:--");
                }
            }
        }
        
        // Show next level button if level was completed and next level exists and is unlocked
        if (nextLevelButton != null) {
            LevelMode nextLevel = levelManager.getLevel(levelId + 1);
            boolean showNextLevel = success && nextLevel != null && nextLevel.isUnlocked();
            nextLevelButton.setVisible(showNextLevel);
            nextLevelButton.setManaged(showNextLevel);
        }
        
        // Show subtitle for new best score/time
        if (subtitleLabel != null) {
            if (isNewBestScore || isNewBestTime) {
                String subtitle = "";
                if (isNewBestScore && isNewBestTime) {
                    subtitle = "New Best Score & Time!";
                } else if (isNewBestScore) {
                    subtitle = "New Best Score!";
                } else if (isNewBestTime) {
                    subtitle = "New Best Time!";
                }
                subtitleLabel.setText(subtitle);
                subtitleLabel.setVisible(true);
                
                // Add animation
                Timeline animation = new Timeline(
                    new KeyFrame(Duration.seconds(0.5), e -> subtitleLabel.setOpacity(0.5)),
                    new KeyFrame(Duration.seconds(1.0), e -> subtitleLabel.setOpacity(1.0))
                );
                animation.setCycleCount(Animation.INDEFINITE);
                animation.play();
            } else {
                subtitleLabel.setVisible(false);
            }
        }
    }
    
    /**
     * Updates the visual star rating display with animated transitions.
     * Filled stars (★) are shown in gold for earned stars, empty stars (☆) in gray.
     *
     * @param stars the number of stars earned, from 0 to 3
     */
    private void updateStarsDisplay(int stars) {
        if (starsDisplay == null) {
            return;
        }
        
        starsDisplay.getChildren().clear();
        
        for (int i = 0; i < 3; i++) {
            Label starLabel = new Label(i < stars ? "★" : "☆");
            starLabel.setStyle(
                "-fx-font-size: 48px; " +
                (i < stars ? "-fx-text-fill: #FFD700;" : "-fx-text-fill: #666666;")
            );
            starsDisplay.getChildren().add(starLabel);
        }
        
        // Add animation for earned stars
        if (stars > 0) {
            for (int i = 0; i < stars && i < starsDisplay.getChildren().size(); i++) {
                Label starLabel = (Label) starsDisplay.getChildren().get(i);
                ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.3), starLabel);
                scaleTransition.setFromX(0);
                scaleTransition.setFromY(0);
                scaleTransition.setToX(1);
                scaleTransition.setToY(1);
                scaleTransition.setDelay(Duration.seconds(i * 0.2));
                scaleTransition.play();
            }
        }
    }
    
    /**
     * Sets the callback executed when the Try Again button is clicked.
     *
     * @param callback the runnable to execute, may be null
     */
    public void setOnTryAgain(Runnable callback) {
        this.onTryAgain = callback;
    }
    
    /**
     * Sets the callback executed when the Next Level button is clicked.
     *
     * @param callback the runnable to execute, may be null
     */
    public void setOnNextLevel(Runnable callback) {
        this.onNextLevel = callback;
    }
    
    /**
     * Sets the callback executed when the Back to Selection button is clicked.
     *
     * @param callback the runnable to execute, may be null
     */
    public void setOnBackToSelection(Runnable callback) {
        this.onBackToSelection = callback;
    }
    
    /**
     * Sets the callback executed when the Back to Menu button is clicked.
     *
     * @param callback the runnable to execute, may be null
     */
    public void setOnBackToMenu(Runnable callback) {
        this.onBackToMenu = callback;
    }
    
    @FXML
    private void onTryAgain() {
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        if (onTryAgain != null) {
            onTryAgain.run();
        }
    }
    
    @FXML
    private void onNextLevel() {
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        if (onNextLevel != null) {
            onNextLevel.run();
        }
    }
    
    @FXML
    private void onBackToSelection() {
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        if (onBackToSelection != null) {
            onBackToSelection.run();
        }
    }
    
    @FXML
    private void onBackToMenu() {
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        if (onBackToMenu != null) {
            onBackToMenu.run();
        }
    }
}
