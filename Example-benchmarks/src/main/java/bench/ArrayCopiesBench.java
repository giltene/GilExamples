/**
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */

package bench;

import org.openjdk.jmh.annotations.*;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
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

public class ArrayCopiesBench {

    @Param({"131072"})
    int arrayLengthInKBytes;

    int arrayLength;


    byte[] src;
    byte[] dst;

    long sum;

    static Unsafe unsafe;

    static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
        } catch (NoSuchFieldException ex) {
        } catch (IllegalAccessException ex) {
        }
    }



    @Setup
    public void setup()  {
        arrayLength = arrayLengthInKBytes * 1024;
        src = new byte[arrayLength];
        dst = new byte[arrayLength];

        for (int i = 0; i < arrayLength; i++) {
            src[i] = (byte)(i % 31);
            dst[i] = (byte)((byte)(i % 31) + (((i & 0x1) == 0) ? 5 : 0));
        }
    }

    @Benchmark
    public void loopCopy() {
        for (int i = 0; i < arrayLength; i++) {
            dst[i] = src[i];
        }
    }

    @Benchmark
    public void arrayCopy() {
        System.arraycopy(src, 0, dst, 0, arrayLength);
    }

    @Benchmark
    public void unsafeCopy() {
        int arrayOffset = unsafe.arrayBaseOffset(byte[].class);
        unsafe.copyMemory(src, arrayOffset, dst, arrayOffset, arrayLength);
    }

    @Benchmark
    public void arrayCopiesDoAll() {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            loopCopy();
            arrayCopy();
            unsafeCopy();
        }
    }
}
