package com.comp2042.controller.menu;

import com.comp2042.service.audio.SoundManager;
import com.comp2042.model.mode.EndlessModeLeaderboard;
import com.comp2042.model.mode.LeaderboardEntry;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Rectangle2D;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.IntPredicate;

/**
 * Controller for the Endless Mode Game Over screen.
 * 
 * <p>This controller manages the full-screen game over interface for Endless Mode,
 * displaying the final score, leaderboard, and providing options to try again or
 * return to the main menu. The interface is designed as a complete scene rather
 * than an overlay, providing a more immersive experience.</p>
 * 
 * <p>Key Features:</p>
 * <ul>
 *   <li>Full-screen game over interface</li>
 *   <li>Score display with statistics</li>
 *   <li>Interactive leaderboard</li>
 *   <li>Celebration animations for new high scores</li>
 *   <li>Keyboard navigation support</li>
 * </ul>
 */
public class EndlessGameOverController implements Initializable {
    
    private static final String[] RANK_EMOJIS = {"🥇", "🥈", "🥉", "🏅", "🏅"};
    private static final String[] RANK_COLORS = {
        "#FFD700", // Gold
        "#C0C0C0", // Silver  
        "#CD7F32", // Bronze
        "#4DFFFF", // Cyan
        "#9D4EDD"  // Purple
    };
    
    // UI Components
    @FXML private BorderPane rootPane;
    @FXML private Label mainTitleLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label rankLabel;
    @FXML private VBox yourScoreCard;
    @FXML private Label finalScoreLabel;
    @FXML private Label linesLabel;
    @FXML private Label levelLabel;
    @FXML private Label timeLabel;
    @FXML private VBox leaderboardSection;
    @FXML private VBox leaderboardContainer;
    @FXML private Button tryAgainButton;
    @FXML private Button resetLeaderboardButton;
    @FXML private Button backToMenuButton;
    
    // Game data
    private int finalScore;
    private int linesCleared;
    private int level;
    private long playTimeMs;
    private boolean isNewHighScore;
    private int rank;
    
    // Callbacks
    private Runnable onTryAgain;
    private Runnable onBackToMenu;
    private Runnable onResetLeaderboard;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load background image
        setGameOverBackground();

        // Ensure SPACE does not trigger Try Again; we use 'N' to retry
        if (tryAgainButton != null) {
            tryAgainButton.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                if (e.getCode() == KeyCode.SPACE) {
                    e.consume();
                }
            });
        }
    }
    
    /**
     * Loads and sets the background image for the game over screen.
     * Implements intelligent cropping to display the center portion of the image,
     * maintaining aspect ratio while filling the window dimensions (900x800).
     */
    private void setGameOverBackground() {
        if (rootPane == null) {
            return;
        }
        
        try {
            // Load the game over background image
            Image bgImage = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("images/backgrounds/GameOver_bg.jpg")));
            
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
                
                // Calculate what portion of the original image to show
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
            
            // Clear any existing background images (in case method is called multiple times)
            rootPane.getChildren().removeIf(node -> node instanceof ImageView && 
                node.getId() != null && node.getId().equals("gameOverBackground"));
            
            // Set ID for identification
            bgImageView.setId("gameOverBackground");
            
            // Add ImageView as the first child (background layer)
            rootPane.getChildren().addFirst(bgImageView);
            bgImageView.toBack();  // Ensure it's behind all other elements
            
        } catch (Exception e) {
            System.err.println("Error loading game over background image: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Displays the game over screen with final game statistics and leaderboard.
     * Updates all UI components and triggers celebration animation if a new high score was achieved.
     *
     * @param finalScore the final score achieved in this game session
     * @param linesCleared the total number of lines cleared during the game
     * @param level the highest level reached before game over
     * @param playTimeMs the total play time in milliseconds
     * @param isNewHighScore true if this score qualifies as a new high score
     * @param rank the player's rank in the leaderboard (1-5 if in top 5, 0 otherwise)
     */
    public void showGameOver(int finalScore, int linesCleared, int level, long playTimeMs,
                           boolean isNewHighScore, int rank) {
        this.finalScore = finalScore;
        this.linesCleared = linesCleared;
        this.level = level;
        this.playTimeMs = playTimeMs;
        this.isNewHighScore = isNewHighScore;
        this.rank = rank;
        
        updateUI();
        
        // Play celebration animation if new high score
        if (isNewHighScore) {
            playHighScoreCelebration();
        }
    }
    
    /**
     * Updates all UI components with the current game statistics.
     * Displays score, lines, level, time, rank, and builds the leaderboard.
     */
    private void updateUI() {
        // Update main title
        if (isNewHighScore) {
            mainTitleLabel.setText("NEW HIGH SCORE!");
            mainTitleLabel.getStyleClass().add("new-high-score");
        } else {
            mainTitleLabel.setText("GAME OVER");
            mainTitleLabel.getStyleClass().remove("new-high-score");
            subtitleLabel.setVisible(false);
        }
        
        // Update rank display
        if (rank > 0 && rank <= 5) {
            String emoji = RANK_EMOJIS[rank - 1];
            rankLabel.setText(String.format("%s Rank #%d %s", emoji, rank, emoji));
            rankLabel.setStyle("-fx-text-fill: " + RANK_COLORS[rank - 1] + ";");
            rankLabel.setVisible(true);
        } else {
            // Display friendly message when not in top 5
            rankLabel.setText("💫 Keep Trying! 💫");
            rankLabel.setStyle("-fx-text-fill: #AAAAAA;");  // Gray color for not in top 5
            rankLabel.setVisible(true);
        }
        
        // Update score display
        finalScoreLabel.setText(String.format("%,d", finalScore));
        linesLabel.setText(String.valueOf(linesCleared));
        levelLabel.setText(String.valueOf(level));
        
        // Format time
        long seconds = playTimeMs / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
        
        // Ensure Your Score card uses standard style (same for all ranks)
        // Clear any inline styles that might have been set by animations
        if (yourScoreCard != null) {
            yourScoreCard.setStyle(null);  // Remove any inline styles, use CSS class only
        }
        
        // Build leaderboard
        buildLeaderboard();
    }
    
    /**
     * Builds and displays the top 5 leaderboard entries.
     * Highlights the current player's entry if they are in the top 5.
     */
    private void buildLeaderboard() {
        buildLeaderboardInternal(i -> (i + 1 == rank));
    }
    
    /**
     * Internal method to build the leaderboard with customizable highlight logic.
     * 
     * @param shouldHighlight predicate that determines if the entry at index i should be highlighted
     */
    private void buildLeaderboardInternal(IntPredicate shouldHighlight) {
        leaderboardContainer.getChildren().clear();
        
        EndlessModeLeaderboard leaderboard = EndlessModeLeaderboard.getInstance();
        List<LeaderboardEntry> entries = leaderboard.getTopEntries();
        
        // Display each entry (max 5 entries)
        int maxEntries = Math.min(entries.size(), 5);
        for (int i = 0; i < maxEntries; i++) {
            LeaderboardEntry entry = entries.get(i);
            boolean highlight = shouldHighlight.test(i);
            HBox entryBox = createLeaderboardEntry(i + 1, entry, highlight);
            leaderboardContainer.getChildren().add(entryBox);
        }
        
        // Add empty entries if needed to show 5 total
        for (int i = maxEntries; i < 5; i++) {
            HBox entryBox = createEmptyEntry(i + 1);
            leaderboardContainer.getChildren().add(entryBox);
        }
    }
    
    /**
     * Creates a visual leaderboard entry component displaying rank, score, lines, and level.
     *
     * @param rank the rank position (1-5)
     * @param entry the leaderboard entry data
     * @param highlight true if this entry should be highlighted as the current player's entry
     * @return an HBox containing the formatted leaderboard entry
     */
    private HBox createLeaderboardEntry(int rank, LeaderboardEntry entry, boolean highlight) {
        HBox box = new HBox(12); // Reduced spacing from 15 to 12 for more compact layout
        box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        box.getStyleClass().add("leaderboard-entry");
        
        if (highlight) {
            box.getStyleClass().add("current-player");
        }
        
        // Rank badge
        Label rankBadge = new Label(RANK_EMOJIS[rank - 1]);
        rankBadge.getStyleClass().add("rank-badge");
        
        // Rank number
        Label rankNum = new Label("#" + rank);
        rankNum.getStyleClass().add("rank-number");
        rankNum.setStyle("-fx-text-fill: " + RANK_COLORS[rank - 1] + ";");
        
        // Score
        Label scoreLabel = new Label(String.format("%,d pts", entry.getScore()));
        scoreLabel.getStyleClass().add("entry-score");
        scoreLabel.setWrapText(false);
        scoreLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        
        // Lines
        Label linesLabel = new Label(String.format("%d lines", entry.getLinesCleared()));
        linesLabel.getStyleClass().add("entry-lines");
        linesLabel.setWrapText(false);
        linesLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        
        // Level
        Label levelLabel = new Label(String.format("Lv.%d", entry.getLevel()));
        levelLabel.getStyleClass().add("entry-level");
        levelLabel.setWrapText(false);
        levelLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        
        box.getChildren().addAll(rankBadge, rankNum, scoreLabel, linesLabel, levelLabel);
        return box;
    }
    
    /**
     * Creates a placeholder entry for empty leaderboard positions.
     *
     * @param rank the rank position (1-5)
     * @return an HBox containing the placeholder entry with reduced opacity
     */
    private HBox createEmptyEntry(int rank) {
        HBox box = new HBox(15);
        box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        box.getStyleClass().add("leaderboard-entry");
        box.setOpacity(0.5);
        
        Label rankBadge = new Label(rank <= 5 ? RANK_EMOJIS[rank - 1] : "");
        rankBadge.getStyleClass().add("rank-badge");
        
        Label rankNum = new Label("#" + rank);
        rankNum.getStyleClass().add("rank-number");
        if (rank <= 5) {
            rankNum.setStyle("-fx-text-fill: " + RANK_COLORS[rank - 1] + ";");
        }
        
        Label emptyLabel = new Label("--- No Record ---");
        emptyLabel.getStyleClass().add("empty-entry");
        
        box.getChildren().addAll(rankBadge, rankNum, emptyLabel);
        return box;
    }
    
    
    /**
     * Plays a celebration animation when a new high score is achieved.
     * Applies a pulsing scale transition and glow effect to the main title.
     */
    private void playHighScoreCelebration() {
        // Pulse animation for title (reduced scale to avoid layout issues)
        ScaleTransition pulse = new ScaleTransition(Duration.millis(600), mainTitleLabel);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.1);
        pulse.setToY(1.1);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(3);
        
        // Glow effect
        Glow glow = new Glow(0.8);
        mainTitleLabel.setEffect(glow);
        
        pulse.play();
        
    }
    
    /**
     * Handles keyboard input for screen navigation.
     * Supports N (try again), ESCAPE (back to menu), ENTER (activate focused button), and TAB (cycle buttons).
     *
     * @param event the key event to process
     */
    public void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case N:
                onTryAgain();
                break;
            case ESCAPE:
                onBackToMenu();
                break;
            case ENTER:
                // If a button is focused, activate it
                if (tryAgainButton.isFocused()) {
                    onTryAgain();
                } else if (resetLeaderboardButton.isFocused()) {
                    onResetLeaderboard();
                } else if (backToMenuButton.isFocused()) {
                    onBackToMenu();
                }
                break;
            case TAB:
                // Cycle through buttons
                if (tryAgainButton.isFocused()) {
                    resetLeaderboardButton.requestFocus();
                } else if (resetLeaderboardButton.isFocused()) {
                    backToMenuButton.requestFocus();
                } else {
                    tryAgainButton.requestFocus();
                }
                event.consume();
                break;
        }
    }
    
    /**
     * Handles the Try Again button click event.
     * Plays sound effect and executes the registered callback if available.
     */
    @FXML
    private void onTryAgain() {
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        if (onTryAgain != null) {
            onTryAgain.run();
        }
    }
    
    /**
     * Handles the Reset Leaderboard button click event.
     * Plays sound effect and executes the registered callback if available.
     */
    @FXML
    private void onResetLeaderboard() {
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        if (onResetLeaderboard != null) {
            onResetLeaderboard.run();
        }
    }
    
    /**
     * Handles the Back to Menu button click event.
     * Plays sound effect and executes the registered callback if available.
     */
    @FXML
    private void onBackToMenu() {
        // Play button click sound
        SoundManager.getInstance().playButtonClickSound();
        if (onBackToMenu != null) {
            onBackToMenu.run();
        }
    }
    
    /**
     * Sets the callback executed when the Try Again button is clicked.
     *
     * @param callback the runnable to execute, may be null
     */
    public void setOnTryAgain(Runnable callback) {
        this.onTryAgain = callback;
    }
    
    /**
     * Sets the callback executed when the Reset Leaderboard button is clicked.
     *
     * @param callback the runnable to execute, may be null
     */
    public void setOnResetLeaderboard(Runnable callback) {
        this.onResetLeaderboard = callback;
    }
    
    /**
     * Sets the callback executed when the Back to Menu button is clicked.
     *
     * @param callback the runnable to execute, may be null
     */
    public void setOnBackToMenu(Runnable callback) {
        this.onBackToMenu = callback;
    }
    
    /**
     * Refreshes the leaderboard display with updated data.
     * Typically called after the leaderboard has been cleared or modified.
     */
    public void refreshLeaderboard() {
        buildLeaderboardInternal(i -> (i == 0 && isNewHighScore));
    }
}
