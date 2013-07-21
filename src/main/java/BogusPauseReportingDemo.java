/**
 * HiccupMeter.java
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 */


public class BogusPauseReportingDemo {

    static final int COPYING_ARRAY_LENGTH = 256 * 1024 * 1024;
    static final int ALLOCATING_ARRAY_LENGTH = 2 * 1024 * 1024;

    static volatile boolean doRun = true;

    static class Allocator extends Thread {
        volatile byte[] array;

        public void run() {
            while (doRun) {
                array = new byte[ALLOCATING_ARRAY_LENGTH];
            }
        }
    }

    static class Copier extends Thread {
        volatile byte array1[] = new byte[COPYING_ARRAY_LENGTH];
        volatile byte array2[] = new byte[COPYING_ARRAY_LENGTH];

        public void run() {
            while (doRun) {
                System.arraycopy(array1, 0, array2, 0, COPYING_ARRAY_LENGTH);
            }
        }
    }

    public static void main(String [] args) {
        Allocator allocator = new Allocator();
        Copier copier = new Copier();

        long runTimeMsec = 10000;
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-t")) {
                runTimeMsec = Integer.parseInt(args[++i]);
            } else {
                System.out.println("Usage: java BogusPauseReportingDemo [-t runTimeMsec]");
                System.exit(1);
            }
        }

        System.out.println("BogusPauseReportingDemo started, will run for " + runTimeMsec + " msec");
        allocator.start();
        copier.start();
        try {
            Thread.sleep(runTimeMsec);
        } catch (InterruptedException ex) {

        }

        doRun = false;

        System.out.println("BogusPauseReportingDemo done...");
    }
}


