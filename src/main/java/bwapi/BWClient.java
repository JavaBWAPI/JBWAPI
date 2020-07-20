package bwapi;

import java.util.Objects;

/**
 * Client class to connect to the game with.
 */
public class BWClient {
    private final BWEventListener eventListener;
    private EventHandler handler;

    public BWClient(final BWEventListener eventListener) {
        Objects.requireNonNull(eventListener);
        this.eventListener = eventListener;
    }

    /**
     * Get the {@link Game} instance of the currently running game.
     */
    public Game getGame() {
        return handler == null ? null : handler.getGame();
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
        handler = new EventHandler(eventListener, client);

        do {
            BotWrapper botWrapper;
            while (!getGame().isInGame()) {
                if (!client.isConnected()) {
                    return;
                }
                client.update();
            }
            botWrapper = new BotWrapper(configuration, client.mapFile(), client.gameData(), handler);
            botWrapper.step();
            while (getGame().isInGame()) {
                client.update();
                botWrapper.step();
                if (!client.isConnected()) {
                    System.out.println("Reconnecting...");
                    client.reconnect();
                }
            }

            // TODO: Before exiting give async bot time to complete onEnd().

        } while (configuration.autoContinue); // lgtm [java/constant-loop-condition]


    }
}