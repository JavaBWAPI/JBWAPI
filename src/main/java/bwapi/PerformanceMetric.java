package bwapi;

import java.text.DecimalFormat;

public class PerformanceMetric {
    private final String name;
    private final long maxThreshold;
    public final boolean timerEnabled;

    public double minValue = Long.MAX_VALUE;
    public double maxValue = Long.MIN_VALUE;
    public double avgValue = 0;
    public double avgValueExceeding = 0;
    public int samples = 0;
    public int samplesExceeding = 0;
    public int interrupted = 0;

    private long timeStarted = 0;

    PerformanceMetric(String name, long maxThreshold, boolean timerEnabled) {
        this.name = name;
        this.maxThreshold = maxThreshold;
        this.timerEnabled = timerEnabled;
    }

    void time(Runnable runnable) {
        startTiming();
        runnable.run();
        stopTiming();
    }

    void startTiming() {
        if (!timerEnabled) return;
        if (timeStarted > 0) {
            ++interrupted;
        }
        timeStarted = System.nanoTime();
    }

    void stopTiming() {
        if (!timerEnabled) return;
        if (timeStarted <= 0) return;
        // Use nanosecond resolution timer,
        // and record in units of milliseconds.
        long timeEnded = System.nanoTime();
        long timeDiff = timeEnded - timeStarted;
        timeStarted = 0;
        record(timeDiff / 1000d);
    }

    void record(double value) {
        minValue = Math.min(minValue, value);
        maxValue = Math.max(maxValue, value);
        avgValue = (avgValue * samples + value) / (samples + 1d);
        ++samples;
        if (value > maxThreshold) {
            avgValueExceeding = (avgValueExceeding * samplesExceeding + value) / (samplesExceeding + 1d);
            ++samplesExceeding;
        }
    }

    @Override
    public String toString() {
        DecimalFormat formatter = new DecimalFormat("###,###.#");
        return name
                + ": "
                + samples
                + " averaging "
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
                + (interrupted > 0
                    ? ". Interrupted " + interrupted + " times"
                    : "");
    }
}
