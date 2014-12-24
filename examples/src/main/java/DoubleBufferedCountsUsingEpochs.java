import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by gil on 11/25/14.
 */
public class DoubleBufferedCountsUsingEpochs {
    private AtomicLong startEpoch = new AtomicLong(0);
    private AtomicLong evenEndEpoch = new AtomicLong(0);
    private AtomicLong oddEndEpoch = new AtomicLong(Long.MIN_VALUE);

    private long oddCounts[];
    private long evenCounts[];

    private final long accumulatedCounts[];

    public DoubleBufferedCountsUsingEpochs(int size) {
        oddCounts = new long[size];
        evenCounts = new long[size];
        accumulatedCounts = new long[size];
    }

    public void incrementCount(int iTh) {
        boolean phaseIsOdd = (startEpoch.getAndIncrement() < 0);
        if (phaseIsOdd) {
            oddCounts[iTh]++;
            oddEndEpoch.getAndIncrement();
        } else {
            evenCounts[iTh]++;
            evenEndEpoch.getAndIncrement();
        }
    }

    public synchronized long[] getCounts() {
        long sourceArray[];
        long startValueAtFlip;

        // Clear currently unused [next] phase end epoch and set new startEpoch value:
        boolean nextPhaseIsEven = (startEpoch.get() < 0); // Current phase is odd...
        if (nextPhaseIsEven) {
            evenEndEpoch.set(0);
            startValueAtFlip = startEpoch.getAndSet(0);
            sourceArray = oddCounts;
        } else {
            oddEndEpoch.set(Long.MIN_VALUE);
            startValueAtFlip = startEpoch.getAndSet(Long.MIN_VALUE);
            sourceArray = evenCounts;
        }

        // Spin until previous phase end epoch value catches up with start value at flip:
        while ((nextPhaseIsEven && (oddEndEpoch.get() != startValueAtFlip)) ||
                (!nextPhaseIsEven && (evenEndEpoch.get() != startValueAtFlip))) {
            Thread.yield();
        }

        // sourceArray is stable. Use it:
        for (int i = 0; i < sourceArray.length; i++) {
            accumulatedCounts[i] += sourceArray[i];
            sourceArray[i] = 0;
        }

        return accumulatedCounts.clone();
    }
}
