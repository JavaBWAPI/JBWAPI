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
    public PerformanceMetric getFrameDurationReceiveToSend() {
        return frameDurationReceiveToSend;
    }
    private PerformanceMetric frameDurationReceiveToSend;

    /**
     * Duration of the frame cycle steps measured by BWAPI,
     * from receiving a frame to BWAPI
     * to sending commands back
     * *inclusive* of the time spent sending commands back.
     */
    public PerformanceMetric getFrameDurationReceiveToSent() {
        return frameDurationReceiveToSent;
    }
    private PerformanceMetric frameDurationReceiveToSent;

    /**
     * Duration of a frame cycle originating at
     * the time when JBWAPI observes a new frame in shared memory.
     * Uses GetTickCount() instead of System.nanoRime()
     */
    public PerformanceMetric getFrameDurationReceiveToSentGTC() {
        return frameDurationReceiveToSentGTC;
    }
    private PerformanceMetric frameDurationReceiveToSentGTC;

    /**
     * Duration of a frame cycle originating at
     * the time when JBWAPI observes a new frame in shared memory.
     */
    public PerformanceMetric getFrameDurationReceiveToReceive() {
        return frameDurationReceiveToReceive;
    }
    private PerformanceMetric frameDurationReceiveToReceive;

    /**
     * Duration of a frame cycle originating at
     * the time when JBWAPI observes a new frame in shared memory.
     * Uses GetTickCount() instead of System.nanoRime()
     */
    public PerformanceMetric getFrameDurationReceiveToReceiveGTC() {
        return frameDurationReceiveToReceiveGTC;
    }
    private PerformanceMetric frameDurationReceiveToReceiveGTC;

    /**
     * Time spent copying game data from system pipe shared memory to a frame buffer.
     * Applicable only in asynchronous mode.
     */
    public PerformanceMetric getCopyingToBuffer() {
        return copyingToBuffer;
    }
    private PerformanceMetric copyingToBuffer;

    /**
     * Time spent intentionally blocking on bot operation due to a full frame buffer.
     * Applicable only in asynchronous mode.
     */
    public PerformanceMetric getIntentionallyBlocking() {
        return intentionallyBlocking;
    }
    private PerformanceMetric intentionallyBlocking;

    /**
     * Number of frames backed up in the frame buffer, after enqueuing each frame (and not including the newest frame).
     * Applicable only in asynchronous mode.
     */
    public PerformanceMetric getFrameBufferSize() {
        return frameBufferSize;
    }
    private PerformanceMetric frameBufferSize;

    /**
     * Number of frames behind real-time the bot is at the time it handles events.
     * Applicable only in asynchronous mode.
     */
    public PerformanceMetric getFramesBehind() {
        return framesBehind;
    }
    private PerformanceMetric framesBehind;

    /**
     * Time spent applying bot commands to the live frame.
     */
    public PerformanceMetric getFlushSideEffects() {
        return flushSideEffects;
    }
    private PerformanceMetric flushSideEffects;

    /**
     * Time spent waiting for bot event handlers to complete for a single frame.
     */
    public PerformanceMetric getBotResponse() {
        return botResponse;
    }
    private PerformanceMetric botResponse;

    /**
     * Time spent waiting for a response from BWAPI,
     * inclusive of the time spent sending the signal to BWAPI
     * and the time spent waiting for and receiving it.
     */
    public PerformanceMetric getCommunicationSendToReceive() {
        return communicationSendToReceive;
    }
    private PerformanceMetric communicationSendToReceive;

    /**
     * Time spent sending the "frame complete" signal to BWAPI.
     * Significant durations would indicate something blocking writes to shared memory.
     */
    public PerformanceMetric getCommunicationSendToSent() {
        return communicationSendToSent;
    }
    private PerformanceMetric communicationSendToSent;

    /**
     * Time spent waiting for a "frame ready" signal from BWAPI.
     * This time likely additional response time spent by other bots and StarCraft itself.
     */
    public PerformanceMetric getCommunicationListenToReceive() {
        return communicationListenToReceive;
    }
    private PerformanceMetric communicationListenToReceive;

    /**
     * Time bot spends idle.
     * Applicable only in asynchronous mode.
     */
    public PerformanceMetric getBotIdle() {
        return botIdle;
    }
    private PerformanceMetric botIdle;

    /**
     * Time the main thread spends idle, waiting for the bot to finish processing frames.
     * Applicable only in asynchronous mode.
     */
    public PerformanceMetric getClientIdle() {
        return clientIdle;
    }
    private PerformanceMetric clientIdle;

    /**
     * Time the main thread spends oversleeping its timeout target, potentially causing overtime frames.
     * Applicable only in asynchronous mode.
     */
    public PerformanceMetric getExcessSleep() {
        return excessSleep;
    }
    private PerformanceMetric excessSleep;

    /**
     * The number of events sent by BWAPI each frame.
     * Helps detect use of broken BWAPI 4.4 tournament modules, with respect to:
     * - https://github.com/bwapi/bwapi/issues/860
     * - https://github.com/davechurchill/StarcraftAITournamentManager/issues/42
     */
    public PerformanceMetric getNumberOfEvents() {
        return numberOfEvents;
    }
    private PerformanceMetric numberOfEvents;

    /**
     * The number of events sent by BWAPI each frame,
     * multiplied by the duration of time spent on that frame (receive-to-sent).
     * Helps detect use of broken BWAPI 4.4 tournament modules, with respect to:
     * - https://github.com/bwapi/bwapi/issues/860
     * - https://github.com/davechurchill/StarcraftAITournamentManager/issues/42
     */
    public PerformanceMetric getNumberOfEventsTimesDurationReceiveToSent() {
        return numberOfEventsTimesDurationReceiveToSent;
    }
    PerformanceMetric numberOfEventsTimesDurationReceiveToSent;

    /**
     * The number of events sent by BWAPI each frame,
     * multiplied by the duration of time spent on that frame (receive-to-sent),
     * and using GetTickCount() instead of System.nanoTime().
     * Helps detect use of broken BWAPI 4.4 tournament modules, with respect to:
     *      * - https://github.com/bwapi/bwapi/issues/860
     *      * - https://github.com/davechurchill/StarcraftAITournamentManager/issues/42
     */
    public PerformanceMetric getNumberOfEventsTimesDurationReceiveToSentGTC() {
        return numberOfEventsTimesDurationReceiveToSentGTC;
    }
    PerformanceMetric numberOfEventsTimesDurationReceiveToSentGTC;

    private BWClientConfiguration configuration;
    private ArrayList<PerformanceMetric> performanceMetrics = new ArrayList<>();

    PerformanceMetrics(BWClientConfiguration configuration) {
        this.configuration = configuration;
        reset();
    }

    /**
     * Clears all tracked data and starts counting from a blank slate.
     */
    public void reset() {
        performanceMetrics.clear();
        frameDurationReceiveToSend = new PerformanceMetric(this, "Frame duration: After receiving 'frame ready' -> before sending 'frame done'", 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 85);
        frameDurationReceiveToSent = new PerformanceMetric(this, "Frame duration: After receiving 'frame ready' -> after sending 'frame done'", 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 85);
        frameDurationReceiveToSentGTC = new PerformanceMetric(this, "Frame duration: After receiving 'frame ready' -> after sending 'frame done' (Using GetTickCount())", 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 85).useGetTickCount();
        frameDurationReceiveToReceive = new PerformanceMetric(this, "Frame duration: After receiving 'frame ready' -> receiving next 'frame ready'", 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 85);
        frameDurationReceiveToReceiveGTC = new PerformanceMetric(this, "Frame duration: After receiving 'frame ready' -> receiving next 'frame ready' (Using GetTickCount())", 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 85).useGetTickCount();
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
        numberOfEvents = new PerformanceMetric(this, "Number of events received from BWAPI", 1, 2, 3, 4, 5, 6, 8, 10, 15, 20);
        numberOfEventsTimesDurationReceiveToSent = new PerformanceMetric(this, "Number of events received from BWAPI, multiplied by the receive-to-sent duration of that frame", 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 85);
        numberOfEventsTimesDurationReceiveToSentGTC = new PerformanceMetric(this, "Number of events received from BWAPI, multiplied by the receive-to-sent duration of that frame (Using GetTickCount())", 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 85);
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

