/**
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */

import java.util.ArrayList;
import java.util.List;

public class MinorGC extends Thread
{

    class MinorGCConfiguration {
        public double refsFraction = 0.0;

        public void parseArgs(String[] args) {
            try {
                for (int i = 0; i < args.length; ++i) {
                    if (args[i].equals("-r")) {
                        refsFraction = Double.parseDouble(args[++i]);
                    } else {
                        throw new Exception("Invalid args");
                    }
                }
            } catch (Exception e) {
                System.err.println("Usage: java MinorGC [-r refsFraction]");
                System.exit(1);
            }
        }
    }

    static List<byte[]> list = new ArrayList<byte[]>();

    MinorGCConfiguration config = new MinorGCConfiguration();

    protected MinorGC(final String[] args) {
        config.parseArgs(args);
        this.setDaemon(true);
    }

    @Override
    public void run() {
        System.gc();

        long oldArraysAllocated = 0;
        long oldRefArraysAllocated = 0;
        int oldArrayLength = 16*1024*1024;

        while(true) {
            Object o = null;
            System.out.println("Start minor GCs:");
            for (int gcCount = 0; gcCount < 5; gcCount++)
            {
                for (int i = 0; i < 64*1024; i++)
                {
                    o = new byte[1024];
                }
            }

            oldArraysAllocated++;
            if ((1.0 * oldRefArraysAllocated) / oldArraysAllocated < config.refsFraction) {
                Object[] refs = new Object[oldArrayLength];
                oldRefArraysAllocated++;
                System.out.println("*** allocated a temporary " + refs.length / (1024 * 1024) + "M entry ref array.");
                for (int i = 0; i < refs.length; i++) {
                    refs[i] = refs;
                }
            } else {
                int[] ints = new int[oldArrayLength];
                System.out.println("*** allocated a temporary " + ints.length / (1024 * 1024) + "M entry int array.");
            }
        }
    }

    public static MinorGC commonMain(final String[] args) {
        MinorGC minorGC = new MinorGC(args);
        minorGC.start();
        return minorGC;
    }

    public static void premain(String argsString, java.lang.instrument.Instrumentation inst) {
        String[] args = (argsString != null) ? argsString.split("[ ,;]+") : new String[0];
        commonMain(args);
    }

    public static void main(final String[] args) {
        MinorGC minorGC = commonMain(args);

        if (minorGC != null) {
            // The minorGC thread, on it's own, will not keep the JVM from exiting.
            // If nothing else is running (i.e. we we are the main class), keep main thread from
            // exiting until the AllocationRateExample thread does...
            try {
                minorGC.join();
            } catch (InterruptedException e) {
            }
        }
    }
}
