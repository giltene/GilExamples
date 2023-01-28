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
@Warmup(iterations = 7, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@State(Scope.Thread)

public class FinalArraySumBench {


    @Param({"2048"})
    int bufferLength;

    static volatile long baseVal = 0;
    
    long sum = 0;

    static class MyBuffer {
        private final long[] finalBuf;
        private long[] nonFinalBuf;
        private long[] effectivelyFinalBuf;

        MyBuffer(int length) {
            this.finalBuf = new long[length];
            this.effectivelyFinalBuf = new long[length];
            this.nonFinalBuf = finalBuf;
            for (int i = 0; i < length; i++) {
                finalBuf[i] = effectivelyFinalBuf[i] = i;
            }
        }

        public void setNonFinalBuf(long[] nonFinalBuf) {
            this.nonFinalBuf = nonFinalBuf;
        }

        public long[] getFinalBuf() {
            return finalBuf;
        }

        public long[] getEffectivelyFinalBuf() {
            return effectivelyFinalBuf;
        }

        long bufSumCStyleVolatileValTrulyFinalArray() {
            long sum = 0;
            for (int i = 0; i < finalBuf.length; i++) {
                sum += finalBuf[i] + baseVal;
            }
            return sum;
        }

        long bufSumCStyleVolatileValEffectivelyFinalArray() {
            long sum = 0;
            for (int i = 0; i < effectivelyFinalBuf.length; i++) {
                sum += effectivelyFinalBuf[i] + baseVal;
            }
            return sum;
        }

        long bufSumCStyleVolatileValNonFinalArray() {
            long sum = 0;
            for (int i = 0; i < nonFinalBuf.length; i++) {
                sum += nonFinalBuf[i] + baseVal;
            }
            return sum;
        }

        long bufSumIterationStyleVolatileValTrulyFinalArray() {
            long sum = 0;
            for (long b: finalBuf) {
                sum += b + baseVal;
            }
            return sum;
        }

        long bufSumIterationStyleVolatileValNonFinalArray() {
            long sum = 0;
            for (long b: nonFinalBuf) {
                sum += b + baseVal;
            }
            return sum;
        }

        long bufSumCStyleVolatileValLocalArray() {
            long sum = 0;
            long[] localBuf = finalBuf;
            for (int i = 0; i < localBuf.length; i++) {
                sum += localBuf[i] + baseVal;
            }
            return sum;
        }

        long bufSumCStyleLocalValLocalArray() {                          
            long sum = 0;
            long[] localBuf = finalBuf;
            long val = baseVal;
            for (int i = 0; i < localBuf.length; i++) {
                sum += localBuf[i] + val;
            }
            return sum;
        }

        long bufSumCStyleLocalValTrulyFinalArray() {
            long sum = 0;
            long localBaseVal = baseVal;
            for (int i = 0; i < finalBuf.length; i++) {
                sum += finalBuf[i] + localBaseVal;
            }
            return sum;
        }

        long bufSumCStyleLocalValEffectivelyFinalArray() {
            long sum = 0;
            long localBaseVal = baseVal;
            for (int i = 0; i < effectivelyFinalBuf.length; i++) {
                sum += effectivelyFinalBuf[i] + localBaseVal;
            }
            return sum;
        }

        long bufSumCStyleLocalValNonFinalArray() {
            long sum = 0;
            long localBaseVal = baseVal;
            for (int i = 0; i < nonFinalBuf.length; i++) {
                sum += nonFinalBuf[i] + localBaseVal;
            }
            return sum;
        }

        long bufSumCStylePartiallyLocalValTrulyFinalArray() {
            long sum = 0;
            long localBaseVal = baseVal;
            for (int i = 0; i < finalBuf.length; i++) {
                if ((i & 0xf) == 0) {
                    localBaseVal = baseVal;
                }
                sum += finalBuf[i] + localBaseVal;
            }
            return sum;
        }

        long bufSumCStylePartiallyLocalValEffectivelyFinalArray() {
            long sum = 0;
            long localBaseVal = baseVal;
            for (int i = 0; i < effectivelyFinalBuf.length; i++) {
                if ((i & 0xf) == 0) {
                    localBaseVal = baseVal;
                }
                sum += effectivelyFinalBuf[i] + localBaseVal;
            }
            return sum;
        }

        long bufSumCStylePartiallyLocalValNonFinalArray() {
            long sum = 0;
            long localBaseVal = baseVal;
            for (int i = 0; i < nonFinalBuf.length; i++) {
                if ((i & 0xf) == 0) {
                    localBaseVal = baseVal;
                }
                sum += nonFinalBuf[i] + localBaseVal;
            }
            return sum;
        }
    }

    MyBuffer buffer;

    @Setup
    public void setup() throws NoSuchMethodException {
        buffer = new MyBuffer(bufferLength);

        for (int i = 0; i < bufferLength; i++) {
            buffer.finalBuf[i] = (byte)(i % 31);
        }

        buffer.setNonFinalBuf(buffer.getEffectivelyFinalBuf());
        buffer.setNonFinalBuf(buffer.getFinalBuf());
        System.gc();
        System.gc();
        System.gc();
    }

    @Benchmark
    public void arraySumCStyleVolatileValTrulyFinalArray() {
        sum += buffer.bufSumCStyleVolatileValTrulyFinalArray();
    }

    @Benchmark
    public void arraySumCStyleVolatileValEffectivelyFinalArray() {
        sum += buffer.bufSumCStyleVolatileValEffectivelyFinalArray();
    }

    @Benchmark
    public void arraySumCStyleVolatileValNonFinalArray() {
        sum += buffer.bufSumCStyleVolatileValNonFinalArray();
    }

    @Benchmark
    public void arraySumIterationStyleVolatileValTrulyFinalArray() {
        sum += buffer.bufSumIterationStyleVolatileValTrulyFinalArray();
    }

    @Benchmark
    public void arraySumIterationStyleVolatileValNonFinalArray() {
        sum += buffer.bufSumIterationStyleVolatileValNonFinalArray();
    }

    @Benchmark
    public void arraySumCStyleVolatileValLocalArray() {
        sum += buffer.bufSumCStyleVolatileValLocalArray();
    }

    @Benchmark
    public void arraySumCStyleLocalValLocalArray() {
        sum += buffer.bufSumCStyleLocalValLocalArray();
    }

    @Benchmark
    public void arraySumCStyleLocalValTrulyFinalArray() {
        sum += buffer.bufSumCStyleLocalValTrulyFinalArray();
    }

    @Benchmark
    public void arraySumCStyleLocalValEffectivelyFinalArray() {
        sum += buffer.bufSumCStyleLocalValEffectivelyFinalArray();
    }

    @Benchmark
    public void arraySumCStyleLocalValNonFinalArray() {
        sum += buffer.bufSumCStyleLocalValNonFinalArray();
    }

    @Benchmark
    public void arraySumCStylePartiallyLocalValTrulyFinalArray() {
        sum += buffer.bufSumCStylePartiallyLocalValTrulyFinalArray();
    }

    @Benchmark
    public void arraySumCStylePartiallyLocalValEffectivelyFinalArray() {
        sum += buffer.bufSumCStylePartiallyLocalValEffectivelyFinalArray();
    }

    @Benchmark
    public void arraySumCStylePartiallyLocalValNonFinalArray() {
        sum += buffer.bufSumCStylePartiallyLocalValNonFinalArray();
    }
}
