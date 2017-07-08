/**
 * Written by Gil Tene based on code posted by Michael Hixson to the
 * concurrency interest mailing list:
 * http://cs.oswego.edu/pipermail/concurrency-interest/2017-July/015949.html
 */

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicReference;

public class MutableClockWithBoxing {

    public static MutableClockWithBoxing create(final Instant instant, final ZoneId zone) {
        return new MutableClockWithBoxing(
                new AtomicReference<>(new BoxedInstant(instant)),
                zone);
    }

    private final AtomicReference<BoxedInstant> instantHolder;
    private final ZoneId zone;

    private MutableClockWithBoxing(
            final AtomicReference<BoxedInstant> instantHolder,
            final ZoneId zone) {
        this.instantHolder = instantHolder;
        this.zone = zone;
    }

    public Instant instant() {
        return instantHolder.get().getInstant();
    }

    public ZoneId getZone() {
        return zone;
    }

    public void setInstant(final Instant newInstant) {
        BoxedInstant boxedInstant = instantHolder.get();
        boxedInstant.setInstant(newInstant);
        instantHolder.set(boxedInstant); // to force volatile write
    }

    public void add(final Duration amountToAdd) {
        boolean success;
        do {
            BoxedInstant currentBoxedInstance = instantHolder.get();
            BoxedInstant newBoxedInstant =
                    new BoxedInstant(currentBoxedInstance.getInstant().plus(amountToAdd));
            success = instantHolder.compareAndSet(currentBoxedInstance, newBoxedInstant);
        } while (!success);
    }

    public MutableClockWithBoxing withZone(final ZoneId newZone) {
        // conveniently, AtomicReference also acts as a
        // vehicle for "shared updates" between instances:
        return new MutableClockWithBoxing(instantHolder, newZone);
    }

    private static class BoxedInstant {
        private Instant instant;

        BoxedInstant(final Instant instant) {
            setInstant(instant);
        }

        Instant getInstant() {
            return instant;
        }

        void setInstant(final Instant instant) {
            if (instant == null) {
                throw new UnsupportedOperationException("null instants are unsupported");
            }
            this.instant = instant;
        }
    }
}
