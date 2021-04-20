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

public class ThreadLocalBench {

    @Param({"8", "128", "1024", "131072", "1048576"})
    int numThreadLocals;

//    @Param({"8", "32", "128"})
    @Param({"8"})
    int numInSubset;

//    @Param({"0", "1", "2", "3"})
    @Param({"0"})
    int primingGets;

    @Param({"1000000"})
    int elementsPerLoop;

    @Param({"10000"})
    int warmupLoopCount;

    ThreadLocal<Foo>[] threadLocals;
    ThreadLocal<Foo> single;
    ThreadLocal<Foo> otherSingle;
    Foo fooInstance;

    volatile long sum;

    class Foo {
        long x;
        long y;
    }

    @Setup
    public void setup() throws NoSuchMethodException {

        threadLocals = new ThreadLocal[numThreadLocals];
        numInSubset = Math.min(numInSubset, numThreadLocals);
        primingGets = Math.min(primingGets, numThreadLocals - 1);

        for (int i = 0; i < threadLocals.length; i++) {
            threadLocals[i] = new ThreadLocal<Foo>();
            threadLocals[i].set(new Foo());  // Populate map in this thread
        }
        single = threadLocals[threadLocals.length - 1];
        otherSingle = threadLocals[0];

        fooInstance = new Foo();

        for (int i = 0; i < primingGets; i++) {
            threadLocals[i].get();
        }
    }


    @Benchmark
    public void threadLocalGetOfSingle() {
        for (int i = 0; i < elementsPerLoop; i ++) {
            single.get().x++;
        }
        otherSingle.get().x++;
    }

    @Benchmark
    public void threadLocalSetOfSingle() {
        for (int i = 0; i < elementsPerLoop; i ++) {
            fooInstance.x++;
            single.set(fooInstance);
        }
        otherSingle.get().x++;
    }

    @Benchmark
    public void threadLocalGetOfEach() {
        try {
            for (int i = 0; i < elementsPerLoop; i++) {
                threadLocals[i % numThreadLocals].get().x++;
            }
        } catch (Throwable e) {
            System.out.println("***** threadLocalGetOfEach caught: " + e);
        }
    }

    @Benchmark
    public void threadLocalSetOfEach() {
        for (int i = 0; i < elementsPerLoop; i ++) {
            fooInstance.x++;
            threadLocals[i % numThreadLocals].set(fooInstance);
        }
    }

    @Benchmark
    public void threadLocalGetOfSubset() {
        for (int i = 0; i < elementsPerLoop; i ++) {
            threadLocals[i % numInSubset].get().x++;
        }
    }

    @Benchmark
    public void threadLocalSetOfSubset() {
        for (int i = 0; i < elementsPerLoop; i ++) {
            fooInstance.x++;
            threadLocals[i % numInSubset].set(fooInstance);
        }
    }
}
