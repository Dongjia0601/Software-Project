package com.comp2042;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Interface Segregation Principle (ISP) compliance.
 * 
 * <p>These tests verify that the interface segregation refactoring
 * maintains backward compatibility while improving design.</p>
 * 
 * @author Dong, Jia.
 */
@DisplayName("Interface Segregation Principle Tests")
class InterfaceSegregationTest {

    // ========== Interface Structure Tests ==========

    @Test
    @DisplayName("InputEventListener extends both BrickMovementListener and GameControlListener")
    void testInputEventListenerExtendsBoth() {
        // Verify that InputEventListener extends both interfaces
        assertTrue(BrickMovementListener.class.isAssignableFrom(InputEventListener.class),
            "InputEventListener should extend BrickMovementListener");
        assertTrue(GameControlListener.class.isAssignableFrom(InputEventListener.class),
            "InputEventListener should extend GameControlListener");
    }

    @Test
    @DisplayName("BrickMovementListener and GameControlListener are separate interfaces")
    void testInterfacesAreSeparate() {
        // Verify they are different interfaces
        assertNotSame(BrickMovementListener.class, GameControlListener.class,
            "BrickMovementListener and GameControlListener should be separate interfaces");
        
        // Verify they don't extend each other
        assertFalse(BrickMovementListener.class.isAssignableFrom(GameControlListener.class),
            "BrickMovementListener should not extend GameControlListener");
        assertFalse(GameControlListener.class.isAssignableFrom(BrickMovementListener.class),
            "GameControlListener should not extend BrickMovementListener");
    }

    // ========== Backward Compatibility Tests ==========

    @Test
    @DisplayName("GameController implements InputEventListener (backward compatible)")
    void testGameControllerImplementsInputEventListener() {
        // Create a mock GuiController for testing
        GuiController mockGui = null; // Would need proper mocking in real test
        
        // Verify GameController implements InputEventListener
        assertTrue(InputEventListener.class.isAssignableFrom(GameController.class),
            "GameController should implement InputEventListener");
    }

    @Test
    @DisplayName("TwoPlayerGameController implements InputEventListener (backward compatible)")
    void testTwoPlayerGameControllerImplementsInputEventListener() {
        // Verify TwoPlayerGameController implements InputEventListener
        assertTrue(InputEventListener.class.isAssignableFrom(TwoPlayerGameController.class),
            "TwoPlayerGameController should implement InputEventListener");
    }

    // ========== Interface Segregation Benefits Tests ==========

    @Test
    @DisplayName("BrickMovementListener has only brick movement methods")
    void testBrickMovementListenerMethods() {
        // Verify BrickMovementListener has 8 methods (brick-related)
        java.lang.reflect.Method[] methods = BrickMovementListener.class.getMethods();
        
        // Count public methods (excluding Object methods)
        long brickMethods = java.util.Arrays.stream(methods)
            .filter(m -> m.getDeclaringClass() == BrickMovementListener.class)
            .count();
        
        assertEquals(8, brickMethods, 
            "BrickMovementListener should have 8 methods (down, left, right, rotate, rotateCCW, hardDrop, softDrop, hold)");
    }

    @Test
    @DisplayName("GameControlListener has only game control methods")
    void testGameControlListenerMethods() {
        // Verify GameControlListener has 4 methods (game control-related)
        java.lang.reflect.Method[] methods = GameControlListener.class.getMethods();
        
        // Count public methods (excluding Object methods)
        long controlMethods = java.util.Arrays.stream(methods)
            .filter(m -> m.getDeclaringClass() == GameControlListener.class)
            .count();
        
        assertEquals(4, controlMethods, 
            "GameControlListener should have 4 methods (pause, resume, newGame, quit)");
    }

    @Test
    @DisplayName("InputEventListener inherits all methods from both interfaces")
    void testInputEventListenerInheritsAllMethods() {
        // Verify InputEventListener has all 12 methods (8 + 4)
        java.lang.reflect.Method[] methods = InputEventListener.class.getMethods();
        
        // Count methods declared in InputEventListener or inherited
        long totalMethods = java.util.Arrays.stream(methods)
            .filter(m -> m.getDeclaringClass() != Object.class)
            .count();
        
        assertTrue(totalMethods >= 12, 
            "InputEventListener should have at least 12 methods (8 from BrickMovementListener + 4 from GameControlListener)");
    }

    // ========== ISP Compliance Tests ==========

    @Test
    @DisplayName("ISP: Clients can implement only BrickMovementListener if needed")
    void testCanImplementOnlyBrickMovementListener() {
        // Create a class that only implements BrickMovementListener
        class BrickOnlyHandler implements BrickMovementListener {
            @Override
            public DownData onDownEvent(MoveEvent event) { return null; }
            @Override
            public ViewData onLeftEvent(MoveEvent event) { return null; }
            @Override
            public ViewData onRightEvent(MoveEvent event) { return null; }
            @Override
            public ViewData onRotateEvent(MoveEvent event) { return null; }
            @Override
            public ViewData onRotateCCWEvent(MoveEvent event) { return null; }
            @Override
            public ViewData onHardDropEvent(MoveEvent event) { return null; }
            @Override
            public ViewData onSoftDropEvent(MoveEvent event) { return null; }
            @Override
            public ViewData onHoldEvent(MoveEvent event) { return null; }
        }
        
        BrickOnlyHandler handler = new BrickOnlyHandler();
        assertTrue(handler instanceof BrickMovementListener,
            "Handler should implement BrickMovementListener");
        assertFalse(handler instanceof GameControlListener,
            "Handler should not implement GameControlListener");
    }

    @Test
    @DisplayName("ISP: Clients can implement only GameControlListener if needed")
    void testCanImplementOnlyGameControlListener() {
        // Create a class that only implements GameControlListener
        class ControlOnlyHandler implements GameControlListener {
            @Override
            public void onPauseEvent(MoveEvent event) {}
            @Override
            public void onResumeEvent(MoveEvent event) {}
            @Override
            public void onNewGameEvent(MoveEvent event) {}
            @Override
            public void onQuitEvent(MoveEvent event) {}
        }
        
        ControlOnlyHandler handler = new ControlOnlyHandler();
        assertTrue(handler instanceof GameControlListener,
            "Handler should implement GameControlListener");
        assertFalse(handler instanceof BrickMovementListener,
            "Handler should not implement BrickMovementListener");
    }

    @Test
    @DisplayName("ISP: Clients can implement both interfaces separately")
    void testCanImplementBothInterfacesSeparately() {
        // Create a class that implements both interfaces separately
        class BothHandler implements BrickMovementListener, GameControlListener {
            @Override
            public DownData onDownEvent(MoveEvent event) { return null; }
            @Override
            public ViewData onLeftEvent(MoveEvent event) { return null; }
            @Override
            public ViewData onRightEvent(MoveEvent event) { return null; }
            @Override
            public ViewData onRotateEvent(MoveEvent event) { return null; }
            @Override
            public ViewData onRotateCCWEvent(MoveEvent event) { return null; }
            @Override
            public ViewData onHardDropEvent(MoveEvent event) { return null; }
            @Override
            public ViewData onSoftDropEvent(MoveEvent event) { return null; }
            @Override
            public ViewData onHoldEvent(MoveEvent event) { return null; }
            @Override
            public void onPauseEvent(MoveEvent event) {}
            @Override
            public void onResumeEvent(MoveEvent event) {}
            @Override
            public void onNewGameEvent(MoveEvent event) {}
            @Override
            public void onQuitEvent(MoveEvent event) {}
        }
        
        BothHandler handler = new BothHandler();
        assertTrue(handler instanceof BrickMovementListener,
            "Handler should implement BrickMovementListener");
        assertTrue(handler instanceof GameControlListener,
            "Handler should implement GameControlListener");
        assertTrue(handler instanceof InputEventListener,
            "Handler should also be assignable to InputEventListener");
    }
}

