package bwapi;

import java.util.Objects;

/**
 * Client class to connect to the game with.
 */
public class BWClient {
    private final BWEventListener eventListener;
    private BotWrapper botWrapper;
    private Client client;
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
     * @return JBWAPI performance metrics.
     */
    public PerformanceMetrics getPerformanceMetrics() {
        return performanceMetrics;
    }

    /**
     * @return The number of frames between the one exposed to the bot and the most recent received by JBWAPI.
     * This tracks the size of the frame buffer except when the game is paused (which results in multiple frames arriving with the same count).
     */
    public int framesBehind() {
        return botWrapper == null ? 0 : Math.max(0, client.clientData().gameData().getFrameCount() - getGame().getFrameCount());
    }

    /**
     * For internal test use.
     */
    Client getClient() {
        return client;
    }

    /**
     * Start the game with default settings.
     */
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

        if (client == null) {
            client = new Client(configuration);
        }
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
                if (liveGameData.getFrameCount() > 0 || ! configuration.unlimitedFrameZero) {
                    performanceMetrics.totalFrameDuration.startTiming();
                }
                botWrapper.onFrame();
                performanceMetrics.flushSideEffects.time(() -> getGame().sideEffects.flushTo(liveGameData));
                performanceMetrics.totalFrameDuration.stopTiming();
                performanceMetrics.bwapiResponse.time(client::update);
                if (!client.isConnected()) {
                    client.reconnect();
                }
            }
            botWrapper.endGame();
        } while (configuration.autoContinue);
    }

    /**
     * Provides a Client. Intended for test consumers only.
     */
    void setClient(Client client) {
        this.client = client;
    }
}