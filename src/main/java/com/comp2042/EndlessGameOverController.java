package com.comp2042;

import com.comp2042.game.EndlessModeLeaderboard;
import com.comp2042.game.LeaderboardEntry;
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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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
    @FXML private Label mainTitleLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label rankLabel;
    @FXML private VBox yourScoreCard;
    @FXML private Label finalScoreLabel;
    @FXML private Label linesLabel;
    @FXML private Label timeLabel;
    @FXML private VBox leaderboardSection;
    @FXML private VBox leaderboardContainer;
    @FXML private Button tryAgainButton;
    @FXML private Button resetLeaderboardButton;
    @FXML private Button backToMenuButton;
    
    // Game data
    private int finalScore;
    private int linesCleared;
    private long playTimeMs;
    private boolean isNewHighScore;
    private int rank;
    
    // Callbacks
    private Runnable onTryAgain;
    private Runnable onBackToMenu;
    private Runnable onResetLeaderboard;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up keyboard navigation
        setupKeyboardNavigation();
        
        // Set up button hover effects
        setupButtonEffects();
        
        // Apply button styles directly via Java code
        applyButtonStyles();

        // Ensure SPACE does not trigger Try Again; we use 'N' to retry
        tryAgainButton.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            if (e.getCode() == KeyCode.SPACE) {
                e.consume();
            }
        });
    }
    
    /**
     * Displays the game over screen with the provided game data.
     * 
     * @param finalScore the final score achieved
     * @param linesCleared the number of lines cleared
     * @param playTimeMs the play time in milliseconds
     * @param isNewHighScore whether this is a new high score
     * @param rank the player's rank (1-5), or 0 if not in top 5
     */
    public void showGameOver(int finalScore, int linesCleared, long playTimeMs,
                           boolean isNewHighScore, int rank) {
        this.finalScore = finalScore;
        this.linesCleared = linesCleared;
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
     * Updates the UI with current game data.
     */
    private void updateUI() {
        // Update main title
        if (isNewHighScore) {
            mainTitleLabel.setText("NEW HIGH SCORE!");
            mainTitleLabel.getStyleClass().add("new-high-score");
            subtitleLabel.setText("Congratulations! You've achieved a new record!");
            subtitleLabel.setVisible(true);
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
            rankLabel.setVisible(false);
        }
        
        // Update score display
        finalScoreLabel.setText(String.format("%,d", finalScore));
        linesLabel.setText(String.valueOf(linesCleared));
        
        // Format time
        long seconds = playTimeMs / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
        
        // Build leaderboard
        buildLeaderboard();
    }
    
    /**
     * Builds and displays the leaderboard.
     */
    private void buildLeaderboard() {
        leaderboardContainer.getChildren().clear();
        
        EndlessModeLeaderboard leaderboard = EndlessModeLeaderboard.getInstance();
        List<LeaderboardEntry> entries = leaderboard.getTopEntries();
        
        // Display each entry (max 5 entries)
        int maxEntries = Math.min(entries.size(), 5);
        for (int i = 0; i < maxEntries; i++) {
            LeaderboardEntry entry = entries.get(i);
            boolean isCurrentGame = (i + 1 == rank);
            HBox entryBox = createLeaderboardEntry(i + 1, entry, isCurrentGame);
            leaderboardContainer.getChildren().add(entryBox);
        }
        
        // Add empty entries if needed to show 5 total
        for (int i = maxEntries; i < 5; i++) {
            HBox entryBox = createEmptyEntry(i + 1);
            leaderboardContainer.getChildren().add(entryBox);
        }
    }
    
    /**
     * Creates a leaderboard entry display.
     */
    private HBox createLeaderboardEntry(int rank, LeaderboardEntry entry, boolean highlight) {
        HBox box = new HBox(15);
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
        
        // Lines
        Label linesLabel = new Label(String.format("%d lines", entry.getLinesCleared()));
        linesLabel.getStyleClass().add("entry-lines");
        
        // Level
        Label levelLabel = new Label(String.format("Lv.%d", entry.getLevel()));
        levelLabel.getStyleClass().add("entry-level");
        
        box.getChildren().addAll(rankBadge, rankNum, scoreLabel, linesLabel, levelLabel);
        return box;
    }
    
    /**
     * Creates an empty entry placeholder.
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
     * Sets up keyboard navigation.
     */
    private void setupKeyboardNavigation() {
        // This will be handled by the scene's key event handler
    }
    
    /**
     * Applies button styles directly via Java code.
     */
    private void applyButtonStyles() {
        // Try Again Button - Green styling
        tryAgainButton.setStyle(
            "-fx-background-color: #00FF88;" +
            "-fx-border-color: #00FFAA;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 15px;" +
            "-fx-background-radius: 15px;" +
            "-fx-text-fill: #FFFFFF;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12px 25px;" +
            "-fx-min-width: 180px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 255, 136, 0.8), 10, 0, 0, 0);"
        );
        
        // Back to Menu Button - Purple styling
        backToMenuButton.setStyle(
            "-fx-background-color: #8A2BE2;" +
            "-fx-border-color: #9D4EDD;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 15px;" +
            "-fx-background-radius: 15px;" +
            "-fx-text-fill: #FFFFFF;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12px 25px;" +
            "-fx-min-width: 180px;" +
            "-fx-effect: dropshadow(gaussian, rgba(138, 43, 226, 0.8), 10, 0, 0, 0);"
        );
    }
    
    /**
     * Sets up button hover effects and animations.
     */
    private void setupButtonEffects() {
        // Try Again button effects
        tryAgainButton.setOnMouseEntered(e -> {
            // Change color on hover (no transform)
            tryAgainButton.setStyle(
                "-fx-background-color: #00FFAA;" +
                "-fx-border-color: #00FFCC;" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 15px;" +
                "-fx-background-radius: 15px;" +
                "-fx-text-fill: #FFFFFF;" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 12px 25px;" +
                "-fx-min-width: 180px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 255, 170, 1.0), 15, 0, 0, 0);"
            );
        });
        
        tryAgainButton.setOnMouseExited(e -> {
            // Reset to original color
            tryAgainButton.setStyle(
                "-fx-background-color: #00FF88;" +
                "-fx-border-color: #00FFAA;" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 15px;" +
                "-fx-background-radius: 15px;" +
                "-fx-text-fill: #FFFFFF;" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 12px 25px;" +
                "-fx-min-width: 180px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 255, 136, 0.8), 10, 0, 0, 0);"
            );
        });
        
        tryAgainButton.setOnMousePressed(e -> { /* no transform on press */ });
        tryAgainButton.setOnMouseReleased(e -> { /* no transform on release */ });
        
        // Back to Menu button effects
        backToMenuButton.setOnMouseEntered(e -> {
            // Change color on hover (no transform)
            backToMenuButton.setStyle(
                "-fx-background-color: #9D4EDD;" +
                "-fx-border-color: #B565F0;" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 15px;" +
                "-fx-background-radius: 15px;" +
                "-fx-text-fill: #FFFFFF;" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 12px 25px;" +
                "-fx-min-width: 180px;" +
                "-fx-effect: dropshadow(gaussian, rgba(157, 78, 221, 1.0), 15, 0, 0, 0);"
            );
        });
        
        backToMenuButton.setOnMouseExited(e -> {
            // Reset to original color
            backToMenuButton.setStyle(
                "-fx-background-color: #8A2BE2;" +
                "-fx-border-color: #9D4EDD;" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 15px;" +
                "-fx-background-radius: 15px;" +
                "-fx-text-fill: #FFFFFF;" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 12px 25px;" +
                "-fx-min-width: 180px;" +
                "-fx-effect: dropshadow(gaussian, rgba(138, 43, 226, 0.8), 10, 0, 0, 0);"
            );
        });
        backToMenuButton.setOnMousePressed(e -> { /* no transform on press */ });
        backToMenuButton.setOnMouseReleased(e -> { /* no transform on release */ });
    }
    
    /**
     * Plays celebration animation for new high scores.
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
        
        // Confetti animation (simplified)
        playConfettiAnimation();
    }
    
    /**
     * Plays a simple confetti animation.
     */
    private void playConfettiAnimation() {
        // This could be enhanced with particle effects
        // For now, we'll use a simple color animation
        Timeline confetti = new Timeline(
            new KeyFrame(Duration.ZERO, e -> yourScoreCard.setStyle("-fx-background-color: rgba(255, 215, 0, 0.1);")),
            new KeyFrame(Duration.millis(200), e -> yourScoreCard.setStyle("-fx-background-color: rgba(255, 0, 255, 0.1);")),
            new KeyFrame(Duration.millis(400), e -> yourScoreCard.setStyle("-fx-background-color: rgba(0, 255, 255, 0.1);")),
            new KeyFrame(Duration.millis(600), e -> yourScoreCard.setStyle("-fx-background-color: rgba(255, 255, 0, 0.1);")),
            new KeyFrame(Duration.millis(800), e -> yourScoreCard.setStyle("-fx-background-color: rgba(138, 43, 226, 0.15);"))
        );
        confetti.setCycleCount(3);
        confetti.play();
    }
    
    /**
     * Handles keyboard input for navigation.
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
     * Handles Try Again button click.
     */
    @FXML
    private void onTryAgain() {
        if (onTryAgain != null) {
            onTryAgain.run();
        }
    }
    
    /**
     * Handles Reset Leaderboard button click.
     */
    @FXML
    private void onResetLeaderboard() {
        if (onResetLeaderboard != null) {
            onResetLeaderboard.run();
        }
    }
    
    /**
     * Handles Back to Menu button click.
     */
    @FXML
    private void onBackToMenu() {
        if (onBackToMenu != null) {
            onBackToMenu.run();
        }
    }
    
    /**
     * Sets the callback for Try Again action.
     */
    public void setOnTryAgain(Runnable callback) {
        this.onTryAgain = callback;
    }
    
    /**
     * Sets the callback for Reset Leaderboard action.
     */
    public void setOnResetLeaderboard(Runnable callback) {
        this.onResetLeaderboard = callback;
    }
    
    /**
     * Sets the callback for Back to Menu action.
     */
    public void setOnBackToMenu(Runnable callback) {
        this.onBackToMenu = callback;
    }
    
    /**
     * Refreshes the leaderboard display after clearing.
     */
    public void refreshLeaderboard() {
        // Clear current leaderboard entries
        leaderboardContainer.getChildren().clear();
        
        // Get fresh leaderboard data
        List<LeaderboardEntry> entries = EndlessModeLeaderboard.getInstance().getTopEntries();
        
        // Rebuild leaderboard display
        for (int i = 0; i < 5; i++) {
            if (i < entries.size()) {
                LeaderboardEntry entry = entries.get(i);
                boolean isCurrentPlayer = (i == 0 && isNewHighScore); // Only highlight if it's a new high score
                HBox entryBox = createLeaderboardEntry(i + 1, entry, isCurrentPlayer);
                leaderboardContainer.getChildren().add(entryBox);
            } else {
                HBox emptyEntry = createEmptyEntry(i + 1);
                leaderboardContainer.getChildren().add(emptyEntry);
            }
        }
    }
}
