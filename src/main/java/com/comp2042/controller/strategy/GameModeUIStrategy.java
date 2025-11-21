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

    /** Updates brick display when piece changes. Default: handled by GameViewController. */
    default void updateBrickDisplay(ViewData brick) {
        // Brick display is handled by GameViewController's refreshBrick method
    }

    /** Updates score display. Default: no-op. */
    default void updateScore(int score) {
        // Score updates handled by HudManager
    }

    /** Updates lines cleared display. */
    void updateLines(int lines);

    /** Called when game is paused. Default: no-op (handled by GameTimelineManager). */
    default void onPause() {
        // Pause logic handled by GameTimelineManager
    }

    /** Called when game is resumed. Default: no-op (handled by GameTimelineManager). */
    default void onResume() {
        // Resume logic handled by GameTimelineManager
    }

    /** Called when game ends. Default: no-op (handled by GameViewController). */
    default void onGameOver() {
    }
}
