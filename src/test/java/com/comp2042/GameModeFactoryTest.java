package com.comp2042;

import com.comp2042.gameplay.GameMode;
import com.comp2042.gameplay.GameModeFactory;
import com.comp2042.gameplay.GameModeType;
import com.comp2042.core.GameService;
import com.comp2042.core.GameServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GameModeFactory class.
 * Tests the factory pattern implementation for creating different game modes.
 */
class GameModeFactoryTest {

    private GameService gameService;
    private GuiController guiController;

    @BeforeEach
    void setUp() {
        Board board = new SimpleBoard(10, 20);
        gameService = new GameServiceImpl(board);
        guiController = new GuiController();
    }

    @Test
    void testCreateEndlessMode() {
        // Test creating endless mode
        GameMode endlessMode = GameModeFactory.createGameMode(
            GameModeType.ENDLESS, gameService, guiController);
        
        assertNotNull(endlessMode);
        assertEquals(GameModeType.ENDLESS, endlessMode.getType());
    }

    @Test
    void testCreateLevelMode() {
        // Test creating level mode
        GameMode levelMode = GameModeFactory.createGameMode(
            GameModeType.LEVEL, gameService, guiController);
        
        assertNotNull(levelMode);
        assertEquals(GameModeType.LEVEL, levelMode.getType());
    }

    @Test
    void testCreateTwoPlayerVSMode() {
        // Test creating two player VS mode
        GameMode twoPlayerVSMode = GameModeFactory.createGameMode(
            GameModeType.TWO_PLAYER_VS, gameService, guiController);
        
        assertNotNull(twoPlayerVSMode);
        assertEquals(GameModeType.TWO_PLAYER_VS, twoPlayerVSMode.getType());
    }

    @Test
    void testGameModeFactoryNotNull() {
        // Test that factory methods return non-null objects
        assertNotNull(GameModeFactory.createGameMode(
            GameModeType.ENDLESS, gameService, guiController));
        assertNotNull(GameModeFactory.createGameMode(
            GameModeType.LEVEL, gameService, guiController));
    }

    @Test
    void testAllGameModeTypesSupported() {
        // Test that all game mode types are supported
        for (GameModeType type : GameModeType.values()) {
            GameMode mode = GameModeFactory.createGameMode(type, gameService, guiController);
            assertNotNull(mode, "GameMode should not be null for type: " + type);
            assertEquals(type, mode.getType(), "GameMode type should match requested type");
        }
    }
}
