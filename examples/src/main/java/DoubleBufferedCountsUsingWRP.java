import org.HdrHistogram.WriterReaderPhaser;

public class DoubleBufferedCountsUsingWRP {
    WriterReaderPhaser phaser = new WriterReaderPhaser();
    private long activeCounts[];
    private long inactiveCounts[];

    private final long accumulatedCounts[];

    public DoubleBufferedCountsUsingWRP(int size) {
        activeCounts = new long[size];
        inactiveCounts = new long[size];
        accumulatedCounts = new long[size];
    }

    public void incrementCount(int iTh) {
        long criticalValue = phaser.writerCriticalSectionEnter();
        try {
            activeCounts[iTh]++;
        } finally {
            phaser.writerCriticalSectionExit(criticalValue);
        }
    }

    public synchronized long[] getCounts() {
        try {
            phaser.readerLock();

            long tmp[] = activeCounts;
            activeCounts = inactiveCounts;
            inactiveCounts = tmp;

            phaser.flipPhase();

            for (int i = 0; i < inactiveCounts.length; i++) {
                accumulatedCounts[i] += inactiveCounts[i];
                inactiveCounts[i] = 0;
            }
        } finally {
            phaser.readerUnlock();
        }
        return accumulatedCounts.clone();
    }
}
