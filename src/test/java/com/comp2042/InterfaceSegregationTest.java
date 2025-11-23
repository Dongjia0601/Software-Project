package com.comp2042;

import com.comp2042.event.listener.GameControlListener;
import com.comp2042.event.listener.InputEventListener;
import com.comp2042.event.listener.BrickMovementListener;
import com.comp2042.controller.game.GameController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Interface Segregation Principle Tests")
class InterfaceSegregationTest {

    @Test
    @DisplayName("InputEventListener extends both BrickMovementListener and GameControlListener")
    void testInputEventListenerExtendsBoth() {
        assertTrue(BrickMovementListener.class.isAssignableFrom(InputEventListener.class));
        assertTrue(GameControlListener.class.isAssignableFrom(InputEventListener.class));
    }

    @Test
    @DisplayName("GameController implements InputEventListener")
    void testGameControllerImplementsInputEventListener() {
        assertTrue(InputEventListener.class.isAssignableFrom(GameController.class));
    }

    @Test
    @DisplayName("BrickMovementListener has only brick movement methods")
    void testBrickMovementListenerMethods() {
        java.lang.reflect.Method[] methods = BrickMovementListener.class.getMethods();
        long brickMethods = java.util.Arrays.stream(methods)
            .filter(m -> m.getDeclaringClass() == BrickMovementListener.class)
            .count();
        assertEquals(8, brickMethods);
    }
}
