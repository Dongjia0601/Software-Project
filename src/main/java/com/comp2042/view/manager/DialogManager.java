package com.comp2042.view.manager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Manages dialog windows and scene navigation.
 * 
 * <p>This class is responsible for displaying modal dialogs (Settings, Help),
 * handling scene transitions (return to menu, return to level selection),
 * and managing window lifecycle.</p>
 * 
 * <p><strong>Responsibilities:</strong></p>
 * <ul>
 *   <li>Show Settings dialog with current configuration</li>
 *   <li>Show Help/Gameplay Guide dialog</li>
 *   <li>Handle navigation back to main menu</li>
 *   <li>Handle navigation back to level selection</li>
 *   <li>Manage Stage references and window positioning</li>
 * </ul>
 * 
 * <p><strong>Design Pattern:</strong> Extracted from GuiController to adhere to Single Responsibility Principle (SRP)</p>
 * 
 * @author Dong, Jia
 * @version Phase 3+ - SRP Refactoring (Continued Optimization)
 */
public class DialogManager {
    
    // References to main UI components
    private BorderPane rootPane;
    private GridPane gamePanel;
    
    // Callback interface for dialog actions
    private DialogCallbacks callbacks;
    
    /**
     * Callback interface for dialog manager actions.
     * Allows the GameViewController to respond to dialog events.
     */
    public interface DialogCallbacks {
        /**
         * Called when settings dialog is closed and settings may have changed.
         */
        void onSettingsChanged();
        
        /**
         * Called when navigating back to main menu.
         * 
         * @param stage the current stage
         */
        void onReturnToMenu(Stage stage);
        
        /**
         * Called when navigating back to level selection.
         * 
         * @param stage the current stage
         */
        void onReturnToLevelSelection(Stage stage);
    }
    
    /**
     * Constructs a DialogManager.
     * 
     * @param rootPane the root BorderPane (for scene reference)
     * @param gamePanel the game GridPane (fallback for scene reference)
     */
    public DialogManager(BorderPane rootPane, GridPane gamePanel) {
        this.rootPane = rootPane;
        this.gamePanel = gamePanel;
    }
    
    /**
     * Sets the callbacks for dialog actions.
     * 
     * @param callbacks the callback interface implementation
     */
    public void setCallbacks(DialogCallbacks callbacks) {
        this.callbacks = callbacks;
    }
    
    /**
     * Shows the Settings dialog.
     * Opens a modal dialog allowing user to configure game settings.
     * 
     * @param isTwoPlayerMode true if currently in two-player mode
     */
    public void showSettings(boolean isTwoPlayerMode) {
        try {
            // Load Settings FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Settings.fxml"));
            Parent settingsRoot = loader.load();
            
            // Get the controller
            com.comp2042.controller.menu.SettingsController settingsController = loader.getController();
            
            // Get current scene and stage
            Scene currentGameScene;
            if (rootPane != null && rootPane.getScene() != null) {
                currentGameScene = rootPane.getScene();
            } else if (gamePanel != null && gamePanel.getScene() != null) {
                currentGameScene = gamePanel.getScene();
            } else {
                System.err.println("Cannot open settings: No valid scene reference");
                return;
            }
            
            Stage stage = (Stage) currentGameScene.getWindow();
            
            // Create new scene for settings
            double settingsWidth = isTwoPlayerMode ? 1400 : 900;
            double settingsHeight = isTwoPlayerMode ? 900 : 800;
            Scene settingsScene = new Scene(settingsRoot, settingsWidth, settingsHeight);
            settingsScene.getStylesheets().add(getClass().getResource("/settingsStyle.css").toExternalForm());
            
            // Pass stage and previous scene to settings controller
            // Note: SettingsController handles stage and scene internally
            // Initialize is called automatically by FXMLLoader
            
            // Set the new scene
            stage.setScene(settingsScene);
            
            // Center window
            centerWindowOnScreen(stage, settingsWidth, settingsHeight);
            
            // Notify callbacks when returning from settings
            if (callbacks != null) {
                // Set a listener on scene change to detect return
                stage.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene == currentGameScene) {
                        callbacks.onSettingsChanged();
                    }
                });
            }
            
        } catch (IOException e) {
            System.err.println("Error loading Settings: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Shows the Help/Gameplay Guide dialog.
     * Displays a modal dialog with game instructions and controls.
     */
    public void showHelp() {
        try {
            // Create Help dialog UI programmatically
            javafx.scene.layout.VBox helpRoot = createHelpDialog();
            
            // Create modal stage
            Stage helpStage = new Stage();
            helpStage.setTitle("Gameplay Guide");
            helpStage.initModality(Modality.APPLICATION_MODAL);
            helpStage.setResizable(false);
            
            // Create scene
            Scene helpScene = new Scene(helpRoot, 800, 600);
            helpScene.getStylesheets().add(getClass().getResource("/settingsStyle.css").toExternalForm());
            
            helpStage.setScene(helpScene);
            helpStage.show();
            
            // Center on screen
            centerWindowOnScreen(helpStage, 800, 600);
            
        } catch (Exception e) {
            System.err.println("Error showing help dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates the Help dialog UI programmatically.
     * 
     * @return the VBox containing the help content
     */
    private javafx.scene.layout.VBox createHelpDialog() {
        javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(20);
        root.setPadding(new javafx.geometry.Insets(30));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #0A0E27, #1A1E37);");
        
        // Title
        javafx.scene.control.Label title = new javafx.scene.control.Label("TETRIS GAMEPLAY GUIDE");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #4ECDC4;");
        
        // Controls section
        javafx.scene.control.Label controlsTitle = new javafx.scene.control.Label("CONTROLS");
        controlsTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 10 0 5 0;");
        
        String controlsText = 
            "Single Player:\n" +
            "  A/D - Move Left/Right\n" +
            "  W - Rotate Clockwise\n" +
            "  F - Rotate Counter-Clockwise\n" +
            "  S - Soft Drop\n" +
            "  SPACE - Hard Drop\n" +
            "  SHIFT - Hold Piece\n\n" +
            "Two Player:\n" +
            "  Player 1: Same as single player\n" +
            "  Player 2: Arrow Keys + Numpad 0/2/3\n\n" +
            "Global:\n" +
            "  P - Pause/Resume\n" +
            "  N - New Game\n" +
            "  M - Mute/Unmute";
        
        javafx.scene.control.Label controlsLabel = new javafx.scene.control.Label(controlsText);
        controlsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF; -fx-font-family: 'Consolas';");
        
        // Game modes section
        javafx.scene.control.Label modesTitle = new javafx.scene.control.Label("GAME MODES");
        modesTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 10 0 5 0;");
        
        String modesText = 
            "Endless Mode: Play without limits, level up automatically\n" +
            "Level Mode: Complete objectives with time limits\n" +
            "Two Player VS: Compete head-to-head";
        
        javafx.scene.control.Label modesLabel = new javafx.scene.control.Label(modesText);
        modesLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
        
        // Close button
        javafx.scene.control.Button closeButton = new javafx.scene.control.Button("Close");
        closeButton.setStyle(
            "-fx-background-color: #4ECDC4; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10 30; " +
            "-fx-background-radius: 5;"
        );
        closeButton.setOnAction(e -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });
        
        // Add all components
        root.getChildren().addAll(
            title,
            controlsTitle,
            controlsLabel,
            modesTitle,
            modesLabel,
            closeButton
        );
        
        root.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        
        return root;
    }
    
    /**
     * Returns to the main menu.
     * Stops current game and loads the main menu scene.
     */
    public void returnToMenu() {
        try {
            Stage stage;
            if (rootPane != null && rootPane.getScene() != null) {
                stage = (Stage) rootPane.getScene().getWindow();
            } else if (gamePanel != null && gamePanel.getScene() != null) {
                stage = (Stage) gamePanel.getScene().getWindow();
            } else {
                System.err.println("Cannot return to menu: No valid scene reference");
                return;
            }
            
            if (callbacks != null) {
                callbacks.onReturnToMenu(stage);
            }
            
        } catch (Exception e) {
            System.err.println("Error returning to menu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Returns to the level selection screen.
     * Only applicable when in level mode.
     */
    public void returnToLevelSelection() {
        try {
            Stage stage;
            if (rootPane != null && rootPane.getScene() != null) {
                stage = (Stage) rootPane.getScene().getWindow();
            } else if (gamePanel != null && gamePanel.getScene() != null) {
                stage = (Stage) gamePanel.getScene().getWindow();
            } else {
                System.err.println("Cannot return to level selection: No valid scene reference");
                return;
            }
            
            if (callbacks != null) {
                callbacks.onReturnToLevelSelection(stage);
            }
            
        } catch (Exception e) {
            System.err.println("Error returning to level selection: " + e.getMessage());
            e.printStackTrace();
            // Fallback to main menu
            returnToMenu();
        }
    }
    
    /**
     * Centers a window on the screen.
     * 
     * @param stage the stage to center
     * @param width the window width
     * @param height the window height
     */
    private void centerWindowOnScreen(Stage stage, double width, double height) {
        javafx.stage.Screen screen = javafx.stage.Screen.getPrimary();
        javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();
        
        stage.setX((bounds.getWidth() - width) / 2);
        stage.setY((bounds.getHeight() - height) / 2);
    }
}

