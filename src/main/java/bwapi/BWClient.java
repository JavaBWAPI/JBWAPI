package bwapi;

import com.sun.jna.platform.win32.Kernel32;

import java.util.Objects;

/**
 * Client class to connect to the game with.
 */
public class BWClient {
    private BWClientConfiguration configuration = new BWClientConfiguration();
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
     * @return The current configuration
     */
    public BWClientConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * @return Whether the current frame should be subject to timing.
     */
    boolean doTime() {
        return ! configuration.getUnlimitedFrameZero() || (client.isConnected() && client.liveClientData().gameData().getFrameCount() > 0);
    }

    /**
     * @return The number of frames between the one exposed to the bot and the most recent received by JBWAPI.
     * This tracks the size of the frame buffer except when the game is paused (which results in multiple frames arriving with the same count).
     */
    public int framesBehind() {
        return botWrapper == null ? 0 : Math.max(0, client.liveClientData().gameData().getFrameCount() - getGame().getFrameCount());
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
        configuration.withAutoContinue(autoContinue);
        startGame(configuration);
    }

    /**
     * Start the game.
     *
     * @param gameConfiguration Settings for playing games with this client.
     */
    public void startGame(BWClientConfiguration gameConfiguration) {
        gameConfiguration.validateAndLock();
        this.configuration = gameConfiguration;
        this.performanceMetrics = new PerformanceMetrics(configuration);
        botWrapper = new BotWrapper(configuration, eventListener);

        // Use reduced priority to encourage Windows to give priority to StarCraft.exe/BWAPI.
        // If BWAPI doesn't get priority, it may not detect completion of a frame on our end in timely fashion.
        Thread.currentThread().setName("JBWAPI Client");
        if (configuration.getAsync()) {
            Thread.currentThread().setPriority(4);
        }

        if (client == null) {
            client = new Client(this);
        }
        client.reconnect();

        do {
            ClientData.GameData liveGameData = client.liveClientData().gameData();
            while (!liveGameData.isInGame()) {
                if (!client.isConnected()) {
                    return;
                }
                client.sendFrameReceiveFrame();
                if (liveGameData.isInGame()) {
                    performanceMetrics = new PerformanceMetrics(configuration);
                    botWrapper.startNewGame(client.mapFile(), performanceMetrics);
                }
            }
            while (liveGameData.isInGame()) {
                botWrapper.onFrame();
                performanceMetrics.getFlushSideEffects().time(() -> getGame().sideEffects.flushTo(liveGameData));
                performanceMetrics.getFrameDurationReceiveToSend().stopTiming();

                client.sendFrameReceiveFrame();
                if (!client.isConnected()) {
                    System.out.println("Reconnecting...");
                    client.reconnect();
                }
            }
            botWrapper.endGame();
        } while (configuration.getAutoContinue());
    }

    /**
     * Provides a Client. Intended for test consumers only.
     */
    void setClient(Client client) {
        this.client = client;
    }
}