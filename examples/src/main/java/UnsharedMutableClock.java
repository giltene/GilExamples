/**
 * Written by Gil Tene based on code posted by Michael Hixson to the
 * concurrency interest mailing list:
 * http://cs.oswego.edu/pipermail/concurrency-interest/2017-July/015949.html
 */

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicReference;

public class UnsharedMutableClock {

    public static UnsharedMutableClock create(final Instant instant, final ZoneId zone) {
        return new UnsharedMutableClock(
                new AtomicReference<>(new BoxedInstant(instant)),
                zone);
    }

    private final AtomicReference<BoxedInstant> instantHolder;
    private final ZoneId zone;
    private Instant cachedInstant;
    private volatile boolean cacheIsValid = false;

    private UnsharedMutableClock(
            final AtomicReference<BoxedInstant> instantHolder,
            final ZoneId zone) {
        this.instantHolder = instantHolder;
        this.zone = zone;
        this.cachedInstant = instantHolder.get().getInstant();
    }

    public Instant instant() {
        if (!cacheIsValid) {
            cachedInstant = instantHolder.get().getInstant();
        }
        return cachedInstant;
    }

    public ZoneId getZone() {
        return zone;
    }

    public void setInstant(final Instant newInstant) {
        instantHolder.get().setInstant(newInstant);
        cacheIsValid = false;
    }

    public void add(final Duration amountToAdd) {
        boolean success;
        do {
            BoxedInstant currentBoxedInstance = instantHolder.get();
            BoxedInstant newBoxedInstant =
                    new BoxedInstant(currentBoxedInstance.getInstant().plus(amountToAdd));
            success = instantHolder.compareAndSet(currentBoxedInstance, newBoxedInstant);
        } while (!success);
        cacheIsValid = false;
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


