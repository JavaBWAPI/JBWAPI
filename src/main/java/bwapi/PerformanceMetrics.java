package bwapi;

/**
 * Collects various performance metrics.
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

    public PerformanceMetric frameDuration5;
    public PerformanceMetric frameDuration10;
    public PerformanceMetric frameDuration15;
    public PerformanceMetric frameDuration20;
    public PerformanceMetric frameDuration25;
    public PerformanceMetric frameDuration30;
    public PerformanceMetric frameDuration35;
    public PerformanceMetric frameDuration40;
    public PerformanceMetric frameDuration45;
    public PerformanceMetric frameDuration50;
    public PerformanceMetric frameDuration55;

    private BWClientConfiguration configuration;

    public PerformanceMetrics(BWClientConfiguration configuration) {
        this.configuration = configuration;
        reset();
    }

    void reset() {
        final int sideEffectsBufferMs = 1;
        final int realTimeFrameMs = 42;
        totalFrameDuration = new PerformanceMetric("JBWAPI frame duration", configuration.maxFrameDurationMs + 5);
        copyingToBuffer = new PerformanceMetric("Time copying to buffer", configuration.maxFrameDurationMs + 5);
        intentionallyBlocking = new PerformanceMetric("Blocking with full buffer", 0);
        frameBufferSize = new PerformanceMetric("Frames buffered", 0);
        framesBehind = new PerformanceMetric("Frames behind real-time", 0);
        flushSideEffects = new PerformanceMetric("Flushing side effects", sideEffectsBufferMs );
        botResponse = new PerformanceMetric("Bot event handlers", configuration.maxFrameDurationMs);
        bwapiResponse = new PerformanceMetric("Responses from BWAPI", realTimeFrameMs);
        botIdle = new PerformanceMetric("Bot idle", Long.MAX_VALUE);
        frameDuration5 = new PerformanceMetric("JBWAPI frame @  5ms", 5);
        frameDuration10 = new PerformanceMetric("JBWAPI frame @ 10ms", 10);
        frameDuration15 = new PerformanceMetric("JBWAPI frame @ 15ms", 15);
        frameDuration20 = new PerformanceMetric("JBWAPI frame @ 20ms", 20);
        frameDuration25 = new PerformanceMetric("JBWAPI frame @ 25ms", 25);
        frameDuration30 = new PerformanceMetric("JBWAPI frame @ 30ms", 30);
        frameDuration35 = new PerformanceMetric("JBWAPI frame @ 35ms", 35);
        frameDuration40 = new PerformanceMetric("JBWAPI frame @ 40ms", 40);
        frameDuration45 = new PerformanceMetric("JBWAPI frame @ 45ms", 45);
        frameDuration50 = new PerformanceMetric("JBWAPI frame @ 50ms", 50);
        frameDuration55 = new PerformanceMetric("JBWAPI frame @ 55ms", 55);
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
                + "\n" + botIdle.toString()
                + "\n" + frameDuration5.toString()
                + "\n" + frameDuration10.toString()
                + "\n" + frameDuration15.toString()
                + "\n" + frameDuration20.toString()
                + "\n" + frameDuration25.toString()
                + "\n" + frameDuration30.toString()
                + "\n" + frameDuration35.toString()
                + "\n" + frameDuration40.toString()
                + "\n" + frameDuration45.toString()
                + "\n" + frameDuration50.toString()
                + "\n" + frameDuration55.toString();
    }
}
