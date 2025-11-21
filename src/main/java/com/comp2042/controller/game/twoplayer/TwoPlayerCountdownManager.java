package com.comp2042.controller.game.twoplayer;

import com.comp2042.controller.game.GuiController;

/**
 * Handles the countdown UX prior to starting a versus match so the
 * main controller can focus on gameplay coordination.
 */
public class TwoPlayerCountdownManager {

    private final GuiController guiController;

    public TwoPlayerCountdownManager(GuiController guiController) {
        this.guiController = guiController;
    }

    /**
    * Starts the countdown. If a GUI controller is not available, the completion
    * callback is invoked immediately.
    */
    public void startCountdown(Runnable onComplete) {
        if (guiController == null) {
            onComplete.run();
            return;
        }
        guiController.showCountdown(onComplete);
    }
}

