package com.comp2042;

import com.comp2042.service.gameloop.GameService;
import com.comp2042.service.gameloop.GameServiceImpl;
import com.comp2042.model.board.Board;
import com.comp2042.model.board.SimpleBoard;
import com.comp2042.model.score.Score;
import com.comp2042.dto.DownData;
import com.comp2042.dto.ViewData;
import com.comp2042.event.MoveEvent;
import com.comp2042.event.EventType;
import com.comp2042.event.EventSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GameServiceImpl Tests")
class GameServiceImplTest {

    private GameService gameService;
    private Board board;

    @BeforeEach
    void setUp() {
        board = new SimpleBoard(10, 20);
        gameService = new GameServiceImpl(board);
    }

    @Test
    @DisplayName("Constructor: createDefault factory method creates GameServiceImpl with default board")
    void testCreateDefaultFactoryMethod() {
        GameService service = GameServiceImpl.createDefault();
        assertNotNull(service.getBoard());
        assertNotNull(service.getScore());
        assertTrue(service.getBoard() instanceof SimpleBoard);
    }
    
    @Test
    @DisplayName("Constructor: Throws exception for null board")
    void testConstructorWithNullBoard() {
        assertThrows(IllegalArgumentException.class, () -> {
            new GameServiceImpl(null);
        });
    }

    @Test
    @DisplayName("getBoard: Returns the board instance")
    void testGetBoard() {
        assertEquals(board, gameService.getBoard());
    }

    @Test
    @DisplayName("getScore: Returns the score from board")
    void testGetScore() {
        Score score = gameService.getScore();
        assertNotNull(score);
        assertEquals(0, score.getScore());
    }

    @Test
    @DisplayName("processDownEvent: Handles normal DOWN event")
    void testProcessDownEventNormal() {
        board.createNewBrick();
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);
        DownData result = gameService.processDownEvent(event);
        assertNotNull(result);
        assertNotNull(result.getViewData());
    }

    @Test
    @DisplayName("processDownEvent: Handles HARD_DROP event")
    void testProcessDownEventHardDrop() {
        board.createNewBrick();
        MoveEvent event = new MoveEvent(EventType.HARD_DROP, EventSource.USER);
        DownData result = gameService.processDownEvent(event);
        assertNotNull(result);
        assertTrue(result.isBrickLanded());
    }

    @Test
    @DisplayName("processLeftEvent: Handles LEFT event successfully")
    void testProcessLeftEvent() {
        board.createNewBrick();
        MoveEvent event = new MoveEvent(EventType.LEFT, EventSource.USER);
        ViewData result = gameService.processLeftEvent(event);
        assertNotNull(gameService.getBoard().getViewData());
    }

    @Test
    @DisplayName("startNewGame: Resets game state")
    void testStartNewGame() {
        board.createNewBrick();
        board.moveBrickDown();
        gameService.getScore().add(100);
        
        gameService.startNewGame();
        
        assertEquals(0, gameService.getScore().getScore());
        assertFalse(gameService.isGameOver());
    }

    @Test
    @DisplayName("isGameOver: Returns false initially")
    void testIsGameOverInitially() {
        assertFalse(gameService.isGameOver());
    }
}

