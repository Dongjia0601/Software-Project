package com.comp2042.controller.game.twoplayer;

import com.comp2042.controller.game.GuiController;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TwoPlayerCountdownManager.
 * Tests countdown startup, callback execution, and null handling.
 */
@DisplayName("Two-Player Countdown Manager Tests")
class TwoPlayerCountdownManagerTest {

    private TwoPlayerCountdownManager countdownManager;
    private GuiController mockGuiController;

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
        // Create a mock GuiController that tracks countdown calls
        mockGuiController = new GuiController() {
            private Runnable lastCallback;
            private int showCountdownCallCount = 0;

            @Override
            public void showCountdown(Runnable onComplete) {
                showCountdownCallCount++;
                lastCallback = onComplete;
                // Immediately execute callback for testing
                if (onComplete != null) {
                    onComplete.run();
                }
            }

            public int getShowCountdownCallCount() {
                return showCountdownCallCount;
            }

            public Runnable getLastCallback() {
                return lastCallback;
            }
        };
    }

    @Test
    @DisplayName("Constructor: Creates manager with GuiController")
    void testConstructor() {
        countdownManager = new TwoPlayerCountdownManager(mockGuiController);
        assertNotNull(countdownManager);
    }

    @Test
    @DisplayName("startCountdown: Delegates to GuiController when available")
    void testStartCountdownWithGuiController() {
        countdownManager = new TwoPlayerCountdownManager(mockGuiController);
        AtomicBoolean callbackExecuted = new AtomicBoolean(false);

        countdownManager.startCountdown(() -> callbackExecuted.set(true));

        assertTrue(callbackExecuted.get(), "Callback should be executed");
    }

    @Test
    @DisplayName("startCountdown: Executes callback immediately when GuiController is null")
    void testStartCountdownWithNullGuiController() {
        countdownManager = new TwoPlayerCountdownManager(null);
        AtomicBoolean callbackExecuted = new AtomicBoolean(false);

        countdownManager.startCountdown(() -> callbackExecuted.set(true));

        assertTrue(callbackExecuted.get(), "Callback should be executed immediately when GuiController is null");
    }

    @Test
    @DisplayName("startCountdown: Handles null callback gracefully")
    void testStartCountdownWithNullCallback() {
        countdownManager = new TwoPlayerCountdownManager(mockGuiController);

        // Should not throw exception
        assertDoesNotThrow(() -> {
            countdownManager.startCountdown(null);
        });
    }

    @Test
    @DisplayName("startCountdown: Handles null callback with null GuiController")
    void testStartCountdownNullCallbackAndNullGui() {
        countdownManager = new TwoPlayerCountdownManager(null);

        assertThrows(NullPointerException.class, () -> {
            countdownManager.startCountdown(null);
        });
    }

    @Test
    @DisplayName("startCountdown: Executes callback synchronously when GuiController is null")
    void testStartCountdownSynchronousExecution() {
        countdownManager = new TwoPlayerCountdownManager(null);
        AtomicBoolean callbackExecuted = new AtomicBoolean(false);
        AtomicBoolean afterCall = new AtomicBoolean(false);

        countdownManager.startCountdown(() -> {
            callbackExecuted.set(true);
            // Verify this executes before the next line
            assertFalse(afterCall.get(), "Callback should execute before subsequent code");
        });
        afterCall.set(true);

        assertTrue(callbackExecuted.get(), "Callback should have been executed");
        assertTrue(afterCall.get(), "After-call flag should be set");
    }

    @Test
    @DisplayName("startCountdown: Multiple calls work correctly")
    void testStartCountdownMultipleCalls() {
        countdownManager = new TwoPlayerCountdownManager(mockGuiController);
        AtomicInteger callCount = new AtomicInteger(0);

        countdownManager.startCountdown(() -> callCount.incrementAndGet());
        countdownManager.startCountdown(() -> callCount.incrementAndGet());
        countdownManager.startCountdown(() -> callCount.incrementAndGet());

        assertEquals(3, callCount.get(), "All callbacks should be executed");
    }
}

