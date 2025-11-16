package com.comp2042.view.manager;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Manages game mode-specific UI components and visibility.
 * 
 * <p>This class is responsible for showing/hiding UI elements based on the current
 * game mode (Endless Mode, Level Mode, or Two-Player Mode), and applying theme
 * customizations to preview displays.</p>
 * 
 * <p><strong>Responsibilities:</strong></p>
 * <ul>
 *   <li>Show/hide Level Mode UI components (objectives, progress, timer)</li>
 *   <li>Show/hide Endless Mode UI components (statistics, best stats)</li>
 *   <li>Manage Two-Player Mode UI state</li>
 *   <li>Apply theme colors to preview displays (Hold/Next panels)</li>
 *   <li>Track game mode state</li>
 * </ul>
 * 
 * <p><strong>Design Pattern:</strong> Extracted from GuiController to adhere to Single Responsibility Principle (SRP)</p>
 * 
 * @author Dong, Jia
 * @version Phase 3+ - SRP Refactoring (Continued Optimization)
 */
public class GameModeUIManager {
    
    // UI Components for different modes
    private VBox leftObjectiveBox;    // Level mode objectives panel
    private VBox statisticsBox;       // Endless mode statistics panel
    private VBox bestStatsBox;        // Best statistics panel
    private GridPane holdPanel;       // Hold piece preview panel
    private GridPane nextBrickPanel;  // Next piece preview panel
    
    // Game mode state tracking
    private boolean isLevelMode = false;
    private boolean isEndlessMode = false;
    private boolean isTwoPlayerMode = false;
    
    // Timing information
    private long levelStartTime = 0;
    
    /**
     * Constructs a GameModeUIManager.
     */
    public GameModeUIManager() {
        // Constructor intentionally empty - components set via setters
    }
    
    // ==================== Component Setters ====================
    
    /**
     * Sets the level objective box component.
     * 
     * @param leftObjectiveBox the level objective box
     */
    public void setLeftObjectiveBox(VBox leftObjectiveBox) {
        this.leftObjectiveBox = leftObjectiveBox;
    }
    
    /**
     * Sets the statistics box component.
     * 
     * @param statisticsBox the statistics box
     */
    public void setStatisticsBox(VBox statisticsBox) {
        this.statisticsBox = statisticsBox;
    }
    
    /**
     * Sets the best statistics box component.
     * 
     * @param bestStatsBox the best statistics box
     */
    public void setBestStatsBox(VBox bestStatsBox) {
        this.bestStatsBox = bestStatsBox;
    }
    
    /**
     * Sets the hold panel component.
     * 
     * @param holdPanel the hold panel
     */
    public void setHoldPanel(GridPane holdPanel) {
        this.holdPanel = holdPanel;
    }
    
    /**
     * Sets the next brick panel component.
     * 
     * @param nextBrickPanel the next brick panel
     */
    public void setNextBrickPanel(GridPane nextBrickPanel) {
        this.nextBrickPanel = nextBrickPanel;
    }
    
    // ==================== Mode Management ====================
    
    /**
     * Shows the Level Mode UI and hides other mode-specific UI.
     * Sets up the level objective box and best statistics display.
     */
    public void showLevelModeUI() {
        isLevelMode = true;
        isEndlessMode = false;
        levelStartTime = System.currentTimeMillis();
        
        // Show level objectives
        if (leftObjectiveBox != null) {
            leftObjectiveBox.setManaged(true);
            leftObjectiveBox.setVisible(true);
        }
        
        // Hide endless mode statistics
        if (statisticsBox != null) {
            statisticsBox.setManaged(false);
            statisticsBox.setVisible(false);
        }
        
        // Show best stats for level mode
        if (bestStatsBox != null) {
            bestStatsBox.setVisible(true);
            bestStatsBox.setManaged(true);
        }
    }
    
    /**
     * Hides the Level Mode UI.
     * Resets level mode state and shows endless mode UI if applicable.
     */
    public void hideLevelModeUI() {
        isLevelMode = false;
        
        // Hide level objectives
        if (leftObjectiveBox != null) {
            leftObjectiveBox.setManaged(false);
            leftObjectiveBox.setVisible(false);
        }
        
        // Show statistics for endless mode
        if (statisticsBox != null) {
            statisticsBox.setManaged(true);
            statisticsBox.setVisible(true);
        }
        
        // Hide best stats when not in level mode
        if (bestStatsBox != null) {
            bestStatsBox.setVisible(false);
            bestStatsBox.setManaged(false);
        }
    }
    
    /**
     * Shows the Endless Mode UI.
     * Displays statistics box and hides level-specific UI.
     */
    public void showEndlessModeUI() {
        isEndlessMode = true;
        isLevelMode = false;
        
        // Show endless mode statistics
        if (statisticsBox != null) {
            statisticsBox.setManaged(true);
            statisticsBox.setVisible(true);
        }
        
        // Hide level objectives
        if (leftObjectiveBox != null) {
            leftObjectiveBox.setManaged(false);
            leftObjectiveBox.setVisible(false);
        }
        
        // Hide best stats in endless mode (shown in game over instead)
        if (bestStatsBox != null) {
            bestStatsBox.setVisible(false);
            bestStatsBox.setManaged(false);
        }
    }
    
    /**
     * Sets the Endless Mode state.
     * 
     * @param endlessMode true to enable endless mode
     */
    public void setEndlessMode(boolean endlessMode) {
        this.isEndlessMode = endlessMode;
        if (endlessMode) {
            showEndlessModeUI();
        }
    }
    
    /**
     * Sets the Two-Player Mode state.
     * 
     * @param twoPlayerMode true to enable two-player mode
     */
    public void setTwoPlayerMode(boolean twoPlayerMode) {
        this.isTwoPlayerMode = twoPlayerMode;
        
        // Two-player mode specific UI adjustments
        if (twoPlayerMode) {
            // Hide single-player specific UI
            if (statisticsBox != null) {
                statisticsBox.setManaged(false);
                statisticsBox.setVisible(false);
            }
            if (leftObjectiveBox != null) {
                leftObjectiveBox.setManaged(false);
                leftObjectiveBox.setVisible(false);
            }
            if (bestStatsBox != null) {
                bestStatsBox.setVisible(false);
                bestStatsBox.setManaged(false);
            }
        }
    }
    
    /**
     * Resets all mode states to default (single-player endless mode).
     */
    public void resetToDefaultMode() {
        isLevelMode = false;
        isEndlessMode = false;
        isTwoPlayerMode = false;
        levelStartTime = 0;
        
        showEndlessModeUI();
    }
    
    // ==================== Theme Management ====================
    
    /**
     * Applies theme colors to Hold and Next preview displays.
     * Creates consistent styling with the current level theme.
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
    
    /**
     * Resets preview display styling to default theme.
     */
    public void resetPreviewDisplayTheme() {
        applyThemeToPreviewDisplays("#4ECDC4"); // Default cyan/turquoise theme
    }
    
    // ==================== State Getters ====================
    
    /**
     * Checks if currently in Level Mode.
     * 
     * @return true if in level mode
     */
    public boolean isLevelMode() {
        return isLevelMode;
    }
    
    /**
     * Checks if currently in Endless Mode.
     * 
     * @return true if in endless mode
     */
    public boolean isEndlessMode() {
        return isEndlessMode;
    }
    
    /**
     * Checks if currently in Two-Player Mode.
     * 
     * @return true if in two-player mode
     */
    public boolean isTwoPlayerMode() {
        return isTwoPlayerMode;
    }
    
    /**
     * Gets the level start time in milliseconds.
     * 
     * @return the level start time, or 0 if not in level mode
     */
    public long getLevelStartTime() {
        return levelStartTime;
    }
    
    /**
     * Sets the level start time.
     * 
     * @param levelStartTime the level start time in milliseconds
     */
    public void setLevelStartTime(long levelStartTime) {
        this.levelStartTime = levelStartTime;
    }
}

