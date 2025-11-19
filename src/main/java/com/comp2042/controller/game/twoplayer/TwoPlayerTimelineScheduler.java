package com.comp2042.controller.game.twoplayer;

import com.comp2042.dto.DownData;
import com.comp2042.event.EventSource;
import com.comp2042.event.EventType;
import com.comp2042.event.MoveEvent;
import com.comp2042.model.mode.TwoPlayerMode;
import com.comp2042.service.gameloop.GameService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Encapsulates the dual automatic drop timelines plus the shared stats update
 * loop so {@link com.comp2042.controller.game.TwoPlayerGameController} no longer
 * needs to micromanage Timeline instances directly.
 */
public class TwoPlayerTimelineScheduler {

    @FunctionalInterface
    public interface AutoDropListener {
        void onAutoDrop(int playerIndex, DownData downData);
    }

    @FunctionalInterface
    public interface StatsListener {
        void onStatsTick();
    }

    private final GameService player1Service;
    private final GameService player2Service;
    private final TwoPlayerMode gameMode;
    private final AutoDropListener dropListener;
    private final StatsListener statsListener;

    private Timeline player1Timeline;
    private Timeline player2Timeline;
    private Timeline statsTimeline;
    private boolean paused;

    public TwoPlayerTimelineScheduler(
            GameService player1Service,
            GameService player2Service,
            TwoPlayerMode gameMode,
            AutoDropListener dropListener,
            StatsListener statsListener) {
        this.player1Service = player1Service;
        this.player2Service = player2Service;
        this.gameMode = gameMode;
        this.dropListener = dropListener;
        this.statsListener = statsListener;
    }

    public void start() {
        stop();
        player1Timeline = createTimeline(player1Service, 1);
        player2Timeline = createTimeline(player2Service, 2);
        statsTimeline = createStatsTimeline();
        player1Timeline.play();
        player2Timeline.play();
        statsTimeline.play();
        paused = false;
    }

    public void pause() {
        paused = true;
        if (player1Timeline != null) {
            player1Timeline.pause();
        }
        if (player2Timeline != null) {
            player2Timeline.pause();
        }
        if (statsTimeline != null) {
            statsTimeline.pause();
        }
    }

    public void resume() {
        paused = false;
        if (player1Timeline != null) {
            player1Timeline.play();
        }
        if (player2Timeline != null) {
            player2Timeline.play();
        }
        if (statsTimeline != null) {
            statsTimeline.play();
        }
    }

    public void stop() {
        if (player1Timeline != null) {
            player1Timeline.stop();
            player1Timeline = null;
        }
        if (player2Timeline != null) {
            player2Timeline.stop();
            player2Timeline = null;
        }
        if (statsTimeline != null) {
            statsTimeline.stop();
            statsTimeline = null;
        }
    }

    private Timeline createTimeline(GameService service, int playerIndex) {
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> handleAutoDrop(service, playerIndex)
        ));
        timeline.setCycleCount(Timeline.INDEFINITE);
        return timeline;
    }

    private void handleAutoDrop(GameService service, int playerIndex) {
        if (paused || gameMode.isGameOver()) {
            return;
        }
        DownData downData = service.processDownEvent(new MoveEvent(EventType.DOWN, EventSource.THREAD));
        dropListener.onAutoDrop(playerIndex, downData);
    }

    private Timeline createStatsTimeline() {
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(1),
                ae -> {
                    if (paused || gameMode.isGameOver()) {
                        return;
                    }
                    statsListener.onStatsTick();
                }
        ));
        timeline.setCycleCount(Timeline.INDEFINITE);
        return timeline;
    }
}

