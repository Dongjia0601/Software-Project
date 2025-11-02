package com.comp2042.ui;

import com.comp2042.game.LevelManager;
import com.comp2042.game.LevelMode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Polygon;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the level selection screen.
 * Displays all themed levels in a grid with unlock status and star ratings.
 */
public class LevelSelectionController {
    
    @FXML
    private GridPane levelGrid;
    
    @FXML
    private Label totalStarsLabel;
    
    @FXML
    private Label completedLevelsLabel;
    
    @FXML
    private Button backButton;
    
    @FXML
    private Button resetProgressButton;
    
    @FXML
    private Text headerTitleText;
    
    private LevelManager levelManager;
    private Stage stage;
    
    /**
     * Initializes the controller.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        levelManager = LevelManager.getInstance();
        populateLevelGrid();
        updateStats();
        
        // Apply gradient to title text
        applyTitleGradient();
        
        // Disable space key for all buttons to prevent accidental activation
        disableSpaceKeyForButtons();
    }
    
    /**
     * Applies gradient fill to the title text.
     */
    private void applyTitleGradient() {
        if (headerTitleText != null) {
            LinearGradient gradient = new LinearGradient(
                    0, 0, 1, 0,  // Start (0,0) to End (1,0) = left to right
                    true,        // proportional
                    CycleMethod.NO_CYCLE,
                    new Stop(0.0, javafx.scene.paint.Color.web("#FF6BFF")),    // Pink
                    new Stop(0.5, javafx.scene.paint.Color.web("#FFD700")),    // Gold
                    new Stop(1.0, javafx.scene.paint.Color.web("#4DFFFF"))      // Cyan
            );
            headerTitleText.setFill(gradient);
        }
    }
    
    /**
     * Sets the stage reference.
     * @param stage the primary stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Disables space key for all buttons to prevent accidental activation.
     */
    private void disableSpaceKeyForButtons() {
        if (backButton != null) {
            backButton.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.SPACE) {
                    event.consume();
                }
            });
        }
        
        if (resetProgressButton != null) {
            resetProgressButton.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.SPACE) {
                    event.consume();
                }
            });
        }
    }
    
    /**
     * Refreshes the level display.
     * Call this method when returning from a completed level to update UI.
     */
    public void refresh() {
        levelManager = LevelManager.getInstance();
        levelGrid.getChildren().clear();
        populateLevelGrid();
        updateStats();
    }
    
    /**
     * Handles back to main menu button click.
     */
    @FXML
    private void handleBackToMenu() {
        try {
            FXMLLoader menuLoader = new FXMLLoader(getClass().getClassLoader().getResource("mainMenu.fxml"));
            Parent menuRoot = menuLoader.load();
            Scene menuScene = new Scene(menuRoot, 900, 800);
            
            if (stage != null) {
                stage.setScene(menuScene);
                stage.setTitle("Tetris - Main Menu");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load main menu: " + e.getMessage());
        }
    }
    
    /**
     * Populates the level grid with level cards.
     */
    private void populateLevelGrid() {
        List<LevelMode> levels = levelManager.getAllLevels();
        
        int column = 0;
        int row = 0;
        
        for (LevelMode level : levels) {
            VBox levelCard = createLevelCard(level);
            levelGrid.add(levelCard, column, row);
            
            // 3 cards per row
            column++;
            if (column >= 3) {
                column = 0;
                row++;
            }
        }
    }
    
    /**
     * Creates a visual card for a level.
     * @param level the level mode
     * @return VBox containing the level card
     */
    private VBox createLevelCard(LevelMode level) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(260);
        card.setPrefHeight(350);
        
        if (level.isUnlocked()) {
            card.getStyleClass().add("level-card");
            card.setOnMouseClicked(e -> handleLevelSelect(level));
        } else {
            card.getStyleClass().add("level-card-locked");
        }
        
        // Level number
        Label levelNumber = new Label("LEVEL " + level.getLevelId());
        levelNumber.getStyleClass().add("level-number");
        
        // Level name
        Label levelName = new Label(level.getLevelName());
        levelName.getStyleClass().add("level-name");
        levelName.setMaxWidth(240);
        levelName.setWrapText(true);
        levelName.setAlignment(Pos.CENTER);
        
        // Theme preview image
        ImageView themePreview = new ImageView();
        try {
            String bgImagePath = level.getTheme().getBackgroundImage();
            if (bgImagePath != null && !bgImagePath.isEmpty()) {
                // Remove leading slash if present for getResourceAsStream
                String resourcePath = bgImagePath.startsWith("/") ? bgImagePath.substring(1) : bgImagePath;
                Image themeImage = new Image(getClass().getClassLoader().getResourceAsStream(resourcePath));
                themePreview.setImage(themeImage);
                themePreview.setFitWidth(180);
                themePreview.setFitHeight(100);
                themePreview.setPreserveRatio(true);
                themePreview.setSmooth(true);
                themePreview.setCache(true);
                
                // Add rounded corners clip
                javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(180, 100);
                clip.setArcWidth(10);
                clip.setArcHeight(10);
                themePreview.setClip(clip);
            }
        } catch (Exception e) {
            System.out.println("Could not load theme image for level " + level.getLevelId() + ": " + e.getMessage());
        }
        
        // Difficulty badge
        Label difficulty = new Label(level.getDifficulty().toUpperCase());
        difficulty.getStyleClass().addAll("difficulty-badge", 
                "difficulty-badge-" + level.getDifficulty().toLowerCase());
        
        // Star rating (if unlocked)
        HBox starContainer = createStarRating(level.getBestStars());
        
        // Best score and time (if completed)
        VBox bestStats = new VBox(2);
        bestStats.setAlignment(Pos.CENTER);
        
        if (level.isCompleted()) {
            // Best score
            Label bestScore = new Label("Best: " + level.getBestScore());
            bestScore.getStyleClass().add("best-score-label");
            
            // Best time
            long bestTimeMillis = level.getBestTime();
            if (bestTimeMillis != Long.MAX_VALUE && bestTimeMillis > 0) {
                long bestTimeSeconds = bestTimeMillis / 1000;
                long minutes = bestTimeSeconds / 60;
                long seconds = bestTimeSeconds % 60;
                String timeStr = String.format("%d:%02d", minutes, seconds);
                Label bestTime = new Label("Time: " + timeStr);
                bestTime.getStyleClass().add("best-time-label");
                bestStats.getChildren().addAll(bestScore, bestTime);
            } else {
                bestStats.getChildren().add(bestScore);
            }
        }
        
        // Level stats
        long timeLimitMinutes = level.getTimeLimitSeconds() / 60;
        long timeLimitSecondsRemainder = level.getTimeLimitSeconds() % 60;
        String stats = String.format("%d lines | %d:%02d", 
                level.getTargetLines(),
                timeLimitMinutes,
                timeLimitSecondsRemainder);
        Label statsLabel = new Label(stats);
        statsLabel.getStyleClass().add("level-stats");
        
        // Play button or lock icon
        if (level.isUnlocked()) {
            Button playButton = new Button("▶ PLAY");
            playButton.getStyleClass().add("play-button");
            playButton.setOnAction(e -> handleLevelSelect(level));
            
            card.getChildren().addAll(
                    levelNumber,
                    levelName,
                    themePreview,
                    difficulty,
                    starContainer,
                    bestStats,
                    statsLabel,
                    playButton
            );
        } else {
            // Locked button (same size as Play button)
            Button lockedButton = new Button("🔒 LOCKED");
            lockedButton.getStyleClass().add("locked-button");
            lockedButton.setDisable(true);
            
            card.getChildren().addAll(
                    levelNumber,
                    levelName,
                    themePreview,
                    difficulty,
                    starContainer,
                    statsLabel,
                    lockedButton
            );
        }
        
        return card;
    }
    
    /**
     * Creates a star rating display.
     * @param stars number of stars (0-3)
     * @return HBox with star shapes
     */
    private HBox createStarRating(int stars) {
        HBox container = new HBox(8);
        container.setAlignment(Pos.CENTER);
        container.getStyleClass().add("star-container");
        
        for (int i = 0; i < 3; i++) {
            Polygon star = createStarShape();
            if (i < stars) {
                star.getStyleClass().add("star-filled");
            } else {
                star.getStyleClass().add("star-empty");
            }
            container.getChildren().add(star);
        }
        
        return container;
    }
    
    /**
     * Creates a star-shaped polygon.
     * @return Polygon shaped like a 5-pointed star
     */
    private Polygon createStarShape() {
        Polygon star = new Polygon();
        double size = 12;
        
        // 5-pointed star coordinates
        for (int i = 0; i < 5; i++) {
            double angle = Math.PI / 2 + (2 * Math.PI * i / 5);
            double x = size * Math.cos(angle);
            double y = size * Math.sin(angle);
            star.getPoints().addAll(x, y);
            
            // Inner point
            angle += Math.PI / 5;
            x = (size * 0.4) * Math.cos(angle);
            y = (size * 0.4) * Math.sin(angle);
            star.getPoints().addAll(x, y);
        }
        
        return star;
    }
    
    /**
     * Updates the statistics display.
     */
    private void updateStats() {
        int totalStars = levelManager.getTotalStars();
        int completedLevels = levelManager.getCompletedLevelsCount();
        int totalLevels = levelManager.getTotalLevels();
        
        totalStarsLabel.setText("Total Stars: " + totalStars + "/" + (totalLevels * 3));
        completedLevelsLabel.setText("Completed: " + completedLevels + "/" + totalLevels);
    }
    
    /**
     * Handles level selection.
     * @param level the selected level
     */
    private void handleLevelSelect(LevelMode level) {
        if (!level.isUnlocked()) {
            return;
        }
        
        try {
            // Set current level in manager
            levelManager.setCurrentLevel(level);
            
            // Load game screen with level mode
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("enhancedGameLayout.fxml"));
            Parent root = loader.load();
            
            // Get the GUI controller
            com.comp2042.GuiController guiController = loader.getController();
            if (guiController == null) {
                guiController = new com.comp2042.GuiController();
            }
            
            // Create game service and game mode
            com.comp2042.core.GameService gameService = new com.comp2042.core.GameServiceImpl();
            com.comp2042.gameplay.GameMode gameMode = 
                new com.comp2042.gameplay.LevelGameModeImpl(gameService, guiController, levelManager, level);
            gameMode.initialize();
            
            // Create scene
            Scene scene = new Scene(root, 900, 800);
            
            if (stage != null) {
                stage.setScene(scene);
                stage.setTitle("Tetris - " + level.getLevelName());
            }
            
            // Initialize game controller
            new com.comp2042.GameController(guiController);
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load game screen: " + e.getMessage());
        }
    }
    
    /**
     * Handles reset progress button.
     */
    @FXML
    private void handleResetProgress() {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Reset Progress");
        confirmDialog.setHeaderText("Are you sure?");
        confirmDialog.setContentText("This will reset all level progress and stars. This cannot be undone.");
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            levelManager.resetProgress();
            
            // Refresh UI
            levelGrid.getChildren().clear();
            populateLevelGrid();
            updateStats();
            
            showInfo("Progress reset successfully!");
        }
    }
    
    /**
     * Shows an error dialog.
     * @param message the error message
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Shows an info dialog.
     * @param message the info message
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}