package bwapi;

import java.text.DecimalFormat;

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

    void time(Runnable runnable) {
        startTiming();
        runnable.run();
        stopTiming();
    }

    void startTiming() {
        if (timeStarted > 0) {
            ++interrupted;
        }
        timeStarted = System.nanoTime();
    }

    void stopTiming() {
        if (timeStarted <= 0) return;
        // Use nanosecond resolution timer, but record in units of milliseconds.
        long timeEnded = System.nanoTime();
        long timeDiff = timeEnded - timeStarted;
        timeStarted = 0;
        record(timeDiff / 1000000d);
    }

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
                                + " violations averaging "
                                + formatter.format(avgValueExceeding)
                            : "")
                    : "No samples")
                + (interrupted > 0
                    ? ". Interrupted " + interrupted + " times"
                    : "");
    }
}
