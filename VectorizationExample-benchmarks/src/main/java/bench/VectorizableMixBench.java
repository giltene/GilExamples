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

public class VectorizableMixBench {

    @Param({"1024"})
    int arraySize;

    @Param({"1000000000"})
    int elementsPerLoop;

    @Param({"10000"})
    int warmupLoopCount;

    int[] sumLoopArray;

    volatile long sum;


    @Setup
    public void setup() throws NoSuchMethodException {

        sumLoopArray = new int[arraySize];

        for (int i = 0; i < arraySize; i++) {
            sumLoopArray[i] = i % 99;
        }

        for (int i = 0; i < warmupLoopCount; i++) {
            sum += vectorizableLoop(sumLoopArray);
        }
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    private int vectorizableLoop(int[] a) {
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i];
        }
        return sum;
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    private int scalarLoop(int count) {
        int sum = 0;
        for (int i = 0; i < count; i++) {
            sum += i + ((i -3) & sum);  // For now, no JIT I know of optimizes this away or vectorizes it.
        }
        return sum;
    }

    @Benchmark
    public void doVectorizableLoop() {
        for (int i = 0; i < elementsPerLoop; i += arraySize) {
            sum += vectorizableLoop(sumLoopArray);
        }
    }

    @Benchmark
    public void doScalarLoop() {
        for (int i = 0; i < elementsPerLoop; i += arraySize) {
            sum += scalarLoop(sumLoopArray.length);
        }
    }

    @Benchmark
    public void doMixLoop() {
        for (int i = 0; i < elementsPerLoop; i += arraySize) {
            sum += scalarLoop(sumLoopArray.length);
            sum += vectorizableLoop(sumLoopArray);
        }
    }

}
