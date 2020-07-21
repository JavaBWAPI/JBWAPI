package bwapi;

/**
 * Configuration for constructing a BWClient
 */
public class BWClientConfiguration {

    /**
     * Set to `true` for more explicit error messages (which might spam the terminal).
     */
    public boolean debugConnection;

    /**
     * When true, restarts the client loop when a game ends, allowing the client to play multiple games without restarting.
     */
    public boolean autoContinue = false;

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
    public boolean async = false;

    /**
     * If JBWAPI detects that this much time (in nanoseconds) has passed since a bot's event handlers began, returns control back to BWAPI.
     * Real-time human play typically uses the "fastest" game speed, which has 42.86ms (42,860ns) between frames.
     */
    public int asyncFrameDurationNanos = 40000;

    /**
     * The maximum number of frames to buffer while waiting on a bot.
     * Each frame buffered adds about 33 megabytes to JBWAPI's memory footprint.
     */
    public int asyncFrameBufferSize = 10;

    /**
     * Most bot tournaments allow bots to take an indefinite amount of time on frame #0 (the first frame of the game) to analyze the map and load data,
     * as the bot has no prior access to BWAPI or game information.
     *
     * This flag causes JBWAPI to wait for the bot's event handlers to return on the first frame of the game, even if operating in asynchronous mode,
     * respecting the time bots are typically allowed on frame 0.
     */
    public boolean asyncWaitOnFrameZero = true;

    /**
     * Checks that the configuration is in a valid state. Throws an IllegalArgumentException if it isn't.
     */
    public void validate() {
        if (async && asyncFrameDurationNanos < 0) {
            throw new IllegalArgumentException("asyncFrameDurationNanos needs to be a non-negative number (it's how long JBWAPI waits for a bot response before returning control to BWAPI).");
        }
        if (async && asyncFrameBufferSize < 1) {
            throw new IllegalArgumentException("asyncFrameBufferSize needs to be a positive number (There needs to be at least one frame buffer).");
        }
    }
}