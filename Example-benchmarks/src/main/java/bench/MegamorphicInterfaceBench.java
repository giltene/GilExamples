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

    public interface Doer {
        public int doIt();
    }

    public class DoesA implements Doer {
        static final int A = 173;
        public int doIt() {
            return A;
        }
    }

    public class DoesB implements Doer {
        static final int B = 177;
        public int doIt() {
            return B;
        }
    }

    public class DoesC implements Doer {
        static final int C = 191;
        public int doIt() {
            return C;
        }
    }

    public class DoesD implements Doer {
        static final int D = 197;
        public int doIt() {
            return D;
        }
    }

    public class DoesE implements Doer {
        static final int E = 142;
        public int doIt() {
            return E;
        }
    }

    public class DoesF implements Doer {
        static final int F = 135;
        public int doIt() {
            return F;
        }
    }

    public class DoesG implements Doer {
        static final int G = 126;
        public int doIt() {
            return G;
        }
    }

    public class DoesH implements Doer {
        static final int H = 123;
        public int doIt() {
            return H;
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

    static final int arrayLength = 4096;

    static final Doer[] doerArray1 = new Doer[arrayLength];
    static final Doer[] doerArray2 = new Doer[arrayLength];
    static final Doer[] doerArray3 = new Doer[arrayLength];
    static final Doer[] doerArray4 = new Doer[arrayLength];
    static final Doer[] doerArray5 = new Doer[arrayLength];
    static final Doer[] doerArray6 = new Doer[arrayLength];
    static final Doer[] doerArray7 = new Doer[arrayLength];
    static final Doer[] doerArray8 = new Doer[arrayLength];

    volatile long sum;

    @Setup
    public void setup() throws NoSuchMethodException {
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

        for (int i = 0; i < arrayLength; i++) {
            doerArray1[i] = rand1.next();
            doerArray2[i] = rand2.next();
            doerArray3[i] = rand3.next();
            doerArray4[i] = rand4.next();
            doerArray5[i] = rand5.next();
            doerArray6[i] = rand6.next();
            doerArray7[i] = rand7.next();
            doerArray8[i] = rand8.next();
        }
    }

    public int f(Doer[] b) {
        int sum = 0;
        for(int i = 0; i < b.length; i++) {
            sum += b[i].doIt();
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
    public void c_trimorphicDoers() {
        sum += f(doerArray3);
    }

    @Benchmark
    public void d_quadmorphicDoers() {
        sum += f(doerArray4);
    }

    @Benchmark
    public void e_pentamorphicDoers() {
        sum += f(doerArray5);
    }

    @Benchmark
    public void f_hexamorphicDoers() {
        sum += f(doerArray6);
    }

    @Benchmark
    public void g_septamorphicDoers() {
        sum += f(doerArray7);
    }

    @Benchmark
    public void h_octamorphicDoers() {
        sum += f(doerArray8);
    }
}
