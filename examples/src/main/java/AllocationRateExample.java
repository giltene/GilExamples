/**
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */

import java.util.HashMap;

public class AllocationRateExample extends Thread {

    private final AllocationRateExampleConfiguration config;

    protected static class AllocationRateExampleConfiguration {
        public boolean verbose = false;

        public int concurrencyLevel = 1;
        public long runTimeMs = 0;
        public long startDelayMs = 1000;

        public int allocByteArraySize = 40;
        public int allocMapChunkSize = 100;

        public AllocationRateExampleConfiguration(final String[] args) {
            try {
                for (int i = 0; i < args.length; ++i) {
                    if (args[i].equals("-v")) {
                        verbose = true;
                    } else if (args[i].equals("-c")) {
                        concurrencyLevel = Integer.parseInt(args[++i]);
                    } else if (args[i].equals("-t")) {
                        runTimeMs = Long.parseLong(args[++i]);
                    } else if (args[i].equals("-d")) {
                        startDelayMs = Long.parseLong(args[++i]);
                    } else if (args[i].equals("-m")) {
                        allocMapChunkSize = Integer.parseInt(args[++i]);
                    } else if (args[i].equals("-b")) {
                        allocByteArraySize = Integer.parseInt(args[++i]);
                    } else {
                        throw new Exception("Invalid args: " + args[i]);
                    }
                }
            } catch (Exception e) {
                String errorMessage = "Error: launched with the following args:\n";

                for (String arg : args) {
                    errorMessage += arg + " ";
                }
                errorMessage += "\nWhich was parsed as an error, indicated by the following exception:\n" + e;

                System.err.println(errorMessage);

                String validArgs =
                        "\"[-h]  [-v] [-c] [-t runTimeMs] [-d startDelayMs] " +
                                "[-m allocMapChunkSize] [-b allocByteArraySize]\"\n";

                System.err.println("valid arguments = " + validArgs);

                System.err.println("" +
                        " [-h]                        help\n" +
                        " [-v]                        verbose\n" +
                        " [-c]                        Concurrency level (number of allocator threads)\n" +
                        " [-t runTimeMs]              Limit run time [default 0, for infinite]\n" +
                        " [-d runTimeMs]              Delay measurement start time [default 1000, for 1 sec]\n" +
                        " [-m allocMapChunkSize]      Number of elements to accumulate per map [default 100]\n" +
                        " [-b allocByteArraySize]     number of bytes per allocated array [default 40]\n" +
                        "\n");

                System.exit(1);
            }
        }
    }

    protected class AllocatorThread extends Thread {
        public volatile HashMap exposedMap;

        AllocatorThread() {
            this.setDaemon(true);
        }

        @Override
        public void run() {
            final int allocArraySize = config.allocByteArraySize;
            final int allocMapChuckSize = config.allocMapChunkSize;
            while (true) {
                HashMap<Integer, byte[]> map = new HashMap<>();
                exposedMap = map;
                for (int i = 0; i < allocMapChuckSize; i++) {
                    map.put(i, new byte[allocArraySize]);
                }
            }
        }
    }

    protected AllocationRateExample(final String[] args) {
        config = new AllocationRateExampleConfiguration(args);
        this.setDaemon(true);
    }

    @Override
    public void run() {
        long startTimeMsec = System.currentTimeMillis();
        try {
            // Create and start allocators:
            AllocatorThread[] allocators = new AllocatorThread[config.concurrencyLevel];
            for (int i = 0; i < config.concurrencyLevel; i++) {
                allocators[i] = new AllocatorThread();
                allocators[i].start();
            }

            // Wait for configured delay time:
            do {
                Thread.sleep(100);
            } while (System.currentTimeMillis() - startTimeMsec < config.startDelayMs);

            // Start measurement:
            long measurementStartTime = System.currentTimeMillis();
            long endTime = (config.runTimeMs == 0) ?
                    Long.MAX_VALUE :
                    measurementStartTime + config.runTimeMs;

            while (System.currentTimeMillis() < endTime) {
                Thread.sleep(100);
            }

            // Output something that depends on the allocations to make it impossible to optimize them away:
            long sum = 0;
            for (AllocatorThread a : allocators) {
                sum += a.exposedMap.size();
            }
            System.out.println("Done.." + ((sum == 1) ? "." : ""));

        } catch (InterruptedException ex) {
        }
    }


    public static AllocationRateExample commonMain(final String[] args) {
        AllocationRateExample allocationRateExample = new AllocationRateExample(args);
        allocationRateExample.start();
        return allocationRateExample;
    }

    public static void premain(String argsString, java.lang.instrument.Instrumentation inst) {
        String[] args = (argsString != null) ? argsString.split("[ ,;]+") : new String[0];
        commonMain(args);
    }

    public static void main(final String[] args) {
        AllocationRateExample allocationRateExample = commonMain(args);

        if (allocationRateExample != null) {
            // The AllocationRateExample thread, on it's own, will not keep the JVM from exiting.
            // If nothing else is running (i.e. we we are the main class), keep main thread from
            // exiting until the AllocationRateExample thread does...
            try {
                allocationRateExample.join();
            } catch (InterruptedException e) {
            }
        }
    }
}
