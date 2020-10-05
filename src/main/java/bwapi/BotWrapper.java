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
    private ByteBuffer liveData;
    private Game botGame;
    private Thread botThread;
    private boolean gameOver;
    private PerformanceMetrics performanceMetrics;
    private Throwable lastBotThrow;
    private ReentrantLock lastBotThrowLock = new ReentrantLock();
    private ReentrantLock unsafeReadReadyLock = new ReentrantLock();
    private boolean unsafeReadReady = false;

    BotWrapper(BWClientConfiguration configuration, BWEventListener eventListener) {
        this.configuration = configuration;
        this.eventListener = eventListener;
        frameBuffer = configuration.getAsync() ? new FrameBuffer(configuration) : null;
    }

    /**
     * Resets the BotWrapper for a new botGame.
     */
    void startNewGame(ByteBuffer liveData, PerformanceMetrics performanceMetrics) {
        if (configuration.getAsync()) {
            frameBuffer.initialize(liveData, performanceMetrics);
        }
        this.performanceMetrics = performanceMetrics;
        botGame = new Game();
        botGame.clientData().setBuffer(liveData);
        liveClientData.setBuffer(liveData);
        this.liveData = liveData;
        botThread = null;
        gameOver = false;
    }

    /**
     * @return The Game object used by the bot
     * In asynchronous mode this Game object may point at a copy of a previous frame.
     */
    Game getGame() {
        return botGame;
    }

    private boolean isUnsafeReadReady() {
        unsafeReadReadyLock.lock();
        try { return unsafeReadReady; }
        finally { unsafeReadReadyLock.unlock(); }
    }

    private void setUnsafeReadReady(boolean value) {
        unsafeReadReadyLock.lock();
        try { unsafeReadReady = value; }
        finally { unsafeReadReadyLock.unlock(); }
        frameBuffer.lockSize.lock();
        try {
            frameBuffer.conditionSize.signalAll();
        } finally {
            frameBuffer.lockSize.unlock();
        }
    }

    /**
     * Handles the arrival of a new frame from BWAPI
     */
    void onFrame() {
        if (configuration.getAsync()) {
            configuration.log("Main: onFrame asynchronous start");
            long startNanos = System.nanoTime();
            long endNanos = startNanos + configuration.getMaxFrameDurationMs() * 1000000;
            if (botThread == null) {
                configuration.log("Main: Starting bot thread");
                botThread = createBotThread();
                botThread.setName("JBWAPI Bot");
                // Reduced priority helps ensure that StarCraft.exe/BWAPI pick up on our frame completion in timely fashion
                botThread.setPriority(3);
                botThread.start();
            }

            // Unsafe mode:
            // If the frame buffer is empty (meaning the bot must be idle)
            // allow the bot to read directly from shared memory while we copy it over
            if (configuration.getAsyncUnsafe()) {
                frameBuffer.lockSize.lock();
                try {
                    if (frameBuffer.empty()) {
                        configuration.log("Main: Putting bot on live data");
                        botGame.clientData().setBuffer(liveData);
                        setUnsafeReadReady(true);
                    } else {
                        setUnsafeReadReady(false);
                    }
                } finally {
                    frameBuffer.lockSize.unlock();
                }
            }

            // Add a frame to buffer
            // If buffer is full, will wait until it has capacity.
            // Then wait for the buffer to empty or to run out of time in the frame.
            int frame = liveClientData.gameData().getFrameCount();
            configuration.log("Main: Enqueuing frame #" + frame);
            frameBuffer.enqueueFrame();

            configuration.log("Main: Enqueued frame #" + frame);
            if (frame > 0) {
                performanceMetrics.getClientIdle().startTiming();
            }
            frameBuffer.lockSize.lock();
            try {
                while (!frameBuffer.empty()) {
                    // Unsafe mode: Move the bot off of live data onto the frame buffer
                    // This is the unsafe step!
                    // We don't synchronize on calls which access the buffer
                    // (to avoid tens of thousands of synchronized calls per frame)
                    // so there's no guarantee of safety here.
                    if (configuration.getAsyncUnsafe() && frameBuffer.size() == 1) {
                        configuration.log("Main: Weaning bot off live data");
                        botGame.clientData().setBuffer(frameBuffer.peek());
                    }

                    // Make bot exceptions fall through to the main thread.
                    Throwable lastThrow = getLastBotThrow();
                    if (lastThrow != null) {
                        configuration.log("Main: Rethrowing bot throwable");
                        throw new RuntimeException(lastThrow);
                    }

                    if (configuration.getUnlimitedFrameZero() && frame == 0) {
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
                        long excessNanos = Math.max(0, (System.nanoTime() - endNanos) / 1000000);
                        performanceMetrics.getExcessSleep().record(excessNanos);
                    }
                }
            } catch(InterruptedException ignored) {
            } finally {
                frameBuffer.lockSize.unlock();
                performanceMetrics.getClientIdle().stopTiming();
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

                    boolean doUnsafeRead = false;
                    configuration.log("Bot: Ready for another frame");
                    performanceMetrics.getBotIdle().startTiming();
                    frameBuffer.lockSize.lock();
                    try {
                        doUnsafeRead = isUnsafeReadReady();
                        while ( ! doUnsafeRead && frameBuffer.empty()) {
                            configuration.log("Bot: Waiting for a frame");
                            frameBuffer.conditionSize.awaitUninterruptibly();
                            doUnsafeRead = isUnsafeReadReady();
                        }
                    } finally {
                        frameBuffer.lockSize.unlock();
                    }
                    performanceMetrics.getBotIdle().stopTiming();

                    if (doUnsafeRead) {
                        configuration.log("Bot: Reading live frame");
                        setUnsafeReadReady(false);
                        // TODO: Maybe we should point it at live data from here?
                    } else {
                        configuration.log("Bot: Peeking next frame from buffer");
                        botGame.clientData().setBuffer(frameBuffer.peek());
                    }

                    configuration.log("Bot: Handling events on frame #" + botGame.getFrameCount());
                    handleEvents();

                    configuration.log("Bot: Events handled. Dequeuing frame #" + botGame.getFrameCount());
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
        ClientData.GameData gameData = botGame.clientData().gameData();

        // Populate gameOver before invoking event handlers (in case the bot throws)
        for (int i = 0; i < gameData.getEventCount(); i++) {
            gameOver = gameOver || gameData.getEvents(i).getType() == EventType.MatchEnd;
        }

        if (configuration.getAsync()) {
            performanceMetrics.getFramesBehind().record(Math.max(1, frameBuffer.framesBuffered()) - 1);
        }

        performanceMetrics.getBotResponse().timeIf(
            ! gameOver && (gameData.getFrameCount() > 0 || ! configuration.getUnlimitedFrameZero()),
            () -> {
                for (int i = 0; i < gameData.getEventCount(); i++) {
                    EventHandler.operation(eventListener, botGame, gameData.getEvents(i));
                }
            });
    }
}
