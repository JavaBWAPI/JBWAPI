package bwapi;

import java.util.Objects;

/**
 * Client class to connect to the game with.
 */
public class BWClient {
    private final BWEventListener eventListener;
    private BotWrapper botWrapper;
    private PerformanceMetrics performanceMetrics;

    public BWClient(final BWEventListener eventListener) {
        Objects.requireNonNull(eventListener);
        this.eventListener = eventListener;
    }

    /**
     * Get the {@link Game} instance of the currently running game.
     * When running in asynchronous mode, this is the game from the bot's perspective, eg. potentially a previous frame.
     */
    public Game getGame() {
        return botWrapper == null ? null : botWrapper.getGame();
    }

    /**
     * Returns JBWAPI performance metrics.
     * Metrics will be mostly empty if metrics collection isn't timersEnabled in the bot configuration
     */
    public PerformanceMetrics performanceMetrics() {
        return performanceMetrics;
    }

    public void startGame() {
        BWClientConfiguration configuration = new BWClientConfiguration();
        startGame(configuration);
    }

    /**
     * Start the game.
     *
     * @param autoContinue automatically continue playing the next game(s). false by default
     */
    @Deprecated
    public void startGame(boolean autoContinue) {
        BWClientConfiguration configuration = new BWClientConfiguration();
        configuration.autoContinue = autoContinue;
        startGame(configuration);
    }

    /**
     * Start the game.
     *
     * @param configuration Settings for playing games with this client.
     */
    public void startGame(BWClientConfiguration configuration) {
        configuration.validate();
        botWrapper = new BotWrapper(configuration, eventListener);

        Client client = new Client(configuration);
        client.reconnect();

        do {
            ClientData.GameData liveGameData = client.clientData().gameData();
            while (!liveGameData.isInGame()) {
                if (!client.isConnected()) {
                    return;
                }
                client.update();
                if (liveGameData.isInGame()) {
                    performanceMetrics = new PerformanceMetrics(configuration);
                    botWrapper.startNewGame(client.mapFile(), performanceMetrics);
                }
            }
            while (liveGameData.isInGame()) {
                performanceMetrics.totalFrameDuration.time(() -> {
                    botWrapper.onFrame();
                    performanceMetrics.flushSideEffects.time(() -> {
                        getGame().sideEffects.flushTo(liveGameData);
                    });
                    client.update();
                    if (!client.isConnected()) {
                        client.reconnect();
                    }
                });
            }
            botWrapper.endGame();
        } while (configuration.autoContinue);
    }
}