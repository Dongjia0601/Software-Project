package com.comp2042.view.manager;

import com.comp2042.dto.ViewData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * Renders all brick visuals: current piece, ghost preview, hold, and next piece panels (SRP).
 * Manages color mapping and position calculations for JavaFX rendering.
 * 
 * @author Dong, Jia
 */
public class BrickRenderer {
    
    private static final int BRICK_SIZE = 25;
    private static final int PREVIEW_BRICK_SIZE = 20;
    
    private final Pane brickPanel;
    private final Pane ghostPanel;
    private final GridPane holdPanel;
    private final GridPane nextBrickPanel;
    private final GameBoardRenderer boardRenderer;
    
    private Rectangle[][] rectangles;
    private Rectangle[][] ghostRectangles;
    private Rectangle[][] holdDisplayMatrix;
    private Rectangle[][] nextDisplayMatrix;
    
    private boolean ghostEnabled = true;
    private double currentBrickOpacity = 1.0;
    
    /**
     * Constructs a BrickRenderer with the specified UI components.
     * 
     * @param brickPanel the pane for the current falling brick
     * @param ghostPanel the pane for the ghost brick
     * @param holdPanel the grid for hold piece display
     * @param nextBrickPanel the grid for next piece display
     * @param boardRenderer the board renderer for position calculations
     */
    public BrickRenderer(Pane brickPanel, Pane ghostPanel, GridPane holdPanel, 
                         GridPane nextBrickPanel, GameBoardRenderer boardRenderer) {
        this.brickPanel = brickPanel;
        this.ghostPanel = ghostPanel;
        this.holdPanel = holdPanel;
        this.nextBrickPanel = nextBrickPanel;
        this.boardRenderer = boardRenderer;
    }
    
    /**
     * Initializes the brick rectangles for the current falling brick.
     * 
     * @param brick the ViewData containing brick shape information
     */
    public void initializeBrick(ViewData brick) {
        if (brick == null || brickPanel == null) {
            return;
        }
        
        // Initialize current brick rectangles
        brickPanel.getChildren().clear();
        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setOpacity(currentBrickOpacity);
                rectangles[i][j] = rectangle;
                brickPanel.getChildren().add(rectangle);
            }
        }
        
        // Initialize ghost brick rectangles
        if (ghostPanel != null) {
            ghostPanel.getChildren().clear();
            ghostRectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
            
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                    rectangle.setArcHeight(9);
                    rectangle.setArcWidth(9);
                    rectangle.setFill(Color.TRANSPARENT);
                    rectangle.setVisible(false);
                    ghostRectangles[i][j] = rectangle;
                    ghostPanel.getChildren().add(rectangle);
                }
            }
        }
        
        updateBrickPosition(brick);
    }
    
    /**
     * Refreshes the active brick display with new data.
     * 
     * @param brick the ViewData containing the updated brick state
     */
    public void refreshActiveBrick(ViewData brick) {
        if (brick == null || rectangles == null) {
            return;
        }
        
        if (brick.getNextBrickData() != null) {
            updateNextDisplay(brick.getNextBrickData());
        } else {
            updateNextDisplay(null);
        }
        
        updateHoldDisplay(brick.getHoldBrickData());
        
        updateBrickPosition(brick);
        
        for (int i = 0; i < brick.getBrickData().length && i < rectangles.length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length && j < rectangles[i].length; j++) {
                setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
            }
        }
        
        updateGhostBrick(brick);
    }
    
    /**
     * Updates the brick panel position based on the brick's coordinates.
     * 
     * @param brick the ViewData containing position information
     */
    private void updateBrickPosition(ViewData brick) {
        if (brickPanel != null && boardRenderer != null) {
            brickPanel.setLayoutX(boardRenderer.calculateGridX(brick.getXPosition()));
            brickPanel.setLayoutY(boardRenderer.calculateGridY(brick.getYPosition()));
        }
    }
    
    /**
     * Updates the ghost brick display based on the current brick position and shape.
     * The ghost brick shows where the current brick would land if dropped straight down.
     *
     * @param brick the ViewData containing the current brick shape and ghost position
     */
    private void updateGhostBrick(ViewData brick) {
        if (ghostPanel == null || ghostRectangles == null) {
            return;
        }
        
        // Check if ghost brick should be displayed
        boolean showGhost = ghostEnabled;
        ghostPanel.setVisible(showGhost);
        
        if (!showGhost) {
            return;
        }
        
        int ghostY = brick.getGhostYPosition();
        if (ghostY < 0 || ghostY == brick.getYPosition()) {
            ghostPanel.setVisible(false);
            return;
        }
        
        if (boardRenderer != null) {
            ghostPanel.setLayoutX(boardRenderer.calculateGridX(brick.getXPosition()));
            ghostPanel.setLayoutY(boardRenderer.calculateGridY(ghostY));
        }
        
        // Update ghost brick rectangles to match current brick shape
        int[][] brickData = brick.getBrickData();
        for (int i = 0; i < brickData.length && i < ghostRectangles.length; i++) {
            for (int j = 0; j < brickData[i].length && j < ghostRectangles[i].length; j++) {
                Rectangle ghostRect = ghostRectangles[i][j];
                if (brickData[i][j] != 0) {
                    ghostRect.setVisible(true);
                    Paint brickColor = getFillColor(brickData[i][j]);
                    if (brickColor instanceof Color) {
                        Color color = (Color) brickColor;
                        ghostRect.setFill(new Color(color.getRed(), color.getGreen(), color.getBlue(), 0.2));
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
     * Updates the next piece display panel.
     * 
     * @param nextBrickData the next brick shape data (4x4 matrix), or null to clear
     */
    public void updateNextDisplay(int[][] nextBrickData) {
        if (nextBrickPanel == null) {
            return;
        }
        
        if (nextDisplayMatrix == null) {
            nextDisplayMatrix = new Rectangle[4][4];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    Rectangle rectangle = new Rectangle(PREVIEW_BRICK_SIZE, PREVIEW_BRICK_SIZE);
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
     * Updates the hold piece display panel.
     * 
     * @param holdBrickData the held brick shape data (4x4 matrix), or null to clear
     */
    public void updateHoldDisplay(int[][] holdBrickData) {
        if (holdPanel == null) {
            return;
        }
        
        if (holdDisplayMatrix == null) {
            holdDisplayMatrix = new Rectangle[4][4];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    Rectangle rectangle = new Rectangle(PREVIEW_BRICK_SIZE, PREVIEW_BRICK_SIZE);
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
     * Sets the visual appearance of a rectangle based on its cell value.
     * 
     * @param color the cell value representing the brick type
     * @param rectangle the rectangle to update
     */
    private void setRectangleData(int color, Rectangle rectangle) {
        if (rectangle == null) {
            return;
        }
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }
    
    /**
     * Maps brick type codes to colors using polymorphism (Strategy Pattern).
     * Refactored: Replaced switch statement with polymorphic delegation to Brick objects.
     * 
     * @param color the brick type value
     * @return the corresponding Paint color
     */
    private Paint getFillColor(int color) {
        return com.comp2042.model.brick.BrickColorMapper.getColor(color);
    }
    
    /**
     * Sets whether the ghost brick should be displayed.
     * 
     * @param enabled true to enable ghost brick display
     */
    public void setGhostEnabled(boolean enabled) {
        this.ghostEnabled = enabled;
        if (ghostPanel != null) {
            ghostPanel.setVisible(enabled);
        }
    }
    
    /**
     * Sets the opacity of the current falling brick.
     * 
     * @param opacity the opacity value (0.0 to 1.0)
     */
    public void setBrickOpacity(double opacity) {
        this.currentBrickOpacity = opacity;
        if (rectangles != null) {
            for (Rectangle[] row : rectangles) {
                for (Rectangle rect : row) {
                    if (rect != null) {
                        rect.setOpacity(opacity);
                    }
                }
            }
        }
    }
    
    /**
     * Clears all brick displays.
     */
    public void clear() {
        if (brickPanel != null) {
            brickPanel.getChildren().clear();
        }
        if (ghostPanel != null) {
            ghostPanel.getChildren().clear();
        }
    }
    
    /**
     * Resets the renderer, clearing all display matrices.
     */
    public void reset() {
        clear();
        rectangles = null;
        ghostRectangles = null;
        nextDisplayMatrix = null;
        holdDisplayMatrix = null;
    }
}

