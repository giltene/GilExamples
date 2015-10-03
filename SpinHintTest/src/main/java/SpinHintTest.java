/**
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */

import org.HdrHistogram.Histogram;
import org.performancehints.SpinHint;

/**
 * A simple thread-to-thread communication latency test that measures and reports on the
 * behavior of thread-to-thread ping-pong latencies when spinning using a shared volatile
 * field, along with the impact of using a spinLoo[Hint() call on that latency behavior.
 *
 * This test can be used to measure and document the impact of spinLoopHint behavior
 * on thread-to-thread communication latencies. E.g. when the two threads are pinned to
 * the two hardware threads of a shared x86 core (with a shared L1), this test will
 * demonstrate an estimate the best case thread-to-thread latencies possible on the
 * platform, if the latency of measuring time with System.nanoTime() is discounted
 * (nanoTime latency can be separtely estimated across the percentile spectrum using
 * the {@link NanoTimeLatency} test in this package).
 *
 * For consistent measurement, it is recommended that this test be executed while
 * binding the process to specific cores. E.g. on a Linux system, the following
 * command can be used:
 *
 * taskset -c 23,47 java -jar SpinLoopHint.jar
 *
 * (the choice of cores 23 and 47 is specific to a 48 vcore system where cores
 * 23 and 47 represent two hyper-threads on a common core).
 *
 */
public class SpinHintTest {
    public static final long WARMUP_PASS_COUNT = 5;
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

                latencyHistogram.reset();
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

            System.out.println("# Round trip latency histogram:");
            latencyHistogram.outputPercentileDistribution(System.out, 5, 1.0);
            System.out.println("# duration = " + duration);
            System.out.println("# duration (ns) per round trip op = " + duration / (ITERATIONS));
            System.out.println("# round trip ops/sec = " +
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
