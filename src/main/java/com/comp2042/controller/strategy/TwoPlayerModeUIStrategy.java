package com.comp2042.controller.strategy;

import com.comp2042.dto.ViewData;
import com.comp2042.view.manager.CommonUIManager;

/**
 * Two-Player Mode UI strategy implementation.
 * Most logic handled by TwoPlayerPanelManager.
 */
public class TwoPlayerModeUIStrategy implements GameModeUIStrategy {

    @Override
    public void initialize(CommonUIManager commonUI) {
    }

    @Override
    public void onGameStart() {
    }

    @Override
    public void onGameTick() {
    }

    @Override
    public void updateBrickDisplay(ViewData brick) {
    }

    @Override
    public void updateScore(int score) {
    }

    @Override
    public void updateLines(int lines) {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onGameOver() {
    }
}