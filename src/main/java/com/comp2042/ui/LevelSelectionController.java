package com.comp2042.ui;

import com.comp2042.game.LevelManager;
import com.comp2042.game.LevelMode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LevelSelectionController implements Initializable {

    @FXML
    private VBox rootPane;

    @FXML
    private VBox levelsContainer;

    @FXML
    private Button backButton;

    private LevelManager levelManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        levelManager = LevelManager.getInstance();
        loadLevels();
        setupBackButton();
    }

    private void loadLevels() {
        levelsContainer.getChildren().clear();
        List<LevelMode> levels = levelManager.getAllLevels();

        // Create a grid layout for levels
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(20));

        int col = 0;
        int row = 0;
        int colsPerRow = 3;

        for (LevelMode level : levels) {
            VBox levelCard = createLevelCard(level);
            grid.add(levelCard, col, row);
            col++;
            if (col >= colsPerRow) {
                col = 0;
                row++;
            }
        }

        levelsContainer.getChildren().add(grid);
    }

    private VBox createLevelCard(LevelMode level) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setMinWidth(200);
        card.setMinHeight(250);
        card.setStyle("-fx-background-color: rgba(30, 30, 30, 0.8); " +
                      "-fx-border-color: " + (level.isUnlocked() ? "#FFD700" : "#666666") + "; " +
                      "-fx-border-width: 2; " +
                      "-fx-border-radius: 10; " +
                      "-fx-background-radius: 10;");

        // Level ID
        Label levelIdLabel = new Label("Level " + level.getLevelId());
        levelIdLabel.setFont(Font.font("Arial", 24));
        levelIdLabel.setTextFill(level.isUnlocked() ? Color.WHITE : Color.GRAY);

        // Level Name
        Label levelNameLabel = new Label(level.getLevelName());
        levelNameLabel.setFont(Font.font("Arial", 16));
        levelNameLabel.setTextFill(level.isUnlocked() ? Color.WHITE : Color.GRAY);

        // Theme preview (if available)
        ImageView themePreview = new ImageView();
        try {
            String bgImagePath = level.getTheme().getBackgroundImage();
            if (bgImagePath != null && !bgImagePath.isEmpty()) {
                Image themeImage = new Image(bgImagePath);
                themePreview.setImage(themeImage);
                themePreview.setFitWidth(150);
                themePreview.setFitHeight(80);
                themePreview.setPreserveRatio(true);
            }
        } catch (Exception e) {
            System.out.println("Could not load theme image for level " + level.getLevelId());
        }

        // Stars display
        HBox starsBox = createStarsDisplay(level.getBestStars());
        starsBox.setAlignment(Pos.CENTER);

        // Best Score
        Label bestScoreLabel = new Label();
        if (level.getBestScore() > 0) {
            bestScoreLabel.setText("Best: " + level.getBestScore());
        } else {
            bestScoreLabel.setText("Not Completed");
        }
        bestScoreLabel.setFont(Font.font("Arial", 12));
        bestScoreLabel.setTextFill(Color.LIGHTGRAY);

        // Difficulty
        Label difficultyLabel = new Label(level.getDifficulty());
        difficultyLabel.setFont(Font.font("Arial", 12));
        difficultyLabel.setTextFill(getDifficultyColor(level.getDifficulty()));

        // Play Button
        Button playButton = new Button(level.isUnlocked() ? "Play" : "Locked");
        playButton.setMinWidth(120);
        playButton.setDisable(!level.isUnlocked());
        
        if (level.isUnlocked()) {
            playButton.setOnAction(e -> startLevel(level));
            playButton.setStyle("-fx-background-color: #4CAF50; " +
                              "-fx-text-fill: white; " +
                              "-fx-font-size: 14; " +
                              "-fx-cursor: hand;");
        } else {
            playButton.setStyle("-fx-background-color: #666666; " +
                              "-fx-text-fill: #CCCCCC; " +
                              "-fx-font-size: 14;");
        }

        card.getChildren().addAll(levelIdLabel, levelNameLabel, themePreview, 
                                   starsBox, bestScoreLabel, difficultyLabel, playButton);

        return card;
    }

    private HBox createStarsDisplay(int stars) {
        HBox starsBox = new HBox(5);
        starsBox.setAlignment(Pos.CENTER);
        
        for (int i = 0; i < 3; i++) {
            Label star = new Label("★");
            star.setFont(Font.font("Arial", 20));
            if (i < stars) {
                star.setTextFill(Color.GOLD);
            } else {
                star.setTextFill(Color.GRAY);
            }
            starsBox.getChildren().add(star);
        }
        
        return starsBox;
    }

    private Color getDifficultyColor(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy":
                return Color.GREEN;
            case "medium":
                return Color.ORANGE;
            case "hard":
                return Color.RED;
            case "expert":
                return Color.PURPLE;
            default:
                return Color.WHITE;
        }
    }

    private void startLevel(LevelMode level) {
        try {
            // Set the selected level in LevelManager
            levelManager.setCurrentLevel(level);

            // Create game service and GUI controller
            com.comp2042.core.GameService gameService = new com.comp2042.core.GameServiceImpl();
            com.comp2042.GuiController guiController = new com.comp2042.GuiController();

            // Create level mode with the selected level
            com.comp2042.gameplay.GameMode gameMode = 
                new com.comp2042.gameplay.LevelGameModeImpl(gameService, guiController, levelManager, level);
            gameMode.initialize();

            // Load game scene
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("enhancedGameLayout.fxml"));
            Parent root = loader.load();
            
            com.comp2042.GuiController gameController = loader.getController();
            if (gameController == null) {
                gameController = guiController;
            }

            Scene gameScene = new Scene(root, 900, 800);
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.setScene(gameScene);
            currentStage.setTitle("Tetris - Level " + level.getLevelId());

            // Initialize game controller
            new com.comp2042.GameController(gameController);

            System.out.println("Starting Level " + level.getLevelId() + ": " + level.getLevelName());

        } catch (IOException e) {
            System.err.println("Error loading game scene: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error starting level: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupBackButton() {
        backButton.setOnAction(e -> returnToMenu());
        backButton.setStyle("-fx-background-color: #666666; " +
                          "-fx-text-fill: white; " +
                          "-fx-font-size: 14; " +
                          "-fx-cursor: hand;");
    }

    @FXML
    private void returnToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainMenu.fxml"));
            Parent root = loader.load();
            Scene menuScene = new Scene(root, 900, 800);
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.setScene(menuScene);
            currentStage.setTitle("Tetris - Main Menu");
        } catch (IOException e) {
            System.err.println("Error loading main menu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

