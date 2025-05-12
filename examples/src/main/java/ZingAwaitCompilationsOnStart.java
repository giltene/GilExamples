/**
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */

import java.lang.management.*;
import javax.management.*;

/**
 * A utility for Azul Zing JVMs, useful for awaiting the draining of JIT compilation
 * queues at program start. This utility may be used by e.g. startup or readiness
 * probes to have code better optimized before traffic actually starts.
 *
 * compileQueueDepthOrDeadlineReached(targetDepth, deadlineSinceStartMs)
 *   → true  if  (TotalOutstandingCompiles ≤ targetDepth) OR
 *              (uptime ≥ deadlineSinceStartMs)
 *   → false otherwise.
 *
 * The demo main() can be used as an example.
 */
public final class ZingAwaitCompilationsOnStart {

    private static final long JVM_START =
            ManagementFactory.getRuntimeMXBean().getStartTime();

    private static final MBeanServer MBS =
            ManagementFactory.getPlatformMBeanServer();

    private static final boolean IS_ZING =
            System.getProperty("java.vm.name", "").toLowerCase().contains("zing");

    private static final ObjectName COMPILATION_OBJECTNAME;
    private static final boolean   COMPILATION_MXBEAN_AVAILABLE;

    static {
        ObjectName objectName = null;
        boolean    mxbeanAvailable = false;

        if (IS_ZING) {
            try {
                objectName = new ObjectName("com.azul.zing:type=Compilation");
                mxbeanAvailable = MBS.isRegistered(objectName);
            } catch (MalformedObjectNameException e) {
                e.printStackTrace(System.err);
            }
        }
        COMPILATION_OBJECTNAME       = objectName;
        COMPILATION_MXBEAN_AVAILABLE = mxbeanAvailable;
    }

    private ZingAwaitCompilationsOnStart() {}

    /**
     * @param targetDepth                 queue size (inclusive) that satisfies the condition
     * @param deadlineSinceStartMs        absolute deadline counted from JVM start (ms)
     * @param returnImmediatelyIfNotZing  true → return {@code true} right away on non-Zing JVMs
     */
    public static boolean compileQueueDepthOrDeadlineReached(int targetDepth,
                                                             long deadlineSinceStartMs,
                                                             boolean returnImmediatelyIfNotZing) {

        if (System.currentTimeMillis() - JVM_START >= deadlineSinceStartMs) {
            return true;
        }

        if (!IS_ZING) {
            return returnImmediatelyIfNotZing;
        }
        if (!COMPILATION_MXBEAN_AVAILABLE) {
            return false;
        }

        try {
            long outstandingCompiles = ((Number) MBS.getAttribute(
                    COMPILATION_OBJECTNAME, "TotalOutstandingCompiles")).longValue();
            return outstandingCompiles <= targetDepth;
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {

        int  targetDepth = 0;        // wait until the queue empties
        long deadlineMs  = 120_000;  // 120 s from JVM start

        long startSinceStart = System.currentTimeMillis() - JVM_START;
        System.out.println("Await loop started at JVM-uptime(ms): " + startSinceStart);

        while (!compileQueueDepthOrDeadlineReached(targetDepth, deadlineMs, true)) {
            try { Thread.sleep(1); }
            catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        long endSinceStart = System.currentTimeMillis() - JVM_START;
        System.out.println("Condition met at JVM-uptime(ms): " + endSinceStart);
    }
}
