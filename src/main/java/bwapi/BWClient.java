package bwapi;

import java.util.Objects;

/**
 * Client class to connect to the game with.
 */
public class BWClient {
    private final BWEventListener eventListener;
    private BotWrapper botWrapper;

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
        Client client = new Client(configuration);
        client.reconnect();

        do {
            ClientData liveClientData = client.clientData();
            ClientData.GameData liveGameData = liveClientData.gameData();
            System.out.println("Client: Beginning game loop");
            while (!liveGameData.isInGame()) {
                botWrapper = null;
                if (client.isConnected()) {
                    System.out.println("Client: Not in game; Connected.");
                } else  {
                    System.out.println("Client: Not in game; Not connected.");
                    return;
                }
                client.update();
            }
            while (liveGameData.isInGame()) {
                System.out.println("Client: In game on frame " + liveGameData.getFrameCount());
                if (botWrapper == null) {
                    botWrapper = new BotWrapper(configuration, eventListener, client.mapFile());
                }
                botWrapper.step();

                // Proceed immediately once frame buffer is empty
                // Otherwise, wait for bot to catch up
                botWrapper.idleLock.lock();
                try {
                    while ( ! botWrapper.botIdle()) { // and we have time remaining (with optional extra for frame 0) or no room left in frame buffer
                        System.out.println("Client: Waiting for idle bot on frame " + liveGameData.getFrameCount());
                        botWrapper.idleCondition.awaitUninterruptibly();
                    }
                } finally {
                    botWrapper.idleLock.unlock();
                }

                System.out.println("Client: Sending commands on frame " + liveGameData.getFrameCount());
                getGame().sideEffects.flushTo(liveClientData);
                client.update();
                if (!client.isConnected()) {
                    System.out.println("Reconnecting...");
                    client.reconnect();
                }
            }

            // TODO: Before exiting give async bot time to complete onEnd(), maybe via thread.join().

        } while (configuration.autoContinue); // lgtm [java/constant-loop-condition]
    }
}