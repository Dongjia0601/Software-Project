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
    private Button aiModeBtn;
    
    @FXML
    private Button twoPlayerModeBtn;
    
    @FXML
    private Button settingsBtn;
    
    @FXML
    private Button helpBtn;

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
        aiModeBtn.setText("AI Mode");
        twoPlayerModeBtn.setText("Two-Player Mode");
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
        System.out.println("Starting Endless Mode...");
        try {
            // Create game service and GUI controller
            GameService gameService = new GameServiceImpl();
            GuiController guiController = new GuiController();
            
            // Create endless mode using factory pattern
            var gameMode = GameModeFactory.createGameMode(GameModeType.ENDLESS, gameService, guiController);
            gameMode.initialize();
            
            // Set Endless Mode flag in GUI controller
            guiController.setEndlessMode(true);
            
            // Transition to game scene
            loadGameScene(guiController);
            
        } catch (Exception e) {
            System.err.println("Error starting Endless Mode: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Starts the Level Mode game.
     * Creates a new game service and launches the level mode.
     */
    @FXML
    private void startLevelMode() {
        System.out.println("Starting Level Mode...");
        try {
            // Create game service and GUI controller
            GameService gameService = new GameServiceImpl();
            GuiController guiController = new GuiController();
            
            // Create level mode
            var gameMode = GameModeFactory.createGameMode(GameModeType.LEVEL, gameService, guiController);
            gameMode.initialize();
            
            // Load game layout
            loadGameScene(guiController);
            
        } catch (Exception e) {
            System.err.println("Error starting Level Mode: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Starts the Two-Player Mode game.
     * Creates separate game services for both players and launches VS mode.
     */
    @FXML
    private void startTwoPlayerMode() {
        System.out.println("Starting Two-Player Mode...");
        try {
            // Create GUI controller
            GuiController guiController = new GuiController();
            
            // Create VS mode (it will create its own game services)
            var gameMode = GameModeFactory.createGameMode(GameModeType.TWO_PLAYER_VS, null, guiController);
            gameMode.initialize();
            
            // Load game layout
            loadGameScene(guiController);
            
        } catch (Exception e) {
            System.err.println("Error starting Two-Player Mode: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Starts the AI Mode game.
     * Creates a game service and launches AI mode.
     */
    @FXML
    private void startAIMode() {
        System.out.println("Starting AI Mode...");
        try {
            // Create game service and GUI controller
            GameService gameService = new GameServiceImpl();
            GuiController guiController = new GuiController();
            
            // Create AI mode
            var gameMode = GameModeFactory.createGameMode(GameModeType.TWO_PLAYER_AI, gameService, guiController);
            gameMode.initialize();
            
            // Load game layout
            loadGameScene(guiController);
            
        } catch (Exception e) {
            System.err.println("Error starting AI Mode: " + e.getMessage());
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
        
        // Initialize the game controller
        new GameController(gameController);
        
        System.out.println("Game scene loaded successfully");
    }
    
    /**
     * Handles the settings button click.
     * Opens the settings page.
     */
    @FXML
    private void openSettings() {
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
            
            System.out.println("Settings page loaded successfully");
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
        System.out.println("Help dialog requested from main menu");
        
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
                {"AI Mode", "Play against an intelligent AI opponent and test your skills."},
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
                "Place falling tetrominoes to complete rows.",
                "Clear full horizontal lines to earn points.",
                "Clear multiple lines at once for higher points.",
                "Pieces fall faster as you clear more lines.",
                "Topping out (stack reaches top) ends the game."
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
                "Controls: Settings, Help, Exit to Menu.",
                "Actions: New Game (N), Pause & Resume (P), Mute."
            };
            javafx.scene.layout.VBox basicsRightCol = createBulletedColumn(basicsRightItems, 330);
            basicsRightBox.getChildren().addAll(basicsRightTitle, basicsRightCol);

            basicsDual.getChildren().addAll(basicsLeftBox, basicsRightBox);
            
            // Piece Randomizer help section
            javafx.scene.layout.VBox rngContainer = new javafx.scene.layout.VBox(10);
            rngContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10; -fx-padding: 20;");

            javafx.scene.control.Label rngTitle = new javafx.scene.control.Label("Piece Randomizer Systems");
            rngTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

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

            javafx.scene.control.Label scoreTitle = new javafx.scene.control.Label("Scoring (Single Player)");
            scoreTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

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

            // Close button
            javafx.scene.control.Button closeButton = new javafx.scene.control.Button("Close");
            closeButton.setStyle("-fx-background-color: #4DFFFF; -fx-text-fill: #1A0033; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;");
            closeButton.setOnAction(e -> helpStage.close());
            
            // Create HBox for right-aligned close button
            javafx.scene.layout.HBox buttonContainer = new javafx.scene.layout.HBox();
            buttonContainer.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            buttonContainer.getChildren().add(closeButton);
            
            // Add all components
            mainContainer.getChildren().addAll(titleLabel, modesContainer, basicsDual, rngContainer, scoreContainer, buttonContainer);
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
