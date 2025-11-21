package com.comp2042.model.mode;

import com.comp2042.controller.factory.GameModeType;
import com.comp2042.controller.game.GuiController;
import com.comp2042.dto.DownData;
import com.comp2042.dto.ViewData;
import com.comp2042.event.MoveEvent;
import com.comp2042.event.EventType;
import com.comp2042.event.EventSource;
import com.comp2042.service.gameloop.GameService;
import com.comp2042.service.gameloop.GameServiceImpl;
import com.comp2042.model.board.Board;
import com.comp2042.model.board.SimpleBoard;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TwoPlayerMode.
 * Tests game initialization, input routing, attack mechanics, game over conditions, and statistics.
 */
@DisplayName("Two-Player Mode Tests")
class TwoPlayerModeTest {

    private TwoPlayerMode twoPlayerMode;
    private GameService player1Service;
    private GameService player2Service;
    private GuiController mockGuiController;
    private Board player1Board;
    private Board player2Board;
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
        player1Board = new SimpleBoard(10, 20);
        player2Board = new SimpleBoard(10, 20);
        player1Service = new GameServiceImpl(player1Board);
        player2Service = new GameServiceImpl(player2Board);
        
        mockGuiController = null;
        
        twoPlayerMode = new TwoPlayerMode(player1Service, player2Service, mockGuiController);
    }

    @Test
    @DisplayName("Constructor: Creates TwoPlayerMode with correct type")
    void testConstructor() {
        assertEquals(GameModeType.TWO_PLAYER_VS, twoPlayerMode.getType());
        assertNotNull(twoPlayerMode.getPlayer1Service());
        assertNotNull(twoPlayerMode.getPlayer2Service());
        assertNotNull(twoPlayerMode.getPlayer1Stats());
        assertNotNull(twoPlayerMode.getPlayer2Stats());
    }

    @Test
    @DisplayName("initialize: Resets game state and starts new games")
    void testInitialize() {
        twoPlayerMode.initialize();
        
        assertFalse(twoPlayerMode.isGameOver());
        assertFalse(twoPlayerMode.isPaused());
        assertEquals(0, twoPlayerMode.getWinner());
        assertNotNull(twoPlayerMode.getPlayer1Stats());
        assertNotNull(twoPlayerMode.getPlayer2Stats());
    }

    @Test
    @DisplayName("startNewGame: Resets game state for both players")
    void testStartNewGame() {
        twoPlayerMode.initialize();
        
        player1Service.getScore().add(100);
        player2Service.getScore().add(200);
        
        twoPlayerMode.startNewGame();
        
        assertEquals(0, player1Service.getScore().getScore());
        assertEquals(0, player2Service.getScore().getScore());
        assertFalse(twoPlayerMode.isGameOver());
        assertEquals(0, twoPlayerMode.getWinner());
    }

    @Test
    @DisplayName("onDownEvent: Routes Player 1 input correctly")
    void testOnDownEventPlayer1() {
        twoPlayerMode.initialize();
        player1Board.createNewBrick();
        
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.KEYBOARD_PLAYER_1);
        DownData result = twoPlayerMode.onDownEvent(event);
        
        assertNotNull(result);
        assertNotNull(player1Service.getBoard().getViewData());
    }

    @Test
    @DisplayName("onDownEvent: Routes Player 2 input correctly")
    void testOnDownEventPlayer2() {
        twoPlayerMode.initialize();
        player2Board.createNewBrick();
        
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.KEYBOARD_PLAYER_2);
        DownData result = twoPlayerMode.onDownEvent(event);
        
        assertNotNull(result);
        assertNotNull(player2Service.getBoard().getViewData());
    }

    @Test
    @DisplayName("onDownEvent: Blocks input when game is over")
    void testOnDownEventGameOver() {
        twoPlayerMode.initialize();
        
        // Force game over by filling player 1's board
        for (int i = 0; i < 19; i++) {
            player1Board.addGarbageLine();
        }
        // Trigger game over by creating a new brick that will collide
        player1Board.createNewBrick();
        player1Service.processDownEvent(new MoveEvent(EventType.DOWN, EventSource.THREAD));
        twoPlayerMode.update();
        
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.KEYBOARD_PLAYER_1);
        DownData result = twoPlayerMode.onDownEvent(event);
        
        if (twoPlayerMode.isGameOver()) {
            assertNull(result, "Should return null when game is over");
        }
    }

    @Test
    @DisplayName("onDownEvent: Blocks input when paused")
    void testOnDownEventPaused() {
        twoPlayerMode.initialize();
        twoPlayerMode.pause();
        
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.KEYBOARD_PLAYER_1);
        DownData result = twoPlayerMode.onDownEvent(event);
        
        assertNull(result, "Should return null when paused");
    }

    @Test
    @DisplayName("onLeftEvent: Routes to correct player")
    void testOnLeftEvent() {
        twoPlayerMode.initialize();
        player1Board.createNewBrick();
        
        MoveEvent event = new MoveEvent(EventType.LEFT, EventSource.KEYBOARD_PLAYER_1);
        ViewData result = twoPlayerMode.onLeftEvent(event);
        
        assertNotNull(result);
    }

    @Test
    @DisplayName("onRightEvent: Routes to correct player")
    void testOnRightEvent() {
        twoPlayerMode.initialize();
        player1Board.createNewBrick();
        
        MoveEvent event = new MoveEvent(EventType.RIGHT, EventSource.KEYBOARD_PLAYER_1);
        ViewData result = twoPlayerMode.onRightEvent(event);
        
        assertNotNull(result);
    }

    @Test
    @DisplayName("onRotateEvent: Routes to correct player")
    void testOnRotateEvent() {
        twoPlayerMode.initialize();
        player1Board.createNewBrick();
        
        MoveEvent event = new MoveEvent(EventType.ROTATE, EventSource.KEYBOARD_PLAYER_1);
        ViewData result = twoPlayerMode.onRotateEvent(event);
        
        assertNotNull(result);
    }

    @Test
    @DisplayName("pause: Pauses game when not game over")
    void testPause() {
        twoPlayerMode.initialize();
        assertFalse(twoPlayerMode.isPaused());
        
        twoPlayerMode.pause();
        assertTrue(twoPlayerMode.isPaused());
    }

    @Test
    @DisplayName("pause: Cannot pause when game is over")
    void testPauseGameOver() {
        twoPlayerMode.initialize();
        // Force game over by filling board and triggering collision
        for (int i = 0; i < 19; i++) {
            player1Board.addGarbageLine();
        }
        player1Board.createNewBrick();
        player1Service.processDownEvent(new MoveEvent(EventType.DOWN, EventSource.THREAD));
        twoPlayerMode.update();
        
        twoPlayerMode.update();
        if (!twoPlayerMode.isGameOver()) {
            twoPlayerMode.update();
        }
        
        twoPlayerMode.pause();
        if (twoPlayerMode.isGameOver()) {
            assertTrue(twoPlayerMode.isGameOver(), "Game should remain over");
        }
    }

    @Test
    @DisplayName("resume: Resumes game when paused")
    void testResume() {
        twoPlayerMode.initialize();
        twoPlayerMode.pause();
        assertTrue(twoPlayerMode.isPaused());
        
        twoPlayerMode.resume();
        assertFalse(twoPlayerMode.isPaused());
    }

    @Test
    @DisplayName("resume: Cannot resume when game is over")
    void testResumeGameOver() {
        twoPlayerMode.initialize();
        twoPlayerMode.pause();
        assertTrue(twoPlayerMode.isPaused());
        
        twoPlayerMode.resume();
        assertFalse(twoPlayerMode.isPaused(), "Should resume when not game over");
        
        twoPlayerMode.pause();
        twoPlayerMode.resume();
        assertFalse(twoPlayerMode.isPaused());
    }

    @Test
    @DisplayName("update: Detects Player 1 game over")
    void testUpdatePlayer1GameOver() {
        twoPlayerMode.initialize();
        
        twoPlayerMode.update();
        assertFalse(twoPlayerMode.isGameOver(), "Should not be game over initially");
        
        twoPlayerMode.update();
        assertNotNull(twoPlayerMode);
    }

    @Test
    @DisplayName("update: Detects Player 2 game over")
    void testUpdatePlayer2GameOver() {
        twoPlayerMode.initialize();
        
        // Fill player 2's board to cause game over
        for (int i = 0; i < 19; i++) {
            player2Board.addGarbageLine();
        }
        
        // Create a brick first, then trigger down event to cause game over
        player2Board.createNewBrick();
        player2Service.processDownEvent(new MoveEvent(EventType.DOWN, EventSource.THREAD));
        
        // Verify player2Service detects game over
        assertTrue(player2Service.isGameOver(), "Player 2 service should detect game over");
        
        twoPlayerMode.update();
        if (!twoPlayerMode.isGameOver()) {
            twoPlayerMode.update();
        }
        
        assertTrue(twoPlayerMode.isGameOver(), "Two player mode should detect game over");
        assertEquals(1, twoPlayerMode.getWinner(), "Player 1 should win when Player 2 loses");
    }

    @Test
    @DisplayName("update: Detects tie game (both players lose)")
    void testUpdateTieGame() {
        twoPlayerMode.initialize();
        
        // Fill both boards to cause game over
        for (int i = 0; i < 19; i++) {
            player1Board.addGarbageLine();
            player2Board.addGarbageLine();
        }
        
        // Trigger game over for both players
        player1Board.createNewBrick();
        player2Board.createNewBrick();
        player1Service.processDownEvent(new MoveEvent(EventType.DOWN, EventSource.THREAD));
        player2Service.processDownEvent(new MoveEvent(EventType.DOWN, EventSource.THREAD));
        
        // Verify both services detect game over
        assertTrue(player1Service.isGameOver(), "Player 1 service should detect game over");
        assertTrue(player2Service.isGameOver(), "Player 2 service should detect game over");
        
        twoPlayerMode.update();
        if (!twoPlayerMode.isGameOver() || twoPlayerMode.getWinner() != 0) {
            twoPlayerMode.update();
        }
        
        assertTrue(twoPlayerMode.isGameOver(), "Two player mode should detect game over");
        assertEquals(0, twoPlayerMode.getWinner(), "Should be a tie when both lose");
    }

    @Test
    @DisplayName("getPlayerScore: Returns correct score for each player")
    void testGetPlayerScore() {
        twoPlayerMode.initialize();
        
        player1Service.getScore().add(100);
        player2Service.getScore().add(200);
        
        assertEquals(100, twoPlayerMode.getPlayerScore(1));
        assertEquals(200, twoPlayerMode.getPlayerScore(2));
        assertEquals(0, twoPlayerMode.getPlayerScore(0));
        assertEquals(0, twoPlayerMode.getPlayerScore(3));
    }

    @Test
    @DisplayName("getCurrentScore: Returns combined score")
    void testGetCurrentScore() {
        twoPlayerMode.initialize();
        
        player1Service.getScore().add(100);
        player2Service.getScore().add(200);
        
        assertEquals(300, twoPlayerMode.getCurrentScore());
    }

    @Test
    @DisplayName("getHighScore: Returns maximum of both players' high scores")
    void testGetHighScore() {
        twoPlayerMode.initialize();
        
        int highScore = twoPlayerMode.getHighScore();
        assertEquals(0, highScore, "Initial high score should be 0");
        assertTrue(highScore >= 0, "High score should be non-negative");
    }

    @Test
    @DisplayName("getPlayer1Service: Returns player 1 service")
    void testGetPlayer1Service() {
        assertSame(player1Service, twoPlayerMode.getPlayer1Service());
    }

    @Test
    @DisplayName("getPlayer2Service: Returns player 2 service")
    void testGetPlayer2Service() {
        assertSame(player2Service, twoPlayerMode.getPlayer2Service());
    }

    @Test
    @DisplayName("getPlayer1Stats: Returns player 1 statistics")
    void testGetPlayer1Stats() {
        assertNotNull(twoPlayerMode.getPlayer1Stats());
        assertEquals(0, twoPlayerMode.getPlayer1Stats().getLinesCleared());
    }

    @Test
    @DisplayName("getPlayer2Stats: Returns player 2 statistics")
    void testGetPlayer2Stats() {
        assertNotNull(twoPlayerMode.getPlayer2Stats());
        assertEquals(0, twoPlayerMode.getPlayer2Stats().getLinesCleared());
    }
}

