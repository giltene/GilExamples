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

public class EarlyExitBench {


    @Param({"2048"})
    int bufferLengthKBytes;

    int bufferLength;

    static final int terminator = 1023;

    int[] buffer;

    long sum;

    @Setup
    public void setup() throws NoSuchMethodException {
        bufferLength = bufferLengthKBytes * 1024;
        buffer = new int[bufferLength];

        for (int i = 0; i < bufferLength; i++) {
            buffer[i] = (byte)(i % 31);
        }

        buffer[bufferLength - 1] = terminator;
    }

    @Benchmark
    public void searchForStaticTerminationValue() {
        for (int i = 0; i < bufferLength; i++) {
            if (buffer[i] == terminator)
                break;
            sum += buffer[i];
        }
    }

    @Benchmark
    public void searchForNonStaticTerminationValue() {
        int termValue = buffer[bufferLength - 1];
        for (int i = 0; i < bufferLength; i++) {
            if (buffer[i] == termValue)
                break;
            sum += buffer[i];
        }
    }
}
