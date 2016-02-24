package org.performancehints;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class ThreadHints {

    private static final MethodHandle onSpinWaitMH;

    static {
        MethodHandle mh;
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            mh = lookup.findStatic(java.lang.Thread.class, "onSpinWait", MethodType.methodType(void.class));
        } catch (Exception e) {
            mh = null;
        }
        onSpinWaitMH = mh;
    }

    // prevent construction...
    private ThreadHints() {
    }

    /** Indicates that the caller is momentarily unable to progress, until the
     * occurrence of one or more actions on the part of other activities.  By
     * invoking this method within each iteration of a spin-wait loop construct,
     * the calling thread indicates to the runtime that it is busy-waiting. The runtime
     * may take action to improve the performance of invoking spin-wait loop
     * constructions.
     */
    public static void onSpinWait() {
        // Call java.lang.Runtime.onSpinWait() on Java SE versions that support it. Do nothing otherwise.
        // This should optimize away to either nothing or to an inlining of java.lang.Runtime.onSpinWait()
        if (onSpinWaitMH != null) {
            try {
                onSpinWaitMH.invokeExact();
            } catch (Throwable throwable) {
                // Nothing to do here...
            }
        }
    }
}