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

public class MultiplyVariantsBench {


    static final int valsLength = 1048576; // Large allocation ensures consistent alignment with both UseG1 and with Zing.
//    static final int valsLength = 512;

    @Param({"256"})
    int loopLength;

    static final int staticLoopLength = 256;

    long[] accum;
    long[] src;
    static final long[] staticSrc = new long[valsLength];
    static final long[] staticAccum = new long[valsLength];

    long sum;

    @Setup
    public void setup() throws NoSuchMethodException {
        accum = new long[valsLength];
        src = new long[valsLength];

        for (int i = 0; i < valsLength; i++) {
            accum[i] = (byte)(i % 31) + 1;
            src[i] = (byte)(i % 31) + (((i & 0x1) == 0) ? 5 : 42);
        }
    }

    public static void multiply(long[] accum, int accumOffset, long[] src, int srcOffset, int n){
        if (accum.length != src.length) {
            return;
        }
        for (int ii = 0; ii < n; ++ii) {
            accum[ii + accumOffset] *= src[ii + srcOffset];
        }
    }

    public static void multiplyA(long[] accum, int accumOffset, long[] src, int srcOffset, int n){
        if (accum.length != src.length) {
            return;
        }
        for (int ii = 0; ii < n; ++ii) {
            accum[ii + accumOffset] *= src[ii + srcOffset];
        }
    }

    public static void multiplyB(long[] accum, int accumOffset, long[] src, int srcOffset, int n){
        for (int ii = 0; ii < n; ++ii) {
            accum[ii + accumOffset] *= src[ii + srcOffset];
        }
    }

    public static void multiplyC(long[] accum, int accumOffset, long[] src, int srcOffset, int n){
        if (accum.length < accumOffset + n)
            return;
        if (src.length < srcOffset + n)
            return;
        for (int ii = 0; ii < n; ++ii) {
            accum[ii + accumOffset] *= src[ii + srcOffset];
        }
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public static void multiplyArrays(long[] accum, long[] src, int n){
        for (int ii = 0; ii < n; ++ii) {
            accum[ii] *= src[ii];
        }
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public static void multiplyArraysA(long[] accum, long[] src, int n){
        if (accum.length != src.length) {
            return;
        }
        for (int ii = 0; ii < n; ++ii) {
            accum[ii] *= src[ii];
        }
    }


    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    private void addArrays(long a[], long b[], int n) {
        for (int i = 0; i < n; i++) {
                a[i] += b[i];
        }
    }


    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    private void addArraysA(long a[], long b[], int n) {
        if (a.length != b.length) {
            return;
        }
        for (int i = 0; i < n; i++) {
            a[i] += b[i];
        }
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Benchmark
    public void multiplySeparate() {
        multiply(accum, 0, src, 0, loopLength);
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Benchmark
    public void multiplySeparateA() {
        multiplyA(accum, 0, src, 0, loopLength);
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Benchmark
    public void multiplySeparateB() {
        multiplyB(accum, 0, src, 0, loopLength);
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Benchmark
    public void multiplySeparateC() {
        multiplyC(accum, 0, src, 0, loopLength);
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Benchmark
    public void multiplySelfCompleteOverlap() {
        multiply(accum, 0, accum, 0, loopLength);
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Benchmark
    public void multiplySelfNoOverlap() {
        multiply(accum, loopLength, accum, 0, loopLength);
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Benchmark
    public void multiplySelfNoOverlapWithGap() {
        multiply(accum, loopLength*2, accum, 0, loopLength);
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Benchmark
    public void multiplySelfPartialOverlapSrcLagging() {
        multiply(accum, loopLength/2, accum, 0, loopLength);
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Benchmark
    public void multiplySelfPartialOverlapSrcLeading() {
        multiply(accum, 0, accum, loopLength/2, loopLength);
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Benchmark
    public void multiplySelfPartialOverlapSrcLaggingStaticCount() {
        multiply(accum, staticLoopLength/2, accum, 0, staticLoopLength);
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Benchmark
    public void multiplySelfPartialOverlapSrcLeadingStaticCount() {
        multiply(accum, 0, accum, staticLoopLength/2, staticLoopLength);
    }
//
//    @Benchmark
//    public void multiplySelfOffset001SrcLaggingOverlap() {
//        multiply(accum, 1, accum, 0, loopLength);
//    }
//
//    @Benchmark
//    public void multiplySelfOffset001SrcLeadingOverlap() {
//        multiply(accum, 0, accum, 1, loopLength);
//    }
//
//    @Benchmark
//    public void multiplySelfOffset008SrcLaggingOverlap() {
//        multiply(accum, 8, accum, 0, loopLength);
//    }
//
//    @Benchmark
//    public void multiplySelfOffset008SrcLeadingOverlap() {
//        multiply(accum, 0, accum, 8, loopLength);
//    }
//
//    @Benchmark
//    public void multiplySelfOffset016SrcLaggingOverlap() {
//        multiply(accum, 16, accum, 0, loopLength);
//    }
//
//    @Benchmark
//    public void multiplySelfOffset016SrcLeadingOverlap() {
//        multiply(accum, 0, accum, 16, loopLength);
//    }
//
//    @Benchmark
//    public void multiplySelfOffset032SrcLaggingOverlap() {
//        multiply(accum, 32, accum, 0, loopLength);
//    }
//
//    @Benchmark
//    public void multiplySelfOffset032SrcLeadingOverlap() {
//        multiply(accum, 0, accum, 32, loopLength);
//    }
//
//    @Benchmark
//    public void multiplySelfOffset128SrcLaggingOverlap() {
//        multiply(accum, 128, accum, 0, loopLength);
//    }
//
//    @Benchmark
//    public void multiplySelfOffset128SrcLeadingOverlap() {
//        multiply(accum, 0, accum, 128, loopLength);
//    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Benchmark
    public void multiplySeparateStatic() {
        multiply(staticAccum, 0, staticSrc, 0, loopLength);
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Benchmark
    public void multiplySelfCompleteOverlapStatic() {
        multiply(staticAccum, 0, staticAccum, 0, loopLength);
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Benchmark
    public void multiplySelfNoOverlapStatic() {
        multiply(staticAccum, loopLength, staticAccum, 0, loopLength);
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Benchmark
    public void multiplySelfNoOverlapStaticWithGap() {
        multiply(staticAccum, loopLength*2, staticAccum, 0, loopLength);
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Benchmark
    public void multiplySelfPartialOverlapSrcLaggingStatic() {
        multiply(staticAccum, loopLength/2, staticAccum, 0, loopLength);
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Benchmark
    public void multiplySelfPartialOverlapSrcLeadingStatic() {
        multiply(staticAccum, 0, staticAccum, loopLength/2, loopLength);
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Benchmark
    public void multiplyAll() {
        multiplySeparateC();
        multiplySeparateB();
        multiplySeparateA();
        multiplySeparate();
        addArrays(accum, src, loopLength);
        addArraysA(accum, src, loopLength);
        multiplyArrays(accum, src, loopLength);
        multiplyArraysA(accum, src, loopLength);
//        multiplySelfCompleteOverlap();
//        multiplySelfNoOverlap();
//        multiplySelfNoOverlapWithGap();
//        multiplySelfPartialOverlapSrcLagging();
//        multiplySelfPartialOverlapSrcLeading();
//        multiplySelfPartialOverlapSrcLaggingStaticCount();
//        multiplySelfPartialOverlapSrcLeadingStaticCount();
//        multiplySeparateStatic();
//        multiplySelfCompleteOverlapStatic();
//        multiplySelfNoOverlapStatic();
//        multiplySelfNoOverlapStaticWithGap();
//        multiplySelfPartialOverlapSrcLaggingStatic();
//        multiplySelfPartialOverlapSrcLeadingStatic();
//        multiply(accum, src, loopLength);
//        add(accum, src, loopLength);
//        addArrays(accum, src, loopLength);
    }
}
