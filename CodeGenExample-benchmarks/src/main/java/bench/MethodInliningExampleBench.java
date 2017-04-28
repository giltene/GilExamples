/**
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */

package bench;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/*
  Run all benchmarks:
    $ java -jar target/benchmarks.jar

  Run selected benchmarks:
    $ java -jar target/benchmarks.jar (regexp)

  Run the profiling (Linux only):
     $ java -Djmh.perfasm.events=cycles,cache-misses -jar target/benchmarks.jar -f 1 -prof perfasm
 */
@Warmup(iterations = 7, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Thread)

public class MethodInliningExampleBench {

    @Param({"1"})
    int sleepArg;

    @Param({"100000"})
    int benchLoopCount;
    

    public static void noRetValInnerIntLoop(int t) {
        int x = 0;
        for(int i = 0; i < t * 10000; i++) {
            x += (t ^ x) % 93;
        }
        return;
    }

    public static int retValInnerIntLoop(int t) {
        int x = 0;
        for(int i = 0; i < t * 10000; i++) {
            x += (t ^ x) % 93;
        }
        return x;
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public static int retValInnerIntLoopNoInlining(int t) {
        int x = 0;
        for(int i = 0; i < t * 10000; i++) {
            x += (t ^ x) % 93;
        }
        return x;
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public static void noRetValInnerIntLoopNoInlining(int t) {
        int x = 0;
        for(int i = 0; i < t * 10000; i++) {
            x += (t ^ x) % 93;
        }
        return;
    }

    public static void noRetValInnerLongLoop(long t) {
        long x = 0;
        for(int i = 0; i < t * 10000; i++) {
            x += (t ^ x) % 93;
        }
        return;
    }

    public static long retValInnerLongLoop(long t) {
        long x = 0;
        for(int i = 0; i < t * 10000; i++) {
            x += (t ^ x) % 93;
        }
        return x;
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public static long retValInnerLongLoopNoInlining(long t) {
        long x = 0;
        for(int i = 0; i < t * 10000; i++) {
            x += (t ^ x) % 93;
        }
        return x;
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public static void noRetValInnerLongLoopNoInlining(long t) {
        long x = 0;
        for(int i = 0; i < t * 10000; i++) {
            x += (t ^ x) % 93;
        }
        return;
    }

    @Benchmark
    public void noRetValIntLoop() {
        for (int i = 0; i < benchLoopCount; i++) {
            noRetValInnerIntLoop(sleepArg);
        }
    }

    @Benchmark
    public void retValIntLoop() {
        for (int i = 0; i < benchLoopCount; i++) {
            retValInnerIntLoop(sleepArg);
        }
    }

    @Benchmark
    public void retValIntLoopNoInlining() {
        for (int i = 0; i < benchLoopCount; i++) {
            retValInnerIntLoopNoInlining(sleepArg);
        }
    }

    @Benchmark
    public void noRetValIntLoopNoInlining() {
        for (int i = 0; i < benchLoopCount; i++) {
            noRetValInnerIntLoopNoInlining(sleepArg);
        }
    }

    @Benchmark
    public void noRetValLongLoop() {
        for (int i = 0; i < benchLoopCount; i++) {
            noRetValInnerIntLoop(sleepArg);
        }
    }

    @Benchmark
    public void retValLongLoop() {
        for (int i = 0; i < benchLoopCount; i++) {
            retValInnerLongLoop(sleepArg);
        }
    }

    @Benchmark
    public void retValLongLoopNoInlining() {
        for (int i = 0; i < benchLoopCount; i++) {
            retValInnerLongLoopNoInlining(sleepArg);
        }
    }

    @Benchmark
    public void noRetValLongLoopNoInlining() {
        for (int i = 0; i < benchLoopCount; i++) {
            noRetValInnerLongLoopNoInlining(sleepArg);
        }
    }

}
