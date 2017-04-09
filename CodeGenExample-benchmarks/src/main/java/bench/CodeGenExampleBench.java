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

public class CodeGenExampleBench {

    class ListElement {
        long value;
        ListElement next;
    }

    @Param({"4096"})
    int arraySize;

    @Param({"1000000"})
    int loopCount;

    @Param({"1000"})
    int benchLoopCount;

    int[] sumLoopArray;
    int[] addXArray;
    int[] addArraysIfEvenArrayA;
    int[] addArraysIfEvenArrayB;
    int[] trueIfMaskMatchedArray;

    ListElement list;

    long sum;

    @Setup
    public void setup() throws NoSuchMethodException {

        sumLoopArray = new int[arraySize];
        addXArray = new int[arraySize];
        addArraysIfEvenArrayA = new int[arraySize];
        addArraysIfEvenArrayB = new int[arraySize];
        trueIfMaskMatchedArray = new int[arraySize];
        list = new ListElement();

        for (int i = 0; i < arraySize; i++) {
            sumLoopArray[i] = i % 99;
            addXArray[i] = i % 99;
            addArraysIfEvenArrayA[i] = i % 71;
            addArraysIfEvenArrayB[i] = i % 31;
            trueIfMaskMatchedArray[i] = i % 103;

            // Populate list:
            ListElement head = list;
            list = new ListElement();
            list.next = head;
            list.value = i + 1;
        }
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    private int listSumLoop(final ListElement list) {
        int sum = 0;
        ListElement e = list;
        while (e != null) {
            sum += e.value;
            e = e.next;
        }
        return sum;
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

    public void loopUbench0(int count) {
        long sum = 0;
        for (int i = 0; i < count; i++) {
            sum++;
        }
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)

    public void loopUbench1(int count) {
        long sum = 0;
        for (int i = 0; i < count; i++) {
            sum += i;
        }
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)

    public long loopUbench2(int count) {
        long sum = 0;
        for (int i = 0; i < count; i++) {
            sum++;
        }
        return sum;
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)

    public long loopUbench3(int count) {
        long sum = 0;
        for (int i = 0; i < count; i++) {
            sum += i;
        }
        return sum;
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)

    public long loopUbench4(int count) {
        long sum = 0;
        for (int i = 0; i < count; i++) {
            sum *= i;
        }
        return sum;
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    
    public long loopUbench5(int count) {
        long sum = 0;
        for (int i = 0; i < count; i++) {
            sum += i + ((i - 3) & 0x7);
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
    private boolean trueIfMaskMatched(int mask, int value, int a[]) {
        boolean found = false;
        for (int i = 0; i < a.length; i++) {
            if ((a[i] & mask) == value) {
                found = true;
            }
        }
        return found;
    }

    @Benchmark
    public void doUbenchLoop0() {
        for (int i = 0; i < benchLoopCount; i++) {
            loopUbench0(loopCount);
        }
    }

    @Benchmark
    public void doUbenchLoop1() {
        for (int i = 0; i < benchLoopCount; i++) {
            loopUbench1(loopCount);
        }
    }

    @Benchmark
    public void doUbenchLoop2() {
        for (int i = 0; i < benchLoopCount; i++) {
            sum+= loopUbench2(loopCount);
        }
    }

    @Benchmark
    public void doUbenchLoop3() {
        for (int i = 0; i < benchLoopCount; i++) {
            sum+= loopUbench3(loopCount);
        }
    }

    @Benchmark
    public void doUbenchLoop4() {
        for (int i = 0; i < benchLoopCount; i++) {
            sum+= loopUbench4(loopCount);
        }
    }

    @Benchmark
    public void doUbenchLoop5() {
        for (int i = 0; i < benchLoopCount; i++) {
            sum+= loopUbench5(loopCount);
        }
    }

    @Benchmark
    public void doUbenchLoops() {
        for (int i = 0; i < benchLoopCount; i++) {
            loopUbench0(loopCount);
            loopUbench1(loopCount);
            sum += loopUbench2(loopCount);
            sum += loopUbench3(loopCount);
            sum += loopUbench4(loopCount);
        }
    }

    @Benchmark
    public void doListSumLoop() {
        for (int i = 0; i < loopCount; i++) {
            sum += listSumLoop(list);
        }
    }

    @Benchmark
    public void doSumLoop() {
        for (int i = 0; i < loopCount; i++) {
            sum += sumLoop(sumLoopArray);
        }
    }

    @Benchmark
    public void doSumIfEvenLoop() {
        for (int i = 0; i < loopCount; i++) {
            sum += sumIfEvenLoop(sumLoopArray);
        }
    }

    @Benchmark
    public void doSumShiftedLoop() {
        for (int i = 0; i < loopCount; i++) {
            sum += sumShifted(3, 0x7f, sumLoopArray);
        }
    }

    @Benchmark
    public void doAddX() {
        sum = 0;
        for (int i = 0; i < loopCount; i++) {
            addXtoArray(i, addXArray);
        }
        sum = sumLoop(addXArray);
    }

    @Benchmark
    public void doAddArraysIfEven() {
        sum = 0;
        for (int i = 0; i < loopCount; i++) {
            addArraysIfEven(addArraysIfEvenArrayA, addArraysIfEvenArrayB);
        }
        sum = sumLoop(addArraysIfEvenArrayA);
    }

    @Benchmark
    public void doAddIfEven() {
        sum = 0;
        for (int i = 0; i < loopCount; i++) {
            if (trueIfMaskMatched(0x33, 0x11, trueIfMaskMatchedArray)) {
                sum++;
            }
        }
    }

    @Benchmark
    public void doAll() {
        for (int i = 0; i < benchLoopCount; i++) {
            doUbenchLoops();
            doListSumLoop();
            doSumLoop();
            doSumIfEvenLoop();
            doSumShiftedLoop();
            doAddX();
            doAddArraysIfEven();
            doAddIfEven();
        }
    }
}
