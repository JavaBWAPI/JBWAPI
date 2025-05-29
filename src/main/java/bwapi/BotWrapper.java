package bwapi;

/**
 * Manages invocation of bot event handlers
 */
class BotWrapper {
    protected final BWClientConfiguration configuration;
    protected final BWEventListener eventListener;

    protected Game botGame;
    protected boolean gameOver;
    protected PerformanceMetrics performanceMetrics;

    BotWrapper(BWClientConfiguration configuration, BWEventListener eventListener) {
        this.configuration = configuration;
        this.eventListener = eventListener;
    }

    /**
     * Resets the BotWrapper for a new botGame.
     */
    void startNewGame(WrappedBuffer liveData, PerformanceMetrics performanceMetrics) {
        this.performanceMetrics = performanceMetrics;
        botGame = new Game();
        botGame.setConfiguration(configuration);
        botGame.botClientData().setBuffer(liveData);
        gameOver = false;
    }

    /**
     * @return The Game object used by the bot
     * In asynchronous mode this Game object may point at a copy of a previous frame.
     */
    Game getGame() {
        return botGame;
    }

    /**
     * Handles the arrival of a new frame from BWAPI
     */
    void onFrame() {
        configuration.log("Main: onFrame synchronous start");
        handleEvents();
        configuration.log("Main: onFrame synchronous end");
    }

    void endGame() { }

    protected void handleEvents() {
        ClientData.GameData botGameData = botGame.botClientData().gameData();

        // Populate gameOver before invoking event handlers (in case the bot throws)
        for (int i = 0; i < botGameData.getEventCount(); i++) {
            gameOver = gameOver || botGameData.getEvents(i).getType() == EventType.MatchEnd;
        }

        performanceMetrics.getBotResponse().timeIf(
            ! gameOver && (botGameData.getFrameCount() > 0 || ! configuration.getUnlimitedFrameZero()),
            () -> {
                for (int i = 0; i < botGameData.getEventCount(); i++) {
                    EventHandler.operation(eventListener, botGame, botGameData.getEvents(i));
                }
            });
    }
}
