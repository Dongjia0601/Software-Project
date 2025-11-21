package com.comp2042;

import com.comp2042.controller.factory.GameMode;
import com.comp2042.controller.factory.GameModeFactory;
import com.comp2042.controller.factory.GameModeType;
import com.comp2042.service.gameloop.GameService;
import com.comp2042.service.gameloop.GameServiceImpl;
import com.comp2042.model.board.Board;
import com.comp2042.model.board.SimpleBoard;
import com.comp2042.controller.game.GuiController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class GameModeFactoryTest {

    private GameService gameService;
    private GuiController guiController;

    @BeforeEach
    void setUp() {
        Board board = new SimpleBoard(10, 20);
        gameService = new GameServiceImpl(board);
        // GuiController requires JavaFX, so we might pass null if factory checks allow it,
        // or verify if it throws. Assuming factory passes it through.
        guiController = null; 
    }

    @Test
    void testCreateEndlessMode() {
        GameMode endlessMode = GameModeFactory.createGameMode(
            GameModeType.ENDLESS, gameService, guiController);
        assertNotNull(endlessMode);
        assertEquals(GameModeType.ENDLESS, endlessMode.getType());
    }

    @Test
    void testCreateLevelMode() {
        GameMode levelMode = GameModeFactory.createGameMode(
            GameModeType.LEVEL, gameService, guiController);
        assertNotNull(levelMode);
        assertEquals(GameModeType.LEVEL, levelMode.getType());
    }

    @Test
    void testCreateTwoPlayerVSMode() {
        GameMode twoPlayerVSMode = GameModeFactory.createGameMode(
            GameModeType.TWO_PLAYER_VS, gameService, guiController);
        assertNotNull(twoPlayerVSMode);
        assertEquals(GameModeType.TWO_PLAYER_VS, twoPlayerVSMode.getType());
    }
}
