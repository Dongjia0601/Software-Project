package com.comp2042.controller.strategy;

import com.comp2042.view.manager.CommonUIManager;

/**
 * Two-Player Mode UI strategy implementation.
 * Most logic is handled by TwoPlayerPanelManager, so this class provides minimal implementation.
 * 
 * <p><b>Note:</b> This class is currently not actively used in the codebase.
 * Two-player mode UI management is primarily handled by TwoPlayerPanelManager.
 * This class is retained for Strategy Pattern completeness and potential future extensions.
 * 
 * <p><b>Design Note:</b> The Strategy Pattern for UI management was designed but not fully
 * integrated. Currently, UI updates are handled directly through GuiController methods rather
 * than through strategy instances. This class exists to maintain architectural consistency
 * and may be integrated in future refactoring.
 * 
 * @author Dong, Jia
 * @apiNote Reserved for Strategy Pattern completeness - not currently instantiated
 */
@SuppressWarnings("unused")
public class TwoPlayerModeUIStrategy implements GameModeUIStrategy {

    /**
     * Initializes UI components for two-player mode.
     * Currently, does nothing as UI is managed by TwoPlayerPanelManager.
     * 
     * @param commonUI the common UI manager for shared components
     */
    @Override
    public void initialize(CommonUIManager commonUI) {
        // UI initialization handled by TwoPlayerPanelManager
    }

    /**
     * Called when a new two-player game starts.
     * Currently, does nothing as game start logic is handled by TwoPlayerGameController.
     */
    @Override
    public void onGameStart() {
        // Game start logic handled by TwoPlayerGameController
    }

    /**
     * Updates the lines cleared display for two-player mode.
     * Currently, does nothing as display updates are handled by TwoPlayerPanelManager.
     * 
     * @param lines the number of lines cleared
     */
    @Override
    public void updateLines(int lines) {
        // Lines display updates handled by TwoPlayerPanelManager
    }
    
    // Other methods (onGameTick, updateBrickDisplay, updateScore, onPause, onResume, onGameOver)
    // use default implementations from GameModeUIStrategy interface
}