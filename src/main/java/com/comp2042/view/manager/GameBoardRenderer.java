package com.comp2042.view.manager;

import javafx.geometry.Bounds;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Responsible for rendering the static game board background.
 * 
 * <p>This class manages the display matrix of rectangles that represent the game board,
 * and provides efficient incremental rendering by caching the board state.</p>
 * 
 * <p><strong>Responsibilities:</strong></p>
 * <ul>
 *   <li>Initialize and manage the display matrix (Rectangle[][])</li>
 *   <li>Refresh the game background with incremental updates</li>
 *   <li>Calculate grid positions for brick placement</li>
 *   <li>Manage board state caching for performance optimization</li>
 * </ul>
 * 
 * <p><strong>Design Pattern:</strong> Extracted from GuiController to adhere to Single Responsibility Principle (SRP)</p>
 * 
 * @author Dong, Jia
 * @version Phase 3 - SRP Refactoring
 */
public class GameBoardRenderer {
    
    // Constants
    private static final int BRICK_SIZE = 25; // Size of a single brick cell in pixels
    private static final int DEFAULT_GRID_GAP = 1; // Fallback gap when GridPane spacing not yet initialised
    
    // Board display state
    private Rectangle[][] displayMatrix; // Array of rectangles representing the static board background
    private int[][] cachedBoardMatrix; // Cached board matrix for incremental rendering optimization
    private final GridPane gamePanel; // The GridPane to render into
    
    /**
     * Constructs a GameBoardRenderer for the specified GridPane.
     * 
     * @param gamePanel the GridPane that will contain the board display
     */
    public GameBoardRenderer(GridPane gamePanel) {
        this.gamePanel = gamePanel;
    }
    
    /**
     * Initializes the display matrix based on the provided board matrix.
     * Creates Rectangle objects for each cell and adds them to the GridPane.
     * 
     * @param boardMatrix the initial board state matrix
     */
    public void initializeBoard(int[][] boardMatrix) {
        if (boardMatrix == null || boardMatrix.length == 0) {
            return;
        }
        
        int rows = boardMatrix.length;
        int cols = boardMatrix[0].length;
        
        displayMatrix = new Rectangle[rows][cols];
        cachedBoardMatrix = new int[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.getStyleClass().add("game-cell");
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i);
                cachedBoardMatrix[i][j] = boardMatrix[i][j]; // Initialize cache
            }
        }
    }
    
    /**
     * Refreshes the game background with the updated board state.
     * Uses incremental rendering by only updating cells that have changed.
     * 
     * @param board the updated board matrix
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
     * Sets the visual appearance of a rectangle based on its cell value.
     * 
     * @param value the cell value (0 for empty, positive for brick type)
     * @param rectangle the rectangle to update
     */
    private void setRectangleData(int value, Rectangle rectangle) {
        if (rectangle == null) {
            return;
        }
        
        if (value == 0) {
            rectangle.setFill(Color.TRANSPARENT);
        } else {
            // Map brick type to color
            Color color = getBrickColor(value);
            rectangle.setFill(color);
        }
    }
    
    /**
     * Gets the color for a brick type using polymorphism (Strategy Pattern).
     * Refactored: Replaced switch statement with polymorphic delegation to Brick objects.
     * 
     * @param brickType the brick type value
     * @return the corresponding color
     */
    private Color getBrickColor(int brickType) {
        javafx.scene.paint.Paint paint = com.comp2042.model.brick.BrickColorMapper.getColor(brickType);
        return (Color) paint; // Safe cast as all brick colors are Color instances
    }
    
    /**
     * Calculates the absolute X position for the specified column within the grid.
     *
     * @param column the zero-based column index
     * @return the computed X coordinate in pixels
     */
    public double calculateGridX(int column) {
        GridMetrics metrics = measureGrid();
        return metrics.originX + column * metrics.cellWidth;
    }

    /**
     * Calculates the absolute Y position for the specified row within the grid.
     *
     * @param row the zero-based row index
     * @return the computed Y coordinate in pixels
     */
    public double calculateGridY(int row) {
        GridMetrics metrics = measureGrid();
        return metrics.originY + row * metrics.cellHeight;
    }

    /**
     * Measures grid origin and cell dimensions using the display matrix.
     * Falls back to configured brick size and gaps when layout bounds are unavailable.
     *
     * @return GridMetrics containing origin coordinates and cell dimensions
     */
    private GridMetrics measureGrid() {
        double gapX = DEFAULT_GRID_GAP;
        double gapY = DEFAULT_GRID_GAP;
        double originX = 0;
        double originY = 0;

        if (gamePanel != null) {
            gapX = gamePanel.getHgap() > 0 ? gamePanel.getHgap() : DEFAULT_GRID_GAP;
            gapY = gamePanel.getVgap() > 0 ? gamePanel.getVgap() : DEFAULT_GRID_GAP;
            originX = gamePanel.getLayoutX();
            originY = gamePanel.getLayoutY();
            gamePanel.applyCss();
            gamePanel.layout();
        }

        double cellWidth = BRICK_SIZE + gapX;
        double cellHeight = BRICK_SIZE + gapY;

        if (displayMatrix != null && displayMatrix.length > 0 && displayMatrix[0].length > 0) {
            Rectangle cell00 = displayMatrix[0][0];
            if (cell00 != null) {
                Bounds bounds00 = cell00.getBoundsInParent();
                double minX00 = bounds00.getMinX();
                double minY00 = bounds00.getMinY();
                originX = gamePanel.getLayoutX() + minX00;
                originY = gamePanel.getLayoutY() + minY00;

                if (displayMatrix[0].length > 1 && displayMatrix[0][1] != null) {
                    Bounds bounds01 = displayMatrix[0][1].getBoundsInParent();
                    cellWidth = bounds01.getMinX() - minX00;
                }
                if (displayMatrix.length > 1 && displayMatrix[1][0] != null) {
                    Bounds bounds10 = displayMatrix[1][0].getBoundsInParent();
                    cellHeight = bounds10.getMinY() - minY00;
                }
            }
        }

        return new GridMetrics(originX, originY, cellWidth, cellHeight);
    }
    
    /**
     * Clears the display matrix by resetting all cells to transparent.
     */
    public void clearBoard() {
        if (displayMatrix != null) {
            for (int i = 0; i < displayMatrix.length; i++) {
                for (int j = 0; j < displayMatrix[i].length; j++) {
                    if (displayMatrix[i][j] != null) {
                        displayMatrix[i][j].setFill(Color.TRANSPARENT);
                    }
                }
            }
        }
    }
    
    /**
     * Resets the renderer, clearing the display matrix reference.
     * This allows for clean reinitialization.
     */
    public void reset() {
        displayMatrix = null;
        cachedBoardMatrix = null;
    }
    
    /**
     * Gets the display matrix.
     * 
     * @return the current display matrix, or null if not initialized
     */
    public Rectangle[][] getDisplayMatrix() {
        return displayMatrix;
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
}

