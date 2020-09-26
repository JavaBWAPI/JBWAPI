package bwapi;

import java.util.ArrayList;

/**
 * Collects various performance metrics.
 */
public class PerformanceMetrics {

    /**
     * Duration of the frame cycle steps measured by BWAPI,
     * from receiving a frame to BWAPI
     * to sending commands back
     * *exclusive* of the time spent sending commands back.
     */
    PerformanceMetric frameDurationReceiveToSend;

    /**
     * Duration of the frame cycle steps measured by BWAPI,
     * from receiving a frame to BWAPI
     * to sending commands back
     * *inclusive* of the time spent sending commands back.
     */
    PerformanceMetric frameDurationReceiveToSent;

    /**
     * Duration of a frame cycle originating at
     * the time when JBWAPI observes a new frame in shared memory.
     */
    PerformanceMetric frameDurationReceiveToReceive;

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
     * Time spent waiting for a response from BWAPI,
     * inclusive of the time spent sending the signal to BWAPI
     * and the time spent waiting for and receiving it.
     */
    PerformanceMetric communicationSendToReceive;

    /**
     * Time spent sending the "frame complete" signal to BWAPI.
     * Significant durations would indicate something blocking writes to shared memory.
     */
    PerformanceMetric communicationSendToSent;

    /**
     * Time spent waiting for a "frame ready" signal from BWAPI.
     * This time likely additional response time spent by other bots and StarCraft itself.
     */
    PerformanceMetric communicationListenToReceive;

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
     * Instances of System.nanoTime() measuring a longer frame duration with respect to WinAPI's GetTickCount  which BWAPI uses up to 4.4.
     */
    PerformanceMetric positiveTimeDelta;

    /**
     * Instances of System.nanoTime() measuring a shorter frame duration with respect to WinAPI's GetTickCount which BWAPI uses up to 4.4.
     */
    PerformanceMetric negativeTimeDelta;

    private BWClientConfiguration configuration;
    private ArrayList<PerformanceMetric> performanceMetrics = new ArrayList<>();

    PerformanceMetrics(BWClientConfiguration configuration) {
        this.configuration = configuration;
        reset();
    }

    public void reset() {
        performanceMetrics.clear();
        frameDurationReceiveToSend = new PerformanceMetric(this, "Frame duration: Receiving 'frame ready' -> before sending 'frame done'", 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 85);
        frameDurationReceiveToSent = new PerformanceMetric(this, "Frame duration: Receiving 'frame ready' -> after sending 'frame done'", 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 85);
        frameDurationReceiveToReceive = new PerformanceMetric(this, "Frame duration: From BWAPI receive to BWAPI receive", 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 85);
        communicationSendToReceive = new PerformanceMetric(this, "BWAPI duration: Before sending 'frame done' -> After receiving 'frame ready'", 1, 3, 5, 10, 15, 20, 30);
        communicationSendToSent = new PerformanceMetric(this, "BWAPI duration: Before sending 'frame done' -> After sending 'frame done'", 1, 3, 5, 10, 15, 20, 30);
        communicationListenToReceive = new PerformanceMetric(this, "BWAPI duration: Before listening for 'frame ready' -> After receiving 'frame ready'", 1, 3, 5, 10, 15, 20, 30);
        copyingToBuffer = new PerformanceMetric(this, "Copying frame to buffer", 5, 10, 15, 20, 25, 30);
        intentionallyBlocking = new PerformanceMetric(this, "Time holding frame until buffer frees capacity", 0);
        frameBufferSize = new PerformanceMetric(this, "Frames already buffered when enqueuing a new frame", 0, 1);
        framesBehind = new PerformanceMetric(this, "Frames behind real-time when handling events", 0, 1);
        flushSideEffects = new PerformanceMetric(this, "Time flushing side effects", 1, 3, 5);
        botResponse = new PerformanceMetric(this, "Duration of bot event handlers", 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 85);
        botIdle = new PerformanceMetric(this, "Time bot spent idle", Long.MAX_VALUE);
        clientIdle = new PerformanceMetric(this, "Time client spent waiting for bot", configuration.maxFrameDurationMs);
        excessSleep = new PerformanceMetric(this, "Excess duration of client sleep", 1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 85);
        positiveTimeDelta = new PerformanceMetric(this, "Positive timer discrepancy compared to BWAPI", 1, 2, 3, 4, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 85);
        negativeTimeDelta = new PerformanceMetric(this, "Negative timer discrepancy compared to BWAPI", 1, 2, 3, 4, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 85);
    }

    void addMetric(PerformanceMetric performanceMetric) {
        performanceMetrics.add(performanceMetric);
    }

    @Override
    public String toString() {
        StringBuilder outputBuilder = new StringBuilder();
        outputBuilder.append("Performance metrics:");
        performanceMetrics.forEach(metric -> {
            outputBuilder.append("\n");
            outputBuilder.append(metric.toString());
        });
        return outputBuilder.toString();
    }
}
