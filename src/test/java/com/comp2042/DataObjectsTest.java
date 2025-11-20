package com.comp2042;

import com.comp2042.dto.ClearRow;
import com.comp2042.dto.DownData;
import com.comp2042.dto.ViewData;
import com.comp2042.event.EventSource;
import com.comp2042.event.EventType;
import com.comp2042.event.MoveEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for data transfer objects (DTOs) and value objects.
 * Tests ClearRow, ViewData, DownData, and MoveEvent classes.
 * 
 * <p>These tests validate immutability, equals/hashCode contracts, and
 * proper data encapsulation for all data transfer objects.</p>
 * 
 * @author Dong, Jia.
 */
@DisplayName("Data Objects Tests")
class DataObjectsTest {

    // ========== ClearRow Tests ==========

    @Test
    @DisplayName("ClearRow: Constructor initializes all fields correctly")
    void testClearRowConstructor() {
        int[][] matrix = new int[20][10];
        ClearRow clearRow = new ClearRow(3, matrix, 500);
        
        assertEquals(3, clearRow.getLinesRemoved());
        assertEquals(500, clearRow.getScoreBonus());
        assertNotNull(clearRow.getNewMatrix());
        assertEquals(20, clearRow.getNewMatrix().length);
        assertEquals(10, clearRow.getNewMatrix()[0].length);
    }

    @Test
    @DisplayName("ClearRow: getNewMatrix returns defensive copy")
    void testClearRowDefensiveCopy() {
        int[][] original = new int[20][10];
        original[0][0] = 5;
        
        ClearRow clearRow = new ClearRow(2, original, 300);
        int[][] returned = clearRow.getNewMatrix();
        
        // Modify returned matrix
        returned[0][0] = 999;
        
        // Original should be unchanged
        assertEquals(5, original[0][0]);
        // Returned copy should be modified
        assertEquals(999, returned[0][0]);
    }

    @Test
    @DisplayName("ClearRow: Handles zero lines removed")
    void testClearRowZeroLines() {
        int[][] matrix = new int[20][10];
        ClearRow clearRow = new ClearRow(0, matrix, 0);
        
        assertEquals(0, clearRow.getLinesRemoved());
        assertEquals(0, clearRow.getScoreBonus());
    }

    @Test
    @DisplayName("ClearRow: Handles maximum lines removed (Tetris)")
    void testClearRowTetris() {
        int[][] matrix = new int[20][10];
        ClearRow clearRow = new ClearRow(4, matrix, 800);
        
        assertEquals(4, clearRow.getLinesRemoved());
        assertEquals(800, clearRow.getScoreBonus());
    }

    // ========== ViewData Tests ==========

    @Test
    @DisplayName("ViewData: Full constructor initializes all fields")
    void testViewDataFullConstructor() {
        int[][] brickData = {{1, 1}, {1, 1}};
        int[][] nextBrick = {{2, 2}};
        int[][] holdBrick = {{3, 3}};
        
        ViewData viewData = new ViewData(brickData, 5, 10, 15, nextBrick, holdBrick);
        
        assertArrayEquals(brickData, viewData.getBrickData());
        assertEquals(5, viewData.getXPosition());
        assertEquals(10, viewData.getYPosition());
        assertEquals(15, viewData.getGhostYPosition());
        assertArrayEquals(nextBrick, viewData.getNextBrickData());
        assertArrayEquals(holdBrick, viewData.getHoldBrickData());
    }

    @Test
    @DisplayName("ViewData: Backward compatibility constructor sets defaults")
    void testViewDataBackwardCompatibilityConstructor() {
        int[][] brickData = {{1, 1}};
        int[][] nextBrick = {{2, 2}};
        
        ViewData viewData = new ViewData(brickData, 3, 4, nextBrick);
        
        assertEquals(3, viewData.getXPosition());
        assertEquals(4, viewData.getYPosition());
        assertEquals(-1, viewData.getGhostYPosition()); // Default
        assertNull(viewData.getHoldBrickData()); // Default
    }

    @Test
    @DisplayName("ViewData: getBrickData returns defensive copy")
    void testViewDataBrickDataDefensiveCopy() {
        int[][] original = {{1, 2}, {3, 4}};
        ViewData viewData = new ViewData(original, 0, 0, new int[][]{{0}});
        
        int[][] returned = viewData.getBrickData();
        returned[0][0] = 999;
        
        // Original should be unchanged
        assertEquals(1, original[0][0]);
        // Returned copy should be modified
        assertEquals(999, returned[0][0]);
    }

    @Test
    @DisplayName("ViewData: getNextBrickData returns defensive copy")
    void testViewDataNextBrickDataDefensiveCopy() {
        int[][] original = {{5, 6}};
        ViewData viewData = new ViewData(new int[][]{{0}}, 0, 0, original);
        
        int[][] returned = viewData.getNextBrickData();
        returned[0][0] = 999;
        
        assertEquals(5, original[0][0]);
        assertEquals(999, returned[0][0]);
    }

    @Test
    @DisplayName("ViewData: getHoldBrickData returns defensive copy when not null")
    void testViewDataHoldBrickDataDefensiveCopy() {
        int[][] original = {{7, 8}};
        ViewData viewData = new ViewData(new int[][]{{0}}, 0, 0, -1, new int[][]{{0}}, original);
        
        int[][] returned = viewData.getHoldBrickData();
        returned[0][0] = 999;
        
        assertEquals(7, original[0][0]);
        assertEquals(999, returned[0][0]);
    }

    @Test
    @DisplayName("ViewData: getHoldBrickData returns null when no brick held")
    void testViewDataHoldBrickDataNull() {
        ViewData viewData = new ViewData(new int[][]{{1}}, 0, 0, new int[][]{{2}});
        
        assertNull(viewData.getHoldBrickData());
    }

    @Test
    @DisplayName("ViewData: equals returns true for identical objects")
    void testViewDataEqualsIdentical() {
        int[][] brick = {{1, 1}};
        int[][] next = {{2, 2}};
        
        ViewData v1 = new ViewData(brick, 5, 10, next);
        ViewData v2 = new ViewData(brick, 5, 10, next);
        
        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());
    }

    @Test
    @DisplayName("ViewData: equals returns false for different objects")
    void testViewDataEqualsDifferent() {
        ViewData v1 = new ViewData(new int[][]{{1}}, 5, 10, new int[][]{{2}});
        ViewData v2 = new ViewData(new int[][]{{1}}, 6, 10, new int[][]{{2}});
        
        assertNotEquals(v1, v2);
    }

    @Test
    @DisplayName("ViewData: equals handles null and different types")
    void testViewDataEqualsNullAndType() {
        ViewData v1 = new ViewData(new int[][]{{1}}, 0, 0, new int[][]{{2}});
        
        assertNotEquals(v1, null);
        assertNotEquals(v1, "not a ViewData");
    }

    @Test
    @DisplayName("ViewData: toString provides meaningful representation")
    void testViewDataToString() {
        ViewData viewData = new ViewData(new int[][]{{1}}, 5, 10, new int[][]{{2}});
        String str = viewData.toString();
        
        assertNotNull(str);
        assertTrue(str.contains("ViewData"));
        assertTrue(str.contains("xPosition=5"));
        assertTrue(str.contains("yPosition=10"));
    }

    // ========== DownData Tests ==========

    @Test
    @DisplayName("DownData: Two-parameter constructor sets defaults")
    void testDownDataTwoParameterConstructor() {
        ClearRow clearRow = new ClearRow(1, new int[20][10], 100);
        ViewData viewData = new ViewData(new int[][]{{1}}, 0, 0, new int[][]{{2}});
        
        DownData downData = new DownData(clearRow, viewData);
        
        assertEquals(clearRow, downData.getClearRow());
        assertEquals(viewData, downData.getViewData());
        assertFalse(downData.isBrickLanded());
        assertEquals(0, downData.getScoreBonus());
    }

    @Test
    @DisplayName("DownData: Full constructor initializes all fields")
    void testDownDataFullConstructor() {
        ClearRow clearRow = new ClearRow(2, new int[20][10], 300);
        ViewData viewData = new ViewData(new int[][]{{1}}, 0, 0, new int[][]{{2}});
        
        DownData downData = new DownData(clearRow, viewData, true, 50);
        
        assertTrue(downData.isBrickLanded());
        assertEquals(50, downData.getScoreBonus());
    }

    @Test
    @DisplayName("DownData: hasLinesCleared returns true when lines cleared")
    void testDownDataHasLinesCleared() {
        ClearRow clearRow = new ClearRow(2, new int[20][10], 300);
        ViewData viewData = new ViewData(new int[][]{{1}}, 0, 0, new int[][]{{2}});
        
        DownData downData = new DownData(clearRow, viewData);
        
        assertTrue(downData.hasLinesCleared());
    }

    @Test
    @DisplayName("DownData: hasLinesCleared returns false when no lines cleared")
    void testDownDataNoLinesCleared() {
        ClearRow clearRow = new ClearRow(0, new int[20][10], 0);
        ViewData viewData = new ViewData(new int[][]{{1}}, 0, 0, new int[][]{{2}});
        
        DownData downData = new DownData(clearRow, viewData);
        
        assertFalse(downData.hasLinesCleared());
    }

    @Test
    @DisplayName("DownData: hasLinesCleared returns false when clearRow is null")
    void testDownDataHasLinesClearedNull() {
        ViewData viewData = new ViewData(new int[][]{{1}}, 0, 0, new int[][]{{2}});
        
        DownData downData = new DownData(null, viewData);
        
        assertFalse(downData.hasLinesCleared());
    }

    @Test
    @DisplayName("DownData: getTotalScore calculates correctly")
    void testDownDataGetTotalScore() {
        ClearRow clearRow = new ClearRow(1, new int[20][10], 100);
        ViewData viewData = new ViewData(new int[][]{{1}}, 0, 0, new int[][]{{2}});
        
        DownData downData = new DownData(clearRow, viewData, false, 25);
        
        assertEquals(125, downData.getTotalScore()); // 100 + 25
    }

    @Test
    @DisplayName("DownData: getTotalScore handles null clearRow")
    void testDownDataGetTotalScoreNullClearRow() {
        ViewData viewData = new ViewData(new int[][]{{1}}, 0, 0, new int[][]{{2}});
        
        DownData downData = new DownData(null, viewData, false, 30);
        
        assertEquals(30, downData.getTotalScore()); // Only scoreBonus
    }

    @Test
    @DisplayName("DownData: equals returns true for identical objects")
    void testDownDataEqualsIdentical() {
        ClearRow clearRow = new ClearRow(1, new int[20][10], 100);
        ViewData viewData = new ViewData(new int[][]{{1}}, 0, 0, new int[][]{{2}});
        
        DownData d1 = new DownData(clearRow, viewData, true, 50);
        DownData d2 = new DownData(clearRow, viewData, true, 50);
        
        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    @DisplayName("DownData: equals returns false for different objects")
    void testDownDataEqualsDifferent() {
        ClearRow clearRow = new ClearRow(1, new int[20][10], 100);
        ViewData viewData = new ViewData(new int[][]{{1}}, 0, 0, new int[][]{{2}});
        
        DownData d1 = new DownData(clearRow, viewData, true, 50);
        DownData d2 = new DownData(clearRow, viewData, false, 50);
        
        assertNotEquals(d1, d2);
    }

    @Test
    @DisplayName("DownData: toString provides meaningful representation")
    void testDownDataToString() {
        ClearRow clearRow = new ClearRow(2, new int[20][10], 300);
        ViewData viewData = new ViewData(new int[][]{{1}}, 0, 0, new int[][]{{2}});
        
        DownData downData = new DownData(clearRow, viewData, true, 25);
        String str = downData.toString();
        
        assertNotNull(str);
        assertTrue(str.contains("DownData"));
        assertTrue(str.contains("landed=true"));
        assertTrue(str.contains("linesCleared=2"));
    }

    // ========== MoveEvent Tests ==========

    @Test
    @DisplayName("MoveEvent: Two-parameter constructor sets timestamp automatically")
    void testMoveEventTwoParameterConstructor() {
        long before = System.currentTimeMillis();
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);
        long after = System.currentTimeMillis();
        
        assertEquals(EventType.DOWN, event.getEventType());
        assertEquals(EventSource.USER, event.getEventSource());
        assertNull(event.getAdditionalData());
        assertTrue(event.getTimestamp() >= before && event.getTimestamp() <= after);
    }

    @Test
    @DisplayName("MoveEvent: Three-parameter constructor with additional data")
    void testMoveEventThreeParameterConstructor() {
        String additionalData = "test data";
        MoveEvent event = new MoveEvent(EventType.ROTATE, EventSource.USER, additionalData);
        
        assertEquals(EventType.ROTATE, event.getEventType());
        assertEquals(EventSource.USER, event.getEventSource());
        assertEquals(additionalData, event.getAdditionalData());
    }

    @Test
    @DisplayName("MoveEvent: Full constructor with all parameters")
    void testMoveEventFullConstructor() {
        long timestamp = 1234567890L;
        Object data = new Object();
        
        MoveEvent event = new MoveEvent(EventType.LEFT, EventSource.SYSTEM, timestamp, data);
        
        assertEquals(EventType.LEFT, event.getEventType());
        assertEquals(EventSource.SYSTEM, event.getEventSource());
        assertEquals(timestamp, event.getTimestamp());
        assertEquals(data, event.getAdditionalData());
    }

    @Test
    @DisplayName("MoveEvent: isMovementEvent identifies movement events")
    void testMoveEventIsMovementEvent() {
        assertTrue(new MoveEvent(EventType.LEFT, EventSource.USER).isMovementEvent());
        assertTrue(new MoveEvent(EventType.RIGHT, EventSource.USER).isMovementEvent());
        assertTrue(new MoveEvent(EventType.DOWN, EventSource.USER).isMovementEvent());
        assertFalse(new MoveEvent(EventType.ROTATE, EventSource.USER).isMovementEvent());
        assertFalse(new MoveEvent(EventType.PAUSE, EventSource.USER).isMovementEvent());
    }

    @Test
    @DisplayName("MoveEvent: isRotationEvent identifies rotation events")
    void testMoveEventIsRotationEvent() {
        assertTrue(new MoveEvent(EventType.ROTATE, EventSource.USER).isRotationEvent());
        assertTrue(new MoveEvent(EventType.ROTATE_CCW, EventSource.USER).isRotationEvent());
        assertFalse(new MoveEvent(EventType.LEFT, EventSource.USER).isRotationEvent());
        assertFalse(new MoveEvent(EventType.DOWN, EventSource.USER).isRotationEvent());
    }

    @Test
    @DisplayName("MoveEvent: isDropEvent identifies drop events")
    void testMoveEventIsDropEvent() {
        assertTrue(new MoveEvent(EventType.HARD_DROP, EventSource.USER).isDropEvent());
        assertTrue(new MoveEvent(EventType.SOFT_DROP, EventSource.USER).isDropEvent());
        assertFalse(new MoveEvent(EventType.DOWN, EventSource.USER).isDropEvent());
        assertFalse(new MoveEvent(EventType.ROTATE, EventSource.USER).isDropEvent());
    }

    @Test
    @DisplayName("MoveEvent: isUserEvent identifies user events")
    void testMoveEventIsUserEvent() {
        assertTrue(new MoveEvent(EventType.DOWN, EventSource.USER).isUserEvent());
        assertFalse(new MoveEvent(EventType.DOWN, EventSource.SYSTEM).isUserEvent());
    }

    @Test
    @DisplayName("MoveEvent: equals returns true for identical events")
    void testMoveEventEqualsIdentical() {
        long timestamp = 1234567890L;
        MoveEvent e1 = new MoveEvent(EventType.ROTATE, EventSource.USER, timestamp, null);
        MoveEvent e2 = new MoveEvent(EventType.ROTATE, EventSource.USER, timestamp, null);
        
        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    @DisplayName("MoveEvent: equals returns false for different events")
    void testMoveEventEqualsDifferent() {
        long timestamp = 1234567890L;
        MoveEvent e1 = new MoveEvent(EventType.ROTATE, EventSource.USER, timestamp, null);
        MoveEvent e2 = new MoveEvent(EventType.LEFT, EventSource.USER, timestamp, null);
        
        assertNotEquals(e1, e2);
    }

    @Test
    @DisplayName("MoveEvent: equals handles null and different types")
    void testMoveEventEqualsNullAndType() {
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);
        
        assertNotEquals(event, null);
        assertNotEquals(event, "not a MoveEvent");
    }

    @Test
    @DisplayName("MoveEvent: toString provides meaningful representation")
    void testMoveEventToString() {
        MoveEvent event = new MoveEvent(EventType.ROTATE, EventSource.USER, 1234567890L, "data");
        String str = event.toString();
        
        assertNotNull(str);
        assertTrue(str.contains("MoveEvent"));
        assertTrue(str.contains("ROTATE"));
        assertTrue(str.contains("USER"));
    }
}
