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

public class SillyIsEqualBench {


    @Param({"1"})
    int valsLengthKBytes;

    int valsLength;

    static final int terminator = 1023;

    int[] vals1;
    int[] vals2;

    long sum;

    @Setup
    public void setup() throws NoSuchMethodException {
        valsLength = valsLengthKBytes * 1024;
        vals1 = new int[valsLength];
        vals2 = new int[valsLength];

        for (int i = 0; i < valsLength; i++) {
            vals1[i] = (byte)(i % 31);
            vals2[i] = (byte)(i % 31) + (((i & 0x1) == 0) ? 5 : 0);
        }
    }

    public static boolean isEqual(int a, int b) {
        return a == b;
    }

    public static boolean sillyIsEqual(int a, int b) {
        try {
            int c = 1 / (a - b);
        } catch (ArithmeticException e) {
            return true;
        }
        return false;
    }


    @Benchmark
    public void equalsLoopNormal() {
        for (int i = 0; i < valsLength; i++) {
            if (isEqual(vals1[i], vals2[i])) {
                sum++;
            }
        }
    }

    @Benchmark
    public void equalsLoopSilly() {
        for (int i = 0; i < valsLength; i++) {
            if (sillyIsEqual(vals1[i], vals2[i])) {
                sum++;
            }
        }
    }
}
