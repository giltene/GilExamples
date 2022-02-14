/**
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */

package bench;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.TreeMap;
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

public class MegamorphicInterfaceBench {

    @Param({"4096"})
    int arrayLength;

    public interface Doer {
        public int doIt(int offset);
    }

    public class DoesA implements Doer {
        static final int A = 111;
        public int doIt(int offset) {
            return A + (int) Math.sqrt(offset);
        }
    }

    public class DoesB implements Doer {
        static final int B = 212;
        public int doIt(int offset) {
            return B + (int) Math.sqrt(offset);
        }
    }

    public class DoesC implements Doer {
        static final int C = 313;
        public int doIt(int offset) {
            return C + (int) Math.sqrt(offset);
        }
    }

    public class DoesD implements Doer {
        static final int D = 414;
        public int doIt(int offset) {
            return D + (int) Math.sqrt(offset);
        }
    }

    public class DoesE implements Doer {
        static final int E = 515;
        public int doIt(int offset) {
            return E + (int) Math.sqrt(offset);
        }
    }

    public class DoesF implements Doer {
        static final int F = 616;
        public int doIt(int offset) {
            return F + (int) Math.sqrt(offset);
        }
    }

    public class DoesG implements Doer {
        static final int G = 717;
        public int doIt(int offset) {
            return G + (int) Math.sqrt(offset);
        }
    }

    public class DoesH implements Doer {
        static final int H = 818;
        public int doIt(int offset) {
            return H + (int) Math.sqrt(offset);
        }
    }

    public class RandomCollection<E> {
        /**
         * Implementation of Peter Lawrey's RandomCollection, as posted at
         */
        private final TreeMap<Double, E> map;
        private final Random random;
        private double total = 0;

        public RandomCollection() {
            this.random = new Random(42);
            map = new TreeMap<Double, E>();
        }

        @SuppressWarnings("unchecked")
        public RandomCollection(RandomCollection<E> collectionToCopy) {
            this.random = new Random(42);
            map = (TreeMap<Double, E>) collectionToCopy.map.clone();
            total = collectionToCopy.total;
        }

        public RandomCollection<E> add(double weight, E result) {
            if (weight <= 0) return this;
            total += weight;
            map.put(total, result);
            return this;
        }

        public E next() {
            double value = random.nextDouble() * total;
            return map.higherEntry(value).getValue();
        }
    }

    Doer[] doerArray1;
    Doer[] doerArray2;
    Doer[] doerArray3;
    Doer[] doerArray4;
    Doer[] doerArray5;
    Doer[] doerArray6;
    Doer[] doerArray7;
    Doer[] doerArray8;

    Doer[] doerArray2e;
    Doer[] doerArray3e;
    Doer[] doerArray4e;
    Doer[] doerArray5e;
    Doer[] doerArray6e;
    Doer[] doerArray7e;
    Doer[] doerArray8e;

    volatile long sum;

    @Setup
    public void setup() throws NoSuchMethodException {

        doerArray1 = new Doer[arrayLength];
        doerArray2 = new Doer[arrayLength];
        doerArray3 = new Doer[arrayLength];
        doerArray4 = new Doer[arrayLength];
        doerArray5 = new Doer[arrayLength];
        doerArray6 = new Doer[arrayLength];
        doerArray7 = new Doer[arrayLength];
        doerArray8 = new Doer[arrayLength];

        doerArray2e = new Doer[arrayLength];
        doerArray3e = new Doer[arrayLength];
        doerArray4e = new Doer[arrayLength];
        doerArray5e = new Doer[arrayLength];
        doerArray6e = new Doer[arrayLength];
        doerArray7e = new Doer[arrayLength];
        doerArray8e = new Doer[arrayLength];

        RandomCollection<Doer> rand1 = new RandomCollection<Doer>().
                add(10, new DoesA());
        RandomCollection<Doer> rand2 = new RandomCollection<>(rand1).
                add(20, new DoesB());
        RandomCollection<Doer> rand3 =  new RandomCollection<>(rand2).
                add(30, new DoesC());
        RandomCollection<Doer> rand4 =  new RandomCollection<>(rand3).
                add(50, new DoesD());
        RandomCollection<Doer> rand5 =  new RandomCollection<>(rand4).
                add(70, new DoesE());
        RandomCollection<Doer> rand6 =  new RandomCollection<>(rand5).
                add(400, new DoesF());
        RandomCollection<Doer> rand7 =  new RandomCollection<>(rand6).
                add(500, new DoesG());
        RandomCollection<Doer> rand8 =  new RandomCollection<>(rand7).
                add(1000, new DoesH());

        RandomCollection<Doer> rand2e = new RandomCollection<>(rand1).
                add(10, new DoesB());
        RandomCollection<Doer> rand3e = new RandomCollection<>(rand2e).
                add(10, new DoesC());
        RandomCollection<Doer> rand4e = new RandomCollection<>(rand3e).
                add(10, new DoesD());
        RandomCollection<Doer> rand5e = new RandomCollection<>(rand4e).
                add(10, new DoesE());
        RandomCollection<Doer> rand6e = new RandomCollection<>(rand5e).
                add(10, new DoesF());
        RandomCollection<Doer> rand7e = new RandomCollection<>(rand6e).
                add(10, new DoesG());
        RandomCollection<Doer> rand8e = new RandomCollection<>(rand7e).
                add(10, new DoesH());

        for (int i = 0; i < arrayLength; i++) {
            doerArray1[i] = rand1.next();
            doerArray2[i] = rand2.next();
            doerArray3[i] = rand3.next();
            doerArray4[i] = rand4.next();
            doerArray5[i] = rand5.next();
            doerArray6[i] = rand6.next();
            doerArray7[i] = rand7.next();
            doerArray8[i] = rand8.next();

            doerArray2e[i] = rand2e.next();
            doerArray3e[i] = rand3e.next();
            doerArray4e[i] = rand4e.next();
            doerArray5e[i] = rand5e.next();
            doerArray6e[i] = rand6e.next();
            doerArray7e[i] = rand7e.next();
            doerArray8e[i] = rand8e.next();

        }
    }

    public int f(Doer[] b) {
        int sum = 0;
        for(int i = 0; i < b.length; i++) {
            sum += b[i].doIt(100000000 /* sqrt = 10000 */);
        }
        return sum;
    }

    @Benchmark
    public void a_monomorphicDoers() {
        sum += f(doerArray1);
    }

    @Benchmark
    public void b_bimorphicDoers() {
        sum += f(doerArray2);
    }

    @Benchmark
    public void b_bimorphicEqualDoers() {
        sum += f(doerArray2e);
    }

    @Benchmark
    public void c_trimorphicDoers() {
        sum += f(doerArray3);
    }

    @Benchmark
    public void c_trimorphicEqualDoers() {
        sum += f(doerArray3e);
    }

    @Benchmark
    public void d_quadmorphicDoers() {
        sum += f(doerArray4);
    }

    @Benchmark
    public void d_quadmorphicEqualDoers() {
        sum += f(doerArray4e);
    }

    @Benchmark
    public void e_pentamorphicDoers() {
        sum += f(doerArray5);
    }

    @Benchmark
    public void e_pentamorphicEqualDoers() {
        sum += f(doerArray5e);
    }

    @Benchmark
    public void f_hexamorphicDoers() {
        sum += f(doerArray6);
    }

    @Benchmark
    public void f_hexamorphicEqualDoers() {
        sum += f(doerArray6e);
    }

    @Benchmark
    public void g_septamorphicDoers() {
        sum += f(doerArray7);
    }

    @Benchmark
    public void g_septamorphicEqualDoers() {
        sum += f(doerArray7e);
    }

    @Benchmark
    public void h_octamorphicDoers() {
        sum += f(doerArray8);
    }

    @Benchmark
    public void h_octamorphicEqualDoers() {
        sum += f(doerArray8e);
    }
}
