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


    @Param({"2048"})
    int bufferLength;

    static volatile long baseVal = 0;

    long sum = 0;

    static class MyBuffer {
        final long[] buf;
        long[] nonFinalBuf;
        MyBuffer(int length) {
            this.buf = new long[length];
            this.nonFinalBuf = new long[length];
            for (int i = 0; i < length; i++) {
                buf[i] = nonFinalBuf[i] = i;
            }
        }

        long bufSum1() {
            long sum = 0;
            for (int i = 0; i < buf.length; i++) {
                sum += buf[i] + baseVal;
            }
            return sum;
        }

        long bufSum1NonFinal() {
            long sum = 0;
            for (int i = 0; i < nonFinalBuf.length; i++) {
                sum += nonFinalBuf[i] + baseVal;
            }
            return sum;
        }

        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        long bufSum2() {
            long sum = 0;
            for (int i = 0; i < buf.length; i++) {
                sum += buf[i] + baseVal;
            }
            return sum;
        }

        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        long bufSum2NonFinal() {
            long sum = 0;
            for (int i = 0; i < nonFinalBuf.length; i++) {
                sum += nonFinalBuf[i] + baseVal;
            }
            return sum;
        }

        long bufSum3() {
            long sum = 0;
            for (long b: buf) {
                sum += b + baseVal;
            }
            return sum;
        }

        long bufSum3NonFinal() {
            long sum = 0;
            for (long b: nonFinalBuf) {
                sum += b + baseVal;
            }
            return sum;
        }

        long bufSum4() {
            long sum = 0;
            long[] localBuf = buf;
            for (int i = 0; i < localBuf.length; i++) {
                sum += localBuf[i] + baseVal;
            }
            return sum;
        }

        long bufSum4NonFinal() {
            long sum = 0;
            long[] localBuf = nonFinalBuf;
            for (int i = 0; i < localBuf.length; i++) {
                sum += localBuf[i] + baseVal;
            }
            return sum;
        }

        long bufSum5() {
            long sum = 0;
            long[] localBuf = buf;
            long val = baseVal;
            for (int i = 0; i < localBuf.length; i++) {
                sum += localBuf[i] + val;
            }
            return sum;
        }

        long bufSum5NonFinal() {
            long sum = 0;
            long[] localBuf = nonFinalBuf;
            long val = baseVal;
            for (int i = 0; i < localBuf.length; i++) {
                sum += localBuf[i] + val;
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
    public void sum1Final() {
        sum += buffer.bufSum1();
    }

    @Benchmark
    public void sum1NonFinal() {
        sum += buffer.bufSum1NonFinal();
    }

    @Benchmark
    public void sum2Final() {
        sum += buffer.bufSum2();
    }

    @Benchmark
    public void sum2NonFinal() {
        sum += buffer.bufSum2NonFinal();
    }

    @Benchmark
    public void sum3Final() {
        sum += buffer.bufSum3();
    }

    @Benchmark
    public void sum3NonFinal() {
        sum += buffer.bufSum3NonFinal();
    }

    @Benchmark
    public void sum4Final() {
        sum += buffer.bufSum4();
    }

    @Benchmark
    public void sum4NonFinal() {
        sum += buffer.bufSum4NonFinal();
    }

    @Benchmark
    public void sum5Final() {
        sum += buffer.bufSum5();
    }

    @Benchmark
    public void sum5NonFinal() {
        sum += buffer.bufSum5NonFinal();
    }
}
