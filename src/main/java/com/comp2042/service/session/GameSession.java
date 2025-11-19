package com.comp2042.service.session;

import com.comp2042.dto.DownData;
import com.comp2042.dto.ViewData;
import com.comp2042.event.MoveEvent;
import com.comp2042.model.state.GameStateContext;
import javafx.beans.property.IntegerProperty;

/**
 * Abstraction over a playable session of the Tetris game.
 * Encapsulates board/state management so controllers focus solely
 * on wiring UI interactions.
 */
public interface GameSession extends GameStateContext {

    void initialize();

    DownData handleDown(MoveEvent event);

    ViewData handleLeft(MoveEvent event);

    ViewData handleRight(MoveEvent event);

    ViewData handleRotateCW(MoveEvent event);

    ViewData handleRotateCCW(MoveEvent event);

    ViewData handleHardDrop(MoveEvent event);

    ViewData handleSoftDrop(MoveEvent event);

    ViewData handleHold(MoveEvent event);

    void requestPause();

    void startNewGame();

    IntegerProperty scoreProperty();
}

