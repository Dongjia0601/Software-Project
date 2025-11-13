package com.comp2042;

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
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the JavaFX GUI components.
 * 
 * <p>This class manages all user interface interactions for the Tetris game, including
 * game board rendering, brick display, keyboard input handling, animations, and visual
 * effects. It serves as the bridge between the game logic and the JavaFX UI framework.</p>
 * 
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Initialize and manage JavaFX UI components</li>
 *   <li>Render game board and falling bricks</li>
 *   <li>Handle keyboard input and user interactions</li>
 *   <li>Manage animations and visual effects</li>
 *   <li>Update display based on game state changes</li>
 * </ul>
 */
public class GuiController implements Initializable {

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
    private boolean isMuted = false;
    private double currentBrickOpacity = 1.0;
    private boolean ghostEnabled = true; // Whether ghost piece should be displayed

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
    private double previousVolume = 0.7; // Default volume
    
    // Time tracking (exclude paused duration)
    private long totalPausedMillis = 0L;
    private long lastPauseStartMillis = 0L;
    
    // Endless mode progression tracking
    private int endlessLevel = 1;
    private int endlessLinesClearedUI = 0;
    
    // Countdown tracking
    private Timeline countdownTimeline = null;
    private Runnable countdownCallback = null;
    private StackPane countdownOverlay1 = null;
    private StackPane countdownOverlay2 = null;
    private Pane countdownParent1 = null;
    private Pane countdownParent2 = null;

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
        if (countdownTimeline != null) {
            countdownTimeline.stop();
            countdownTimeline = null;
            // Stop countdown sound when countdown is stopped
            com.comp2042.SoundManager.getInstance().stopCountdownSound();
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
            
            isPause.setValue(false);
            isGameOver.setValue(false);
            
            // Clear old event listener before creating new controller
            eventListener = null;
            
            // Recreate VS mode with new randomizer settings
            com.comp2042.core.GameService player1Service = new com.comp2042.core.GameServiceImpl();
            com.comp2042.core.GameService player2Service = new com.comp2042.core.GameServiceImpl();
            com.comp2042.game.TwoPlayerVSGameMode newGameMode = 
                new com.comp2042.game.TwoPlayerVSGameMode(player1Service, player2Service, this);
            
            // Create new two-player controller which will automatically show countdown and start game
            new TwoPlayerGameController(newGameMode, this);
            
            // Request focus for keyboard input
            if (rootPane != null) {
                rootPane.requestFocus();
            }
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
            
            // Show countdown before starting new game
            showCountdown(() -> {
                // After countdown, start the new game
                controller.onNewGameEvent(new MoveEvent(EventType.NEW_GAME, EventSource.USER));
                
                // Request focus for keyboard input
                if (rootPane != null) {
                    rootPane.requestFocus();
                }
            });
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
            com.comp2042.game.LevelManager levelManager = com.comp2042.game.LevelManager.getInstance();
            com.comp2042.game.LevelMode currentLevel = levelManager.getCurrentLevel();
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
        if (leftProgressLabel != null) {
            String progressText = String.format("%d/%d", linesClearedInLevel, targetLines);
            leftProgressLabel.setText(progressText);
            
            // Change color based on progress (large font size)
            double progress = (double) linesClearedInLevel / targetLines;
            if (progress >= 1.0) {
                leftProgressLabel.setStyle("-fx-text-fill: #00FF00; -fx-font-weight: bold; -fx-font-size: 32px;");
            } else if (progress >= 0.75) {
                leftProgressLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold; -fx-font-size: 32px;");
            } else {
                leftProgressLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 32px; -fx-text-fill: #FF0000;");
            }
        }
    }

    public void updateStarDisplay(int stars) {
        if (leftStarDisplay != null) {
            leftStarDisplay.getChildren().clear();
            
            for (int i = 0; i < 3; i++) {
                Label star = new Label(i < stars ? "★" : "☆");
                star.setStyle("-fx-font-size: 28px; " + 
                             (i < stars ? "-fx-text-fill: #FFD700;" : "-fx-text-fill: #666666;"));
                leftStarDisplay.getChildren().add(star);
            }
        }
    }

    public void updateLevelSpeedDisplay(int levelId) {
        if (leftSpeedLabel != null) {
            // Calculate speed based on level ID (simple display)
            double speed = 1.0 + (levelId - 1) * 0.2;
            leftSpeedLabel.setText(String.format("%.1fx", speed));
            // Set large font size to match other objective labels
            leftSpeedLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");
        }
    }

    public void updateBestStats(int bestScore, long bestTimeMillis) {
        if (bestScoreLabel != null) {
            bestScoreLabel.setText("Best Score: " + bestScore);
        }
        
        if (bestTimeLabel != null) {
            if (bestTimeMillis == Long.MAX_VALUE || bestTimeMillis <= 0) {
                bestTimeLabel.setText("Best Time: --:--");
            } else {
                int bestTimeSeconds = (int) (bestTimeMillis / 1000);
                int minutes = bestTimeSeconds / 60;
                int seconds = bestTimeSeconds % 60;
                String timeText = String.format("Best Time: %d:%02d", minutes, seconds);
                bestTimeLabel.setText(timeText);
            }
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
        if (leftTimerLabel == null) {
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
                    leftTimerLabel.setText("0:00");
                    leftTimerLabel.setStyle("-fx-text-fill: #FF0000; -fx-font-weight: bold; -fx-font-size: 32px;");
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
        if (leftTimerLabel != null) {
            int minutes = levelTimeRemainingSeconds / 60;
            int seconds = levelTimeRemainingSeconds % 60;
            leftTimerLabel.setText(String.format("%d:%02d", minutes, seconds));
            
            // Change color if time is running out (large font size)
            if (levelTimeRemainingSeconds <= 30) {
                leftTimerLabel.setStyle("-fx-text-fill: #FF0000; -fx-font-weight: bold; -fx-font-size: 32px;");
            } else if (levelTimeRemainingSeconds <= 60) {
                leftTimerLabel.setStyle("-fx-text-fill: #FFA500; -fx-font-weight: bold; -fx-font-size: 32px;");
            } else {
                leftTimerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 32px; -fx-text-fill: #00FF00;");
            }
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
        if (timeLabel == null) {
            return;
        }
        long now = System.currentTimeMillis();
        long pausedSoFar = totalPausedMillis + (isPause.getValue() && lastPauseStartMillis > 0L ? (now - lastPauseStartMillis) : 0L);
        long elapsed = (gameStartTime > 0) ? (now - gameStartTime - pausedSoFar) : 0;
        long totalSeconds = elapsed / 1000;
        long mm = totalSeconds / 60;
        long ss = totalSeconds % 60;
        timeLabel.setText(String.format("%02d:%02d", mm, ss));
    }
    
    /**
     * Updates the score display for EndlessMode.
     * 
     * @param currentScore the current score
     * @param highScore the high score
     */
    public void updateScore(int currentScore, int highScore) {
        if (scoreLabel != null) {
            scoreLabel.setText(String.valueOf(currentScore));
        }
        if (highScoreLabel != null) {
            highScoreLabel.setText("Best Score: " + highScore);
        }
    }
    
    /**
     * Updates the lines cleared display for EndlessMode.
     * 
     * @param linesCleared the number of lines cleared
     */
    public void updateLines(int linesCleared) {
        if (linesLabel != null) {
            linesLabel.setText(String.valueOf(linesCleared));
        }
    }
    
    /**
     * Updates the level display for EndlessMode.
     * 
     * @param level the current level
     */
    public void updateLevel(int level) {
        if (levelLabel != null) {
            levelLabel.setText(String.valueOf(level));
        }
    }
    
    /**
     * Updates the speed display for EndlessMode.
     * 
     * @param speedLevel the current speed level
     */
    public void updateSpeed(int speedLevel) {
        if (speedLabel != null) {
            speedLabel.setText(speedLevel + "x");
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
            com.comp2042.game.LevelManager levelManager = com.comp2042.game.LevelManager.getInstance();
            com.comp2042.game.LevelMode currentLevel = levelManager.getCurrentLevel();
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
        SoundManager soundManager = SoundManager.getInstance();
        
        if (isMuted) {
            // Unmute: restore previous volume
            settings.setMasterVolume(previousVolume);
            // Apply to SoundManager immediately - this will restore all audio
            soundManager.setMasterVolume(previousVolume);
            if (muteButton != null) {
                muteButton.setText("MUTE (M)");
            }
        } else {
            // Mute: save current volume and set to 0%
            previousVolume = settings.getMasterVolume();
            settings.setMasterVolume(0.0);
            // Apply to SoundManager immediately - this will mute all audio
            soundManager.setMasterVolume(0.0);
            if (muteButton != null) {
                muteButton.setText("UNMUTE (M)");
            }
        }
        
        isMuted = !isMuted;
        
        // Save settings to persist the mute state
        settings.saveSettings();
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
        if (settings != null) {
            double currentVolume = settings.getMasterVolume();
            if (currentVolume > 0) {
                // Volume is not 0, so we're not muted
                isMuted = false;
                if (muteButton != null) {
                    muteButton.setText("MUTE (M)");
                }
            } else {
                // Volume is 0, so we're muted
                isMuted = true;
                if (muteButton != null) {
                    muteButton.setText("UNMUTE (M)");
                }
            }
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
     * Player 2: ←/→ - Move, ↑ - Rotate, ↓ - Soft Drop, 0 - Hard Drop, 1 - Hold, 2 - Rotate CCW
     */
    private void handleTwoPlayerControls(KeyEvent keyEvent) {
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
        if (keyEvent.getCode() == KeyCode.DIGIT1 || keyEvent.getCode() == KeyCode.NUMPAD1) {
            // Player 2: Hold brick (1 / NumPad1)
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
            
            // Stop countdown if running (will restart from beginning when Settings closes)
            // Save callback before stopping to ensure we can restart countdown correctly
            final boolean wasCountdownRunning = (countdownTimeline != null && countdownTimeline.getStatus() == Animation.Status.RUNNING);
            final Runnable savedCountdownCallback;
            if (wasCountdownRunning && countdownTimeline != null) {
                // Save callback before stopping
                savedCountdownCallback = countdownCallback;
                countdownTimeline.stop();
                countdownTimeline = null;
                // Stop countdown sound when countdown is stopped
                com.comp2042.SoundManager.getInstance().stopCountdownSound();
                // Remove overlays
                if (countdownOverlay1 != null && countdownParent1 != null) {
                    countdownParent1.getChildren().remove(countdownOverlay1);
                    countdownOverlay1 = null;
                }
                if (countdownOverlay2 != null && countdownParent2 != null) {
                    countdownParent2.getChildren().remove(countdownOverlay2);
                    countdownOverlay2 = null;
                }
            } else {
                savedCountdownCallback = null;
            }
            
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
            com.comp2042.ui.SettingsController settingsController = settingsLoader.getController();
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
            final boolean wasCountdownRunning = (countdownTimeline != null && countdownTimeline.getStatus() == Animation.Status.RUNNING);
            
            // Stop countdown if running (will restart from beginning when Help closes)
            // Save callback before stopping to ensure we can restart countdown correctly
            final Runnable savedCountdownCallback;
            if (wasCountdownRunning && countdownTimeline != null) {
                // Save callback before stopping
                savedCountdownCallback = countdownCallback;
                countdownTimeline.stop();
                countdownTimeline = null;
                // Stop countdown sound when countdown is stopped
                com.comp2042.SoundManager.getInstance().stopCountdownSound();
                // Remove overlays
                if (countdownOverlay1 != null && countdownParent1 != null) {
                    countdownParent1.getChildren().remove(countdownOverlay1);
                    countdownOverlay1 = null;
                }
                if (countdownOverlay2 != null && countdownParent2 != null) {
                    countdownParent2.getChildren().remove(countdownOverlay2);
                    countdownOverlay2 = null;
                }
            } else {
                savedCountdownCallback = null;
            }
            
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

            com.comp2042.ui.LevelSelectionController controller = levelSelectionLoader.getController();
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
        if (countdownTimeline != null) {
            countdownTimeline.stop();
            countdownTimeline = null;
            // Stop countdown sound when countdown is stopped
            com.comp2042.SoundManager.getInstance().stopCountdownSound();
        }
        
        // Clear countdown overlays
        countdownOverlay1 = null;
        countdownOverlay2 = null;
        countdownParent1 = null;
        countdownParent2 = null;
        countdownCallback = null;
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
            
            // Get leaderboard instance and check for high score
            com.comp2042.game.EndlessModeLeaderboard leaderboard = 
                com.comp2042.game.EndlessModeLeaderboard.getInstance();
            boolean isNewHighScore = leaderboard.isNewHighScore(finalScore);
            
            // Add entry to leaderboard and get rank
            int rank = leaderboard.addEntry(finalScore, linesCleared, playTimeMs, getCurrentLevel());
            
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
                    com.comp2042.core.GameService gameService = new com.comp2042.core.GameServiceImpl();
                    GuiController newGuiController = new GuiController();
                    var gameMode = com.comp2042.gameplay.GameModeFactory.createGameMode(com.comp2042.gameplay.GameModeType.ENDLESS, gameService, newGuiController);
                    gameMode.initialize();
                    newGuiController.setEndlessMode(true);
                    
                    // Load the new game scene
                    FXMLLoader gameLoader = new FXMLLoader(getClass().getClassLoader().getResource("enhancedGameLayout.fxml"));
                    Parent gameRoot = gameLoader.load();
                    GuiController gameController = gameLoader.getController();
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
                    com.comp2042.game.EndlessModeLeaderboard lb = 
                        com.comp2042.game.EndlessModeLeaderboard.getInstance();
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
        
        try {
            // Get level data from LevelManager
            com.comp2042.game.LevelManager levelManager = com.comp2042.game.LevelManager.getInstance();
            com.comp2042.game.LevelMode currentLevel = levelManager.getCurrentLevel();
            
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
            final com.comp2042.game.LevelMode finalCurrentLevel = levelManager.getCurrentLevel();
            
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
                    com.comp2042.ui.LevelSelectionController levelSelectionController = levelSelectionLoader.getController();
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
                com.comp2042.game.LevelMode nextLevel = levelManager.getLevel(finalCurrentLevel.getLevelId() + 1);
                if (nextLevel != null && nextLevel.isUnlocked()) {
                    try {
                        // First load level selection screen
                        FXMLLoader levelSelectionLoader = new FXMLLoader(getClass().getClassLoader().getResource("levelSelection.fxml"));
                        Parent levelSelectionRoot = levelSelectionLoader.load();
                        com.comp2042.ui.LevelSelectionController levelSelectionController = levelSelectionLoader.getController();
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
                        com.comp2042.ui.LevelSelectionController levelSelectionController = levelSelectionLoader.getController();
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
                // Return to level selection (to show updated stars and unlocked levels)
                try {
                    FXMLLoader levelSelectionLoader = new FXMLLoader(getClass().getClassLoader().getResource("levelSelection.fxml"));
                    Parent levelSelectionRoot = levelSelectionLoader.load();
                    com.comp2042.ui.LevelSelectionController levelSelectionController = levelSelectionLoader.getController();
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
                    com.comp2042.MainMenuController menuController = menuLoader.getController();
                    
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
    
    // Player 1 display matrices (for two-player mode)
    private Rectangle[][] displayMatrix1;
    private int[][] cachedBoardMatrix1; // Cached board matrix for Player 1 incremental rendering
    private Rectangle[][] rectangles1;
    private Rectangle[][] ghostRectangles1; // Ghost brick rectangles for Player 1
    private Rectangle[][] holdDisplayMatrix1;
    private Rectangle[][] nextDisplayMatrix1;
    
    // Player 2 display matrices (for two-player mode)
    private Rectangle[][] displayMatrix2;
    private int[][] cachedBoardMatrix2; // Cached board matrix for Player 2 incremental rendering
    private Rectangle[][] rectangles2;
    private Rectangle[][] ghostRectangles2; // Ghost brick rectangles for Player 2
    private Rectangle[][] holdDisplayMatrix2;
    private Rectangle[][] nextDisplayMatrix2;
    
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
        if (gamePanel1 == null) {
            return; // Two-player layout not loaded
        }
        
        // Set border style for Player 1's board background (same as endless mode)
        // Apply CSS styles directly to ensure visibility
        if (boardBackground1 != null) {
            boardBackground1.getStyleClass().add("gameBoard-two-player");
            boardBackground1.getStyleClass().add("player1-board");
            // Ensure styles are applied
            boardBackground1.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #851ee5, #4B0082), rgba(10, 14, 39, 0.96); " +
                "-fx-background-insets: 0,10; " +
                "-fx-background-radius: 16, 8; " +
                "-fx-border-color: rgba(138, 43, 226, 0.8); " +
                "-fx-border-width: 6px; " +
                "-fx-border-radius: 16px;"
            );
        }
        
        // Ensure grid lines are visible
        gamePanel1.setGridLinesVisible(true);
        if (!gamePanel1.getStyleClass().contains("game-grid")) {
            gamePanel1.getStyleClass().add("game-grid");
        }
        
        // Initialize display matrix for Player 1's static board background
        // Render all 20 rows (10 columns × 20 rows)
        displayMatrix1 = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        // Initialize cache for Player 1 incremental rendering optimization
        cachedBoardMatrix1 = new int[boardMatrix.length][boardMatrix[0].length];
        for (int i = 0; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.getStyleClass().add("game-cell");
                displayMatrix1[i][j] = rectangle;
                gamePanel1.add(rectangle, j, i);
                cachedBoardMatrix1[i][j] = boardMatrix[i][j]; // Initialize cache
            }
        }
        
        // Initialize rectangles for Player 1's current falling brick
        rectangles1 = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangle.setLayoutX(j * (BRICK_SIZE + 1));
                rectangle.setLayoutY(i * (BRICK_SIZE + 1));
                rectangles1[i][j] = rectangle;
                brickPanel1.getChildren().add(rectangle);
            }
        }
        
        // Initialize rectangles for Player 1's ghost brick
        if (ghostPanel1 != null) {
            ghostPanel1.getChildren().clear();
            ghostRectangles1 = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                    rectangle.setFill(Color.TRANSPARENT);
                    // Enhanced ghost brick appearance: rounded corners
                    rectangle.setArcHeight(9); // Rounded corners to match regular bricks
                    rectangle.setArcWidth(9);
                    rectangle.setOpacity(1.0); // Full opacity for better visibility
                    rectangle.setLayoutX(j * (BRICK_SIZE + 1));
                    rectangle.setLayoutY(i * (BRICK_SIZE + 1));
                    ghostRectangles1[i][j] = rectangle;
                    ghostPanel1.getChildren().add(rectangle);
                }
            }
        }
        
        if (gamePanel1 != null && brickPanel1 != null) {
            brickPanel1.setLayoutX(calculateGridX(gamePanel1, displayMatrix1, brick.getxPosition()));
            brickPanel1.setLayoutY(calculateGridY(gamePanel1, displayMatrix1, brick.getyPosition()));
        }
        
        // Initialize next and hold displays
        if (brick.getNextBrickData() != null) {
            updatePlayer1NextDisplay(brick.getNextBrickData());
        }
        if (brick.getHoldBrickData() != null) {
            updatePlayer1HoldDisplay(brick.getHoldBrickData());
        }
        
        // Update ghost brick position
        updatePlayer1GhostBrick(brick);
        
        // Hide game over panel initially
        if (gameOverPanel1 != null) {
            gameOverPanel1.setVisible(false);
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
        if (gamePanel2 == null) {
            return; // Two-player layout not loaded
        }
        
        // Set border style for Player 2's board background
        // Apply CSS styles directly to ensure visibility
        if (boardBackground2 != null) {
            boardBackground2.getStyleClass().add("gameBoard-two-player");
            boardBackground2.getStyleClass().add("player2-board");
            // Ensure styles are applied
            boardBackground2.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #851ee5, #4B0082), rgba(10, 14, 39, 0.96); " +
                "-fx-background-insets: 0,10; " +
                "-fx-background-radius: 16, 8; " +
                "-fx-border-color: rgba(138, 43, 226, 0.8); " +
                "-fx-border-width: 6px; " +
                "-fx-border-radius: 16px;"
            );
        }
        
        // Ensure grid lines are visible
        gamePanel2.setGridLinesVisible(true);
        if (!gamePanel2.getStyleClass().contains("game-grid")) {
            gamePanel2.getStyleClass().add("game-grid");
        }
        
        // Initialize display matrix for Player 2's static board background
        // Render all 20 rows (10 columns × 20 rows)
        displayMatrix2 = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        // Initialize cache for Player 2 incremental rendering optimization
        cachedBoardMatrix2 = new int[boardMatrix.length][boardMatrix[0].length];
        for (int i = 0; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.getStyleClass().add("game-cell");
                displayMatrix2[i][j] = rectangle;
                gamePanel2.add(rectangle, j, i);
                cachedBoardMatrix2[i][j] = boardMatrix[i][j]; // Initialize cache
            }
        }
        
        // Initialize rectangles for Player 2's current falling brick
        rectangles2 = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangle.setLayoutX(j * (BRICK_SIZE + 1));
                rectangle.setLayoutY(i * (BRICK_SIZE + 1));
                rectangles2[i][j] = rectangle;
                brickPanel2.getChildren().add(rectangle);
            }
        }
        
        // Initialize rectangles for Player 2's ghost brick
        if (ghostPanel2 != null) {
            ghostPanel2.getChildren().clear();
            ghostRectangles2 = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                    rectangle.setFill(Color.TRANSPARENT);
                    // Enhanced ghost brick appearance: rounded corners
                    rectangle.setArcHeight(9); // Rounded corners to match regular bricks
                    rectangle.setArcWidth(9);
                    rectangle.setOpacity(1.0); // Full opacity for better visibility
                    rectangle.setLayoutX(j * (BRICK_SIZE + 1));
                    rectangle.setLayoutY(i * (BRICK_SIZE + 1));
                    ghostRectangles2[i][j] = rectangle;
                    ghostPanel2.getChildren().add(rectangle);
                }
            }
        }
        
        if (gamePanel2 != null && brickPanel2 != null) {
            brickPanel2.setLayoutX(calculateGridX(gamePanel2, displayMatrix2, brick.getxPosition()));
            brickPanel2.setLayoutY(calculateGridY(gamePanel2, displayMatrix2, brick.getyPosition()));
        }
        
        // Initialize next and hold displays
        if (brick.getNextBrickData() != null) {
            updatePlayer2NextDisplay(brick.getNextBrickData());
        }
        if (brick.getHoldBrickData() != null) {
            updatePlayer2HoldDisplay(brick.getHoldBrickData());
        }
        
        // Update ghost brick position
        updatePlayer2GhostBrick(brick);
        
        // Hide game over panel initially
        if (gameOverPanel2 != null) {
            gameOverPanel2.setVisible(false);
        }
    }
    
    /**
     * Refreshes Player 1's brick display based on updated view data.
     * Updates the position and shape of Player 1's falling brick.
     * 
     * @param brick the updated view data for Player 1's brick
     */
    public void refreshPlayer1Brick(ViewData brick) {
        if (brickPanel1 == null || gamePanel1 == null || rectangles1 == null) {
            return;
        }
        
        if (!isPause.getValue()) {
            brickPanel1.setLayoutX(calculateGridX(gamePanel1, displayMatrix1, brick.getxPosition()));
            brickPanel1.setLayoutY(calculateGridY(gamePanel1, displayMatrix1, brick.getyPosition()));
            
            for (int i = 0; i < brick.getBrickData().length && i < rectangles1.length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length && j < rectangles1[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles1[i][j]);
                }
            }
            
            // Update next and hold displays
            if (brick.getNextBrickData() != null) {
                updatePlayer1NextDisplay(brick.getNextBrickData());
            }
            if (brick.getHoldBrickData() != null) {
                updatePlayer1HoldDisplay(brick.getHoldBrickData());
            }
            
            // Update ghost brick position
            updatePlayer1GhostBrick(brick);
        }
    }
    
    /**
     * Refreshes Player 2's brick display based on updated view data.
     * Updates the position and shape of Player 2's falling brick.
     * 
     * @param brick the updated view data for Player 2's brick
     */
    public void refreshPlayer2Brick(ViewData brick) {
        if (brickPanel2 == null || gamePanel2 == null || rectangles2 == null) {
            return;
        }
        
        if (!isPause.getValue()) {
            brickPanel2.setLayoutX(calculateGridX(gamePanel2, displayMatrix2, brick.getxPosition()));
            brickPanel2.setLayoutY(calculateGridY(gamePanel2, displayMatrix2, brick.getyPosition()));
            
            for (int i = 0; i < brick.getBrickData().length && i < rectangles2.length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length && j < rectangles2[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles2[i][j]);
                }
            }
            
            // Update next and hold displays
            if (brick.getNextBrickData() != null) {
                updatePlayer2NextDisplay(brick.getNextBrickData());
            }
            if (brick.getHoldBrickData() != null) {
                updatePlayer2HoldDisplay(brick.getHoldBrickData());
            }
            
            // Update ghost brick position
            updatePlayer2GhostBrick(brick);
        }
    }
    
    /**
     * Updates Player 1's ghost brick display based on the current brick position and shape.
     * 
     * @param brick The ViewData containing the current brick shape and ghost position.
     */
    private void updatePlayer1GhostBrick(ViewData brick) {
        if (ghostPanel1 == null || ghostRectangles1 == null) {
            return;
        }
        
        // Two-player mode: always show ghost brick
        boolean showGhost = shouldShowGhostBrick();
        ghostPanel1.setVisible(showGhost);
        
        if (!showGhost) {
            return;
        }
        
        int ghostY = brick.getGhostYPosition();
        if (ghostY < 0 || ghostY == brick.getyPosition()) {
            ghostPanel1.setVisible(false);
            return;
        }
        
        ghostPanel1.setLayoutX(calculateGridX(gamePanel1, displayMatrix1, brick.getxPosition()));
        ghostPanel1.setLayoutY(calculateGridY(gamePanel1, displayMatrix1, ghostY));
        
        // Update ghost brick rectangles to match current brick shape
        int[][] brickData = brick.getBrickData();
        for (int i = 0; i < brickData.length && i < ghostRectangles1.length; i++) {
            for (int j = 0; j < brickData[i].length && j < ghostRectangles1[i].length; j++) {
                Rectangle ghostRect = ghostRectangles1[i][j];
                if (brickData[i][j] != 0) {
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
                    ghostRect.setVisible(false);
                }
            }
        }
    }
    
    /**
     * Updates Player 2's ghost brick display based on the current brick position and shape.
     * 
     * @param brick The ViewData containing the current brick shape and ghost position.
     */
    private void updatePlayer2GhostBrick(ViewData brick) {
        if (ghostPanel2 == null || ghostRectangles2 == null) {
            return;
        }
        
        // Two-player mode: always show ghost brick
        boolean showGhost = shouldShowGhostBrick();
        ghostPanel2.setVisible(showGhost);
        
        if (!showGhost) {
            return;
        }
        
        int ghostY = brick.getGhostYPosition();
        if (ghostY < 0 || ghostY == brick.getyPosition()) {
            ghostPanel2.setVisible(false);
            return;
        }
        
        ghostPanel2.setLayoutX(calculateGridX(gamePanel2, displayMatrix2, brick.getxPosition()));
        ghostPanel2.setLayoutY(calculateGridY(gamePanel2, displayMatrix2, ghostY));
        
        // Update ghost brick rectangles to match current brick shape
        int[][] brickData = brick.getBrickData();
        for (int i = 0; i < brickData.length && i < ghostRectangles2.length; i++) {
            for (int j = 0; j < brickData[i].length && j < ghostRectangles2[i].length; j++) {
                Rectangle ghostRect = ghostRectangles2[i][j];
                if (brickData[i][j] != 0) {
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
                    ghostRect.setVisible(false);
                }
            }
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
        if (displayMatrix1 == null || board == null) {
            return;
        }
        
        // Initialize cache on first call
        if (cachedBoardMatrix1 == null || 
            cachedBoardMatrix1.length != board.length || 
            cachedBoardMatrix1[0].length != board[0].length) {
            cachedBoardMatrix1 = new int[board.length][board[0].length];
            // Force full update on first call
            for (int i = 0; i < board.length && i < displayMatrix1.length; i++) {
                for (int j = 0; j < board[i].length && j < displayMatrix1[i].length; j++) {
                    cachedBoardMatrix1[i][j] = board[i][j];
                    setRectangleData(board[i][j], displayMatrix1[i][j]);
                }
            }
            return;
        }
        
        // Incremental update: only update changed cells
        for (int i = 0; i < board.length && i < displayMatrix1.length; i++) {
            for (int j = 0; j < board[i].length && j < displayMatrix1[i].length; j++) {
                if (cachedBoardMatrix1[i][j] != board[i][j]) {
                    cachedBoardMatrix1[i][j] = board[i][j];
                    setRectangleData(board[i][j], displayMatrix1[i][j]);
                }
            }
        }
    }
    
    /**
     * Refreshes Player 2's game board background display.
     * PERFORMANCE OPTIMIZATION: Only updates cells that have actually changed.
     * 
     * @param board the updated board matrix
     */
    public void refreshGameBackground2(int[][] board) {
        if (displayMatrix2 == null || board == null) {
            return;
        }
        
        // Initialize cache on first call
        if (cachedBoardMatrix2 == null || 
            cachedBoardMatrix2.length != board.length || 
            cachedBoardMatrix2[0].length != board[0].length) {
            cachedBoardMatrix2 = new int[board.length][board[0].length];
            // Force full update on first call
            for (int i = 0; i < board.length && i < displayMatrix2.length; i++) {
                for (int j = 0; j < board[i].length && j < displayMatrix2[i].length; j++) {
                    cachedBoardMatrix2[i][j] = board[i][j];
                    setRectangleData(board[i][j], displayMatrix2[i][j]);
                }
            }
            return;
        }
        
        // Incremental update: only update changed cells
        for (int i = 0; i < board.length && i < displayMatrix2.length; i++) {
            for (int j = 0; j < board[i].length && j < displayMatrix2[i].length; j++) {
                if (cachedBoardMatrix2[i][j] != board[i][j]) {
                    cachedBoardMatrix2[i][j] = board[i][j];
                    setRectangleData(board[i][j], displayMatrix2[i][j]);
                }
            }
        }
    }
    
    /**
     * Updates Player 1's next piece display.
     * 
     * @param nextBrickData the next brick shape data
     */
    public void updatePlayer1NextDisplay(int[][] nextBrickData) {
        if (nextBrickPanel1 == null) {
            return;
        }
        
        if (nextDisplayMatrix1 == null) {
            nextDisplayMatrix1 = new Rectangle[4][4];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    Rectangle rectangle = new Rectangle(20, 20);
                    rectangle.setFill(Color.TRANSPARENT);
                    rectangle.setArcHeight(9);
                    rectangle.setArcWidth(9);
                    nextDisplayMatrix1[i][j] = rectangle;
                    nextBrickPanel1.add(rectangle, j, i);
                }
            }
        }
        
        // Clear display
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                nextDisplayMatrix1[i][j].setFill(Color.TRANSPARENT);
            }
        }
        
        // Display brick
        if (nextBrickData != null) {
            for (int i = 0; i < nextBrickData.length && i < 4; i++) {
                for (int j = 0; j < nextBrickData[i].length && j < 4; j++) {
                    if (nextBrickData[i][j] != 0) {
                        nextDisplayMatrix1[i][j].setFill(getFillColor(nextBrickData[i][j]));
                    }
                }
            }
        }
    }
    
    /**
     * Updates Player 2's next piece display.
     * 
     * @param nextBrickData the next brick shape data
     */
    public void updatePlayer2NextDisplay(int[][] nextBrickData) {
        if (nextBrickPanel2 == null) {
            return;
        }
        
        if (nextDisplayMatrix2 == null) {
            nextDisplayMatrix2 = new Rectangle[4][4];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    Rectangle rectangle = new Rectangle(20, 20);
                    rectangle.setFill(Color.TRANSPARENT);
                    rectangle.setArcHeight(9);
                    rectangle.setArcWidth(9);
                    nextDisplayMatrix2[i][j] = rectangle;
                    nextBrickPanel2.add(rectangle, j, i);
                }
            }
        }
        
        // Clear display
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                nextDisplayMatrix2[i][j].setFill(Color.TRANSPARENT);
            }
        }
        
        // Display brick
        if (nextBrickData != null) {
            for (int i = 0; i < nextBrickData.length && i < 4; i++) {
                for (int j = 0; j < nextBrickData[i].length && j < 4; j++) {
                    if (nextBrickData[i][j] != 0) {
                        nextDisplayMatrix2[i][j].setFill(getFillColor(nextBrickData[i][j]));
                    }
                }
            }
        }
    }
    
    /**
     * Updates Player 1's hold piece display.
     * 
     * @param holdBrickData the held brick shape data
     */
    public void updatePlayer1HoldDisplay(int[][] holdBrickData) {
        if (holdPanel1 == null) {
            return;
        }
        
        if (holdDisplayMatrix1 == null) {
            holdDisplayMatrix1 = new Rectangle[4][4];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    Rectangle rectangle = new Rectangle(20, 20);
                    rectangle.setFill(Color.TRANSPARENT);
                    rectangle.setArcHeight(9);
                    rectangle.setArcWidth(9);
                    holdDisplayMatrix1[i][j] = rectangle;
                    holdPanel1.add(rectangle, j, i);
                }
            }
        }
        
        // Clear display
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                holdDisplayMatrix1[i][j].setFill(Color.TRANSPARENT);
            }
        }
        
        // Display brick
        if (holdBrickData != null) {
            for (int i = 0; i < holdBrickData.length && i < 4; i++) {
                for (int j = 0; j < holdBrickData[i].length && j < 4; j++) {
                    if (holdBrickData[i][j] != 0) {
                        holdDisplayMatrix1[i][j].setFill(getFillColor(holdBrickData[i][j]));
                    }
                }
            }
        }
    }
    
    /**
     * Updates Player 2's hold piece display.
     * 
     * @param holdBrickData the held brick shape data
     */
    public void updatePlayer2HoldDisplay(int[][] holdBrickData) {
        if (holdPanel2 == null) {
            return;
        }
        
        if (holdDisplayMatrix2 == null) {
            holdDisplayMatrix2 = new Rectangle[4][4];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    Rectangle rectangle = new Rectangle(20, 20);
                    rectangle.setFill(Color.TRANSPARENT);
                    rectangle.setArcHeight(9);
                    rectangle.setArcWidth(9);
                    holdDisplayMatrix2[i][j] = rectangle;
                    holdPanel2.add(rectangle, j, i);
                }
            }
        }
        
        // Clear display
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                holdDisplayMatrix2[i][j].setFill(Color.TRANSPARENT);
            }
        }
        
        // Display brick
        if (holdBrickData != null) {
            for (int i = 0; i < holdBrickData.length && i < 4; i++) {
                for (int j = 0; j < holdBrickData[i].length && j < 4; j++) {
                    if (holdBrickData[i][j] != 0) {
                        holdDisplayMatrix2[i][j].setFill(getFillColor(holdBrickData[i][j]));
                    }
                }
            }
        }
    }
    
    /**
     * Updates Player 1's score display.
     * 
     * @param score the new score value
     */
    public void updatePlayer1Score(int score) {
        if (player1ScoreLabel != null) {
            player1ScoreLabel.setText("Score: " + score);
        }
    }
    
    /**
     * Updates Player 2's score display.
     * 
     * @param score the new score value
     */
    public void updatePlayer2Score(int score) {
        if (player2ScoreLabel != null) {
            player2ScoreLabel.setText("Score: " + score);
        }
    }
    
    /**
     * Updates scores for both players (helper method for two-player mode).
     * 
     * @param controller the TwoPlayerGameController instance
     */
    private void updatePlayerScores(TwoPlayerGameController controller) {
        if (controller != null) {
            int player1Score = controller.getPlayer1Service().getScore().getScore();
            int player2Score = controller.getPlayer2Service().getScore().getScore();
            updatePlayer1Score(player1Score);
            updatePlayer2Score(player2Score);
        }
    }
    
    /**
     * Updates player statistics display for two-player mode.
     * 
     * @param player the player number (1 or 2)
     * @param stats the PlayerStats instance containing the statistics
     */
    public void updatePlayerStats(int player, com.comp2042.game.PlayerStats stats) {
        if (stats == null) {
            return;
        }
        
        if (player == 1) {
            if (player1LinesLabel != null) {
                player1LinesLabel.setText(String.valueOf(stats.getLinesCleared()));
            }
            if (player1ComboLabel != null) {
                player1ComboLabel.setText(String.valueOf(stats.getCurrentCombo()));
            }
            if (player1AttackLabel != null) {
                player1AttackLabel.setText(String.valueOf(stats.getAttacksSent()));
            }
            if (player1DefenseLabel != null) {
                player1DefenseLabel.setText(String.valueOf(stats.getAttacksReceived()));
            }
            if (player1TetrisLabel != null) {
                player1TetrisLabel.setText(String.valueOf(stats.getTetrisCount()));
            }
            if (player1TimeLabel != null) {
                player1TimeLabel.setText(stats.getFormattedTime());
            }
        } else if (player == 2) {
            if (player2LinesLabel != null) {
                player2LinesLabel.setText(String.valueOf(stats.getLinesCleared()));
            }
            if (player2ComboLabel != null) {
                player2ComboLabel.setText(String.valueOf(stats.getCurrentCombo()));
            }
            if (player2AttackLabel != null) {
                player2AttackLabel.setText(String.valueOf(stats.getAttacksSent()));
            }
            if (player2DefenseLabel != null) {
                player2DefenseLabel.setText(String.valueOf(stats.getAttacksReceived()));
            }
            if (player2TetrisLabel != null) {
                player2TetrisLabel.setText(String.valueOf(stats.getTetrisCount()));
            }
            if (player2TimeLabel != null) {
                player2TimeLabel.setText(stats.getFormattedTime());
            }
        }
    }
    
    /**
     * Clears all panels for Player 1 (game board, hold, next).
     * Used when starting a new game in two-player mode.
     */
    private void clearPlayer1Panels() {
        // Clear game board display
        if (displayMatrix1 != null && gamePanel1 != null) {
            for (int i = 0; i < displayMatrix1.length; i++) {
                if (displayMatrix1[i] != null) {
                    for (int j = 0; j < displayMatrix1[i].length; j++) {
                        if (displayMatrix1[i][j] != null) {
                            displayMatrix1[i][j].setFill(Color.TRANSPARENT);
                        }
                    }
                }
            }
        }
        
        // Clear hold display
        updatePlayer1HoldDisplay(null);
        
        // Clear next display
        updatePlayer1NextDisplay(null);

        // Clear ghost brick
        if (ghostPanel1 != null) {
            ghostPanel1.setVisible(false);
            ghostPanel1.getChildren().clear();
        }
        ghostRectangles1 = null;
        
        // Clear falling brick
        if (brickPanel1 != null) {
            brickPanel1.getChildren().clear();
        }
    }
    
    /**
     * Clears all panels for Player 2 (game board, hold, next).
     * Used when starting a new game in two-player mode.
     */
    private void clearPlayer2Panels() {
        // Clear game board display
        if (displayMatrix2 != null && gamePanel2 != null) {
            for (int i = 0; i < displayMatrix2.length; i++) {
                if (displayMatrix2[i] != null) {
                    for (int j = 0; j < displayMatrix2[i].length; j++) {
                        if (displayMatrix2[i][j] != null) {
                            displayMatrix2[i][j].setFill(Color.TRANSPARENT);
                        }
                    }
                }
            }
        }
        
        // Clear hold display
        updatePlayer2HoldDisplay(null);
        
        // Clear next display
        updatePlayer2NextDisplay(null);

        // Clear ghost brick
        if (ghostPanel2 != null) {
            ghostPanel2.setVisible(false);
            ghostPanel2.getChildren().clear();
        }
        ghostRectangles2 = null;
        
        // Clear falling brick
        if (brickPanel2 != null) {
            brickPanel2.getChildren().clear();
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
        // Get statistics from game mode
        com.comp2042.game.PlayerStats player1Stats = null;
        com.comp2042.game.PlayerStats player2Stats = null;
        
        if (eventListener instanceof TwoPlayerGameController) {
            TwoPlayerGameController controller = (TwoPlayerGameController) eventListener;
            if (controller.getGameMode() instanceof com.comp2042.game.TwoPlayerVSGameMode) {
                com.comp2042.game.TwoPlayerVSGameMode gameMode = 
                    (com.comp2042.game.TwoPlayerVSGameMode) controller.getGameMode();
                player1Stats = gameMode.getPlayer1Stats();
                player2Stats = gameMode.getPlayer2Stats();
            }
        }
        
        // Use default stats if not available
        if (player1Stats == null) {
            player1Stats = new com.comp2042.game.PlayerStats();
        }
        if (player2Stats == null) {
            player2Stats = new com.comp2042.game.PlayerStats();
        }
        
        // Show enhanced game over panel
        if (twoPlayerGameOverPanel != null) {
            twoPlayerGameOverPanel.setVisible(true);
            twoPlayerGameOverPanel.setManaged(true);
            twoPlayerGameOverPanel.setGameOverInfo(winner, player1Stats, player2Stats, 
                                                    player1Score, player2Score);
            
            // Set button actions
            twoPlayerGameOverPanel.setButtons(
                e -> {
                    // Hide game over panel
                    if (twoPlayerGameOverPanel != null) {
                        twoPlayerGameOverPanel.setVisible(false);
                        twoPlayerGameOverPanel.setManaged(false);
                    }
                    newGame(e);
                },  // New Game button
                e -> {
                    // Hide game over panel
                    if (twoPlayerGameOverPanel != null) {
                        twoPlayerGameOverPanel.setVisible(false);
                        twoPlayerGameOverPanel.setManaged(false);
                    }
                    returnToMenu();
                }  // Back to Menu button
            );
            
            // Add fade-in animation
            javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(
                javafx.util.Duration.millis(500), twoPlayerGameOverPanel);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        } else {
            // Fallback to old panels if new panel is not available
            String winnerText;
            if (winner == 0) {
                winnerText = "TIE GAME";
            } else if (winner == 1) {
                winnerText = "PLAYER 1 WINS!";
            } else {
                winnerText = "PLAYER 2 WINS!";
            }
            
            if (gameOverPanel1 != null) {
                gameOverPanel1.setVisible(true);
                gameOverPanel1.setTitle(winnerText);
                gameOverPanel1.setSubtitle("Player 1: " + player1Score + " | Player 2: " + player2Score);
            }
            if (gameOverPanel2 != null) {
                gameOverPanel2.setVisible(true);
                gameOverPanel2.setTitle(winnerText);
                gameOverPanel2.setSubtitle("Player 1: " + player1Score + " | Player 2: " + player2Score);
            }
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
        if (rootPane == null || gamePanel1 == null || gamePanel2 == null) {
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }
        
        // Stop any existing countdown
        if (countdownTimeline != null) {
            countdownTimeline.stop();
            countdownTimeline = null;
            // Stop countdown sound when countdown is stopped
            com.comp2042.SoundManager.getInstance().stopCountdownSound();
        }
        
        // Stop game timelines during countdown (to prevent blocks from falling)
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
        
        // Remove any existing overlays
        if (countdownOverlay1 != null && countdownParent1 != null) {
            countdownParent1.getChildren().remove(countdownOverlay1);
        }
        if (countdownOverlay2 != null && countdownParent2 != null) {
            countdownParent2.getChildren().remove(countdownOverlay2);
        }
        
        // Save callback for later use
        countdownCallback = onComplete;
        
        // Create countdown labels for each player
        Label countdownLabel1 = new Label();
        countdownLabel1.setFont(Font.font("Arial", FontWeight.BOLD, 80));
        countdownLabel1.setStyle(
            "-fx-text-fill: #4ECDC4; " +
            "-fx-effect: dropshadow(gaussian, rgba(78, 205, 196, 1.0), 30, 0, 0, 0); " +
            "-fx-alignment: center;"
        );
        countdownLabel1.setAlignment(Pos.CENTER);
        countdownLabel1.setMouseTransparent(true);
        
        Label countdownLabel2 = new Label();
        countdownLabel2.setFont(Font.font("Arial", FontWeight.BOLD, 80));
        countdownLabel2.setStyle(
            "-fx-text-fill: #FF6B6B; " +
            "-fx-effect: dropshadow(gaussian, rgba(255, 107, 107, 1.0), 30, 0, 0, 0); " +
            "-fx-alignment: center;"
        );
        countdownLabel2.setAlignment(Pos.CENTER);
        countdownLabel2.setMouseTransparent(true);
        
        // Create overlays for each game panel
        StackPane overlay1 = new StackPane();
        overlay1.setAlignment(Pos.CENTER);
        overlay1.setStyle("-fx-background-color: rgba(0, 0, 0, 0.55); -fx-background-radius: 8;");
        overlay1.getChildren().add(countdownLabel1);
        overlay1.setMouseTransparent(true);
        
        StackPane overlay2 = new StackPane();
        overlay2.setAlignment(Pos.CENTER);
        overlay2.setStyle("-fx-background-color: rgba(0, 0, 0, 0.55); -fx-background-radius: 8;");
        overlay2.getChildren().add(countdownLabel2);
        overlay2.setMouseTransparent(true);
        
        // Find parent containers for game panels (should be StackPane or Pane)
        Node parent1 = gamePanel1.getParent();
        Node parent2 = gamePanel2.getParent();
        
        if (parent1 instanceof Pane && parent2 instanceof Pane) {
            Pane pane1 = (Pane) parent1;
            Pane pane2 = (Pane) parent2;
            
            // Save parent references
            countdownParent1 = pane1;
            countdownParent2 = pane2;
            countdownOverlay1 = overlay1;
            countdownOverlay2 = overlay2;
            
            // Add overlays to parent containers
            pane1.getChildren().add(overlay1);
            pane2.getChildren().add(overlay2);
            
            // Bind overlay size to cover entire board background
            if (boardBackground1 != null) {
                overlay1.prefWidthProperty().bind(boardBackground1.widthProperty().subtract(20));
                overlay1.prefHeightProperty().bind(boardBackground1.heightProperty().subtract(20));
                overlay1.layoutXProperty().bind(boardBackground1.layoutXProperty().add(10));
                overlay1.layoutYProperty().bind(boardBackground1.layoutYProperty().add(10));
            } else {
                overlay1.prefWidthProperty().bind(gamePanel1.widthProperty());
                overlay1.prefHeightProperty().bind(gamePanel1.heightProperty());
                overlay1.layoutXProperty().bind(gamePanel1.layoutXProperty());
                overlay1.layoutYProperty().bind(gamePanel1.layoutYProperty());
            }
            
            if (boardBackground2 != null) {
                overlay2.prefWidthProperty().bind(boardBackground2.widthProperty().subtract(20));
                overlay2.prefHeightProperty().bind(boardBackground2.heightProperty().subtract(20));
                overlay2.layoutXProperty().bind(boardBackground2.layoutXProperty().add(10));
                overlay2.layoutYProperty().bind(boardBackground2.layoutYProperty().add(10));
            } else {
                overlay2.prefWidthProperty().bind(gamePanel2.widthProperty());
                overlay2.prefHeightProperty().bind(gamePanel2.heightProperty());
                overlay2.layoutXProperty().bind(gamePanel2.layoutXProperty());
                overlay2.layoutYProperty().bind(gamePanel2.layoutYProperty());
            }
        }
        
        // Countdown animation
        countdownTimeline = new Timeline();
        
        // Play countdown sound once at the start (4-second audio)
        KeyFrame soundFrame = new KeyFrame(
            Duration.millis(0),
            e -> {
                com.comp2042.SoundManager.getInstance().playCountdownSound();
            }
        );
        countdownTimeline.getKeyFrames().add(soundFrame);
        
        for (int i = 3; i >= 1; i--) {
            final int count = i;
            KeyFrame keyFrame = new KeyFrame(
                Duration.millis((3 - i) * 1000),
                e -> {
                    countdownLabel1.setText(String.valueOf(count));
                    countdownLabel2.setText(String.valueOf(count));
                }
            );
            countdownTimeline.getKeyFrames().add(keyFrame);
        }
        
        // Show "Start!" at 3000ms
        KeyFrame startFrame = new KeyFrame(
            Duration.millis(3000),
            e -> {
                countdownLabel1.setText("Start!");
                countdownLabel2.setText("Start!");
            }
        );
        countdownTimeline.getKeyFrames().add(startFrame);
        
        // Final keyframe to hide overlays and start game (at 4000ms to allow audio to finish)
        final Pane finalParent1 = countdownParent1;
        final Pane finalParent2 = countdownParent2;
        
        KeyFrame finalFrame = new KeyFrame(
            Duration.millis(4000),
            e -> {
                if (finalParent1 != null && finalParent1.getChildren().contains(overlay1)) {
                    finalParent1.getChildren().remove(overlay1);
                }
                if (finalParent2 != null && finalParent2.getChildren().contains(overlay2)) {
                    finalParent2.getChildren().remove(overlay2);
                }
                // Clear countdown tracking
                countdownTimeline = null;
                countdownCallback = null;
                countdownOverlay1 = null;
                countdownOverlay2 = null;
                countdownParent1 = null;
                countdownParent2 = null;
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        );
        countdownTimeline.getKeyFrames().add(finalFrame);
        
        countdownTimeline.play();
    }
    
    /**
     * Shows an enhanced attack animation on the specified player's board.
     * Includes shockwave, screen shake, and flash effects.
     * 
     * @param player the player number (1 or 2)
     * @param attackPower the number of lines being attacked
     */
    public void showAttackAnimation(int player, int attackPower) {
        if (rootPane == null) {
            return;
        }
        
        // Find the player's game board container
        Pane boardBackground = (player == 1) ? boardBackground1 : boardBackground2;
        Pane playerBoard = (player == 1) ? gamePanel1 : gamePanel2;
        if (boardBackground == null || playerBoard == null) {
            return;
        }
        
        // Calculate animation intensity based on attack power
        double intensity = Math.min(attackPower / 4.0, 1.0); // Max intensity at 4+ lines
        long duration = (long)(300 + intensity * 200); // 300-500ms based on power
        
        // Get board container (parent of boardBackground)
        Pane boardContainer = (Pane) boardBackground.getParent();
        if (boardContainer == null) {
            return;
        }
        
        // 1. Screen shake effect
        double shakeAmount = 3 + intensity * 5; // 3-8 pixels based on intensity
        double originalX = boardContainer.getLayoutX();
        double originalY = boardContainer.getLayoutY();
        
        javafx.animation.Timeline shakeTimeline = new javafx.animation.Timeline();
        int shakeCount = 8;
        for (int i = 0; i < shakeCount; i++) {
            double offsetX = (Math.random() - 0.5) * shakeAmount * 2;
            double offsetY = (Math.random() - 0.5) * shakeAmount * 2;
            javafx.animation.KeyFrame keyFrame = new javafx.animation.KeyFrame(
                Duration.millis(i * duration / shakeCount),
                e -> {
                    boardContainer.setLayoutX(originalX + offsetX);
                    boardContainer.setLayoutY(originalY + offsetY);
                }
            );
            shakeTimeline.getKeyFrames().add(keyFrame);
        }
        // Return to original position
        javafx.animation.KeyFrame returnFrame = new javafx.animation.KeyFrame(
            Duration.millis(duration),
            e -> {
                boardContainer.setLayoutX(originalX);
                boardContainer.setLayoutY(originalY);
            }
        );
        shakeTimeline.getKeyFrames().add(returnFrame);
        shakeTimeline.play();
        
        // 2. Flash overlay effect
        Rectangle flashOverlay = new Rectangle();
        flashOverlay.setWidth(boardBackground.getWidth());
        flashOverlay.setHeight(boardBackground.getHeight());
        flashOverlay.setFill(player == 1 ? 
            javafx.scene.paint.Color.rgb(255, 107, 107, 0.4 + intensity * 0.3) : 
            javafx.scene.paint.Color.rgb(78, 205, 196, 0.4 + intensity * 0.3));
        flashOverlay.setMouseTransparent(true);
        flashOverlay.setLayoutX(boardBackground.getLayoutX());
        flashOverlay.setLayoutY(boardBackground.getLayoutY());
        
        boardContainer.getChildren().add(flashOverlay);
        
        // Flash animation with multiple pulses
        javafx.animation.SequentialTransition flashSequence = new javafx.animation.SequentialTransition();
        
        // First strong flash
        javafx.animation.FadeTransition flash1 = new javafx.animation.FadeTransition(
            Duration.millis(duration / 3), flashOverlay);
        flash1.setFromValue(0.7 + intensity * 0.3);
        flash1.setToValue(0.2);
        
        // Second pulse
        javafx.animation.FadeTransition flash2 = new javafx.animation.FadeTransition(
            Duration.millis(duration / 3), flashOverlay);
        flash2.setFromValue(0.3);
        flash2.setToValue(0.1);
        
        // Fade out
        javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(
            Duration.millis(duration / 3), flashOverlay);
        fadeOut.setFromValue(0.1);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            boardContainer.getChildren().remove(flashOverlay);
        });
        
        flashSequence.getChildren().addAll(flash1, flash2, fadeOut);
        flashSequence.play();
        
        // 3. Shockwave effect (circular ripple from center)
        Circle shockwave = new Circle();
        double centerX = boardBackground.getWidth() / 2 + boardBackground.getLayoutX();
        double centerY = boardBackground.getHeight() / 2 + boardBackground.getLayoutY();
        shockwave.setCenterX(centerX);
        shockwave.setCenterY(centerY);
        shockwave.setRadius(10);
        shockwave.setFill(javafx.scene.paint.Color.TRANSPARENT);
        shockwave.setStroke(player == 1 ? 
            javafx.scene.paint.Color.rgb(255, 107, 107, 0.8) : 
            javafx.scene.paint.Color.rgb(78, 205, 196, 0.8));
        shockwave.setStrokeWidth(3 + intensity * 2);
        shockwave.setMouseTransparent(true);
        
        boardContainer.getChildren().add(shockwave);
        
        // Shockwave expansion animation
        double maxRadius = Math.max(boardBackground.getWidth(), boardBackground.getHeight()) * 0.7;
        javafx.animation.ScaleTransition shockwaveExpand = new javafx.animation.ScaleTransition(
            Duration.millis(duration), shockwave);
        shockwaveExpand.setFromX(1.0);
        shockwaveExpand.setFromY(1.0);
        shockwaveExpand.setToX(maxRadius / 10.0);
        shockwaveExpand.setToY(maxRadius / 10.0);
        
        javafx.animation.FadeTransition shockwaveFade = new javafx.animation.FadeTransition(
            Duration.millis(duration), shockwave);
        shockwaveFade.setFromValue(0.8);
        shockwaveFade.setToValue(0.0);
        
        javafx.animation.ParallelTransition shockwaveAnimation = new javafx.animation.ParallelTransition(
            shockwaveExpand, shockwaveFade);
        shockwaveAnimation.setOnFinished(e -> {
            boardContainer.getChildren().remove(shockwave);
        });
        shockwaveAnimation.play();
        
        // 4. Particle effect (small circles radiating outward)
        if (attackPower >= 2) {
            int particleCount = (int)(5 + intensity * 10); // 5-15 particles
            for (int i = 0; i < particleCount; i++) {
                Circle particle = new Circle(2 + Math.random() * 3);
                particle.setCenterX(centerX);
                particle.setCenterY(centerY);
                particle.setFill(player == 1 ? 
                    javafx.scene.paint.Color.rgb(255, 107, 107, 0.9) : 
                    javafx.scene.paint.Color.rgb(78, 205, 196, 0.9));
                particle.setMouseTransparent(true);
                
                boardContainer.getChildren().add(particle);
                
                // Random direction and distance
                double angle = Math.random() * 2 * Math.PI;
                double distance = 30 + Math.random() * 50;
                double endX = centerX + Math.cos(angle) * distance;
                double endY = centerY + Math.sin(angle) * distance;
                
                javafx.animation.TranslateTransition particleMove = new javafx.animation.TranslateTransition(
                    Duration.millis(duration), particle);
                particleMove.setFromX(0);
                particleMove.setFromY(0);
                particleMove.setToX(endX - centerX);
                particleMove.setToY(endY - centerY);
                
                javafx.animation.FadeTransition particleFade = new javafx.animation.FadeTransition(
                    Duration.millis(duration), particle);
                particleFade.setFromValue(0.9);
                particleFade.setToValue(0.0);
                
                javafx.animation.ParallelTransition particleAnimation = new javafx.animation.ParallelTransition(
                    particleMove, particleFade);
                particleAnimation.setOnFinished(e -> {
                    boardContainer.getChildren().remove(particle);
                });
                particleAnimation.play();
            }
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
        if (rootPane == null) {
            return;
        }
        
        // Find the player's game board area
        Pane playerBoard = (player == 1) ? gamePanel1 : gamePanel2;
        if (playerBoard == null) {
            return;
        }
        
        // Create combo bonus label
        Label comboLabel = new Label("COMBO x" + combo + "!\n" + linesEliminated + " lines eliminated!");
        comboLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        comboLabel.setStyle(
            "-fx-text-fill: #FFD700; " +
            "-fx-effect: dropshadow(gaussian, rgba(255, 215, 0, 1.0), 20, 0, 0, 0); " +
            "-fx-alignment: center; " +
            "-fx-text-alignment: center;"
        );
        comboLabel.setAlignment(Pos.CENTER);
        comboLabel.setMouseTransparent(true);
        
        // Position label over player board
        StackPane comboOverlay = new StackPane();
        comboOverlay.setAlignment(Pos.CENTER);
        comboOverlay.getChildren().add(comboLabel);
        comboOverlay.setMouseTransparent(true);
        
        // Add to root pane
        rootPane.getChildren().add(comboOverlay);
        
        // Animate: scale up, then fade out
        comboLabel.setScaleX(0.5);
        comboLabel.setScaleY(0.5);
        comboLabel.setOpacity(0.0);
        
        javafx.animation.ScaleTransition scaleUp = new javafx.animation.ScaleTransition(
            Duration.millis(300), comboLabel);
        scaleUp.setToX(1.2);
        scaleUp.setToY(1.2);
        
        javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(
            Duration.millis(300), comboLabel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        javafx.animation.ParallelTransition appear = new javafx.animation.ParallelTransition(
            scaleUp, fadeIn);
        
        javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(
            Duration.millis(500), comboLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.millis(1000));
        
        javafx.animation.SequentialTransition sequence = new javafx.animation.SequentialTransition(
            appear, fadeOut);
        sequence.setOnFinished(e -> rootPane.getChildren().remove(comboOverlay));
        sequence.play();
    }
}