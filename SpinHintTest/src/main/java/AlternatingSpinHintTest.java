/**
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */

import org.HdrHistogram.Histogram;
import org.performancehints.SpinHint;

import java.util.Locale;

public class AlternatingSpinHintTest {
    public static final long WARMUP_ITERATIONS = 500L * 1000L;
    public static final long ITERATIONS = 100L * 1000L * 1000L;

    public static volatile long spinData; // even: ready to produce; odd: ready to consume; -3: terminate

    public static final Histogram noHintLatencyHistogram = new Histogram(3600L * 1000L * 1000L * 1000L, 2);
    public static final Histogram withHintLatencyHistogram = new Histogram(3600L * 1000L * 1000L * 1000L, 2);

    static class Producer extends Thread {
        final long iterations;
        Producer(final long terminatingIterationCount) {
            this.iterations = terminatingIterationCount;
        }
        public void run() {
            for (long i = 0; i < iterations; i++) {

                // Measure latency with spin hint:
                long prevTime = System.nanoTime();
                spinData++; // produce

                while ((spinData & 0x1) == 1) {
                    // busy spin until ready to produce
                    SpinHint.spinLoopHint();
                }
                long currTime = System.nanoTime();
                withHintLatencyHistogram.recordValue(currTime - prevTime);

                // Measure latency with no spin hint:
                prevTime = System.nanoTime();
                spinData++; // produce

                while ((spinData & 0x1) == 1) {
                    // busy spin until ready to produce
                }
                currTime = System.nanoTime();
                noHintLatencyHistogram.recordValue(currTime - prevTime);

            }

            // Signal consumer to terminate:
            // First make sure we are ready to produce, to avoid racing
            // with (and getting stomped by) consumer's ++ op:
            while ((spinData & 0x1) == 1);
            // Now, knowing that consumer is not concurrently modifying
            // spinData, we can signal to terminate:
            spinData = -3;
        }
    }

    static class Consumer extends Thread {
        public void run() {
            while (spinData >= 0) {
                while ((spinData & 0x1) == 0) {
                    // busy spin until ready to consume
                    SpinHint.spinLoopHint();
                }
                spinData++; // consume
            }
        }
    }

    public static void main(final String[] args) {
        try {
            spinData = 0;
            Thread consumer = new Consumer();
            consumer.setDaemon(true);
            consumer.start();

            Thread producer = new Producer(WARMUP_ITERATIONS);
            producer.start();
            producer.join();
            consumer.join();
            noHintLatencyHistogram.reset();
            withHintLatencyHistogram.reset();

            Thread.sleep(1000); // Let things (like JIT compilations) settle down.
            System.out.println("# Warmup done. Restarting threads.");

            spinData = 0;
            consumer = new Consumer();
            consumer.setDaemon(true);
            consumer.start();

            producer = new Producer(ITERATIONS);

            long start = System.nanoTime();
            producer.start();
            producer.join();
            consumer.join();

            long duration = System.nanoTime() - start;

            System.out.println("# Round trip latency histogram WITH NO spin hint:");
            noHintLatencyHistogram.outputPercentileDistribution(System.out, 5, 1.0);

            System.out.println("# Round trip latency histogram WITH spin hint:");
            withHintLatencyHistogram.outputPercentileDistribution(System.out, 5, 1.0);

            System.out.println("# duration = " + duration);
            System.out.println("# duration (ns) per op = " + duration / (ITERATIONS));
            System.out.println("# op/sec = " +
                    (ITERATIONS * 1000L * 1000L * 1000L) / duration);

            System.out.format(Locale.US, "# Percentile  No Hint      With Hint\n");
            System.out.format(Locale.US, "# 50%%'ile:    %8d ns   %8d ns\n",
                    noHintLatencyHistogram.getValueAtPercentile(50.0),
                    withHintLatencyHistogram.getValueAtPercentile(50.0));
            System.out.format(Locale.US, "# 90%%'ile:    %8d ns   %8d ns\n",
                    noHintLatencyHistogram.getValueAtPercentile(90.0),
                    withHintLatencyHistogram.getValueAtPercentile(90.0));
            System.out.format(Locale.US, "# 99%%'ile:    %8d ns   %8d ns\n",
                    noHintLatencyHistogram.getValueAtPercentile(99.0),
                    withHintLatencyHistogram.getValueAtPercentile(99.0));
            System.out.format(Locale.US, "# 99.9%%'ile:  %8d ns   %8d ns\n",
                    noHintLatencyHistogram.getValueAtPercentile(99.9),
                    withHintLatencyHistogram.getValueAtPercentile(99.9));

        } catch (InterruptedException ex) {
            System.err.println("SpinHintTest interrupted.");
        }
    }

}
