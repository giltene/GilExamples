/**
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */

package bench;

import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
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

public class MegamorphicInterfaceListsBench {

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

    static final int arrayLength = 4096;

    static final List<Doer> doerList1 = new ArrayList<>();
    static final List<Doer> doerList2 = new ArrayList<>();
    static final List<Doer> doerList3 = new ArrayList<>();
    static final List<Doer> doerList4 = new ArrayList<>();
    static final List<Doer> doerList5 = new ArrayList<>();
    static final List<Doer> doerList6 = new ArrayList<>();
    static final List<Doer> doerList7 = new ArrayList<>();
    static final List<Doer> doerList8 = new ArrayList<>();

    static final List<Doer> doerList2e = new ArrayList<>();
    static final List<Doer> doerList3e = new ArrayList<>();
    static final List<Doer> doerList4e = new ArrayList<>();
    static final List<Doer> doerList5e = new ArrayList<>();
    static final List<Doer> doerList6e = new ArrayList<>();
    static final List<Doer> doerList7e = new ArrayList<>();
    static final List<Doer> doerList8e = new ArrayList<>();

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
            doerList1.add(rand1.next());
            doerList2.add(rand2.next());
            doerList3.add(rand3.next());
            doerList4.add(rand4.next());
            doerList5.add(rand5.next());
            doerList6.add(rand6.next());
            doerList7.add(rand7.next());
            doerList8.add(rand8.next());

            doerList2e.add(rand2e.next());
            doerList3e.add(rand3e.next());
            doerList4e.add(rand4e.next());
            doerList5e.add(rand5e.next());
            doerList6e.add(rand6e.next());
            doerList7e.add(rand7e.next());
            doerList8e.add(rand8e.next());
        }
    }

    public int f(List<Doer> b) {
        int sum = 0;
        for(Doer d : b ) {
            sum += d.doIt(100000000 /* sqrt = 10000 */);
        }
        return sum;
    }

    @Benchmark
    public void a_monomorphicDoersList() {
        sum += f(doerList1);
    }

    @Benchmark
    public void b_bimorphicDoersList() {
        sum += f(doerList2);
    }

    @Benchmark
    public void b_bimorphicEqualDoersList() {
        sum += f(doerList2e);
    }

    @Benchmark
    public void c_trimorphicDoersList() {
        sum += f(doerList3);
    }

    @Benchmark
    public void c_trimorphicEqualDoersList() {
        sum += f(doerList3e);
    }

    @Benchmark
    public void d_quadmorphicDoersList() {
        sum += f(doerList4);
    }

    @Benchmark
    public void d_quadmorphicEqualDoersList() {
        sum += f(doerList4e);
    }

    @Benchmark
    public void e_pentamorphicDoersList() {
        sum += f(doerList5);
    }

    @Benchmark
    public void e_pentamorphicEqualDoersList() {
        sum += f(doerList5e);
    }

    @Benchmark
    public void f_hexamorphicDoersList() {
        sum += f(doerList6);
    }

    @Benchmark
    public void f_hexamorphicEqualDoersList() {
        sum += f(doerList6e);
    }

    @Benchmark
    public void g_septamorphicDoersList() {
        sum += f(doerList7);
    }

    @Benchmark
    public void g_septamorphicEqualDoersList() {
        sum += f(doerList7e);
    }

    @Benchmark
    public void h_octamorphicDoersList() {
        sum += f(doerList8);
    }

    @Benchmark
    public void h_octamorphicEqualDoersList() {
        sum += f(doerList8e);
    }
}
