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
    private Button twoPlayerVSBtn;
    
    @FXML
    private Button twoPlayerAIBtn;

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
        twoPlayerVSBtn.setText("Two Player VS");
        twoPlayerAIBtn.setText("Two Player AI");
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
     * Starts the Two Player VS Mode game.
     * Creates separate game services for both players and launches VS mode.
     */
    @FXML
    private void startTwoPlayerVS() {
        System.out.println("Starting Two Player VS Mode...");
        try {
            // Create GUI controller
            GuiController guiController = new GuiController();
            
            // Create VS mode (it will create its own game services)
            var gameMode = GameModeFactory.createGameMode(GameModeType.TWO_PLAYER_VS, null, guiController);
            gameMode.initialize();
            
            // Load game layout
            loadGameScene(guiController);
            
        } catch (Exception e) {
            System.err.println("Error starting Two Player VS Mode: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Starts the Two Player AI Mode game.
     * Creates a game service and launches AI mode.
     */
    @FXML
    private void startTwoPlayerAI() {
        System.out.println("Starting Two Player AI Mode...");
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
            System.err.println("Error starting Two Player AI Mode: " + e.getMessage());
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
}
