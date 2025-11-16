package com.comp2042.view.panel;

import com.comp2042.model.mode.PlayerStats;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Enhanced game over panel for two-player VS mode.
 * Displays winner, detailed statistics, and action buttons.
 */
public class TwoPlayerGameOverPanel extends StackPane {
    
    private Label winnerLabel;
    private VBox statsContainer;
    private HBox buttonsContainer;
    
    public TwoPlayerGameOverPanel() {
        initializePanel();
    }
    
    /**
     * Initializes the game over panel with all components.
     */
    private void initializePanel() {
        // Main container with background
        VBox mainContainer = new VBox(18);
        mainContainer.setAlignment(javafx.geometry.Pos.CENTER);
        mainContainer.setStyle(
            "-fx-background-color: rgba(20, 20, 40, 0.95); " +
            "-fx-background-radius: 15px; " +
            "-fx-border-color: linear-gradient(to right, #FF6B6B, #4ECDC4, #45B7D1); " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 15px; " +
            "-fx-padding: 30px 50px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.6), 20, 0, 0, 0);"
        );
        mainContainer.setMinWidth(500);
        mainContainer.setMaxWidth(600);
        mainContainer.setMinHeight(450);
        mainContainer.setMaxHeight(500);
        
        // Winner label
        winnerLabel = new Label();
        winnerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        winnerLabel.setStyle(
            "-fx-text-fill: linear-gradient(to bottom, #FFD700, #FFA500); " +
            "-fx-effect: dropshadow(gaussian, rgba(255, 215, 0, 0.9), 15, 0, 0, 0);"
        );
        
        // Stats container
        statsContainer = new VBox(12);
        statsContainer.setAlignment(javafx.geometry.Pos.CENTER);
        
        // Buttons container
        buttonsContainer = new HBox(18);
        buttonsContainer.setAlignment(javafx.geometry.Pos.CENTER);
        buttonsContainer.setStyle("-fx-padding: 18px 0 0 0;");
        
        mainContainer.getChildren().addAll(winnerLabel, statsContainer, buttonsContainer);
        
        // Position the panel higher up
        StackPane.setAlignment(mainContainer, javafx.geometry.Pos.TOP_CENTER);
        StackPane.setMargin(mainContainer, new javafx.geometry.Insets(50, 0, 0, 0));
        
        getChildren().add(mainContainer);
    }
    
    /**
     * Sets the game over information and displays statistics.
     * 
     * @param winner the winner (1 for Player 1, 2 for Player 2, 0 for tie)
     * @param player1Stats Player 1's statistics
     * @param player2Stats Player 2's statistics
     * @param player1Score Player 1's final score
     * @param player2Score Player 2's final score
     */
    public void setGameOverInfo(int winner, PlayerStats player1Stats, PlayerStats player2Stats, 
                                int player1Score, int player2Score) {
        // Set winner text
        String winnerText;
        String winnerColor;
        if (winner == 0) {
            winnerText = "TIE GAME!";
            winnerColor = "#FFD700";
        } else if (winner == 1) {
            winnerText = "🏆 PLAYER 1 WINS! 🏆";
            winnerColor = "#4ECDC4";
        } else {
            winnerText = "🏆 PLAYER 2 WINS! 🏆";
            winnerColor = "#FF6B6B";
        }
        winnerLabel.setText(winnerText);
        winnerLabel.setStyle(
            "-fx-text-fill: " + winnerColor + "; " +
            "-fx-effect: dropshadow(gaussian, rgba(255, 215, 0, 0.9), 20, 0, 0, 0);"
        );
        
        // Clear previous stats
        statsContainer.getChildren().clear();
        
        // Create stats display
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(30);
        statsGrid.setVgap(10);
        statsGrid.setAlignment(javafx.geometry.Pos.CENTER);
        
        // Column headers
        Label p1Header = createStatLabel("PLAYER 1", "#4ECDC4", true);
        Label p2Header = createStatLabel("PLAYER 2", "#FF6B6B", true);
        Label statHeader = createStatLabel("STATS", "#FFD700", true);
        
        statsGrid.add(statHeader, 0, 0);
        statsGrid.add(p1Header, 1, 0);
        statsGrid.add(p2Header, 2, 0);
        
        // Add statistics rows
        int row = 1;
        addStatRow(statsGrid, "Score", 
                   String.valueOf(player1Score), 
                   String.valueOf(player2Score), row++);
        addStatRow(statsGrid, "Lines", 
                   String.valueOf(player1Stats.getLinesCleared()), 
                   String.valueOf(player2Stats.getLinesCleared()), row++);
        addStatRow(statsGrid, "Attack", 
                   String.valueOf(player1Stats.getAttacksSent()), 
                   String.valueOf(player2Stats.getAttacksSent()), row++);
        addStatRow(statsGrid, "Defense", 
                   String.valueOf(player1Stats.getAttacksReceived()), 
                   String.valueOf(player2Stats.getAttacksReceived()), row++);
        addStatRow(statsGrid, "Max Combo", 
                   String.valueOf(player1Stats.getMaxCombo()), 
                   String.valueOf(player2Stats.getMaxCombo()), row++);
        addStatRow(statsGrid, "Tetris", 
                   String.valueOf(player1Stats.getTetrisCount()), 
                   String.valueOf(player2Stats.getTetrisCount()), row++);
        addStatRow(statsGrid, "Time", 
                   player1Stats.getFormattedTime(), 
                   player2Stats.getFormattedTime(), row++);
        
        statsContainer.getChildren().add(statsGrid);
    }
    
    /**
     * Adds a row of statistics to the grid.
     */
    private void addStatRow(GridPane grid, String statName, String p1Value, String p2Value, int row) {
        Label statLabel = createStatLabel(statName, "#FFFFFF", false);
        Label p1Label = createStatLabel(p1Value, "#4ECDC4", false);
        Label p2Label = createStatLabel(p2Value, "#FF6B6B", false);
        
        grid.add(statLabel, 0, row);
        grid.add(p1Label, 1, row);
        grid.add(p2Label, 2, row);
    }
    
    /**
     * Creates a styled label for statistics.
     */
    private Label createStatLabel(String text, String color, boolean isHeader) {
        Label label = new Label(text);
        label.setStyle(
            "-fx-text-fill: " + color + "; " +
            "-fx-font-size: " + (isHeader ? "17px" : "15px") + "; " +
            "-fx-font-weight: " + (isHeader ? "bold" : "normal") + "; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 2, 0, 0, 0);"
        );
        return label;
    }
    
    /**
     * Sets the action buttons for the game over panel.
     * 
     * @param newGameAction action to perform on "New Game" button click
     * @param menuAction action to perform on "Back to Menu" button click
     */
    public void setButtons(javafx.event.EventHandler<javafx.event.ActionEvent> newGameAction,
                          javafx.event.EventHandler<javafx.event.ActionEvent> menuAction) {
        buttonsContainer.getChildren().clear();
        
        Button newGameButton = createActionButton("New Game", newGameAction);
        Button menuButton = createActionButton("Back to Menu", menuAction);
        
        buttonsContainer.getChildren().addAll(newGameButton, menuButton);
    }
    
    /**
     * Creates a styled action button.
     */
    private Button createActionButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button button = new Button(text);
        button.setOnAction(action);
        button.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #9A3BF2, #7A2BC2); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12px 30px; " +
            "-fx-border-color: rgba(157, 78, 221, 1.0); " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 4, 0, 0, 2);"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #BA5BF2, #9A3BD2); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12px 30px; " +
            "-fx-border-color: rgba(157, 78, 221, 1.0); " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand; " +
            "-fx-scale-x: 1.05; " +
            "-fx-scale-y: 1.05; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 5, 0, 0, 2);"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #9A3BF2, #7A2BC2); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12px 30px; " +
            "-fx-border-color: rgba(157, 78, 221, 1.0); " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand; " +
            "-fx-scale-x: 1.0; " +
            "-fx-scale-y: 1.0; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 4, 0, 0, 2);"
        ));
        
        return button;
    }
}

