package com.comp2042.model.board;

import com.comp2042.model.brick.Brick;
import com.comp2042.model.brick.BrickFactory;
import com.comp2042.model.brick.BrickGenerator;
import com.comp2042.model.score.Score;
import com.comp2042.model.savestate.GameStateMemento;

import java.awt.Point;

/**
 * Adapter for board state serialization/deserialization (Memento Pattern).
 * Keeps snapshot operations cohesive and separates concern from SimpleBoard.
 * 
 * @author Dong, Jia.
 */
class BoardMementoAdapter {

    private final BrickRotator brickRotator;
    private final BrickGenerator brickGenerator;
    private final Score score;
    private final HoldManager holdManager;

    BoardMementoAdapter(BrickRotator brickRotator,
                        BrickGenerator brickGenerator,
                        Score score,
                        HoldManager holdManager) {
        this.brickRotator = brickRotator;
        this.brickGenerator = brickGenerator;
        this.score = score;
        this.holdManager = holdManager;
    }

    GameStateMemento createSnapshot(int[][] currentMatrix,
                                    int width,
                                    int height,
                                    Point currentOffset,
                                    int totalLinesCleared,
                                    String generatorType) {

        Brick currentBrick = brickRotator.getBrick();
        int[][] currentBrickShape = currentBrick != null ? brickRotator.getCurrentShape() : null;
        String currentBrickType = currentBrick != null ? getBrickType(currentBrick) : null;
        int currentShapeIndex = getCurrentShapeIndex(currentBrick, currentBrickShape);

        int currentX = currentOffset != null ? (int) currentOffset.getX() : 0;
        int currentY = currentOffset != null ? (int) currentOffset.getY() : 0;

        Brick held = holdManager.getHeldBrick();
        int[][] heldShape = (held != null && held.getShapeMatrix() != null && !held.getShapeMatrix().isEmpty())
                ? held.getShapeMatrix().getFirst() : null;
        String heldType = held != null ? getBrickType(held) : null;

        int[][] nextBrickShape = null;
        String nextBrickType = null;
        try {
            Brick nextBrick = brickGenerator.getNextBrick();
            if (nextBrick != null && nextBrick.getShapeMatrix() != null && !nextBrick.getShapeMatrix().isEmpty()) {
                nextBrickShape = nextBrick.getShapeMatrix().getFirst();
                nextBrickType = getBrickType(nextBrick);
            }
        } catch (Exception ignored) {
        }

        return new GameStateMemento(
                currentMatrix,
                width,
                height,
                currentBrickShape,
                currentX,
                currentY,
                currentShapeIndex,
                currentBrickType,
                score.getScore(),
                totalLinesCleared,
                heldShape,
                heldType,
                nextBrickShape,
                nextBrickType,
                holdManager.canHold(),
                generatorType
        );
    }

    RestorationResult restoreFromSnapshot(GameStateMemento memento, int width, int height) {
        if (memento == null) {
            throw new IllegalArgumentException("Memento cannot be null");
        }
        if (memento.getBoardWidth() != width || memento.getBoardHeight() != height) {
            throw new IllegalArgumentException("Memento dimensions do not match board dimensions");
        }

        score.reset();
        score.add(memento.getScore());

        holdManager.restoreState(createBrick(memento.getHeldBrickType()), memento.canHold());

        Point offset = null;
        if (memento.getCurrentBrickType() != null) {
            Brick currentBrick = createBrick(memento.getCurrentBrickType());
            brickRotator.setBrick(currentBrick);
            brickRotator.setCurrentShape(memento.getCurrentShapeIndex());
            offset = new Point(memento.getCurrentBrickX(), memento.getCurrentBrickY());
        }

        return new RestorationResult(
                memento.getBoardMatrix(),
                offset,
                memento.getTotalLinesCleared()
        );
    }

    private Brick createBrick(String brickType) {
        return brickType != null ? BrickFactory.createBrick(brickType) : null;
    }

    private String getBrickType(Brick brick) {
        if (brick == null) {
            return null;
        }
        String className = brick.getClass().getSimpleName();
        if (className.endsWith("Brick") && className.length() > 5) {
            return className.substring(0, className.length() - 5);
        }
        return null;
    }

    private int getCurrentShapeIndex(Brick currentBrick, int[][] currentShape) {
        if (currentBrick == null || currentShape == null) {
            return 0;
        }
        var allShapes = currentBrick.getShapeMatrix();
        for (int i = 0; i < allShapes.size(); i++) {
            if (shapesEqual(currentShape, allShapes.get(i))) {
                return i;
            }
        }
        return 0;
    }

    private boolean shapesEqual(int[][] shape1, int[][] shape2) {
        if (shape1 == null || shape2 == null) {
            return shape1 == shape2;
        }
        if (shape1.length != shape2.length) {
            return false;
        }
        for (int i = 0; i < shape1.length; i++) {
            if (shape1[i].length != shape2[i].length) {
                return false;
            }
            for (int j = 0; j < shape1[i].length; j++) {
                if (shape1[i][j] != shape2[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    static class RestorationResult {
        private final int[][] boardMatrix;
        private final Point currentOffset;
        private final int totalLinesCleared;

        RestorationResult(int[][] boardMatrix, Point currentOffset, int totalLinesCleared) {
            this.boardMatrix = boardMatrix;
            this.currentOffset = currentOffset;
            this.totalLinesCleared = totalLinesCleared;
        }

        int[][] getBoardMatrix() {
            return boardMatrix;
        }

        Point getCurrentOffset() {
            return currentOffset;
        }

        int getTotalLinesCleared() {
            return totalLinesCleared;
        }
    }
}

