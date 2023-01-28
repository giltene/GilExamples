/**
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */

package bench;

import org.openjdk.jmh.annotations.*;

import java.util.HashMap;
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

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)

public class StringHashCodesBench {

    @Param({"interned12", "interned24", "interned36", "interned48", "interned96", "new12", "new24", "new36", "new48", "new96"})
    String stringName;

    char[] val;

    volatile int result;

    private String interned12;
    private String interned24;
    private String interned36;
    private String interned48;
    private String interned96;
    private String new12;
    private String new24;
    private String new36;
    private String new48;
    private String new96;

    @Setup
    public void setup() {
        interned12 = "abcdefghijkl";
        interned24 = "abcdefghijklabcdefghijkl";
        interned36 = "abcdefghijklabcdefghijklabcdefghijkl";
        interned48 = "abcdefghijklabcdefghijklabcdefghijklabcdefghijkl";
        interned96 = "abcdefghijklabcdefghijklabcdefghijklabcdefghijklabcdefghijklabcdefghijklabcdefghijklabcdefghijkl";

        new12 = new String(new char[] {
                72, 90, 100, 89, 105, 2, 72, 90, 100, 89, 105, 2
        });
        new24 = new String(new char[] {
                72, 90, 100, 89, 105, 2, 72, 90, 100, 89, 105, 2,
                72, 90, 100, 89, 105, 2, 72, 90, 100, 89, 105, 2
        });
        new36 = new String(new char[] {
                72, 90, 100, 89, 105, 2, 72, 90, 100, 89, 105, 2,
                72, 90, 100, 89, 105, 2, 72, 90, 100, 89, 105, 2,
                72, 90, 100, 89, 105, 2, 72, 90, 100, 89, 105, 2
        });
        new48 = new String(new char[] {
                72, 90, 100, 89, 105, 2, 72, 90, 100, 89, 105, 2,
                72, 90, 100, 89, 105, 2, 72, 90, 100, 89, 105, 2,
                72, 90, 100, 89, 105, 2, 72, 90, 100, 89, 105, 2,
                72, 90, 100, 89, 105, 2, 72, 90, 100, 89, 105, 2
        });
        new96 = new String(new char[] {
                72, 90, 100, 89, 105, 2, 72, 90, 100, 89, 105, 2,
                72, 90, 100, 89, 105, 2, 72, 90, 100, 89, 105, 2,
                72, 90, 100, 89, 105, 2, 72, 90, 100, 89, 105, 2,
                72, 90, 100, 89, 105, 2, 72, 90, 100, 89, 105, 2,
                72, 90, 100, 89, 105, 2, 72, 90, 100, 89, 105, 2,
                72, 90, 100, 89, 105, 2, 72, 90, 100, 89, 105, 2,
                72, 90, 100, 89, 105, 2, 72, 90, 100, 89, 105, 2,
                72, 90, 100, 89, 105, 2, 72, 90, 100, 89, 105, 2
        });

        HashMap<String, String> stringNames = new HashMap<>();
        stringNames.put("interned12", interned12);
        stringNames.put("interned24", interned24);
        stringNames.put("interned36", interned36);
        stringNames.put("interned48", interned48);
        stringNames.put("interned96", interned96);
        stringNames.put("new12", new12);
        stringNames.put("new24", new24);
        stringNames.put("new36", new36);
        stringNames.put("new48", new48);
        stringNames.put("new96", new96);

        val = stringNames.get(stringName).toCharArray();
    }

    @Benchmark
    public void hashUnroll16Times() {
        int len = val.length;
        int i = 0;
        int h = 0;
        for (; i + 15 < len; i += 16) {
            h = 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * h
                    + 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * val[i]
                    + 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * val[i+1]
                    + 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * val[i+2]
                    + 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * val[i+3]
                    + 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * val[i+4]
                    + 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * val[i+5]
                    + 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * val[i+6]
                    + 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * val[i+7]
                    + 31 * 31 * 31 * 31 * 31 * 31 * 31 * val[i+8]
                    + 31 * 31 * 31 * 31 * 31 * 31 * val[i+9]
                    + 31 * 31 * 31 * 31 * 31 * val[i+10]
                    + 31 * 31 * 31 * 31 * val[i+11]
                    + 31 * 31 * 31 * val[i+12]
                    + 31 * 31 * val[i+13]
                    + 31 * val[i+14]
                    + val[i+15];
        }
        for (; i + 7 < len; i += 8) {
            h = 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * h
                    + 31 * 31 * 31 * 31 * 31 * 31 * 31 * val[i]
                    + 31 * 31 * 31 * 31 * 31 * 31 * val[i+1]
                    + 31 * 31 * 31 * 31 * 31 * val[i+2]
                    + 31 * 31 * 31 * 31 * val[i+3]
                    + 31 * 31 * 31 * val[i+4]
                    + 31 * 31 * val[i+5]
                    + 31 * val[i+6]
                    + val[i+7];
        }
        for (; i + 3 < len; i += 4) {
            h = 31 * 31 * 31 * 31 * h
                    + 31 * 31 * 31 * val[i]
                    + 31 * 31 * val[i+1]
                    + 31 * val[i+2]
                    + val[i+3];
        }
        for (; i < len; i++) {
            h = 31 * h + val[i];
        }
        result = h;
    }

    @Benchmark
    public void hashUnroll8Times() {
        int len = val.length;
        int i = 0;
        int h = 0;
        for (; i + 7 < len; i += 8) {
            h = 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * h
                    + 31 * 31 * 31 * 31 * 31 * 31 * 31 * val[i]
                    + 31 * 31 * 31 * 31 * 31 * 31 * val[i+1]
                    + 31 * 31 * 31 * 31 * 31 * val[i+2]
                    + 31 * 31 * 31 * 31 * val[i+3]
                    + 31 * 31 * 31 * val[i + 4]
                    + 31 * 31 * val[i+5]
                    + 31 * val[i+6]
                    + val[i+7];
        }
        for (; i + 3 < len; i += 4) {
            h = 31 * 31 * 31 * 31 * h
                    + 31 * 31 * 31 * val[i]
                    + 31 * 31 * val[i+1]
                    + 31 * val[i+2]
                    + val[i+3];
        }
        for (; i < len; i++) {
            h = 31 * h + val[i];
        }
        result = h;
    }

    @Benchmark
    public void hashUnroll8TimesInner() {
        int i = 0;
        int h = 0;
        final int len = val.length;
        final int[] hashMultipliers = new int[] {
                31 * 31 * 31 * 31 * 31 * 31 * 31,
                31 * 31 * 31 * 31 * 31 * 31,
                31 * 31 * 31 * 31 * 31,
                31 * 31 * 31 * 31,
                31 * 31 * 31,
                31 * 31,
                31,
                1
        };
        for (; i + 7 < len; i += 8) {
            int sum = 0;
            for (int j = 0; j < 8; j++) {
                sum += hashMultipliers[j] * val[i + j];
            }
            h = (31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * h);
            h += sum;
        }
        for (; i + 3 < len; i += 4) {
            h = 31 * 31 * 31 * 31 * h
                    + 31 * 31 * 31 * val[i]
                    + 31 * 31 * val[i+1]
                    + 31 * val[i+2]
                    + val[i+3];
        }
        for (; i < len; i++) {
            h = 31 * h + val[i];
        }
        result = h;
    }

    @Benchmark
    public void hashUnroll8TimesTwoInners() {
        int i = 0;
        int h = 0;
        final int len = val.length;
        final int[] hashMultipliers = new int[] {
                31 * 31 * 31 * 31 * 31 * 31 * 31,
                31 * 31 * 31 * 31 * 31 * 31,
                31 * 31 * 31 * 31 * 31,
                31 * 31 * 31 * 31,
                31 * 31 * 31,
                31 * 31,
                31,
                1
        };
        for (; i + 7 < len; i += 8) {
            final int[] elements = new int[8];
            for (int j = 0; j < 8; j++) {
                elements[j] = hashMultipliers[j] * val[i + j];
            }
            int sum = 0;
            for (int j = 0; j < 8; j++) {
                sum += elements[j];
            }
            h = (31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * h);
            h += sum;
        }
        for (; i + 3 < len; i += 4) {
            h = 31 * 31 * 31 * 31 * h
                    + 31 * 31 * 31 * val[i]
                    + 31 * 31 * val[i+1]
                    + 31 * val[i+2]
                    + val[i+3];
        }
        for (; i < len; i++) {
            h = 31 * h + val[i];
        }
        result = h;
    }

    @Benchmark
    public void hashBasic() {
        final int len = val.length;
        int h = 0;
        for (int i = 0; i < len; i++) {
            h = 31 * h + val[i];
        }
        result = h;
    }

    long sumOfEvenValues(int[] values) {
        long sum = 0;
        for (int i = 0; i < values.length; i++) {
            int value = values[i];
            if (value % 2 == 0) {
                sum += value;
            }
        }
        return sum;
    }

    long sumOfEvenValues(Object[] values) {
        long sum = 0;
        for (int i = 0; i < values.length; i++) {
            Object member = values[i];
            Integer value = (Integer) member; // Can break out of loop with a class cast exception
            if (value.intValue() % 2 == 0) {  // Can break out of the loop with an NPE
                sum += value.intValue();
            }
        }
        return sum;
    }

    long sumOfEvenValuesTransformed(Object[] values) {
        long sum = 0;
        int i = 0;
        int len = values.length;
        // "Chunked" loop that does 8 things at a time, if all can validly be done. Loop always exits at a chunk
        // boundary (even f any of the elements in the chunk cannot be validly dealt with), and is safely followed
        // by a one-at-a-time execution of the remaining elements at the end, or in and early-exit situation:
        outerLoop:
        for (; i + 7 < len; i += 8) {
            // Load a chunk of 8 elements from values[]:
            final Object[] elements = new Object[8];
            for (int j = 0; j < 8; j++) {
                elements[j] = values[i + j];
            }
            // Null check 8 eleemnts, if any are null exit chunked loop and continue one-at-a-time
            for (int j = 0; j < 8; j++) {
                if (elements[i] == null)
                    break outerLoop;
            }
            // Gather the kids of 8 elements
            final Class[] kids = new Class[8];
            for (int j = 0; j < 8; j++) {
                kids[j] = elements[j].getClass();
            }
            // Verify the kids of 8 elements. If any is not Integer, exit chunked loop and continue one-at-a-time
            for (int j = 0; j < 8; j++) {
                if (kids[j] != Integer.class)
                    break outerLoop;
            }

            // Ignore: this is redundant with the kids check already done above. Here just to contain the actual java casts.
            final Integer[] integerVals = new Integer[8];
            for (int j = 0; j < 8; j++) {
                integerVals[j] = (Integer) elements[j];
            }

            // Gather 8 int values from 8 separate Integer objects:
            final int[] intVals = new int[8];
            for (int j = 0; j < 8; j++) {
                intVals[j] = integerVals[j].intValue();
            }
            // Perform predicated adds (for even valued intVals[] member):
            for (int j = 0; j < 8; j++) {
                int val = intVals[j];
                if (val % 2 == 0) {
                    sum += val;
                }
            }
        }
        for (; i < len; i++) {
            Object member = values[i];
            Integer value = (Integer) member; // Can break out of loop with a class cast exception
            if (value.intValue() % 2 == 0) {  // Can break out of the loop with an NPE
                sum += value.intValue();
            }
        }
        return sum;
    }
}
