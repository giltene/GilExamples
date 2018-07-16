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

public class VectorizationExampleBench {

    @Param({"1024", "16384", "65536", "524288", "67108864"})
    int arraySize;

    @Param({"1000000000"})
    int elementsPerLoop;

    int[] sumLoopArray;
    int[] addXArray;
    int[] addArraysIfEvenArrayA;
    int[] addArraysIfEvenArrayB;
    boolean[] predicateArray;

    long sum;

    @Setup
    public void setup() throws NoSuchMethodException {

        sumLoopArray = new int[arraySize];
        addXArray = new int[arraySize];
        addArraysIfEvenArrayA = new int[arraySize];
        addArraysIfEvenArrayB = new int[arraySize];
        predicateArray = new boolean[arraySize];

        for (int i = 0; i < arraySize; i++) {
            sumLoopArray[i] = i % 99;
            addXArray[i] = i % 99;
            addArraysIfEvenArrayA[i] = i % 71;
            addArraysIfEvenArrayB[i] = i % 31;
            predicateArray[i] = (i % 2 == 0);
        }
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    private int sumLoop(int[] a) {
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i];
        }
        return sum;
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    private int sumIfEvenLoop(int[] a) {
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            if ((a[i] & 0x1) == 0) {
                sum += a[i];
            }
        }
        return sum;
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    private int sumIfPredicate(int[] a) {
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            if (predicateArray[i]) {
                sum += a[i];
            }
        }
        return sum;
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    private int sumShifted(int shiftRightBy, int maskAfterShift, int a[]) {
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += (a[i] >> shiftRightBy) & maskAfterShift;
        }
        return sum;
    }



    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    private void addXtoArray(int x, int[] a) {
        for (int i = 0; i < a.length; i++) {
            a[i] += x;
        }
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    private void addArraysIfEven(int a[], int b[]) {
        if (a.length != b.length) {
            throw new RuntimeException("length mismatch");
        }
        for (int i = 0; i < a.length; i++) {
            if ((b[i] & 0x1) == 0) {
                a[i] += b[i];
            }
        }
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    private void addArraysIfPredicate(int a[], int b[]) {
        if (a.length != b.length) {
            throw new RuntimeException("length mismatch");
        }
        for (int i = 0; i < a.length; i++) {
            if (predicateArray[i]) {
                a[i] += b[i];
            }
        }
    }


    @Benchmark
    public void doSumLoop() {
        for (int i = 0; i < elementsPerLoop; i += arraySize) {
            sum += sumLoop(sumLoopArray);
        }
    }

    @Benchmark
    public void doSumIfEvenLoop() {
        for (int i = 0; i < elementsPerLoop; i += arraySize) {
            sum += sumIfEvenLoop(sumLoopArray);
        }
    }

    @Benchmark
    public void doSumIfPredicateLoop() {
        for (int i = 0; i < elementsPerLoop; i += arraySize) {
            sum += sumIfPredicate(sumLoopArray);
        }
    }

    @Benchmark
    public void doSumShiftedLoop() {
        for (int i = 0; i < elementsPerLoop; i += arraySize) {
            sum += sumShifted(3, 0x7f, sumLoopArray);
        }
    }

    @Benchmark
    public void doAddX() {
        sum = 0;
        for (int i = 0; i < elementsPerLoop; i += arraySize) {
            addXtoArray(i, addXArray);
        }
        sum = sumLoop(addXArray);
    }

    @Benchmark
    public void doAddArraysIfEven() {
        sum = 0;
        for (int i = 0; i < elementsPerLoop; i += arraySize) {
            addArraysIfEven(addArraysIfEvenArrayA, addArraysIfEvenArrayB);
        }
        sum = sumLoop(addArraysIfEvenArrayA);
    }

    @Benchmark
    public void doAddArraysIfPredicate() {
        sum = 0;
        for (int i = 0; i < elementsPerLoop; i += arraySize) {
            addArraysIfPredicate(addArraysIfEvenArrayA, addArraysIfEvenArrayB);
        }
        sum = sumLoop(addArraysIfEvenArrayA);
    }

//  Useful [only] for keeping the code hot and running for viewing in profiling tools:
//    @Benchmark
    public void doAll() {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            sum += sumLoop(sumLoopArray);
            sum += sumIfEvenLoop(sumLoopArray);
            sum += sumIfPredicate(sumLoopArray);
            sum += sumShifted(3, 0x7f, sumLoopArray);
            addXtoArray(i, addXArray);
            sum += sumLoop(addXArray);
            addArraysIfEven(addArraysIfEvenArrayA, addArraysIfEvenArrayB);
            addArraysIfPredicate(addArraysIfEvenArrayA, addArraysIfEvenArrayB);
            sum += sumLoop(addArraysIfEvenArrayA);
        }
    }

}
