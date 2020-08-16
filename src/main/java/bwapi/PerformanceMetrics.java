package bwapi;

/**
 * When performance diagnostics are timersEnabled, collects various performance metrics.
 */
public class PerformanceMetrics {

    /**
     * Total duration of the frame from JBWAPI's perspective.
     * Likely to be at least a little bit of an undercount,
     * given that the tournament module is timing a superset of JBWAPI's execution time.
     */
    public PerformanceMetric totalFrameDuration;

    /**
     * Time spent copying game data from system pipe shared memory to a frame buffer.
     * Applicable only in asynchronous mode.
     */
    public PerformanceMetric copyingToBuffer;

    /**
     * Time spent intentionally blocking on bot operation due to a full frame buffer.
     * Applicable only in asynchronous mode.
     */
    public PerformanceMetric intentionallyBlocking;

    /**
     * Number of frames backed up in the frame buffer, after enqueuing each frame (and not including the newest frame).
     * Applicable only in asynchronous mode.
     */
    public PerformanceMetric frameBufferSize;

    /**
     * Number of frames behind real-time the bot is at the time it handles events.
     * Applicable only in asynchronous mode.
     */
    public PerformanceMetric framesBehind;

    /**
     * Time spent applying bot commands to the live frame.
     */
    public PerformanceMetric flushSideEffects;

    /**
     * Time spent waiting for bot event handlers to complete for a single frame.
     */
    public PerformanceMetric botResponse;

    /**
     * Time spent waiting for a response from BWAPI; is likely reflective of the performance of any opponent bots.
     */
    public PerformanceMetric bwapiResponse;

    /**
     * Time bot spends idle.
     * Applicable only in asynchronous mode.
     */
    public PerformanceMetric botIdle;

    private BWClientConfiguration configuration;

    public PerformanceMetrics(BWClientConfiguration configuration) {
        this.configuration = configuration;
        reset();
    }

    void reset() {
        final int frameDurationBufferMs = 5;
        final int sideEffectsBufferMs = 1;
        final int realTimeFrameMs = 42;
        totalFrameDuration = new PerformanceMetric("Total frame duration", configuration.maxFrameDurationMs + frameDurationBufferMs);
        copyingToBuffer = new PerformanceMetric("Time copying to buffer", 15);
        intentionallyBlocking = new PerformanceMetric("Intentionally blocking", 0);
        frameBufferSize = new PerformanceMetric("Frames buffered", 0);
        framesBehind = new PerformanceMetric("Frames behind", 0);
        flushSideEffects = new PerformanceMetric("Flush side effects", sideEffectsBufferMs );
        botResponse = new PerformanceMetric("Bot responses", configuration.maxFrameDurationMs);
        bwapiResponse = new PerformanceMetric("BWAPI responses", realTimeFrameMs);
        botIdle = new PerformanceMetric("Bot idle", Long.MAX_VALUE);
    }

    @Override
    public String toString() {
        return "Performance metrics:"
                + "\n" + totalFrameDuration.toString()
                + "\n" + copyingToBuffer.toString()
                + "\n" + intentionallyBlocking.toString()
                + "\n" + frameBufferSize.toString()
                + "\n" + framesBehind.toString()
                + "\n" + flushSideEffects.toString()
                + "\n" + botResponse.toString()
                + "\n" + bwapiResponse.toString()
                + "\n" + botIdle.toString();
    }
}
