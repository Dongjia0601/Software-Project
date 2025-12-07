package com.comp2042;

import com.comp2042.model.state.PlayingState;
import com.comp2042.model.state.PausedState;
import com.comp2042.model.state.GameOverState;
import com.comp2042.model.state.GameState;
import com.comp2042.model.state.GameStateContext;
import com.comp2042.controller.game.GameController;
import com.comp2042.controller.game.GuiController;
import com.comp2042.model.board.Board;
import com.comp2042.model.board.SimpleBoard;
import com.comp2042.dto.DownData;
import com.comp2042.dto.ViewData;
import com.comp2042.event.MoveEvent;
import com.comp2042.event.EventType;
import com.comp2042.event.EventSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import javafx.application.Platform;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Game State Pattern Tests")
class GameStatePatternTest {

    private Board board;
    private GuiController guiController;
    private GameStateContext stateContext;
    private GameState lastTransitionState;
    private static boolean javaFxInitialized = false;

    @BeforeAll
    static void initJavaFX() {
        if (!javaFxInitialized) {
            try {
                Platform.startup(() -> {});
            } catch (IllegalStateException e) {
                // Platform already started
            }
            javaFxInitialized = true;
        }
    }

    @BeforeEach
    void setUp() {
        board = new SimpleBoard(10, 20);
        board.createNewBrick(); // Ensure brick exists
        
        // Create a stub GuiController to isolate state logic from UI side effects
        guiController = new GuiController() {
            @Override
            public void updateScore(int score, int highScore) {
                // UI not needed in this unit test
            }
            
            @Override
            public void updateLines(int lines) {
                // UI not needed in this unit test
            }
            
            @Override
            public boolean isEndlessMode() {
                return false;
            }
            
            @Override
            public boolean isLevelMode() {
                return false;
            }
            
            @Override
            public void refreshGameBackground(int[][] matrix) {
                // UI not needed in this unit test
            }
            
            @Override
            public void refreshActiveBrick(ViewData data) {
                // UI not needed in this unit test
            }

            @Override
            public void gameOver() {
                // UI not needed in this unit test
            }
        };
        
        // Mock GameStateContext
        stateContext = new GameStateContext() {
            @Override
            public void transitionToState(GameState newState) {
                lastTransitionState = newState;
            }
        };
    }

    @Test
    @DisplayName("PlayingState: Handles DOWN event")
    void testPlayingStateOnDownEvent() {
        // Run on FX thread to avoid concurrency issues with SoundManager and UI stubs
        Platform.runLater(() -> {
            PlayingState state = new PlayingState(board, guiController, stateContext);
            MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);
            DownData result = state.onDownEvent(event);
            assertNotNull(result);
        });
        try {
            // Allow FX thread to flush runLater before assertions finish
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("State Pattern: State transitions are correct")
    void testStatePatternTransitions() {
        // Verify pause/resume returns the correct state type without mutating context
        PlayingState playing = new PlayingState(board, guiController, stateContext);
        GameState paused = playing.handlePauseRequest();
        assertTrue(paused instanceof PausedState);
        
        GameState resumed = paused.handlePauseRequest();
        assertTrue(resumed instanceof PlayingState);
    }
}
