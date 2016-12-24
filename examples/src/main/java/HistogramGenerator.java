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

public class HistogramGenerator extends Thread {

    public static final String versionString = "HistogramGenerator version 1.0.1";

    private final HistogramGeneratorConfiguration config;

    private static class HistogramGeneratorConfiguration {
        public long numPointsInHistogram = 10000000;

        public double baseLevel = 100.0;
        public double randomLevel = 0.0;
        public double gaussianLevel = 0.0;
        public double gammaLevel = 0.0;
        public double gammaShape = 0.0;
        public double gammaScale = 0.0;
        public double stallLevel = 0.0;
        public double stallLikelihood = 0.0;
        public double backlogCountLevel = 0;
        public double backlogLikelihood = 0.0;
        public double interval = 1.0;
        public double modeOneMagnitude = 0.0;
        public double modeOneLikelihood = 0.0;
        public double modeTwoMagnitude = 0.0;
        public double modeTwoLikelihood = 0.0;


        public String outputFileName = null;

        public boolean logFormatCsv = false;

        public int percentilesOutputTicksPerHalf = 5;
        public Double outputValueUnitRatio = 0.001;

        public boolean error = false;
        public String errorMessage = "";

        public HistogramGeneratorConfiguration(final String[] args) {
            boolean askedForHelp = false;
            try {
                for (int i = 0; i < args.length; ++i) {
                    if (args[i].equals("-csv")) {
                        logFormatCsv = true;
                    } else if (args[i].equals("-baseLevel")) {
                        baseLevel = Double.parseDouble(args[++i]);
                    } else if (args[i].equals("-randomLevel")) {
                        randomLevel = Double.parseDouble(args[++i]);
                    } else if (args[i].equals("-gaussianLevel")) {
                        gaussianLevel = Double.parseDouble(args[++i]);
                    } else if (args[i].equals("-gammaLevel")) {
                        gammaLevel = Double.parseDouble(args[++i]);
                    } else if (args[i].equals("-gammaShape")) {
                        gammaShape = Double.parseDouble(args[++i]);
                    } else if (args[i].equals("-gammaScale")) {
                        gammaScale = Double.parseDouble(args[++i]);
                    } else if (args[i].equals("-stallLevel")) {
                        stallLevel = Double.parseDouble(args[++i]);
                    } else if (args[i].equals("-stallLikelihood")) {
                        stallLikelihood = Double.parseDouble(args[++i]);
                    } else if (args[i].equals("-backlogCountLevel")) {
                        backlogCountLevel = Long.parseLong(args[++i]);
                    } else if (args[i].equals("-backlogLikelihood")) {
                        backlogLikelihood = Double.parseDouble(args[++i]);
                    } else if (args[i].equals("-modeOneMagnitude")) {
                        modeOneMagnitude = Double.parseDouble(args[++i]);
                    } else if (args[i].equals("-modeOneLikelihood")) {
                        modeOneLikelihood = Double.parseDouble(args[++i]);
                    } else if (args[i].equals("-modeTwoMagnitude")) {
                        modeTwoMagnitude = Double.parseDouble(args[++i]);
                    } else if (args[i].equals("-modeTwoLikelihood")) {
                        modeTwoLikelihood = Double.parseDouble(args[++i]);
                    } else if (args[i].equals("-interval")) {
                        interval = Double.parseDouble(args[++i]);
                    } else if (args[i].equals("-o")) {
                        outputFileName = args[++i];
                    } else if (args[i].equals("-percentilesOutputTicksPerHalf")) {
                        percentilesOutputTicksPerHalf = Integer.parseInt(args[++i]);
                    } else if (args[i].equals("-outputValueUnitRatio")) {
                        outputValueUnitRatio = Double.parseDouble(args[++i]);
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
                        "\"[-csv] [-i inputFileName] [-baseLevel baseLevel] " +
                                "[-randomLevel randomLevel] [-gaussianLevel gaussianLevel] " +
                                "[-gammaLevel gammaLevel] [-gammaShape gammaShape] [-gammaScale gammaScale] " +
                                "[-stallLevel stallLevel] [-stallLikelihood stallLikelihood]" +
                                "[-backlogCountLevel backlogCountLevel] [-backlogLikelihood backlogLikelihood]" +
                                "[-outputValueUnitRatio outputValueUnitRatio] [-interval interval]";

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
        PrintStream timeIntervalLog = null;
        PrintStream histogramPercentileLog = System.out;
        Double firstStartTime = 0.0;
        boolean timeIntervalLogLegendWritten = false;

        DoubleHistogram histogram = new DoubleHistogram(3);
        Random random = new Random(42);
        Random stallRandom = new Random(42);
        Random backlogRandom = new Random(42);
        Random modeRandom = new Random(42);
        GammaDistribution gammaDistribution =
                ((config.gammaShape > 0) && (config.gammaScale > 0) && (config.gammaLevel > 0)) ?
                        new GammaDistribution(config.gammaShape, config.gammaScale) :
                        null;
        double stallComponent = 0;
        long backlogCount = 0;
        long backlogPosition = 0;

        for (long i = 0; i < config.numPointsInHistogram; i++) {
            Double value = config.baseLevel;

            if (config.modeOneLikelihood != 0.0) {
                if (modeRandom.nextDouble() < config.modeOneLikelihood) {
                    value = config.modeOneMagnitude;
                }
            }

            if (config.modeTwoLikelihood != 0.0) {
                if (modeRandom.nextDouble() < config.modeTwoLikelihood) {
                    value = config.modeTwoMagnitude;
                }
            }

            if (config.stallLikelihood != 0.0) {
                if (stallRandom.nextDouble() < config.stallLikelihood) {
                    stallComponent = config.stallLevel;
                }
            }

            if (config.backlogLikelihood != 0.0) {
                if (backlogRandom.nextDouble() < config.backlogLikelihood) {
                    backlogCount += config.backlogCountLevel;
                }
            }

            if (stallComponent > 0) {
                value += stallComponent;
                stallComponent -= config.interval;
                // Stalls ignore the contributions above base level:
                histogram.recordValue(value);
                continue;
            }

            if (backlogCount > 0) {
                double backlogComponent = backlogPosition * config.interval;
                value += backlogComponent;
                backlogPosition++;
                if (backlogPosition >= backlogCount) {
                    backlogCount = 0;
                    backlogPosition = 0;
                }
            }

            if (config.randomLevel != 0) {
                value += random.nextDouble() * config.randomLevel;
            }

            if (config.gaussianLevel != 0) {
                value += (random.nextGaussian() + 1.0) * config.gaussianLevel;
                value = Math.min(value, 1_000_000_000L);
                value = Math.max(value, 0);
            }

            if (gammaDistribution != null) {
                value += gammaDistribution.sample() * config.gammaLevel;
                value = Math.min(value, 1_000_000_000L);
                value = Math.max(value, 0);
            }

            histogram.recordValue(value);
        }

        if (config.outputFileName != null) {
            try {
                histogramPercentileLog = new PrintStream(new FileOutputStream(config.outputFileName), false);
            } catch (FileNotFoundException ex) {
                System.err.println("Failed to open percentiles histogram output file " + config.outputFileName);
            }
        }


        histogram.outputPercentileDistribution(histogramPercentileLog,
                config.percentilesOutputTicksPerHalf, config.outputValueUnitRatio, config.logFormatCsv);
    }

    /**
     * Construct a {@link HistogramGenerator} with the given arguments
     * (provided in command line style).
     *
     * @param args command line arguments
     */
    public HistogramGenerator(final String[] args) {
        this.setName("HistogramLogProcessor");
        config = new HistogramGeneratorConfiguration(args);
    }

    /**
     * main() method.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        final HistogramGenerator generator = new HistogramGenerator(args);
        generator.start();
    }
}
