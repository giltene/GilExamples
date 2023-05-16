import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A demonstrator for a basic and easily-hittable deadlock situation with the current (Java 20) implementation
 * of Virtual Threads. At the core of the mechanism that creates the inherent deadlock potential is the fact that
 * virtual threads that hold a monitor are "pinned" to their platform carrier threads, and will not relinquish
 * them and allow other virtual threads to use them until the monitor is released. This design limitation
 * inherently limits the total number of monitors that can be held at the same time across all virtual threads,
 * creating plenty of potential a carrier-resource-starvation situations to lead to virtual thread execution
 * deadlocks, even in situation where perfect lock priority discipline is maintained.
 *
 * Usage: java --enable-preview ThreadDeadLocker [p | v] <numberOfChains> <chainLength>
 *
 * To demonstrate deadlocks: use virtual thread ("v") and supply numberOfChains and chainLength parameters such
 * that (2 * numberOfChains * chainLength) will be larger than the number of carrier threads the system you run on has.
 * To demonstrate that a "normal" thread scheduling system (which can interleave the execution of different threads
 * on cpus, regardless of what locks those threads may or may not hold) is NOT susceptible to deadlocking under the
 * same scenarios, run with the same chain count and chain length, but with platform threads ("p"). A deadlock will
 * be evident either by continued reports of a zero rate of progress, or through reports of the specific chains that
 * are making no progress...
 *
 * Note that the deadlocks being demonstrated here can naturally and inherently happen when timing happens to be
 * right, even with monitors that protect normally-very-quick-to-execute critical code sections. This demonstrator
 * simply induces the needed timing quickly and semi-reliably. But even without this intentionally-induced timing,
 * the exact same deadlock conditions can (and will) eventually happen naturally with enough time under load, in
 * the presence of enough monitors being exercised. Natural causes for occasionally elongated execution time while
 * holding a monitor protecting a normally-very-quick-to-execute critical section of code (e.g. throttling in
 * container environments, preemptive time slicing by the operating system when other work is sharing the same
 * vCPU, etc.) will eventually lead to these deadlock situation in real world conditions.
 *
 * Bottom line: Lock priority discipline is a commonly used technique for ensuring that deadlocks are impossible
 * in multi-threaded systems (by systemically preventing the possibility of lock-blocking loops), but this technique
 * can easily fail to protect against deadlocks with Java's the current (as of Java 20) Virtual Threads
 * implementations, leaving most systems with no effective means of preventing deadlocks, or of detecting their
 * potential existence...
 */
public class ThreadDeadLocker {
    static final boolean verbose = false;
    static final Duration doSomethingDuration = Duration.ZERO;
    static final Duration waitLoopSleepDuration = Duration.ofMillis(1);
    static final Duration runnerLoopSleepDuration = Duration.ofMillis(1);

    private static Thread makeThread(String threadName, Runnable runnable, boolean useVirtualThread) {
        if (useVirtualThread) {
            return Thread.ofVirtual().name(threadName).start(runnable);
        }
        return Thread.ofPlatform().name(threadName).start(runnable);
    }

    public static class ChainedThing {
        private static AtomicLong overallCount = new AtomicLong(0);
        private final String name;
        private final Duration duration;
        private final AtomicLong chainCount;
        private ChainedThing nextThing = null;
        private volatile boolean go = false;

        ChainedThing(String name, AtomicLong chainCount, Duration duration) {
            this.name = name;
            this.duration = duration;
            this.chainCount = chainCount;
        }

        void setNextThing(ChainedThing nextThing) {
            this.nextThing = nextThing;
        }

        String getName() {
            return name;
        }

        static long getOverallCount() {
            return overallCount.get();
        }

        long getChainCount() {
            return chainCount.get();
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
                try { Thread.sleep(duration); } catch (InterruptedException e) {}
            }
            if (nextThing != null) {
                nextThing.go();
            }
            overallCount.incrementAndGet();
            chainCount.incrementAndGet();
        }

        public synchronized void waitToDoSomething() {
            while (!go) {
                // wait for go to be set by another thing.
                if (!waitLoopSleepDuration.isZero()) {
                    try { Thread.sleep(waitLoopSleepDuration); } catch (InterruptedException e) {}
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
            while (true) {
                theThing.waitToDoSomething();
                theThing.doSomething();
                if (!runnerLoopSleepDuration.isZero()) {
                    try { Thread.sleep(runnerLoopSleepDuration); } catch (InterruptedException e) {}
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
            AtomicLong chainCount = new AtomicLong();
            // Populate the chain:
            for (int j = 0; j < chainLength; j++) {
                chain.add(new ChainedThing("Thing " + j + " in ThingChain " + i,
                        chainCount,
                        (j == 0) ? doSomethingDuration : Duration.ZERO));
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
            // position starts from end of chain and moves backwards to the beginning:
            int position = chainLength - 1 - i;
            // For each position accross all chains, create a runnable and start a thread for the thing at
            // the position of that chain:
            // (we do this in this semi-strange order so that each chain would have threads started before
            // other chains have all their threads started, and such that the threads started first in each chain
            // would be the last ones to get to proceed when activity starts.
            // This way, in a limited-ability-to-host-threads deadlock-inducing situation  (i.e. Virtual Threads
            // with fewer carrier threads than (2 * chain length * number of chains), no chain will have all of its
            // threads sitting on carrier threads, and no chain will make continued forward progress, even as no thread
            // holds onto any monitor indefinitely if it allowed to proceed with work...
            // (deadlocks can happen at even fewer "thing" counts, but at (2 * chain length * number of chains) they
            // are fairly certain to reliably happen with this runnable start order...)
            for (List<ChainedThing> chain : thingChains) {
                ChainedThing thing = chain.get(position);
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
            long prevOverallCount = 0;
            long[] prevChainCounts = new long[numberOfChains];
            List<Integer> noProgressChains = new ArrayList<>();
            long prevMillis = System.currentTimeMillis();

            while (true) {
                Thread.sleep(Duration.ofSeconds(1));
                long overallCount = ChainedThing.getOverallCount();
                long timeMillis = System.currentTimeMillis();
                double rate = (1000.0 * (double)(overallCount - prevOverallCount)) / (timeMillis - prevMillis);
                noProgressChains.clear();
                for (int i = 0; i < numberOfChains; i++) {
                    List<ChainedThing> chain = thingChains.get(i);
                    long chainCount = chain.get(0).getChainCount();
                    if (chainCount - prevChainCounts[i] == 0) {
                        noProgressChains.add(i);
                    }
                    prevChainCounts[i] = chainCount;
                }
                prevOverallCount = overallCount;
                prevMillis = timeMillis;

                System.out.println("progress count = " + overallCount + ", rate of progress = " + rate + "/sec");
                if (!noProgressChains.isEmpty()) {
                    System.out.print("\tThe following chains showed no progress:");
                    for (Integer chainNumber : noProgressChains) {
                        System.out.print(" " + chainNumber);
                    }
                    System.out.println();
                }
            }
        } catch (InterruptedException e) {
            // bugger out
        }
    }
}
