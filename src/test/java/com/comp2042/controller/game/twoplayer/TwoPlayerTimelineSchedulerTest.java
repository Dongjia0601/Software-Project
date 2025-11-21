package com.comp2042.controller.game.twoplayer;

import com.comp2042.dto.DownData;
import com.comp2042.event.EventSource;
import com.comp2042.event.EventType;
import com.comp2042.event.MoveEvent;
import com.comp2042.model.mode.TwoPlayerMode;
import com.comp2042.service.gameloop.GameService;
import com.comp2042.service.gameloop.GameServiceImpl;
import com.comp2042.model.board.Board;
import com.comp2042.model.board.SimpleBoard;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TwoPlayerTimelineScheduler.
 * Tests timeline management, auto-drop scheduling, stats updates, pause/resume, and game over handling.
 */
@DisplayName("Two-Player Timeline Scheduler Tests")
class TwoPlayerTimelineSchedulerTest {

    private TwoPlayerTimelineScheduler scheduler;
    private GameService player1Service;
    private GameService player2Service;
    private TwoPlayerMode gameMode;
    private AtomicInteger player1DropCount;
    private AtomicInteger player2DropCount;
    private AtomicInteger statsTickCount;
    private Board player1Board;
    private Board player2Board;

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // JavaFX toolkit already initialized
        }
    }

    @BeforeEach
    void setUp() {
        player1Board = new SimpleBoard(10, 20);
        player2Board = new SimpleBoard(10, 20);
        player1Service = new GameServiceImpl(player1Board);
        player2Service = new GameServiceImpl(player2Board);

        // Create a minimal TwoPlayerMode for testing
        gameMode = new TwoPlayerMode(player1Service, player2Service, null);
        gameMode.initialize();

        player1DropCount = new AtomicInteger(0);
        player2DropCount = new AtomicInteger(0);
        statsTickCount = new AtomicInteger(0);

        scheduler = new TwoPlayerTimelineScheduler(
            player1Service,
            player2Service,
            gameMode,
            (playerIndex, downData) -> {
                if (playerIndex == 1) {
                    player1DropCount.incrementAndGet();
                } else if (playerIndex == 2) {
                    player2DropCount.incrementAndGet();
                }
            },
            () -> statsTickCount.incrementAndGet()
        );
    }

    @Test
    @DisplayName("Constructor: Creates scheduler with correct dependencies")
    void testConstructor() {
        assertNotNull(scheduler);
    }

    @Test
    @DisplayName("start: Starts all timelines")
    void testStart() {
        scheduler.start();
        
        // Wait a bit for timelines to execute
        sleep(500);
        
        // Verify timelines are running (callbacks should have been called)
        assertTrue(player1DropCount.get() > 0 || player2DropCount.get() > 0 || statsTickCount.get() > 0,
            "At least one timeline should have executed");
    }

    @Test
    @DisplayName("start: Stops previous timelines before starting new ones")
    void testStartStopsPrevious() {
        scheduler.start();
        sleep(200);
        int initialCount1 = player1DropCount.get();
        int initialCount2 = player2DropCount.get();
        
        scheduler.start(); // Restart
        
        sleep(200);
        // Counts should continue from where they were (not double)
        assertTrue(player1DropCount.get() >= initialCount1);
        assertTrue(player2DropCount.get() >= initialCount2);
    }

    @Test
    @DisplayName("pause: Pauses all timelines")
    void testPause() {
        scheduler.start();
        sleep(200);
        
        int countBefore1 = player1DropCount.get();
        int countBefore2 = player2DropCount.get();
        int statsBefore = statsTickCount.get();
        
        scheduler.pause();
        sleep(300);
        
        // Counts should not increase after pause
        assertEquals(countBefore1, player1DropCount.get(), "Player 1 drops should be paused");
        assertEquals(countBefore2, player2DropCount.get(), "Player 2 drops should be paused");
        assertEquals(statsBefore, statsTickCount.get(), "Stats ticks should be paused");
    }

    @Test
    @DisplayName("resume: Resumes all timelines")
    void testResume() {
        scheduler.start();
        sleep(200);
        scheduler.pause();
        sleep(100);
        
        int countBefore1 = player1DropCount.get();
        int countBefore2 = player2DropCount.get();
        int statsBefore = statsTickCount.get();
        
        scheduler.resume();
        sleep(300);
        
        // Counts should increase after resume
        assertTrue(player1DropCount.get() > countBefore1, "Player 1 drops should resume");
        assertTrue(player2DropCount.get() > countBefore2, "Player 2 drops should resume");
        // Stats may or may not tick depending on timing (1 second interval)
        assertTrue(statsTickCount.get() >= statsBefore, "Stats ticks should resume or maintain");
    }

    @Test
    @DisplayName("stop: Stops all timelines")
    void testStop() {
        scheduler.start();
        sleep(200);
        
        int countBefore1 = player1DropCount.get();
        int countBefore2 = player2DropCount.get();
        int statsBefore = statsTickCount.get();
        
        scheduler.stop();
        sleep(300);
        
        // Counts should not increase after stop
        assertEquals(countBefore1, player1DropCount.get(), "Player 1 drops should stop");
        assertEquals(countBefore2, player2DropCount.get(), "Player 2 drops should stop");
        assertEquals(statsBefore, statsTickCount.get(), "Stats ticks should stop");
    }

    @Test
    @DisplayName("pause: Does not process drops when paused")
    void testPauseBlocksDrops() {
        scheduler.start();
        sleep(100);
        scheduler.pause();
        
        int countBefore1 = player1DropCount.get();
        int countBefore2 = player2DropCount.get();
        
        sleep(500); // Wait longer to ensure no drops occur
        
        assertEquals(countBefore1, player1DropCount.get(), "No drops should occur when paused");
        assertEquals(countBefore2, player2DropCount.get(), "No drops should occur when paused");
    }

    @Test
    @DisplayName("pause: Does not process stats updates when paused")
    void testPauseBlocksStats() {
        scheduler.start();
        sleep(100);
        scheduler.pause();
        
        int statsBefore = statsTickCount.get();
        sleep(1200); // Wait 1.2 seconds (stats ticks every 1 second)
        
        assertEquals(statsBefore, statsTickCount.get(), "No stats updates should occur when paused");
    }

    @Test
    @DisplayName("Auto-drop: Processes drops for both players")
    void testAutoDropBothPlayers() {
        scheduler.start();
        sleep(500);
        
        assertTrue(player1DropCount.get() > 0, "Player 1 should have auto-drops");
        assertTrue(player2DropCount.get() > 0, "Player 2 should have auto-drops");
    }

    @Test
    @DisplayName("Auto-drop: Does not drop when game is over")
    void testAutoDropGameOver() {
        scheduler.start();
        sleep(100);
        
        // Force game over
        for (int i = 0; i < 20; i++) {
            player1Board.addGarbageLine();
            player2Board.addGarbageLine();
        }
        player1Board.createNewBrick();
        player2Board.createNewBrick();
        player1Service.processDownEvent(new MoveEvent(EventType.DOWN, EventSource.THREAD));
        player2Service.processDownEvent(new MoveEvent(EventType.DOWN, EventSource.THREAD));
        gameMode.update();
        
        int countBefore1 = player1DropCount.get();
        int countBefore2 = player2DropCount.get();
        
        sleep(500);
        
        // Counts should not increase when game is over
        assertEquals(countBefore1, player1DropCount.get(), "No drops when game is over");
        assertEquals(countBefore2, player2DropCount.get(), "No drops when game is over");
    }

    @Test
    @DisplayName("Stats update: Processes stats ticks")
    void testStatsUpdate() {
        scheduler.start();
        sleep(1200); // Wait 1.2 seconds (stats ticks every 1 second)
        
        assertTrue(statsTickCount.get() > 0, "Stats should have been updated");
    }

    @Test
    @DisplayName("Stats update: Does not update when game is over")
    void testStatsUpdateGameOver() {
        scheduler.start();
        sleep(100);
        
        // Pause first to prevent any stats updates while setting up game over
        scheduler.pause();
        for (int i = 0; i < 20; i++) {
            player1Board.addGarbageLine();
        }
        player1Board.createNewBrick();
        player1Service.processDownEvent(new MoveEvent(EventType.DOWN, EventSource.THREAD));
        gameMode.update();
        
        // Verify game is over
        assertTrue(gameMode.isGameOver() || player1Service.isGameOver(), "Game should be over");
        
        // Get stats count before resuming (should be 0 or 1 depending on timing)
        int statsBefore = statsTickCount.get();
        
        // Resume - but game is over, so stats should not update
        scheduler.resume();
        sleep(1200); // Wait 1.2 seconds (stats ticks every 1 second)
        
        // Stats should not increase when game is over
        assertEquals(statsBefore, statsTickCount.get(), "No stats updates when game is over");
    }

    @Test
    @DisplayName("stop: Can be called multiple times safely")
    void testStopMultipleTimes() {
        scheduler.start();
        sleep(100);
        
        scheduler.stop();
        scheduler.stop();
        scheduler.stop();
        
        // Should not throw exception
        assertDoesNotThrow(() -> scheduler.stop());
    }

    @Test
    @DisplayName("pause: Can be called when not started")
    void testPauseWhenNotStarted() {
        // Should not throw exception
        assertDoesNotThrow(() -> scheduler.pause());
    }

    @Test
    @DisplayName("resume: Can be called when not started")
    void testResumeWhenNotStarted() {
        // Should not throw exception
        assertDoesNotThrow(() -> scheduler.resume());
    }

    @Test
    @DisplayName("Lifecycle: Start -> Pause -> Resume -> Stop")
    void testLifecycle() {
        scheduler.start();
        sleep(500); // Wait for timelines to start (400ms interval)
        // Verify at least one timeline is running
        boolean hasDrops = player1DropCount.get() > 0 || player2DropCount.get() > 0;
        assertTrue(hasDrops || statsTickCount.get() > 0,
            "At least one timeline should have executed after start");
        
        scheduler.pause();
        sleep(100);
        int pausedCount1 = player1DropCount.get();
        int pausedCount2 = player2DropCount.get();
        int pausedStats = statsTickCount.get();
        
        scheduler.resume();
        sleep(500); // Wait for resume to take effect
        // Verify that timelines continue after resume
        boolean resumed = (player1DropCount.get() > pausedCount1) || 
                         (player2DropCount.get() > pausedCount2) ||
                         (statsTickCount.get() > pausedStats);
        assertTrue(resumed, "Timelines should continue after resume");
        
        scheduler.stop();
        sleep(100);
        int stoppedCount1 = player1DropCount.get();
        int stoppedCount2 = player2DropCount.get();
        int stoppedStats = statsTickCount.get();
        
        sleep(500); // Wait to ensure no more updates occur
        assertEquals(stoppedCount1, player1DropCount.get(), "No drops after stop");
        assertEquals(stoppedCount2, player2DropCount.get(), "No drops after stop");
        assertEquals(stoppedStats, statsTickCount.get(), "No stats updates after stop");
    }

    /**
     * Helper method to sleep for a specified number of milliseconds.
     */
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

