package bwapi;

import java.util.Objects;

/**
 * Client class to connect to the game with.
 */
public class BWClient {
    private final BWEventListener eventListener;
    private final boolean debugConnection;
    private EventHandler handler;

    public BWClient(final BWEventListener eventListener) {
        this(eventListener, false);
    }

    public BWClient(final BWEventListener eventListener, final boolean debugConnection) {
        Objects.requireNonNull(eventListener);
        this.debugConnection = debugConnection;
        this.eventListener = eventListener;
    }

    /**
     * Get the {@link Game} instance of the currently running game.
     */
    public Game getGame() {
        return handler == null ? null : handler.getGame();
    }

    public void startGame() {
        startGame(false);
    }

    /**
     * Start the game.
     *
     * @param autoContinue automatically continue playing the next game(s). false by default
     */
    public void startGame(boolean autoContinue) {
        Client client = new Client(debugConnection);
        client.reconnect();
        handler = new EventHandler(eventListener, client);

        do {
            while (!getGame().isInGame()) {
                if (!client.isConnected()) {
                    return;
                }
                client.update(handler);
            }
            while (getGame().isInGame()) {
                client.update(handler);
                if (!client.isConnected()) {
                    System.out.println("Reconnecting...");
                    client.reconnect();
                }
            }
        } while (autoContinue); // lgtm [java/constant-loop-condition]
    }
}