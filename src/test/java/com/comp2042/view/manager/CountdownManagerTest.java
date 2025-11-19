package com.comp2042.view.manager;

import com.comp2042.service.audio.SoundManager;
import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class CountdownManagerTest {

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    void pauseCountdownReturnsCallback() throws Exception {
        CountdownManager countdownManager = new CountdownManager(SoundManager.getInstance());
        BorderPane root = new BorderPane();
        GridPane player1Panel = new GridPane();
        GridPane player2Panel = new GridPane();
        Pane boardBackground1 = new Pane();
        Pane boardBackground2 = new Pane();
        boardBackground1.setPrefSize(200, 400);
        boardBackground2.setPrefSize(200, 400);

        Pane container1 = new Pane(boardBackground1, player1Panel);
        Pane container2 = new Pane(boardBackground2, player2Panel);
        root.setLeft(container1);
        root.setRight(container2);

        CountDownLatch ready = new CountDownLatch(1);
        CountDownLatch finished = new CountDownLatch(1);

        Platform.runLater(() -> {
            countdownManager.showTwoPlayerCountdown(
                root,
                player1Panel,
                player2Panel,
                boardBackground1,
                boardBackground2,
                finished::countDown
            );
            ready.countDown();
        });

        assertTrue(ready.await(2, TimeUnit.SECONDS));
        assertTrue(countdownManager.isRunning());

        Runnable callback = countdownManager.pauseCountdown();
        assertNotNull(callback);
        assertFalse(countdownManager.isRunning());
        assertEquals(1, finished.getCount());

        callback.run();
        assertTrue(finished.await(1, TimeUnit.SECONDS));
    }

    @Test
    void cancelCountdownSafeWhenNotRunning() {
        CountdownManager countdownManager = new CountdownManager(SoundManager.getInstance());
        countdownManager.cancelCountdown();
        assertFalse(countdownManager.isRunning());
    }
}

