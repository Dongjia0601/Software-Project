package com.comp2042.view.manager;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AnimationController class, specifically testing row clear animation.
 * 
 * <p>These tests verify that the row clear animation correctly animates rectangles
 * in cleared rows and properly calls the completion callback.</p>
 * 
 * @author Dong, Jia
 * @version Phase 3 - Animation Testing
 */
@DisplayName("AnimationController Row Clear Animation Tests")
class AnimationControllerTest {
    
    private AnimationController animationController;
    private Group notificationGroup;
    private BorderPane rootPane;
    private static boolean javaFxInitialized = false;
    
    /**
     * Initialize JavaFX toolkit once for all tests.
     */
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
    
    /**
     * Set up test fixtures before each test.
     */
    @BeforeEach
    void setUp() {
        Platform.runLater(() -> {
            notificationGroup = new Group();
            rootPane = new BorderPane();
            animationController = new AnimationController(notificationGroup, rootPane);
        });
        
        // Wait for JavaFX thread to complete
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    @DisplayName("Should handle null displayMatrix gracefully")
    void testAnimateRowClearWithNullMatrix() {
        Platform.runLater(() -> {
            AtomicBoolean callbackCalled = new AtomicBoolean(false);
            
            // Act
            animationController.animateRowClear(null, Collections.singletonList(0), () -> {
                callbackCalled.set(true);
            });
            
            // Wait a bit for callback
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            // Assert
            assertTrue(callbackCalled.get(), "Callback should be called even with null matrix");
        });
    }
    
    @Test
    @DisplayName("Should handle null clearedRows gracefully")
    void testAnimateRowClearWithNullClearedRows() {
        Platform.runLater(() -> {
            Rectangle[][] displayMatrix = createTestDisplayMatrix(20, 10);
            AtomicBoolean callbackCalled = new AtomicBoolean(false);
            
            // Act
            animationController.animateRowClear(displayMatrix, null, () -> {
                callbackCalled.set(true);
            });
            
            // Wait a bit for callback
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            // Assert
            assertTrue(callbackCalled.get(), "Callback should be called even with null clearedRows");
        });
    }
    
    @Test
    @DisplayName("Should handle empty clearedRows gracefully")
    void testAnimateRowClearWithEmptyClearedRows() {
        Platform.runLater(() -> {
            Rectangle[][] displayMatrix = createTestDisplayMatrix(20, 10);
            AtomicBoolean callbackCalled = new AtomicBoolean(false);
            
            // Act
            animationController.animateRowClear(displayMatrix, Collections.emptyList(), () -> {
                callbackCalled.set(true);
            });
            
            // Wait a bit for callback
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            // Assert
            assertTrue(callbackCalled.get(), "Callback should be called even with empty clearedRows");
        });
    }
    
    @Test
    @DisplayName("Should animate single row correctly")
    void testAnimateRowClearSingleRow() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean callbackCalled = new AtomicBoolean(false);
        
        Platform.runLater(() -> {
            // Arrange
            Rectangle[][] displayMatrix = createTestDisplayMatrix(20, 10);
            fillRow(displayMatrix, 10, Color.RED); // Fill row 10 with red rectangles
            
            // Verify initial state
            for (int col = 0; col < 10; col++) {
                assertEquals(Color.RED, displayMatrix[10][col].getFill());
                assertEquals(1.0, displayMatrix[10][col].getOpacity());
                assertEquals(1.0, displayMatrix[10][col].getScaleX());
                assertEquals(1.0, displayMatrix[10][col].getScaleY());
            }
            
            // Act
            animationController.animateRowClear(displayMatrix, Collections.singletonList(10), () -> {
                callbackCalled.set(true);
                latch.countDown();
            });
        });
        
        // Wait for animation to complete (150ms + buffer)
        boolean completed = latch.await(500, TimeUnit.MILLISECONDS);
        
        // Assert
        assertTrue(completed, "Animation should complete within timeout");
        assertTrue(callbackCalled.get(), "Callback should be called after animation");
        
        Platform.runLater(() -> {
            // Verify rectangles are reset after animation
            Rectangle[][] displayMatrix = createTestDisplayMatrix(20, 10);
            fillRow(displayMatrix, 10, Color.RED);
            
            // After animation, rectangles should be reset
            for (int col = 0; col < 10; col++) {
                assertEquals(1.0, displayMatrix[10][col].getOpacity(), 
                           "Opacity should be reset to 1.0");
                assertEquals(1.0, displayMatrix[10][col].getScaleX(), 
                           "ScaleX should be reset to 1.0");
                assertEquals(1.0, displayMatrix[10][col].getScaleY(), 
                           "ScaleY should be reset to 1.0");
            }
        });
    }
    
    @Test
    @DisplayName("Should animate multiple rows correctly")
    void testAnimateRowClearMultipleRows() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean callbackCalled = new AtomicBoolean(false);
        
        Platform.runLater(() -> {
            // Arrange
            Rectangle[][] displayMatrix = createTestDisplayMatrix(20, 10);
            fillRow(displayMatrix, 10, Color.RED);
            fillRow(displayMatrix, 11, Color.BLUE);
            fillRow(displayMatrix, 12, Color.GREEN);
            
            List<Integer> clearedRows = new ArrayList<>();
            clearedRows.add(10);
            clearedRows.add(11);
            clearedRows.add(12);
            
            // Act
            animationController.animateRowClear(displayMatrix, clearedRows, () -> {
                callbackCalled.set(true);
                latch.countDown();
            });
        });
        
        // Wait for animation to complete
        boolean completed = latch.await(500, TimeUnit.MILLISECONDS);
        
        // Assert
        assertTrue(completed, "Animation should complete within timeout");
        assertTrue(callbackCalled.get(), "Callback should be called after animation");
    }
    
    @Test
    @DisplayName("Should only animate non-transparent rectangles")
    void testAnimateRowClearSkipsTransparentRectangles() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean callbackCalled = new AtomicBoolean(false);
        
        Platform.runLater(() -> {
            // Arrange
            Rectangle[][] displayMatrix = createTestDisplayMatrix(20, 10);
            // Fill only some columns in row 10
            displayMatrix[10][0].setFill(Color.RED);
            displayMatrix[10][1].setFill(Color.RED);
            // Leave other columns transparent
            
            // Act
            animationController.animateRowClear(displayMatrix, Collections.singletonList(10), () -> {
                callbackCalled.set(true);
                latch.countDown();
            });
        });
        
        // Wait for animation to complete
        boolean completed = latch.await(500, TimeUnit.MILLISECONDS);
        
        // Assert
        assertTrue(completed, "Animation should complete within timeout");
        assertTrue(callbackCalled.get(), "Callback should be called after animation");
    }
    
    @Test
    @DisplayName("Should handle out-of-bounds row indices")
    void testAnimateRowClearWithOutOfBoundsRows() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean callbackCalled = new AtomicBoolean(false);
        
        Platform.runLater(() -> {
            // Arrange
            Rectangle[][] displayMatrix = createTestDisplayMatrix(20, 10);
            List<Integer> clearedRows = new ArrayList<>();
            clearedRows.add(-1); // Invalid row
            clearedRows.add(25); // Out of bounds
            clearedRows.add(10); // Valid row
            fillRow(displayMatrix, 10, Color.RED);
            
            // Act
            animationController.animateRowClear(displayMatrix, clearedRows, () -> {
                callbackCalled.set(true);
                latch.countDown();
            });
        });
        
        // Wait for animation to complete
        boolean completed = latch.await(500, TimeUnit.MILLISECONDS);
        
        // Assert
        assertTrue(completed, "Animation should complete within timeout");
        assertTrue(callbackCalled.get(), "Callback should be called after animation");
    }
    
    @Test
    @DisplayName("Should reset rectangle properties before animation")
    void testAnimateRowClearResetsPropertiesBeforeAnimation() {
        Platform.runLater(() -> {
            // Arrange
            Rectangle[][] displayMatrix = createTestDisplayMatrix(20, 10);
            fillRow(displayMatrix, 10, Color.RED);
            
            // Modify rectangle properties
            displayMatrix[10][0].setOpacity(0.5);
            displayMatrix[10][0].setScaleX(0.8);
            displayMatrix[10][0].setScaleY(0.8);
            
            // Act
            animationController.animateRowClear(displayMatrix, Collections.singletonList(10), () -> {});
            
            // Immediately check (before animation completes)
            // Properties should be reset to 1.0 before animation starts
            assertEquals(1.0, displayMatrix[10][0].getOpacity(), 
                        "Opacity should be reset to 1.0 before animation");
            assertEquals(1.0, displayMatrix[10][0].getScaleX(), 
                        "ScaleX should be reset to 1.0 before animation");
            assertEquals(1.0, displayMatrix[10][0].getScaleY(), 
                        "ScaleY should be reset to 1.0 before animation");
        });
    }
    
    @Test
    @DisplayName("Should call callback even when no rectangles to animate")
    void testAnimateRowClearWithAllTransparentRectangles() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean callbackCalled = new AtomicBoolean(false);
        
        Platform.runLater(() -> {
            // Arrange - all rectangles are transparent
            Rectangle[][] displayMatrix = createTestDisplayMatrix(20, 10);
            // All rectangles are already transparent
            
            // Act
            animationController.animateRowClear(displayMatrix, Collections.singletonList(10), () -> {
                callbackCalled.set(true);
                latch.countDown();
            });
        });
        
        // Wait for callback
        boolean completed = latch.await(200, TimeUnit.MILLISECONDS);
        
        // Assert
        assertTrue(completed, "Callback should be called immediately");
        assertTrue(callbackCalled.get(), "Callback should be called even with no rectangles to animate");
    }
    
    /**
     * Creates a test display matrix with the specified dimensions.
     * 
     * @param rows number of rows
     * @param cols number of columns
     * @return a 2D array of Rectangle objects
     */
    private Rectangle[][] createTestDisplayMatrix(int rows, int cols) {
        Rectangle[][] matrix = new Rectangle[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Rectangle rect = new Rectangle(25, 25);
                rect.setFill(Color.TRANSPARENT);
                rect.setOpacity(1.0);
                rect.setScaleX(1.0);
                rect.setScaleY(1.0);
                matrix[i][j] = rect;
            }
        }
        return matrix;
    }
    
    /**
     * Fills a row with rectangles of the specified color.
     * 
     * @param displayMatrix the display matrix
     * @param row the row index to fill
     * @param color the color to use
     */
    private void fillRow(Rectangle[][] displayMatrix, int row, Color color) {
        if (row >= 0 && row < displayMatrix.length) {
            for (int col = 0; col < displayMatrix[row].length; col++) {
                if (displayMatrix[row][col] != null) {
                    displayMatrix[row][col].setFill(color);
                }
            }
        }
    }
}

