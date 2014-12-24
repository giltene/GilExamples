/**
 * HiccupMeter.java
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 */


public class BogusLoopPauseReportingDemo {
    static int pausingLoopTripCount = 100000000; // Loop for several sec. by default.

    static final int COPYING_ARRAY_LENGTH = 256 * 1024 * 1024;
    static final int ALLOCATING_ARRAY_LENGTH = 2 * 1024 * 1024;

    static volatile boolean doRun = true;

    static class Looper extends Thread {
        volatile long num = 0;

        public static long func(long num, int tripcount) {
            // -server compiler typically smart enough to take safepoint out of the counted loop:
            for (int i = 1; i < tripcount; i++) {
                num %= i;
                num += 765;
            }
            return num;
        }

        public void run() {
            // Repeatedly run simple loop function (pass num to foil optimizing loop away).
            long i = 0;
            long startTime = System.currentTimeMillis();
            while (doRun) {
                i++;
                if (i < 20000 ) {
                    num = func(num, 20000); // Warm up
                } else {
                    num = func(num, pausingLoopTripCount);
                }
                num -=i;
                if ((i % 10) == 0) System.out.print("+");
            }
        }
    }

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
        Looper looper = new Looper();
        Allocator allocator = new Allocator();
        Copier copier = new Copier();

        long runTimeMsec = 10000;
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-l")) {
                pausingLoopTripCount = Integer.parseInt(args[++i]);
            } else
            if (args[i].equals("-t")) {
                runTimeMsec = Integer.parseInt(args[++i]);
            } else {
                System.out.println("Usage: java BogusPauseReportingDemo [-t runTimeMsec]");
                System.exit(1);
            }
        }

        System.out.println("BogusPauseReportingDemo started, will run for " + runTimeMsec + " msec");
        looper.start();
        allocator.start();
        copier.start();
        try {
            Thread.sleep(runTimeMsec);
        } catch (InterruptedException ignore) {

        }

        doRun = false;

        System.out.println("BogusPauseReportingDemo done...");
    }
}


