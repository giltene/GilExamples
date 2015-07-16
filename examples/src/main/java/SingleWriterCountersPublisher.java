/**
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */

import java.util.concurrent.atomic.AtomicLong;
import org.HdrHistogram.WriterReaderPhaser;

/**
 * Publishes a set counter values. Provides a stable, consistent view of those
 * live published counters without interrupting or stalling active publishing of values.
 * <p>
 * This pattern is commonly used in logging a set of counters that need to be viewed consistently as
 * a set.
 * <p>
 * {@link SingleWriterCountersPublisher} expects only a single thread (the "single writer") to
 * call {@link SingleWriterCountersPublisher#publicCounters(Counters)} or
 * {@link SingleWriterCountersPublisher#publicCounters(long[])} at any point in time.
 * It DOES NOT safely support concurrent publishing calls.
 */

public class SingleWriterCountersPublisher {

    public class Counters {
        private long[] counterValues;
        private long observationTimeStamp;

        public Counters(final int numberOfCounters) {
            counterValues = new long[numberOfCounters];
        }

        public long[] getCounterValues() {
            return counterValues;
        }

        public long getObservationTimeStamp() {
            return observationTimeStamp;
        }

        void setObservationTimeStamp(final long timeStamp) {
            this.observationTimeStamp = timeStamp;
        }

        void copyInto(Counters targetCounters) {
            final long[] targetCounterValues = targetCounters.getCounterValues();
            final int length = Math.min(counterValues.length, targetCounterValues.length);
            for (int i = 0; i < length; i++) {
                targetCounterValues[i] = counterValues[i];
            }
            targetCounters.setObservationTimeStamp(observationTimeStamp);
        }
    }

    private static AtomicLong instanceIdSequencer = new AtomicLong(1);
    private final long instanceId = instanceIdSequencer.getAndIncrement();

    private final WriterReaderPhaser recordingPhaser = new WriterReaderPhaser();

    private volatile InternalCounters activeCounters;
    private InternalCounters inactiveCounters;

    /**
     * Construct a {@link SingleWriterCountersPublisher} used to publish a given number of long counter values.
     *
     * @param numberOfCounters The number of long counter values
     *
     */
    public SingleWriterCountersPublisher(final int numberOfCounters) {
        activeCounters = new InternalCounters(instanceId, numberOfCounters);
        inactiveCounters = new InternalCounters(instanceId, numberOfCounters);
    }

    /**
     * Publish a set of counters
     * @param counters the counters tonpublish
     */
    public void publicCounters(final Counters counters) {
        long criticalValueAtEnter = recordingPhaser.writerCriticalSectionEnter();
        try {
            counters.copyInto(activeCounters);
        } finally {
            recordingPhaser.writerCriticalSectionExit(criticalValueAtEnter);
        }
    }

    /**
     * Publish a set of counter values
     * @param counterValues the counter values to publish
     */
    public void publicCounters(final long[] counterValues) {
        long criticalValueAtEnter = recordingPhaser.writerCriticalSectionEnter();
        try {
            final long[] counters = activeCounters.getCounterValues();
            final int length = Math.min(counters.length, counterValues.length);
            for (int i = 0; i < length; i++) {
                counters[i] = counterValues[i];
            }
        } finally {
            recordingPhaser.writerCriticalSectionExit(criticalValueAtEnter);
        }
    }

    /**
     * Get an instance of the latest published Counters
     *
     * @return a Counters instance containing the value counts last published.
     */
    public synchronized Counters getCounters() {
        return getCounters(null);
    }

    /**
     * Get an Counters instance, which will include a stable, consistent view of all value counts
     * that were last published.
     * <p>
     * {@link SingleWriterCountersPublisher#getCounters(SingleWriterCountersPublisher.Counters countersToRecycle)
     * getCounters(countersToRecycle)}
     * accepts a previously returned Counters instance that can be recycled internally to avoid allocation
     * and content copying operations, and is therefore significantly more efficient for repeated use than
     * {@link SingleWriterCountersPublisher#getCounters()} and
     * {@link SingleWriterCountersPublisher#getCountersInto getCountersInto()}. The provided
     * {@code countersToRecycle} must
     * be either be null or a {@link SingleWriterCountersPublisher.Counters} instance returned by a previous call to
     * {@link SingleWriterCountersPublisher#getCounters(Counters countersToRecycle)
     * getCounters(countersToRecycle)} or
     * {@link SingleWriterCountersPublisher#getCounters()}.
     * <p>
     * NOTE: The caller is responsible for not recycling the same returned Counters instance more than once. If
     * the same Counters instance is recycled more than once, behavior is undefined.
     * <p>
     *
     * @param countersToRecycle a previously returned Counters instance that may be recycled to avoid allocation and
     *                           copy operations.
     * @return a Counters instance containing the values last published.
     */
    public synchronized Counters getCounters(Counters countersToRecycle) {
        if (countersToRecycle == null) {
            countersToRecycle = new InternalCounters(inactiveCounters);
        }
        // Verify that replacement histogram can validly be used as an inactive histogram replacement:
        validateFitAsReplacementCounters(countersToRecycle);
        try {
            recordingPhaser.readerLock();
            inactiveCounters = (InternalCounters) countersToRecycle;
            performIntervalSample();
            return inactiveCounters;
        } finally {
            recordingPhaser.readerUnlock();
        }
    }

    /**
     * Place a copy of the last published counters into {@code targetCounters}.
     *
     * @param targetCounters the Counters instance into which the published counters should be copied
     */
    public synchronized void getCountersInto(Counters targetCounters) {
        performIntervalSample();
        inactiveCounters.copyInto(targetCounters);
    }

    private void performIntervalSample() {
        try {
            recordingPhaser.readerLock();

            // Swap active and inactive counters:
            final InternalCounters tempHistogram = inactiveCounters;
            inactiveCounters = activeCounters;
            activeCounters = tempHistogram;

            // Mark end time of previous interval and start time of new one:
            long now = System.currentTimeMillis();
            inactiveCounters.setObservationTimeStamp(now);

            // Make sure we are not in the middle of recording a value on the previously active histogram:

            // Flip phase to make sure no recordings that were in flight pre-flip are still active:
            recordingPhaser.flipPhase(500000L /* yield in 0.5 msec units if needed */);
        } finally {
            recordingPhaser.readerUnlock();
        }
    }

    private class InternalCounters extends Counters {
        private final long containingInstanceId;

        private InternalCounters(final long id, final int numberOfCounters) {
            super(numberOfCounters);
            this.containingInstanceId = id;
        }

        private InternalCounters(InternalCounters source) {
            super(source.getCounterValues().length);
            this.containingInstanceId = source.containingInstanceId;
        }
    }

    void validateFitAsReplacementCounters(Counters replacementCounters) {
        boolean bad = true;
        if ((replacementCounters instanceof InternalCounters)
                &&
                (((InternalCounters) replacementCounters).containingInstanceId ==
                        activeCounters.containingInstanceId)
                ) {
            bad = false;
        }

        if (bad) {
            throw new IllegalArgumentException("replacement counters must have been obtained via a previous" +
                    "getCounters() call from this " + this.getClass().getName() +" instance");
        }
    }
}