package bwapi;

/**
 * Manages invocation of bot event handlers
 */
class BotWrapperAsync extends BotWrapper {
    private final ClientData liveClientData = new ClientData();
    private final FrameBuffer frameBuffer;
    private WrappedBuffer liveData;
    private Thread botThread;
    private volatile Throwable lastBotThrow;
    private boolean unsafeReadReady = false;

    BotWrapperAsync(BWClientConfiguration configuration, BWEventListener eventListener) {
        super(configuration, eventListener);
        frameBuffer = new FrameBuffer(configuration);

        // Use reduced priority to encourage Windows to give priority to StarCraft.exe/BWAPI.
        // If BWAPI doesn't get priority, it may not detect completion of a frame on our end in timely fashion.
        Thread.currentThread().setName("JBWAPI Client");
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY - 1);
    }

    @Override
    void startNewGame(WrappedBuffer liveData, PerformanceMetrics performanceMetrics) {
        frameBuffer.initialize(liveData, performanceMetrics);

        this.performanceMetrics = performanceMetrics;
        ClientDataAsync clientDataAsync = new ClientDataAsync(liveData);
        botGame = new Game(clientDataAsync);
        botGame.setConfiguration(configuration);
        botGame.botClientData().setBuffer(liveData);
        gameOver = false;

        liveClientData.setBuffer(liveData);
        this.liveData = liveData;

        configuration.log("Main: Starting bot thread");
        botThread = createBotThread();
        botThread.setName("JBWAPI Bot");
        // Reduced priority helps ensure that StarCraft.exe/BWAPI pick up on our frame completion in timely fashion
        botThread.setPriority(Thread.NORM_PRIORITY - 2);
        botThread.start();
    }


    @Override
    void onFrame() {
        configuration.log("Main: onFrame asynchronous start");
        asyncOnFrame();
        configuration.log("Main: onFrame asynchronous end");
    }

    void asyncOnFrame() {
        long startNanos = System.nanoTime();
        long endNanos = startNanos + (long) configuration.getMaxFrameDurationMs() * 1000000;

        // Unsafe mode:
        // If the frame buffer is empty (meaning the bot must be idle)
        // allow the bot to read directly from shared memory while we copy it over
        if (configuration.getAsyncUnsafe()) {
            frameBuffer.lockSize.lock();
            try {
                if (frameBuffer.empty()) {
                    configuration.log("Main: Putting bot on live data");
                    botGame.botClientData().setBuffer(liveData);
                    unsafeReadReady = true;
                    frameBuffer.conditionSize.signalAll();
                } else {
                    unsafeReadReady = false;
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
                        // Unsafe mode: Move the bot off of live data onto the frame buffer
                        // only when the frame times out and control is about to return to StarCraft.
                        // When the bot finishes on time, liveData remains frozen for the entire frame.
                        if (configuration.getAsyncUnsafe() && frameBuffer.size() == 1) {
                            configuration.log("Main: Weaning bot off live data");
                            unsafeReadReady = false;
                            botGame.botClientData().setBuffer(frameBuffer.peek());
                        }
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
        }
    }

    /**
     * Allows an asynchronous bot time to finish operation
     */
    @Override
    void endGame() {
        if (botThread != null) {
            try {
                botThread.join();
            } catch (InterruptedException ignored) {}
        }
    }

    Throwable getLastBotThrow() {
        return lastBotThrow;
    }

    private Thread createBotThread() {
        return new Thread(() -> {
            try {
                configuration.log("Bot: Thread started");
                while (!gameOver) {
                    configuration.log("Bot: Ready for another frame");
                    performanceMetrics.getBotIdle().startTiming();
                    frameBuffer.lockSize.lock();
                    try {
                        while (!unsafeReadReady && frameBuffer.empty()) {
                            configuration.log("Bot: Waiting for a frame");
                            frameBuffer.conditionSize.awaitUninterruptibly();
                        }
                        performanceMetrics.getBotIdle().stopTiming();

                        if (unsafeReadReady) {
                            configuration.log("Bot: Reading live frame");
                            unsafeReadReady = false;
                        } else {
                            configuration.log("Bot: Peeking next frame from buffer");
                            botGame.botClientData().setBuffer(frameBuffer.peek());
                        }
                    } finally {
                        frameBuffer.lockSize.unlock();
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
                lastBotThrow = throwable;

                // Awaken any threads waiting on bot progress
                while (!frameBuffer.empty()) {
                    frameBuffer.dequeue();
                }
            }
        });
    }

    @Override
    protected void handleEvents() {
        performanceMetrics.getFramesBehind().record(Math.max(1, frameBuffer.framesBuffered()) - 1);
        super.handleEvents();
    }
}
