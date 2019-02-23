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

public class VarArgsBench {

    long loopCount = 10000;

    int sum1, sum2, sum3, sum4;

    long calc3(int a, int b, int c) {
        return a + b + c;
    }

    long calc3va(final Object... args) {
        int a = (Integer) args[0];
        int b = (Integer) args[1];
        int c = (Integer) args[2];
        return a + b + c;
    }

    long calc4(int a, int b, int c, int d) {
        return a + b + c + d;
    }

    long calc4va(final Object... args) {
        int a = (Integer) args[0];
        int b = (Integer) args[1];
        int c = (Integer) args[2];
        int d = (Integer) args[3];
        return a + b + c + d;
    }

    long calcVa(final Object... args) {
        long val = 0;
        for (Object o : args) {
            val += (Integer) o;
        }
        return val;
    }

    @Setup
    public void setup() throws NoSuchMethodException {
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Benchmark
    public void varArgsBench3() {
        for (int i = 0; i < loopCount; i++) {
            sum1 += calc3(sum1, sum2, sum3);
        }
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Benchmark
    public void varArgsBench3va() {
        for (int i = 0; i < loopCount; i++) {
            sum1 += calc3va(sum1, sum2, sum3);
        }
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Benchmark
    public void varArgsBench3GenericVa() {
        for (int i = 0; i < loopCount; i++) {
            sum1 += calcVa(sum1, sum2, sum3);
        }
    }

    @Benchmark
    public void allVarArgs() {
        for (long i = 0; i < 10000000000000L; i++) {
            varArgsBench3();
            varArgsBench3va();
            varArgsBench3GenericVa();
        }
    }
}
