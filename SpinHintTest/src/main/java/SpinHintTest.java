/**
 * Created by gil on 7/1/15.
 */

import org.HdrHistogram.Histogram;
import org.performancehints.SpinHint;

public class SpinHintTest {
    public static final long WARMUP_ITERATIONS = 500L * 1000L;
    public static final long ITERATIONS = 50L * 1000L * 1000L;

    public static volatile long s1;

    // Padding (2 cache lines worth):
    public static volatile long p0, p1, p2, p3, p4, p5, p6, p7;
    public static volatile long p8, p9, p10, p11, p12, p13, p14, p15;

    public static volatile long s2;

    public static volatile boolean doRun = true;

    public static final Histogram latencyHistogram = new Histogram(3600L * 1000L * 1000L * 1000L, 2);

    static class Producer extends Thread {
        final long iterations;
        Producer(final long terminatingIterationCount) {
            this.iterations = terminatingIterationCount;
        }
        public void run() {
            long prevTime = System.nanoTime();
            long value = s1;
            while (s1 < iterations) {
                while (s2 != value) {
                    // busy spin
                    SpinHint.spinLoopHint();
                }
                long currTime = System.nanoTime();
                latencyHistogram.recordValue(currTime - prevTime);
                prevTime = System.nanoTime();
                value = ++s1;
            }
        }
    }

    static class Consumer extends Thread {
        public void run() {
            long value = s2;
            while (doRun) {
                while (doRun && (value == s1)) {
                    // busy spin
                    SpinHint.spinLoopHint();
                }
                value = ++s2;
            }
        }
    }

    public static void main(final String[] args) {
        try {
            Thread consumer = new Consumer();
            consumer.setDaemon(true);
            consumer.start();

            s1 = s2 = 0;
            doRun = true;


            Thread producer = new Producer(WARMUP_ITERATIONS);
            producer.start();
            producer.join();
            doRun = false;
            consumer.join();
            latencyHistogram.reset();

            Thread.sleep(500);
            System.out.println("Warmup done. Restarting threads.");

            s1 = s2 = 0;
            doRun = true;
            consumer = new Consumer();
            consumer.setDaemon(true);
            consumer.start();

            producer = new Producer(ITERATIONS);

            long start = System.nanoTime();
            producer.start();
            producer.join();

            long duration = System.nanoTime() - start;

            System.out.println("duration = " + duration);
            System.out.println("ns per op = " + duration / (ITERATIONS));
            System.out.println("op/sec = " +
                    (ITERATIONS * 1000L * 1000L * 1000L) / duration);
            System.out.println("s1 = " + s1 + ", s2 = " + s2);
            System.out.println("\nRound trip latency histogram:\n");
            latencyHistogram.outputPercentileDistribution(System.out, 5, 1.0);
        } catch (InterruptedException ex) {
            System.err.println("InterThreadLatency interrupted.");
        }
    }

}
