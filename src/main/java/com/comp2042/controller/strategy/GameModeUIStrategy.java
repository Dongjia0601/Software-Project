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

    /** Initializes UI for this game mode. Called once when mode is activated. */
    void initialize(CommonUIManager commonUI);

    /** Called when a new game starts. */
    void onGameStart();

    /** Called on each game loop tick. Default: no-op. */
    default void onGameTick() {

    }

    /** Updates brick display when piece changes. Default: handled by GuiController. */
    default void updateBrickDisplay(ViewData brick) {
    }

    /** Updates score display. Default: no-op. */
    default void updateScore(int score) {
    }

    /** Updates lines cleared display. */
    void updateLines(int lines);

    /** Called when game is paused. Default: no-op (handled by GameTimelineManager). */
    default void onPause() {
    }

    /** Called when game is resumed. Default: no-op (handled by GameTimelineManager). */
    default void onResume() {
    }

    /** Called when game ends. Default: no-op (handled by GuiController). */
    default void onGameOver() {
    }
}
