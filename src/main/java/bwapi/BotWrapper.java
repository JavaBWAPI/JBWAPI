package bwapi;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manages invocation of bot event handlers
 */
class BotWrapper {
    private final ClientData liveClientData = new ClientData();
    private final BWClientConfiguration configuration;
    private final BWEventListener eventListener;
    private final FrameBuffer frameBuffer;
    private Game game;
    private Thread botThread;
    private boolean gameOver;
    private PerformanceMetrics performanceMetrics;
    private Throwable lastBotThrow;
    private ReentrantLock lastBotThrowLock = new ReentrantLock();

    BotWrapper(BWClientConfiguration configuration, BWEventListener eventListener) {
        this.configuration = configuration;
        this.eventListener = eventListener;
        frameBuffer = configuration.async ? new FrameBuffer(configuration) : null;
    }

    /**
     * Resets the BotWrapper for a new game.
     */
    void startNewGame(ByteBuffer liveData, PerformanceMetrics performanceMetrics) {
        if (configuration.async) {
            frameBuffer.initialize(liveData, performanceMetrics);
        }
        this.performanceMetrics = performanceMetrics;
        game = new Game();
        game.clientData().setBuffer(liveData);
        liveClientData.setBuffer(liveData);
        botThread = null;
        gameOver = false;
    }

    /**
     * @return The Game object used by the bot
     * In asynchronous mode this Game object may point at a copy of a previous frame.
     */
    Game getGame() {
        return game;
    }

    /**
     * Handles the arrival of a new frame from BWAPI
     */
    void onFrame() {
        if (configuration.async) {
            configuration.log("Main: onFrame asynchronous start");
            long startNanos = System.nanoTime();
            long endNanos = startNanos + configuration.maxFrameDurationMs * 1000000;
            if (botThread == null) {
                configuration.log("Main: Starting bot thread");
                botThread = createBotThread();
                botThread.setName("JBWAPI Bot");
                botThread.start();
            }
            /*
            Add a frame to buffer
            If buffer is full, it will wait until it has capacity
            Wait for empty buffer OR termination condition
            */
            int frame = liveClientData.gameData().getFrameCount();
            configuration.log("Main: Enqueuing frame #" + frame);
            frameBuffer.enqueueFrame();
            frameBuffer.lockSize.lock();
            try {
                while (!frameBuffer.empty()) {

                    // Make bot exceptions fall through to the main thread.
                    Throwable lastThrow = getLastBotThrow();
                    if (lastThrow != null) {
                        configuration.log("Main: Rethrowing bot throwable");
                        throw new RuntimeException(lastThrow);
                    }

                    if (configuration.unlimitedFrameZero && frame == 0) {
                        configuration.log("Main: Waiting indefinitely on frame #" + frame);
                        frameBuffer.conditionSize.await();
                    } else {
                        long remainingNanos = endNanos - System.nanoTime();
                        if (remainingNanos <= 0) {
                            configuration.log("Main: Out of time in frame #" + frame);
                            break;
                        }
                        configuration.log("Main: Waiting " + remainingNanos / 1000000 + "ms for bot on frame #" + frame);
                        frameBuffer.conditionSize.awaitNanos(remainingNanos);
                    }
                }
            } catch(InterruptedException ignored) {
            } finally {
                frameBuffer.lockSize.unlock();
                configuration.log("Main: onFrame asynchronous end");
            }
        } else {
            configuration.log("Main: onFrame synchronous start");
            handleEvents();
            configuration.log("Main: onFrame synchronous end");
        }

    }

    /**
     * Allows an asynchronous bot time to finish operation
     */
    void endGame() {
        if (botThread != null) {
            try {
                botThread.join();
            } catch (InterruptedException ignored) {}
        }
    }

    Throwable getLastBotThrow() {
        lastBotThrowLock.lock();
        Throwable output = lastBotThrow;
        lastBotThrowLock.unlock();
        return output;
    }

    private Thread createBotThread() {
        return new Thread(() -> {
            try {
                configuration.log("Bot: Thread started");
                while (!gameOver) {

                    configuration.log("Bot: Ready for another frame");
                    performanceMetrics.botIdle.time(() -> {
                        frameBuffer.lockSize.lock();
                        try {
                            while (frameBuffer.empty()) {
                                configuration.log("Bot: Waiting for a frame");
                                frameBuffer.conditionSize.awaitUninterruptibly();
                            }
                        } finally {
                            frameBuffer.lockSize.unlock();
                        }
                    });

                    configuration.log("Bot: Peeking next frame");
                    game.clientData().setBuffer(frameBuffer.peek());

                    configuration.log("Bot: Handling frame #" + game.getFrameCount());
                    handleEvents();

                    configuration.log("Bot: Events done. Dequeuing frame #" + game.getFrameCount());
                    frameBuffer.dequeue();
                }
            } catch (Throwable throwable) {
                // Record the throw,
                // Then allow the thread to terminate silently.
                // The main thread will look for the stored throw.
                lastBotThrowLock.lock();
                lastBotThrow = throwable;
                lastBotThrowLock.unlock();

                // Awaken any threads waiting on bot progress
                while (!frameBuffer.empty()) {
                    frameBuffer.dequeue();
                }
            }
        });
    }

    private void handleEvents() {
        ClientData.GameData gameData = game.clientData().gameData();

        // Populate gameOver before invoking event handlers (in case the bot throws)
        for (int i = 0; i < gameData.getEventCount(); i++) {
            gameOver = gameOver || gameData.getEvents(i).getType() == EventType.MatchEnd;
        }

        if (configuration.async) {
            performanceMetrics.framesBehind.record(frameBuffer.framesBuffered() - 1);
        }

        performanceMetrics.botResponse.timeIf(
            ! gameOver && (gameData.getFrameCount() > 0 || ! configuration.unlimitedFrameZero),
            () -> {
                for (int i = 0; i < gameData.getEventCount(); i++) {
                    EventHandler.operation(eventListener, game, gameData.getEvents(i));
                }
            });
    }
}
