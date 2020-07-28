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
            ClientData.GameData gameData = client.clientData().gameData();
            long lastUpdateTimestampMillis = 0;
            System.out.println("Client: Beginning game loop");
            while (!gameData.isInGame()) {
                botWrapper = null;
                if (client.isConnected()) {
                    System.out.println("Client: Not in game; Connected.");
                } else  {
                    System.out.println("Client: Not in game; Not connected.");
                    return;
                }
                lastUpdateTimestampMillis = System.currentTimeMillis();
                client.update();
            }
            while (gameData.isInGame()) {
                System.out.println("Client: In game on frame " + gameData.getFrameCount());
                if (botWrapper == null) {
                    botWrapper = new BotWrapper(configuration, eventListener, client.mapFile());
                }
                botWrapper.step();

                // Proceed immediately once framebuffer is empty
                // Otherwise, wait for bot to catch up
                // TODO: Replace with a wait instead of a sleep
                while(true) {
                    if (botWrapper.botIdle()) {
                        System.out.println("Client: Proceeding because bot is idle.");
                        break;
                    }
                    long frameDurationMillis = System.currentTimeMillis() - lastUpdateTimestampMillis;
                    if (botWrapper.frameDurationMillis > configuration.asyncFrameDurationMillis && (client.clientData().gameData().getFrameCount() > 0 || ! configuration.asyncWaitOnFrameZero)) {
                        System.out.println("Client: Proceeding because frame " + botWrapper.getGame().getFrameCount() + " lasted " + frameDurationMillis + "ms");
                        break;
                    }
                    try { Thread.sleep(1); } catch (InterruptedException ignored) {}
                }

                long currentTimeMillis = System.currentTimeMillis();
                long frameDurationMillis = currentTimeMillis - lastUpdateTimestampMillis;
                lastUpdateTimestampMillis = currentTimeMillis;
                System.out.println("Client: Ending frame after " + frameDurationMillis + "ms");
                getGame().sideEffects.flush(client.clientData());
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