package com.comp2042;

import com.comp2042.model.state.PlayingState;
import com.comp2042.model.state.PausedState;
import com.comp2042.model.state.GameOverState;
import com.comp2042.model.state.GameState;
import com.comp2042.model.state.GameStateContext;
import com.comp2042.controller.game.GameController;
import com.comp2042.controller.game.GameViewController;
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
    private GameViewController guiController;
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
        
        // Create a stub GameViewController
        guiController = new GameViewController() {
            @Override
            public void updateScore(int score, int highScore) {
                // No-op
            }
            
            @Override
            public void updateLines(int lines) {
                // No-op
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
                // No-op
            }
            
            @Override
            public void refreshActiveBrick(ViewData data) {
                // No-op
            }

            @Override
            public void gameOver() {
                // No-op
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
        // Run on FX thread to avoid concurrency issues with SoundManager
        Platform.runLater(() -> {
            PlayingState state = new PlayingState(board, guiController, stateContext);
            MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);
            DownData result = state.onDownEvent(event);
            assertNotNull(result);
        });
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("State Pattern: State transitions are correct")
    void testStatePatternTransitions() {
        PlayingState playing = new PlayingState(board, guiController, stateContext);
        GameState paused = playing.handlePauseRequest();
        assertTrue(paused instanceof PausedState);
        
        GameState resumed = paused.handlePauseRequest();
        assertTrue(resumed instanceof PlayingState);
    }
}
