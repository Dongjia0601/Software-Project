package com.comp2042;

import com.comp2042.SoundManager;
import com.comp2042.game.LevelManager;
import com.comp2042.game.LevelMode;
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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Level Mode Game Over screen.
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
     * Sets up keyboard navigation for the game over screen.
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
     * Sets up hover effects for buttons.
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
     * Applies button styles directly via Java code.
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
     * Sets the game over background image.
     */
    private void setGameOverBackground() {
        try {
            javafx.scene.image.Image bgImage = new javafx.scene.image.Image(
                getClass().getClassLoader().getResourceAsStream("images/backgrounds/GameOver_bg.jpg")
            );
            if (bgImage != null) {
                javafx.scene.image.ImageView bgImageView = new javafx.scene.image.ImageView(bgImage);
                bgImageView.setFitWidth(900);
                bgImageView.setFitHeight(800);
                bgImageView.setPreserveRatio(false);
                
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
            }
        } catch (Exception e) {
        }
    }
    
    /**
     * Displays the game over screen with the provided data.
     *
     * @param finalScore the final score achieved
     * @param linesCleared the number of lines cleared
     * @param targetLines the target number of lines
     * @param playTimeMs the time taken to complete/fail the level (in milliseconds)
     * @param stars the number of stars earned (0-3)
     * @param success whether the level was completed successfully
     * @param levelId the level ID
     * @param isNewBestScore whether this is a new best score
     * @param isNewBestTime whether this is a new best time
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
        
        // Play level win/fail sound effect
        if (success) {
            SoundManager.getInstance().playLevelWinSound();
        } else {
            SoundManager.getInstance().playLevelFailedSound();
        }
        
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
     * Updates the stars display.
     *
     * @param stars the number of stars (0-3)
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
     * Sets the callback for the Try Again button.
     *
     * @param callback the callback to execute when the button is clicked
     */
    public void setOnTryAgain(Runnable callback) {
        this.onTryAgain = callback;
    }
    
    /**
     * Sets the callback for the Next Level button.
     *
     * @param callback the callback to execute when the button is clicked
     */
    public void setOnNextLevel(Runnable callback) {
        this.onNextLevel = callback;
    }
    
    /**
     * Sets the callback for the Back to Selection button.
     *
     * @param callback the callback to execute when the button is clicked
     */
    public void setOnBackToSelection(Runnable callback) {
        this.onBackToSelection = callback;
    }
    
    /**
     * Sets the callback for the Back to Menu button.
     *
     * @param callback the callback to execute when the button is clicked
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
