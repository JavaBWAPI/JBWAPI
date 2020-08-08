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
     * Number of frames backed up in the frame buffer, after releasing each frame.
     * Applicable only in asynchronous mode.
     */
    public PerformanceMetric frameBufferSize;

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
     * Whether performance timers have been enabled in the JBWAPI configuration.
     * Timers are off by default to spare performance.
     */
    public final boolean timersEnabled;

    /**
     * Time bot spends idle.
     * Applicable only in asynchronous mode.
     */
    public PerformanceMetric botIdle;

    public PerformanceMetrics(BWClientConfiguration configuration) {
        timersEnabled = configuration.diagnosePerformance;
        final int frameDurationBufferMs = 5;
        final int sideEffectsBufferMs = 1;
        final int realTimeFrameMs = 42;
        totalFrameDuration = new PerformanceMetric("Total frame duration", configuration.asyncFrameDurationMs + frameDurationBufferMs, timersEnabled);
        copyingToBuffer = new PerformanceMetric("Time copying to buffer", 5, timersEnabled);
        intentionallyBlocking = new PerformanceMetric("Intentionally blocking", 0, timersEnabled);
        frameBufferSize = new PerformanceMetric("Frame buffer size", 0, timersEnabled);
        flushSideEffects = new PerformanceMetric("Flush side effects", sideEffectsBufferMs , timersEnabled);
        botResponse = new PerformanceMetric("Bot Responses", configuration.asyncFrameDurationMs, timersEnabled);
        bwapiResponse = new PerformanceMetric("BWAPI Responses", realTimeFrameMs, timersEnabled);
        botIdle = new PerformanceMetric("Bot idle", Long.MAX_VALUE, timersEnabled);
    }

    public String toString() {
        return "Performance metrics:"
                + "\n" + totalFrameDuration.toString()
                + "\n" + copyingToBuffer.toString()
                + "\n" + intentionallyBlocking.toString()
                + "\n" + frameBufferSize.toString()
                + "\n" + flushSideEffects.toString()
                + "\n" + botResponse.toString()
                + "\n" + bwapiResponse.toString();
    }
}
