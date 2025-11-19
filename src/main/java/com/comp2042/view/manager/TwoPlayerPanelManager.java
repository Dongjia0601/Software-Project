package com.comp2042.view.manager;

import com.comp2042.dto.ViewData;
import com.comp2042.service.audio.SoundManager;
import com.comp2042.view.panel.GameOverPanel;
import com.comp2042.view.panel.TwoPlayerGameOverPanel;
import javafx.animation.*;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.function.BooleanSupplier;

/**
 * Encapsulates all UI responsibilities for the two-player game view.
 */
public class TwoPlayerPanelManager {

    private final int brickSize;
    private final int defaultGridGap;

    private final BorderPane rootPane;
    private final GridPane gamePanel1;
    private final GridPane gamePanel2;
    private final Pane brickPanel1;
    private final Pane brickPanel2;
    private final Pane ghostPanel1;
    private final Pane ghostPanel2;
    private final GridPane holdPanel1;
    private final GridPane holdPanel2;
    private final GridPane nextPanel1;
    private final GridPane nextPanel2;
    private final Group notificationGroup1;
    private final Group notificationGroup2;
    private final GameOverPanel gameOverPanel1;
    private final GameOverPanel gameOverPanel2;
    private final TwoPlayerGameOverPanel twoPlayerGameOverPanel;
    private final Pane boardBackground1;
    private final Pane boardBackground2;
    private final Label player1ScoreLabel;
    private final Label player1LinesLabel;
    private final Label player1ComboLabel;
    private final Label player1AttackLabel;
    private final Label player1DefenseLabel;
    private final Label player1TetrisLabel;
    private final Label player1TimeLabel;
    private final Label player2ScoreLabel;
    private final Label player2LinesLabel;
    private final Label player2ComboLabel;
    private final Label player2AttackLabel;
    private final Label player2DefenseLabel;
    private final Label player2TetrisLabel;
    private final Label player2TimeLabel;
    private final BooleanProperty isPause;
    private final BooleanSupplier ghostVisibilitySupplier;

    private Rectangle[][] displayMatrix1;
    private Rectangle[][] displayMatrix2;
    private int[][] cachedBoardMatrix1;
    private int[][] cachedBoardMatrix2;
    private Rectangle[][] rectangles1;
    private Rectangle[][] rectangles2;
    private Rectangle[][] ghostRectangles1;
    private Rectangle[][] ghostRectangles2;
    private Rectangle[][] holdDisplayMatrix1;
    private Rectangle[][] holdDisplayMatrix2;
    private Rectangle[][] nextDisplayMatrix1;
    private Rectangle[][] nextDisplayMatrix2;

    public TwoPlayerPanelManager(
        int brickSize,
        int defaultGridGap,
        BorderPane rootPane,
        GridPane gamePanel1,
        GridPane gamePanel2,
        Pane brickPanel1,
        Pane brickPanel2,
        Pane ghostPanel1,
        Pane ghostPanel2,
        GridPane holdPanel1,
        GridPane holdPanel2,
        GridPane nextPanel1,
        GridPane nextPanel2,
        Group notificationGroup1,
        Group notificationGroup2,
        GameOverPanel gameOverPanel1,
        GameOverPanel gameOverPanel2,
        TwoPlayerGameOverPanel twoPlayerGameOverPanel,
        Pane boardBackground1,
        Pane boardBackground2,
        Label player1ScoreLabel,
        Label player1LinesLabel,
        Label player1ComboLabel,
        Label player1AttackLabel,
        Label player1DefenseLabel,
        Label player1TetrisLabel,
        Label player1TimeLabel,
        Label player2ScoreLabel,
        Label player2LinesLabel,
        Label player2ComboLabel,
        Label player2AttackLabel,
        Label player2DefenseLabel,
        Label player2TetrisLabel,
        Label player2TimeLabel,
        BooleanProperty isPause,
        BooleanSupplier ghostVisibilitySupplier
    ) {
        this.brickSize = brickSize;
        this.defaultGridGap = defaultGridGap;
        this.rootPane = rootPane;
        this.gamePanel1 = gamePanel1;
        this.gamePanel2 = gamePanel2;
        this.brickPanel1 = brickPanel1;
        this.brickPanel2 = brickPanel2;
        this.ghostPanel1 = ghostPanel1;
        this.ghostPanel2 = ghostPanel2;
        this.holdPanel1 = holdPanel1;
        this.holdPanel2 = holdPanel2;
        this.nextPanel1 = nextPanel1;
        this.nextPanel2 = nextPanel2;
        this.notificationGroup1 = notificationGroup1;
        this.notificationGroup2 = notificationGroup2;
        this.gameOverPanel1 = gameOverPanel1;
        this.gameOverPanel2 = gameOverPanel2;
        this.twoPlayerGameOverPanel = twoPlayerGameOverPanel;
        this.boardBackground1 = boardBackground1;
        this.boardBackground2 = boardBackground2;
        this.player1ScoreLabel = player1ScoreLabel;
        this.player1LinesLabel = player1LinesLabel;
        this.player1ComboLabel = player1ComboLabel;
        this.player1AttackLabel = player1AttackLabel;
        this.player1DefenseLabel = player1DefenseLabel;
        this.player1TetrisLabel = player1TetrisLabel;
        this.player1TimeLabel = player1TimeLabel;
        this.player2ScoreLabel = player2ScoreLabel;
        this.player2LinesLabel = player2LinesLabel;
        this.player2ComboLabel = player2ComboLabel;
        this.player2AttackLabel = player2AttackLabel;
        this.player2DefenseLabel = player2DefenseLabel;
        this.player2TetrisLabel = player2TetrisLabel;
        this.player2TimeLabel = player2TimeLabel;
        this.isPause = isPause;
        this.ghostVisibilitySupplier = ghostVisibilitySupplier;
    }

    private double calculateGridX(GridPane grid, Rectangle[][] matrix, int column) {
        GridMetrics metrics = measureGrid(grid, matrix);
        return metrics.originX + column * metrics.cellWidth;
    }

    private double calculateGridY(GridPane grid, Rectangle[][] matrix, int row) {
        GridMetrics metrics = measureGrid(grid, matrix);
        return metrics.originY + row * metrics.cellHeight;
    }

    private GridMetrics measureGrid(GridPane grid, Rectangle[][] matrix) {
        double gapX = defaultGridGap;
        double gapY = defaultGridGap;
        double originX = 0;
        double originY = 0;

        if (grid != null) {
            gapX = grid.getHgap() > 0 ? grid.getHgap() : defaultGridGap;
            gapY = grid.getVgap() > 0 ? grid.getVgap() : defaultGridGap;
            originX = grid.getLayoutX();
            originY = grid.getLayoutY();
            grid.applyCss();
            grid.layout();
        }

        double cellWidth = brickSize + gapX;
        double cellHeight = brickSize + gapY;

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

    private Paint getFillColor(int i) {
        switch (i) {
            case 0:
                return Color.TRANSPARENT;
            case 1:
                return Color.AQUA;
            case 2:
                return Color.BLUEVIOLET;
            case 3:
                return Color.DARKGREEN;
            case 4:
                return Color.YELLOW;
            case 5:
                return Color.RED;
            case 6:
                return Color.BEIGE;
            case 7:
                return Color.BURLYWOOD;
            case 8:
                return Color.DARKORANGE;
            default:
                return Color.GAINSBORO;
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

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

    public void updatePlayer1Score(int score) {
        if (player1ScoreLabel != null) {
            player1ScoreLabel.setText("Score: " + score);
        }
    }

    public void updatePlayer2Score(int score) {
        if (player2ScoreLabel != null) {
            player2ScoreLabel.setText("Score: " + score);
        }
    }

    public void updatePlayerStats(int player, com.comp2042.model.mode.PlayerStats stats) {
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

    public void clearPlayer1Panels() {
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

        updatePlayer1HoldDisplay(null);
        updatePlayer1NextDisplay(null);

        if (ghostPanel1 != null) {
            ghostPanel1.setVisible(false);
            ghostPanel1.getChildren().clear();
        }
        ghostRectangles1 = null;

        if (brickPanel1 != null) {
            brickPanel1.getChildren().clear();
        }
    }

    public void clearPlayer2Panels() {
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

        updatePlayer2HoldDisplay(null);
        updatePlayer2NextDisplay(null);

        if (ghostPanel2 != null) {
            ghostPanel2.setVisible(false);
            ghostPanel2.getChildren().clear();
        }
        ghostRectangles2 = null;

        if (brickPanel2 != null) {
            brickPanel2.getChildren().clear();
        }
    }

    public void initPlayer1View(int[][] boardMatrix, ViewData brick) {
        if (gamePanel1 == null || boardMatrix == null || brick == null) {
            return;
        }

        if (boardBackground1 != null) {
            boardBackground1.getStyleClass().add("gameBoard-two-player");
            boardBackground1.getStyleClass().add("player1-board");
            boardBackground1.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #851ee5, #4B0082), rgba(10, 14, 39, 0.96); " +
                "-fx-background-insets: 0,10; " +
                "-fx-background-radius: 16, 8; " +
                "-fx-border-color: rgba(138, 43, 226, 0.8); " +
                "-fx-border-width: 6px; " +
                "-fx-border-radius: 16px;"
            );
        }

        gamePanel1.setGridLinesVisible(true);
        if (!gamePanel1.getStyleClass().contains("game-grid")) {
            gamePanel1.getStyleClass().add("game-grid");
        }

        displayMatrix1 = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        cachedBoardMatrix1 = new int[boardMatrix.length][boardMatrix[0].length];
        for (int i = 0; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(brickSize, brickSize);
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.getStyleClass().add("game-cell");
                displayMatrix1[i][j] = rectangle;
                gamePanel1.add(rectangle, j, i);
                cachedBoardMatrix1[i][j] = boardMatrix[i][j];
            }
        }

        rectangles1 = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(brickSize, brickSize);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangle.setLayoutX(j * (brickSize + 1));
                rectangle.setLayoutY(i * (brickSize + 1));
                rectangles1[i][j] = rectangle;
                brickPanel1.getChildren().add(rectangle);
            }
        }

        if (ghostPanel1 != null) {
            ghostPanel1.getChildren().clear();
            ghostRectangles1 = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    Rectangle rectangle = new Rectangle(brickSize, brickSize);
                    rectangle.setFill(Color.TRANSPARENT);
                    rectangle.setArcHeight(9);
                    rectangle.setArcWidth(9);
                    rectangle.setOpacity(1.0);
                    rectangle.setLayoutX(j * (brickSize + 1));
                    rectangle.setLayoutY(i * (brickSize + 1));
                    ghostRectangles1[i][j] = rectangle;
                    ghostPanel1.getChildren().add(rectangle);
                }
            }
        }

        if (brickPanel1 != null) {
            brickPanel1.setLayoutX(calculateGridX(gamePanel1, displayMatrix1, brick.getxPosition()));
            brickPanel1.setLayoutY(calculateGridY(gamePanel1, displayMatrix1, brick.getyPosition()));
        }

        if (brick.getNextBrickData() != null) {
            updatePlayer1NextDisplay(brick.getNextBrickData());
        }
        if (brick.getHoldBrickData() != null) {
            updatePlayer1HoldDisplay(brick.getHoldBrickData());
        }

        updatePlayer1GhostBrick(brick);

        if (gameOverPanel1 != null) {
            gameOverPanel1.setVisible(false);
        }
    }

    public void initPlayer2View(int[][] boardMatrix, ViewData brick) {
        if (gamePanel2 == null || boardMatrix == null || brick == null) {
            return;
        }

        if (boardBackground2 != null) {
            boardBackground2.getStyleClass().add("gameBoard-two-player");
            boardBackground2.getStyleClass().add("player2-board");
            boardBackground2.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #851ee5, #4B0082), rgba(10, 14, 39, 0.96); " +
                "-fx-background-insets: 0,10; " +
                "-fx-background-radius: 16, 8; " +
                "-fx-border-color: rgba(138, 43, 226, 0.8); " +
                "-fx-border-width: 6px; " +
                "-fx-border-radius: 16px;"
            );
        }

        gamePanel2.setGridLinesVisible(true);
        if (!gamePanel2.getStyleClass().contains("game-grid")) {
            gamePanel2.getStyleClass().add("game-grid");
        }

        displayMatrix2 = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        cachedBoardMatrix2 = new int[boardMatrix.length][boardMatrix[0].length];
        for (int i = 0; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(brickSize, brickSize);
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.getStyleClass().add("game-cell");
                displayMatrix2[i][j] = rectangle;
                gamePanel2.add(rectangle, j, i);
                cachedBoardMatrix2[i][j] = boardMatrix[i][j];
            }
        }

        rectangles2 = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(brickSize, brickSize);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangle.setLayoutX(j * (brickSize + 1));
                rectangle.setLayoutY(i * (brickSize + 1));
                rectangles2[i][j] = rectangle;
                brickPanel2.getChildren().add(rectangle);
            }
        }

        if (ghostPanel2 != null) {
            ghostPanel2.getChildren().clear();
            ghostRectangles2 = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    Rectangle rectangle = new Rectangle(brickSize, brickSize);
                    rectangle.setFill(Color.TRANSPARENT);
                    rectangle.setArcHeight(9);
                    rectangle.setArcWidth(9);
                    rectangle.setOpacity(1.0);
                    rectangle.setLayoutX(j * (brickSize + 1));
                    rectangle.setLayoutY(i * (brickSize + 1));
                    ghostRectangles2[i][j] = rectangle;
                    ghostPanel2.getChildren().add(rectangle);
                }
            }
        }

        if (brickPanel2 != null) {
            brickPanel2.setLayoutX(calculateGridX(gamePanel2, displayMatrix2, brick.getxPosition()));
            brickPanel2.setLayoutY(calculateGridY(gamePanel2, displayMatrix2, brick.getyPosition()));
        }

        if (brick.getNextBrickData() != null) {
            updatePlayer2NextDisplay(brick.getNextBrickData());
        }
        if (brick.getHoldBrickData() != null) {
            updatePlayer2HoldDisplay(brick.getHoldBrickData());
        }

        updatePlayer2GhostBrick(brick);

        if (gameOverPanel2 != null) {
            gameOverPanel2.setVisible(false);
        }
    }

    public void refreshPlayer1Brick(ViewData brick) {
        if (brickPanel1 == null || gamePanel1 == null || rectangles1 == null || displayMatrix1 == null) {
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

            if (brick.getNextBrickData() != null) {
                updatePlayer1NextDisplay(brick.getNextBrickData());
            }
            if (brick.getHoldBrickData() != null) {
                updatePlayer1HoldDisplay(brick.getHoldBrickData());
            }

            updatePlayer1GhostBrick(brick);
        }
    }

    public void refreshPlayer2Brick(ViewData brick) {
        if (brickPanel2 == null || gamePanel2 == null || rectangles2 == null || displayMatrix2 == null) {
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

            if (brick.getNextBrickData() != null) {
                updatePlayer2NextDisplay(brick.getNextBrickData());
            }
            if (brick.getHoldBrickData() != null) {
                updatePlayer2HoldDisplay(brick.getHoldBrickData());
            }

            updatePlayer2GhostBrick(brick);
        }
    }

    public void updatePlayer1GhostBrick(ViewData brick) {
        if (ghostPanel1 == null || ghostRectangles1 == null) {
            return;
        }

        boolean showGhost = ghostVisibilitySupplier == null || ghostVisibilitySupplier.getAsBoolean();
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

        int[][] brickData = brick.getBrickData();
        for (int i = 0; i < brickData.length && i < ghostRectangles1.length; i++) {
            for (int j = 0; j < brickData[i].length && j < ghostRectangles1[i].length; j++) {
                Rectangle ghostRect = ghostRectangles1[i][j];
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
                    ghostRect.setVisible(false);
                }
            }
        }
    }

    public void updatePlayer2GhostBrick(ViewData brick) {
        if (ghostPanel2 == null || ghostRectangles2 == null) {
            return;
        }

        boolean showGhost = ghostVisibilitySupplier == null || ghostVisibilitySupplier.getAsBoolean();
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

        int[][] brickData = brick.getBrickData();
        for (int i = 0; i < brickData.length && i < ghostRectangles2.length; i++) {
            for (int j = 0; j < brickData[i].length && j < ghostRectangles2[i].length; j++) {
                Rectangle ghostRect = ghostRectangles2[i][j];
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
                    ghostRect.setVisible(false);
                }
            }
        }
    }

    public void refreshGameBackground1(int[][] board) {
        if (displayMatrix1 == null || board == null) {
            return;
        }

        if (cachedBoardMatrix1 == null ||
            cachedBoardMatrix1.length != board.length ||
            cachedBoardMatrix1[0].length != board[0].length) {
            cachedBoardMatrix1 = new int[board.length][board[0].length];
            for (int i = 0; i < board.length && i < displayMatrix1.length; i++) {
                for (int j = 0; j < board[i].length && j < displayMatrix1[i].length; j++) {
                    cachedBoardMatrix1[i][j] = board[i][j];
                    setRectangleData(board[i][j], displayMatrix1[i][j]);
                }
            }
            return;
        }

        for (int i = 0; i < board.length && i < displayMatrix1.length; i++) {
            for (int j = 0; j < board[i].length && j < displayMatrix1[i].length; j++) {
                if (cachedBoardMatrix1[i][j] != board[i][j]) {
                    cachedBoardMatrix1[i][j] = board[i][j];
                    setRectangleData(board[i][j], displayMatrix1[i][j]);
                }
            }
        }
    }

    public void refreshGameBackground2(int[][] board) {
        if (displayMatrix2 == null || board == null) {
            return;
        }

        if (cachedBoardMatrix2 == null ||
            cachedBoardMatrix2.length != board.length ||
            cachedBoardMatrix2[0].length != board[0].length) {
            cachedBoardMatrix2 = new int[board.length][board[0].length];
            for (int i = 0; i < board.length && i < displayMatrix2.length; i++) {
                for (int j = 0; j < board[i].length && j < displayMatrix2[i].length; j++) {
                    cachedBoardMatrix2[i][j] = board[i][j];
                    setRectangleData(board[i][j], displayMatrix2[i][j]);
                }
            }
            return;
        }

        for (int i = 0; i < board.length && i < displayMatrix2.length; i++) {
            for (int j = 0; j < board[i].length && j < displayMatrix2[i].length; j++) {
                if (cachedBoardMatrix2[i][j] != board[i][j]) {
                    cachedBoardMatrix2[i][j] = board[i][j];
                    setRectangleData(board[i][j], displayMatrix2[i][j]);
                }
            }
        }
    }



    public void updatePlayer1NextDisplay(int[][] nextBrickData) {
        if (nextPanel1 == null) {
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
                    nextPanel1.add(rectangle, j, i);
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                nextDisplayMatrix1[i][j].setFill(Color.TRANSPARENT);
            }
        }

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

    public void updatePlayer2NextDisplay(int[][] nextBrickData) {
        if (nextPanel2 == null) {
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
                    nextPanel2.add(rectangle, j, i);
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                nextDisplayMatrix2[i][j].setFill(Color.TRANSPARENT);
            }
        }

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

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                holdDisplayMatrix1[i][j].setFill(Color.TRANSPARENT);
            }
        }

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

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                holdDisplayMatrix2[i][j].setFill(Color.TRANSPARENT);
            }
        }

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

    public void showTwoPlayerGameOver(
        int winner,
        int player1Score,
        int player2Score,
        com.comp2042.model.mode.PlayerStats player1Stats,
        com.comp2042.model.mode.PlayerStats player2Stats,
        Runnable onNewGame,
        Runnable onReturnToMenu
    ) {
        SoundManager.getInstance().playTwoPlayerGameOverSound();

        if (player1Stats == null) {
            player1Stats = new com.comp2042.model.mode.PlayerStats();
        }
        if (player2Stats == null) {
            player2Stats = new com.comp2042.model.mode.PlayerStats();
        }

        if (twoPlayerGameOverPanel != null) {
            twoPlayerGameOverPanel.setVisible(true);
            twoPlayerGameOverPanel.setManaged(true);
            twoPlayerGameOverPanel.setGameOverInfo(winner, player1Stats, player2Stats,
                player1Score, player2Score);

            twoPlayerGameOverPanel.setButtons(
                e -> {
                    twoPlayerGameOverPanel.setVisible(false);
                    twoPlayerGameOverPanel.setManaged(false);
                    if (onNewGame != null) {
                        onNewGame.run();
                    }
                },
                e -> {
                    twoPlayerGameOverPanel.setVisible(false);
                    twoPlayerGameOverPanel.setManaged(false);
                    if (onReturnToMenu != null) {
                        onReturnToMenu.run();
                    }
                }
            );

            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), twoPlayerGameOverPanel);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        } else {
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
    }

    public void showAttackAnimation(int player, int attackPower) {
        Pane boardBackground = (player == 1) ? boardBackground1 : boardBackground2;
        GridPane gamePanel = (player == 1) ? gamePanel1 : gamePanel2;
        if (boardBackground == null || gamePanel == null) {
            return;
        }

        Pane boardContainer = (Pane) boardBackground.getParent();
        if (boardContainer == null) {
            return;
        }

        double intensity = Math.min(attackPower / 4.0, 1.0);
        long duration = (long) (300 + intensity * 200);

        double shakeAmount = 3 + intensity * 5;
        double originalX = boardContainer.getLayoutX();
        double originalY = boardContainer.getLayoutY();

        Timeline shakeTimeline = new Timeline();
        int shakeCount = 8;
        for (int i = 0; i < shakeCount; i++) {
            double offsetX = (Math.random() - 0.5) * shakeAmount * 2;
            double offsetY = (Math.random() - 0.5) * shakeAmount * 2;
            KeyFrame keyFrame = new KeyFrame(
                Duration.millis(i * duration / shakeCount),
                e -> {
                    boardContainer.setLayoutX(originalX + offsetX);
                    boardContainer.setLayoutY(originalY + offsetY);
                }
            );
            shakeTimeline.getKeyFrames().add(keyFrame);
        }

        KeyFrame returnFrame = new KeyFrame(
            Duration.millis(duration),
            e -> {
                boardContainer.setLayoutX(originalX);
                boardContainer.setLayoutY(originalY);
            }
        );
        shakeTimeline.getKeyFrames().add(returnFrame);
        shakeTimeline.play();

        Rectangle flashOverlay = new Rectangle();
        flashOverlay.setWidth(boardBackground.getWidth());
        flashOverlay.setHeight(boardBackground.getHeight());
        flashOverlay.setFill(player == 1
            ? Color.rgb(255, 107, 107, 0.4 + intensity * 0.3)
            : Color.rgb(78, 205, 196, 0.4 + intensity * 0.3));
        flashOverlay.setMouseTransparent(true);
        flashOverlay.setLayoutX(boardBackground.getLayoutX());
        flashOverlay.setLayoutY(boardBackground.getLayoutY());

        boardContainer.getChildren().add(flashOverlay);

        SequentialTransition flashSequence = new SequentialTransition();

        FadeTransition flash1 = new FadeTransition(Duration.millis(duration / 3), flashOverlay);
        flash1.setFromValue(0.7 + intensity * 0.3);
        flash1.setToValue(0.2);

        FadeTransition flash2 = new FadeTransition(Duration.millis(duration / 3), flashOverlay);
        flash2.setFromValue(0.3);
        flash2.setToValue(0.1);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(duration / 3), flashOverlay);
        fadeOut.setFromValue(0.1);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> boardContainer.getChildren().remove(flashOverlay));

        flashSequence.getChildren().addAll(flash1, flash2, fadeOut);
        flashSequence.play();

        Circle shockwave = new Circle();
        double centerX = boardBackground.getWidth() / 2 + boardBackground.getLayoutX();
        double centerY = boardBackground.getHeight() / 2 + boardBackground.getLayoutY();
        shockwave.setCenterX(centerX);
        shockwave.setCenterY(centerY);
        shockwave.setRadius(10);
        shockwave.setFill(Color.TRANSPARENT);
        shockwave.setStroke(player == 1
            ? Color.rgb(255, 107, 107, 0.8)
            : Color.rgb(78, 205, 196, 0.8));
        shockwave.setStrokeWidth(3 + intensity * 2);
        shockwave.setMouseTransparent(true);

        boardContainer.getChildren().add(shockwave);

        double maxRadius = Math.max(boardBackground.getWidth(), boardBackground.getHeight()) * 0.7;
        ScaleTransition shockwaveExpand = new ScaleTransition(Duration.millis(duration), shockwave);
        shockwaveExpand.setFromX(1.0);
        shockwaveExpand.setFromY(1.0);
        shockwaveExpand.setToX(maxRadius / 10.0);
        shockwaveExpand.setToY(maxRadius / 10.0);

        FadeTransition shockwaveFade = new FadeTransition(Duration.millis(duration), shockwave);
        shockwaveFade.setFromValue(0.8);
        shockwaveFade.setToValue(0.0);

        ParallelTransition shockwaveAnimation = new ParallelTransition(shockwaveExpand, shockwaveFade);
        shockwaveAnimation.setOnFinished(e -> boardContainer.getChildren().remove(shockwave));
        shockwaveAnimation.play();

        if (attackPower >= 2) {
            int particleCount = (int) (5 + intensity * 10);
            for (int i = 0; i < particleCount; i++) {
                Circle particle = new Circle(2 + Math.random() * 3);
                particle.setCenterX(centerX);
                particle.setCenterY(centerY);
                particle.setFill(player == 1
                    ? Color.rgb(255, 107, 107, 0.9)
                    : Color.rgb(78, 205, 196, 0.9));
                particle.setMouseTransparent(true);

                boardContainer.getChildren().add(particle);

                double angle = Math.random() * 2 * Math.PI;
                double distance = 30 + Math.random() * 50;
                double endX = centerX + Math.cos(angle) * distance;
                double endY = centerY + Math.sin(angle) * distance;

                TranslateTransition particleMove = new TranslateTransition(Duration.millis(duration), particle);
                particleMove.setFromX(0);
                particleMove.setFromY(0);
                particleMove.setToX(endX - centerX);
                particleMove.setToY(endY - centerY);

                FadeTransition particleFade = new FadeTransition(Duration.millis(duration), particle);
                particleFade.setFromValue(0.9);
                particleFade.setToValue(0.0);

                ParallelTransition particleAnimation = new ParallelTransition(particleMove, particleFade);
                particleAnimation.setOnFinished(e -> boardContainer.getChildren().remove(particle));
                particleAnimation.play();
            }
        }
    }

    public void showComboBonus(int player, int combo, int linesEliminated) {
        if (rootPane == null) {
            return;
        }

        GridPane playerBoard = (player == 1) ? gamePanel1 : gamePanel2;
        if (playerBoard == null) {
            return;
        }

        Label comboLabel = new Label("COMBO x" + combo + "!\n" + linesEliminated + " lines eliminated!");
        comboLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        comboLabel.setStyle("-fx-text-fill: #FFD700; " +
            "-fx-effect: dropshadow(gaussian, rgba(255, 215, 0, 1.0), 20, 0, 0, 0); " +
            "-fx-alignment: center; " +
            "-fx-text-alignment: center;");
        comboLabel.setAlignment(Pos.CENTER);
        comboLabel.setMouseTransparent(true);

        StackPane comboOverlay = new StackPane();
        comboOverlay.setAlignment(Pos.CENTER);
        comboOverlay.getChildren().add(comboLabel);
        comboOverlay.setMouseTransparent(true);

        rootPane.getChildren().add(comboOverlay);

        comboLabel.setScaleX(0.5);
        comboLabel.setScaleY(0.5);
        comboLabel.setOpacity(0.0);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(300), comboLabel);
        scaleUp.setToX(1.2);
        scaleUp.setToY(1.2);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), comboLabel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        ParallelTransition appear = new ParallelTransition(scaleUp, fadeIn);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), comboLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.millis(1000));

        SequentialTransition sequence = new SequentialTransition(appear, fadeOut);
        sequence.setOnFinished(e -> rootPane.getChildren().remove(comboOverlay));
        sequence.play();
    }
}

