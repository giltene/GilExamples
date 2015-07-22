/**
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */

import org.HdrHistogram.Histogram;
import org.performancehints.SpinHint;

public class SpinHintTest {
    public static final long WARMUP_ITERATIONS = 500L * 1000L;
    public static final long ITERATIONS = 200L * 1000L * 1000L;

    public static volatile long spinData; // even: ready to produce; odd: ready to consume; -3: terminate

    public static final Histogram latencyHistogram = new Histogram(3600L * 1000L * 1000L * 1000L, 2);

    static class Producer extends Thread {
        final long iterations;
        Producer(final long terminatingIterationCount) {
            this.iterations = terminatingIterationCount;
        }
        public void run() {
            long prevTime = System.nanoTime();
            for (long i = 0; i < iterations; i++) {
                while ((spinData & 0x1) == 1) {
                    // busy spin until ready to produce
                    SpinHint.spinLoopHint();
                }
                long currTime = System.nanoTime();
                latencyHistogram.recordValue(currTime - prevTime);
                prevTime = System.nanoTime();
                spinData++; // produce
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
            latencyHistogram.reset();

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

            System.out.println("# Round trip latency histogram:");
            latencyHistogram.outputPercentileDistribution(System.out, 5, 1.0);
            System.out.println("# duration = " + duration);
            System.out.println("# duration (ns) per op = " + duration / (ITERATIONS));
            System.out.println("# op/sec = " +
                    (ITERATIONS * 1000L * 1000L * 1000L) / duration);

            System.out.println("# 50%'ile:   " + latencyHistogram.getValueAtPercentile(50.0) + "ns");
            System.out.println("# 90%'ile:   " + latencyHistogram.getValueAtPercentile(90.0) + "ns");
            System.out.println("# 99%'ile:   " + latencyHistogram.getValueAtPercentile(99.0) + "ns");
            System.out.println("# 99.9%'ile: " + latencyHistogram.getValueAtPercentile(99.9) + "ns");
        } catch (InterruptedException ex) {
            System.err.println("SpinHintTest interrupted.");
        }
    }

}
