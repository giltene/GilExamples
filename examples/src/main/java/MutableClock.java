/**
 * Written by Gil Tene based on code posted by Michael Hixson to the
 * concurrency interest mailing list:
 * http://cs.oswego.edu/pipermail/concurrency-interest/2017-July/015949.html
 */

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicReference;

public class MutableClock {

    public static MutableClock create(final Instant instant, final ZoneId zone) {
        return new MutableClock(
                new AtomicReference<>(instant),
                zone);
    }

    // Instants are held in an Object field to allow identity-based CAS operations:
    private final AtomicReference<Object> instantHolder;
    private final ZoneId zone;

    private MutableClock(
            final AtomicReference<Object> instantHolder,
            final ZoneId zone) {
        this.instantHolder = instantHolder;
        this.zone = zone;
    }

    public Instant instant() {
        return (Instant) instantHolder.get();
    }

    public ZoneId getZone() {
        return zone;
    }

    public void setInstant(final Instant newInstant) {
        instantHolder.set(newInstant);
    }

    void add(Duration amountToAdd) {
        boolean success = false;
        do {
            Object holderContents = instantHolder.get();
            Instant newInstant = ((Instant) holderContents).plus(amountToAdd);
            // Compare part of CAS would not be valid for an Instant field,
            // but is valid for an Object field:
            success = instantHolder.compareAndSet(holderContents, newInstant);
        } while (!success);

        // the above is equivalent to this, I believe:
        //   instantHolder.updateAndGet(instant -> ((Instant)instant).plus(amountToAdd));
    }

    public MutableClock withZone(final ZoneId newZone) {
        // conveniently, AtomicReference also acts as a
        // vehicle for "shared updates" between instances:
        return new MutableClock(instantHolder, newZone);
    }
}
