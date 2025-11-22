package com.comp2042.controller.game.twoplayer;

import com.comp2042.controller.game.GuiController;

/**
 * Manages the countdown sequence before starting a two-player match.
 * 
 * @param guiController the GUI controller for displaying countdown, may be null
 */
public record TwoPlayerCountdownManager(GuiController guiController) {

    /**
     * Starts the countdown sequence before game start.
     * 
     * @param onComplete the callback to execute when countdown finishes, must not be null
     */
    public void startCountdown(Runnable onComplete) {
        if (guiController == null) {
            onComplete.run();
            return;
        }
        guiController.showCountdown(onComplete);
    }
}

