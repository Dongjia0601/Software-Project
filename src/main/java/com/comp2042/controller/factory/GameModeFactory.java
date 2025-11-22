package com.comp2042.controller.factory;

import com.comp2042.service.gameloop.GameService;
import com.comp2042.controller.game.GuiController;
import com.comp2042.model.mode.EndlessMode;
import com.comp2042.model.mode.LevelManager;
import com.comp2042.model.mode.TwoPlayerMode;

/**
 * Factory class for instantiating game mode implementations.
 * Implements the Factory pattern to encapsulate the creation logic and provide
 * a centralized point for game mode instantiation based on the requested type.
 * 
 * <p>This factory handles the creation of three game mode types:
 * <ul>
 *   <li>ENDLESS: Creates EndlessMode</li>
 *   <li>LEVEL: Creates LevelGameModeImpl with level configuration</li>
 *   <li>TWO_PLAYER_VS: Creates TwoPlayerMode with dual game services</li>
 * </ul>
 * 
 * @author Dong, Jia.
 */
public class GameModeFactory {

    /**
     * Creates a new game mode instance based on the specified type.
     * 
     * <p>For LEVEL mode, automatically retrieves the first available level from
     * LevelManager. For TWO_PLAYER_VS mode, creates separate game services and boards
     * for both players.
     * 
     * @param type the type of game mode to create, must not be null
     * @param gameService the game service instance for single-player modes (null for TWO_PLAYER_VS)
     * @param guiController the GUI controller for UI updates, must not be null
     * @return a new game mode instance implementing the requested mode
     * @throws IllegalArgumentException if the game mode type is not supported
     * @throws IllegalStateException if LEVEL mode is requested but no levels are available
     */
    public static GameMode createGameMode(GameModeType type, GameService gameService, GuiController guiController) {
        switch (type) {
            case ENDLESS:
                return new EndlessMode(gameService, guiController);
            case LEVEL:
                LevelManager levelManager = LevelManager.getInstance();
                var availableLevels = levelManager.getAllLevels();
                if (availableLevels.isEmpty()) {
                    throw new IllegalStateException("No levels available");
                }
                return new LevelGameModeImpl(gameService, guiController, levelManager, availableLevels.getFirst());
            case TWO_PLAYER_VS:
                com.comp2042.model.board.Board player1Board = new com.comp2042.model.board.SimpleBoard(10, 20);
                com.comp2042.model.board.Board player2Board = new com.comp2042.model.board.SimpleBoard(10, 20);
                GameService player1Service = new com.comp2042.service.gameloop.GameServiceImpl(player1Board);
                GameService player2Service = new com.comp2042.service.gameloop.GameServiceImpl(player2Board);
                return new TwoPlayerMode(player1Service, player2Service, guiController);
            default:
                throw new IllegalArgumentException("Unknown game mode type: " + type);
        }
    }
}