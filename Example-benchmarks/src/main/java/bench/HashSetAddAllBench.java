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
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
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

public class HashSetAddAllBench {

    @Param({"1024"})
    int hahSetSizeInK;

    int hashSetSize;

    HashSet<Integer> intSourceSet;
    HashSet<String> stringSourceSet;

    static volatile Set visibleSet;

    @Setup
    public void setup()  {
        hashSetSize = hahSetSizeInK * 1024;
        intSourceSet = new HashSet<>(hashSetSize);
        stringSourceSet = new HashSet<>(hashSetSize);
        Random rand = new Random(42);
        for (int i = 0; i < hashSetSize; i++) {
            int num = rand.nextInt();
            intSourceSet.add(num);
            stringSourceSet.add("Entry" + i + " rand: " + num);
        }
    }

    @Benchmark
    public void addAllIntToEmpty() {
        HashSet<Integer> set = new HashSet<>();
        set.addAll(intSourceSet);
        visibleSet = set;
    }

    @Benchmark
    public void addAllIntToPresized() {
        HashSet<Integer> set = new HashSet<>(hashSetSize);
        set.addAll(intSourceSet);
        visibleSet = set;
    }

    @Benchmark
    public void addAllStringToEmpty() {
        HashSet<String> set = new HashSet<>();
        set.addAll(stringSourceSet);
        visibleSet = set;
    }

    @Benchmark
    public void addAllStringToPresized() {
        HashSet<String> set = new HashSet<>(hashSetSize);
        set.addAll(stringSourceSet);
        visibleSet = set;
    }
}
