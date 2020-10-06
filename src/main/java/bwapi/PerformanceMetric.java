package bwapi;

import com.sun.jna.platform.win32.Kernel32;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Aggregates labeled time series data.
 */
public class PerformanceMetric {
    public class RunningTotal {
        private int samples = 0;
        private double last = 0d;
        private double mean = 0d;
        private double min = Long.MAX_VALUE;
        private double max = Long.MIN_VALUE;
        void record(double value) {
            last = value;
            min = Math.min(min, value);
            max = Math.max(max, value);
            mean = (mean * samples + value) / (samples + 1d);
            ++samples;
        }
        public double getSamples() {
            return samples;
        }
        public double getLast() {
            return last;
        }
        public double getMean() {
            return mean;
        }
        public double getMin() {
            return min;
        }
        public double getMax() {
            return max;
        }
    }
    class Threshold {
        double threshold;
        RunningTotal runningTotal = new RunningTotal();
        Threshold(double value) {
            threshold = value;
        }
        void record(double value) {
            if (value >= threshold) {
                runningTotal.record(value);
            }
        }
        public String toString() {
            if (runningTotal.samples <= 0) {
                return "";
            }
            DecimalFormat formatter = new DecimalFormat("###,###.#");
            return "\n>= " + formatter.format(threshold) + ": " + runningTotal.samples + " samples averaging " + formatter.format(runningTotal.mean);
        }
    }

    private final String name;
    private long timeStarted = 0;
    private int interrupted = 0;

    private final RunningTotal runningTotal = new RunningTotal();
    private ArrayList<Threshold> thresholds = new ArrayList<>();

    PerformanceMetric(PerformanceMetrics metrics, String name, double... thresholds) {
        this.name = name;
        for (double threshold : thresholds) {
            this.thresholds.add(new Threshold(threshold));
        }
        metrics.addMetric(this);
    }

    public RunningTotal getRunningTotal() {
        return runningTotal;
    }

    public int getInterrupted() {
        return interrupted;
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
        runningTotal.record(value);
        thresholds.forEach(threshold -> threshold.record(value));
    }

    /**
     * @return A pretty-printed description of the recorded values.
     */
    @Override
    public String toString() {
        if (runningTotal.samples <= 0) {
            return name + ": No samples.";
        }
        DecimalFormat formatter = new DecimalFormat("###,###.#");
        String output = name
                + ":\n"
                + formatter.format(runningTotal.samples)
                    + " samples averaging "
                    + formatter.format(runningTotal.mean)
                    + " ["
                    + formatter.format(runningTotal.min)
                    + " - "
                    + formatter.format(runningTotal.max)
                    + "]";
        for (Threshold threshold : thresholds) {
            output += threshold.toString();
        }
        if (interrupted > 0) {
            output += "\n\tInterrupted " + interrupted + " times";
        }
        return output;
    }
}
