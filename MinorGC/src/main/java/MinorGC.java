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
    static List<byte[]> list = new ArrayList<byte[]>();

    protected MinorGC(final String[] args) {
        this.setDaemon(true);
    }

    @Override
    public void run() {
        System.gc();
        for (int l = 0; l < 64; l++) {
            Object o = null;
            System.out.println("Start minor GCs:");
            for (int gcCount = 0; gcCount < 5; gcCount++)
            {
                for (int i = 0; i < 64*1024; i++)
                {
                    o = new byte[1024];
                }
            }
            byte[] a = new byte[128*1024*1024];
            System.out.println("*** allocated a temporary " + a.length / (1024 * 1024) + "MB array.");
            a = null;
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
