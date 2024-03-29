package bwapi;

import org.junit.Test;

import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class SynchronizationTest {

    private void sleepUnchecked(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch(InterruptedException exception) {
            throw new RuntimeException(exception);
        }
    }

    private String describeApproximateExpectation(double expected, double actual, double margin) {
        return "Expected " + expected + " == " + actual + " +/- " + margin;
    }

    private boolean measureApproximateEquality(double expected, double actual, double margin) {
        return expected + margin >= actual && expected - margin <= actual;
    }

    private void assertWithin(String message, double expected, double actual, double margin) {
        assertTrue(
            message + ": " + describeApproximateExpectation(expected, actual, margin),
            measureApproximateEquality(expected, actual, margin));
    }

    @Test
    public void sync_IfException_ThrowException() {
        BWClientConfiguration config = new BWClientConfiguration.Builder()
                .withAsync(false)
                .build();
        SynchronizationEnvironment environment = new SynchronizationEnvironment(config);
        environment.onFrame(0, () -> { throw new RuntimeException("Simulated bot exception"); });
        assertThrows(RuntimeException.class, environment::runGame);
    }

    @Test
    public void async_IfException_ThrowException() {
        // An exception in the bot thread must be re-thrown by the main thread.
        BWClientConfiguration config = new BWClientConfiguration.Builder()
                .withAsync(true)
                .withAsyncFrameBufferCapacity(3)
                .build();
        SynchronizationEnvironment environment = new SynchronizationEnvironment(config);

        environment.onFrame(0, () -> { throw new RuntimeException("Simulated bot exception"); });
        assertThrows(RuntimeException.class, environment::runGame);
    }

    @Test
    public void sync_IfDelay_ThenNoBuffer() {
        BWClientConfiguration config = new BWClientConfiguration.Builder()
                .withAsync(false)
                .withMaxFrameDurationMs(1)
                .withAsyncFrameBufferCapacity(3)
                .build();
        SynchronizationEnvironment environment = new SynchronizationEnvironment(config);

        IntStream.range(0, 5).forEach(frame -> {
            environment.onFrame(frame, () -> {
                sleepUnchecked(5);
                assertEquals(0, environment.bwClient.framesBehind());
                assertEquals(frame, environment.bwClient.getGame().getFrameCount());
                assertEquals(frame, environment.liveGameData().getFrameCount());
            });
        });

        environment.runGame();
    }

    @Test
    public void async_IfBotDelay_ThenClientBuffers() {
        BWClientConfiguration config = new BWClientConfiguration.Builder()
                .withAsync(true)
                .withMaxFrameDurationMs(100)
                .withAsyncFrameBufferCapacity(4)
                .build();
        SynchronizationEnvironment environment = new SynchronizationEnvironment(config);

        environment.onFrame(1, () -> {
            sleepUnchecked(500);
            assertEquals("Bot should be observing an old frame", 1, environment.bwClient.getGame().getFrameCount());
            assertEquals("Client should be as far ahead as the frame buffer allows", 5, environment.liveGameData().getFrameCount());
            assertEquals("Bot should be behind the live game", 4, environment.bwClient.framesBehind());
        });

        environment.onFrame(6, () -> { // Maybe it should be possible to demand that these assertions pass a frame earlier?
            assertEquals("Bot should be observing the live frame", 6, environment.bwClient.getGame().getFrameCount());
            assertEquals("Client should not be ahead of the bot", 6, environment.liveGameData().getFrameCount());
            assertEquals("Bot should not be behind the live game", 0, environment.bwClient.framesBehind());
        });

        environment.runGame();
    }

    @Test
    public void async_IfBotDelay_ThenClientStalls() {
        BWClientConfiguration config = new BWClientConfiguration.Builder()
                .withAsync(true)
                .withMaxFrameDurationMs(200)
                .withAsyncFrameBufferCapacity(5)
                .build();
        SynchronizationEnvironment environment = new SynchronizationEnvironment(config);

        environment.onFrame(1, () -> {
            sleepUnchecked(500);
            assertEquals("3: Bot should be observing an old frame", 1, environment.bwClient.getGame().getFrameCount());
            assertEquals("3: Client should have progressed as slowly as possible", 3, environment.liveGameData().getFrameCount());
            assertEquals("3: Bot should be behind the live game by as little as possible", 2, environment.bwClient.framesBehind());
            sleepUnchecked(200);
            assertEquals("4: Bot should be observing an old frame", 1, environment.bwClient.getGame().getFrameCount());
            assertEquals("4: Client should have progressed as slowly as possible", 4, environment.liveGameData().getFrameCount());
            assertEquals("4: Bot should be behind the live game by as little as possible", 3, environment.bwClient.framesBehind());
            sleepUnchecked(200);
            assertEquals("5: Bot should be observing an old frame", 1, environment.bwClient.getGame().getFrameCount());
            assertEquals("5: Client should have progressed as slowly as possible", 5, environment.liveGameData().getFrameCount());
            assertEquals("5: Bot should be behind the live game by as little as possible", 4, environment.bwClient.framesBehind());
        });

        environment.runGame();
    }

    @Test
    public void async_IfFrameZeroWaitsEnabled_ThenAllowInfiniteTime() {
        BWClientConfiguration config = new BWClientConfiguration.Builder()
                .withAsync(true)
                .withUnlimitedFrameZero(true)
                .withMaxFrameDurationMs(5)
                .withAsyncFrameBufferCapacity(2)
                .build();
        SynchronizationEnvironment environment = new SynchronizationEnvironment(config);

        environment.onFrame(0, () -> {
            sleepUnchecked(50);
            assertEquals("Bot should still be on frame zero", 0, environment.bwClient.getGame().getFrameCount());
            assertEquals("Client should still be on frame zero", 0, environment.liveGameData().getFrameCount());
            assertEquals("Bot should not be behind the live game", 0, environment.bwClient.framesBehind());
        });

        environment.runGame(2);
    }

    @Test
    public void async_IfFrameZeroWaitsDisabled_ThenClientBuffers() {
        BWClientConfiguration config = new BWClientConfiguration.Builder()
                .withAsync(true)
                .withUnlimitedFrameZero(false)
                .withMaxFrameDurationMs(5)
                .withAsyncFrameBufferCapacity(2)
                .build();
        SynchronizationEnvironment environment = new SynchronizationEnvironment(config);


        environment.onFrame(0, () -> {
            sleepUnchecked(50);
            assertEquals("Bot should still be on frame zero", 0, environment.bwClient.getGame().getFrameCount());
            assertEquals("Client should have advanced to the next frame", 2, environment.liveGameData().getFrameCount());
            assertEquals("Bot should be behind the live game", 2, environment.bwClient.framesBehind());
        });

        environment.runGame(2);
    }

    @Test
    public void async_MeasurePerformance_CopyingToBuffer() {
        // Somewhat lazy test; just verify that we're getting sane values
        BWClientConfiguration config = new BWClientConfiguration.Builder()
                .withAsync(true)
                .build();
        SynchronizationEnvironment environment = new SynchronizationEnvironment(config);
        environment.runGame(20);
        final double minObserved = 0.25;
        final double maxObserved = 15;
        final double meanObserved = (minObserved + maxObserved) / 2;
        final double rangeObserved = (maxObserved - minObserved) / 2;
        assertWithin("Copy to buffer: minimum", environment.metrics().getCopyingToBuffer().getRunningTotal().getMin(), meanObserved, rangeObserved);
        assertWithin("Copy to buffer: maximum", environment.metrics().getCopyingToBuffer().getRunningTotal().getMax(), meanObserved, rangeObserved);
        assertWithin("Copy to buffer: average", environment.metrics().getCopyingToBuffer().getRunningTotal().getMean(), meanObserved, rangeObserved);
        assertWithin("Copy to buffer: previous", environment.metrics().getCopyingToBuffer().getRunningTotal().getLast(), meanObserved, rangeObserved);
    }

    @Test
    public void async_MeasurePerformance_FrameBufferSizeAndFramesBehind() {
        BWClientConfiguration config = new BWClientConfiguration.Builder()
                .withAsync(true)
                .withUnlimitedFrameZero(true)
                .withAsyncFrameBufferCapacity(3)
                .withMaxFrameDurationMs(20)
                .build();
        SynchronizationEnvironment environment = new SynchronizationEnvironment(config);

        environment.onFrame(5, () -> {
            assertWithin("5: Frame buffer average", 0, environment.metrics().getFrameBufferSize().getRunningTotal().getMean(), 0.1);
            assertWithin("5: Frame buffer minimum", 0, environment.metrics().getFrameBufferSize().getRunningTotal().getMin(), 0.1);
            assertWithin("5: Frame buffer maximum", 0, environment.metrics().getFrameBufferSize().getRunningTotal().getMax(), 0.1);
            assertWithin("5: Frame buffer previous", 0, environment.metrics().getFrameBufferSize().getRunningTotal().getLast(), 0.1);
            assertWithin("5: Frames behind average", 0, environment.metrics().getFramesBehind().getRunningTotal().getMean(), 0.1);
            assertWithin("5: Frames behind minimum", 0, environment.metrics().getFramesBehind().getRunningTotal().getMin(), 0.1);
            assertWithin("5: Frames behind maximum", 0, environment.metrics().getFramesBehind().getRunningTotal().getMax(), 0.1);
            assertWithin("5: Frames behind previous", 0, environment.metrics().getFramesBehind().getRunningTotal().getLast(), 0.1);
            sleepUnchecked(200);
        });
        environment.onFrame(6, () -> {
            assertWithin("6: Frame buffer average", 1 / 6.0 + 2 / 7.0, environment.metrics().getFrameBufferSize().getRunningTotal().getMean(), 0.1);
            assertWithin("6: Frame buffer minimum", 0, environment.metrics().getFrameBufferSize().getRunningTotal().getMin(), 0.1);
            assertWithin("6: Frame buffer maximum", 2, environment.metrics().getFrameBufferSize().getRunningTotal().getMax(), 0.1);
            assertWithin("6: Frame buffer previous", 2, environment.metrics().getFrameBufferSize().getRunningTotal().getLast(), 0.1);
            assertWithin("6: Frames behind average", 1 / 6.0, environment.metrics().getFramesBehind().getRunningTotal().getMean(), 0.1);
            assertWithin("6: Frames behind minimum", 0, environment.metrics().getFramesBehind().getRunningTotal().getMin(), 0.1);
            assertWithin("6: Frames behind maximum", 1, environment.metrics().getFramesBehind().getRunningTotal().getMax(), 0.1);
            assertWithin("6: Frames behind previous", 1, environment.metrics().getFramesBehind().getRunningTotal().getLast(), 0.1);
        });

        environment.runGame(8);
    }

    /**
     * Number of milliseconds of leeway to give in potentially noisy performance metrics.
     * Increase if tests are flaky due to variance in execution speed.
     */
    private final static long MS_MARGIN = 20;

    @Test
    public void MeasurePerformance_BotResponse() {
        // Frame zero appears to take an extra 60ms, so let's disable timing for it
        // (and also verify that we omit frame zero from performance metrics)
        BWClientConfiguration config = new BWClientConfiguration.Builder()
                .withUnlimitedFrameZero(true)
                .build();
        SynchronizationEnvironment environment = new SynchronizationEnvironment(config);

        environment.onFrame(1, () -> {
            sleepUnchecked(100);
        });
        environment.onFrame(2, () -> {
            assertWithin("2: Bot response average", 100, environment.metrics().getBotResponse().getRunningTotal().getMean(), MS_MARGIN);
            assertWithin("2: Bot response minimum", 100, environment.metrics().getBotResponse().getRunningTotal().getMin(), MS_MARGIN);
            assertWithin("2: Bot response maximum", 100, environment.metrics().getBotResponse().getRunningTotal().getMax(), MS_MARGIN);
            assertWithin("2: Bot response previous", 100, environment.metrics().getBotResponse().getRunningTotal().getLast(), MS_MARGIN);
            sleepUnchecked(300);
        });
        environment.onFrame(3, () -> {
            assertWithin("3: Bot response average", 200, environment.metrics().getBotResponse().getRunningTotal().getMean(), MS_MARGIN);
            assertWithin("3: Bot response minimum", 100, environment.metrics().getBotResponse().getRunningTotal().getMin(), MS_MARGIN);
            assertWithin("3: Bot response maximum", 300, environment.metrics().getBotResponse().getRunningTotal().getMax(), MS_MARGIN);
            assertWithin("3: Bot response previous", 300, environment.metrics().getBotResponse().getRunningTotal().getLast(), MS_MARGIN);
            sleepUnchecked(200);
        });

        environment.runGame(4);

        assertWithin("Final: Bot response average", 200, environment.metrics().getBotResponse().getRunningTotal().getMean(), MS_MARGIN);
        assertWithin("Final: Bot response minimum", 100, environment.metrics().getBotResponse().getRunningTotal().getMin(), MS_MARGIN);
        assertWithin("Final: Bot response maximum", 300, environment.metrics().getBotResponse().getRunningTotal().getMax(), MS_MARGIN);
        assertWithin("Final: Bot response previous", 200, environment.metrics().getBotResponse().getRunningTotal().getLast(), MS_MARGIN);
    }

    @Test
    public void MeasurePerformance_BotIdle() {
        final long bwapiDelayMs = 10;
        final int frames = 10;
        BWClientConfiguration config = new BWClientConfiguration.Builder()
                .withAsync(true)
                .withAsyncFrameBufferCapacity(3)
                .withUnlimitedFrameZero(true)
                .build();
        SynchronizationEnvironment environment = new SynchronizationEnvironment(config);

        environment.setBwapiDelayMs(bwapiDelayMs);
        environment.runGame(frames);
        double expected = environment.metrics().getCopyingToBuffer().getRunningTotal().getMean() + bwapiDelayMs;
        assertWithin("Bot Idle: Average", environment.metrics().getBotIdle().getRunningTotal().getMean(), expected, MS_MARGIN);
    }

    @Test
    public void async_MeasurePerformance_IntentionallyBlocking() {
        BWClientConfiguration config = new BWClientConfiguration.Builder()
                .withAsync(true)
                .withUnlimitedFrameZero(true)
                .withAsyncFrameBufferCapacity(2)
                .withMaxFrameDurationMs(20)
                .build();
        SynchronizationEnvironment environment = new SynchronizationEnvironment(config);

        final int frameDelayMs = 100;
        environment.onFrame(1, () -> {
            sleepUnchecked(100);
        });
        environment.onFrame(2, () -> {
            assertWithin(
                "2: Intentionally blocking previous",
                environment.metrics().getIntentionallyBlocking().getRunningTotal().getLast(),
                frameDelayMs - environment.configuration.getAsyncFrameBufferCapacity() * environment.configuration.getMaxFrameDurationMs(),
                MS_MARGIN);
            sleepUnchecked(100);
        });
        environment.runGame(3);
    }

    @Test
    public void async_DisablesLatencyCompensation() {
        BWClientConfiguration config = new BWClientConfiguration.Builder()
                .withAsync(false)
                .build();
        SynchronizationEnvironment environmentSync = new SynchronizationEnvironment(config);
        environmentSync.onFrame(1, () -> { assertTrue(environmentSync.bwClient.getGame().isLatComEnabled()); });
        environmentSync.runGame(2);

        BWClientConfiguration configAsync = new BWClientConfiguration.Builder()
                .withAsync(true)
                .withAsyncFrameBufferCapacity(2)
                .build();
        SynchronizationEnvironment environmentAsync = new SynchronizationEnvironment(configAsync);
        environmentAsync.onFrame(1, () -> { assertFalse(environmentAsync.bwClient.getGame().isLatComEnabled()); });
        environmentAsync.onFrame(2, () -> { assertThrows(IllegalStateException.class, () -> environmentAsync.bwClient.getGame().setLatCom(true)); });
        environmentAsync.runGame(3);
    }
}
