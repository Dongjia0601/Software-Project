package com.comp2042;

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
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.geometry.Pos;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.Parent;
import javafx.util.Duration;

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
    private static final int LAYOUT_OFFSET_Y = 0; // Vertical layout offset for brick panel
    private static final int LAYOUT_OFFSET_X = 0; // Horizontal layout offset for brick panel

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
    private Label linesLabel;
    
    @FXML
    private Label levelLabel;
    
    @FXML
    private Label speedLabel;
    
    @FXML
    private Label timeLabel;
    
    @FXML
    private GridPane holdPanel;
    
    @FXML
    private GridPane nextBrickPanel;
    
    @FXML
    private javafx.scene.control.Button muteButton;

    private Rectangle[][] displayMatrix; // Array of rectangles representing the static board background
    private Rectangle[][] holdDisplayMatrix; // Array of rectangles for hold display
    private Rectangle[][] nextDisplayMatrix; // Array of rectangles for next piece display

    private InputEventListener eventListener; // Listener for game events (likely GameController)

    private Rectangle[][] rectangles; // Array of rectangles representing the current falling brick

    private Timeline timeLine; // Timeline for the automatic downward movement of the brick
    private Timeline timeTimer; // Timer to update elapsed time in Endless Mode
    
    // EndlessMode specific fields
    private int currentDropSpeed = 400; // Default speed in milliseconds
    private boolean isMuted = false;
    private double currentBrickOpacity = 1.0;
    private boolean ghostEnabled = true; // Whether ghost piece should be displayed

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
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();

        // Set up keyboard event handling
        gamePanel.setOnKeyPressed(this::handleKeyPressEvent);

        // Initialize game over panel visibility
        gameOverPanel.setVisible(false);
        
        // Apply visual effects - using DropShadow instead of deprecated Reflection
        final DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(8.0);
        dropShadow.setOffsetX(0.0);
        dropShadow.setOffsetY(4.0);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.3));
    }

    /**
     * Handles keyboard press events for game controls.
     * Processes movement, rotation, pause, and new game requests.
     *
     * @param keyEvent The KeyEvent containing information about the key press.
     */
    private void handleKeyPressEvent(KeyEvent keyEvent) {
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
            // Cast eventListener to GameController to call requestPause
            if (eventListener instanceof GameController) {
                ((GameController) eventListener).requestPause();
            }
            keyEvent.consume();
        }

        // Add key handler for new game (N key)
        if (keyEvent.getCode() == KeyCode.N) {
            newGame(null);
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
        // Initialize the display matrix for the static board background
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
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

        // Set initial position of the brick panel using precise calculation for 25px bricks
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * gamePanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(-52.5 + gamePanel.getLayoutY() + brick.getyPosition() * gamePanel.getHgap() + brick.getyPosition() * BRICK_SIZE);

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
        if (!isPause.getValue()) { // Only update position if not paused
            brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * gamePanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            brickPanel.setLayoutY(-52.5 + gamePanel.getLayoutY() + brick.getyPosition() * gamePanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
            
            // Update next piece display
            if (brick.getNextBrickData() != null) {
                updateNextDisplay(brick.getNextBrickData());
            }
        }
    }

    /**
     * Updates the visual representation of the static game board background.
     *
     * @param board The updated board matrix.
     */
    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
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
        // Binding logic might be implemented here if needed based on original code structure
        // For now, left empty as per original placeholder
    }

    /**
     * Handles the game over state.
     * Stops the automatic movement timeline and shows the game over panel.
     */
    public void gameOver() {
        if (timeLine != null) {
            timeLine.stop(); // Stop automatic movement
        }
        if (timeTimer != null) {
            timeTimer.stop();
        }
        gameOverPanel.setVisible(true);
        isGameOver.setValue(true);
        isPause.setValue(false); // Ensure pause is off on game over
        
        // Clear hold and next panels when game is over
        updateHoldDisplay(null);
        updateNextDisplay(null);
    }

    /**
     * Handles the request to start a new game.
     * Stops the timeline, hides the game over panel, requests a new game from the event listener,
     * and restarts the timeline.
     *
     * @param actionEvent The ActionEvent triggering the new game (e.g., from a button).
     */
    public void newGame(ActionEvent actionEvent) {
        if (timeLine != null) {
            timeLine.stop(); // Stop current timeline
        }
        if (timeTimer != null) {
            timeTimer.stop();
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
        
        eventListener.createNewGame(); // Delegate to state/controller
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
     * Delegates the pause request to the GameController.
     *
     * @param actionEvent The ActionEvent triggering the pause/unpause.
     */
    public void pauseGame(ActionEvent actionEvent) {
        // Call the controller's pause request method
        if (eventListener instanceof GameController) {
            ((GameController) eventListener).requestPause();
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
        } else {
            // Game is now resumed - resume the timer
            if (lastPauseStartMillis > 0L) {
                totalPausedMillis += System.currentTimeMillis() - lastPauseStartMillis;
                lastPauseStartMillis = 0L;
            }
            if (timeTimer != null) {
                timeTimer.play();
            }
        }
        gamePanel.requestFocus();
    }

    /**
     * Resumes the game by restarting the timeline if it exists.
     * This method is called when returning from settings to ensure the game continues.
     */
    public void resumeGame() {
        if (timeLine != null && !isGameOver.getValue()) {
            timeLine.play();
            System.out.println("Game timeline resumed");
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
        gamePanel.requestFocus();
    }

    public void updateProgress(int linesClearedInLevel, int targetLines) {
    }

    public void updateStarDisplay(int i) {
    }

    public void updateLevelSpeedDisplay(int levelId) {
    }


    public void updateBestStats(int bestScore, long bestTime) {
    }

    public void showLevelModeUI() {
    }

    public void hideLevelModeUI() {
    }

    public void updateTime(int timeLimitSeconds) {
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
        if (timeTimer != null) {
            timeTimer.stop();
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
            highScoreLabel.setText("High: " + highScore);
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
     */
    @FXML
    public void toggleMute() {
        if (isMuted) {
            // Unmute: restore previous volume
            settings.setMasterVolume(previousVolume);
            muteButton.setText("Mute");
            System.out.println("Audio unmuted - Master Volume: " + (int)(previousVolume * 100) + "%");
        } else {
            // Mute: save current volume and set to 0%
            previousVolume = settings.getMasterVolume();
            settings.setMasterVolume(0.0);
            muteButton.setText("Unmute");
            System.out.println("Audio muted - Master Volume: 0%");
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
        System.out.println("Game mode set to: " + (isTwoPlayer ? "Two-Player" : "Single-Player"));
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
                muteButton.setText("Mute");
                System.out.println("Mute button updated: Volume is " + (int)(currentVolume * 100) + "% - showing Mute");
            } else {
                // Volume is 0, so we're muted
                isMuted = true;
                muteButton.setText("Unmute");
                System.out.println("Mute button updated: Volume is 0% - showing Unmute");
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
     * Player 2: ←/→ - Move, ↑ - Rotate, ↓ - Soft Drop, Ctrl - Hard Drop, Right Shift - Hold, Enter - Rotate CCW
     */
    private void handleTwoPlayerControls(KeyEvent keyEvent) {
        // === Player 1 Controls (WASD Keys) ===
        if (keyEvent.getCode() == KeyCode.A) {
            // Player 1: Move left
            refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.KEYBOARD_PLAYER_1)));
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.D) {
            // Player 1: Move right
            refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.KEYBOARD_PLAYER_1)));
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.W) {
            // Player 1: Rotate clockwise
            refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.KEYBOARD_PLAYER_1)));
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.S) {
            // Player 1: Soft drop
            moveDown(new MoveEvent(EventType.DOWN, EventSource.KEYBOARD_PLAYER_1));
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.SPACE) {
            // Player 1: Hard drop
            moveDown(new MoveEvent(EventType.HARD_DROP, EventSource.KEYBOARD_PLAYER_1));
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.SHIFT) {
            // Player 1: Hold brick
            ViewData result = eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.KEYBOARD_PLAYER_1));
            if (result != null) {
                refreshBrick(result);
                updateHoldDisplay(result.getHoldBrickData());
            }
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.F) {
            // Player 1: Rotate counterclockwise
            refreshBrick(eventListener.onRotateCCWEvent(new MoveEvent(EventType.ROTATE_CCW, EventSource.KEYBOARD_PLAYER_1)));
            keyEvent.consume();
        }
        
        // === Player 2 Controls (Arrow Keys + Special Keys) ===
        if (keyEvent.getCode() == KeyCode.LEFT) {
            // Player 2: Move left
            refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.KEYBOARD_PLAYER_2)));
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.RIGHT) {
            // Player 2: Move right
            refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.KEYBOARD_PLAYER_2)));
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.UP) {
            // Player 2: Rotate clockwise
            refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.KEYBOARD_PLAYER_2)));
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.DOWN) {
            // Player 2: Soft drop
            moveDown(new MoveEvent(EventType.DOWN, EventSource.KEYBOARD_PLAYER_2));
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.CONTROL) {
            // Player 2: Hard drop
            moveDown(new MoveEvent(EventType.HARD_DROP, EventSource.KEYBOARD_PLAYER_2));
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.ALT) {
            // Player 2: Hold brick (Alt key)
            ViewData result = eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.KEYBOARD_PLAYER_2));
            if (result != null) {
                refreshBrick(result);
                updateHoldDisplay(result.getHoldBrickData());
            }
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.ENTER) {
            // Player 2: Rotate counterclockwise
            refreshBrick(eventListener.onRotateCCWEvent(new MoveEvent(EventType.ROTATE_CCW, EventSource.KEYBOARD_PLAYER_2)));
            keyEvent.consume();
        }
    }
    
    /**
     * Shows settings dialog.
     * Opens the settings page with ability to return to current game.
     */
    @FXML
    public void showSettings() {
        System.out.println("Settings dialog requested from game");
        try {
            // Save current game scene for returning
            Scene currentGameScene = gamePanel.getScene();
            Stage stage = (Stage) currentGameScene.getWindow();
            
            // Load settings FXML
            FXMLLoader settingsLoader = new FXMLLoader(getClass().getResource("/settings.fxml"));
            Scene settingsScene = new Scene(settingsLoader.load(), 900, 800);
            
            // Get the settings controller and configure it
            com.comp2042.ui.SettingsController settingsController = settingsLoader.getController();
            settingsController.setStage(stage);
            settingsController.setSavedGameScene(currentGameScene); // Pass current game scene
            
            // Check if game is currently paused
            boolean isGamePaused = false;
            if (eventListener instanceof GameController) {
                // We can't directly access the current state, so we'll assume it's not paused
                // The settings controller will handle the game state properly
                isGamePaused = false; // Default to not paused
            }
            settingsController.setGameController(this, isGamePaused); // Pass game controller and pause state
            
            // Set up keyboard handling to prevent space key conflicts
            settingsController.setupKeyboardHandling(settingsScene);
            
            // Apply settings CSS
            settingsScene.getStylesheets().add(
                getClass().getResource("/settings.css").toExternalForm()
            );
            
            // Switch to settings scene
            stage.setScene(settingsScene);
            stage.setTitle("TETRIS - Settings");
            
            System.out.println("Settings page loaded successfully from game");
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
        System.out.println("Help dialog requested");
        
        try {
            // Create help dialog
            Stage helpStage = new Stage();
            helpStage.setTitle("Gameplay Guide");
            helpStage.initModality(Modality.APPLICATION_MODAL);
            helpStage.setResizable(false);
            
            // Create main container
            VBox mainContainer = new VBox(20);
            mainContainer.setPadding(new Insets(30));
            mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #1A0033, #2D1B69);");
            
            // Title
            Label titleLabel = new Label("Gameplay Guide");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4DFFFF; -fx-alignment: center;");
            titleLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(titleLabel, Priority.ALWAYS);
            
            // Game modes table
            VBox modesContainer = new VBox(15);
            modesContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10; -fx-padding: 20;");
            
            // Mode descriptions
            String[][] modeData = {
                {"Endless Mode", "Play endlessly and aim for the highest score."},
                {"Level Mode", "Clear levels with increasing difficulty and unlock new themes."},
                {"AI Mode", "Play against an intelligent AI opponent and test your skills."},
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
            
            // Close button
            Button closeButton = new Button("Close");
            closeButton.setStyle("-fx-background-color: #4DFFFF; -fx-text-fill: #1A0033; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;");
            closeButton.setOnAction(e -> helpStage.close());
            
            // Create HBox for right-aligned close button
            HBox buttonContainer = new HBox();
            buttonContainer.setAlignment(Pos.CENTER_RIGHT);
            buttonContainer.getChildren().add(closeButton);
            
            // Add all components
            mainContainer.getChildren().addAll(titleLabel, modesContainer, buttonContainer);
            
            // Create scene and show
            Scene helpScene = new Scene(mainContainer, 700, 450);
            helpStage.setScene(helpScene);
            helpStage.show();
            
        } catch (Exception e) {
            System.err.println("Error showing help dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Returns to main menu.
     */
    @FXML
    public void returnToMenu() {
        System.out.println("Returning to main menu");
        try {
            // Load main menu FXML
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainMenu.fxml"));
            Parent root = loader.load();
            
            // Get current stage and set new scene
            Stage stage = (Stage) gamePanel.getScene().getWindow();
            Scene scene = new Scene(root, 900, 800);
            stage.setScene(scene);
            stage.setTitle("Tetris - Main Menu");
        } catch (Exception e) {
            System.err.println("Error loading main menu: " + e.getMessage());
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
                    System.out.println("New Endless Mode game started");
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
                    System.out.println("Leaderboard cleared successfully");
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
                    } else {
                        System.err.println("Current stage is null, cannot switch to menu scene");
                    }
                    System.out.println("Returned to main menu");
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
                    System.out.println("CSS loaded: " + cssPath);
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
            
            System.out.println("Endless Game Over scene loaded successfully");
            
        } catch (Exception e) {
            System.err.println("Error loading Endless Game Over scene: " + e.getMessage());
            e.printStackTrace();
            // Fallback to regular game over
            gameOver();
        }
    }
}