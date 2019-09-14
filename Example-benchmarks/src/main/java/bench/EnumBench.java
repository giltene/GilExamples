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

    @Param({"8192"})
    int arrayLengthInKs;

    int arrayLength;

    Number[] array;

    int sum = 0;

    volatile int volatileNum = 0;

    static enum Number {
        MINUS_THREE, MINUS_ONE, ZERO, ONE, THREE, FOUR, SEVEN, NINE, TWENTY;
    }

    int numberToInt(Number number) {
        switch (number) {
        case ZERO: return 0;
        case ONE: return 1;
        case THREE: return 3;
        case FOUR: return 4;
        case SEVEN: return 7;
        case NINE: return 9;
        }
        throw new IllegalStateException("An unexpected enum value is being looked up.");
    }

    @Setup
    public void setup() {
        arrayLength = arrayLengthInKs * 1024;
        array = new Number[arrayLength];

        for (int i = 0; i < arrayLength; i++) {
            array[i] = Number.ONE;
        }
    }

    @Benchmark
    public void sumEnums() {
        for (int i = 0; i < arrayLength; i++) {
            sum += numberToInt(array[i]);
        }
    }

    @Benchmark
    public void sumVolatileEnums() {
        for (int i = 0; i < arrayLength; i++) {
            sum += numberToInt(array[i]) + volatileNum;
        }
    }

    @Benchmark
    public void sumConstantEnum() {
        for (int i = 0; i < arrayLength; i++) {
            sum += numberToInt(Number.ONE);
        }
    }

    @Benchmark
    public void sumConstantVolatileEnum() {
        for (int i = 0; i < arrayLength; i++) {
            sum += numberToInt(Number.ONE) + volatileNum;
        }
    }
}
