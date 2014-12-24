/**
 * AbstractHistogramIterator.java
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 */

public class TimeIntervalEstimator {

    private final StatsWindow timeIntervals; // including outliers
    private final StatsWindow timeIntervalsWithoutOutliers;

    long totalCount = 0;

    private final int windowSize;
    private final int countBeforeCompensation;

    public TimeIntervalEstimator(final int windowSize, int countBeforeCompensation) {
        this.windowSize = windowSize;
        this.countBeforeCompensation = countBeforeCompensation;
        this.timeIntervals = new StatsWindow(windowSize);
        this.timeIntervalsWithoutOutliers = new StatsWindow(windowSize);
    }

    public void recordTimeInterval(long interval) {
        double threshold = timeIntervals.getAverage() + (3.5 * timeIntervals.getStandardDeviation());

        timeIntervals.addValue(interval);

        if ((interval <= threshold) || (totalCount <= countBeforeCompensation)) {
            timeIntervalsWithoutOutliers.addValue(interval);
        }

        totalCount++;
    }

    public long getAverageInterval() {
        return (long) timeIntervals.getAverage();
    }


    public long getAverageIntervalWithoutOutliers() {
        return (long) timeIntervalsWithoutOutliers.getAverage();
    }

    public boolean isEstablishedAverage() {
        return (totalCount > countBeforeCompensation);
    }

    public class StatsWindow {
        private final int windowSize;
        private final double window[];
        private int totalCount = 0;

        private double sumOfValuesInWindow = 0.0;
        private double sumOfSquaresOfValuesInWindow = 0.0;

        private double average = 0.0;

        public StatsWindow(int size) {
            this.windowSize = size;
            window = new double[size];
        }

        public void addValue(double value) {
            int prevPosition = totalCount % windowSize;

            double prevValue = window[prevPosition];
            window[prevPosition] = value;

            totalCount++;
            int numberOfValuesInWindow = (totalCount > windowSize) ? windowSize : (prevPosition + 1);

            sumOfValuesInWindow -= prevValue;
            sumOfValuesInWindow += value;
            average = sumOfValuesInWindow / numberOfValuesInWindow;

            sumOfSquaresOfValuesInWindow -= prevValue * prevValue;
            sumOfSquaresOfValuesInWindow += value * value;
        }

        public double getAverage() {
            return average;
        }

        public double getStandardDeviation() {
            int numberOfValuesInWindow = (totalCount > windowSize) ? windowSize : (totalCount % windowSize);

            double numerator = numberOfValuesInWindow * sumOfSquaresOfValuesInWindow - Math.pow(sumOfValuesInWindow, 2);
            long denominator = numberOfValuesInWindow * numberOfValuesInWindow;

            return Math.sqrt(numerator / denominator);
        }
    }
}
