package com.comp2042.controller.game;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.geometry.Pos;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.Parent;
import javafx.util.Duration;
import javafx.application.Platform;

import com.comp2042.config.GameSettings;
import com.comp2042.view.manager.*;
import com.comp2042.view.panel.NotificationPanel;
import com.comp2042.view.panel.GameOverPanel;
import com.comp2042.controller.factory.*;
import com.comp2042.controller.menu.EndlessGameOverController;
import com.comp2042.controller.menu.LevelGameOverController;
import com.comp2042.view.panel.TwoPlayerGameOverPanel;
import com.comp2042.model.board.*;
import com.comp2042.model.mode.*;
import com.comp2042.service.audio.*;
import com.comp2042.dto.*;
import com.comp2042.event.*;
import com.comp2042.event.listener.InputEventListener;
import com.comp2042.util.MatrixOperations;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the JavaFX GUI components.
 * 
 * <p><strong>REFACTORED (Phase 3):</strong> This class has been refactored to adhere to the 
 * Single Responsibility Principle (SRP). It now acts as a coordinator/facade that delegates
 * responsibilities to specialized sub-controllers:</p>
 * <ul>
 *   <li>{@link GameBoardRenderer} - Handles game board background rendering</li>
 *   <li>{@link BrickRenderer} - Handles brick rendering (current, ghost, hold, next)</li>
 *   <li>{@link AnimationController} - Manages all animations and visual effects</li>
 *   <li>{@link GameInputHandler} - Processes keyboard input</li>
 *   <li>{@link HudManager} - Updates HUD elements (labels, stats)</li>
 *   <li>{@link TwoPlayerPanelManager} - Coordinates two-player specific panels</li>
 *   <li>{@link CountdownManager} - Handles pre-game countdown overlays</li>
 *   <li>{@link AudioVolumeManager} - Synchronises mute state and audio UI</li>
 * </ul>
 * 
 * <p>Key responsibilities (now delegated):</p>
 * <ul>
 *   <li>Coordinate sub-controllers and manage their lifecycle</li>
 *   <li>Provide facade methods for backward compatibility</li>
 *   <li>Initialize and wire JavaFX UI components</li>
 * </ul>
 * 
 * <p><strong>Design Patterns:</strong></p>
 * <ul>
 *   <li>Facade Pattern - Simplifies interaction with subsystems</li>
 *   <li>Mediator Pattern - Coordinates communication between sub-controllers</li>
 * </ul>
 * 
 * @author Original code + Dong, Jia (Phase 3 Refactoring)
 * @version Phase 3 - SRP Refactoring
 */
public class GameViewController implements Initializable, GameInputHandler.InputHandlerCallbacks {

    // Extracted constants for better readability and maintainability
    private static final int BRICK_SIZE = 25; // Size of a single brick cell in pixels (enlarged for better visibility)
    private static final int TIMELINE_DURATION_MS = 400; // Duration for automatic brick drop (milliseconds)
    private static final int DEFAULT_GRID_GAP = 1; // Fallback gap when GridPane spacing not yet initialised
    private static final int VISIBLE_ROW_OFFSET = 0; // Number of hidden rows above the visible board

    @FXML
    private BorderPane rootPane; // Root BorderPane container
    
    @FXML
    private GridPane gamePanel; // GridPane for the main game board display

    @FXML
    private Group groupNotification; // Group to hold notification panels (e.g., score bonuses)

    @FXML
    private Pane brickPanel; // Pane for the currently falling brick display

    @FXML
    private Pane ghostPanel; // Pane for the ghost piece display

    @FXML
    private GameOverPanel gameOverPanel; // Panel displayed when the game ends
    
    
    // Track current game mode
    private boolean isEndlessMode = false;
    private long gameStartTime = 0;
    
    // EndlessMode UI components
    @FXML
    private Label scoreLabel;
    
    @FXML
    private Label highScoreLabel;
    
    @FXML
    private VBox bestStatsBox;
    
    @FXML
    private Label bestScoreLabel;
    
    @FXML
    private Label bestTimeLabel;
    
    @FXML
    private Label linesLabel;
    
    @FXML
    private Label levelLabel;
    
    @FXML
    private Label speedLabel;
    
    @FXML
    private Label timeLabel;
    
    @FXML
    private Label gameTitleLabel;
    
    @FXML
    private VBox statisticsBox;
    
    @FXML
    private GridPane holdPanel;
    
    @FXML
    private GridPane nextBrickPanel;
    
    @FXML
    private javafx.scene.control.Button muteButton;

    @FXML
    private Button backToSelectionButton;
    @FXML
    private VBox leftObjectiveBox;
    @FXML
    private Label leftTimerLabel;
    @FXML
    private Label leftProgressLabel;
    @FXML
    private Label leftSpeedLabel;
    @FXML
    private HBox leftStarDisplay;

    // Level Mode state
    private int levelTimeLimitSeconds = 0;
    private int levelTimeRemainingSeconds = 0;
    private Timeline levelTimer;
    private boolean isLevelMode = false;
    private long levelStartTime = 0;
    
    /**
     * Gets the level start time.
     * @return the level start time in milliseconds
     */
    public long getLevelStartTime() {
        return levelStartTime;
    }

    private Rectangle[][] displayMatrix; // Array of rectangles representing the static board background
    private int[][] cachedBoardMatrix; // Cached board matrix for incremental rendering optimization
    private Rectangle[][] holdDisplayMatrix; // Array of rectangles for hold display
    private Rectangle[][] nextDisplayMatrix; // Array of rectangles for next piece display

    private InputEventListener eventListener; // Listener for game events (likely GameController)

    private Rectangle[][] rectangles; // Array of rectangles representing the current falling brick
    private Rectangle[][] ghostRectangles; // Array of rectangles representing the ghost brick

    private Timeline timeLine; // Timeline for the automatic downward movement of the brick
    private Timeline timeTimer; // Timer to update elapsed time in Endless Mode
    
    // EndlessMode specific fields
    private int currentDropSpeed = 400; // Default speed in milliseconds
    private double currentBrickOpacity = 1.0;
    private boolean ghostEnabled = true; // Whether ghost piece should be displayed

    // ==================== Sub-Controllers (Phase 3 Refactoring) ====================
    private GameBoardRenderer boardRenderer; // Manages game board background rendering
    private BrickRenderer brickRenderer; // Manages brick rendering (current, ghost, hold, next)
    private AnimationController animationController; // Manages animations and effects
    private GameInputHandler inputHandler; // Handles keyboard input
    private HudManager uiManager; // Manages UI component updates
    private GameModeUIManager modeUIManager; // Manages game mode-specific UI (Phase 3+)
    private DialogManager dialogManager; // Manages dialogs and navigation (Phase 3+)
    private TwoPlayerPanelManager twoPlayerPanelManager; // Manages two-player specific UI
    
    /**
     * Calculates the absolute X position for the specified column within a given grid.
     *
     * @param grid   the GridPane representing the board (may be null before initialization)
     * @param matrix the matrix of rectangles corresponding to the grid
     * @param column the zero-based column index
     * @return the computed X coordinate in pixels
     */
    private double calculateGridX(GridPane grid, Rectangle[][] matrix, int column) {
        GridMetrics metrics = measureGrid(grid, matrix);
        return metrics.originX + column * metrics.cellWidth;
    }

    /**
     * Calculates the absolute Y position for the specified row within a given grid.
     *
     * @param grid the GridPane representing the board (may be null before initialization)
     * @param matrix the matrix of rectangles corresponding to the grid
     * @param row  the zero-based row index
     * @return the computed Y coordinate in pixels
     */
    private double calculateGridY(GridPane grid, Rectangle[][] matrix, int row) {
        GridMetrics metrics = measureGrid(grid, matrix);
        return metrics.originY + row * metrics.cellHeight;
    }

    /**
     * Measures grid origin and cell dimensions using the provided rectangle matrix.
     * Falls back to configured brick size and gaps when layout bounds are unavailable.
     *
     * @param grid the GridPane associated with the matrix
     * @param matrix the rectangles composing the static board background for the grid
     * @return GridMetrics containing origin coordinates and cell dimensions
     */
    private GridMetrics measureGrid(GridPane grid, Rectangle[][] matrix) {
        double gapX = DEFAULT_GRID_GAP;
        double gapY = DEFAULT_GRID_GAP;
        double originX = 0;
        double originY = 0;

        if (grid != null) {
            gapX = grid.getHgap() > 0 ? grid.getHgap() : DEFAULT_GRID_GAP;
            gapY = grid.getVgap() > 0 ? grid.getVgap() : DEFAULT_GRID_GAP;
            originX = grid.getLayoutX();
            originY = grid.getLayoutY();
            grid.applyCss();
            grid.layout();
        }

        double cellWidth = BRICK_SIZE + gapX;
        double cellHeight = BRICK_SIZE + gapY;

        if (matrix != null && matrix.length > 0 && matrix[0].length > 0) {
            Rectangle cell00 = matrix[0][0];
            if (cell00 != null) {
                Bounds bounds00 = cell00.getBoundsInParent();
                double minX00 = bounds00.getMinX();
                double minY00 = bounds00.getMinY();
                originX = (grid != null ? grid.getLayoutX() : 0) + minX00;
                originY = (grid != null ? grid.getLayoutY() : 0) + minY00;

                if (matrix[0].length > 1 && matrix[0][1] != null) {
                    Bounds bounds01 = matrix[0][1].getBoundsInParent();
                    cellWidth = bounds01.getMinX() - minX00;
                }
                if (matrix.length > 1 && matrix[1][0] != null) {
                    Bounds bounds10 = matrix[1][0].getBoundsInParent();
                    cellHeight = bounds10.getMinY() - minY00;
                }
            }
        }

        return new GridMetrics(originX, originY, cellWidth, cellHeight);
    }

    /**
     * Holds grid measurement data for alignment calculations.
     */
    private static final class GridMetrics {
        final double originX;
        final double originY;
        final double cellWidth;
        final double cellHeight;

        GridMetrics(double originX, double originY, double cellWidth, double cellHeight) {
            this.originX = originX;
            this.originY = originY;
            this.cellWidth = cellWidth;
            this.cellHeight = cellHeight;
        }
    }

    private final BooleanProperty isPause = new SimpleBooleanProperty(); // Property indicating if the game is paused

    private final BooleanProperty isGameOver = new SimpleBooleanProperty(); // Property indicating if the game is over
    
    // Game mode tracking
    private boolean isTwoPlayerMode = false; // Track if we're in two-player mode
    
    // Settings reference
    private GameSettings settings;
    
    // Store previous volume before muting
    private AudioVolumeManager audioVolumeManager;
    
    // Time tracking (exclude paused duration)
    private long totalPausedMillis = 0L;
    private long lastPauseStartMillis = 0L;
    
    // Endless mode progression tracking
    private int endlessLevel = 1;
    private int endlessLinesClearedUI = 0;
    
    // Countdown tracking
    private CountdownManager countdownManager;

    @Override
    /**
     * Initializes the GUI components, sets up keyboard input handling,
     * and applies initial styling.
     * This method is called after the FXML file has been loaded.
     *
     * <p>This method performs the following operations:</p>
     * <ul>
     *   <li>Loads the digital font for UI elements</li>
     *   <li>Sets up keyboard event handling for game controls</li>
     *   <li>Initializes the game over panel visibility</li>
     *   <li>Applies visual effects like reflection</li>
     * </ul>
     *
     * @param location The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources The resources used to localize the root object, or null if the root object was not localized.
     */
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize settings
        settings = GameSettings.getInstance();

        // Initialize audio volume manager
        audioVolumeManager = new AudioVolumeManager(settings, SoundManager.getInstance());
        audioVolumeManager.bindMuteButton(muteButton);
        
        // Load the digital font for UI elements
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);

        // Set focus and request focus for keyboard input
        // For two-player mode, use rootPane; for single-player, use gamePanel
        if (isTwoPlayerMode && rootPane != null) {
            rootPane.setFocusTraversable(true);
            rootPane.requestFocus();
            rootPane.setOnKeyPressed(this::handleKeyPressEvent);
        } else if (gamePanel != null) {
            gamePanel.setFocusTraversable(true);
            gamePanel.requestFocus();
            gamePanel.setOnKeyPressed(this::handleKeyPressEvent);
        }

        // Initialize game over panel visibility
        if (gameOverPanel != null) {
            gameOverPanel.setVisible(false);
        }
        if (gameOverPanel1 != null) {
            gameOverPanel1.setVisible(false);
        }
        if (gameOverPanel2 != null) {
            gameOverPanel2.setVisible(false);
        }
        
        // Apply visual effects
        final DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(8.0);
        dropShadow.setOffsetX(0.0);
        dropShadow.setOffsetY(4.0);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.3));
        
        // CRITICAL FIX: Disable Space key for all control buttons to prevent accidental activation
        // This prevents Space key from triggering buttons when they have focus
        // Use Platform.runLater to ensure scene is loaded before setting up filters
        javafx.application.Platform.runLater(() -> {
            disableSpaceKeyForControlButtons();
        });
        
        // ==================== Initialize Sub-Controllers (Phase 3 Refactoring) ====================
        initializeSubControllers();
    }
    
    /**
     * Initializes all sub-controllers and wires them together.
     * This method is part of the Phase 3 refactoring to implement SRP.
     * 
     * <p><strong>Phase 3+ Update:</strong> Now includes GameModeUIManager and DialogManager</p>
     */
    private void initializeSubControllers() {
        // 1. Initialize AnimationController
        animationController = new AnimationController(groupNotification, rootPane);
        
        // 2. Initialize GameBoardRenderer
        boardRenderer = new GameBoardRenderer(gamePanel);
        
        // 3. Initialize BrickRenderer
        brickRenderer = new BrickRenderer(brickPanel, ghostPanel, holdPanel, nextBrickPanel, boardRenderer);
        
        // 4. Initialize GameInputHandler
        inputHandler = new GameInputHandler(isPause, isGameOver);
        inputHandler.setCallbacks(this); // GameViewController implements InputHandlerCallbacks
        inputHandler.setEventListener(eventListener);
        inputHandler.setTwoPlayerMode(isTwoPlayerMode);
        
        // 5. Initialize HudManager (previously GameUIManager)
        uiManager = new HudManager();
        uiManager.setScoreLabel(scoreLabel);
        uiManager.setHighScoreLabel(highScoreLabel);
        uiManager.setLinesLabel(linesLabel);
        uiManager.setLevelLabel(levelLabel);
        uiManager.setSpeedLabel(speedLabel);
        uiManager.setTimeLabel(timeLabel);
        uiManager.setGameTitleLabel(gameTitleLabel);
        uiManager.setLeftTimerLabel(leftTimerLabel);
        uiManager.setLeftProgressLabel(leftProgressLabel);
        uiManager.setLeftSpeedLabel(leftSpeedLabel);
        uiManager.setLeftStarDisplay(leftStarDisplay);
        uiManager.setBestScoreLabel(bestScoreLabel);
        uiManager.setBestTimeLabel(bestTimeLabel);
        uiManager.setBestStatsBox(bestStatsBox);
        
        // 6. Initialize GameModeUIManager (Phase 3+)
        modeUIManager = new GameModeUIManager();
        modeUIManager.setLeftObjectiveBox(leftObjectiveBox);
        modeUIManager.setStatisticsBox(statisticsBox);
        modeUIManager.setBestStatsBox(bestStatsBox);
        modeUIManager.setHoldPanel(holdPanel);
        modeUIManager.setNextBrickPanel(nextBrickPanel);
        
        // 7. Initialize DialogManager (Phase 3+)
        dialogManager = new DialogManager(rootPane, gamePanel);
        dialogManager.setCallbacks(new DialogManager.DialogCallbacks() {
            @Override
            public void onSettingsChanged() {
                // Reload settings after closing settings dialog
                settings = com.comp2042.config.GameSettings.getInstance();
            }
            
            @Override
            public void onReturnToMenu(javafx.stage.Stage stage) {
                loadMainMenu(stage);
            }
            
            @Override
            public void onReturnToLevelSelection(javafx.stage.Stage stage) {
                loadLevelSelection(stage);
            }
        });

        // 8. Initialize TwoPlayerPanelManager when two-player layout is present
        if (isTwoPlayerMode) {
            twoPlayerPanelManager = new TwoPlayerPanelManager(
                BRICK_SIZE,
                DEFAULT_GRID_GAP,
                rootPane,
                gamePanel1,
                gamePanel2,
                brickPanel1,
                brickPanel2,
                ghostPanel1,
                ghostPanel2,
                holdPanel1,
                holdPanel2,
                nextBrickPanel1,
                nextBrickPanel2,
                groupNotification1,
                groupNotification2,
                gameOverPanel1,
                gameOverPanel2,
                twoPlayerGameOverPanel,
                boardBackground1,
                boardBackground2,
                player1ScoreLabel,
                player1LinesLabel,
                player1ComboLabel,
                player1AttackLabel,
                player1DefenseLabel,
                player1TetrisLabel,
                player1TimeLabel,
                player2ScoreLabel,
                player2LinesLabel,
                player2ComboLabel,
                player2AttackLabel,
                player2DefenseLabel,
                player2TetrisLabel,
                player2TimeLabel,
                isPause,
                this::shouldShowGhostBrick
            );
        } else {
            twoPlayerPanelManager = null;
        }

        countdownManager = new CountdownManager(SoundManager.getInstance());
    }
    
    /**
     * Loads the main menu scene.
     * 
     * @param stage the current stage
     */
    private void loadMainMenu(javafx.stage.Stage stage) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/MainMenu.fxml"));
            javafx.scene.Parent mainMenuRoot = loader.load();
            com.comp2042.controller.menu.MainMenuController mainMenuController = loader.getController();
            
            javafx.scene.Scene mainMenuScene = new javafx.scene.Scene(mainMenuRoot, 800, 600);
            stage.setScene(mainMenuScene);
            
            // Initialize the main menu controller with the stage
            if (mainMenuController != null) {
                // MainMenuController should handle its own stage setup via FXML
                // No need to call initialize - it's called automatically by FXMLLoader
            }
        } catch (Exception e) {
            System.err.println("Error loading main menu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Loads the level selection scene.
     * 
     * @param stage the current stage
     */
    private void loadLevelSelection(javafx.stage.Stage stage) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/LevelSelection.fxml"));
            javafx.scene.Parent levelSelectionRoot = loader.load();
            com.comp2042.controller.menu.LevelSelectionController controller = loader.getController();
            controller.setStage(stage);
            
            javafx.scene.Scene levelSelectionScene = new javafx.scene.Scene(levelSelectionRoot);
            levelSelectionScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            stage.setScene(levelSelectionScene);
        } catch (Exception e) {
            System.err.println("Error loading level selection: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Disables Space key for all control buttons to prevent accidental activation.
     * CRITICAL FIX: Uses addEventFilter for CAPTURE phase to intercept events BEFORE button processes them.
     */
    private void disableSpaceKeyForControlButtons() {
        if (rootPane != null) {
            addSpaceKeyFilterToButtons(rootPane);
        }
    }
    
    /**
     * Recursively finds all control buttons and adds Space key event filters.
     */
    private void addSpaceKeyFilterToButtons(javafx.scene.Node node) {
        if (node instanceof Button) {
            Button button = (Button) node;
            String buttonText = button.getText();
            // Only filter control buttons (Settings, Help, Back to Menu)
            if (buttonText != null && (
                buttonText.contains("Settings") ||
                buttonText.contains("Help") ||
                buttonText.contains("Back to Menu")
            )) {
                button.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.SPACE) {
                        event.consume();
                    }
                });
            }
        }
        
        // Recursively process children
        if (node instanceof javafx.scene.Parent) {
            for (javafx.scene.Node child : ((javafx.scene.Parent) node).getChildrenUnmodifiable()) {
                addSpaceKeyFilterToButtons(child);
            }
        }
    }

    // ==================== InputHandlerCallbacks Implementation (Phase 3 Refactoring) ====================
    
    @Override
    public void onRefreshBrick(ViewData viewData) {
        refreshBrick(viewData);
    }
    
    @Override
    public void onMoveDown(MoveEvent moveEvent) {
        moveDown(moveEvent);
    }
    
    @Override
    public void onUpdateHoldDisplay(int[][] holdBrickData) {
        if (brickRenderer != null) {
            brickRenderer.updateHoldDisplay(holdBrickData);
        } else {
            updateHoldDisplay(holdBrickData);
        }
    }
    
    @Override
    public void onPauseRequested() {
        // Handle pause for both GameController and TwoPlayerGameController
        if (eventListener instanceof GameController) {
            ((GameController) eventListener).requestPause();
        } else if (eventListener instanceof TwoPlayerGameController) {
            ((TwoPlayerGameController) eventListener).requestPause();
        }
    }
    
    @Override
    public void onNewGameRequested() {
        newGame(null);
    }
    
    @Override
    public void onMuteToggleRequested() {
        toggleMute();
    }
    
    @Override
    public void onRefreshPlayer1Brick(ViewData viewData) {
        refreshPlayer1Brick(viewData);
    }
    
    @Override
    public void onRefreshPlayer2Brick(ViewData viewData) {
        refreshPlayer2Brick(viewData);
    }
    
    @Override
    public void onHandlePlayer1Down(DownData downData) {
        handlePlayer1DownEvent(downData);
    }
    
    @Override
    public void onHandlePlayer2Down(DownData downData) {
        handlePlayer2DownEvent(downData);
    }
    
    /**
     * Handles keyboard press events for game controls.
     * Processes movement, rotation, pause, and new game requests.
     * This method is public to allow external components to set up keyboard handlers.
     *
     * @param keyEvent The KeyEvent containing information about the key press.
     */
    public void handleKeyPressEvent(KeyEvent keyEvent) {
        // Check for pause state before handling movement/rotation
        if (!isPause.getValue() && !isGameOver.getValue()) {
            
            if (isTwoPlayerMode) {
                // === Two-Player Mode Controls ===
                handleTwoPlayerControls(keyEvent);
            } else {
                // === Single-Player Mode Controls (Endless/Level Mode) ===
                handleSinglePlayerControls(keyEvent);
            }
        }

        // Add key handler for pause/unpause (P key)
        if (keyEvent.getCode() == KeyCode.P) {
            // Handle pause for both GameController and TwoPlayerGameController
            if (eventListener instanceof GameController) {
                ((GameController) eventListener).requestPause();
            } else if (eventListener instanceof TwoPlayerGameController) {
                ((TwoPlayerGameController) eventListener).requestPause();
            }
            keyEvent.consume();
        }

        // Add key handler for new game (N key)
        if (keyEvent.getCode() == KeyCode.N) {
            newGame(null);
            keyEvent.consume();
        }

        // Toggle mute with M key
        if (keyEvent.getCode() == KeyCode.M) {
            toggleMute();
            keyEvent.consume();
        }
    }

    /**
     * Initializes the game view by setting up the visual representation of the board
     * and the initial falling brick based on the provided board matrix and view data.
     *
     * @param boardMatrix The initial state of the game board matrix.
     * @param brick       The initial view data for the falling brick (shape, position).
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        // Background music is already playing from main menu, no need to restart
        // Ensure the grid pane renders its grid lines and keeps the correct style
        if (gamePanel != null) {
            gamePanel.setGridLinesVisible(true);
            if (!gamePanel.getStyleClass().contains("game-grid")) {
                gamePanel.getStyleClass().add("game-grid");
            }
        }
        // Initialize the display matrix for the static board background
        // Render all 20 rows (10 columns × 20 rows)
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        // Initialize cache for incremental rendering optimization
        cachedBoardMatrix = new int[boardMatrix.length][boardMatrix[0].length];
        for (int i = 0; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.getStyleClass().add("game-cell");
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i);
                cachedBoardMatrix[i][j] = boardMatrix[i][j]; // Initialize cache
            }
        }

        // Initialize the rectangles for the current falling brick
        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                // Set position for each rectangle within the brick panel
                rectangle.setLayoutX(j * (BRICK_SIZE + 1)); // +1 for gap
                rectangle.setLayoutY(i * (BRICK_SIZE + 1)); // +1 for gap
                rectangles[i][j] = rectangle;
                brickPanel.getChildren().add(rectangle);
            }
        }
        
        // Initialize the rectangles for the ghost brick
        if (ghostPanel != null) {
            ghostPanel.getChildren().clear();
            ghostRectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                    rectangle.setFill(Color.TRANSPARENT);
                    // Enhanced ghost brick appearance: rounded corners and semi-transparent border
                    rectangle.setArcHeight(9); // Rounded corners to match regular bricks
                    rectangle.setArcWidth(9);
                    rectangle.setOpacity(1.0); // Full opacity for better visibility
                    // Set position for each rectangle within the ghost panel
                    rectangle.setLayoutX(j * (BRICK_SIZE + 1)); // +1 for gap
                    rectangle.setLayoutY(i * (BRICK_SIZE + 1)); // +1 for gap
                    ghostRectangles[i][j] = rectangle;
                    ghostPanel.getChildren().add(rectangle);
                }
            }
        }

        brickPanel.setLayoutX(calculateGridX(gamePanel, displayMatrix, brick.getxPosition()));
        brickPanel.setLayoutY(calculateGridY(gamePanel, displayMatrix, brick.getyPosition()));
        
        // Update ghost brick position
        updateGhostBrick(brick);

        // Initialize the timeline for automatic brick movement
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(TIMELINE_DURATION_MS),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();

        // Start time tracking for Endless Mode
        if (isEndlessMode) {
            if (gameStartTime == 0) {
                gameStartTime = System.currentTimeMillis();
            }
            startTimeTimer();
            // Ensure initial Endless speed/labels are applied after timeline exists
            if (endlessLevel <= 0) {
                endlessLevel = 1;
            }
            updateLevel(endlessLevel);
            updateSpeed(getSpeedDisplayForLevel(endlessLevel));
            updateGameSpeed(getDropMsForLevel(endlessLevel));
        }
    }

    /**
     * Maps integer values from the brick/board matrix to JavaFX Paint objects (colors).
     *
     * @param i The integer value representing the brick type or state.
     * @return The corresponding Paint object (color).
     */
    private Paint getFillColor(int i) {
        Paint returnPaint;
        switch (i) {
            case 0:
                returnPaint = Color.TRANSPARENT;
                break;
            case 1:
                returnPaint = Color.AQUA;
                break;
            case 2:
                returnPaint = Color.BLUEVIOLET;
                break;
            case 3:
                returnPaint = Color.DARKGREEN;
                break;
            case 4:
                returnPaint = Color.YELLOW;
                break;
            case 5:
                returnPaint = Color.RED;
                break;
            case 6:
                returnPaint = Color.BEIGE;
                break;
            case 7:
                returnPaint = Color.BURLYWOOD;
                break;
            case 8:
                returnPaint = Color.GRAY;
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
    }

    /**
     * Updates the visual representation of the currently falling brick based on its new position and shape.
     *
     * @param brick The ViewData containing the new shape and position of the brick.
     */
    private void refreshBrick(ViewData brick) {
        if (!isPause.getValue()) {
            brickPanel.setLayoutX(calculateGridX(gamePanel, displayMatrix, brick.getxPosition()));
            brickPanel.setLayoutY(calculateGridY(gamePanel, displayMatrix, brick.getyPosition()));
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
            
            // Update next piece display
            if (brick.getNextBrickData() != null) {
                updateNextDisplay(brick.getNextBrickData());
            }
            
            // Update ghost brick position
            updateGhostBrick(brick);
        }
    }
    
    /**
     * Updates the ghost brick display based on the current brick position and shape.
     * The ghost brick shows where the current brick would land if dropped straight down.
     *
     * @param brick The ViewData containing the current brick shape and ghost position.
     */
    private void updateGhostBrick(ViewData brick) {
        if (ghostPanel == null || ghostRectangles == null) {
            return;
        }
        
        // Check if ghost brick should be displayed
        boolean showGhost = shouldShowGhostBrick();
        ghostPanel.setVisible(showGhost);
        
        if (!showGhost) {
            return;
        }
        
        int ghostY = brick.getGhostYPosition();
        if (ghostY < 0 || ghostY == brick.getyPosition()) {
            // Ghost position not calculated or same as current position, hide ghost
            ghostPanel.setVisible(false);
            return;
        }
        
        ghostPanel.setLayoutX(calculateGridX(gamePanel, displayMatrix, brick.getxPosition()));
        ghostPanel.setLayoutY(calculateGridY(gamePanel, displayMatrix, ghostY));
        
        // Update ghost brick rectangles to match current brick shape
        int[][] brickData = brick.getBrickData();
        for (int i = 0; i < brickData.length && i < ghostRectangles.length; i++) {
            for (int j = 0; j < brickData[i].length && j < ghostRectangles[i].length; j++) {
                Rectangle ghostRect = ghostRectangles[i][j];
                if (brickData[i][j] != 0) {
                    // Show ghost rectangle for non-empty cells
                    ghostRect.setVisible(true);
                    // Enhanced ghost brick appearance: semi-transparent fill with border
                    Paint brickColor = getFillColor(brickData[i][j]);
                    if (brickColor instanceof Color) {
                        Color color = (Color) brickColor;
                        // Use semi-transparent fill (0.2 opacity for subtle effect)
                        ghostRect.setFill(new Color(color.getRed(), color.getGreen(), color.getBlue(), 0.2));
                        // Add semi-transparent border with same color (0.6 opacity for visibility)
                        ghostRect.setStroke(new Color(color.getRed(), color.getGreen(), color.getBlue(), 0.6));
                        ghostRect.setStrokeWidth(2.0);
                    } else {
                        ghostRect.setFill(Color.TRANSPARENT);
                        ghostRect.setStroke(null);
                    }
                } else {
                    // Hide ghost rectangle for empty cells
                    ghostRect.setVisible(false);
                }
            }
        }
    }

    /**
     * Updates the visual representation of the static game board background.
     * PERFORMANCE OPTIMIZATION: Only updates cells that have actually changed.
     *
     * @param board The updated board matrix.
     */
    public void refreshGameBackground(int[][] board) {
        if (displayMatrix == null || board == null) {
            return;
        }
        
        // Initialize cache on first call
        if (cachedBoardMatrix == null || 
            cachedBoardMatrix.length != board.length || 
            cachedBoardMatrix[0].length != board[0].length) {
            cachedBoardMatrix = new int[board.length][board[0].length];
            // Force full update on first call
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    cachedBoardMatrix[i][j] = board[i][j];
                    setRectangleData(board[i][j], displayMatrix[i][j]);
                }
            }
            return;
        }
        
        // Incremental update: only update changed cells
        for (int i = 0; i < board.length && i < displayMatrix.length; i++) {
            for (int j = 0; j < board[i].length && j < displayMatrix[i].length; j++) {
                if (cachedBoardMatrix[i][j] != board[i][j]) {
                    cachedBoardMatrix[i][j] = board[i][j];
                    setRectangleData(board[i][j], displayMatrix[i][j]);
                }
            }
        }
    }

    /**
     * Forces the active brick to refresh its position and shape based on supplied view data.
     * Used when the underlying board resets (e.g., starting a new game) to ensure the brick
     * appears at the intended spawn location before the timeline ticks.
     *
     * @param brick the current ViewData for the active brick
     */
    public void refreshActiveBrick(ViewData brick) {
        if (brick == null || isTwoPlayerMode) {
            return; // Only handle single-player boards here
        }

        boolean wasPaused = isPause.getValue();
        if (wasPaused) {
            isPause.setValue(false);
        }

        try {
            refreshBrick(brick);
        } finally {
            if (wasPaused) {
                isPause.setValue(true);
            }
        }
    }

    /**
     * Sets the color and styling for a specific rectangle based on its value.
     *
     * @param color      The integer value representing the color/state.
     * @param rectangle  The JavaFX Rectangle object to update.
     */
    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    /**
     * Handles the automatic downward movement of the brick.
     * Delegates the 'DOWN' event to the event listener (GameController) and updates the view.
     *
     * @param event The MoveEvent representing the automatic downward movement.
     */
    private void moveDown(MoveEvent event) {
        // Check for pause state before processing down event
        if (!isPause.getValue()) {
            DownData downData = eventListener.onDownEvent(event);
            // Check if downData is null (e.g., from PausedState) before processing
            if (downData != null) {
                if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                    NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                    groupNotification.getChildren().add(notificationPanel);
                    notificationPanel.showScore(groupNotification.getChildren());
                    // Endless progression: update on clear
                    if (isEndlessMode) {
                        int removedLines = downData.getClearRow().getLinesRemoved();
                        endlessLinesClearedUI += removedLines;
                        updateLines(endlessLinesClearedUI);
                        
                        applyEndlessProgression();
                    }
                }
                refreshBrick(downData.getViewData());
            }
        }
        gamePanel.requestFocus();
    }

    /**
     * Sets the event listener (likely GameController) for handling input events.
     *
     * @param eventListener The InputEventListener instance.
     */
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Binds the game score property to a GUI element (e.g., a label).
     * This method is currently a placeholder.
     *
     * @param integerProperty The IntegerProperty representing the score.
     */
    public void bindScore(IntegerProperty integerProperty) {
        // Score binding is handled through direct updates rather than property binding
    }

    /**
     * Handles the game over state.
     * Stops the automatic movement timeline and shows the game over panel.
     */
    public void gameOver() {
        // Play game over sound effect
        SoundManager.getInstance().playGameOverSound();
        
        // Stop all timelines and timers
        if (timeLine != null) {
            timeLine.stop(); // Stop automatic movement
        }
        if (timeTimer != null) {
            timeTimer.stop();
        }
        if (levelTimer != null) {
            levelTimer.stop();
        }
        if (countdownManager != null) {
            countdownManager.cancelCountdown();
        }
        
        gameOverPanel.setVisible(true);
        isGameOver.setValue(true);
        isPause.setValue(false); // Ensure pause is off on game over
        
        // Clear hold and next panels when game is over
        updateHoldDisplay(null);
        updateNextDisplay(null);
    }

    /**
     * Rebuilds the underlying game controller and board after a gameplay-affecting
     * settings change (e.g., switching the piece randomizer). Creates a fresh
     * GameController which constructs a new Board based on current settings and
     * re-initializes the view. Any running timers are stopped beforehand.
     */
    public void rebuildGameForRandomizerChange() {
        // Handle two-player mode separately
        if (isTwoPlayerMode && eventListener instanceof TwoPlayerGameController) {
            TwoPlayerGameController controller = (TwoPlayerGameController) eventListener;
            
            // Stop timelines before rebuilding
            try {
                java.lang.reflect.Field player1TimelineField = TwoPlayerGameController.class.getDeclaredField("player1Timeline");
                java.lang.reflect.Field player2TimelineField = TwoPlayerGameController.class.getDeclaredField("player2Timeline");
                java.lang.reflect.Field statsUpdateTimelineField = TwoPlayerGameController.class.getDeclaredField("statsUpdateTimeline");
                player1TimelineField.setAccessible(true);
                player2TimelineField.setAccessible(true);
                statsUpdateTimelineField.setAccessible(true);
                
                Timeline player1Timeline = (Timeline) player1TimelineField.get(controller);
                Timeline player2Timeline = (Timeline) player2TimelineField.get(controller);
                Timeline statsUpdateTimeline = (Timeline) statsUpdateTimelineField.get(controller);
                
                if (player1Timeline != null) {
                    player1Timeline.stop();
                }
                if (player2Timeline != null) {
                    player2Timeline.stop();
                }
                if (statsUpdateTimeline != null) {
                    statsUpdateTimeline.stop();
                }
            } catch (Exception e) {
                System.err.println("Failed to stop two-player timelines: " + e.getMessage());
            }
            
            // Clear all panels before rebuilding
            clearPlayer1Panels();
            clearPlayer2Panels();
            
            // Hide game over panels
            if (gameOverPanel1 != null) {
                gameOverPanel1.setVisible(false);
            }
            if (gameOverPanel2 != null) {
                gameOverPanel2.setVisible(false);
            }
            if (twoPlayerGameOverPanel != null) {
                twoPlayerGameOverPanel.setVisible(false);
                twoPlayerGameOverPanel.setManaged(false);
            }
            
            if (countdownManager != null) {
                countdownManager.cancelCountdown();
            }
            
            isPause.setValue(false);
            isGameOver.setValue(false);
            
            // Reset scores and statistics UI BEFORE creating new game controller
            // This ensures cleared data is shown during countdown
            updatePlayer1Score(0);
            updatePlayer2Score(0);
            // Create empty stats to show cleared statistics
            com.comp2042.model.mode.PlayerStats emptyStats = new com.comp2042.model.mode.PlayerStats();
            updatePlayerStats(1, emptyStats);
            updatePlayerStats(2, emptyStats);
            
            eventListener = null;
            isTwoPlayerMode = true;
            
            javafx.application.Platform.runLater(() -> {
                javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.millis(250));
                delay.setOnFinished(e -> {
                    if (rootPane != null && gamePanel1 != null && gamePanel2 != null &&
                        gamePanel1.getParent() != null && gamePanel2.getParent() != null) {
                        // Use dependency injection: create Board instances explicitly and inject them
                        com.comp2042.model.board.Board player1Board = new com.comp2042.model.board.SimpleBoard(10, 20);
                        com.comp2042.model.board.Board player2Board = new com.comp2042.model.board.SimpleBoard(10, 20);
                        com.comp2042.service.gameloop.GameService player1Service = new com.comp2042.service.gameloop.GameServiceImpl(player1Board);
                        com.comp2042.service.gameloop.GameService player2Service = new com.comp2042.service.gameloop.GameServiceImpl(player2Board);
                        com.comp2042.model.mode.TwoPlayerMode newGameMode = 
                            new com.comp2042.model.mode.TwoPlayerMode(player1Service, player2Service, this);
                        new TwoPlayerGameController(newGameMode, this);
                        rootPane.requestFocus();
                    } else {
                        javafx.animation.PauseTransition retryDelay = new javafx.animation.PauseTransition(javafx.util.Duration.millis(200));
                        retryDelay.setOnFinished(retryEvent -> {
                            if (rootPane != null && gamePanel1 != null && gamePanel2 != null && 
                                gamePanel1.getParent() != null && gamePanel2.getParent() != null) {
                                // Use dependency injection: create Board instances explicitly and inject them
                                com.comp2042.model.board.Board player1Board = new com.comp2042.model.board.SimpleBoard(10, 20);
                                com.comp2042.model.board.Board player2Board = new com.comp2042.model.board.SimpleBoard(10, 20);
                                com.comp2042.service.gameloop.GameService player1Service = new com.comp2042.service.gameloop.GameServiceImpl(player1Board);
                                com.comp2042.service.gameloop.GameService player2Service = new com.comp2042.service.gameloop.GameServiceImpl(player2Board);
                                com.comp2042.model.mode.TwoPlayerMode newGameMode = 
                                    new com.comp2042.model.mode.TwoPlayerMode(player1Service, player2Service, this);
                                new TwoPlayerGameController(newGameMode, this);
                                rootPane.requestFocus();
                            }
                        });
                        retryDelay.play();
                    }
                });
                delay.play();
            });
            return;
        }
        
        // Single-player mode handling
        // Stop timers
        if (timeLine != null) {
            timeLine.stop();
        }
        if (timeTimer != null) {
            timeTimer.stop();
        }
        timeLine = null;
        timeTimer = null;
        // Reset UI panels
        if (brickPanel != null) {
            brickPanel.getChildren().clear();
        }
        // Clear static board cells by resetting fills; keep grid nodes to preserve styling
        if (displayMatrix != null) {
            for (int i = 0; i < displayMatrix.length; i++) {
                for (int j = 0; j < displayMatrix[i].length; j++) {
                    if (displayMatrix[i][j] != null) {
                        displayMatrix[i][j].setFill(Color.TRANSPARENT);
                    }
                }
            }
        }
        if (nextBrickPanel != null) {
            nextBrickPanel.getChildren().clear();
        }
        if (holdPanel != null) {
            holdPanel.getChildren().clear();
        }
        // Clear cached display matrices so initGameView rebuilds them cleanly
        displayMatrix = null;
        rectangles = null;
        nextDisplayMatrix = null;
        holdDisplayMatrix = null;
        updateHoldDisplay(null);
        updateNextDisplay(null);
        isGameOver.setValue(false);
        isPause.setValue(false);
        
        // Reset score and lines display BEFORE creating new controller
        // This ensures cleared data is shown immediately
        if (isEndlessMode) {
            try {
                com.comp2042.model.mode.EndlessModeLeaderboard leaderboard = 
                    com.comp2042.model.mode.EndlessModeLeaderboard.getInstance();
                int highScore = leaderboard.getHighScore();
                updateScore(0, highScore);
            } catch (Exception e) {
                updateScore(0, 0);
            }
            updateLines(0);
        } else if (isLevelMode) {
            com.comp2042.model.mode.LevelManager levelManager = com.comp2042.model.mode.LevelManager.getInstance();
            com.comp2042.model.mode.LevelMode currentLevel = levelManager.getCurrentLevel();
            if (currentLevel != null) {
                updateScore(0, currentLevel.getBestScore());
                updateProgress(0, currentLevel.getTargetLines());
            } else {
                updateScore(0, 0);
            }
        } else {
            updateScore(0, 0);
            updateLines(0);
        }
        
        // Recreate controller which will read settings and build a new Board
        new GameController(this);
        // Re-assert grid lines visibility and style just in case
        if (gamePanel != null) {
            gamePanel.setGridLinesVisible(true);
            if (!gamePanel.getStyleClass().contains("game-grid")) {
                gamePanel.getStyleClass().add("game-grid");
            }
        }
        // Reset Endless mode timers/progression UI
        if (isEndlessMode) {
            gameStartTime = System.currentTimeMillis();
            totalPausedMillis = 0L;
            lastPauseStartMillis = 0L;
            endlessLevel = 1;
            endlessLinesClearedUI = 0;
            updateLevel(endlessLevel);
            updateSpeed(getSpeedDisplayForLevel(endlessLevel));
            updateGameSpeed(getDropMsForLevel(endlessLevel));
            updateTimeLabel();
            startTimeTimer();
        }
        gamePanel.requestFocus();
    }

    /**
     * Handles the request to start a new game.
     * Stops the timeline, hides the game over panel, requests a new game from the event listener,
     * and restarts the timeline.
     *
     * @param actionEvent The ActionEvent triggering the new game (e.g., from a button).
     */
    public void newGame(ActionEvent actionEvent) {
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        // Handle two-player mode
        if (isTwoPlayerMode && eventListener instanceof TwoPlayerGameController) {
            TwoPlayerGameController controller = (TwoPlayerGameController) eventListener;
            
            // Stop timelines before starting new game (to prevent blocks from falling during countdown)
            try {
                java.lang.reflect.Field player1TimelineField = TwoPlayerGameController.class.getDeclaredField("player1Timeline");
                java.lang.reflect.Field player2TimelineField = TwoPlayerGameController.class.getDeclaredField("player2Timeline");
                java.lang.reflect.Field statsUpdateTimelineField = TwoPlayerGameController.class.getDeclaredField("statsUpdateTimeline");
                player1TimelineField.setAccessible(true);
                player2TimelineField.setAccessible(true);
                statsUpdateTimelineField.setAccessible(true);
                
                Timeline player1Timeline = (Timeline) player1TimelineField.get(controller);
                Timeline player2Timeline = (Timeline) player2TimelineField.get(controller);
                Timeline statsUpdateTimeline = (Timeline) statsUpdateTimelineField.get(controller);
                
                if (player1Timeline != null) {
                    player1Timeline.stop();
                }
                if (player2Timeline != null) {
                    player2Timeline.stop();
                }
                if (statsUpdateTimeline != null) {
                    statsUpdateTimeline.stop();
                }
            } catch (Exception e) {
                // If reflection fails, fall back to onNewGameEvent
                // But we need to prevent it from starting timelines
                System.err.println("Failed to stop timelines via reflection: " + e.getMessage());
            }
            
            // Clear all panels before starting new game
            clearPlayer1Panels();
            clearPlayer2Panels();
            
            // Hide game over panels
            if (gameOverPanel1 != null) {
                gameOverPanel1.setVisible(false);
            }
            if (gameOverPanel2 != null) {
                gameOverPanel2.setVisible(false);
            }
            if (twoPlayerGameOverPanel != null) {
                twoPlayerGameOverPanel.setVisible(false);
                twoPlayerGameOverPanel.setManaged(false);
            }
            
            isPause.setValue(false);
            isGameOver.setValue(false);
            
            // Update UI to show cleared scores and statistics immediately (before countdown)
            updatePlayer1Score(0);
            updatePlayer2Score(0);
            com.comp2042.model.mode.TwoPlayerMode gameMode = controller.getGameMode();
            if (gameMode != null) {
                // Reset statistics only (not game state yet - that happens in onNewGameEvent)
                gameMode.getPlayer1Stats().reset();
                gameMode.getPlayer2Stats().reset();
                updatePlayerStats(1, gameMode.getPlayer1Stats());
                updatePlayerStats(2, gameMode.getPlayer2Stats());
            }
            
            // Let TwoPlayerGameController handle the countdown and game start
            // It will call showCountdownAndStart() internally
            controller.onNewGameEvent(new MoveEvent(EventType.NEW_GAME, EventSource.USER));
            
            // Request focus for keyboard input
            if (rootPane != null) {
                rootPane.requestFocus();
            }
            return;
        }
        
        // Handle single-player mode
        if (timeLine != null) {
            timeLine.stop(); // Stop current timeline
        }
        if (timeTimer != null) {
            timeTimer.stop();
        }
        if (levelTimer != null) {
            stopLevelTimer(); // Stop Level Mode timer
        }
        gameOverPanel.setVisible(false);
        
        // Clear hold and next panels when starting new game
        updateHoldDisplay(null);
        updateNextDisplay(null);
        // Reset time for Endless Mode
        if (isEndlessMode) {
            gameStartTime = System.currentTimeMillis();
            totalPausedMillis = 0L;
            lastPauseStartMillis = 0L;
            updateTimeLabel();
            endlessLevel = 1;
            endlessLinesClearedUI = 0;
            updateLevel(endlessLevel);
            updateSpeed(getSpeedDisplayForLevel(endlessLevel));
            updateGameSpeed(getDropMsForLevel(endlessLevel));
        }
        // Reset timer for Level Mode
        if (isLevelMode) {
            com.comp2042.model.mode.LevelManager levelManager = com.comp2042.model.mode.LevelManager.getInstance();
            com.comp2042.model.mode.LevelMode currentLevel = levelManager.getCurrentLevel();
            if (currentLevel != null) {
                updateTime(currentLevel.getTimeLimitSeconds()); // Reset timer to full time limit
            }
        }
        
        eventListener.onNewGameEvent(new MoveEvent(EventType.NEW_GAME, EventSource.USER));
        gamePanel.requestFocus();
        
        // Restart timeline after state transition (assuming PlayingState starts the game loop)
        if (timeLine != null) {
            timeLine.play(); // Restart automatic movement
        }
        // Restart timer if Endless Mode
        if (isEndlessMode) {
            startTimeTimer();
        }
        isPause.setValue(false);
        isGameOver.setValue(false);
    }

    /**
     * Handles the request to pause or unpause the game via a UI button.
     * Delegates the pause request to the GameController or TwoPlayerGameController.
     *
     * @param actionEvent The ActionEvent triggering the pause/unpause.
     */
    public void pauseGame(ActionEvent actionEvent) {
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        // Call the controller's pause request method
        if (eventListener instanceof GameController) {
            ((GameController) eventListener).requestPause();
        } else if (eventListener instanceof TwoPlayerGameController) {
            ((TwoPlayerGameController) eventListener).requestPause();
        }
        
        // Toggle pause state and handle timer accordingly
        isPause.setValue(!isPause.getValue());
        
        if (isPause.getValue()) {
            // Game is now paused - pause the timer
            if (lastPauseStartMillis == 0L) {
                lastPauseStartMillis = System.currentTimeMillis();
            }
            if (timeTimer != null) {
                timeTimer.pause();
            }
            if (levelTimer != null && isLevelMode) {
                levelTimer.pause();
            }
        } else {
            // Game is now resumed - resume the timer
            if (lastPauseStartMillis > 0L) {
                totalPausedMillis += System.currentTimeMillis() - lastPauseStartMillis;
                lastPauseStartMillis = 0L;
            }
            if (timeTimer != null) {
                timeTimer.play();
            }
            if (levelTimer != null && isLevelMode) {
                levelTimer.play();
            }
        }
        
        // Request focus for keyboard input
        if (isTwoPlayerMode && rootPane != null) {
            rootPane.requestFocus();
        } else if (gamePanel != null) {
            gamePanel.requestFocus();
        }
    }

    /**
     * Sets the pause state directly without triggering pause logic.
     * Used when restoring countdown state.
     * 
     * @param paused whether the game should be paused
     */
    public void setPauseStateDirectly(boolean paused) {
        isPause.setValue(paused);
    }
    
    /**
     * Resumes the game by restarting the timeline if it exists.
     * This method is called when returning from settings to ensure the game continues.
     */
    public void resumeGame() {
        if (timeLine != null && !isGameOver.getValue()) {
            timeLine.play();
        }
        if (timeTimer != null && !isGameOver.getValue()) {
            timeTimer.play();
        }
        // Ensure pause state is false when resuming
        if (isPause.getValue() && lastPauseStartMillis > 0L) {
            totalPausedMillis += System.currentTimeMillis() - lastPauseStartMillis;
            lastPauseStartMillis = 0L;
        }
        isPause.setValue(false);
        // Request focus appropriately for single or two-player mode
        if (isTwoPlayerMode && rootPane != null) {
            rootPane.requestFocus();
        } else if (gamePanel != null) {
            gamePanel.requestFocus();
        }
    }

    /**
     * Resumes gameplay after returning from modal overlays (Settings/Help).
     * Ensures the state machine toggles back to Playing before timers resume.
     */
    public void resumeFromOverlay() {
        if (eventListener instanceof GameController) {
            ((GameController) eventListener).requestPause(); // Paused -> Playing
        } else if (eventListener instanceof TwoPlayerGameController) {
            ((TwoPlayerGameController) eventListener).requestPause(); // Paused -> Playing
        }
        resumeGame();
    }

    public void updateProgress(int linesClearedInLevel, int targetLines) {
        if (uiManager != null) {
            uiManager.updateProgress(linesClearedInLevel, targetLines);
        }
    }

    public void updateStarDisplay(int stars) {
        if (uiManager != null) {
            uiManager.updateStarDisplay(stars);
        }
    }

    public void updateLevelSpeedDisplay(int levelId) {
        if (uiManager != null) {
            uiManager.updateLevelSpeedDisplay(levelId);
        }
    }

    public void updateBestStats(int bestScore, long bestTimeMillis) {
        if (uiManager != null) {
            uiManager.updateBestStats(bestScore, bestTimeMillis);
        }
    }

    /**
     * Updates the central game title to indicate the active level.
     *
     * @param levelId the numeric level identifier to display
     */
    public void setGameTitleForLevel(int levelId) {
        if (gameTitleLabel != null) {
            gameTitleLabel.setText("Level " + levelId);
        }
    }

    /**
     * Resets the central game title to the default label for non-level modes.
     */
    public void resetGameTitle() {
        if (gameTitleLabel != null) {
            gameTitleLabel.setText("TETRIS");
        }
    }
    
    /**
     * Applies theme colors to Hold and Next preview displays.
     * 
     * @param accentColor the theme's accent color (e.g., "#FFD700" for gold)
     */
    public void applyThemeToPreviewDisplays(String accentColor) {
        if (holdPanel != null) {
            String style = String.format(
                "-fx-background-color: rgba(10, 14, 39, 0.8); " +
                "-fx-border-color: %s; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 5px; " +
                "-fx-background-radius: 5px; " +
                "-fx-padding: 10px;",
                accentColor
            );
            holdPanel.setStyle(style);
        }
        
        if (nextBrickPanel != null) {
            String style = String.format(
                "-fx-background-color: rgba(10, 14, 39, 0.8); " +
                "-fx-border-color: %s; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 5px; " +
                "-fx-background-radius: 5px; " +
                "-fx-padding: 10px;",
                accentColor
            );
            nextBrickPanel.setStyle(style);
        }
    }

    public void showLevelModeUI() {
        isLevelMode = true;
        levelStartTime = System.currentTimeMillis(); // Track level start time
        
        if (leftObjectiveBox != null) {
            leftObjectiveBox.setManaged(true);
            leftObjectiveBox.setVisible(true);
        }
        
        if (statisticsBox != null) {
            statisticsBox.setManaged(false);
            statisticsBox.setVisible(false);
        }
        
        if (bestStatsBox != null) {
            bestStatsBox.setVisible(true);
            bestStatsBox.setManaged(true);
        }
        
        if (highScoreLabel != null) {
            highScoreLabel.setVisible(false);
            highScoreLabel.setManaged(false);
        }

        if (backToSelectionButton != null) {
            backToSelectionButton.setVisible(true);
            backToSelectionButton.setManaged(true);
        }
    }
    
    /**
     * Checks if the game is in Level Mode.
     * @return true if in Level Mode, false otherwise
     */
    public boolean isLevelMode() {
        return isLevelMode;
    }

    public void hideLevelModeUI() {
        isLevelMode = false;
        
        if (leftObjectiveBox != null) {
            leftObjectiveBox.setManaged(false);
            leftObjectiveBox.setVisible(false);
        }
        
        if (statisticsBox != null) {
            statisticsBox.setManaged(true);
            statisticsBox.setVisible(true);
        }
        
        if (bestStatsBox != null) {
            bestStatsBox.setVisible(false);
            bestStatsBox.setManaged(false);
        }
        
        if (highScoreLabel != null) {
            highScoreLabel.setVisible(true);
            highScoreLabel.setManaged(true);
        }
        
        stopLevelTimer();
        resetGameTitle();

        if (backToSelectionButton != null) {
            backToSelectionButton.setVisible(false);
            backToSelectionButton.setManaged(false);
        }
    }

    public void updateTime(int timeLimitSeconds) {
        this.levelTimeLimitSeconds = timeLimitSeconds;
        this.levelTimeRemainingSeconds = timeLimitSeconds;
        startLevelTimer();
        updateTimeDisplay();
    }

    private void startLevelTimer() {
        stopLevelTimer();
        if (uiManager == null) {
            return;
        }
        
        levelTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (!isPause.getValue() && !isGameOver.getValue()) {
                if (levelTimeRemainingSeconds > 0) {
                    levelTimeRemainingSeconds--;
                    updateTimeDisplay();
                    
                    // Color is set in updateTimeDisplay()
                } else {
                    // Time's up
                    uiManager.updateLevelTimer(0);
                }
            }
        }));
        levelTimer.setCycleCount(Timeline.INDEFINITE);
        if (!isGameOver.getValue() && !isPause.getValue()) {
            levelTimer.play();
        }
    }

    private void stopLevelTimer() {
        if (levelTimer != null) {
            levelTimer.stop();
            levelTimer = null;
        }
    }

    private void updateTimeDisplay() {
        if (uiManager != null) {
            uiManager.updateLevelTimer(levelTimeRemainingSeconds);
        }
    }

    public int getTimeRemainingSeconds() {
        return levelTimeRemainingSeconds;
    }
    
    // ==================== EndlessMode UI Update Methods ====================
    private void applyEndlessProgression() {
        int newLevel = 1 + (endlessLinesClearedUI / 10); // +1 level every 10 lines
        newLevel = Math.min(newLevel, 15); // cap at 15
        if (newLevel != endlessLevel) {
            endlessLevel = newLevel;
            updateLevel(endlessLevel);
            int dropMs = getDropMsForLevel(endlessLevel);
            updateGameSpeed(dropMs);
            int speedDisplay = getSpeedDisplayForLevel(endlessLevel);
            updateSpeed(speedDisplay);
            showLevelUpNotification(endlessLevel);
        }
    }

    private int getDropMsForLevel(int level) {
        // Reasonable decreasing speeds (ms per step)
        // 1:700, 2:600, 3:530, 4:470, 5:420, 6:380, 7:340, 8:300, 9:260, 10:230, 11:200, 12:180, 13:160, 14:140, 15+:120
        int[] table = {700, 600, 530, 470, 420, 380, 340, 300, 260, 230, 200, 180, 160, 140, 120};
        int idx = Math.max(1, Math.min(level, 15)) - 1;
        return table[idx];
    }

    private int getSpeedDisplayForLevel(int level) {
        // Display as integer multiplier: +1 every 2 levels, cap 8x
        int speed = 1 + ((Math.max(1, level) - 1) / 2);
        return Math.min(speed, 8);
    }
    
    // ==================== Public getters for Endless Mode statistics ====================
    
    /**
     * Gets current level for Endless Mode.
     * @return current level
     */
    public int getCurrentLevel() {
        return endlessLevel;
    }
    
    private void startTimeTimer() {
        if (timeLabel == null) {
            return;
        }
        // Stop and clear old timer to prevent memory leaks
        if (timeTimer != null) {
            timeTimer.stop();
            timeTimer = null;
        }
        updateTimeLabel();
        timeTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimeLabel()));
        timeTimer.setCycleCount(Timeline.INDEFINITE);
        if (!isGameOver.getValue() && !isPause.getValue()) {
            timeTimer.play();
        }
    }

    private void updateTimeLabel() {
        if (uiManager == null) {
            return;
        }
        long now = System.currentTimeMillis();
        long pausedSoFar = totalPausedMillis + (isPause.getValue() && lastPauseStartMillis > 0L ? (now - lastPauseStartMillis) : 0L);
        long elapsed = (gameStartTime > 0) ? (now - gameStartTime - pausedSoFar) : 0;
        long totalSeconds = elapsed / 1000;
        uiManager.updateElapsedTime(totalSeconds);
    }
    
    /**
     * Updates the score display for EndlessMode.
     * 
     * @param currentScore the current score
     * @param highScore the high score
     */
    public void updateScore(int currentScore, int highScore) {
        if (uiManager != null) {
            uiManager.updateScore(currentScore, highScore);
        }
    }
    
    /**
     * Updates the lines cleared display for EndlessMode.
     * 
     * @param linesCleared the number of lines cleared
     */
    public void updateLines(int linesCleared) {
        if (uiManager != null) {
            uiManager.updateLines(linesCleared);
        }
    }
    
    /**
     * Updates the level display for EndlessMode.
     * 
     * @param level the current level
     */
    public void updateLevel(int level) {
        if (uiManager != null) {
            uiManager.updateLevel(level);
        }
    }
    
    /**
     * Updates the speed display for EndlessMode.
     * 
     * @param speedLevel the current speed level
     */
    public void updateSpeed(int speedLevel) {
        if (uiManager != null) {
            uiManager.updateSpeed(speedLevel);
        }
    }
    
    /**
     * Updates the game speed for EndlessMode.
     * 
     * @param newSpeed the new drop speed in milliseconds
     */
    public void updateGameSpeed(int newSpeed) {
        if (timeLine != null) {
            timeLine.stop();
            timeLine = new Timeline(new KeyFrame(
                    Duration.millis(newSpeed),
                    ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
            ));
            timeLine.setCycleCount(Timeline.INDEFINITE);
            
            // Only play if game is not paused and not over
            if (!isPause.getValue() && !isGameOver.getValue()) {
                timeLine.play();
            }
        }
    }
    
    /**
     * Sets the drop speed for the game.
     * 
     * @param speed the new drop speed in milliseconds
     */
    public void setDropSpeed(int speed) {
        this.currentDropSpeed = speed;
        updateGameSpeed(speed);
    }
    
    /**
     * Sets the brick opacity for visual effects.
     * 
     * @param opacity the opacity value (0.0 to 1.0)
     */
    public void setBrickOpacity(double opacity) {
        this.currentBrickOpacity = Math.max(0.0, Math.min(1.0, opacity));
    }
    
    /**
     * Sets whether ghost piece should be displayed.
     * 
     * @param enabled true to show ghost piece, false to hide it
     */
    public void setGhostEnabled(boolean enabled) {
        this.ghostEnabled = enabled;
    }
    
    /**
     * Checks if ghost brick should be displayed based on game mode conditions.
     * Ghost brick is shown for:
     * - Endless mode: when level is less than 5 (levels 1-4)
     * - Level mode: when difficulty is Easy (level 1 and 2)
     * - Two-player mode: always
     * 
     * @return true if ghost brick should be displayed, false otherwise
     */
    private boolean shouldShowGhostBrick() {
        if (!ghostEnabled) {
            return false;
        }
        
        // Two-player mode: always show ghost brick
        if (isTwoPlayerMode) {
            return true;
        }
        
        // Endless mode: show ghost brick when level is less than 5 (levels 1-4)
        if (isEndlessMode) {
            return endlessLevel < 5;
        }
        
        // Level mode: show ghost brick for Easy difficulty (level 1 and 2)
        if (isLevelMode) {
            com.comp2042.model.mode.LevelManager levelManager = com.comp2042.model.mode.LevelManager.getInstance();
            com.comp2042.model.mode.LevelMode currentLevel = levelManager.getCurrentLevel();
            if (currentLevel != null) {
                String difficulty = currentLevel.getDifficulty();
                return "Easy".equals(difficulty);
            }
        }
        
        return false;
    }
    
    /**
     * Shows a level up notification.
     * 
     * @param newLevel the new level number
     */
    public void showLevelUpNotification(int newLevel) {
        // Create level up notification label
        Label levelUpLabel = new Label("LEVEL UP: " + newLevel);
        levelUpLabel.setStyle(
            "-fx-font-size: 48px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #FFD700; " +
            "-fx-effect: dropshadow(gaussian, rgba(255, 215, 0, 0.8), 15, 0, 0, 0);"
        );
        
        // Add to notification group
        if (groupNotification != null) {
            groupNotification.getChildren().add(levelUpLabel);
            
            // Remove after 3 seconds
            Timeline removeTimeline = new Timeline(new KeyFrame(
                Duration.seconds(3),
                ae -> groupNotification.getChildren().remove(levelUpLabel)
            ));
            removeTimeline.play();
        }
    }
    
    /**
     * Updates the level display.
     * 
     * @param level the current level
     */
    public void updateLevelDisplay(int level) {
        updateLevel(level);
    }
    
    /**
     * Updates the next piece display for EndlessMode.
     * 
     * @param nextBrickData the next brick shape data
     */
    public void updateNextDisplay(int[][] nextBrickData) {
        if (nextBrickPanel == null) {
            return;
        }
        
        // Initialize next display matrix if not already done
        if (nextDisplayMatrix == null) {
            nextDisplayMatrix = new Rectangle[4][4];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    Rectangle rectangle = new Rectangle(20, 20);
                    rectangle.setFill(Color.TRANSPARENT);
                    rectangle.setArcHeight(9);
                    rectangle.setArcWidth(9);
                    nextDisplayMatrix[i][j] = rectangle;
                    nextBrickPanel.add(rectangle, j, i);
                }
            }
        }
        
        // Clear the display
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                nextDisplayMatrix[i][j].setFill(Color.TRANSPARENT);
            }
        }
        
        // Display the brick if data is provided
        if (nextBrickData != null) {
            for (int i = 0; i < nextBrickData.length && i < 4; i++) {
                for (int j = 0; j < nextBrickData[i].length && j < 4; j++) {
                    if (nextBrickData[i][j] != 0) {
                        nextDisplayMatrix[i][j].setFill(getFillColor(nextBrickData[i][j]));
                    }
                }
            }
        }
    }
    
    /**
     * Updates the hold piece display for EndlessMode.
     * 
     * @param holdBrickData the held brick shape data
     */
    public void updateHoldDisplay(int[][] holdBrickData) {
        if (holdPanel == null) {
            return;
        }
        
        // Initialize hold display matrix if not already done
        if (holdDisplayMatrix == null) {
            holdDisplayMatrix = new Rectangle[4][4];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    Rectangle rectangle = new Rectangle(20, 20);
                    rectangle.setFill(Color.TRANSPARENT);
                    rectangle.setArcHeight(9);
                    rectangle.setArcWidth(9);
                    holdDisplayMatrix[i][j] = rectangle;
                    holdPanel.add(rectangle, j, i);
                }
            }
        }
        
        // Clear the display
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                holdDisplayMatrix[i][j].setFill(Color.TRANSPARENT);
            }
        }
        
        // Display the brick if data is provided
        if (holdBrickData != null) {
            for (int i = 0; i < holdBrickData.length && i < 4; i++) {
                for (int j = 0; j < holdBrickData[i].length && j < 4; j++) {
                    if (holdBrickData[i][j] != 0) {
                        holdDisplayMatrix[i][j].setFill(getFillColor(holdBrickData[i][j]));
                    }
                }
            }
        }
    }
    
    /**
     * Toggles mute state for audio.
     * When muted, sets Master Volume to 0%. When unmuted, restores previous volume.
     * This affects all audio (both music and sound effects) through the master volume control.
     */
    @FXML
    public void toggleMute() {
        if (audioVolumeManager != null) {
            audioVolumeManager.toggleMute();
            }
    }
    
    /**
     * Sets the game mode for keyboard binding purposes.
     * @param isTwoPlayer true for two-player mode, false for single-player mode
     */
    public void setGameMode(boolean isTwoPlayer) {
        this.isTwoPlayerMode = isTwoPlayer;
    }
    
    /**
     * Updates the Mute button state based on current Master Volume.
     * Called when settings are saved from the settings dialog.
     */
    public void updateMuteButtonState() {
        if (audioVolumeManager != null) {
            audioVolumeManager.syncFromSettings();
        }
    }
    
    /**
     * Handles keyboard controls for single-player mode (Endless/Level Mode).
     * Controls: A/D - Move, W - Rotate, S - Soft Drop, Space - Hard Drop, Left Shift - Hold, F - Rotate CCW
     */
    private void handleSinglePlayerControls(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.A) {
            // Move left
            refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.KEYBOARD_PLAYER_1)));
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.D) {
            // Move right
            refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.KEYBOARD_PLAYER_1)));
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.W) {
            // Rotate clockwise
            refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.KEYBOARD_PLAYER_1)));
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.S) {
            // Soft drop
            moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.SPACE) {
            // Hard drop
            moveDown(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.SHIFT) {
            // Hold brick (Left Shift only)
            ViewData result = eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.KEYBOARD_PLAYER_1));
            if (result != null) {
                refreshBrick(result);
                updateHoldDisplay(result.getHoldBrickData());
            }
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.F) {
            // Rotate counterclockwise
            refreshBrick(eventListener.onRotateCCWEvent(new MoveEvent(EventType.ROTATE_CCW, EventSource.KEYBOARD_PLAYER_1)));
            keyEvent.consume();
        }
    }
    
    /**
     * Handles keyboard controls for two-player mode.
     * Player 1: A/D - Move, W - Rotate, S - Soft Drop, Space - Hard Drop, Shift/C - Hold, F - Rotate CCW
     * Player 2: ←/→ - Move, ↑ - Rotate, ↓ - Soft Drop, 0 - Hard Drop, 3 - Hold, 2 - Rotate CCW
     */
    private void handleTwoPlayerControls(KeyEvent keyEvent) {
        // Check if eventListener is null (e.g., during countdown)
        // This prevents NullPointerException when keys are pressed before game starts
        if (eventListener == null) {
            keyEvent.consume();
            return;
        }
        
        // === Player 1 Controls (WASD Keys) ===
        if (keyEvent.getCode() == KeyCode.A) {
            // Player 1: Move left
            ViewData result = eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.KEYBOARD_PLAYER_1));
            if (result != null) {
                refreshPlayer1Brick(result);
            }
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.D) {
            // Player 1: Move right
            ViewData result = eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.KEYBOARD_PLAYER_1));
            if (result != null) {
                refreshPlayer1Brick(result);
            }
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.W) {
            // Player 1: Rotate clockwise
            ViewData result = eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.KEYBOARD_PLAYER_1));
            if (result != null) {
                refreshPlayer1Brick(result);
            }
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.S) {
            // Player 1: Soft drop
            DownData downData = eventListener.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.KEYBOARD_PLAYER_1));
            if (downData != null) {
                handlePlayer1DownEvent(downData);
            }
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.SPACE) {
            // Player 1: Hard drop (Scene-level filter ensures delivery even if a Button is focused)
            DownData downData = eventListener.onDownEvent(new MoveEvent(EventType.HARD_DROP, EventSource.KEYBOARD_PLAYER_1));
            if (downData != null) {
                handlePlayer1DownEvent(downData);
            }
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.SHIFT) {
            // Player 1: Hold brick
            ViewData result = eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.KEYBOARD_PLAYER_1));
            if (result != null) {
                refreshPlayer1Brick(result);
            }
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.F) {
            // Player 1: Rotate counterclockwise
            ViewData result = eventListener.onRotateCCWEvent(new MoveEvent(EventType.ROTATE_CCW, EventSource.KEYBOARD_PLAYER_1));
            if (result != null) {
                refreshPlayer1Brick(result);
            }
            keyEvent.consume();
        }
        
        // === Player 2 Controls (Arrow Keys + Special Keys) ===
        if (keyEvent.getCode() == KeyCode.LEFT) {
            // Player 2: Move left
            ViewData result = eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.KEYBOARD_PLAYER_2));
            if (result != null) {
                refreshPlayer2Brick(result);
            }
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.RIGHT) {
            // Player 2: Move right
            ViewData result = eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.KEYBOARD_PLAYER_2));
            if (result != null) {
                refreshPlayer2Brick(result);
            }
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.UP) {
            // Player 2: Rotate clockwise
            ViewData result = eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.KEYBOARD_PLAYER_2));
            if (result != null) {
                refreshPlayer2Brick(result);
            }
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.DOWN) {
            // Player 2: Soft drop
            DownData downData = eventListener.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.KEYBOARD_PLAYER_2));
            if (downData != null) {
                handlePlayer2DownEvent(downData);
            }
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.DIGIT0 || keyEvent.getCode() == KeyCode.NUMPAD0) {
            // Player 2: Hard drop (0 / NumPad0)
            DownData downData = eventListener.onDownEvent(new MoveEvent(EventType.HARD_DROP, EventSource.KEYBOARD_PLAYER_2));
            if (downData != null) {
                handlePlayer2DownEvent(downData);
            }
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.DIGIT3 || keyEvent.getCode() == KeyCode.NUMPAD3) {
            // Player 2: Hold brick (3 / NumPad3)
            ViewData result = eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.KEYBOARD_PLAYER_2));
            if (result != null) {
                refreshPlayer2Brick(result);
            }
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.DIGIT2 || keyEvent.getCode() == KeyCode.NUMPAD2) {
            // Player 2: Rotate counterclockwise (2 / NumPad2)
            ViewData result = eventListener.onRotateCCWEvent(new MoveEvent(EventType.ROTATE_CCW, EventSource.KEYBOARD_PLAYER_2));
            if (result != null) {
                refreshPlayer2Brick(result);
            }
            keyEvent.consume();
        }
    }
    
    /**
     * Shows settings dialog.
     * Opens the settings page with ability to return to current game.
     */
    @FXML
    public void showSettings() {
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        try {
            // Save current game scene for returning - handle both single-player and two-player modes
            Scene currentGameScene;
            if (isTwoPlayerMode && rootPane != null) {
                currentGameScene = rootPane.getScene();
            } else if (gamePanel != null) {
                currentGameScene = gamePanel.getScene();
            } else {
                System.err.println("Cannot determine current scene for settings");
                return;
            }
            
            if (currentGameScene == null) {
                System.err.println("Current game scene is null");
                return;
            }
            
            Stage stage = (Stage) currentGameScene.getWindow();
            
            final boolean wasCountdownRunning = countdownManager != null && countdownManager.isRunning();
            final Runnable savedCountdownCallback = (wasCountdownRunning && countdownManager != null)
                ? countdownManager.pauseCountdown()
                : null;
            
            // Ensure the game is paused while settings are open
            boolean wasGamePaused = isPause.getValue();
            if (!wasGamePaused) {
                if (isTwoPlayerMode && eventListener instanceof TwoPlayerGameController) {
                    ((TwoPlayerGameController) eventListener).requestPause();
                } else if (eventListener instanceof GameController) {
                    ((GameController) eventListener).requestPause();
                }
                // Pause local timers defensively
                if (timeLine != null) {
                    timeLine.pause();
                }
                if (timeTimer != null) {
                    timeTimer.pause();
                }
                isPause.setValue(true);
            }

            // Load settings FXML
            FXMLLoader settingsLoader = new FXMLLoader(getClass().getResource("/settings.fxml"));
            Parent settingsRoot = settingsLoader.load();
            double settingsWidth = isTwoPlayerMode ? 1400 : 900;
            double settingsHeight = isTwoPlayerMode ? 900 : 800;
            Scene settingsScene = new Scene(settingsRoot, settingsWidth, settingsHeight);
            
            // Get the settings controller and configure it
            com.comp2042.controller.menu.SettingsController settingsController = settingsLoader.getController();
            settingsController.setStage(stage);
            settingsController.setSavedGameScene(currentGameScene); // Pass current game scene
            // Pass whether it was already paused before opening settings, and countdown state
            settingsController.setGameController(this, wasGamePaused, wasCountdownRunning, savedCountdownCallback);
            
            // Set up keyboard handling to prevent space key conflicts
            settingsController.setupKeyboardHandling(settingsScene);
            
            // Apply settings CSS
            settingsScene.getStylesheets().add(
                getClass().getResource("/settings.css").toExternalForm()
            );
            
            // Switch to settings scene
            stage.setScene(settingsScene);
            stage.setTitle("TETRIS - Settings");
            centerWindowOnScreen(stage, settingsWidth, settingsHeight);
        } catch (Exception e) {
            System.err.println("Error loading settings page: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Shows help dialog with game mode descriptions.
     */
    @FXML
    public void showHelp() {
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        
        try {
            boolean wasPaused = isPause.getValue();
            final boolean wasCountdownRunning = countdownManager != null && countdownManager.isRunning();
            
            final Runnable savedCountdownCallback = (wasCountdownRunning && countdownManager != null)
                ? countdownManager.pauseCountdown()
                : null;
            
            // Pause game if it's running (but not during countdown - countdown means game hasn't started yet)
            if (!wasPaused && !wasCountdownRunning) {
                if (eventListener instanceof GameController) {
                    ((GameController) eventListener).requestPause();
                } else if (eventListener instanceof TwoPlayerGameController) {
                    ((TwoPlayerGameController) eventListener).requestPause();
                }
                if (timeLine != null) timeLine.pause();
                if (timeTimer != null) timeTimer.pause();
                isPause.setValue(true);
            }
            
            // If countdown is running, ensure timelines are stopped (game hasn't started yet)
            if (wasCountdownRunning && eventListener instanceof TwoPlayerGameController) {
                TwoPlayerGameController controller = (TwoPlayerGameController) eventListener;
                try {
                    java.lang.reflect.Field player1TimelineField = TwoPlayerGameController.class.getDeclaredField("player1Timeline");
                    java.lang.reflect.Field player2TimelineField = TwoPlayerGameController.class.getDeclaredField("player2Timeline");
                    java.lang.reflect.Field statsUpdateTimelineField = TwoPlayerGameController.class.getDeclaredField("statsUpdateTimeline");
                    player1TimelineField.setAccessible(true);
                    player2TimelineField.setAccessible(true);
                    statsUpdateTimelineField.setAccessible(true);
                    
                    Timeline player1Timeline = (Timeline) player1TimelineField.get(controller);
                    Timeline player2Timeline = (Timeline) player2TimelineField.get(controller);
                    Timeline statsUpdateTimeline = (Timeline) statsUpdateTimelineField.get(controller);
                    
                    if (player1Timeline != null) {
                        player1Timeline.stop();
                    }
                    if (player2Timeline != null) {
                        player2Timeline.stop();
                    }
                    if (statsUpdateTimeline != null) {
                        statsUpdateTimeline.stop();
                    }
                } catch (Exception e) {
                    System.err.println("Failed to stop timelines during countdown: " + e.getMessage());
                }
            }
            // Create help dialog
            Stage helpStage = new Stage();
            helpStage.setTitle("Gameplay Guide");
            helpStage.initModality(Modality.APPLICATION_MODAL);
            helpStage.setResizable(false);
            
            // Create scrollable content
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefViewportHeight(520);
            scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
            scrollPane.getStyleClass().add("help-scroll");

            VBox mainContainer = new VBox(20);
            mainContainer.setPadding(new Insets(30));
            mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #1A0033, #2D1B69);");
            
            // Title
            Label titleLabel = new Label("Gameplay Guide");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4DFFFF; -fx-alignment: center;");
            titleLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(titleLabel, Priority.ALWAYS);
            
            // Game modes section (compact, no section header)
            VBox modesContainer = new VBox(12);
            modesContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.08); -fx-background-radius: 10; -fx-padding: 18;");
            
            // Mode descriptions
            String[][] modeData = {
                {"Endless Mode", "Play endlessly and aim for the highest score."},
                {"Level Mode", "Clear levels with increasing difficulty and unlock new themes."},
                {"Two-Player Mode", "Challenge a friend in local two-player battle."}
            };
            
            for (String[] mode : modeData) {
                HBox modeRow = new HBox(20);
                modeRow.setAlignment(Pos.CENTER_LEFT);
                
                // Mode name
                Label modeLabel = new Label(mode[0]);
                modeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-min-width: 150;");
                
                // Mode description
                Label descLabel = new Label(mode[1]);
                descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
                descLabel.setMaxWidth(500);
                descLabel.setWrapText(true);
                descLabel.setPrefWidth(500);
                
                modeRow.getChildren().addAll(modeLabel, descLabel);
                modesContainer.getChildren().add(modeRow);
            }

            // Basics & Controls split into two purple boxes
            HBox basicsDual = new HBox(20);
            basicsDual.setAlignment(Pos.TOP_LEFT);

            // Left: Gameplay Basics & Rules
            VBox basicsLeftBox = new VBox(12);
            basicsLeftBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.08); -fx-background-radius: 10; -fx-padding: 18;");
            Label basicsLeftTitle = new Label("Gameplay Basics");
            basicsLeftTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");
            VBox basicsLeftCol = createBulletedColumn(new String[] {
                "Place falling tetrominoes to complete rows.",
                "Clear full horizontal lines to earn points.",
                "Clear multiple lines at once for higher points.",
                "Pieces fall faster as you clear more lines.",
                "Topping out (stack reaches top) ends the game."
            }, 330);
            basicsLeftBox.getChildren().addAll(basicsLeftTitle, basicsLeftCol);

            // Right: Sidebar Panels & Actions
            VBox basicsRightBox = new VBox(12);
            basicsRightBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.08); -fx-background-radius: 10; -fx-padding: 18;");
            Label basicsRightTitle = new Label("Side Panels & Actions");
            basicsRightTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");
            VBox basicsRightCol = createBulletedColumn(new String[] {
                "Next: preview upcoming pieces.",
                "Hold: store one piece to swap later (one swap per piece).",
                "Ghost Brick: semi-transparent preview showing where the piece will land.",
                "Statistics: shows Level, Lines cleared, Speed and Time.",
                "Score: real-time points and the Highest Score.",
                "Controls: Settings, Help, Back to Menu.",
                "Actions: New Game (N), Pause & Resume (P), Mute."
            }, 330);
            basicsRightBox.getChildren().addAll(basicsRightTitle, basicsRightCol);

            basicsDual.getChildren().addAll(basicsLeftBox, basicsRightBox);

            // Piece Randomizer help section
            VBox rngContainer = new VBox(10);
            rngContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10; -fx-padding: 20;");

            Label rngTitle = new Label("Piece Randomizer Systems");
            rngTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-alignment: center;");
            rngTitle.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(rngTitle, Priority.ALWAYS);

            Label rngIntro = new Label(
                "Modern Tetris variants use a \"bag\" to distribute tetrominoes, while early games used pure random selection. Choose your system in Settings > Gameplay > Piece Randomizer. Default is 7‑Bag System.");
            rngIntro.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            rngIntro.setWrapText(true);

            // 7-Bag description
            Label bagHeader = new Label("7‑Bag System (Recommended)");
            bagHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

            Label bagDesc = new Label(
                "• Each set of seven contains I, O, T, S, Z, J, L exactly once, then a new bag is shuffled.\n" +
                "• Guarantees fairness and predictability: no long droughts, no long streaks.\n" +
                "• Best for skill development and consistent difficulty.");
            bagDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            bagDesc.setWrapText(true);

            // Pure Random description
            Label prHeader = new Label("Pure Random System (Classic)");
            prHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

            Label prDesc = new Label(
                "• Each piece is chosen uniformly at random with replacement.\n" +
                "• Can produce streaks and droughts (harder and more volatile).\n" +
                "• Choose this if you prefer old-school variance and challenge.");
            prDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            prDesc.setWrapText(true);

            // How to apply
            Label applyInfo = new Label(
                "Note: Changing the piece randomizer requires a game restart.\n" +
                "When you click Save in the settings page, the current game will reset with the selected system.");
            applyInfo.setStyle("-fx-font-size: 13px; -fx-text-fill: #AAAAAA;");
            applyInfo.setWrapText(true);

            rngContainer.getChildren().addAll(rngTitle, rngIntro, bagHeader, bagDesc, prHeader, prDesc, applyInfo);

            // Scoring help section
            VBox scoreContainer = new VBox(10);
            scoreContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10; -fx-padding: 20;");

            Label scoreTitle = new Label("Score System");
            scoreTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-alignment: center;");
            scoreTitle.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(scoreTitle, Priority.ALWAYS);

            Label lineScores = new Label(
                "Line Clears:\n" +
                "• Single (1 line): +100 pts\n" +
                "• Double (2 lines): +300 pts\n" +
                "• Triple (3 lines): +500 pts\n" +
                "• Tetris (4 lines): +800 pts");
            lineScores.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            lineScores.setWrapText(true);

            Label dropScores = new Label(
                "Drops:\n" +
                "• Soft Drop: +1 pt per row (accelerated)\n" +
                "• Hard Drop: +2 pts per row (instant)");
            dropScores.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            dropScores.setWrapText(true);

            HBox scoreRow = new HBox(40);
            scoreRow.setAlignment(Pos.TOP_LEFT);
            lineScores.setPrefWidth(300);
            dropScores.setPrefWidth(300);
            scoreRow.getChildren().addAll(lineScores, dropScores);

            scoreContainer.getChildren().addAll(scoreTitle, scoreRow);

            // Ghost Brick System section
            VBox ghostContainer = new VBox(10);
            ghostContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10; -fx-padding: 20;");

            Label ghostTitle = new Label("Ghost Brick System");
            ghostTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-alignment: center;");
            ghostTitle.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(ghostTitle, Priority.ALWAYS);

            Label ghostDesc = new Label(
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
            VBox endlessContainer = new VBox(10);
            endlessContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10; -fx-padding: 20;");

            Label endlessTitle = new Label("Endless Mode Rules");
            endlessTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-alignment: center;");
            endlessTitle.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(endlessTitle, Priority.ALWAYS);

            // Level Progression subsection
            Label levelProgTitle = new Label("Level Progression");
            levelProgTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
            Label levelProgText = new Label(
                "• Level increases by 1 for every 10 lines cleared\n" +
                "• Starting level: 1, Maximum level: 15\n" +
                "• Examples: 0-9 lines = Level 1, 10-19 lines = Level 2, ..., 140+ lines = Level 15");
            levelProgText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            levelProgText.setWrapText(true);

            // Speed Progression subsection
            Label speedTitle = new Label("Speed Progression");
            speedTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
            Label speedText = new Label(
                "• Speed multiplier increases every 2 levels\n" +
                "• Starting speed: 1x, Maximum speed: 8x\n" +
                "• Examples: Level 1-2 = 1x, Level 3-4 = 2x, ..., Level 15 = 8x");
            speedText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            speedText.setWrapText(true);

            endlessContainer.getChildren().addAll(endlessTitle, levelProgTitle, levelProgText, speedTitle, speedText);

            // Two-Player Mode Rules section
            VBox twoPlayerContainer = new VBox(12);
            twoPlayerContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10; -fx-padding: 20;");

            Label twoPlayerTitle = new Label("Two-Player Mode Rules");
            twoPlayerTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-alignment: center;");
            twoPlayerTitle.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(twoPlayerTitle, Priority.ALWAYS);

            // Objective and Winning Condition
            Label objectiveTitle = new Label("Objective & Winning Condition");
            objectiveTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
            Label objectiveText = new Label(
                "Clear lines to send garbage lines to your opponent. The last player standing wins!\n" +
                "The game ends when one player's board fills up. The player with the higher score wins!");
            objectiveText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            objectiveText.setWrapText(true);

            // Attack System
            Label attackTitle = new Label("Attack System");
            attackTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
            Label attackText = new Label(
                "• 1 line cleared: No attack (0 garbage lines)\n" +
                "• 2 lines cleared: Send 1 garbage line\n" +
                "• 3 lines cleared: Send 2 garbage lines\n" +
                "• 4 lines cleared (Tetris): Send 4 garbage lines");
            attackText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            attackText.setWrapText(true);

            // Combo Bonus
            Label comboTitle = new Label("Combo Bonus");
            comboTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
            Label comboText = new Label(
                "Build combos by clearing lines consecutively. Each combo above 1 eliminates 2 garbage lines from your board!\n" +
                "Example: Combo x3 = Eliminates 4 garbage lines (2 per combo above 1)");
            comboText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            comboText.setWrapText(true);

            // Garbage Lines
            Label garbageTitle = new Label("Garbage Lines");
            garbageTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
            Label garbageText = new Label("Garbage lines appear as gray blocks with one random hole. Clear them quickly or they'll stack up!");
            garbageText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            garbageText.setWrapText(true);

            // Special Features
            Label featuresTitle = new Label("Special Features");
            featuresTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
            Label featuresText = new Label(
                "• Countdown timer before game starts (3-2-1)\n" +
                "• Visual attack animations when receiving attacks\n" +
                "• Real-time statistics tracking (combo, attacks, defense)\n" +
                "• Sound effects for attacks and line clears");
            featuresText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            featuresText.setWrapText(true);

            // Strategy Tips
            Label strategyTitle = new Label("Strategy Tips");
            strategyTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
            Label strategyText = new Label(
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
            Button closeButton = new Button("Close");
            closeButton.setStyle("-fx-background-color: #4DFFFF; -fx-text-fill: #1A0033; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;");
            closeButton.setOnAction(e -> {
                // Play button click sound
                SoundManager.getInstance().playButtonClickSound();
                helpStage.close();
                // Restart countdown from beginning if it was running
                if (wasCountdownRunning && savedCountdownCallback != null) {
                    // Ensure game is not paused before restarting countdown
                    isPause.setValue(false);
                    // Restart countdown from beginning
                    showCountdown(savedCountdownCallback);
                } else if (!wasPaused) {
                    resumeFromOverlay();
                }
            });
            // Prevent initial focus from jumping to the bottom button
            closeButton.setFocusTraversable(false);

            // Also handle the window's X (close) button to resume if needed
            helpStage.setOnCloseRequest(e -> {
                // Play button click sound
                SoundManager.getInstance().playButtonClickSound();
                // Restart countdown from beginning if it was running
                if (wasCountdownRunning && savedCountdownCallback != null) {
                    // Ensure game is not paused before restarting countdown
                    isPause.setValue(false);
                    // Restart countdown from beginning
                    showCountdown(savedCountdownCallback);
                } else if (!wasPaused) {
                    resumeFromOverlay();
                }
            });
            
            // Create HBox for right-aligned close button
            HBox buttonContainer = new HBox();
            buttonContainer.setAlignment(Pos.CENTER_RIGHT);
            buttonContainer.getChildren().add(closeButton);
            
            // Add all components
            mainContainer.getChildren().addAll(titleLabel, modesContainer, basicsDual, rngContainer, scoreContainer, ghostContainer, endlessContainer, twoPlayerContainer, buttonContainer);
            scrollPane.setContent(mainContainer);
            
            // Create scene and show
            Scene helpScene = new Scene(scrollPane, 720, 560);
            // Reuse settings.css for visual consistency (scrollbar styling etc.)
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
     * Returns to main menu.
     * Properly cleans up all resources (timelines, timers) before returning.
     */
    @FXML
    public void returnToMenu() {
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        try {
            // Stop and clean up all timelines and timers
            cleanupAllTimelines();
            
            // Stop any running timelines for two-player mode
            if (eventListener instanceof TwoPlayerGameController) {
                TwoPlayerGameController controller = (TwoPlayerGameController) eventListener;
                controller.onQuitEvent(new MoveEvent(EventType.QUIT, EventSource.USER));
            }
            
            // Clear event listener reference
            eventListener = null;
            
            // Load main menu FXML
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainMenu.fxml"));
            Parent root = loader.load();
            
            // Get current stage - handle both single-player and two-player modes
            Stage stage;
            if (rootPane != null && rootPane.getScene() != null) {
                stage = (Stage) rootPane.getScene().getWindow();
            } else if (gamePanel != null && gamePanel.getScene() != null) {
                stage = (Stage) gamePanel.getScene().getWindow();
            } else {
                System.err.println("Cannot determine current stage");
                return;
            }
            
            Scene scene = new Scene(root, 900, 800);
            stage.setScene(scene);
            stage.setTitle("Tetris - Main Menu");
            // Center window on primary screen to handle multi-monitor setups
            centerWindowOnScreen(stage, 900, 800);
        } catch (Exception e) {
            System.err.println("Error loading main menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Returns to the level selection screen when in Level Mode.
     */
    @FXML
    public void returnToLevelSelection() {
        if (!isLevelMode) {
            returnToMenu();
            return;
        }

        SoundManager.getInstance().playButtonClickSound();
        
        // Stop level background music and play main menu music when returning to level selection
        SoundManager.getInstance().playMainMenuBackgroundMusic();

        try {
            hideLevelModeUI();
            cleanupAllTimelines();

            FXMLLoader levelSelectionLoader = new FXMLLoader(getClass().getClassLoader().getResource("levelSelection.fxml"));
            Parent levelSelectionRoot = levelSelectionLoader.load();

            Stage stage;
            if (rootPane != null && rootPane.getScene() != null) {
                stage = (Stage) rootPane.getScene().getWindow();
            } else if (gamePanel != null && gamePanel.getScene() != null) {
                stage = (Stage) gamePanel.getScene().getWindow();
            } else {
                System.err.println("Cannot determine current stage");
                return;
            }

            com.comp2042.controller.menu.LevelSelectionController controller = levelSelectionLoader.getController();
            if (controller != null) {
                controller.setStage(stage);
                controller.refreshData();
            }

            Scene scene = new Scene(levelSelectionRoot, 900, 800);
            stage.setScene(scene);
            stage.setTitle("Tetris - Level Selection");
            centerWindowOnScreen(stage, 900, 800);
        } catch (Exception e) {
            System.err.println("Error returning to level selection: " + e.getMessage());
            e.printStackTrace();
            returnToMenu();
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
    
    /**
     * Cleans up all timelines and timers to prevent memory leaks.
     * Should be called when returning to menu or closing the game.
     */
    private void cleanupAllTimelines() {
        // Stop and clear single-player timelines
        if (timeLine != null) {
            timeLine.stop();
            timeLine = null;
        }
        if (timeTimer != null) {
            timeTimer.stop();
            timeTimer = null;
        }
        if (levelTimer != null) {
            levelTimer.stop();
            levelTimer = null;
        }
        if (countdownManager != null) {
            countdownManager.cancelCountdown();
        }
    }
    
    
    /**
     * Shows the Endless Mode Game Over screen.
     * Displays final score, leaderboard, and options to retry or return to menu.
     * 
     * @param finalScore the final score achieved
     * @param linesCleared the number of lines cleared
     * @param playTimeMs the play time in milliseconds
     * @param isNewHighScore whether this is a new high score
     * @param rank the player's rank (1-5), or 0 if not in top 5
     */
    
    /**
     * Checks if the current game is in Endless Mode.
     * 
     * @return true if in Endless Mode, false otherwise
     */
    public boolean isEndlessMode() {
        return isEndlessMode;
    }
    
    /**
     * Sets the current game mode.
     * 
     * @param endlessMode true for Endless Mode, false for other modes
     */
    public void setEndlessMode(boolean endlessMode) {
        this.isEndlessMode = endlessMode;
        if (endlessMode) {
            this.gameStartTime = System.currentTimeMillis();
            startTimeTimer();
            resetGameTitle();
            if (backToSelectionButton != null) {
                backToSelectionButton.setVisible(false);
                backToSelectionButton.setManaged(false);
            }
            // Initialize endless progression UI
            endlessLevel = 1;
            endlessLinesClearedUI = 0;
            updateLevel(endlessLevel);
            updateSpeed(getSpeedDisplayForLevel(endlessLevel));
            updateGameSpeed(getDropMsForLevel(endlessLevel));
        }
    }
    
    /**
     * This method loads the endless game over FXML and switches to it.
     * 
     * @param board the game board containing final game data
     */
    public void showEndlessGameOverScene(Board board) {
        if (!isEndlessMode) {
            return; 
        }
        
        try {
            // Get final game data from board
            int finalScore = board.getScore().getScore();
            int linesCleared = board.getTotalLinesCleared();
            
            // Calculate actual play time
            long playTimeMs = 60000; // Default fallback
            if (gameStartTime > 0) {
                playTimeMs = System.currentTimeMillis() - gameStartTime;
            }
            
            // Get leaderboard instance and add entry to get rank
            com.comp2042.model.mode.EndlessModeLeaderboard leaderboard = 
                com.comp2042.model.mode.EndlessModeLeaderboard.getInstance();
            
            // Add entry to leaderboard and get rank (1-5 if in top 5, 0 otherwise)
            int rank = leaderboard.addEntry(finalScore, linesCleared, playTimeMs, getCurrentLevel());
            
            // Play appropriate sound effect based on whether entry is in top 5
            if (rank > 0 && rank <= 5) {
                SoundManager.getInstance().playEndlessNewRecordSound();
            } else {
                SoundManager.getInstance().playEndlessGameOverSound();
            }
            
            // Check if this is a new high score for display purposes
            boolean isNewHighScore = leaderboard.isNewHighScore(finalScore);
            
            // Load the endless game over FXML
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("endlessGameOver.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set up callbacks
            EndlessGameOverController controller = loader.getController();
            // Capture current stage for reliable scene switching across retries
            final Stage stageForCallbacks = (Stage) gamePanel.getScene().getWindow();
            controller.setOnTryAgain(() -> {
                // Start a new Endless Mode game (same as clicking Endless Mode button)
                try {
                    // Use dependency injection: create Board explicitly and inject it
                    com.comp2042.model.board.Board newBoard = new com.comp2042.model.board.SimpleBoard(10, 20);
                    com.comp2042.service.gameloop.GameService gameService = new com.comp2042.service.gameloop.GameServiceImpl(newBoard);
                    GameViewController newGuiController = new GameViewController();
                    var gameMode = com.comp2042.controller.factory.GameModeFactory.createGameMode(com.comp2042.controller.factory.GameModeType.ENDLESS, gameService, newGuiController);
                    gameMode.initialize();
                    newGuiController.setEndlessMode(true);
                    
                    // Load the new game scene
                    FXMLLoader gameLoader = new FXMLLoader(getClass().getClassLoader().getResource("enhancedGameLayout.fxml"));
                    Parent gameRoot = gameLoader.load();
                    GameViewController gameController = gameLoader.getController();
                    if (gameController == null) {
                        gameController = newGuiController;
                    } else {
                        gameController.setEndlessMode(true);
                    }
                    
                    Scene gameScene = new Scene(gameRoot, 900, 800);
                    if (stageForCallbacks != null) {
                        stageForCallbacks.setScene(gameScene);
                        stageForCallbacks.setTitle("Tetris - Game");
                    } else {
                        System.err.println("Current stage is null, cannot switch to game scene");
                    }
                    
                    new GameController(gameController);
                } catch (Exception e) {
                    System.err.println("Error starting new Endless Mode: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            controller.setOnResetLeaderboard(() -> {
                // Clear the leaderboard and refresh the display
                try {
                    com.comp2042.model.mode.EndlessModeLeaderboard lb = 
                        com.comp2042.model.mode.EndlessModeLeaderboard.getInstance();
                    lb.clearLeaderboard();
                    
                    // Refresh the leaderboard display by recreating the entries
                    controller.refreshLeaderboard();
                } catch (Exception e) {
                    System.err.println("Error clearing leaderboard: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            controller.setOnBackToMenu(() -> {
                // Return to main menu
                try {
                    FXMLLoader menuLoader = new FXMLLoader(getClass().getClassLoader().getResource("mainMenu.fxml"));
                    Parent menuRoot = menuLoader.load();
                    Scene menuScene = new Scene(menuRoot, 900, 800);
                    if (stageForCallbacks != null) {
                        stageForCallbacks.setScene(menuScene);
                        stageForCallbacks.setTitle("Tetris - Main Menu");
                        // Center window on primary screen to handle multi-monitor setups
                        centerWindowOnScreen(stageForCallbacks, 900, 800);
                    } else {
                        System.err.println("Current stage is null, cannot switch to menu scene");
                    }
                } catch (Exception e) {
                    System.err.println("Error loading main menu: " + e.getMessage());
                }
            });
            
            // Show the game over data
            controller.showGameOver(finalScore, linesCleared, playTimeMs, isNewHighScore, rank);
            
            // Create new scene
            Scene gameOverScene = new Scene(root, 900, 800);
            
            // Load CSS stylesheet
            try {
                String cssPath = getClass().getClassLoader().getResource("endlessGameOverStyle.css").toExternalForm();
                if (cssPath != null) {
                    gameOverScene.getStylesheets().add(cssPath);
                }
            } catch (Exception e) {
                System.err.println("Error loading CSS: " + e.getMessage());
            }
            
            // Set up keyboard handling
            gameOverScene.setOnKeyPressed(event -> controller.handleKeyPress(event));
            
            // Get current stage and switch scene
            Stage stage = (Stage) gamePanel.getScene().getWindow();
            if (stage != null) {
                stage.setScene(gameOverScene);
                stage.setTitle("Tetris - Game Over");
            } else {
                System.err.println("Stage is null, cannot switch to game over scene");
            }
            
            // Stop the game timeline
            if (timeLine != null) {
                timeLine.stop();
            }
            isGameOver.setValue(true);
            isPause.setValue(false);
            
        } catch (Exception e) {
            System.err.println("Error loading Endless Game Over scene: " + e.getMessage());
            e.printStackTrace();
            // Fallback to regular game over
            gameOver();
        }
    }
    
    /**
     * Shows the Level Mode Game Over scene.
     * Called when a level is completed or failed.
     *
     * @param board the game board instance
     * @param newRecords array containing [isNewBestScore, isNewBestTime], or null if not available
     */
    public void showLevelGameOverScene(Board board, boolean[] newRecords) {
        if (!isLevelMode) {
            return;
        }
        
        // Play appropriate sound effect based on success/failure
            com.comp2042.model.mode.LevelManager levelManager = com.comp2042.model.mode.LevelManager.getInstance();
            com.comp2042.model.mode.LevelMode currentLevel = levelManager.getCurrentLevel();
        if (currentLevel != null && board.getTotalLinesCleared() >= currentLevel.getTargetLines()) {
            // Level completed successfully
            SoundManager.getInstance().playLevelWinSound();
        } else {
            // Level failed
            SoundManager.getInstance().playLevelFailedSound();
        }
        
        try {
            // Get level data from LevelManager (already retrieved above)
            
            if (currentLevel == null) {
                System.err.println("No current level found");
                gameOver();
                return;
            }
            
            // Get final game data
            int finalScore = board.getScore().getScore();
            int linesCleared = board.getTotalLinesCleared(); // Get from board
            int targetLines = currentLevel.getTargetLines();
            
            // Calculate actual play time from level start time
            long playTimeMs = 0;
            if (levelStartTime > 0) {
                playTimeMs = System.currentTimeMillis() - levelStartTime;
            }
            
            // Get stars and success status
            int completionTimeSeconds = (int) (playTimeMs / 1000);
            boolean success = linesCleared >= targetLines;
            
            // Level is already completed in PlayingState, which updates best stats and unlocks next level
            final com.comp2042.model.mode.LevelMode finalCurrentLevel = levelManager.getCurrentLevel();
            
            int stars = finalCurrentLevel != null ? finalCurrentLevel.calculateStars(finalScore, linesCleared, completionTimeSeconds, success) : 0;
            
            // Use provided new record status, or determine from current level stats if not provided
            boolean isNewBestScore = false;
            boolean isNewBestTime = false;
            if (newRecords != null && newRecords.length >= 2) {
                isNewBestScore = newRecords[0];
                isNewBestTime = newRecords[1];
            } else if (finalCurrentLevel != null) {
                // Fallback: determine from current stats (may have false positives)
                isNewBestScore = finalScore > 0 && finalScore == finalCurrentLevel.getBestScore();
                isNewBestTime = success && finalCurrentLevel.getBestTime() != Long.MAX_VALUE && 
                    playTimeMs > 0 && playTimeMs == finalCurrentLevel.getBestTime();
            }
            
            // Load the level game over FXML
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("levelGameOver.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set up callbacks
            LevelGameOverController controller = loader.getController();
            final Stage stageForCallbacks = (Stage) gamePanel.getScene().getWindow();
            
            controller.setOnTryAgain(() -> {
                // Reload the same level
                try {
                    // First load level selection screen
                    FXMLLoader levelSelectionLoader = new FXMLLoader(getClass().getClassLoader().getResource("levelSelection.fxml"));
                    Parent levelSelectionRoot = levelSelectionLoader.load();
                    com.comp2042.controller.menu.LevelSelectionController levelSelectionController = levelSelectionLoader.getController();
                    levelSelectionController.setStage(stageForCallbacks);
                    
                    // Create scene and set it
                    Scene levelSelectionScene = new Scene(levelSelectionRoot, 900, 800);
                    if (stageForCallbacks != null) {
                        stageForCallbacks.setScene(levelSelectionScene);
                        stageForCallbacks.setTitle("Tetris - Level Selection");
                    }
                    
                    // Then load the level
                    levelSelectionController.handleLevelSelect(finalCurrentLevel);
                } catch (Exception e) {
                    System.err.println("Error restarting level: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            
            controller.setOnNextLevel(() -> {
                // Load next level if available
                com.comp2042.model.mode.LevelMode nextLevel = levelManager.getLevel(finalCurrentLevel.getLevelId() + 1);
                if (nextLevel != null && nextLevel.isUnlocked()) {
                    try {
                        // First load level selection screen
                        FXMLLoader levelSelectionLoader = new FXMLLoader(getClass().getClassLoader().getResource("levelSelection.fxml"));
                        Parent levelSelectionRoot = levelSelectionLoader.load();
                        com.comp2042.controller.menu.LevelSelectionController levelSelectionController = levelSelectionLoader.getController();
                        levelSelectionController.setStage(stageForCallbacks);
                        
                        // Create scene and set it
                        Scene levelSelectionScene = new Scene(levelSelectionRoot, 900, 800);
                        if (stageForCallbacks != null) {
                            stageForCallbacks.setScene(levelSelectionScene);
                            stageForCallbacks.setTitle("Tetris - Level Selection");
                        }
                        
                        // Then load the next level
                        levelSelectionController.handleLevelSelect(nextLevel);
                    } catch (Exception e) {
                        System.err.println("Error loading next level: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    // Next level not available or locked - return to level selection
                    try {
                        FXMLLoader levelSelectionLoader = new FXMLLoader(getClass().getClassLoader().getResource("levelSelection.fxml"));
                        Parent levelSelectionRoot = levelSelectionLoader.load();
                        com.comp2042.controller.menu.LevelSelectionController levelSelectionController = levelSelectionLoader.getController();
                        levelSelectionController.setStage(stageForCallbacks);
                        
                        Scene levelSelectionScene = new Scene(levelSelectionRoot, 900, 800);
                        if (stageForCallbacks != null) {
                            stageForCallbacks.setScene(levelSelectionScene);
                            stageForCallbacks.setTitle("Tetris - Level Selection");
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading level selection: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
            
            controller.setOnBackToSelection(() -> {
                // Stop level background music and play main menu music when returning to level selection
                SoundManager.getInstance().playMainMenuBackgroundMusic();
                
                // Return to level selection (to show updated stars and unlocked levels)
                try {
                    FXMLLoader levelSelectionLoader = new FXMLLoader(getClass().getClassLoader().getResource("levelSelection.fxml"));
                    Parent levelSelectionRoot = levelSelectionLoader.load();
                    com.comp2042.controller.menu.LevelSelectionController levelSelectionController = levelSelectionLoader.getController();
                    levelSelectionController.setStage(stageForCallbacks);
                    
                    // Refresh data to show updated stars and unlocked levels
                    levelSelectionController.refreshData();
                    
                    Scene levelSelectionScene = new Scene(levelSelectionRoot, 900, 800);
                    if (stageForCallbacks != null) {
                        stageForCallbacks.setScene(levelSelectionScene);
                        stageForCallbacks.setTitle("Tetris - Level Selection");
                    }
                } catch (Exception e) {
                    System.err.println("Error loading level selection: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            
            controller.setOnBackToMenu(() -> {
                // Return to main menu
                try {
                    FXMLLoader menuLoader = new FXMLLoader(getClass().getClassLoader().getResource("mainMenu.fxml"));
                    Parent menuRoot = menuLoader.load();
                    com.comp2042.controller.menu.MainMenuController menuController = menuLoader.getController();
                    
                    Scene menuScene = new Scene(menuRoot, 900, 800);
                    if (stageForCallbacks != null) {
                        stageForCallbacks.setScene(menuScene);
                        stageForCallbacks.setTitle("Tetris - Main Menu");
                        // Center window on primary screen to handle multi-monitor setups
                        centerWindowOnScreen(stageForCallbacks, 900, 800);
                    }
                } catch (Exception e) {
                    System.err.println("Error loading main menu: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            
            // Show the game over data
            controller.showGameOver(finalScore, linesCleared, targetLines, playTimeMs,
                    stars, success, finalCurrentLevel.getLevelId(),
                    isNewBestScore, isNewBestTime);
            
            // Create new scene
            Scene gameOverScene = new Scene(root, 900, 800);
            
            // Load CSS stylesheet
            try {
                String cssPath = getClass().getClassLoader().getResource("levelGameOverStyle.css").toExternalForm();
                if (cssPath != null) {
                    gameOverScene.getStylesheets().add(cssPath);
                }
            } catch (Exception e) {
                System.err.println("Error loading CSS: " + e.getMessage());
            }
            
            // Get current stage and switch scene
            Stage stage = (Stage) gamePanel.getScene().getWindow();
            if (stage != null) {
                stage.setScene(gameOverScene);
                stage.setTitle("Tetris - Level Complete");
            }
            
            // Stop the game timeline
            if (timeLine != null) {
                timeLine.stop();
            }
            if (levelTimer != null) {
                levelTimer.stop();
            }
            isGameOver.setValue(true);
            isPause.setValue(false);
            
        } catch (Exception e) {
            System.err.println("Error loading Level Game Over scene: " + e.getMessage());
            e.printStackTrace();
            // Fallback to regular game over
            gameOver();
        }
    }
    
    // Helper: build a bullet column with consistent wrapping and spacing for Help dialog
    private VBox createBulletedColumn(String[] items, double width) {
        VBox box = new VBox(6);
        for (String text : items) {
            Label lbl = new Label("• " + text);
            lbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            lbl.setWrapText(true);
            lbl.setPrefWidth(width);
            box.getChildren().add(lbl);
        }
        return box;
    }
    
    // ============================================
    // Two-Player Mode Components and Methods
    // ============================================
    
    // Player 1 FXML components (for two-player layout)
    @FXML
    private GridPane gamePanel1;
    @FXML
    private Pane brickPanel1;
    @FXML
    private Pane ghostPanel1;
    @FXML
    private GridPane holdPanel1;
    @FXML
    private GridPane nextBrickPanel1;
    @FXML
    private Group groupNotification1;
    @FXML
    private GameOverPanel gameOverPanel1;
    @FXML
    private Label player1ScoreLabel;
    @FXML
    private Label player1LinesLabel;
    @FXML
    private Label player1LevelLabel;
    @FXML
    private Label player1ComboLabel;
    @FXML
    private Label player1AttackLabel;
    @FXML
    private Label player1DefenseLabel;
    @FXML
    private Label player1TetrisLabel;
    @FXML
    private Label player1TimeLabel;
    
    // Player 2 FXML components (for two-player layout)
    @FXML
    private GridPane gamePanel2;
    @FXML
    private Pane brickPanel2;
    @FXML
    private Pane ghostPanel2;
    @FXML
    private GridPane holdPanel2;
    @FXML
    private GridPane nextBrickPanel2;
    @FXML
    private Group groupNotification2;
    @FXML
    private GameOverPanel gameOverPanel2;
    @FXML
    private Label player2ScoreLabel;
    @FXML
    private Label player2LinesLabel;
    @FXML
    private Label player2LevelLabel;
    @FXML
    private Label player2ComboLabel;
    @FXML
    private Label player2AttackLabel;
    @FXML
    private Label player2DefenseLabel;
    @FXML
    private Label player2TetrisLabel;
    @FXML
    private Label player2TimeLabel;
    @FXML
    private Label vsLabel;
    
    @FXML
    private TwoPlayerGameOverPanel twoPlayerGameOverPanel;
    
    // Background board panes for precise centering
    @FXML
    private Pane boardBackground1;
    @FXML
    private Pane boardBackground2;
    
    /**
     * Initializes Player 1's game view for two-player mode.
     * Sets up the visual representation of Player 1's board and falling brick.
     * 
     * @param boardMatrix the initial state of Player 1's game board matrix
     * @param brick the initial view data for Player 1's falling brick
     */
    public void initPlayer1View(int[][] boardMatrix, ViewData brick) {
        if (twoPlayerPanelManager != null) {
            twoPlayerPanelManager.initPlayer1View(boardMatrix, brick);
        }
    }
    
    /**
     * Initializes Player 2's game view for two-player mode.
     * Sets up the visual representation of Player 2's board and falling brick.
     * 
     * @param boardMatrix the initial state of Player 2's game board matrix
     * @param brick the initial view data for Player 2's falling brick
     */
    public void initPlayer2View(int[][] boardMatrix, ViewData brick) {
        if (twoPlayerPanelManager != null) {
            twoPlayerPanelManager.initPlayer2View(boardMatrix, brick);
        }
    }
    
    /**
     * Refreshes Player 1's brick display based on updated view data.
     * Updates the position and shape of Player 1's falling brick.
     * 
     * @param brick the updated view data for Player 1's brick
     */
    public void refreshPlayer1Brick(ViewData brick) {
        if (twoPlayerPanelManager != null) {
            twoPlayerPanelManager.refreshPlayer1Brick(brick);
        }
    }
    
    /**
     * Refreshes Player 2's brick display based on updated view data.
     * Updates the position and shape of Player 2's falling brick.
     * 
     * @param brick the updated view data for Player 2's brick
     */
    public void refreshPlayer2Brick(ViewData brick) {
        if (twoPlayerPanelManager != null) {
            twoPlayerPanelManager.refreshPlayer2Brick(brick);
        }
    }
    
    /**
     * Updates Player 1's ghost brick display based on the current brick position and shape.
     * 
     * @param brick The ViewData containing the current brick shape and ghost position.
     */
    private void updatePlayer1GhostBrick(ViewData brick) {
        if (twoPlayerPanelManager != null) {
            twoPlayerPanelManager.updatePlayer1GhostBrick(brick);
        }
    }
    
    /**
     * Updates Player 2's ghost brick display based on the current brick position and shape.
     * 
     * @param brick The ViewData containing the current brick shape and ghost position.
     */
    private void updatePlayer2GhostBrick(ViewData brick) {
        if (twoPlayerPanelManager != null) {
            twoPlayerPanelManager.updatePlayer2GhostBrick(brick);
        }
    }
    
    /**
     * Handles Player 1's down event (brick landing).
     * Updates the board display and handles row clearing animations.
     * 
     * @param downData the down event data containing row clearing information
     */
    public void handlePlayer1DownEvent(DownData downData) {
        if (downData == null) {
            return;
        }
        
        refreshPlayer1Brick(downData.getViewData());
        
        // Update board background
        if (eventListener instanceof TwoPlayerGameController) {
            TwoPlayerGameController controller = (TwoPlayerGameController) eventListener;
            refreshGameBackground1(controller.getPlayer1Service().getBoard().getBoardMatrix());
            
            // Update score after each move
            updatePlayerScores(controller);
            
            // Check for game over - if brick landed and game is over
            if (downData.isBrickLanded() && controller.getPlayer1Service().isGameOver()) {
                controller.checkGameOver();
            }
        }
        
        // Handle row clearing notification
        if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
            NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
            if (groupNotification1 != null) {
                groupNotification1.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification1.getChildren());
            }
        }
        
        // Check for game over
        if (eventListener instanceof TwoPlayerGameController) {
            TwoPlayerGameController controller = (TwoPlayerGameController) eventListener;
            controller.checkGameOver();
        }
    }
    
    /**
     * Handles Player 2's down event (brick landing).
     * Updates the board display and handles row clearing animations.
     * 
     * @param downData the down event data containing row clearing information
     */
    public void handlePlayer2DownEvent(DownData downData) {
        if (downData == null) {
            return;
        }
        
        refreshPlayer2Brick(downData.getViewData());
        
        // Update board background
        if (eventListener instanceof TwoPlayerGameController) {
            TwoPlayerGameController controller = (TwoPlayerGameController) eventListener;
            refreshGameBackground2(controller.getPlayer2Service().getBoard().getBoardMatrix());
            
            // Update score after each move
            updatePlayerScores(controller);
            
            // Check for game over - if brick landed and game is over
            if (downData.isBrickLanded() && controller.getPlayer2Service().isGameOver()) {
                controller.checkGameOver();
            }
        }
        
        // Handle row clearing notification
        if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
            NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
            if (groupNotification2 != null) {
                groupNotification2.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification2.getChildren());
            }
        }
        
        // Check for game over
        if (eventListener instanceof TwoPlayerGameController) {
            TwoPlayerGameController controller = (TwoPlayerGameController) eventListener;
            controller.checkGameOver();
        }
    }
    
    
    
    /**
     * Refreshes Player 1's game board background display.
     * PERFORMANCE OPTIMIZATION: Only updates cells that have actually changed.
     * 
     * @param board the updated board matrix
     */
    public void refreshGameBackground1(int[][] board) {
        if (twoPlayerPanelManager != null) {
            twoPlayerPanelManager.refreshGameBackground1(board);
        }
    }
    
    /**
     * Refreshes Player 2's game board background display.
     * PERFORMANCE OPTIMIZATION: Only updates cells that have actually changed.
     * 
     * @param board the updated board matrix
     */
    public void refreshGameBackground2(int[][] board) {
        if (twoPlayerPanelManager != null) {
            twoPlayerPanelManager.refreshGameBackground2(board);
        }
    }
    
    /**
     * Updates Player 1's next piece display.
     * 
     * @param nextBrickData the next brick shape data
     */
    public void updatePlayer1NextDisplay(int[][] nextBrickData) {
        if (twoPlayerPanelManager != null) {
            twoPlayerPanelManager.updatePlayer1NextDisplay(nextBrickData);
        }
    }
    
    /**
     * Updates Player 2's next piece display.
     * 
     * @param nextBrickData the next brick shape data
     */
    public void updatePlayer2NextDisplay(int[][] nextBrickData) {
        if (twoPlayerPanelManager != null) {
            twoPlayerPanelManager.updatePlayer2NextDisplay(nextBrickData);
        }
    }
    
    /**
     * Updates Player 1's hold piece display.
     * 
     * @param holdBrickData the held brick shape data
     */
    public void updatePlayer1HoldDisplay(int[][] holdBrickData) {
        if (twoPlayerPanelManager != null) {
            twoPlayerPanelManager.updatePlayer1HoldDisplay(holdBrickData);
        }
    }
    
    /**
     * Updates Player 2's hold piece display.
     * 
     * @param holdBrickData the held brick shape data
     */
    public void updatePlayer2HoldDisplay(int[][] holdBrickData) {
        if (twoPlayerPanelManager != null) {
            twoPlayerPanelManager.updatePlayer2HoldDisplay(holdBrickData);
        }
    }
    
    /**
     * Updates Player 1's score display.
     * 
     * @param score the new score value
     */
    public void updatePlayer1Score(int score) {
        if (twoPlayerPanelManager != null) {
            twoPlayerPanelManager.updatePlayer1Score(score);
        }
    }
    
    /**
     * Updates Player 2's score display.
     * 
     * @param score the new score value
     */
    public void updatePlayer2Score(int score) {
        if (twoPlayerPanelManager != null) {
            twoPlayerPanelManager.updatePlayer2Score(score);
        }
    }
    
    /**
     * Updates scores for both players (helper method for two-player mode).
     * 
     * @param controller the TwoPlayerGameController instance
     */
    private void updatePlayerScores(TwoPlayerGameController controller) {
        if (controller == null) {
            return;
        }
            int player1Score = controller.getPlayer1Service().getScore().getScore();
            int player2Score = controller.getPlayer2Service().getScore().getScore();
            updatePlayer1Score(player1Score);
            updatePlayer2Score(player2Score);
    }
    
    /**
     * Updates player statistics display for two-player mode.
     * 
     * @param player the player number (1 or 2)
     * @param stats the PlayerStats instance containing the statistics
     */
    public void updatePlayerStats(int player, com.comp2042.model.mode.PlayerStats stats) {
        if (twoPlayerPanelManager != null) {
            twoPlayerPanelManager.updatePlayerStats(player, stats);
        }
    }
    
    /**
     * Clears all panels for Player 1 (game board, hold, next).
     * Used when starting a new game in two-player mode.
     */
    private void clearPlayer1Panels() {
        if (twoPlayerPanelManager != null) {
            twoPlayerPanelManager.clearPlayer1Panels();
        }
    }
    
    /**
     * Clears all panels for Player 2 (game board, hold, next).
     * Used when starting a new game in two-player mode.
     */
    private void clearPlayer2Panels() {
        if (twoPlayerPanelManager != null) {
            twoPlayerPanelManager.clearPlayer2Panels();
        }
    }
    
    /**
     * Shows the game over screen for two-player mode.
     * Displays the winner, detailed statistics, and action buttons.
     * 
     * @param winner the winner (1 for Player 1, 2 for Player 2, 0 for tie)
     * @param player1Score Player 1's final score
     * @param player2Score Player 2's final score
     */
    public void showTwoPlayerGameOver(int winner, int player1Score, int player2Score) {
        com.comp2042.model.mode.PlayerStats player1Stats = null;
        com.comp2042.model.mode.PlayerStats player2Stats = null;
        
        if (eventListener instanceof TwoPlayerGameController) {
            TwoPlayerGameController controller = (TwoPlayerGameController) eventListener;
            if (controller.getGameMode() instanceof com.comp2042.model.mode.TwoPlayerMode) {
                com.comp2042.model.mode.TwoPlayerMode gameMode = 
                    (com.comp2042.model.mode.TwoPlayerMode) controller.getGameMode();
                player1Stats = gameMode.getPlayer1Stats();
                player2Stats = gameMode.getPlayer2Stats();
            }
        }
        
        if (twoPlayerPanelManager != null) {
            final com.comp2042.model.mode.PlayerStats finalPlayer1Stats = player1Stats;
            final com.comp2042.model.mode.PlayerStats finalPlayer2Stats = player2Stats;
            twoPlayerPanelManager.showTwoPlayerGameOver(
                winner,
                player1Score,
                player2Score,
                finalPlayer1Stats,
                finalPlayer2Stats,
                () -> newGame(null),
                this::returnToMenu
            );
        }
        
        isGameOver.setValue(true);
    }
    
    /**
     * Gets the root pane for keyboard focus management.
     * 
     * @return the root BorderPane
     */
    public BorderPane getRootPane() {
        return rootPane;
    }
    
    /**
     * Shows a countdown (3-2-1) before starting the game.
     * Displays countdown on each player's game panel.
     * 
     * @param onComplete callback to execute when countdown completes
     */
    public void showCountdown(Runnable onComplete) {
        if (!isTwoPlayerMode || countdownManager == null) {
            if (onComplete != null) {
                            onComplete.run();
            }
            return;
        }
        
        countdownManager.cancelCountdown();

        if (eventListener instanceof TwoPlayerGameController) {
            TwoPlayerGameController controller = (TwoPlayerGameController) eventListener;
            try {
                java.lang.reflect.Field player1TimelineField = TwoPlayerGameController.class.getDeclaredField("player1Timeline");
                java.lang.reflect.Field player2TimelineField = TwoPlayerGameController.class.getDeclaredField("player2Timeline");
                java.lang.reflect.Field statsUpdateTimelineField = TwoPlayerGameController.class.getDeclaredField("statsUpdateTimeline");
                player1TimelineField.setAccessible(true);
                player2TimelineField.setAccessible(true);
                statsUpdateTimelineField.setAccessible(true);
                
                Timeline player1Timeline = (Timeline) player1TimelineField.get(controller);
                Timeline player2Timeline = (Timeline) player2TimelineField.get(controller);
                Timeline statsUpdateTimeline = (Timeline) statsUpdateTimelineField.get(controller);
                
                if (player1Timeline != null) {
                    player1Timeline.stop();
                }
                if (player2Timeline != null) {
                    player2Timeline.stop();
                }
                if (statsUpdateTimeline != null) {
                    statsUpdateTimeline.stop();
                }
            } catch (Exception e) {
                System.err.println("Failed to stop timelines during countdown: " + e.getMessage());
            }
        }
        
        countdownManager.showTwoPlayerCountdown(
            rootPane,
            gamePanel1,
            gamePanel2,
            boardBackground1,
            boardBackground2,
            onComplete
        );
    }
    
    /**
     * Shows an enhanced attack animation on the specified player's board.
     * Includes shockwave, screen shake, and flash effects.
     * 
     * @param player the player number (1 or 2)
     * @param attackPower the number of lines being attacked
     */
    public void showAttackAnimation(int player, int attackPower) {
        if (twoPlayerPanelManager != null) {
            twoPlayerPanelManager.showAttackAnimation(player, attackPower);
        }
    }
    
    /**
     * Shows a combo bonus message.
     * 
     * @param player the player number (1 or 2)
     * @param combo the combo count
     * @param linesEliminated the number of lines eliminated
     */
    public void showComboBonus(int player, int combo, int linesEliminated) {
        if (twoPlayerPanelManager != null) {
            twoPlayerPanelManager.showComboBonus(player, combo, linesEliminated);
        }
    }
}