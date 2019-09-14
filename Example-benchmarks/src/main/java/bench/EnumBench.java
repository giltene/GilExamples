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

    Number[] numbers;
    City[] cities;


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

    static enum City {
        LOS_ANGELES, SAN_FRANCISCO, SEATTLE, NEW_YORK, ATLANTA, BOSTON;
    }

    boolean isOnEastCoast(City city) {
        switch (city) {
        case LOS_ANGELES:
        case SAN_FRANCISCO:
        case SEATTLE:
            return false;
        default:
            return true;
        }
    }

    @Setup
    public void setup() {
        arrayLength = arrayLengthInKs * 1024;
        numbers = new Number[arrayLength];
        cities = new City[arrayLength];

        for (int i = 0; i < arrayLength; i++) {
            numbers[i] = Number.ONE;
            cities[i] = City.BOSTON;
        }
    }

    @Benchmark
    public void sumEnums() {
        for (int i = 0; i < arrayLength; i++) {
            sum += numberToInt(numbers[i]);
        }
    }

    @Benchmark
    public void sumVolatileEnums() {
        for (int i = 0; i < arrayLength; i++) {
            sum += numberToInt(numbers[i]) + volatileNum;
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

    @Benchmark
    public void sumEastCoastCities() {
        for (int i = 0; i < arrayLength; i++) {
            sum += isOnEastCoast(cities[i]) ? 1 : 0;
        }
    }

    @Benchmark
    public void sumConstantEastCoastCities() {
        for (int i = 0; i < arrayLength; i++) {
            sum += isOnEastCoast(City.BOSTON) ? 1 : 0;
        }
    }
}
