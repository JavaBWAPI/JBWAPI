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
            long lastUpdateTimestampMillis = 0;
            System.out.println("Client: Starting game");
            while (!getGame().isInGame()) {
                if (!client.isConnected()) {
                    return;
                }
                System.out.println("Client: connected.");
                lastUpdateTimestampMillis = System.currentTimeMillis();
                client.update();
            }

            System.out.println("Client: Creating bot wrapper");
            BotWrapper botWrapper = new BotWrapper(configuration, eventListener, client.mapFile());

            while (getGame().isInGame()) {
                System.out.println("Client: Stepping bot wrapper");
                botWrapper.step();
                System.out.println("Client: Waiting for idle bot or frame duration");

                // Proceed immediately once framebuffer is empty
                // Otherwise, wait for bot to catch up
                // TODO: Replace with a wait instead of a sleep
                // TODO: Respect configuration.asyncWaitOnFrameZero
                while ( ! botWrapper.botIdle()) {
                    long frameDuration = System.currentTimeMillis() - lastUpdateTimestampMillis;
                    if (frameDuration > configuration.asyncFrameDurationMillis && (client.clientData().gameData().getFrameCount() > 0 || ! configuration.asyncWaitOnFrameZero)) {
                        System.out.println("Client: Exceeded frame duration while waiting for bot: " + frameDuration + "ms on frame " + client.clientData().gameData().getFrameCount());
                        break;
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ignored) {}
                }

                System.out.println("Client: Ending frame. Frames buffered: ");
                lastUpdateTimestampMillis = System.currentTimeMillis();
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