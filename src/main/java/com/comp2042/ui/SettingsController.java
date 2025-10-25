package com.comp2042.ui;

import com.comp2042.config.GameSettings;
// import com.comp2042.ui.theme.CosmicTheme; // Removed - not available in addition branch
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the Settings page.
 * Manages all game settings including audio, controls, and display options.
 * 
 * <p>This controller provides a comprehensive settings interface with:
 * <ul>
 *   <li>Audio controls (master volume, music, sound effects)</li>
 *   <li>Control key bindings display</li>
 *   <li>Display options (FPS, grid)</li>
 *   <li>Save/Load functionality</li>
 * </ul>
 * 
 * @author Dong, Jia.
 */
public class SettingsController {
    
    // Audio controls
    @FXML
    private Slider masterVolumeSlider;
    
    @FXML
    private Slider musicVolumeSlider;
    
    @FXML
    private Slider sfxVolumeSlider;
    
    @FXML
    private Label masterVolumeLabel;
    
    @FXML
    private Label musicVolumeLabel;
    
    @FXML
    private Label sfxVolumeLabel;
    
    // Status label
    @FXML
    private Label statusLabel;
    
    @FXML
    private javafx.scene.control.Button backToGameButton;
    
    @FXML
    private javafx.scene.control.Button backToMenuButton;
    
    @FXML
    private javafx.scene.control.Button saveButton;
    
    @FXML
    private javafx.scene.control.Button resetButton;
    
    private GameSettings settings;
    private Stage stage;
    private Scene savedGameScene; // Saved game scene for returning to game
    private com.comp2042.GuiController guiController; // Reference to GUI controller for resuming game
    private boolean wasGamePausedBeforeSettings; // Track if game was already paused 
    
    /**
     * Initializes the settings controller.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        settings = GameSettings.getInstance();
        
        // CRITICAL FIX: Disable space key for all buttons to prevent accidental activation
        disableSpaceKeyForButtons();
        
        // Setup volume sliders with listeners
        setupVolumeSlider(masterVolumeSlider, masterVolumeLabel, 
                         settings.getMasterVolume() * 100);
        setupVolumeSlider(musicVolumeSlider, musicVolumeLabel, 
                         settings.getMusicVolume() * 100);
        setupVolumeSlider(sfxVolumeSlider, sfxVolumeLabel, 
                         settings.getSfxVolume() * 100);
    }
    
    /**
     * Sets up a volume slider with value change listener.
     * 
     * @param slider the slider to setup
     * @param label the label to update with percentage
     * @param initialValue the initial value (0-100)
     */
    private void setupVolumeSlider(Slider slider, Label label, double initialValue) {
        slider.setValue(initialValue);
        label.setText(String.format("%.0f%%", initialValue));
        
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            label.setText(String.format("%.0f%%", newVal.doubleValue()));
        });
    }
    
    /**
     * Sets the stage for this controller (for main menu mode).
     * 
     * @param stage the JavaFX stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Disables space key for all buttons to prevent accidental activation.
     * CRITICAL FIX: Uses addEventFilter for CAPTURE phase to intercept events BEFORE button processes them.
     */
    private void disableSpaceKeyForButtons() {
        // CRITICAL: Use addEventFilter (CAPTURE phase) instead of setOnKeyPressed (BUBBLING phase)
        
        if (saveButton != null) {
            saveButton.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.SPACE) {
                    event.consume();
                    System.out.println("Space key intercepted (CAPTURE) for Save button");
                }
            });
        }
        
        if (resetButton != null) {
            resetButton.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.SPACE) {
                    event.consume();
                    System.out.println("Space key intercepted (CAPTURE) for Reset button");
                }
            });
        }
        
        if (backToGameButton != null) {
            backToGameButton.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.SPACE) {
                    event.consume();
                    System.out.println("Space key intercepted (CAPTURE) for Back to Game button");
                }
            });
        }
        
        if (backToMenuButton != null) {
            backToMenuButton.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.SPACE) {
                    event.consume();
                    System.out.println("Space key intercepted (CAPTURE) for Back to Menu button");
                }
            });
        }
    }
    
    /**
     * Sets up keyboard event handling for the settings scene.
     * CRITICAL FIX: Uses addEventFilter for CAPTURE phase at Scene level.
     * 
     * @param scene the settings scene
     */
    public void setupKeyboardHandling(Scene scene) {
        // CRITICAL: Use addEventFilter at Scene level for CAPTURE phase
        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.SPACE) {
                event.consume();
                System.out.println("Space key intercepted (CAPTURE) at Scene level - settings");
            }
        });
    }
    
    /**
     * Sets the saved game scene for returning to game.
     * If a game scene is provided, both "Back to Game" and "Exit to Menu" buttons are shown.
     * Otherwise, only "Back to Menu" button is shown.
     * 
     * @param gameScene the saved game scene, or null if opened from menu
     */
    public void setSavedGameScene(Scene gameScene) {
        this.savedGameScene = gameScene;
        
        // Show/hide buttons based on whether there's a saved game scene
        if (gameScene != null) {
            // From game: show both buttons
            if (backToGameButton != null) {
                backToGameButton.setVisible(true);
                backToGameButton.setManaged(true);
            }
            if (backToMenuButton != null) {
                backToMenuButton.setText("Exit to Menu");
            }
        } else {
            // From menu: only show back to menu button
            if (backToGameButton != null) {
                backToGameButton.setVisible(false);
                backToGameButton.setManaged(false);
            }
            if (backToMenuButton != null) {
                backToMenuButton.setText("Back to Menu");
            }
        }
    }
    
    /**
     * Sets the GUI controller reference for game state management.
     * 
     * @param guiController the GUI controller managing the game
     * @param wasAlreadyPaused whether the game was paused before opening settings
     */
    public void setGameController(com.comp2042.GuiController guiController, boolean wasAlreadyPaused) {
        this.guiController = guiController;
        this.wasGamePausedBeforeSettings = wasAlreadyPaused;
        
        System.out.println("SettingsController: Game controller set (was already paused: " + wasAlreadyPaused + ")");
    }
    
    /**
     * Saves all current settings.
     */
    @FXML
    public void saveSettings() {
        // Update settings object with current UI values
        settings.setMasterVolume(masterVolumeSlider.getValue() / 100.0);
        settings.setMusicVolume(musicVolumeSlider.getValue() / 100.0);
        settings.setSfxVolume(sfxVolumeSlider.getValue() / 100.0);
        
        // Save to file
        boolean success = settings.saveSettings();
        
        if (success) {
            showStatus("Settings saved successfully!", "#4DFFFF");
            
            // Update Mute button state in game interface
            if (guiController != null) {
                guiController.updateMuteButtonState();
            }
        } else {
            showStatus("Failed to save settings.", "#FF6B6B");
        }
    }
    
    /**
     * Resets all settings to default values.
     */
    @FXML
    public void resetToDefault() {
        settings.resetToDefaults();
        
        // Update UI to reflect defaults
        masterVolumeSlider.setValue(settings.getMasterVolume() * 100);
        musicVolumeSlider.setValue(settings.getMusicVolume() * 100);
        sfxVolumeSlider.setValue(settings.getSfxVolume() * 100);
        
        // Update Mute button state in game interface
        if (guiController != null) {
            guiController.updateMuteButtonState();
        }
        
        showStatus("Settings reset to defaults", "#FFD700");
    }
    
    /**
     * Returns to the game.
     * Restores the saved game scene and resumes gameplay automatically.
     */
    @FXML
    public void backToGame() {
        if (savedGameScene != null && stage != null) {
            // Restore the saved game scene
            stage.setScene(savedGameScene);
            stage.setTitle("TETRIS - Game");
            
            // CRITICAL FIX: Resume game automatically if it wasn't paused before settings
            if (guiController != null && !wasGamePausedBeforeSettings) {
                guiController.resumeGame(); // Resume game timeline
                System.out.println("Game automatically resumed after settings");
            } else {
                System.out.println("Returned to game from settings (kept paused)");
            }
        }
    }
    
    /**
     * Returns to main menu.
     * Ends the current game and frees memory.
     */
    @FXML
    public void backToMenu() {
        System.out.println("========== backToMenu() CLICKED ==========");
        System.out.println("Stage reference: " + (stage != null ? "VALID" : "NULL"));
        
        if (stage == null) {
            System.err.println("ERROR: Stage is null! Cannot navigate.");
            showStatus("Error: Stage not initialized", "#FF6B6B");
            return;
        }
        
        try {
            System.out.println("Loading main menu FXML...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainMenu.fxml"));
            Parent root = loader.load();
            System.out.println("Main menu FXML loaded successfully");
            
            com.comp2042.MainMenuController menuController = loader.getController();
            // Note: MainMenuController doesn't have setStage method in addition branch
            
            // Create scene with main menu size
            Scene scene = new Scene(root, 900, 800);
            System.out.println("Scene created: 900x800");
            
            // Apply inline theme (CosmicTheme not available in addition branch)
            scene.getRoot().setStyle("-fx-background-color: linear-gradient(to bottom, #0a0e27 0%, #1a1a3e 25%, #2d1b69 50%, #1a1a3e 75%, #0f0c29 100%);");
            System.out.println("Theme applied");
            
            stage.setScene(scene);
            stage.setTitle("TETRIS - Main Menu");
            System.out.println("Scene switched to main menu successfully!");
            
        } catch (IOException e) {
            System.err.println("ERROR loading main menu: " + e.getMessage());
            e.printStackTrace();
            showStatus("Error returning to menu", "#FF6B6B");
        } catch (Exception e) {
            System.err.println("UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace();
            showStatus("Unexpected error", "#FF6B6B");
        }
    }
    
    /**
     * Shows a status message to the user.
     * 
     * @param message the message to display
     * @param color the color of the message
     */
    private void showStatus(String message, String color) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + color + ";");
        
        // Clear status after 3 seconds
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> statusLabel.setText(""));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
