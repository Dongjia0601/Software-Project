package com.comp2042.ui;

import com.comp2042.SoundManager;
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
        refreshData();
        
        // Apply gradient to title text
        applyTitleGradient();
        
        // Disable space key for all buttons to prevent accidental activation
        disableSpaceKeyForButtons();
    }
    
    /**
     * Refreshes the level data and UI.
     * Called when returning from game over screen to update stars and stats.
     */
    public void refreshData() {
        levelManager = LevelManager.getInstance();
        // LevelManager is a singleton and automatically loads progress in constructor
        // The levels are already updated, just need to refresh the UI
        populateLevelGrid();
        updateStats();
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
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        try {
            FXMLLoader menuLoader = new FXMLLoader(getClass().getClassLoader().getResource("mainMenu.fxml"));
            Parent menuRoot = menuLoader.load();
            Scene menuScene = new Scene(menuRoot, 900, 800);
            
            if (stage != null) {
                stage.setScene(menuScene);
                stage.setTitle("Tetris - Main Menu");
                // Center window on primary screen to handle multi-monitor setups
                centerWindowOnScreen(stage, 900, 800);
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
        // Clear existing cards first
        levelGrid.getChildren().clear();
        
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
        levelName.setMaxWidth(280); 
        levelName.setWrapText(false); // Disable wrapping to keep name on one line
        levelName.setAlignment(Pos.CENTER);
        // Use CSS text-overrun: ellipsis for overflow handling
        
        // Theme preview image
        ImageView themePreview = new ImageView();
        try {
            String bgImagePath = level.getTheme().getBackgroundImage();
            if (bgImagePath != null && !bgImagePath.isEmpty()) {
                // Remove leading slash if present for getResourceAsStream
                String resourcePath = bgImagePath.startsWith("/") ? bgImagePath.substring(1) : bgImagePath;
                Image themeImage = new Image(getClass().getClassLoader().getResourceAsStream(resourcePath));
                themePreview.setImage(themeImage);
                themePreview.setFitWidth(220);
                themePreview.setFitHeight(130);
                themePreview.setPreserveRatio(true);
                themePreview.setSmooth(true);
                themePreview.setCache(true);
                
                // Add rounded corners clip
                javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(220, 130);
                clip.setArcWidth(10);
                clip.setArcHeight(10);
                themePreview.setClip(clip);
            }
        } catch (Exception e) {
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
    public void handleLevelSelect(LevelMode level) {
        if (!level.isUnlocked()) {
            return;
        }
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        
        // Set current level in manager first to get level ID
        levelManager.setCurrentLevel(level);
        
        // Play level background music based on level number
        SoundManager.getInstance().playLevelBackgroundMusic(level.getLevelId());
        
        try {
            
            // Load game screen with level mode
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("enhancedGameLayout.fxml"));
            Parent root = loader.load();
            
            // Get the GUI controller
            com.comp2042.GuiController guiController = loader.getController();
            if (guiController == null) {
                guiController = new com.comp2042.GuiController();
            }
            
            // Create game service and game mode
            // Use dependency injection: create Board explicitly and inject it
            com.comp2042.Board board = new com.comp2042.SimpleBoard(10, 20);
            com.comp2042.core.GameService gameService = new com.comp2042.core.GameServiceImpl(board);
            com.comp2042.gameplay.GameMode gameMode = 
                new com.comp2042.gameplay.LevelGameModeImpl(gameService, guiController, levelManager, level);
            gameMode.initialize();
            
            // Create scene
            Scene scene = new Scene(root, 900, 800);
            
            if (stage != null) {
                stage.setScene(scene);
                stage.setTitle("Tetris - " + level.getLevelName());
            }
            
            // Apply theme background and colors AFTER scene is set
            applyThemeBackground(root, level);
            
            // Apply theme colors to Hold and Next preview displays
            guiController.applyThemeToPreviewDisplays(level.getTheme().getAccentColor());
            
            // Initialize game controller
            new com.comp2042.GameController(guiController);
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load game screen: " + e.getMessage());
        }
    }
    
    /**
     * Applies the theme background and UI colors to the game scene.
     * Adds darkening overlay for visual clarity.
     * 
     * @param root the root parent node
     * @param level the level with theme info
     */
    private void applyThemeBackground(Parent root, com.comp2042.game.LevelMode level) {
        com.comp2042.game.LevelTheme theme = level.getTheme();
        
        if (theme == null) {
            System.err.println("Theme is null for level: " + level.getLevelName());
            return;
        }
        
        String bgImagePath = theme.getBackgroundImage();
        
        if (bgImagePath != null && !bgImagePath.isEmpty()) {
            try {
                // Remove leading slash if present for getResourceAsStream
                String resourcePath = bgImagePath.startsWith("/") ? bgImagePath.substring(1) : bgImagePath;
                java.net.URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);
                
                if (resourceUrl != null) {
                    // Apply background image with darkening overlay
                    String backgroundStyle = String.format(
                        "-fx-background-image: url('%s'); " +
                        "-fx-background-size: cover; " +
                        "-fx-background-position: center center; " +
                        "-fx-background-repeat: no-repeat; " +
                        "-fx-background-color: rgba(0, 0, 0, 0.4);",
                        resourceUrl.toExternalForm()
                    );
                    
                    if (root instanceof javafx.scene.layout.Region) {
                        ((javafx.scene.layout.Region) root).setStyle(backgroundStyle);
                    }
                    
                } else {
                    System.err.println("Resource URL is null for: " + bgImagePath);
                    // Fallback to gradient
                    applyGradientFallback(root, theme);
                }
            } catch (Exception e) {
                System.err.println("Failed to apply theme background: " + e.getMessage());
                e.printStackTrace();
                // Fallback to gradient
                applyGradientFallback(root, theme);
            }
            
            // Apply theme colors to game elements
            applyThemeColors(root, theme);
        } else {
            System.err.println("Background image path is empty for level: " + level.getLevelName());
            // Fallback to gradient
            applyGradientFallback(root, theme);
            
            // Apply theme colors to game elements
            applyThemeColors(root, theme);
        }
    }
    
    /**
     * Applies gradient fallback when background image fails to load.
     */
    private void applyGradientFallback(Parent root, com.comp2042.game.LevelTheme theme) {
        String gradientStyle = String.format(
            "-fx-background-color: linear-gradient(to bottom, %s, %s);",
            theme.getPrimaryColor(),
            theme.getSecondaryColor()
        );
        if (root instanceof javafx.scene.layout.Region) {
            ((javafx.scene.layout.Region) root).setStyle(gradientStyle);
        }
    }
    
    /**
     * Applies theme colors to game UI elements.
     * @param root the root parent node
     * @param theme the level theme
     */
    private void applyThemeColors(Parent root, com.comp2042.game.LevelTheme theme) {
        // Use Platform.runLater to ensure styles are applied after CSS loading
        javafx.application.Platform.runLater(() -> {
            // Apply theme to different UI element types
            applyThemeToElementType(root, ".side-panel", createSidePanelStyle(theme));
            applyThemeToElementType(root, ".gameBoard", createGameBoardStyle(theme));
            applyThemeToElementType(root, ".info-box", createInfoBoxStyle(theme));
            applyThemeToElementType(root, ".section-title", createSectionTitleStyle(theme));
            applyThemeToElementType(root, ".game-button", createButtonStyle(theme));
            applyThemeToElementType(root, ".control-button", createButtonStyle(theme));
            applyThemeToElementType(root, "#settingsButton", createSettingsButtonStyle(theme));
            applyThemeToElementType(root, "Button[fx:id='settingsButton']", createSettingsButtonStyle(theme));
            applyThemeToElementType(root, ".control-hint", createControlHintStyle());
            
        });
    }
    
    /**
     * Helper method to apply theme to specific element types.
     */
    private void applyThemeToElementType(Parent root, String selector, String style) {
        root.lookupAll(selector).forEach(node -> node.setStyle(style));
    }
    
    /**
     * Creates side panel style.
     */
    private String createSidePanelStyle(com.comp2042.game.LevelTheme theme) {
        return String.format(
            "-fx-background-color: rgba(255, 255, 255, 0.05); " +
            "-fx-border-color: %s; " +
            "-fx-border-width: 0 2px 0 2px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 10, 0, 0, 0);",
            convertColorToRgba(theme.getAccentColor(), 0.3)
        );
    }
    
    /**
     * Creates game board style.
     */
    private String createGameBoardStyle(com.comp2042.game.LevelTheme theme) {
        return String.format(
            "-fx-background-color: linear-gradient(to bottom, %s, %s), rgba(10, 14, 39, 0.96); " +
            "-fx-background-insets: 0, 12; " +
            "-fx-background-radius: 16, 8; " +
            "-fx-effect: dropshadow(gaussian, %s, 18, 0, 0, 0);",
            theme.getPrimaryColor(),
            theme.getSecondaryColor(),
            convertColorToRgba(theme.getAccentColor(), 0.85)
        );
    }
    
    /**
     * Creates info box style.
     */
    private String createInfoBoxStyle(com.comp2042.game.LevelTheme theme) {
        return String.format(
            "-fx-background-color: rgba(0, 0, 0, 0.7); " +
            "-fx-background-radius: 10px; " +
            "-fx-padding: 15px; " +
            "-fx-border-color: %s; " +
            "-fx-border-radius: 10px; " +
            "-fx-border-width: 2px; " +
            "-fx-effect: dropshadow(gaussian, %s, 8, 0, 0, 0);",
            theme.getAccentColor(),
            convertColorToRgba(theme.getPrimaryColor(), 0.3)
        );
    }
    
    /**
     * Creates section title style.
     */
    private String createSectionTitleStyle(com.comp2042.game.LevelTheme theme) {
        return String.format(
            "-fx-text-fill: %s; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 18px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.9), 3, 0, 0, 0);",
            theme.getAccentColor()
        );
    }
    
    /**
     * Creates button style.
     */
    private String createButtonStyle(com.comp2042.game.LevelTheme theme) {
        return String.format(
            "-fx-background-color: linear-gradient(to bottom, %s, %s); " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 20px; " +
            "-fx-background-radius: 8px; " +
            "-fx-border-color: %s; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 8px; " +
            "-fx-cursor: hand;",
            theme.getPrimaryColor(),
            theme.getSecondaryColor(),
            theme.getAccentColor()
        );
    }
    
    /**
     * Creates settings button style.
     */
    private String createSettingsButtonStyle(com.comp2042.game.LevelTheme theme) {
        return String.format(
            "-fx-background-color: linear-gradient(to bottom, %s, %s); " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 20px; " +
            "-fx-background-radius: 8px; " +
            "-fx-border-color: %s; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 8px; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, %s, 8, 0, 0, 0);",
            theme.getPrimaryColor(),
            theme.getSecondaryColor(),
            theme.getAccentColor(),
            convertColorToRgba(theme.getAccentColor(), 0.6)
        );
    }
    
    /**
     * Creates control hint style.
     */
    private String createControlHintStyle() {
        return "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;";
    }
    
    /**
     * Converts a hex color to rgba format with specified opacity.
     * @param hexColor the hex color (e.g., "#FFD700")
     * @param opacity the opacity (0.0 to 1.0)
     * @return the rgba color string (e.g., "rgba(255, 215, 0, 0.3)")
     */
    private String convertColorToRgba(String hexColor, double opacity) {
        try {
            // Remove # if present
            String hex = hexColor.startsWith("#") ? hexColor.substring(1) : hexColor;
            
            // Parse hex to RGB
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            
            return String.format("rgba(%d, %d, %d, %.2f)", r, g, b, opacity);
        } catch (Exception e) {
            System.err.println("Failed to convert color: " + hexColor + " - " + e.getMessage());
            // Fallback to white with opacity
            return String.format("rgba(255, 255, 255, %.2f)", opacity);
        }
    }
    
    /**
     * Handles reset progress button.
     */
    @FXML
    private void handleResetProgress() {
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Reset Progress");
        confirmDialog.setHeaderText("Are you sure?");
        confirmDialog.setContentText("This will reset all level progress and stars. This cannot be undone.");
        
        // Force dialog pane creation by getting it
        javafx.scene.control.DialogPane dialogPane = confirmDialog.getDialogPane();
        // Fix text truncation
        dialogPane.setPrefWidth(450);
        javafx.scene.control.Label contentLabel = (javafx.scene.control.Label) dialogPane.lookup(".content.label");
        if (contentLabel != null) {
            contentLabel.setWrapText(true);
            contentLabel.setMaxWidth(Double.MAX_VALUE);
        }
        
        // Add button click sound for dialog buttons using event filter on dialog pane
        // This approach works more reliably than setOnMouseClicked
        dialogPane.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
            javafx.scene.Node target = (javafx.scene.Node) event.getTarget();
            // Check if the clicked node is a button in the dialog
            if (target instanceof javafx.scene.control.Button) {
                javafx.scene.control.Button button = (javafx.scene.control.Button) target;
                // Check if this button is one of the dialog buttons
                for (ButtonType buttonType : dialogPane.getButtonTypes()) {
                    if (dialogPane.lookupButton(buttonType) == button) {
                        SoundManager.getInstance().playButtonClickSound();
                        break;
                    }
                }
            }
        });
        
        // Also try the direct approach when dialog is shown
        confirmDialog.setOnShown(e -> {
            javafx.application.Platform.runLater(() -> {
                dialogPane.getButtonTypes().forEach(buttonType -> {
                    javafx.scene.Node button = dialogPane.lookupButton(buttonType);
                    if (button != null && button instanceof javafx.scene.control.Button) {
                        ((javafx.scene.control.Button) button).setOnAction(actionEvent -> {
                            SoundManager.getInstance().playButtonClickSound();
                        });
                    }
                });
            });
        });
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            levelManager.resetProgress();
            
            // Refresh UI
            levelGrid.getChildren().clear();
            populateLevelGrid();
            updateStats();
        }
    }
    
    /**
     * Centers the window on the current screen (where the window is located).
     * Handles multi-monitor setups and ensures window appears correctly on the current display.
     * 
     * @param stage the stage to center
     * @param width the window width
     * @param height the window height
     */
    private void centerWindowOnScreen(javafx.stage.Stage stage, double width, double height) {
        // Use centerOnScreen which automatically centers on the current screen
        // This respects the user's current display setup
        stage.centerOnScreen();
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
        
        // Fix text truncation
        javafx.scene.control.DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setPrefWidth(450);
        javafx.scene.control.Label contentLabel = (javafx.scene.control.Label) dialogPane.lookup(".content.label");
        if (contentLabel != null) {
            contentLabel.setWrapText(true);
            contentLabel.setMaxWidth(Double.MAX_VALUE);
        }
        
        // Add button click sound for dialog buttons after dialog is shown
        alert.setOnShown(e -> {
            alert.getDialogPane().getButtonTypes().forEach(buttonType -> {
                javafx.scene.Node button = alert.getDialogPane().lookupButton(buttonType);
                if (button != null) {
                    button.setOnMouseClicked(event -> SoundManager.getInstance().playButtonClickSound());
                }
            });
        });
        
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
        
        // Fix text truncation
        javafx.scene.control.DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setPrefWidth(450);
        javafx.scene.control.Label contentLabel = (javafx.scene.control.Label) dialogPane.lookup(".content.label");
        if (contentLabel != null) {
            contentLabel.setWrapText(true);
            contentLabel.setMaxWidth(Double.MAX_VALUE);
        }
        
        // Add button click sound for dialog buttons after dialog is shown
        alert.setOnShown(e -> {
            alert.getDialogPane().getButtonTypes().forEach(buttonType -> {
                javafx.scene.Node button = alert.getDialogPane().lookupButton(buttonType);
                if (button != null) {
                    button.setOnMouseClicked(event -> SoundManager.getInstance().playButtonClickSound());
                }
            });
        });
        
        alert.showAndWait();
    }
}