package com.comp2042;

import com.comp2042.gameplay.GameModeFactory;
import com.comp2042.gameplay.GameModeType;
import com.comp2042.core.GameService;
import com.comp2042.core.GameServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.geometry.Rectangle2D;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the main menu interface.
 * 
 * <p>This class handles the main menu functionality and provides navigation
 * to different game modes. It manages the user interface components and
 * coordinates the transition between the main menu and various game modes.</p>
 * 
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Initialize main menu UI components</li>
 *   <li>Handle game mode selection events</li>
 *   <li>Create and configure game services for different modes</li>
 *   <li>Manage scene transitions between menu and game</li>
 * </ul>
 */
public class MainMenuController {

    // FXML-injected UI components for game mode selection
    @FXML
    private Button endlessModeBtn;
    
    @FXML
    private Button levelModeBtn;
    
    @FXML
    private Button twoPlayerModeBtn;
    
    @FXML
    private Button settingsBtn;
    
    @FXML
    private Button helpBtn;
    
    @FXML
    private AnchorPane rootPane;
    
    @FXML
    private Text titleText;

    /**
     * Initializes the main menu controller after FXML loading.
     * 
     * <p>This method is automatically called by JavaFX after the FXML file has been
     * loaded and all @FXML annotated fields have been injected. It sets up the
     * initial state of the UI components and prepares them for user interaction.</p>
     * 
     * <p>The method performs the following operations:</p>
     * <ul>
     *   <li>Sets appropriate text labels for all game mode buttons</li>
     *   <li>Ensures consistent UI appearance across all buttons</li>
     *   <li>Prepares the interface for user interaction</li>
     * </ul>
     */
    @FXML
    public void initialize() {
        // Configure button text for different game modes
        endlessModeBtn.setText("Endless Mode");
        levelModeBtn.setText("Level Mode");
        twoPlayerModeBtn.setText("Two-Player Mode");
        
        // Set centered background image
        setCenteredBackground();
        
        // Enhance Tetris title with advanced neon effects
        enhanceTitleNeonEffect();
        
        // CRITICAL FIX: Disable Space key for all buttons to prevent accidental activation
        disableSpaceKeyForButtons();
        
        // Load volume settings from GameSettings and apply to SoundManager
        com.comp2042.config.GameSettings settings = com.comp2042.config.GameSettings.getInstance();
        SoundManager soundManager = SoundManager.getInstance();
        soundManager.setVolumes(
            settings.getMasterVolume(),
            settings.getMusicVolume(),
            settings.getSfxVolume()
        );
        
        // Start playing main menu background music when main menu loads
        SoundManager.getInstance().playMainMenuBackgroundMusic();
    }
    
    /**
     * Disables Space key for all buttons to prevent accidental activation.
     * CRITICAL FIX: Uses addEventFilter for CAPTURE phase to intercept events BEFORE button processes them.
     */
    private void disableSpaceKeyForButtons() {
        // CRITICAL: Use addEventFilter (CAPTURE phase) instead of setOnKeyPressed (BUBBLING phase)
        
        if (endlessModeBtn != null) {
            endlessModeBtn.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.SPACE) {
                    event.consume();
                }
            });
        }
        
        if (levelModeBtn != null) {
            levelModeBtn.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.SPACE) {
                    event.consume();
                }
            });
        }
        
        
        if (twoPlayerModeBtn != null) {
            twoPlayerModeBtn.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.SPACE) {
                    event.consume();
                }
            });
        }
        
        if (settingsBtn != null) {
            settingsBtn.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.SPACE) {
                    event.consume();
                }
            });
        }
        
        if (helpBtn != null) {
            helpBtn.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.SPACE) {
                    event.consume();
                }
            });
        }
    }
    
    /**
     * Enhances the Tetris title with clean neon glow effects.
     * Simple and effective: bright magenta text with cyan outer glow.
     */
    private void enhanceTitleNeonEffect() {
        if (titleText != null) {
            // Set bright magenta text color
            titleText.setFill(Color.web("#FF6BFF")); // Vibrant magenta/pink
            
            // Simple but effective: single strong cyan glow
            DropShadow glow = new DropShadow(30, Color.web("#4DFFFF")); // Bright cyan
            glow.setSpread(0.5);
            
            // Apply effect
            titleText.setEffect(glow);
        }
    }
    
    /**
     * Sets the background image with centered cropping.
     * This ensures the middle portion of the image is displayed when the image is wider than the window.
     */
    private void setCenteredBackground() {
        try {
            // Load the background image
            Image bgImage = new Image(getClass().getClassLoader().getResourceAsStream("images/backgrounds/main_menu_bg1.jpg"));
            
            // Window dimensions
            double windowWidth = 900.0;
            double windowHeight = 800.0;
            double windowAspectRatio = windowWidth / windowHeight;
            
            // Image dimensions
            double imageWidth = bgImage.getWidth();
            double imageHeight = bgImage.getHeight();
            double imageAspectRatio = imageWidth / imageHeight;
            
            // Create ImageView for precise control
            ImageView bgImageView = new ImageView(bgImage);
            bgImageView.setPreserveRatio(true);
            
            // Calculate how to display the image to show the center portion
            if (imageAspectRatio > windowAspectRatio) {
                // Image is wider than window - need to crop from sides to show center
                // Scale to fit height (fill vertically), then crop width from center
                double scaleFactor = windowHeight / imageHeight;
                double scaledImageWidth = imageWidth * scaleFactor;
                
                // Calculate what portion of the original image to show
                // We want to show a width that equals windowWidth when scaled
                double originalVisibleWidth = windowWidth / scaleFactor;
                
                // Calculate x offset to center the viewport (crop equal amounts from both sides)
                double xOffset = (imageWidth - originalVisibleWidth) / 2.0;
                
                // Set viewport to show center portion of original image
                bgImageView.setViewport(new Rectangle2D(
                    xOffset,                    // x: start from this x position in original image
                    0,                         // y: start from top
                    originalVisibleWidth,      // width: portion of original image to show
                    imageHeight                // height: full height
                ));
                
                // Set fit size to fill the window height
                bgImageView.setFitHeight(windowHeight);
                bgImageView.setFitWidth(windowWidth);
            } else {
                // Image is taller than window - scale to fit width (will crop top/bottom)
                bgImageView.setFitWidth(windowWidth);
                bgImageView.setFitHeight(windowHeight);
            }
            
            // Position ImageView at (0, 0) to fill the pane
            AnchorPane.setLeftAnchor(bgImageView, 0.0);
            AnchorPane.setRightAnchor(bgImageView, 0.0);
            AnchorPane.setTopAnchor(bgImageView, 0.0);
            AnchorPane.setBottomAnchor(bgImageView, 0.0);
            
            // Add ImageView as the first child (background layer)
            rootPane.getChildren().add(0, bgImageView);
            bgImageView.toBack();  // Ensure it's behind all other elements
            
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Starts the Endless Mode game.
     * 
     * <p>This method is called when the user clicks the "Endless Mode" button.
     * It creates a new game service, initializes the GUI controller, and launches
     * the endless mode game. Endless mode allows players to play indefinitely
     * without level progression or time limits.</p>
     * 
     * <p>The method performs the following operations:</p>
     * <ul>
     *   <li>Creates a new GameService instance for game logic</li>
     *   <li>Initializes a GuiController for UI management</li>
     *   <li>Creates an EndlessMode game mode using the factory pattern</li>
     *   <li>Transitions to the game scene</li>
     * </ul>
     */
    @FXML
    private void startEndlessMode() {
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        try {
            // Create game service and GUI controller
            GameService gameService = new GameServiceImpl();
            GuiController guiController = new GuiController();
            
            // Create endless mode using factory pattern
            var gameMode = GameModeFactory.createGameMode(GameModeType.ENDLESS, gameService, guiController);
            gameMode.initialize();
            
            // Set Endless Mode flag in GUI controller
            guiController.setEndlessMode(true);
            
            // Play endless mode background music
            SoundManager.getInstance().playEndlessBackgroundMusic();
            
            // Transition to game scene
            loadGameScene(guiController);
            
        } catch (Exception e) {
            System.err.println("Error starting Endless Mode: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Starts the Level Mode game.
     * Opens the level selection interface.
     */
    @FXML
    private void startLevelMode() {
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        try {
            // Load level selection scene
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("levelSelection.fxml"));
            Parent root = loader.load();
            Scene levelSelectionScene = new Scene(root, 900, 800);
            
            Stage currentStage = (Stage) levelModeBtn.getScene().getWindow();
            currentStage.setScene(levelSelectionScene);
            currentStage.setTitle("Tetris - Level Selection");
            centerWindowOnScreen(currentStage, 900, 800);
            
            // Set stage reference in controller
            com.comp2042.ui.LevelSelectionController controller = loader.getController();
            if (controller != null) {
                controller.setStage(currentStage);
            }
            
        } catch (Exception e) {
            System.err.println("Error opening Level Selection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Starts the Two-Player Mode game.
     * Creates separate game services for both players and launches VS mode.
     */
    @FXML
    private void startTwoPlayerMode() {
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        try {
            // Load the two-player game layout FXML
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("twoPlayerGameLayout.fxml"));
            Parent root = loader.load();
            
            // Get the GUI controller from the loaded FXML
            GuiController guiController = loader.getController();
            if (guiController == null) {
                guiController = new GuiController();
            }
            
            // Set two-player mode flag BEFORE creating game mode
            guiController.setGameMode(true);
            
            // Play two-player mode background music
            SoundManager.getInstance().playTwoPlayerBackgroundMusic();
            
            // Create VS mode (it will create its own game services)
            var gameMode = GameModeFactory.createGameMode(GameModeType.TWO_PLAYER_VS, null, guiController);
            
            // Create two-player game controller to manage both players
            TwoPlayerGameController twoPlayerController = new TwoPlayerGameController((com.comp2042.game.TwoPlayerVSGameMode) gameMode, guiController);
            
            // Create new scene with two-player layout size
            Scene gameScene = new Scene(root, 1400, 900);
            
            // Get the current stage and set the new scene
            Stage currentStage = (Stage) twoPlayerModeBtn.getScene().getWindow();
            currentStage.setScene(gameScene);
            currentStage.setTitle("TetrisJFX - Two-Player Mode");
            centerWindowOnScreen(currentStage, 1400, 900);
            
            // Set up keyboard focus for input handling AFTER scene is set
            final GuiController gc = guiController;
            final Parent sceneRoot = root;
            // Critical: capture key presses at Scene level so SPACE/BACKSPACE/ALT work even when buttons are focused
            gameScene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                // Forward to game handler first
                gc.handleKeyPressEvent(event);
                // Ensure ALT doesn't trigger mnemonics
                if (event.getCode() == javafx.scene.input.KeyCode.ALT || event.getCode() == javafx.scene.input.KeyCode.ALT_GRAPH) {
                    event.consume();
                }
            });
            javafx.application.Platform.runLater(() -> {
                // Ensure rootPane is initialized and has focus
                if (gc.getRootPane() != null) {
                    gc.getRootPane().setFocusTraversable(true);
                    gc.getRootPane().requestFocus();
                    gc.getRootPane().setOnKeyPressed(gc::handleKeyPressEvent);
                } else if (sceneRoot != null) {
                    // Fallback: use root if rootPane not available
                    sceneRoot.requestFocus();
                    sceneRoot.setOnKeyPressed(gc::handleKeyPressEvent);
                }
            });
            
            
        } catch (Exception e) {
            System.err.println("Error starting Two-Player Mode: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Loads the game scene with the specified GUI controller.
     * 
     * @param guiController the GUI controller for the game
     * @throws IOException if the FXML file cannot be loaded
     */
    private void loadGameScene(GuiController guiController) throws IOException {
        // Load the enhanced game layout FXML
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("enhancedGameLayout.fxml"));
        Parent root = loader.load();
        
        // Set the controller
        GuiController gameController = loader.getController();
        if (gameController == null) {
            gameController = guiController;
        } else {
            // Copy settings from the original controller to the new one
            gameController.setEndlessMode(guiController.isEndlessMode());
        }
        
        // Create new scene with enhanced layout size
        Scene gameScene = new Scene(root, 900, 800);
        
        // Get the current stage and set the new scene
        Stage currentStage = (Stage) endlessModeBtn.getScene().getWindow();
        currentStage.setScene(gameScene);
        currentStage.setTitle("TetrisJFX - Game");
        centerWindowOnScreen(currentStage, 900, 800);
        
        // Initialize the game controller
        new GameController(gameController);
        
    }
    
    /**
     * Handles the settings button click.
     * Opens the settings page.
     */
    @FXML
    private void openSettings() {
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        try {
            FXMLLoader settingsLoader = new FXMLLoader(getClass().getResource("/settings.fxml"));
            // Use same size as main menu for consistency (1000x700)
            Scene settingsScene = new Scene(settingsLoader.load(), 900, 800);
            
            // Get the settings controller and set the stage
            com.comp2042.ui.SettingsController settingsController = settingsLoader.getController();
            settingsController.setStage((Stage) endlessModeBtn.getScene().getWindow());
            settingsController.setSavedGameScene(null); // From main menu, no saved game
            
            // CRITICAL FIX: Set up keyboard handling to prevent space key from triggering buttons
            settingsController.setupKeyboardHandling(settingsScene);
            
            // Apply the settings CSS
            settingsScene.getStylesheets().add(
                getClass().getResource("/settings.css").toExternalForm()
            );
            
            // Switch to settings scene
            Stage stage = (Stage) endlessModeBtn.getScene().getWindow();
            stage.setScene(settingsScene);
            stage.setTitle("TETRIS - Settings");
            centerWindowOnScreen(stage, 900, 800);
            
        } catch (IOException e) {
            System.err.println("Error loading settings page: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handles the help button click.
     * Shows the help dialog with game mode descriptions.
     */
    @FXML
    private void showHelp() {
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        
        try {
            // Create help dialog
            javafx.stage.Stage helpStage = new javafx.stage.Stage();
            helpStage.setTitle("Gameplay Guide");
            helpStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            helpStage.setResizable(false);

            // Scrollable content
            javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefViewportHeight(520);
            scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
            scrollPane.getStyleClass().add("help-scroll");

            // Create main container
            javafx.scene.layout.VBox mainContainer = new javafx.scene.layout.VBox(20);
            mainContainer.setPadding(new javafx.geometry.Insets(30));
            mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #1A0033, #2D1B69);");
            
            // Title
            javafx.scene.control.Label titleLabel = new javafx.scene.control.Label("Gameplay Guide");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4DFFFF; -fx-alignment: center;");
            titleLabel.setMaxWidth(Double.MAX_VALUE);
            javafx.scene.layout.HBox.setHgrow(titleLabel, javafx.scene.layout.Priority.ALWAYS);
            
            // Game modes section (compact, no header)
            javafx.scene.layout.VBox modesContainer = new javafx.scene.layout.VBox(12);
            modesContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.08); -fx-background-radius: 10; -fx-padding: 18;");
            
            // Mode descriptions
            String[][] modeData = {
                {"Endless Mode", "Play endlessly and aim for the highest score."},
                {"Level Mode", "Clear levels with increasing difficulty and unlock new themes."},
                {"Two-Player Mode", "Challenge a friend in local two-player battle."}
            };
            
            for (String[] mode : modeData) {
                javafx.scene.layout.HBox modeRow = new javafx.scene.layout.HBox(20);
                modeRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                
                // Mode name
                javafx.scene.control.Label modeLabel = new javafx.scene.control.Label(mode[0]);
                modeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-min-width: 150;");
                
                // Mode description
                javafx.scene.control.Label descLabel = new javafx.scene.control.Label(mode[1]);
                descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
                descLabel.setMaxWidth(500);
                descLabel.setWrapText(true);
                descLabel.setPrefWidth(500);
                
                modeRow.getChildren().addAll(modeLabel, descLabel);
                modesContainer.getChildren().add(modeRow);
            }

            // Basics & Controls split into two purple boxes
            javafx.scene.layout.HBox basicsDual = new javafx.scene.layout.HBox(20);
            basicsDual.setAlignment(javafx.geometry.Pos.TOP_LEFT);

            // Left: Gameplay Basics & Rules
            javafx.scene.layout.VBox basicsLeftBox = new javafx.scene.layout.VBox(12);
            basicsLeftBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.08); -fx-background-radius: 10; -fx-padding: 18;");
            javafx.scene.control.Label basicsLeftTitle = new javafx.scene.control.Label("Gameplay Basics");
            basicsLeftTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");
            String[] basicsLeftItems = new String[] {
                "Stack falling pieces(bricks) to fill horizontal rows while planning ahead with the Next preview.",
                "Clearing lines awards points, 100/300/500/800 points for single/double/triple/tetris.",
                "Speed increases as more lines are cleared—plan placements early.",
                "The game ends when the stack reaches the top of the board.",
                "Use A/D to move, W or F to rotate(clockwise/counterclockwise), S for soft drop(accelerated), Space for hard drop(instant), Shift to hold."
            };
            javafx.scene.layout.VBox basicsLeftCol = createBulletedColumn(basicsLeftItems, 330);
            basicsLeftBox.getChildren().addAll(basicsLeftTitle, basicsLeftCol);

            // Right: Sidebar Panels & Actions
            javafx.scene.layout.VBox basicsRightBox = new javafx.scene.layout.VBox(12);
            basicsRightBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.08); -fx-background-radius: 10; -fx-padding: 18;");
            javafx.scene.control.Label basicsRightTitle = new javafx.scene.control.Label("Side Panels & Actions");
            basicsRightTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");
            String[] basicsRightItems = new String[] {
                "Next: preview upcoming pieces.",
                "Hold: store one piece to swap later (one swap per piece).",
                "Statistics: shows Level, Lines cleared, Speed and Time.",
                "Score: real-time points and the Highest Score.",
                "Controls: Settings, Help, Back to Menu, Back to Selection",
                "Actions: New Game (N), Pause & Resume (P), Mute / Unmute (M)."
            };
            javafx.scene.layout.VBox basicsRightCol = createBulletedColumn(basicsRightItems, 330);
            basicsRightBox.getChildren().addAll(basicsRightTitle, basicsRightCol);

            basicsDual.getChildren().addAll(basicsLeftBox, basicsRightBox);
            
            // Piece Randomizer help section
            javafx.scene.layout.VBox rngContainer = new javafx.scene.layout.VBox(10);
            rngContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10; -fx-padding: 20;");

            javafx.scene.control.Label rngTitle = new javafx.scene.control.Label("Piece Randomizer Systems");
            rngTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-alignment: center;");
            rngTitle.setMaxWidth(Double.MAX_VALUE);
            javafx.scene.layout.HBox.setHgrow(rngTitle, javafx.scene.layout.Priority.ALWAYS);

            javafx.scene.control.Label rngIntro = new javafx.scene.control.Label(
                "Modern Tetris variants use a \"bag\" to distribute tetrominoes, while early games used pure random selection. Choose your system in Settings > Gameplay > Piece Randomizer. Default is 7‑Bag System.");
            rngIntro.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            rngIntro.setWrapText(true);

            javafx.scene.control.Label bagHeader = new javafx.scene.control.Label("7‑Bag System (Recommended)");
            bagHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");
            javafx.scene.control.Label bagDesc = new javafx.scene.control.Label(
                "• Each set of seven contains I, O, T, S, Z, J, L exactly once, then a new bag is shuffled.\n" +
                "• Guarantees fairness and predictability: no long droughts, no long streaks.\n" +
                "• Best for skill development and consistent difficulty.");
            bagDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            bagDesc.setWrapText(true);

            javafx.scene.control.Label prHeader = new javafx.scene.control.Label("Pure Random System (Classic)");
            prHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");
            javafx.scene.control.Label prDesc = new javafx.scene.control.Label(
                "• Each piece is chosen uniformly at random with replacement.\n" +
                "• Can produce streaks and droughts (harder and more volatile).\n" +
                "• Choose this if you prefer old-school variance and challenge.");
            prDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            prDesc.setWrapText(true);

            javafx.scene.control.Label applyInfo = new javafx.scene.control.Label(
                "Note: Changing the piece randomizer requires a game restart.\n" +
                "When you click Save in the settings page, the current game will reset with the selected system.");
            applyInfo.setStyle("-fx-font-size: 13px; -fx-text-fill: #AAAAAA;");
            applyInfo.setWrapText(true);

            rngContainer.getChildren().addAll(rngTitle, rngIntro, bagHeader, bagDesc, prHeader, prDesc, applyInfo);

            // Scoring help section
            javafx.scene.layout.VBox scoreContainer = new javafx.scene.layout.VBox(10);
            scoreContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10; -fx-padding: 20;");

            javafx.scene.control.Label scoreTitle = new javafx.scene.control.Label("Score System");
            scoreTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-alignment: center;");
            scoreTitle.setMaxWidth(Double.MAX_VALUE);
            javafx.scene.layout.HBox.setHgrow(scoreTitle, javafx.scene.layout.Priority.ALWAYS);

            javafx.scene.control.Label lineScores = new javafx.scene.control.Label(
                "Line Clears:\n" +
                "• Single (1 line): +100 pts\n" +
                "• Double (2 lines): +300 pts\n" +
                "• Triple (3 lines): +500 pts\n" +
                "• Tetris (4 lines): +800 pts");
            lineScores.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            lineScores.setWrapText(true);

            javafx.scene.control.Label dropScores = new javafx.scene.control.Label(
                "Drops:\n" +
                "• Soft Drop: +1 pt per row (accelerated)\n" +
                "• Hard Drop: +2 pts per row (instant)");
            dropScores.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            dropScores.setWrapText(true);

            javafx.scene.layout.HBox scoreRow = new javafx.scene.layout.HBox(40);
            scoreRow.setAlignment(javafx.geometry.Pos.TOP_LEFT);
            lineScores.setPrefWidth(300);
            dropScores.setPrefWidth(300);
            scoreRow.getChildren().addAll(lineScores, dropScores);

            scoreContainer.getChildren().addAll(scoreTitle, scoreRow);

            // Ghost Brick System section
            javafx.scene.layout.VBox ghostContainer = new javafx.scene.layout.VBox(10);
            ghostContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10; -fx-padding: 20;");

            javafx.scene.control.Label ghostTitle = new javafx.scene.control.Label("Ghost Brick System");
            ghostTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-alignment: center;");
            ghostTitle.setMaxWidth(Double.MAX_VALUE);
            javafx.scene.layout.HBox.setHgrow(ghostTitle, javafx.scene.layout.Priority.ALWAYS);

            javafx.scene.control.Label ghostDesc = new javafx.scene.control.Label(
                "The Ghost Brick is a semi-transparent preview that shows where your current piece will land if dropped straight down. " +
                "It helps you plan your placement strategy and make precise drops.\n\n" +
                "Display Conditions:\n" +
                "• Endless Mode: Shown when level is less than 5 (levels 1-4). Not shown from level 5 onwards (up to level 15).\n" +
                "• Level Mode: Shown for Easy difficulty (Level 1 and 2)\n" +
                "• Two-Player Mode: Always shown");
            ghostDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            ghostDesc.setWrapText(true);

            ghostContainer.getChildren().addAll(ghostTitle, ghostDesc);

            // Endless Mode Level Progression section
            javafx.scene.layout.VBox endlessContainer = new javafx.scene.layout.VBox(10);
            endlessContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10; -fx-padding: 20;");

            javafx.scene.control.Label endlessTitle = new javafx.scene.control.Label("Endless Mode Rules");
            endlessTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-alignment: center;");
            endlessTitle.setMaxWidth(Double.MAX_VALUE);
            javafx.scene.layout.HBox.setHgrow(endlessTitle, javafx.scene.layout.Priority.ALWAYS);

            // Level Progression subsection
            javafx.scene.control.Label levelProgTitle = new javafx.scene.control.Label("Level Progression");
            levelProgTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
            javafx.scene.control.Label levelProgText = new javafx.scene.control.Label(
                "• Level increases by 1 for every 10 lines cleared\n" +
                "• Starting level: 1, Maximum level: 15\n" +
                "• Examples: 0-9 lines = Level 1, 10-19 lines = Level 2, ..., 140+ lines = Level 15");
            levelProgText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            levelProgText.setWrapText(true);

            // Speed Progression subsection
            javafx.scene.control.Label speedTitle = new javafx.scene.control.Label("Speed Progression");
            speedTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
            javafx.scene.control.Label speedText = new javafx.scene.control.Label(
                "• Speed multiplier increases every 2 levels\n" +
                "• Starting speed: 1x, Maximum speed: 8x\n" +
                "• Examples: Level 1-2 = 1x, Level 3-4 = 2x, ..., Level 15 = 8x");
            speedText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            speedText.setWrapText(true);

            // Reset Leaderboard tip
            javafx.scene.control.Label endlessResetNote = new javafx.scene.control.Label(
                "Tip: After an Endless run, use the \"Reset Leaderboard\" button on the Game Over screen to clear your local high scores.");
            endlessResetNote.setStyle("-fx-font-size: 13px; -fx-text-fill: #C8C8C8;");
            endlessResetNote.setWrapText(true);

            endlessContainer.getChildren().addAll(endlessTitle, levelProgTitle, levelProgText, speedTitle, speedText, endlessResetNote);

            // Level Mode Rules section
            javafx.scene.layout.VBox levelModeContainer = new javafx.scene.layout.VBox(10);
            levelModeContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10; -fx-padding: 20;");

            javafx.scene.control.Label levelModeTitle = new javafx.scene.control.Label("Level Mode Rules");
            levelModeTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-alignment: center;");
            levelModeTitle.setMaxWidth(Double.MAX_VALUE);
            javafx.scene.layout.HBox.setHgrow(levelModeTitle, javafx.scene.layout.Priority.ALWAYS);

            // Objective subsection
            javafx.scene.control.Label levelObjectiveTitle = new javafx.scene.control.Label("Objective & Completion");
            levelObjectiveTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
            javafx.scene.control.Label levelObjectiveText = new javafx.scene.control.Label(
                "Each level has a target number of lines to clear within a time limit.\n" +
                "Complete the objective to unlock the next level and earn stars based on your performance.");
            levelObjectiveText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            levelObjectiveText.setWrapText(true);

            // Star Rating subsection
            javafx.scene.control.Label starTitle = new javafx.scene.control.Label("Star Rating System");
            starTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
            javafx.scene.control.Label starText = new javafx.scene.control.Label(
                "Earn up to 3 stars per level based on:\n" +
                "• Score achieved\n" +
                "• Lines cleared\n" +
                "• Completion time\n" +
                "Higher performance = more stars!");
            starText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            starText.setWrapText(true);

            // Difficulty & Themes subsection
            javafx.scene.control.Label difficultyTitle = new javafx.scene.control.Label("Difficulty & Themes");
            difficultyTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
            javafx.scene.control.Label difficultyText = new javafx.scene.control.Label(
                "Levels are organized by difficulty: Easy, Medium, Hard, Expert.\n" +
                "Each difficulty tier features unique visual themes that unlock as you progress.");
            difficultyText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            difficultyText.setWrapText(true);

            // Reset Progress tip
            javafx.scene.control.Label levelResetNote = new javafx.scene.control.Label(
                "Tip: To restart your Level Mode journey, use the \"Reset Progress\" button on the level selection screen (bottom-right corner).");
            levelResetNote.setStyle("-fx-font-size: 13px; -fx-text-fill: #C8C8C8;");
            levelResetNote.setWrapText(true);

            levelModeContainer.getChildren().addAll(levelModeTitle, levelObjectiveTitle, levelObjectiveText, starTitle, starText, difficultyTitle, difficultyText, levelResetNote);

            // Two-Player Mode Rules section
            javafx.scene.layout.VBox twoPlayerContainer = new javafx.scene.layout.VBox(12);
            twoPlayerContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10; -fx-padding: 20;");

            javafx.scene.control.Label twoPlayerTitle = new javafx.scene.control.Label("Two-Player Mode Rules");
            twoPlayerTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-alignment: center;");
            twoPlayerTitle.setMaxWidth(Double.MAX_VALUE);
            javafx.scene.layout.HBox.setHgrow(twoPlayerTitle, javafx.scene.layout.Priority.ALWAYS);

            // Objective and Winning Condition (combined)
            javafx.scene.control.Label objectiveTitle = new javafx.scene.control.Label("Objective & Winning Condition");
            objectiveTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
            javafx.scene.control.Label objectiveText = new javafx.scene.control.Label(
                "Clear lines to send garbage lines to your opponent. The last player standing wins!\n" +
                "The game ends when one player's board fills up. The player with the higher score wins!");
            objectiveText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            objectiveText.setWrapText(true);

            // Attack System
            javafx.scene.control.Label attackTitle = new javafx.scene.control.Label("Attack System");
            attackTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
            javafx.scene.control.Label attackText = new javafx.scene.control.Label(
                "• 1 line cleared: No attack (0 garbage lines)\n" +
                "• 2 lines cleared: Send 1 garbage line\n" +
                "• 3 lines cleared: Send 2 garbage lines\n" +
                "• 4 lines cleared (Tetris): Send 4 garbage lines");
            attackText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            attackText.setWrapText(true);

            // Combo Bonus
            javafx.scene.control.Label comboTitle = new javafx.scene.control.Label("Combo Bonus");
            comboTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
            javafx.scene.control.Label comboText = new javafx.scene.control.Label(
                "Build combos by clearing lines consecutively. Each combo above 1 eliminates 2 garbage lines from your board!\n" +
                "Example: Combo x3 = Eliminates 4 garbage lines (2 per combo above 1)");
            comboText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            comboText.setWrapText(true);

            // Garbage Lines
            javafx.scene.control.Label garbageTitle = new javafx.scene.control.Label("Garbage Lines");
            garbageTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
            javafx.scene.control.Label garbageText = new javafx.scene.control.Label("Garbage lines appear as gray blocks with one random hole. Clear them quickly or they'll stack up!");
            garbageText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            garbageText.setWrapText(true);

            // Special Features
            javafx.scene.control.Label featuresTitle = new javafx.scene.control.Label("Special Features");
            featuresTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
            javafx.scene.control.Label featuresText = new javafx.scene.control.Label(
                "• Countdown timer before game starts (3-2-1)\n" +
                "• Visual attack animations when receiving attacks\n" +
                "• Real-time statistics tracking (combo, attacks, defense)\n" +
                "• Sound effects for attacks and line clears");
            featuresText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            featuresText.setWrapText(true);

            // Strategy Tips
            javafx.scene.control.Label strategyTitle = new javafx.scene.control.Label("Strategy Tips");
            strategyTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
            javafx.scene.control.Label strategyText = new javafx.scene.control.Label(
                "• Build for Tetris (4-line clears) for maximum damage\n" +
                "• Maintain combos to clear incoming garbage lines\n" +
                "• Watch your opponent's board and adapt your strategy\n" +
                "• Use hold to save pieces for better setups");
            strategyText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            strategyText.setWrapText(true);

            twoPlayerContainer.getChildren().addAll(
                twoPlayerTitle,
                objectiveTitle, objectiveText,
                attackTitle, attackText,
                comboTitle, comboText,
                garbageTitle, garbageText,
                featuresTitle, featuresText,
                strategyTitle, strategyText
            );

            // Close button
            javafx.scene.control.Button closeButton = new javafx.scene.control.Button("Close");
            closeButton.setStyle("-fx-background-color: #4DFFFF; -fx-text-fill: #1A0033; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;");
            closeButton.setOnAction(e -> {
                // Play button click sound
                SoundManager.getInstance().playButtonClickSound();
                helpStage.close();
            });
            // Prevent initial focus from jumping to the bottom button
            closeButton.setFocusTraversable(false);
            
            // Also handle the window's X (close) button
            helpStage.setOnCloseRequest(e -> {
                // Play button click sound
                SoundManager.getInstance().playButtonClickSound();
            });
            
            // Create HBox for right-aligned close button
            javafx.scene.layout.HBox buttonContainer = new javafx.scene.layout.HBox();
            buttonContainer.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            buttonContainer.getChildren().add(closeButton);
            
            // Add all components
            mainContainer.getChildren().addAll(titleLabel, modesContainer, basicsDual, rngContainer, scoreContainer, ghostContainer, endlessContainer, levelModeContainer, twoPlayerContainer, buttonContainer);
            scrollPane.setContent(mainContainer);
            
            // Create scene and show
            javafx.scene.Scene helpScene = new javafx.scene.Scene(scrollPane, 720, 560);
            // Load settings.css for shared styles including scrollbar
            helpScene.getStylesheets().add(
                getClass().getResource("/settings.css").toExternalForm()
            );
            helpStage.setScene(helpScene);
            helpStage.show();
            
        } catch (Exception e) {
            System.err.println("Error showing help dialog: " + e.getMessage());
            e.printStackTrace();
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
    private void centerWindowOnScreen(Stage stage, double width, double height) {
        // Use centerOnScreen which automatically centers on the current screen
        // This respects the user's current display setup
        stage.centerOnScreen();
    }
    
    // Helper: build a bullet column with consistent wrapping and spacing
    private javafx.scene.layout.VBox createBulletedColumn(String[] items, double width) {
        javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(6);
        for (String text : items) {
            javafx.scene.control.Label lbl = new javafx.scene.control.Label("• " + text);
            lbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            lbl.setWrapText(true);
            lbl.setPrefWidth(width);
            box.getChildren().add(lbl);
        }
        return box;
    }
}
