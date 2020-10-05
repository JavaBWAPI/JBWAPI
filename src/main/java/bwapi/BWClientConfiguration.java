package bwapi;

/**
 * Configuration for constructing a BWClient
 */
public class BWClientConfiguration {

    /**
     * Set to `true` for more explicit error messages (which might spam the terminal).
     */
    public BWClientConfiguration withDebugConnection(boolean value) {
        debugConnection = value;
        return this;
    }
    boolean getDebugConnection() {
        return debugConnection;
    }
    private boolean debugConnection;

    /**
     * When true, restarts the client loop when a game ends, allowing the client to play multiple games without restarting.
     */
    public BWClientConfiguration withAutoContinue(boolean value) {
        autoContinue = value;
        return this;
    }
    boolean getAutoContinue() {
        return autoContinue;
    }
    private boolean autoContinue = false;

    /**
     * Most bot tournaments allow bots to take an indefinite amount of time on frame #0 (the first frame of the game) to analyze the map and load data,
     * as the bot has no prior access to BWAPI or game information.
     *
     * This flag indicates that taking arbitrarily long on frame zero is acceptable.
     * Performance metrics omit the frame as an outlier.
     * Asynchronous operation will block until the bot's event handlers are complete.
     */
    public BWClientConfiguration withUnlimitedFrameZero(boolean value) {
        unlimitedFrameZero = value;
        return this;
    }
    boolean getUnlimitedFrameZero() {
        return unlimitedFrameZero;
    }
    private boolean unlimitedFrameZero = true;

    /**
     * The maximum amount of time the bot is supposed to spend on a single frame.
     * In asynchronous mode, JBWAPI will attempt to let the bot use up to this much time to process all frames before returning control to BWAPI.
     * In synchronous mode, JBWAPI is not empowered to prevent the bot to exceed this amount, but will record overruns in performance metrics.
     * Real-time human play typically uses the "fastest" game speed, which has 42.86ms (42,860ns) between frames.
     */
    public BWClientConfiguration withMaxFrameDurationMs(int value) {
        maxFrameDurationMs = value;
        return this;
    }
    int getMaxFrameDurationMs() {
        return maxFrameDurationMs;
    }
    private int maxFrameDurationMs = 40;

    /**
     * Runs the bot in asynchronous mode. Asynchronous mode helps attempt to ensure that the bot adheres to real-time performance constraints.
     *
     * Humans playing StarCraft (and some tournaments) expect bots to return commands within a certain period of time; ~42ms for humans ("fastesT" game speed),
     * and some tournaments enforce frame-wise time limits (at time of writing, 55ms for COG and AIIDE; 85ms for SSCAIT).
     *
     * Asynchronous mode invokes bot event handlers in a separate thread, and if all event handlers haven't returned by a specified period of time, sends an
     * returns control to StarCraft, allowing the game to proceed while the bot continues to step in the background. This increases the likelihood of meeting
     * real-time performance requirements, while not fully guaranteeing it (subject to the whims of the JVM thread scheduler), at a cost of the bot possibly
     * issuing commands later than intended, and a marginally larger memory footprint.
     */
    public BWClientConfiguration withAsync(boolean value) {
        async = value;
        return this;
    }
    boolean getAsync() {
        return async;
    }
    private boolean async = false;

    /**
     * The maximum number of frames to buffer while waiting on a bot.
     * Each frame buffered adds about 33 megabytes to JBWAPI's memory footprint.
     */
    public BWClientConfiguration withAsyncFrameBufferCapacity(int size) {
        asyncFrameBufferCapacity = size;
        return this;
    }
    int getAsyncFrameBufferCapacity() {
        return asyncFrameBufferCapacity;
    }
    private int asyncFrameBufferCapacity = 10;

    /**
     * Enables thread-unsafe async mode.
     * In this mode, the bot is allowed to read directly from shared memory until shared memory has been copied into the frame buffer,
     * at wihch point the bot switches to using the frame buffer.
     * This should enhance performance by allowing the bot to act while the frame is copied, but poses unidentified risk due to
     * the non-thread-safe switc from shared memory reads to frame buffer reads.
     */
    public BWClientConfiguration withAsyncUnsafe(boolean value) {
        asyncUnsafe = value;
        return this;
    }
    boolean getAsyncUnsafe() {
        return asyncUnsafe;
    }
    private boolean asyncUnsafe = false;

    /**
     * Toggles verbose logging, particularly of synchronization steps.
     */
    public BWClientConfiguration withLogVerbosely(boolean value) {
        logVerbosely = value;
        return this;
    }
    boolean getLogVerbosely() {
        return logVerbosely;
    }
    private boolean logVerbosely = false;

    /**
     * Checks that the configuration is in a valid state. Throws an IllegalArgumentException if it isn't.
     */
    void validate() {
        if (asyncUnsafe && ! async) {
            throw new IllegalArgumentException("asyncUnsafe mode needs async mode.");
        }
        if (async && maxFrameDurationMs < 0) {
            throw new IllegalArgumentException("maxFrameDurationMs needs to be a non-negative number (it's how long JBWAPI waits for a bot response before returning control to BWAPI).");
        }
        if (async && asyncFrameBufferCapacity < 1) {
            throw new IllegalArgumentException("asyncFrameBufferCapacity needs to be a positive number (There needs to be at least one frame buffer).");
        }
    }

    void log(String value) {
        if (logVerbosely) {
            System.out.println(value);
        }
    }
}