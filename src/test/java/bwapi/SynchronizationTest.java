package bwapi;

import org.junit.Test;

import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class SynchronizationTest {

    private void sleepUnchecked(int milliseconds) {
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

    private void assertWithin(double expected, double actual, double margin) {
        assertTrue(
                describeApproximateExpectation(expected, actual, margin),
                measureApproximateEquality(expected, actual, margin));
    }

    private void assertWithin(String message, double expected, double actual, double margin) {
        assertTrue(
                message + ": " + describeApproximateExpectation(expected, actual, margin),
                measureApproximateEquality(expected, actual, margin));
    }

    @Test
    public void sync_IfException_ThrowException() throws InterruptedException {
        SynchronizationEnvironment environment = new SynchronizationEnvironment();
        environment.configuration.async = false;
        environment.onFrame(0, () -> { throw new RuntimeException("Simulated bot exception"); });
        assertThrows(RuntimeException.class, environment::runGame);
    }

    @Test
    public void async_IfException_ThrowException() throws InterruptedException {
        // An exception in the bot thread must be re-thrown by the main thread.
        SynchronizationEnvironment environment = new SynchronizationEnvironment();
        environment.configuration.async = true;
        environment.configuration.asyncFrameBufferSize = 3;
        environment.onFrame(0, () -> { throw new RuntimeException("Simulated bot exception"); });
        assertThrows(RuntimeException.class, environment::runGame);
    }

    @Test
    public void sync_IfDelay_ThenNoBuffer() throws InterruptedException {
        SynchronizationEnvironment environment = new SynchronizationEnvironment();
        environment.configuration.async = false;
        environment.configuration.asyncFrameDurationMs = 1;
        environment.configuration.asyncFrameBufferSize = 3;

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
    public void async_IfBotDelay_ThenClientBuffers() throws InterruptedException {
        SynchronizationEnvironment environment = new SynchronizationEnvironment();
        environment.configuration.async = true;
        environment.configuration.asyncFrameDurationMs = 10;
        environment.configuration.asyncFrameBufferSize = 4;

        environment.onFrame(1, () -> {
            sleepUnchecked(40);
            assertEquals("Bot should be observing an old frame", 1, environment.bwClient.getGame().getFrameCount());
            assertEquals("Client should be as far ahead as the frame buffer allows", 4, environment.liveGameData().getFrameCount());
            assertEquals("Bot should be behind the live game", 3, environment.bwClient.framesBehind());
        });

        environment.onFrame(6, () -> { // Maybe it should be possible to demand that these assertions pass a frame earlier?
            assertEquals("Bot should be observing the live frame", 6, environment.bwClient.getGame().getFrameCount());
            assertEquals("Client should not be ahead of the bot", 6, environment.liveGameData().getFrameCount());
            assertEquals("Bot should not be behind the live game", 0, environment.bwClient.framesBehind());
        });

        environment.runGame();
    }

    @Test
    public void async_IfBotDelay_ThenClientStalls() throws InterruptedException {
        SynchronizationEnvironment environment = new SynchronizationEnvironment();
        environment.configuration.async = true;
        environment.configuration.asyncFrameDurationMs = 50;
        environment.configuration.asyncFrameBufferSize = 5;

        environment.onFrame(1, () -> {
            sleepUnchecked(125);
            assertEquals("3: Bot should be observing an old frame", 1, environment.bwClient.getGame().getFrameCount());
            assertEquals("3: Client should have progressed as slowly as possible", 3, environment.liveGameData().getFrameCount());
            assertEquals("3: Bot should be behind the live game by as little as possible", 2, environment.bwClient.framesBehind());
            sleepUnchecked(50);
            assertEquals("4: Bot should be observing an old frame", 1, environment.bwClient.getGame().getFrameCount());
            assertEquals("4: Client should have progressed as slowly as possible", 4, environment.liveGameData().getFrameCount());
            assertEquals("4: Bot should be behind the live game by as little as possible", 3, environment.bwClient.framesBehind());
            sleepUnchecked(50);
            assertEquals("5: Bot should be observing an old frame", 1, environment.bwClient.getGame().getFrameCount());
            assertEquals("5: Client should have progressed as slowly as possible", 5, environment.liveGameData().getFrameCount());
            assertEquals("5: Bot should be behind the live game by as little as possible", 4, environment.bwClient.framesBehind());
        });

        environment.runGame();
    }

    @Test
    public void async_IfFrameZeroWaitsEnabled_ThenAllowInfiniteTime() throws InterruptedException {
        SynchronizationEnvironment environment = new SynchronizationEnvironment();
        environment.configuration.async = true;
        environment.configuration.unlimitedFrameZero = true;
        environment.configuration.asyncFrameDurationMs = 5;
        environment.configuration.asyncFrameBufferSize = 2;

        environment.onFrame(0, () -> {
            sleepUnchecked(50);
            assertEquals("Bot should still be on frame zero", 0, environment.bwClient.getGame().getFrameCount());
            assertEquals("Client should still be on frame zero", 0, environment.liveGameData().getFrameCount());
            assertEquals("Bot should not be behind the live game", 0, environment.bwClient.framesBehind());
        });

        environment.runGame();
    }

    @Test
    public void async_IfFrameZeroWaitsDisabled_ThenClientBuffers() throws InterruptedException {
        SynchronizationEnvironment environment = new SynchronizationEnvironment();
        environment.configuration.async = true;
        environment.configuration.unlimitedFrameZero = false;
        environment.configuration.asyncFrameDurationMs = 5;
        environment.configuration.asyncFrameBufferSize = 2;

        environment.onFrame(0, () -> {
            sleepUnchecked(50);
            assertEquals("Bot should still be on frame zero", 0, environment.bwClient.getGame().getFrameCount());
            assertEquals("Client should have advanced to the next frame", 2, environment.liveGameData().getFrameCount());
            assertEquals("Bot should be behind the live game", 2, environment.bwClient.framesBehind());
        });

        environment.runGame();
    }

    @Test
    public void async_MeasurePerformance_TotalFrameDuration() {

    }

    @Test
    public void async_MeasurePerformance_CopyingToBuffer() {

    }

    @Test
    public void async_MeasurePerformance_IntentionallyBlocking() {

    }

    @Test
    public void async_MeasurePerformance_FrameBufferSize() {

    }

    @Test
    public void async_MeasurePerformance_FlushSideEffects() {

    }

    /**
     * Number of milliseconds of leeway to give in performance metrics.
     * Increase if tests are flaky due to variance in execution speed.
     */
    private final static long MS_MARGIN = 10;

    @Test
    public void async_MeasurePerformance_BotResponse() {
        SynchronizationEnvironment environment = new SynchronizationEnvironment();

        // Frame zero appears to take an extra 60ms, so let's disable timing for it
        // (and also verify that we omit frame zero from performance metrics)
        environment.configuration.unlimitedFrameZero = true;

        environment.onFrame(1, () -> {
            sleepUnchecked(100);
        });
        environment.onFrame(2, () -> {
            assertWithin("2: Bot response average", 100, environment.metrics().botResponse.avgValue, MS_MARGIN);
            assertWithin("2: Bot response minimum", 100, environment.metrics().botResponse.minValue, MS_MARGIN);
            assertWithin("2: Bot response maximum", 100, environment.metrics().botResponse.maxValue, MS_MARGIN);
            assertWithin("2: Bot response previous", 100, environment.metrics().botResponse.lastValue, MS_MARGIN);
            sleepUnchecked(300);
        });
        environment.onFrame(3, () -> {
            assertWithin("3: Bot response average", 200, environment.metrics().botResponse.avgValue, MS_MARGIN);
            assertWithin("3: Bot response minimum", 100, environment.metrics().botResponse.minValue, MS_MARGIN);
            assertWithin("3: Bot response maximum", 300, environment.metrics().botResponse.maxValue, MS_MARGIN);
            assertWithin("3: Bot response previous", 300, environment.metrics().botResponse.lastValue, MS_MARGIN);
            sleepUnchecked(200);
        });

        environment.runGame(4);

        assertWithin("Final: Bot response average", 200, environment.metrics().botResponse.avgValue, MS_MARGIN);
        assertWithin("Final: Bot response minimum", 100, environment.metrics().botResponse.minValue, MS_MARGIN);
        assertWithin("Final: Bot response maximum", 300, environment.metrics().botResponse.maxValue, MS_MARGIN);
        assertWithin("Final: Bot response previous", 200, environment.metrics().botResponse.lastValue, MS_MARGIN);
    }

    @Test
    public void async_MeasurePerformance_BwapiResponse() {

    }

    @Test
    public void async_MeasurePerformance_BotIdle() {

    }


}
