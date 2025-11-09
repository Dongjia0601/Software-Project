package com.comp2042.ui;

import com.comp2042.SoundManager;
import com.comp2042.config.GameSettings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ChoiceBox;
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

    // Gameplay: piece randomizer
    @FXML
    private ChoiceBox<String> randomizerChoice;

    // Track original randomizer to detect changes on save
    private String originalRandomizer;
    
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

        // Setup randomizer choice box if present
        if (randomizerChoice != null) {
            final String LABEL_7BAG = "7-Bag System (Recommended)";
            final String LABEL_RANDOM = "Pure Random System (Classic)";
            randomizerChoice.getItems().setAll(LABEL_7BAG, LABEL_RANDOM);
            String mode = settings.getPieceRandomizer();
            boolean isPure = "pure_random".equalsIgnoreCase(mode);
            randomizerChoice.setValue(isPure ? LABEL_RANDOM : LABEL_7BAG);
            originalRandomizer = isPure ? "pure_random" : "seven_bag";

            // Color the control based on selection (ChoiceBox has no cell factory API)
            java.util.function.Consumer<String> applyStyle = (val) -> {
                // Always blue to match key legend style
                randomizerChoice.setStyle("-fx-text-fill: #4DFFFF; -fx-border-color: rgba(77,255,255,0.6); -fx-background-color: rgba(77,255,255,0.12);");
            };
            applyStyle.accept(randomizerChoice.getValue());
            // Live confirm on selection change: revert to original if user cancels
            final boolean[] reverting = new boolean[] { false };
            randomizerChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                applyStyle.accept(newVal);
                if (reverting[0]) {
                    // Prevent re-entrancy when we programmatically revert
                    reverting[0] = false;
                    return;
                }
                String target = (newVal != null && newVal.startsWith("Pure Random")) ? "pure_random" : "seven_bag";
                if (originalRandomizer != null && !originalRandomizer.equals(target)) {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirm Selection");
                    alert.setHeaderText("Change Piece Randomizer?");
                    alert.setContentText("This selection will switch the piece randomizer and restart the game after you click Save.\n\nProceed with this selection?");
                    alert.getButtonTypes().setAll(javafx.scene.control.ButtonType.YES, javafx.scene.control.ButtonType.NO);
                    
                    // Add button click sound for dialog buttons
                    alert.getDialogPane().getButtonTypes().forEach(buttonType -> {
                        javafx.scene.Node button = alert.getDialogPane().lookupButton(buttonType);
                        if (button != null) {
                            button.setOnMouseClicked(e -> SoundManager.getInstance().playButtonClickSound());
                        }
                    });
                    
                    java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.YES) {
                        // Accept change in settings model; actual rebuild handled on Save/Reset
                        settings.setPieceRandomizer(target);
                        showStatus("Piece randomizer updated. Save to apply now.", "#4DFFFF");
                    } else {
                        // Revert selection immediately, stay on settings
                        reverting[0] = true;
                    randomizerChoice.setValue("pure_random".equals(originalRandomizer) ? LABEL_RANDOM : LABEL_7BAG);
                        applyStyle.accept(randomizerChoice.getValue());
                        showStatus("Change canceled.", "#FFD700");
                    }
                }
            });
        }
    }
    
    /**
     * Sets up a volume slider with value change listener.
     * Updates SoundManager in real-time as user drags the slider.
     * 
     * @param slider the slider to setup
     * @param label the label to update with percentage
     * @param initialValue the initial value (0-100)
     */
    private void setupVolumeSlider(Slider slider, Label label, double initialValue) {
        slider.setValue(initialValue);
        label.setText(String.format("%.0f%%", initialValue));
        
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double value = newVal.doubleValue() / 100.0;
            label.setText(String.format("%.0f%%", newVal.doubleValue()));
            
            // Update SoundManager in real-time
            SoundManager soundManager = SoundManager.getInstance();
            if (slider == masterVolumeSlider) {
                soundManager.setMasterVolume(value);
            } else if (slider == musicVolumeSlider) {
                soundManager.setMusicVolume(value);
            } else if (slider == sfxVolumeSlider) {
                soundManager.setSfxVolume(value);
            }
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
     * If a game scene is provided, both "Back to Game" and "Back to Menu" buttons are shown.
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
                backToMenuButton.setText("Back to Menu");
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
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        // Update settings object with current UI values
        settings.setMasterVolume(masterVolumeSlider.getValue() / 100.0);
        settings.setMusicVolume(musicVolumeSlider.getValue() / 100.0);
        settings.setSfxVolume(sfxVolumeSlider.getValue() / 100.0);

        String pendingRandomizer = originalRandomizer;
        boolean randomizerChanged = false;
        if (randomizerChoice != null) {
            String selected = randomizerChoice.getValue();
            pendingRandomizer = (selected != null && selected.startsWith("Pure Random") ? "pure_random" : "seven_bag");
            randomizerChanged = (originalRandomizer != null && !originalRandomizer.equals(pendingRandomizer));
        }

        // If randomizer changed, ask for confirmation because this resets the game
        if (randomizerChanged) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Gameplay Change");
            alert.setHeaderText("Change Piece Randomizer?");
            alert.setContentText("Switching the piece randomizer will reset the current game.\n\nDo you want to apply the change now?");
            alert.getButtonTypes().setAll(javafx.scene.control.ButtonType.YES, javafx.scene.control.ButtonType.NO);
            
            // Add button click sound for dialog buttons
            alert.getDialogPane().getButtonTypes().forEach(buttonType -> {
                javafx.scene.Node button = alert.getDialogPane().lookupButton(buttonType);
                if (button != null) {
                    button.setOnMouseClicked(e -> SoundManager.getInstance().playButtonClickSound());
                }
            });
            
            java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.YES) {
                settings.setPieceRandomizer(pendingRandomizer);
                originalRandomizer = pendingRandomizer; 
            } else {
                // Revert UI selection
                if (randomizerChoice != null) {
                    randomizerChoice.setValue("pure_random".equals(originalRandomizer) ? "Pure Random System (Classic)" : "7-Bag System (Recommended)");
                }
                pendingRandomizer = originalRandomizer;
                // Remain in settings, show inline notice
                showStatus("Change canceled.", "#FFD700");
            }
        }

        // Apply volumes to SoundManager immediately
        SoundManager soundManager = SoundManager.getInstance();
        soundManager.setVolumes(
            settings.getMasterVolume(),
            settings.getMusicVolume(),
            settings.getSfxVolume()
        );
        
        // Persist settings (volumes and possibly randomizer)
        boolean success = settings.saveSettings();
        
        if (success) {
            showStatus("Settings saved successfully!", "#4DFFFF");
            
            // Update Mute button state in game interface
            if (guiController != null) {
                guiController.updateMuteButtonState();
                // If randomizer was applied and we are in-game, rebuild the board immediately
                if (randomizerChanged && pendingRandomizer.equals(settings.getPieceRandomizer())) {
                    // Return to game scene if we are currently in the settings scene
                    if (savedGameScene != null && stage != null) {
                        stage.setScene(savedGameScene);
                        stage.setTitle("TETRIS - Game");
                    }
                    guiController.rebuildGameForRandomizerChange();
                    // Update baseline to new value for subsequent saves
                    originalRandomizer = settings.getPieceRandomizer();
                }
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
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        // Capture current randomizer to decide whether we need confirmation
        String currentRandomizer = settings.getPieceRandomizer();

        // Always reset audio sliders to defaults first
        settings.resetToDefaults();
        masterVolumeSlider.setValue(settings.getMasterVolume() * 100);
        musicVolumeSlider.setValue(settings.getMusicVolume() * 100);
        sfxVolumeSlider.setValue(settings.getSfxVolume() * 100);
        
        // Apply reset volumes to SoundManager immediately
        SoundManager soundManager = SoundManager.getInstance();
        soundManager.setVolumes(
            settings.getMasterVolume(),
            settings.getMusicVolume(),
            settings.getSfxVolume()
        );

        // If current piece system is pure_random, ask user before forcing 7-bag reset
        if ("pure_random".equalsIgnoreCase(currentRandomizer)) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Gameplay Change");
            alert.setHeaderText("Reset Piece System to Default?");
            alert.setContentText("Resetting to defaults will switch the piece randomizer from 'Pure Random' to '7-Bag' and restart the game.\n\nApply now?");
            alert.getButtonTypes().setAll(javafx.scene.control.ButtonType.YES, javafx.scene.control.ButtonType.NO);
            
            // Add button click sound for dialog buttons
            alert.getDialogPane().getButtonTypes().forEach(buttonType -> {
                javafx.scene.Node button = alert.getDialogPane().lookupButton(buttonType);
                if (button != null) {
                    button.setOnMouseClicked(e -> SoundManager.getInstance().playButtonClickSound());
                }
            });
            
            java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.YES) {
                // Apply default piece system and persist
                settings.setPieceRandomizer("seven_bag");
                if (randomizerChoice != null) {
                    randomizerChoice.setValue("7-Bag System (Recommended)");
                }
                originalRandomizer = "seven_bag";
                settings.saveSettings();

                // Back to game and rebuild so change takes effect immediately
                if (guiController != null && savedGameScene != null && stage != null) {
                    stage.setScene(savedGameScene);
                    stage.setTitle("TETRIS - Game");
                    guiController.rebuildGameForRandomizerChange();
                }
                showStatus("Defaults applied (7-Bag) and game restarted", "#4DFFFF");
                return;
            } else {
                // Keep Pure Random and return to game like Back to Game
                settings.setPieceRandomizer("pure_random");
                if (randomizerChoice != null) {
                    randomizerChoice.setValue("Pure Random System (Classic)");
                }
                settings.saveSettings();
                if (guiController != null && savedGameScene != null && stage != null) {
                    stage.setScene(savedGameScene);
                    stage.setTitle("TETRIS - Game");
                    guiController.resumeFromOverlay();
                }
                showStatus("Kept Pure Random and returned to game", "#FFD700");
                return;
            }
        }

        // If already seven_bag (or anything else), enforce defaults quietly
        settings.setPieceRandomizer("seven_bag");
        if (randomizerChoice != null) {
            randomizerChoice.setValue("7-Bag System (Recommended)");
        }
        originalRandomizer = "seven_bag";
        settings.saveSettings();

        if (guiController != null) {
            guiController.updateMuteButtonState();
        }
        // Do not auto-return if piece system was already 7-Bag; just notify under the buttons
        showStatus("Audio settings have been reset to defaults!", "#FFD700");
    }
    
    /**
     * Returns to the game.
     * Restores the saved game scene and resumes gameplay automatically.
     */
    @FXML
    public void backToGame() {
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        if (savedGameScene != null && stage != null) {
            // Restore the saved game scene
            stage.setScene(savedGameScene);
            stage.setTitle("TETRIS - Game");
            
            // CRITICAL FIX: Resume game automatically if it wasn't paused before settings
            if (guiController != null && !wasGamePausedBeforeSettings) {
                guiController.resumeFromOverlay(); // Toggle state and resume
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
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
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

            
            // Create scene with main menu size
            Scene scene = new Scene(root, 900, 800);
            System.out.println("Scene created: 900x800");
            
            // Apply inline theme
            scene.getRoot().setStyle("-fx-background-color: linear-gradient(to bottom, #0a0e27 0%, #1a1a3e 25%, #2d1b69 50%, #1a1a3e 75%, #0f0c29 100%);");
            System.out.println("Theme applied");
            
            stage.setScene(scene);
            stage.setTitle("TETRIS - Main Menu");

            centerWindowOnScreen(stage, 900, 800);
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
