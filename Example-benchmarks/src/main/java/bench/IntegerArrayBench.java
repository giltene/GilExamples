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

public class IntegerArrayBench {


    static final int arrayLength = 2048;

    static final int terminator = 1023;

    static final Integer[] integerArray = new Integer[arrayLength];

    long sum;

    @Setup
    public void setup() throws NoSuchMethodException {
//        integerArray = new Integer[arrayLength];

        for (int i = 0; i < arrayLength; i++) {
            integerArray[i] = (i % 31);
        }

        integerArray[arrayLength - 1] = terminator;
    }

    @Benchmark
    public void integerArraySum() {
        for (Integer integer : integerArray) {
            sum += integer;
        }
    }

    @Benchmark
    public void integerArraySearchForStaticTerminationValue() {
        for (int i = 0; i < integerArray.length; i++) {
            if (integerArray[i] == terminator)
                break;
            sum += integerArray[i];
        }
    }

    @Benchmark
    public void integerArraySearchForNonStaticTerminationValue() {
        int termValue = integerArray[integerArray.length - 1];
        for (int i = 0; i < integerArray.length; i++) {
            if (integerArray[i] == termValue)
                break;
            sum += integerArray[i];
        }
    }
}
