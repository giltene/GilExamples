/**
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */

import org.performancehints.ThreadHints;

/**
 * A simple thread-to-thread communication latency test that measures and reports on the
 * throughout of thread-to-thread ping-pong communications when spinning using a shared
 * volatile field, along with the impact of using a Runtime.onSpinWait() on that ping pong
 * throughput.
 *
 * By observing the effects of Runtime.onSpinWait() behavior on ping pong throughout, this test can be
 * used to indirectly measure and document the impact of Runtime.onSpinWait() behavior on thread-to-thread
 * communication latencies.
 *
 * For consistent measurement, it is recommended that this test be executed while
 * binding the process to specific cores. E.g. on a Linux system, the following
 * command can be used:
 *
 * taskset -c 23,47 java -cp SpinWaitTest.jar SpinWaitThroughputTest
 *
 * (the choice of cores 23 and 47 is specific to a 48 vcore system where cores
 * 23 and 47 represent two hyper-threads on a common core).
 *
 */
public class SpinWaitThroughputTest {
    public static final long WARMUP_PASS_COUNT = 5;
    public static final long WARMUP_ITERATIONS = 500L * 1000L;
    public static final long ITERATIONS = 200L * 1000L * 1000L;

    public static volatile long spinData; // even: ready to produce; odd: ready to consume; -3: terminate
    public static volatile long totalSpins = 0;

    static class Producer extends java.lang.Thread {
        final long iterations;

        Producer(final long terminatingIterationCount) {
            this.iterations = terminatingIterationCount;
        }
        public void run() {
            long spins = 0;
            for (long i = 0; i < iterations; i++) {
                while ((spinData & 0x1) == 1) {
                    // busy spin until ready to produce
                    ThreadHints.onSpinWait();
                    spins++;
                }
                spinData++; // produce
            }

            // Signal consumer to terminate:
            // First make sure we are ready to produce, to avoid racing
            // with (and getting stomped by) consumer's ++ op:
            while ((spinData & 0x1) == 1);
            // Now, knowing that consumer is not concurrently modifying
            // spinData, we can signal to terminate:
            spinData = -3;

            totalSpins += spins;
        }
    }

    static class Consumer extends java.lang.Thread {
        public void run() {
            while (spinData >= 0) {
                while ((spinData & 0x1) == 0) {
                    // busy spin until ready to consume
                    ThreadHints.onSpinWait();
                }
                spinData++; // consume
            }
        }
    }

    public static void main(final String[] args) {
        try {
            java.lang.Thread producer;
            java.lang.Thread consumer;

            for (int i = 0; i < WARMUP_PASS_COUNT; i++) {
                spinData = 0;
                consumer = new Consumer();
                consumer.setDaemon(true);
                consumer.start();

                producer = new Producer(WARMUP_ITERATIONS);
                producer.start();
                producer.join();
                consumer.join();
            }

            java.lang.Thread.sleep(1000); // Let things (like JIT compilations) settle down.
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

            System.out.println("# of iterations in producer = " + ITERATIONS);
            System.out.println("# of total spins in producer = " + totalSpins);
            System.out.println("# of producer spins per iteration = " + (1.0 * totalSpins)/ ITERATIONS);
            System.out.println("# duration = " + duration);
            System.out.println("# duration (ns) per round trip op = " + duration / (ITERATIONS * 1.0));
            System.out.println("# round trip ops/sec = " +
                    (ITERATIONS * 1000L * 1000L * 1000L) / duration);

        } catch (InterruptedException ex) {
            System.err.println("SpinWaitThroughputTest interrupted.");
        }
    }

}
