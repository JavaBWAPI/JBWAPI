package bwapi;

import java.text.DecimalFormat;

/**
 * Aggregates labeled time series data.
 */
public class PerformanceMetric {
    private final String name;
    private final long maxAllowed;

    public double minValue = Long.MAX_VALUE;
    public double maxValue = Long.MIN_VALUE;
    public double lastValue = 0;
    public double avgValue = 0;
    public double avgValueExceeding = 0;
    public int samples = 0;
    public int samplesExceeding = 0;
    public int interrupted = 0;

    private long timeStarted = 0;

    PerformanceMetric(String name, long maxAllowed) {
        this.name = name;
        this.maxAllowed = maxAllowed;
    }

    /**
     * Records the duration of a function call.
     * @param runnable The function to time
     */
    void time(Runnable runnable) {
        startTiming();
        runnable.run();
        stopTiming();
    }

    /**
     * Convenience method; calls a function; but only records the duration if a condition is met
     * @param condition Whether to record the function call duration
     * @param runnable The function to call
     */
    void timeIf(boolean condition, Runnable runnable) {
        if (condition) {
            time(runnable);
        } else {
            runnable.run();
        }
    }

    /**
     * Manually start timing.
     * The next call to stopTiming() will record the duration in fractional milliseconds.
     */
    void startTiming() {
        if (timeStarted > 0) {
            ++interrupted;
        }
        timeStarted = System.nanoTime();
    }


   /**
     * Manually stop timing.
     * If paired with a previous call to startTiming(), records the measured time between the calls in fractional milliseconds.
     */
    void stopTiming() {
        if (timeStarted <= 0) return;
        // Use nanosecond resolution timer, but record in units of milliseconds.
        long timeEnded = System.nanoTime();
        long timeDiff = timeEnded - timeStarted;
        timeStarted = 0;
        record(timeDiff / 1000000d);
    }

    /**
     * Manually records a specific value.
     */
    void record(double value) {
        lastValue = value;
        minValue = Math.min(minValue, value);
        maxValue = Math.max(maxValue, value);
        avgValue = (avgValue * samples + value) / (samples + 1d);
        ++samples;
        if (value > maxAllowed) {
            avgValueExceeding = (avgValueExceeding * samplesExceeding + value) / (samplesExceeding + 1d);
            ++samplesExceeding;
        }
    }

    /**
     * @return A pretty-printed description of the recorded values.
     */
    @Override
    public String toString() {
        DecimalFormat formatter = new DecimalFormat("###,###.#");
        return name
                + ": "
                + (samples > 0
                    ? formatter.format(samples)
                        + " samples averaging "
                        + formatter.format(avgValue)
                        + " ["
                        + formatter.format(minValue)
                        + " - "
                        + formatter.format(maxValue)
                        + "] over "
                        + samples
                        + " samples"
                        + (samplesExceeding > 0
                            ? ". "
                                + samplesExceeding
                                + " values over "
                                + maxAllowed
                                + " averaging "
                                + formatter.format(avgValueExceeding)
                            : "")
                    : "No samples")
                + (interrupted > 0
                    ? ". Interrupted " + interrupted + " times"
                    : "");
    }
}
