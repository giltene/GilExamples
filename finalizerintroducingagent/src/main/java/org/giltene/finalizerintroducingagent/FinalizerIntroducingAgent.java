package org.giltene.finalizerintroducingagent;

import java.lang.instrument.Instrumentation;

public class FinalizerIntroducingAgent {
    static final boolean verbose = Boolean.getBoolean("org.giltene.finalizerintroducingagent.verbose");

    static class ThingWithFinalizer {
        static int instantiationCount = 0;
        static int finalizationCount = 0;

        ThingWithFinalizer() {
            instantiationCount++;
        }

        @Override
        protected void finalize() {
            if (verbose) {
                System.out.println("Finalization detected. finalizationCount = " + finalizationCount);
            }
            finalizationCount++;
        }

        static int getInstantiationCount() {
            return instantiationCount;
        }
        static int getFinalizationCount() {
            return finalizationCount;
        }

    }

    static class AgentThread extends Thread {
        @Override
        public void run() {
            int prevFinalizationCount = ThingWithFinalizer.getFinalizationCount();
            while (true) {
                try {
                    int finalizationCount = ThingWithFinalizer.getFinalizationCount();
                    if (verbose && (finalizationCount != prevFinalizationCount)) {
                        System.out.println("FinalizerIntroducingAgent: new finalization count = " + finalizationCount);
                        prevFinalizationCount = finalizationCount;
                    }
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    // do nothing on interruption,
                }
            }
        }
    }
    static Object[] someThings = new Object[10];

    /**
     * Agent premain entry point.
     * @param args Agent options string
     * @param instrumentation instrumentation provided to the agent
     */
    public static void premain(String args, Instrumentation instrumentation) {

        for (int i = 0; i < someThings.length; i++) {
            someThings[i] = new ThingWithFinalizer(); // will be thrown away and should be finalized eventually
            someThings[i] = new ThingWithFinalizer(); // will stick around.
        }

        AgentThread agentThread = new AgentThread();
        agentThread.setDaemon(true);
        agentThread.start();

        if (verbose) {
            System.out.println("Waiting for finalization:");
            while (ThingWithFinalizer.finalizationCount < 10) {
                System.out.println(".");
                System.gc();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    // do nothing on interruption,
                }
            }
        }
    }
}
