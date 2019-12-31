/**
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */

package bench;

import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.Random;
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

public class GenericCheckcastBench {

    @Param({"1024"})
    int arrayLengthInKs;

    int arrayLength;

    Object[] targetOs;
    String[] targetSs;
    ArrayList<String> sList;
    ArrayList<String> sShuffledList;


    int sum = 0;

    volatile int volatileNum = 0;

    static final int shuffleCount = 5;

    @Setup
    public void setup() {
        arrayLength = arrayLengthInKs * 1024;
        targetOs= new Object[arrayLength];
        targetSs= new String[arrayLength];
        sList = new ArrayList<>(arrayLength);
        sShuffledList = new ArrayList<>(arrayLength);

        for (int i = 0; i < arrayLength; i++) {
            String s = String.format("s = %s", i);
            sList.add(s);
            sShuffledList.add(s);
        }

        // Shuffle shuffled lists:
        Random rand = new Random(42);
        for (int shuffles = 0; shuffles < shuffleCount; shuffles++) {
            for (int i = 0; i < arrayLength; i++) {
                int otherIndex = rand.nextInt(arrayLength);

                String otherS = sShuffledList.get(otherIndex);
                sShuffledList.set(otherIndex, sShuffledList.get(i));
                sShuffledList.set(i, otherS);
            }
        }
    }

    @Benchmark
    public void xferStringsToOs() {
        for (int i = 0; i < arrayLength; i++) {
            targetOs[i] = sList.get(i);
        }
    }

    @Benchmark
    public void xferShuffledStringsToOs() {
        for (int i = 0; i < arrayLength; i++) {
            targetOs[i] = sShuffledList.get(i);
        }
    }

    @Benchmark
    public void xferStrings() {
        for (int i = 0; i < arrayLength; i++) {
            targetSs[i] = sList.get(i);
        }
    }

    @Benchmark
    public void xferShuffledStrings() {
        for (int i = 0; i < arrayLength; i++) {
            targetSs[i] = sShuffledList.get(i);
        }
    }
}
