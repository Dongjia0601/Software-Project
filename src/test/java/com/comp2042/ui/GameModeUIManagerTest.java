package com.comp2042.view.manager;

import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GameModeUIManager class.
 * 
 * <p>These tests demonstrate how game mode UI management can be tested
 * independently after SRP refactoring.</p>
 * 
 * @author Dong, Jia
 * @version Phase 3+ - SRP Refactoring Testing
 */
@DisplayName("GameModeUIManager Unit Tests")
class GameModeUIManagerTest {
    
    private GameModeUIManager modeManager;
    private VBox leftObjectiveBox;
    private VBox statisticsBox;
    private VBox bestStatsBox;
    private GridPane holdPanel;
    private GridPane nextBrickPanel;
    private static boolean javaFxInitialized = false;
    
    @BeforeAll
    static void initJavaFX() {
        if (!javaFxInitialized) {
            try {
                Platform.startup(() -> {});
            } catch (IllegalStateException e) {
                // Platform already started
            }
            javaFxInitialized = true;
        }
    }
    
    @BeforeEach
    void setUp() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                modeManager = new GameModeUIManager();
                
                // Create mock UI components
                leftObjectiveBox = new VBox();
                statisticsBox = new VBox();
                bestStatsBox = new VBox();
                holdPanel = new GridPane();
                nextBrickPanel = new GridPane();
                
                // Set components
                modeManager.setLeftObjectiveBox(leftObjectiveBox);
                modeManager.setStatisticsBox(statisticsBox);
                modeManager.setBestStatsBox(bestStatsBox);
                modeManager.setHoldPanel(holdPanel);
                modeManager.setNextBrickPanel(nextBrickPanel);
            } finally {
                latch.countDown();
            }
        });
        
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new RuntimeException("JavaFX setup timeout");
        }
    }
    
    /**
     * Helper method to run test actions on JavaFX thread and wait for completion.
     * 
     * @param action the action to run
     * @throws Exception if action execution fails or times out
     */
    private void runOnJavaFXThread(Runnable action) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                latch.countDown();
            }
        });
        
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new RuntimeException("JavaFX test action timeout");
        }
    }
    
    @Test
    @DisplayName("Should show Level Mode UI correctly")
    void testShowLevelModeUI() throws Exception {
        runOnJavaFXThread(() -> {
            // Act
            modeManager.showLevelModeUI();
            
            // Assert
            assertTrue(modeManager.isLevelMode(), "Should be in level mode");
            assertFalse(modeManager.isEndlessMode(), "Should not be in endless mode");
            assertTrue(leftObjectiveBox.isVisible(), "Objective box should be visible");
            assertTrue(leftObjectiveBox.isManaged(), "Objective box should be managed");
            assertFalse(statisticsBox.isVisible(), "Statistics box should be hidden");
            assertTrue(bestStatsBox.isVisible(), "Best stats should be visible in level mode");
            assertTrue(modeManager.getLevelStartTime() > 0, "Level start time should be set");
        });
    }
    
    @Test
    @DisplayName("Should hide Level Mode UI correctly")
    void testHideLevelModeUI() throws Exception {
        runOnJavaFXThread(() -> {
            // Arrange
            modeManager.showLevelModeUI();
            
            // Act
            modeManager.hideLevelModeUI();
            
            // Assert
            assertFalse(modeManager.isLevelMode(), "Should not be in level mode");
            assertFalse(leftObjectiveBox.isVisible(), "Objective box should be hidden");
            assertTrue(statisticsBox.isVisible(), "Statistics box should be visible");
            assertFalse(bestStatsBox.isVisible(), "Best stats should be hidden");
        });
    }
    
    @Test
    @DisplayName("Should show Endless Mode UI correctly")
    void testShowEndlessModeUI() throws Exception {
        runOnJavaFXThread(() -> {
            // Act
            modeManager.showEndlessModeUI();
            
            // Assert
            assertTrue(modeManager.isEndlessMode(), "Should be in endless mode");
            assertFalse(modeManager.isLevelMode(), "Should not be in level mode");
            assertTrue(statisticsBox.isVisible(), "Statistics box should be visible");
            assertFalse(leftObjectiveBox.isVisible(), "Objective box should be hidden");
            assertFalse(bestStatsBox.isVisible(), "Best stats hidden in endless mode");
        });
    }
    
    @Test
    @DisplayName("Should set Two-Player Mode correctly")
    void testSetTwoPlayerMode() throws Exception {
        runOnJavaFXThread(() -> {
            // Act
            modeManager.setTwoPlayerMode(true);
            
            // Assert
            assertTrue(modeManager.isTwoPlayerMode(), "Should be in two-player mode");
            assertFalse(statisticsBox.isVisible(), "Statistics should be hidden");
            assertFalse(leftObjectiveBox.isVisible(), "Objectives should be hidden");
            assertFalse(bestStatsBox.isVisible(), "Best stats should be hidden");
        });
    }
    
    @Test
    @DisplayName("Should reset to default mode")
    void testResetToDefaultMode() throws Exception {
        runOnJavaFXThread(() -> {
            // Arrange
            modeManager.showLevelModeUI();
            modeManager.setTwoPlayerMode(true);
            
            // Act
            modeManager.resetToDefaultMode();
            
            // Assert
            assertFalse(modeManager.isLevelMode(), "Should not be in level mode");
            assertFalse(modeManager.isEndlessMode(), "Should not be explicitly endless");
            assertFalse(modeManager.isTwoPlayerMode(), "Should not be in two-player mode");
            assertEquals(0, modeManager.getLevelStartTime(), "Level start time should be reset");
        });
    }
    
    @Test
    @DisplayName("Should apply theme to preview displays")
    void testApplyThemeToPreviewDisplays() throws Exception {
        runOnJavaFXThread(() -> {
            // Arrange
            String accentColor = "#FFD700"; // Gold
            
            // Act
            modeManager.applyThemeToPreviewDisplays(accentColor);
            
            // Assert
            assertNotNull(holdPanel.getStyle(), "Hold panel style should be set");
            assertTrue(holdPanel.getStyle().contains(accentColor), 
                      "Hold panel should contain accent color");
            assertNotNull(nextBrickPanel.getStyle(), "Next panel style should be set");
            assertTrue(nextBrickPanel.getStyle().contains(accentColor), 
                      "Next panel should contain accent color");
        });
    }
    
    @Test
    @DisplayName("Should reset preview display theme")
    void testResetPreviewDisplayTheme() throws Exception {
        runOnJavaFXThread(() -> {
            // Act
            modeManager.resetPreviewDisplayTheme();
            
            // Assert
            assertNotNull(holdPanel.getStyle(), "Hold panel style should be set");
            assertTrue(holdPanel.getStyle().contains("#4ECDC4"), 
                      "Should use default cyan theme");
        });
    }
    
    @Test
    @DisplayName("Should track level start time correctly")
    void testLevelStartTimeTracking() throws Exception {
        runOnJavaFXThread(() -> {
            // Act
            long beforeTime = System.currentTimeMillis();
            modeManager.showLevelModeUI();
            long afterTime = System.currentTimeMillis();
            long recordedTime = modeManager.getLevelStartTime();
            
            // Assert
            assertTrue(recordedTime >= beforeTime, "Start time should be after or equal to before time");
            assertTrue(recordedTime <= afterTime, "Start time should be before or equal to after time");
        });
    }
    
    @Test
    @DisplayName("Should handle mode transitions correctly")
    void testModeTransitions() throws Exception {
        runOnJavaFXThread(() -> {
            // Start in endless mode
            modeManager.showEndlessModeUI();
            assertTrue(modeManager.isEndlessMode());
            
            // Transition to level mode
            modeManager.showLevelModeUI();
            assertFalse(modeManager.isEndlessMode());
            assertTrue(modeManager.isLevelMode());
            
            // Transition to two-player mode
            modeManager.setTwoPlayerMode(true);
            assertTrue(modeManager.isTwoPlayerMode());
            
            // Reset
            modeManager.resetToDefaultMode();
            assertFalse(modeManager.isTwoPlayerMode());
            assertFalse(modeManager.isLevelMode());
        });
    }
}
