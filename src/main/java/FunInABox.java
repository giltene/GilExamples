
public class FunInABox {
    static final int THING_ONE_THREASHOLD = 20000000;
    static final int WARMUP_ITERS = THING_ONE_THREASHOLD / 2;
    static final int ITERS = THING_ONE_THREASHOLD * 100;


    static public class ThingOne {
        static long valueOne = 0;

        static long getValue() {
            return valueOne++;
        }
    }

    static public class ThingTwo {
        static long valueTwo = 3;

        static long getValue() {
            return valueTwo++;
        }
    }

    public static long testRun(int iterations) {
        long sum = 0;

        for(int iter = 0; iter < iterations; iter++) {
            if (iter > THING_ONE_THREASHOLD)
                sum += ThingOne.getValue();
            else
                sum += ThingTwo.getValue();
        }
        return sum;
    }

    public static void main(String[] args)
    {
        long sum = 0;
        if (args.length > 0) {
            System.out.println("Keeping ThingOne and ThingTwo tame (by initializing them ahead of time):");
            tameTheThings();
        }
        System.out.println("Starting warmup run (will only use ThingTwo):");
        long startTime = System.currentTimeMillis();
        sum = testRun(WARMUP_ITERS);
        sum += testRun(WARMUP_ITERS); // Two warmups, to make not only an OSR version is created.
        long now = System.currentTimeMillis();
        System.out.println("Warmup run [" + WARMUP_ITERS + " iterations] took " + (now - startTime) +
                " msec." + ((sum % 2 == 0) ? "." : "..") );
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        System.out.println("\n...Then, out of the box\n" +
                "Came Thing Two and Thing One!\n" +
                "And they ran to us fast\n" +
                "They said, \"How do you do?\"...\n");
        System.out.println("Starting actual run (will start using ThingOne a bit after using ThingTwo):");
        startTime = System.currentTimeMillis();
        sum = testRun(ITERS);
        now = System.currentTimeMillis();
        System.out.println("Test run [" + ITERS + " iterations] took " + (now - startTime) +
                " msec." + ((sum % 2 == 0) ? "." : "..") );

    }


    public static <T> Class<T> forceInit(Class<T> klass) {
        // Forces actual initialization (not just loading) of the class klass:
        try {
            Class.forName(klass.getName(), true, klass.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);  // Can't happen
        }
        return klass;
    }

    public static void tameTheThings() {
        forceInit(ThingOne.class);
        forceInit(ThingTwo.class);
    }
}
