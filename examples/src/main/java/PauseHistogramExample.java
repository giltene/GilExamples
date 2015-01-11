/**
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */

import org.HdrHistogram.DoubleHistogram;
import org.HdrHistogram.Histogram;
import org.apache.commons.math3.distribution.GammaDistribution;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Random;

public class PauseHistogramExample extends Thread {

    public static final String versionString = "PauseHistogramExample version 1.0.1";

    private final HistogramGeneratorConfiguration config;

    private static class HistogramGeneratorConfiguration {
        public long numIntervals = 1;

        public long intervalLength = 10000000;
        public long level1 = 200000;
        public int level1Count = 1;
        public long level2 = 1000;
        public int level2Count = 20;
        public long level3 = 0;
        public int level3Count = 0;

        public int percentilesOutputTicksPerHalf = 5;
        public Double outputValueUnitRatio = 1000.0;

        public boolean error = false;
        public String errorMessage = "";

        public HistogramGeneratorConfiguration(final String[] args) {
            boolean askedForHelp = false;
            try {
                for (int i = 0; i < args.length; ++i) {
                    if (args[i].equals("-n")) {
                        numIntervals = Long.parseLong(args[++i]);
                    } else if (args[i].equals("-il")) {
                        intervalLength = Long.parseLong(args[++i]);
                    } else if (args[i].equals("-l1")) {
                        level1 = Long.parseLong(args[++i]);
                    } else if (args[i].equals("-l2")) {
                        level1 = Long.parseLong(args[++i]);
                    } else if (args[i].equals("-l3")) {
                        level1 = Long.parseLong(args[++i]);
                    } else if (args[i].equals("-l1c")) {
                        level1Count = Integer.parseInt(args[++i]);
                    } else if (args[i].equals("-l2c")) {
                        level2Count = Integer.parseInt(args[++i]);
                    } else if (args[i].equals("-l3c")) {
                        level3Count = Integer.parseInt(args[++i]);
                    } else if (args[i].equals("-h")) {
                        askedForHelp = true;
                        throw new Exception("Help: " + args[i]);
                    } else {
                        throw new Exception("Invalid args: " + args[i]);
                    }
                }

            } catch (Exception e) {
                error = true;
                errorMessage = "Error: " + versionString + " launched with the following args:\n";

                for (String arg : args) {
                    errorMessage += arg + " ";
                }
                if (!askedForHelp) {
                    errorMessage += "\nWhich was parsed as an error, indicated by the following exception:\n" + e;
                    System.err.println(errorMessage);
                }

                final String validArgs =
                        "\"[-n] [-il intervalLength] [-l1 level1] [-l1c level1Count] " +
                                "[-l2 level2] [-l2c level2Count] " +
                                "[-l3 level3] [-l3c level3Count] ";

                System.err.println("valid arguments = " + validArgs);

                System.exit(1);
            }
        }
    }

    /**
     * Run the log processor with the currently provided arguments.
     */
    @Override
    public void run() {
        PrintStream histogramPercentileLog = System.out;

        Histogram histogram = new Histogram(3);

        for (long c = 0; c < config.numIntervals; c++) {

            for (int i = 0; i < config.level1Count; i++) {
                histogram.recordValueWithExpectedInterval(config.level1, 1);
            }
            for (int i = 0; i < config.level2Count; i++) {
                histogram.recordValueWithExpectedInterval(config.level2, 1);
            }
            for (int i = 0; i < config.level3Count; i++) {
                histogram.recordValueWithExpectedInterval(config.level3, 1);
            }

            long zeroCount = config.intervalLength -
                    (
                            (config.level1Count * config.level1) +
                                    (config.level2Count * config.level2) +
                                    (config.level3Count * config.level3)
                    );
            histogram.recordValueWithCount(0, zeroCount);
        }

        histogram.outputPercentileDistribution(histogramPercentileLog,
                config.percentilesOutputTicksPerHalf, config.outputValueUnitRatio);

        System.out.println("---------------------------------");
        System.out.println("97%'ile = " + histogram.getValueAtPercentile(97.0));
        System.out.println("98%'ile = " + histogram.getValueAtPercentile(98.0));
        System.out.println("99%'ile = " + histogram.getValueAtPercentile(99.0));
        System.out.println("99.9%'ile = " + histogram.getValueAtPercentile(99.9));
    }

    /**
     * Construct a {@link PauseHistogramExample} with the given arguments
     * (provided in command line style).
     *
     * @param args command line arguments
     */
    public PauseHistogramExample(final String[] args) {
        this.setName("HistogramLogProcessor");
        config = new HistogramGeneratorConfiguration(args);
    }

    /**
     * main() method.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        final PauseHistogramExample generator = new PauseHistogramExample(args);
        generator.start();
    }
}
