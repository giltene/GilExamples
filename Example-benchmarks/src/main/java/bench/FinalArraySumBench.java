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

public class FinalArraySumBench {


    @Param({"4096"})
    int bufferLength;

    static volatile long baseVal = 0;

    long sum = 0;

    static class MyBuffer {
        final long[] buf;
        MyBuffer(int length) {
            this.buf = new long[length];
        }

        long bufSum1() {
            long sum = 0;
            for (int i = 0; i < buf.length; i++) {
                sum += buf[i] + baseVal;
            }
            return sum;
        }

        long bufSum2() {
            long sum = 0;
            for (long b: buf) {
                sum += b + baseVal;
            }
            return sum;
        }

        long bufSum3() {
            long sum = 0;
            long[] localBuf = buf;
            for (int i = 0; i < localBuf.length; i++) {
                sum += localBuf[i] + baseVal;
            }
            return sum;
        }
    }

    MyBuffer buffer;

    @Setup
    public void setup() throws NoSuchMethodException {
        buffer = new MyBuffer(bufferLength);

        for (int i = 0; i < bufferLength; i++) {
            buffer.buf[i] = (byte)(i % 31);
        }
    }

    @Benchmark
    public void sum1() {
        sum += buffer.bufSum1();
    }

    @Benchmark
    public void sum2() {
        sum += buffer.bufSum2();
    }

    @Benchmark
    public void sum3() {
        sum += buffer.bufSum3();
    }
}
