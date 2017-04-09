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
@Fork(3)
@State(Scope.Thread)

public class MethodInliningExampleBench {

    @Param({"1"})
    int sleepArg;

    @Param({"100000"})
    int benchLoopCount;
    

    public static void mySleep0(int t) {
        int x = 0;
        for(int i = 0; i < t * 10000; i++) {
            x += (t ^ x) % 93;
        }
        return;
    }

    public static int mySleep1(int t) {
        int x = 0;
        for(int i = 0; i < t * 10000; i++) {
            x += (t ^ x) % 93;
        }
        return x;
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public static int mySleep2(int t) {
        int x = 0;
        for(int i = 0; i < t * 10000; i++) {
            x += (t ^ x) % 93;
        }
        return x;
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public static void mySleep3(int t) {
        int x = 0;
        for(int i = 0; i < t * 10000; i++) {
            x += (t ^ x) % 93;
        }
        return;
    }

    public static void mySleepL0(long t) {
        long x = 0;
        for(int i = 0; i < t * 10000; i++) {
            x += (t ^ x) % 93;
        }
        return;
    }

    public static long mySleepL1(long t) {
        long x = 0;
        for(int i = 0; i < t * 10000; i++) {
            x += (t ^ x) % 93;
        }
        return x;
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public static long mySleepL2(long t) {
        long x = 0;
        for(int i = 0; i < t * 10000; i++) {
            x += (t ^ x) % 93;
        }
        return x;
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public static void mySleepL3(long t) {
        long x = 0;
        for(int i = 0; i < t * 10000; i++) {
            x += (t ^ x) % 93;
        }
        return;
    }

    @Benchmark
    public void doMySleep0() {
        for (int i = 0; i < benchLoopCount; i++) {
            mySleep0(sleepArg);
        }
    }

    @Benchmark
    public void doMySleep1() {
        for (int i = 0; i < benchLoopCount; i++) {
            mySleep1(sleepArg);
        }
    }

    @Benchmark
    public void doMySleep2() {
        for (int i = 0; i < benchLoopCount; i++) {
            mySleep2(sleepArg);
        }
    }

    @Benchmark
    public void doMySleep3() {
        for (int i = 0; i < benchLoopCount; i++) {
            mySleep3(sleepArg);
        }
    }

    @Benchmark
    public void doMySleepL0() {
        for (int i = 0; i < benchLoopCount; i++) {
            mySleepL0(sleepArg);
        }
    }

    @Benchmark
    public void doMySleepL1() {
        for (int i = 0; i < benchLoopCount; i++) {
            mySleepL1(sleepArg);
        }
    }

    @Benchmark
    public void doMySleepL2() {
        for (int i = 0; i < benchLoopCount; i++) {
            mySleepL2(sleepArg);
        }
    }

    @Benchmark
    public void doMySleepL3() {
        for (int i = 0; i < benchLoopCount; i++) {
            mySleepL3(sleepArg);
        }
    }

}
