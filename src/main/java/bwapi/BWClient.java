package bwapi;

import java.util.Objects;

public class BWClient {
    private final BWEventListener eventListener;

    private Client client;
    private EventHandler handler;


    public BWClient(final BWEventListener eventListener) {
        Objects.requireNonNull(eventListener);
        this.eventListener = eventListener;
    }

    public Game getGame() {
        return handler == null ? null : handler.getGame();
    }

    public void startGame() {
        while (client == null) {
            try {
                client = new Client();
            } catch (final Exception t) {
                System.err.println("Game table mapping not found.");
                try {
                    Thread.sleep(1000);
                } catch (final Exception ignored) {
                }
            }
        }

        handler = new EventHandler(eventListener, client);

        try {
            while (!client.data().isInGame()) {
                client.update(handler);
            }
            while (client.data().isInGame()) {
                client.update(handler);
            }
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
    }
}