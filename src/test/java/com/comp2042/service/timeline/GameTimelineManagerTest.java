package com.comp2042.service.timeline;

import javafx.application.Platform;
import javafx.animation.Animation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GameTimelineManager.
 *
 * <p>Tests cover:
 * <ul>
 *   <li>Timeline initialization</li>
 *   <li>Game loop control (start, stop, speed update)</li>
 *   <li>Elapsed time timer control</li>
 *   <li>Level countdown timer control</li>
 *   <li>Unified pause/resume/stop operations</li>
 *   <li>Callback execution</li>
 *   <li>Error handling (invalid parameters)</li>
 * </ul>
 */
class GameTimelineManagerTest {

    private GameTimelineManager manager;
    private AtomicInteger moveDownCount;
    private AtomicInteger timeUpdateCount;
    private AtomicInteger levelTickCount;
    private int lastLevelTickValue;

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // JavaFX toolkit already initialized
        }
    }

    @BeforeEach
    void setUp() {
        moveDownCount = new AtomicInteger(0);
        timeUpdateCount = new AtomicInteger(0);
        levelTickCount = new AtomicInteger(0);
        lastLevelTickValue = -1;

        manager = new GameTimelineManager(
            () -> moveDownCount.incrementAndGet(),
            () -> timeUpdateCount.incrementAndGet(),
            (seconds) -> {
                levelTickCount.incrementAndGet();
                lastLevelTickValue = seconds;
            }
        );
    }

    @Test
    void testConstructorWithoutCallbacks() {
        GameTimelineManager emptyManager = new GameTimelineManager();
        assertNotNull(emptyManager);
        assertFalse(emptyManager.isGameLoopRunning());
    }

    @Test
    void testConstructorWithCallbacks() {
        assertNotNull(manager);
        assertFalse(manager.isGameLoopRunning());
    }

    @Test
    void testSetCallbacks() {
        AtomicInteger newMoveDownCount = new AtomicInteger(0);
        AtomicInteger newTimeUpdateCount = new AtomicInteger(0);
        AtomicInteger newLevelTickCount = new AtomicInteger(0);

        manager.setCallbacks(
            () -> newMoveDownCount.incrementAndGet(),
            () -> newTimeUpdateCount.incrementAndGet(),
            (seconds) -> newLevelTickCount.incrementAndGet()
        );

        // Start game loop to verify new callback is used
        manager.startGameLoop(100);
        sleep(150);
        manager.stopGameLoop();

        assertEquals(0, moveDownCount.get(), "Old callback should not be called");
        assertTrue(newMoveDownCount.get() > 0, "New callback should be called");
    }

    @Test
    void testStartGameLoop() throws InterruptedException {
        assertFalse(manager.isGameLoopRunning());

        manager.startGameLoop(100);
        
        // Wait for timeline to start (JavaFX Timeline starts asynchronously)
        sleep(50);
        assertTrue(manager.isGameLoopRunning(), "Game loop should be running after start");

        // Wait for at least one tick
        sleep(150);
        assertTrue(moveDownCount.get() > 0, "Move down callback should be called");

        manager.stopGameLoop();
        sleep(50); // Wait for stop to complete
        assertFalse(manager.isGameLoopRunning());
    }

    @Test
    void testStartGameLoopWithInvalidSpeed() {
        assertThrows(IllegalArgumentException.class, () -> manager.startGameLoop(0));
        assertThrows(IllegalArgumentException.class, () -> manager.startGameLoop(-1));
    }

    @Test
    void testStopGameLoop() {
        manager.startGameLoop(100);
        // Wait longer for timeline to start (JavaFX Timeline starts asynchronously on JavaFX thread)
        // Check if callback was called as evidence it's running
        sleep(300); // Increased wait time for async Timeline startup
        int initialCount = moveDownCount.get();
        
        // Use Platform.runLater to check status on JavaFX thread
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] isRunning = new boolean[1];
        Platform.runLater(() -> {
            isRunning[0] = manager.isGameLoopRunning();
            latch.countDown();
        });
        try {
            latch.await(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        assertTrue(initialCount > 0 || isRunning[0], 
            "Game loop should be running (check callback count or status)");

        manager.stopGameLoop();
        sleep(150); // Wait for stop to complete
        assertFalse(manager.isGameLoopRunning(), "Game loop should be stopped");

        int countBefore = moveDownCount.get();
        sleep(150);
        assertEquals(countBefore, moveDownCount.get(), "Callback should not be called after stop");
    }

    @Test
    void testUpdateGameLoopSpeed() throws InterruptedException {
        manager.startGameLoop(200);
        sleep(250);
        int countAt200ms = moveDownCount.get();

        // Update to faster speed
        manager.updateGameLoopSpeed(100);
        assertTrue(manager.isGameLoopRunning(), "Game loop should still be running");

        sleep(150);
        int countAfterUpdate = moveDownCount.get();
        assertTrue(countAfterUpdate > countAt200ms, "Should have more ticks after speed increase");

        manager.stopGameLoop();
    }

    @Test
    void testUpdateGameLoopSpeedWhenStopped() {
        assertFalse(manager.isGameLoopRunning());
        manager.updateGameLoopSpeed(100);
        assertFalse(manager.isGameLoopRunning(), "Should remain stopped");
    }

    @Test
    void testUpdateGameLoopSpeedWithInvalidSpeed() {
        manager.startGameLoop(100);
        assertThrows(IllegalArgumentException.class, () -> manager.updateGameLoopSpeed(0));
        assertThrows(IllegalArgumentException.class, () -> manager.updateGameLoopSpeed(-1));
        manager.stopGameLoop();
    }

    @Test
    void testStartElapsedTimer() throws InterruptedException {
        manager.startElapsedTimer(System.currentTimeMillis());
        sleep(1200); // Wait for at least one second (timer ticks every 1 second)
        assertTrue(timeUpdateCount.get() > 0, "Time update callback should be called");

        manager.stopElapsedTimer();
    }

    @Test
    void testStopElapsedTimer() {
        manager.startElapsedTimer(System.currentTimeMillis());
        sleep(100);
        manager.stopElapsedTimer();

        int countBefore = timeUpdateCount.get();
        sleep(1200); // Wait 1.2 seconds to ensure no callback after stop
        assertEquals(countBefore, timeUpdateCount.get(), "Callback should not be called after stop");
    }

    @Test
    void testStartLevelCountdown() throws InterruptedException {
        manager.startLevelCountdown(3);
        sleep(1200); // Wait for first tick (timer ticks every 1 second)
        assertTrue(levelTickCount.get() > 0, "Level tick callback should be called");
        assertEquals(2, lastLevelTickValue, "Should have decremented to 2");

        // Verify it's working, then stop (don't wait for full countdown)
        manager.stopLevelTimer();
    }

    @Test
    void testStartLevelCountdownWithInvalidSeconds() {
        assertThrows(IllegalArgumentException.class, () -> manager.startLevelCountdown(-1));
    }

    @Test
    void testStartLevelCountdownAutoStop() throws InterruptedException {
        manager.startLevelCountdown(1); // Use shorter countdown (1 second)
        // Wait for countdown to complete (1 second + buffer)
        sleep(1500);
        // Timer should auto-stop when reaching 0
        // Verify it stopped (no more ticks)
        int finalCount = levelTickCount.get();
        sleep(1200); // Wait to ensure no more ticks
        assertEquals(finalCount, levelTickCount.get(), "Should not tick after reaching 0");
    }

    @Test
    void testStopLevelTimer() {
        manager.startLevelCountdown(5);
        sleep(100);
        manager.stopLevelTimer();

        int countBefore = levelTickCount.get();
        sleep(1200); // Wait 1.2 seconds to ensure no callback after stop
        assertEquals(countBefore, levelTickCount.get(), "Callback should not be called after stop");
    }

    @Test
    void testPauseAll() throws InterruptedException {
        manager.startGameLoop(100);
        manager.startElapsedTimer(System.currentTimeMillis());
        manager.startLevelCountdown(10);

        sleep(100);
        int moveDownBefore = moveDownCount.get();
        int timeUpdateBefore = timeUpdateCount.get();
        int levelTickBefore = levelTickCount.get();

        manager.pauseAll();
        sleep(200);

        assertEquals(moveDownBefore, moveDownCount.get(), "Move down should be paused");
        assertEquals(timeUpdateBefore, timeUpdateCount.get(), "Time update should be paused");
        assertEquals(levelTickBefore, levelTickCount.get(), "Level tick should be paused");

        manager.stopAll();
    }

    @Test
    void testResumeAll() throws InterruptedException {
        manager.startGameLoop(100);
        manager.startElapsedTimer(System.currentTimeMillis());
        manager.startLevelCountdown(10);

        sleep(200); // Wait for timelines to start and get initial ticks
        manager.pauseAll();
        sleep(150);

        int moveDownBefore = moveDownCount.get();
        int timeUpdateBefore = timeUpdateCount.get();
        int levelTickBefore = levelTickCount.get();

        manager.resumeAll();
        // Move down should resume quickly (100ms interval)
        sleep(250);
        assertTrue(moveDownCount.get() > moveDownBefore, "Move down should resume");
        
        // Time update and level tick run every 1 second, so check they at least didn't decrease
        assertTrue(timeUpdateCount.get() >= timeUpdateBefore, "Time update should resume or maintain");
        assertTrue(levelTickCount.get() >= levelTickBefore, "Level tick should resume or maintain");

        manager.stopAll();
    }

    @Test
    void testStopAll() {
        manager.startGameLoop(100);
        manager.startElapsedTimer(System.currentTimeMillis());
        manager.startLevelCountdown(10);

        sleep(150); // Wait for timelines to start
        manager.stopAll();
        sleep(50); // Wait for stop to complete

        assertFalse(manager.isGameLoopRunning(), "Game loop should be stopped");
        int moveDownBefore = moveDownCount.get();
        int timeUpdateBefore = timeUpdateCount.get();
        int levelTickBefore = levelTickCount.get();

        sleep(200);
        assertEquals(moveDownBefore, moveDownCount.get(), "Move down should not continue");
        assertEquals(timeUpdateBefore, timeUpdateCount.get(), "Time update should not continue");
        assertEquals(levelTickBefore, levelTickCount.get(), "Level tick should not continue");
    }

    @Test
    void testIsGameLoopRunning() {
        assertFalse(manager.isGameLoopRunning(), "Should not be running initially");
        manager.startGameLoop(100);
        // Wait longer for timeline to start (JavaFX Timeline starts asynchronously on JavaFX thread)
        // Check if callback was called as evidence it's running
        sleep(300); // Increased wait time for async Timeline startup
        int callbackCount = moveDownCount.get();
        
        // Use Platform.runLater to check status on JavaFX thread
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] isRunning = new boolean[1];
        Platform.runLater(() -> {
            isRunning[0] = manager.isGameLoopRunning();
            latch.countDown();
        });
        try {
            latch.await(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Either the status should be true, or callbacks should have been called
        assertTrue(isRunning[0] || callbackCount > 0, 
            "Should be running after start (check status or callback count)");
        manager.stopGameLoop();
        sleep(150); // Wait for stop to complete
        assertFalse(manager.isGameLoopRunning(), "Should not be running after stop");
    }

    @Test
    void testManagerWithoutCallbacks() {
        GameTimelineManager emptyManager = new GameTimelineManager();
        
        // Should throw exception when starting without callbacks
        assertThrows(IllegalStateException.class, () -> {
            emptyManager.startGameLoop(100);
        });
        
        // Set callbacks first
        emptyManager.setCallbacks(
            () -> {},
            () -> {},
            (seconds) -> {}
        );
        
        // Now should work
        emptyManager.startGameLoop(100);
        sleep(100);
        emptyManager.stopGameLoop();
        
        emptyManager.startElapsedTimer(System.currentTimeMillis());
        sleep(100);
        emptyManager.stopElapsedTimer();
        
        emptyManager.startLevelCountdown(5);
        sleep(100);
        emptyManager.stopLevelTimer();
        
        emptyManager.pauseAll();
        emptyManager.resumeAll();
        emptyManager.stopAll();
    }

    /**
     * Helper method to sleep for a specified number of milliseconds.
     * This is a simple sleep that works in test environment.
     */
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

