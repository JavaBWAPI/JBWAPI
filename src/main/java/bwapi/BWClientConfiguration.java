package bwapi;

/**
 * Configuration for constructing a BWClient
 */
public final class BWClientConfiguration {
    public final static BWClientConfiguration DEFAULT = new BWClientConfiguration();

    private boolean debugConnection = false;
    private boolean autoContinue = false;
    private boolean unlimitedFrameZero = true;
    private int maxFrameDurationMs = 40;
    private boolean async = false;
    private int asyncFrameBufferCapacity = 10;
    private boolean asyncUnsafe = false;
    private boolean logVerbosely = false;

    /**
     * Use the Builder to build a valid BWClientConfiguration object.
     */
    private BWClientConfiguration() {}
    public static class Builder {
        final BWClientConfiguration bwClientConfiguration = new BWClientConfiguration();

        /**
         * Set to `true` for more explicit error messages (which might spam the terminal).
         */
        public Builder withDebugConnection(boolean value) {
            bwClientConfiguration.debugConnection = value;
            return this;
        }

        /**
         * When true, restarts the client loop when a game ends, allowing the client to play multiple games without restarting.
         */
        public Builder withAutoContinue(boolean value) {
            bwClientConfiguration.autoContinue = value;
            return this;
        }

        /**
         * Most bot tournaments allow bots to take an indefinite amount of time on frame #0 (the first frame of the game) to analyze the map and load data,
         * as the bot has no prior access to BWAPI or game information.
         *
         * This flag indicates that taking arbitrarily long on frame zero is acceptable.
         * Performance metrics omit the frame as an outlier.
         * Asynchronous operation will block until the bot's event handlers are complete.
         */
        public Builder withUnlimitedFrameZero(boolean value) {
            bwClientConfiguration.unlimitedFrameZero = value;
            return this;
        }

        /**
         * The maximum amount of time the bot is supposed to spend on a single frame.
         * In asynchronous mode, JBWAPI will attempt to let the bot use up to this much time to process all frames before returning control to BWAPI.
         * In synchronous mode, JBWAPI is not empowered to prevent the bot to exceed this amount, but will record overruns in performance metrics.
         * Real-time human play typically uses the "fastest" game speed, which has 42.86ms (42,860ns) between frames.
         */
        public Builder withMaxFrameDurationMs(int value) {
            if (value < 0) {
                throw new IllegalArgumentException("maxFrameDurationMs needs to be a non-negative number (it's how long JBWAPI waits for a bot response before returning control to BWAPI).");
            }
            bwClientConfiguration.maxFrameDurationMs = value;
            return this;
        }

        /**
         * Runs the bot in asynchronous mode. Asynchronous mode helps attempt to ensure that the bot adheres to real-time performance constraints.
         *
         * Humans playing StarCraft (and some tournaments) expect bots to return commands within a certain period of time; ~42ms for humans ("fastest" game speed),
         * and some tournaments enforce frame-wise time limits (at time of writing, 55ms for COG and AIIDE; 85ms for SSCAIT).
         *
         * Asynchronous mode invokes bot event handlers in a separate thread, and if all event handlers haven't returned by a specified period of time,
         * returns control to StarCraft, allowing the game to proceed while the bot continues to step in the background. This increases the likelihood of meeting
         * real-time performance requirements, while not fully guaranteeing it (subject to the whims of the JVM thread scheduler), at a cost of the bot possibly
         * issuing commands later than intended, and a marginally larger memory footprint.
         *
         * Asynchronous mode is not compatible with latency compensation. Enabling asynchronous mode automatically disables latency compensation.
         */
        public Builder withAsync(boolean value) {
            bwClientConfiguration.async = value;
            return this;
        }

        /**
         * The maximum number of frames to buffer while waiting on a bot.
         * Each frame buffered adds about 33 megabytes to JBWAPI's memory footprint.
         */
        public Builder withAsyncFrameBufferCapacity(int size) {
            if (size < 1) {
                throw new IllegalArgumentException("asyncFrameBufferCapacity needs to be a positive number (There needs to be at least one frame buffer).");
            }
            bwClientConfiguration.asyncFrameBufferCapacity = size;
            return this;
        }

        /**
         * Enables thread-unsafe async mode.
         * In this mode, the bot is allowed to read directly from shared memory until shared memory has been copied into the frame buffer,
         * at which point the bot switches to using the frame buffer.
         * This should enhance performance by allowing the bot to act while the frame is copied, but poses unidentified risk due to
         * the non-thread-safe switch from shared memory reads to frame buffer reads.
         */
        public Builder withAsyncUnsafe(boolean value) {
            bwClientConfiguration.asyncUnsafe = value;
            return this;
        }

        /**
         * Toggles verbose logging, particularly of synchronization steps.
         */
        public Builder withLogVerbosely(boolean value) {
            bwClientConfiguration.logVerbosely = value;
            return this;
        }

        public BWClientConfiguration build() {
            if (bwClientConfiguration.asyncUnsafe && ! bwClientConfiguration.async) {
                throw new IllegalArgumentException("asyncUnsafe mode needs async mode.");
            }
            return bwClientConfiguration;
        }
    }

    public boolean getDebugConnection() {
        return debugConnection;
    }

    public boolean getAutoContinue() {
        return autoContinue;
    }

    public boolean getUnlimitedFrameZero() {
        return unlimitedFrameZero;
    }

    public int getMaxFrameDurationMs() {
        return maxFrameDurationMs;
    }

    public boolean getAsync() {
        return async;
    }

    public int getAsyncFrameBufferCapacity() {
        return asyncFrameBufferCapacity;
    }

    public boolean getAsyncUnsafe() {
        return asyncUnsafe;
    }

    public boolean getLogVerbosely() {
        return logVerbosely;
    }

    void log(String value) {
        if (logVerbosely) {
            System.out.println(value);
        }
    }
}