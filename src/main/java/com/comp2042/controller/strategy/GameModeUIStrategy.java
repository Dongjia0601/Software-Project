package com.comp2042.controller.strategy;

import com.comp2042.dto.ViewData;
import com.comp2042.view.manager.CommonUIManager;

/**
 * Strategy interface for game mode-specific UI behavior.
 * Enables different UI logic for Endless, Level, and Two-Player modes.
 *
 * @author Dong, Jia
 */
public interface GameModeUIStrategy {

    /**
     * Initializes UI components for this game mode.
     * Called once when the mode is activated.
     * 
     * @param commonUI the common UI manager for shared components
     */
    void initialize(CommonUIManager commonUI);

    /**
     * Called when a new game starts.
     * Performs mode-specific initialization for the new game session.
     */
    void onGameStart();

    /**
     * Called on each game loop tick.
     * Default implementation does nothing.
     */
    default void onGameTick() {

    }

    /**
     * Updates brick display when the current piece changes.
     * Default implementation does nothing (handled by GuiController).
     * 
     * <p><b>Note:</b> This method is reserved for future extensions.
     * Currently, brick display updates are handled directly by GuiController.
     * 
     * @param brick the view data containing brick information
     * 
     * <p><b>Note:</b> Reserved for future use - not currently invoked
     */
    default void updateBrickDisplay(ViewData brick) {
    }

    /**
     * Updates the score display.
     * Default implementation does nothing.
     * 
     * @param score the current score value
     */
    default void updateScore(int score) {
    }

    /**
     * Updates the lines cleared display.
     * 
     * @param lines the number of lines cleared
     */
    void updateLines(int lines);

    /**
     * Called when the game is paused.
     * Default implementation does nothing (handled by GameTimelineManager).
     * 
     * <p><b>Note:</b> This method is reserved for future extensions.
     * Currently, pause logic is handled directly by GameTimelineManager and GuiController.
     * 
     * 
     * <p><b>Note:</b> Reserved for future use - not currently invoked
     */
    default void onPause() {
    }

    /**
     * Called when the game is resumed from pause.
     * Default implementation does nothing (handled by GameTimelineManager).
     * 
     * <p><b>Note:</b> This method is reserved for future extensions.
     * Currently, resume logic is handled directly by GameTimelineManager and GuiController.
     * 
     * 
     * <p><b>Note:</b> Reserved for future use - not currently invoked
     */
    default void onResume() {
    }

    /**
     * Called when the game ends.
     * Default implementation does nothing (handled by GuiController).
     * 
     * <p><b>Note:</b> This method is reserved for future extensions.
     * Currently, game over logic is handled directly by GuiController.
     * 
     * 
     * <p><b>Note:</b> Reserved for future use - not currently invoked
     */
    default void onGameOver() {
    }
}
