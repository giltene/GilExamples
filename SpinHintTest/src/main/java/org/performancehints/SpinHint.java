package org.performancehints;

public final class SpinHint {

    // sole ctor
    private SpinHint() {}

    /**
     * Provides a hint to the processor that the code sequence is a spin-wait loop.
     */
    public static void spinLoopHint() {
        // intentionally empty
    }

}