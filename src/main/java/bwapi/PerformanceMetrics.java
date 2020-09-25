package bwapi;

/**
 * Collects various performance metrics.
 */
public class PerformanceMetrics {

    /**
     * Total duration of the frame from JBWAPI's perspective,
     * exclusive of time modifying shared memory to indicate frame completion.
     * Likely to be at least a little bit of an undercount from the perspective of BWAPI,
     * given that the tournament module is timing a superset of JBWAPI's execution time.
     */
    PerformanceMetric jbwapiFrameDuration;

    /**
     * Total duration of the frame from JBWAPI's perspective,
     * inclusive of time modifying shared memory to indicate frame completion.
     * Likely to be at least a little bit of an undercount from the perspective of BWAPI,
     * given that the tournament module is timing a superset of JBWAPI's execution time.
     */
    PerformanceMetric endToEndFrameDuration;

    /**
     * Time spent copying game data from system pipe shared memory to a frame buffer.
     * Applicable only in asynchronous mode.
     */
    PerformanceMetric copyingToBuffer;

    /**
     * Time spent intentionally blocking on bot operation due to a full frame buffer.
     * Applicable only in asynchronous mode.
     */
    PerformanceMetric intentionallyBlocking;

    /**
     * Number of frames backed up in the frame buffer, after enqueuing each frame (and not including the newest frame).
     * Applicable only in asynchronous mode.
     */
    PerformanceMetric frameBufferSize;

    /**
     * Number of frames behind real-time the bot is at the time it handles events.
     * Applicable only in asynchronous mode.
     */
    PerformanceMetric framesBehind;

    /**
     * Time spent applying bot commands to the live frame.
     */
    PerformanceMetric flushSideEffects;

    /**
     * Time spent waiting for bot event handlers to complete for a single frame.
     */
    PerformanceMetric botResponse;

    /**
     * Time spent waiting for a response from BWAPI; is likely reflective of the performance of any opponent bots.
     */
    PerformanceMetric bwapiResponse;

    /**
     * Time bot spends idle.
     * Applicable only in asynchronous mode.
     */
    PerformanceMetric botIdle;

    /**
     * Time the main thread spends idle, waiting for the bot to finish processing frames.
     * Applicable only in asynchronous mode.
     */
    PerformanceMetric clientIdle;

    /**
     * Time the main thread spends oversleeping its timeout target, potentially causing overtime frames.
     * Applicable only in asynchronous mode.
     */
    PerformanceMetric excessSleep;

    /**
     * Instances of System.nanoTime() measuring a longer frame duration with respect to WinAPI's GetTickCount.
     */
    PerformanceMetric positiveTimeDelta;

    /**
     * Instances of System.nanoTime() measuring a shorter frame duration with respect to WinAPI's GetTickCount.
     */
    PerformanceMetric negativeTimeDelta;

    /**
     * When Kernel32.INSTANCE.GetTickCount() returns at least one inexplicable value
     */
    PerformanceMetric weirdTimeDelta;

    private BWClientConfiguration configuration;

    PerformanceMetrics(BWClientConfiguration configuration) {
        this.configuration = configuration;
        reset();
    }

    public void reset() {
        jbwapiFrameDuration = new PerformanceMetric("JBWAPI frame duration", 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 85);
        endToEndFrameDuration = new PerformanceMetric("End-to-end frame duration", 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 85);
        copyingToBuffer = new PerformanceMetric("Time copying to buffer", 5, 10, 15, 20, 25, 30);
        intentionallyBlocking = new PerformanceMetric("Blocking with full buffer", 0);
        frameBufferSize = new PerformanceMetric("Frames buffered", 0, 1);
        framesBehind = new PerformanceMetric("Frames behind real-time", 0, 1);
        flushSideEffects = new PerformanceMetric("Flushing side effects", 1, 3, 5);
        botResponse = new PerformanceMetric("Bot event handlers", 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 85);
        bwapiResponse = new PerformanceMetric("Responses from BWAPI", 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 85);
        botIdle = new PerformanceMetric("Bot idle", Long.MAX_VALUE);
        clientIdle = new PerformanceMetric("Client idling", configuration.maxFrameDurationMs);
        excessSleep = new PerformanceMetric("Excess sleep", 1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 85);
        positiveTimeDelta = new PerformanceMetric("Positive timer delta", 1, 2, 3, 4, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 85);
        negativeTimeDelta = new PerformanceMetric("Negative timer delta", 1, 2, 3, 4, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 85);
        weirdTimeDelta = new PerformanceMetric("Weird timer delta");
    }

    @Override
    public String toString() {
        return "Performance metrics:"
            + "\n" + jbwapiFrameDuration.toString()
            + "\n" + endToEndFrameDuration.toString()
            + "\n" + copyingToBuffer.toString()
            + "\n" + intentionallyBlocking.toString()
            + "\n" + frameBufferSize.toString()
            + "\n" + framesBehind.toString()
            + "\n" + flushSideEffects.toString()
            + "\n" + botResponse.toString()
            + "\n" + bwapiResponse.toString()
            + "\n" + botIdle.toString()
            + "\n" + clientIdle.toString()
            + "\n" + excessSleep.toString()
            + "\n" + positiveTimeDelta.toString()
            + "\n" + negativeTimeDelta.toString()
            + "\n" + weirdTimeDelta.toString();
    }
}
