/**
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */

import org.HdrHistogram.Histogram;
import org.performancehints.Runtime;

import java.util.Locale;

/**
 * And alternating variant of SpinHintTest: Each thread alternates between spinning with a Runtime.onSpinWait()
 * call in the loop and spinning without a hint. Comparing the behavior with alternating hinting loops to
 * the behavior with hints in all loops (or no hints at all) may provide some insight about the cause of
 * the latency benefits derived on a given platform (assuming such benefits have been shown with
 * Runtime.onSpinWait()).
 *
 * E.g. if the latency behavior with alternating hints does not differ dramatically from the behavior with
 * hints always on, it could be postulated that the benefit derives from increased core frequency (and that a
 * 1 in 2 loops hinting is enough to produce the same power savings to allow the same higher core frequency).
 * On the other hand, if the behavior with alternating hint loops does differ significantly from non-alternating
 * ones, the benfit might be caused by something else (like some sort of reduction in the amount of speculative
 * state that needs to be unrrolled when existing the loop).
 */
public class AlternatingSpinHintTest {
    public static final long WARMUP_PASS_COUNT = 5;
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
                    Runtime.onSpinWait();
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
                    Runtime.onSpinWait();
                }
                spinData++; // consume
            }
        }
    }

    public static void main(final String[] args) {
        try {

            Thread producer;
            Thread consumer;

            for (int i = 0; i < WARMUP_PASS_COUNT; i++) {
                spinData = 0;
                consumer = new Consumer();
                consumer.setDaemon(true);
                consumer.start();

                producer = new Producer(WARMUP_ITERATIONS);
                producer.start();
                producer.join();
                consumer.join();

                noHintLatencyHistogram.reset();
                withHintLatencyHistogram.reset();
            }

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
            System.out.println("# duration (ns) per round trip op = " + duration / (ITERATIONS * 1.0));
            System.out.println("# round trip ops/sec = " +
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
