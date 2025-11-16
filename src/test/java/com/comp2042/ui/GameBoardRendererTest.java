package com.comp2042.view.manager;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import javafx.application.Platform;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GameBoardRenderer class.
 * 
 * <p>These tests demonstrate the testability benefits of SRP refactoring.
 * GameBoardRenderer can be tested independently without requiring the entire
 * GuiController infrastructure.</p>
 * 
 * @author Dong, Jia
 * @version Phase 3+ - SRP Refactoring Testing
 */
@DisplayName("GameBoardRenderer Unit Tests")
class GameBoardRendererTest {
    
    private GameBoardRenderer renderer;
    private GridPane testGridPane;
    private static boolean javaFxInitialized = false;
    
    /**
     * Initialize JavaFX toolkit once for all tests.
     */
    @BeforeAll
    static void initJavaFX() {
        if (!javaFxInitialized) {
            // Initialize JavaFX Platform
            Platform.startup(() -> {});
            javaFxInitialized = true;
        }
    }
    
    /**
     * Set up test fixtures before each test.
     */
    @BeforeEach
    void setUp() {
        // Run on JavaFX Application Thread
        Platform.runLater(() -> {
            testGridPane = new GridPane();
            testGridPane.setHgap(1);
            testGridPane.setVgap(1);
            renderer = new GameBoardRenderer(testGridPane);
        });
        
        // Wait for JavaFX thread to complete
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    @DisplayName("Should initialize board with correct dimensions")
    void testInitializeBoard() {
        Platform.runLater(() -> {
            // Arrange
            int rows = 20;
            int cols = 10;
            int[][] boardMatrix = new int[rows][cols];
            
            // Act
            renderer.initializeBoard(boardMatrix);
            Rectangle[][] displayMatrix = renderer.getDisplayMatrix();
            
            // Assert
            assertNotNull(displayMatrix, "Display matrix should not be null");
            assertEquals(rows, displayMatrix.length, "Display matrix should have correct number of rows");
            assertEquals(cols, displayMatrix[0].length, "Display matrix should have correct number of columns");
            
            // Verify all rectangles are created
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    assertNotNull(displayMatrix[i][j], "Rectangle at [" + i + "][" + j + "] should not be null");
                    assertEquals(Color.TRANSPARENT, displayMatrix[i][j].getFill(), 
                                "Initial cell should be transparent");
                }
            }
        });
    }
    
    @Test
    @DisplayName("Should handle null board matrix gracefully")
    void testInitializeBoardWithNull() {
        Platform.runLater(() -> {
            // Act
            renderer.initializeBoard(null);
            Rectangle[][] displayMatrix = renderer.getDisplayMatrix();
            
            // Assert
            assertNull(displayMatrix, "Display matrix should remain null for null input");
        });
    }
    
    @Test
    @DisplayName("Should refresh board background with incremental updates")
    void testRefreshGameBackground() {
        Platform.runLater(() -> {
            // Arrange
            int rows = 20;
            int cols = 10;
            int[][] boardMatrix = new int[rows][cols];
            renderer.initializeBoard(boardMatrix);
            
            // Modify board state
            boardMatrix[0][0] = 1; // Add a brick
            boardMatrix[1][1] = 2; // Add another brick
            
            // Act
            renderer.refreshGameBackground(boardMatrix);
            Rectangle[][] displayMatrix = renderer.getDisplayMatrix();
            
            // Assert
            assertNotEquals(Color.TRANSPARENT, displayMatrix[0][0].getFill(), 
                           "Cell [0][0] should be colored");
            assertNotEquals(Color.TRANSPARENT, displayMatrix[1][1].getFill(), 
                           "Cell [1][1] should be colored");
            assertEquals(Color.TRANSPARENT, displayMatrix[2][2].getFill(), 
                        "Cell [2][2] should remain transparent");
        });
    }
    
    @Test
    @DisplayName("Should clear board display correctly")
    void testClearBoard() {
        Platform.runLater(() -> {
            // Arrange
            int rows = 20;
            int cols = 10;
            int[][] boardMatrix = new int[rows][cols];
            boardMatrix[0][0] = 1; // Add a brick
            renderer.initializeBoard(boardMatrix);
            renderer.refreshGameBackground(boardMatrix);
            
            // Act
            renderer.clearBoard();
            Rectangle[][] displayMatrix = renderer.getDisplayMatrix();
            
            // Assert
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    assertEquals(Color.TRANSPARENT, displayMatrix[i][j].getFill(), 
                                "All cells should be transparent after clear");
                }
            }
        });
    }
    
    @Test
    @DisplayName("Should reset renderer state")
    void testReset() {
        Platform.runLater(() -> {
            // Arrange
            int rows = 20;
            int cols = 10;
            int[][] boardMatrix = new int[rows][cols];
            renderer.initializeBoard(boardMatrix);
            
            // Act
            renderer.reset();
            
            // Assert
            assertNull(renderer.getDisplayMatrix(), "Display matrix should be null after reset");
        });
    }
    
    @Test
    @DisplayName("Should calculate grid positions correctly")
    void testCalculateGridPositions() {
        Platform.runLater(() -> {
            // Arrange
            int rows = 20;
            int cols = 10;
            int[][] boardMatrix = new int[rows][cols];
            renderer.initializeBoard(boardMatrix);
            
            // Act
            double x0 = renderer.calculateGridX(0);
            double x5 = renderer.calculateGridX(5);
            double y0 = renderer.calculateGridY(0);
            double y10 = renderer.calculateGridY(10);
            
            // Assert
            assertTrue(x5 > x0, "X position should increase with column");
            assertTrue(y10 > y0, "Y position should increase with row");
        });
    }
    
    @Test
    @DisplayName("Should handle incremental rendering optimization")
    void testIncrementalRenderingOptimization() {
        Platform.runLater(() -> {
            // Arrange
            int rows = 20;
            int cols = 10;
            int[][] boardMatrix = new int[rows][cols];
            renderer.initializeBoard(boardMatrix);
            
            // First update
            boardMatrix[0][0] = 1;
            renderer.refreshGameBackground(boardMatrix);
            
            // Second update - only one cell changes
            boardMatrix[0][1] = 2;
            renderer.refreshGameBackground(boardMatrix);
            
            Rectangle[][] displayMatrix = renderer.getDisplayMatrix();
            
            // Assert
            assertNotEquals(Color.TRANSPARENT, displayMatrix[0][0].getFill(), 
                           "First cell should still be colored");
            assertNotEquals(Color.TRANSPARENT, displayMatrix[0][1].getFill(), 
                           "Second cell should now be colored");
            assertEquals(Color.TRANSPARENT, displayMatrix[0][2].getFill(), 
                        "Unchanged cells should remain transparent");
        });
    }
}

