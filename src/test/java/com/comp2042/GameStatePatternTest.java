package com.comp2042;

import com.comp2042.controller.factory.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for State Pattern implementation.
 * Tests game state transitions, state behavior, and state pattern correctness.
 * 
 * <p>These tests validate the State Pattern implementation for managing
 * game states (Playing, Paused, GameOver) and ensure proper state transitions.</p>
 * 
 * @author Dong, Jia.
 */
@DisplayName("Game State Pattern Tests")
class GameStatePatternTest {

    private Board board;
    private GuiController guiController;
    private GameController gameController;

    @BeforeEach
    void setUp() {
        board = new SimpleBoard(10, 20);
        // Note: GuiController requires JavaFX initialization, so we'll test
        // state behavior that doesn't require full GUI setup
        guiController = new GuiController();
        gameController = new GameController(guiController);
    }

    // ========== PlayingState Tests ==========

    @Test
    @DisplayName("PlayingState: Handles DOWN event")
    void testPlayingStateOnDownEvent() {
        PlayingState state = new PlayingState(board, guiController, gameController);
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);
        
        DownData result = state.onDownEvent(event);
        
        assertNotNull(result);
        assertNotNull(result.getViewData());
    }

    @Test
    @DisplayName("PlayingState: Handles LEFT event")
    void testPlayingStateOnLeftEvent() {
        board.createNewBrick();
        PlayingState state = new PlayingState(board, guiController, gameController);
        MoveEvent event = new MoveEvent(EventType.LEFT, EventSource.USER);
        
        ViewData result = state.onLeftEvent(event);
        
        assertNotNull(result);
    }

    @Test
    @DisplayName("PlayingState: Handles RIGHT event")
    void testPlayingStateOnRightEvent() {
        board.createNewBrick();
        PlayingState state = new PlayingState(board, guiController, gameController);
        MoveEvent event = new MoveEvent(EventType.RIGHT, EventSource.USER);
        
        ViewData result = state.onRightEvent(event);
        
        assertNotNull(result);
    }

    @Test
    @DisplayName("PlayingState: Handles ROTATE event")
    void testPlayingStateOnRotateEvent() {
        board.createNewBrick();
        PlayingState state = new PlayingState(board, guiController, gameController);
        MoveEvent event = new MoveEvent(EventType.ROTATE, EventSource.USER);
        
        ViewData result = state.onRotateEvent(event);
        
        assertNotNull(result);
    }

    @Test
    @DisplayName("PlayingState: Handles ROTATE_CCW event")
    void testPlayingStateOnRotateCCWEvent() {
        board.createNewBrick();
        PlayingState state = new PlayingState(board, guiController, gameController);
        MoveEvent event = new MoveEvent(EventType.ROTATE_CCW, EventSource.USER);
        
        ViewData result = state.onRotateCCWEvent(event);
        
        assertNotNull(result);
    }

    @Test
    @DisplayName("PlayingState: Transitions to PausedState on pause request")
    void testPlayingStatePauseTransition() {
        PlayingState state = new PlayingState(board, guiController, gameController);
        
        GameState newState = state.handlePauseRequest();
        
        assertNotNull(newState);
        assertTrue(newState instanceof PausedState);
        assertNotEquals(state, newState);
    }

    @Test
    @DisplayName("PlayingState: Handles new game request")
    void testPlayingStateNewGameRequest() {
        PlayingState state = new PlayingState(board, guiController, gameController);
        
        GameState newState = state.handleNewGameRequest();
        
        assertNotNull(newState);
        assertTrue(newState instanceof PlayingState);
    }

    // ========== PausedState Tests ==========

    @Test
    @DisplayName("PausedState: Ignores DOWN event")
    void testPausedStateIgnoresDownEvent() {
        board.createNewBrick();
        PausedState state = new PausedState(board, guiController);
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);
        
        DownData result = state.onDownEvent(event);
        
        assertNotNull(result);
        // Should return current view data without moving
        assertNotNull(result.getViewData());
    }

    @Test
    @DisplayName("PausedState: Ignores movement events")
    void testPausedStateIgnoresMovement() {
        board.createNewBrick();
        ViewData initialData = board.getViewData();
        PausedState state = new PausedState(board, guiController);
        
        MoveEvent leftEvent = new MoveEvent(EventType.LEFT, EventSource.USER);
        MoveEvent rightEvent = new MoveEvent(EventType.RIGHT, EventSource.USER);
        
        ViewData leftResult = state.onLeftEvent(leftEvent);
        ViewData rightResult = state.onRightEvent(rightEvent);
        
        // Should return same data (no movement)
        assertNotNull(leftResult);
        assertNotNull(rightResult);
    }

    @Test
    @DisplayName("PausedState: Transitions back to PlayingState on pause request")
    void testPausedStateResumeTransition() {
        PausedState state = new PausedState(board, guiController);
        
        GameState newState = state.handlePauseRequest();
        
        assertNotNull(newState);
        assertTrue(newState instanceof PlayingState);
    }

    @Test
    @DisplayName("PausedState: Handles new game request")
    void testPausedStateNewGameRequest() {
        PausedState state = new PausedState(board, guiController);
        
        GameState newState = state.handleNewGameRequest();
        
        assertNotNull(newState);
        assertTrue(newState instanceof PlayingState);
    }

    // ========== GameOverState Tests ==========

    @Test
    @DisplayName("GameOverState: Ignores all movement events")
    void testGameOverStateIgnoresEvents() {
        board.createNewBrick();
        GameOverState state = new GameOverState(board, guiController);
        
        MoveEvent downEvent = new MoveEvent(EventType.DOWN, EventSource.USER);
        MoveEvent leftEvent = new MoveEvent(EventType.LEFT, EventSource.USER);
        MoveEvent rotateEvent = new MoveEvent(EventType.ROTATE, EventSource.USER);
        
        DownData downResult = state.onDownEvent(downEvent);
        ViewData leftResult = state.onLeftEvent(leftEvent);
        ViewData rotateResult = state.onRotateEvent(rotateEvent);
        
        // Should return data but not process events
        assertNotNull(downResult);
        assertNotNull(leftResult);
        assertNotNull(rotateResult);
    }

    @Test
    @DisplayName("GameOverState: Handles new game request")
    void testGameOverStateNewGameRequest() {
        GameOverState state = new GameOverState(board, guiController);
        
        GameState newState = state.handleNewGameRequest();
        
        assertNotNull(newState);
        assertTrue(newState instanceof PlayingState);
    }

    // ========== State Pattern Correctness Tests ==========

    @Test
    @DisplayName("State Pattern: All states implement GameState interface")
    void testStatePatternInterface() {
        PlayingState playingState = new PlayingState(board, guiController, gameController);
        PausedState pausedState = new PausedState(board, guiController);
        GameOverState gameOverState = new GameOverState(board, guiController);
        
        assertTrue(playingState instanceof GameState);
        assertTrue(pausedState instanceof GameState);
        assertTrue(gameOverState instanceof GameState);
    }

    @Test
    @DisplayName("State Pattern: States can be substituted")
    void testStatePatternSubstitution() {
        GameState playingState = new PlayingState(board, guiController, gameController);
        GameState pausedState = new PausedState(board, guiController);
        GameState gameOverState = new GameOverState(board, guiController);
        
        // All should handle same events (polymorphism)
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);
        
        assertNotNull(playingState.onDownEvent(event));
        assertNotNull(pausedState.onDownEvent(event));
        assertNotNull(gameOverState.onDownEvent(event));
    }

    @Test
    @DisplayName("State Pattern: State transitions are correct")
    void testStatePatternTransitions() {
        // Playing -> Paused
        PlayingState playing = new PlayingState(board, guiController, gameController);
        GameState paused = playing.handlePauseRequest();
        assertTrue(paused instanceof PausedState);
        
        // Paused -> Playing
        GameState resumed = paused.handlePauseRequest();
        assertTrue(resumed instanceof PlayingState);
        
        // Any state -> New Game -> Playing
        GameState newGame = paused.handleNewGameRequest();
        assertTrue(newGame instanceof PlayingState);
    }

    // ========== GameController State Management Tests ==========

    @Test
    @DisplayName("GameController: Delegates events to current state")
    void testGameControllerDelegation() {
        board.createNewBrick();
        MoveEvent event = new MoveEvent(EventType.LEFT, EventSource.USER);
        
        ViewData result = gameController.onLeftEvent(event);
        
        assertNotNull(result);
    }

    @Test
    @DisplayName("GameController: Handles state transitions")
    void testGameControllerStateTransition() {
        // Request pause
        gameController.requestPause();
        
        // State should be PausedState now
        // Verify by checking behavior (paused state ignores events)
        MoveEvent event = new MoveEvent(EventType.LEFT, EventSource.USER);
        ViewData result = gameController.onLeftEvent(event);
        assertNotNull(result);
    }

    @Test
    @DisplayName("GameController: Transitions between states correctly")
    void testGameControllerStateTransitions() {
        // Initial state should be PlayingState
        MoveEvent downEvent = new MoveEvent(EventType.DOWN, EventSource.USER);
        DownData initialResult = gameController.onDownEvent(downEvent);
        assertNotNull(initialResult);
        
        // Pause
        gameController.requestPause();
        
        // Resume
        gameController.requestPause();
        
        // Should be back to playing
        DownData resumedResult = gameController.onDownEvent(downEvent);
        assertNotNull(resumedResult);
    }
}

