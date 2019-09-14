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

public class EnumBench {

    @Param({"1024", "2048", "4096"})
    int arrayLengthInKs;

    int arrayLength;

    Number[] array;

    long sum;

    static enum Number {
        ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX;
        static final Number[] intToNumber = {ZERO, ONE, TWO, THREE, FOUR, FIVE};
    }

    int numberToInt(Number number) {
        switch (number) {
        case ZERO: return 0;
        case ONE: return 1;
        case TWO: return 2;
        case THREE: return 3;
        case FOUR: return 4;
        case FIVE: return 5;
        }
        throw new IllegalStateException("Should never see an enum value not covered by complete enum switch.");
    }

    @Setup
    public void setup() {
        arrayLength = arrayLengthInKs * 1024;
        array = new Number[arrayLength];

        for (int i = 0; i < arrayLength; i++) {
            array[i] = Number.intToNumber[(i % 6)];
        }
    }

    @Benchmark
    public void sumEnums() {
        for (int i = 0; i < arrayLength; i++) {
            sum += numberToInt(array[i]);
        }
    }

    @Benchmark
    public void sumConstantEnum() {
        for (int i = 0; i < arrayLength; i++) {
            sum += numberToInt(Number.ONE);
        }
    }
}
