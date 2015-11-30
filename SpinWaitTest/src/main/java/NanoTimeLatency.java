import org.HdrHistogram.Histogram;

/**
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */

public class NanoTimeLatency {
    public static final long WARMUP_ITERATIONS = 500L * 1000L;
    public static final long ITERATIONS = 100L * 1000L * 1000L;

    public static final Histogram latencyHistogram = new Histogram(3600L * 1000L * 1000L * 1000L, 2);

    public static void collectNanoTimeLatencies(final long iterations) {
        for (long count = 0; count < iterations; count++) {
            long prevTime = System.nanoTime();
            long currTime = System.nanoTime();
            latencyHistogram.recordValue(currTime - prevTime);
        }
    }

    public static void main(final String[] args) {
        try {
            collectNanoTimeLatencies(WARMUP_ITERATIONS);
            latencyHistogram.reset();

            Thread.sleep(500);
            System.out.println("Warmup done. Starting nanoTime() latency measurement.");

            long start = System.nanoTime();
            collectNanoTimeLatencies(ITERATIONS);

            long duration = System.nanoTime() - start;

            System.out.println("duration = " + duration);
            System.out.println("ns per op = " + duration / (ITERATIONS * 1.0));
            System.out.println("op/sec = " +
                    (ITERATIONS * 1000L * 1000L * 1000L) / duration);
            System.out.println("\nSystems.nanoTime() latency histogram:\n");
            latencyHistogram.outputPercentileDistribution(System.out, 5, 1.0);

            System.out.println("50%'ile:   " + latencyHistogram.getValueAtPercentile(50.0) + "ns");
            System.out.println("90%'ile:   " + latencyHistogram.getValueAtPercentile(90.0) + "ns");
            System.out.println("99%'ile:   " + latencyHistogram.getValueAtPercentile(99.0) + "ns");
            System.out.println("99.9%'ile: " + latencyHistogram.getValueAtPercentile(99.9) + "ns");
        } catch (InterruptedException ex) {
            System.err.println("NanoTimeLatency interrupted.");
        }
    }
}
