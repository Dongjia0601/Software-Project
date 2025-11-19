package com.comp2042;

import com.comp2042.service.gameloop.GameService;
import com.comp2042.service.gameloop.GameServiceImpl;
import com.comp2042.model.board.Board;
import com.comp2042.model.board.SimpleBoard;
import com.comp2042.model.score.Score;
import com.comp2042.model.brick.Brick;
import com.comp2042.dto.ClearRow;
import com.comp2042.dto.ViewData;
import com.comp2042.model.savestate.GameStateMemento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Dependency Inversion Principle Tests")
class DependencyInversionTest {

    @Test
    @DisplayName("DIP: GameServiceImpl uses dependency injection")
    void testDependencyInjection() {
        Board board = new SimpleBoard(10, 20);
        GameService service = new GameServiceImpl(board);
        assertSame(board, service.getBoard());
    }

    @Test
    @DisplayName("DIP: GameServiceImpl depends on Board interface, not SimpleBoard")
    void testDependsOnInterface() {
        class MockBoard implements Board {
            @Override public boolean moveBrickDown() { return false; }
            @Override public int hardDropBrick() { return 0; }
            @Override public boolean moveBrickLeft() { return false; }
            @Override public boolean moveBrickRight() { return false; }
            @Override public boolean rotateLeftBrick() { return false; }
            @Override public boolean rotateRightBrick() { return false; }
            @Override public boolean createNewBrick() { return false; }
            @Override public int[][] getBoardMatrix() { return new int[20][10]; }
            @Override public ViewData getViewData() { return null; }
            @Override public void mergeBrickToBackground() {}
            @Override public ClearRow clearRows() { return null; }
            @Override public Score getScore() { return new Score(); }
            @Override public int getTotalLinesCleared() { return 0; }
            @Override public void newGame() {}
            @Override public boolean holdBrick() { return false; }
            @Override public Brick getHeldBrick() { return null; }
            @Override public int getGhostBrickY() { return -1; }
            @Override public boolean addGarbageLine() { return false; }
            @Override public int removeGarbageLines(int lines) { return 0; }
        }
        
        Board mockBoard = new MockBoard();
        GameService service = new GameServiceImpl(mockBoard);
        assertSame(mockBoard, service.getBoard());
    }
}
