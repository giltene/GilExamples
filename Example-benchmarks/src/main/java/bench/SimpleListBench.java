/**
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */

package bench;

import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
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

public class SimpleListBench {


    static final int listLength = 2001;

    static final int terminator = listLength - 1;

    static final List<Integer> integerList = new ArrayList<>(listLength);

    long sum;

    @Setup
    public void setup() throws NoSuchMethodException {
        for (int i = 0; i < listLength; i++) {
            integerList.add(i % 31);
        }

        integerList.set(listLength - 1, terminator);
        System.gc();
        System.gc();
        System.gc();
//        integerList.set(32, terminator);
    }

    @Benchmark
    public void integerListSum() {
        for (Integer integer : integerList) {
            sum += integer;
        }
    }

    @Benchmark
    public void integerListSearchForStaticTerminationValue() {
        for (Integer integer : integerList) {
            if (integer == terminator)
                break;
            sum += integer;
        }
    }

    @Benchmark
    public void integerListIndexLoopSearchForStaticTerminationValue() {
        for (int i = 0; i < integerList.size(); i++) {
            if (integerList.get(i) == terminator)
                break;
            sum += integerList.get(i);
        }
    }

    @Benchmark
    public void integerListSearchForNonStaticTerminationValue() {
        int termValue = integerList.get(integerList.size() - 1);
        for (Integer integer : integerList) {
            if (integer == termValue)
                break;
            sum += integer;
        }
    }

    @Benchmark
    public void integerListIndexLoopSearchForNonStaticTerminationValue() {
        int termValue = integerList.get(integerList.size() - 1);
        for (int i = 0; i < integerList.size(); i++) {
            if (integerList.get(i) == termValue)
                break;
            sum += integerList.get(i);
        }
    }
}
