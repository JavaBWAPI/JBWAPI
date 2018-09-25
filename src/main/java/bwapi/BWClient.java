package bwapi;

import JavaBWAPIBackend.Client;
import java.util.Objects;



public class BWClient {
    private BWListener eventListener;

    private Client client;
    private EventHandler handler;


    public BWClient(final BWListener eventListener) {
        Objects.requireNonNull(eventListener);
        this.eventListener = eventListener;
    }

    public Game getGame() {
        return handler == null ? null : handler.getGame();
    }

    public void startGame() {
        while(client == null) {
            try {
                client = new Client();
            }
            catch (Throwable t) {
                System.err.println("Game table mapping not found.");
                try {
                    Thread.sleep(1000);
                }
                catch (Throwable ignored) { }            }
        }

        handler = new EventHandler(eventListener, client.data());

        try {
            while(!client.data().isInGame()) {
                client.update(handler);
            }
            while(client.data().isInGame()) {
                client.update(handler);
            }
        }
        catch (Throwable exception) {
            exception.printStackTrace();
        }
    }
}