import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A demonstrator for a basic and easily-hittable deadlock situation with the current (Java 20) implementation
 * of Virtual Threads. At the core of the mechanism that creates the inherent deadlock potential is the fact that
 * virtial threads that hold a monitor are "pinned" to their platform carrier threads, and will not relinquish
 * them and allow other virtual threads to use them until the monitor is released. This design limitation
 * inherently limits the total number of monitors that can be held at the same time across all virtual threads,
 * creating plenty of potential a carrier-resource-starvation situations to lead to virtual thread execution
 * deadlocks, even in situation where perfect lock priority discipline is maintained.
 *
 * Usage: java --enable-preview ThreadDeadLocker [p | v] <numberOfChains> <chainLength>
 *
 * To demonstrate deadlocks: use virtual thread ("v") and a chain length or a number of chains (of length 2 or more)
 * larger than the number of carrier threads the system you run on has. To demonstrate that a "normal" thread
 * scheduling system (which can interleave the execution of different threads on cpus, regardless of what locks those
 * threads may or may not hold) is NOT susceptible to deadlocking under the same scenarios, run the same chain length
 * chain  counts, but with platform threads ("p"). A deadlock will be evident by continued reports of a zero rate
 * of progress...
 *
 * Bottom line: Lock priority discipline is a commonly used technique for ensuring that deadlocks are impossible
 * in multi-threaded systems (by systemically preventing the possibility of lock-blocking loops), but this technique
 * can easily fail to protect against deadlocks with Java's the current (as of Java 20) Virtual Threads
 * implementations, leaving most systems with no effective means of preventing deadlocks, or of detecting their
 * potential existence...
 */
public class ThreadDeadLocker {
    static final boolean verbose = false;
    static final Duration timeToDoStuff = Duration.ZERO;
    static final Duration waitLoopSleepDuration = Duration.ofMillis(1);
    static final Duration runnerLoopSleepDuration = Duration.ofMillis(1);

    private static Thread makeThread(String threadName, Runnable runnable, boolean useVirtualThread) {
        if (useVirtualThread) {
            return Thread.ofVirtual().name(threadName).start(runnable);
        }
        return Thread.ofPlatform().name(threadName).start(runnable);
    }

    public static class ChainedThing {
        private static AtomicLong valueCount = new AtomicLong(0);
        private String name;
        private ChainedThing nextThing;
        private Duration duration;

        private volatile boolean go = false;

        ChainedThing(String name, ChainedThing nextThing, Duration duration) {
            this.name = name;
            setNextThing(nextThing);
            this.duration = duration;
        }

        void setNextThing(ChainedThing nextThing) {
            this.nextThing = nextThing;
        }

        String getName() {
            return name;
        }

        static long getCount() {
            return valueCount.get();
        }

        public void go() {
            go = true;
        }

        public synchronized void doSomething() {
            doSomething(duration);
        }

        public synchronized void doSomething(Duration duration) {
            go = false;
            if (!duration.isZero()) {
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    // skip
                }
            }
            nextThing.go();
            valueCount.incrementAndGet();
        }

        public synchronized void waitToDoSomething() {
            while (!go) {
                // wait for go to be set by another thing.
                if (!waitLoopSleepDuration.isZero()) {
                    try {
                        Thread.sleep(waitLoopSleepDuration);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    static class RunnableThing implements Runnable {
        ChainedThing theThing;
        RunnableThing(ChainedThing theThing) {
            this.theThing = theThing;
        }

        @Override
        public void run() {
            synchronized (this) {
                while (true) {
                    theThing.waitToDoSomething();
                    theThing.doSomething();
                    if (!runnerLoopSleepDuration.isZero()) {
                        try {Thread.sleep(runnerLoopSleepDuration);} catch (InterruptedException e) {}
                    }
                }
            }
        }
    }

    public static void main(String[] args) {

        if ((args.length != 3) || !(args[0].equals("p") || args[0].equals("v"))) {
            System.out.println("Usage: java --enable-preview ThreadDeadLocker [p | v] <numberOfChains> <chainLength>");
            System.exit(1);
        }
        boolean useVirtualThreads = args[0].equals("v");
        int numberOfChains = Integer.parseInt(args[1]);
        int chainLength = Integer.parseInt(args[2]);
        System.out.println("Running with numberOfChains = " + numberOfChains + ", chainLength = " + chainLength);

        List<List<ChainedThing>> thingChains = new ArrayList<>();

        for (int i = 0; i < numberOfChains; i++) {
            List<ChainedThing> chain = new ArrayList<>();
            // Populate the chain:
            for (int j = 0; j < chainLength; j++) {
                chain.add(new ChainedThing("Thing " + j + " in ThingChain " + i, null,
                        (j == 0) ? timeToDoStuff : Duration.ZERO));
            }
            // Connect the chain in a loop:
            for (int j = 0; j < chainLength; j++) {
                ChainedThing thisThing = chain.get(j);
                ChainedThing prevThing = chain.get(j > 0 ? j - 1 : (chainLength - 1));
                prevThing.setNextThing(thisThing);
                if (verbose) {
                    System.out.println("Thing " + prevThing.getName() + " chained to next thing " + prevThing.nextThing.getName());
                }
            }
            thingChains.add(chain);
        }

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < chainLength; i++) {
            // For each position i accross all chains, create a runnable and start a thread for the thing at
            // the i'th position of that chain:
            // (we do this in this semi-strange order so that each chain would have threads started before
            // other chains have all their threads started. This way, in a limited-ability-to-host-threads
            // deadlock-inducing situation  (i.e. Virtual Threads with fewer carrier threads than eiter the
            // chain length or the number of chains), no chain will have all of its thread sitting on carrier
            // threads, and no chain will make continued forward progress once).
            for (List<ChainedThing> chain : thingChains) {
                ChainedThing thing = chain.get(i);
                threads.add(makeThread(thing.getName(), new RunnableThing(thing), useVirtualThreads));
            }
        }

        // Hit "go" on each chain. This should start continuing forward progress in that chain as
        // long as deadlocks don't occur.
        for (List<ChainedThing> chain : thingChains) {
            chain.get(0).go();
        }

        // Report on progress:
        try {
            long prevVal = 0;
            long prevMillis = System.currentTimeMillis();
            while (true) {
                Thread.sleep(Duration.ofSeconds(1));
                long val = ChainedThing.getCount();
                long timeMillis = System.currentTimeMillis();
                double rate = (1000.0 * (double)(val - prevVal)) / (timeMillis - prevMillis);
                System.out.println("progress count = " + ChainedThing.getCount() + ", rate of progress = " + rate + "/sec");
                prevVal = val;
                prevMillis = timeMillis;
            }
        } catch (InterruptedException e) {
            // bugger out
        }
    }
}
