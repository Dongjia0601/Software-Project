package com.comp2042.controller.factory;

import com.comp2042.service.gameloop.GameService;
import com.comp2042.controller.game.GameViewController;
import com.comp2042.model.mode.EndlessMode;
import com.comp2042.model.mode.LevelManager;
import com.comp2042.model.mode.TwoPlayerVSGameMode;

/**
 * Factory class for creating game mode instances.
 * This class implements the Factory pattern to create appropriate game mode
 * implementations based on the requested game mode type.
 * 
 * @author Dong, Jia.
 */
public class GameModeFactory {

    /**
     * Creates a new game mode instance based on the specified type.
     * @param type the type of game mode to create
     * @param gameService the game service to use
     * @param guiController the GUI controller to use
     * @return a new game mode instance
     * @throws IllegalArgumentException if the game mode type is not supported
     */
    public static GameMode createGameMode(GameModeType type, GameService gameService, GameViewController guiController) {
        switch (type) {
            case ENDLESS:
                return new EndlessMode(gameService, guiController);
            case LEVEL:
                // Get the first available level for now
                LevelManager levelManager = LevelManager.getInstance();
                var availableLevels = levelManager.getAllLevels();
                if (availableLevels.isEmpty()) {
                    throw new IllegalStateException("No levels available");
                }
                return new LevelGameModeImpl(gameService, guiController, levelManager, availableLevels.get(0));
            case TWO_PLAYER_VS:
                // Create two separate game services for VS mode
                // Use dependency injection: create Board instances explicitly and inject them
                com.comp2042.model.board.Board player1Board = new com.comp2042.model.board.SimpleBoard(10, 20);
                com.comp2042.model.board.Board player2Board = new com.comp2042.model.board.SimpleBoard(10, 20);
                GameService player1Service = new com.comp2042.service.gameloop.GameServiceImpl(player1Board);
                GameService player2Service = new com.comp2042.service.gameloop.GameServiceImpl(player2Board);
                return new TwoPlayerVSGameMode(player1Service, player2Service, guiController);
            default:
                throw new IllegalArgumentException("Unknown game mode type: " + type);
        }
    }

    /**
     * Creates a game mode with default configuration.
     * @param type the type of game mode to create
     * @return a new game mode instance with default configuration
     */
    public static GameMode createGameMode(GameModeType type) {
        // This method will be implemented when we have default configurations
        throw new UnsupportedOperationException("Default game mode creation not yet implemented");
    }

    /**
     * Gets all available game mode types.
     * @return an array of all game mode types
     */
    public static GameModeType[] getAvailableGameModes() {
        return GameModeType.values();
    }

    /**
     * Checks if a game mode type is supported.
     * @param type the game mode type to check
     * @return true if the type is supported, false otherwise
     */
    public static boolean isGameModeSupported(GameModeType type) {
        return type != null;
    }

    /**
     * Gets the display name for a game mode type.
     * @param type the game mode type
     * @return the display name
     */
    public static String getGameModeDisplayName(GameModeType type) {
        return type != null ? type.getDisplayName() : "Unknown Mode";
    }

    /**
     * Gets the description for a game mode type.
     * @param type the game mode type
     * @return the description
     */
    public static String getGameModeDescription(GameModeType type) {
        return type != null ? type.getDescription() : "No description available";
    }
}