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
        botWrapper = new BotWrapper(configuration, eventListener);

        Client client = new Client(configuration);
        client.reconnect();

        do {
            System.out.println("Client: Beginning game loop");
            ClientData.GameData liveGameData = client.clientData().gameData();
            while (!liveGameData.isInGame()) {
                if (client.isConnected()) {
                    System.out.println("Client: Not in game; Connected.");
                } else  {
                    System.out.println("Client: Not in game; Not connected.");
                    return;
                }
                client.update();
                if (liveGameData.isInGame()) {
                    botWrapper.startNewGame(client.mapFile());
                }
            }
            while (liveGameData.isInGame()) {
                System.out.println("Client: In game on frame " + liveGameData.getFrameCount());
                botWrapper.onFrame();
                System.out.println("Client: Sending commands for frame " + liveGameData.getFrameCount());
                getGame().sideEffects.flushTo(liveGameData);
                client.update();
                if (!client.isConnected()) {
                    System.out.println("Reconnecting...");
                    client.reconnect();
                }
            }
            botWrapper.endGame();
        } while (configuration.autoContinue); // lgtm [java/constant-loop-condition]
    }
}