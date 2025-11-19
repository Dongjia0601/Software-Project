package com.comp2042;

import com.comp2042.model.savestate.GameStateMemento;
import com.comp2042.model.savestate.GameStateCaretaker;
import com.comp2042.model.board.SimpleBoard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Memento Pattern Tests")
class MementoPatternTest {

    private SimpleBoard board;
    private GameStateCaretaker caretaker;

    @BeforeEach
    void setUp() {
        board = new SimpleBoard(10, 20);
        caretaker = new GameStateCaretaker();
    }

    @Test
    @DisplayName("SimpleBoard: createMemento returns valid memento")
    void testCreateMemento() {
        board.newGame();
        GameStateMemento memento = board.createMemento();
        assertNotNull(memento);
        assertEquals(10, memento.getBoardWidth());
    }

    @Test
    @DisplayName("SimpleBoard: Save and restore preserves score")
    void testSaveAndRestoreScore() {
        board.newGame();
        board.getScore().add(500);
        int savedScore = board.getScore().getScore();
        
        GameStateMemento memento = board.createMemento();
        board.getScore().add(200);
        
        board.restoreFromMemento(memento);
        assertEquals(savedScore, board.getScore().getScore());
    }

    @Test
    @DisplayName("GameStateCaretaker: Saves memento to history")
    void testCaretakerSaveMemento() {
        board.newGame();
        GameStateMemento memento = board.createMemento();
        caretaker.saveMemento(memento);
        assertTrue(caretaker.hasMemento());
    }
}

