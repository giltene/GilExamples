
public class CompilerBarrierExample {
    static int regularInt;
    static volatile int volatileInt;


    static long playWithRegularInt(int value) {
        int regularInt = 87;
        for (int i = 0; i < 32768; i++) {
            regularInt++;
        }
        return regularInt;
    }

    static long playWithVolatileInt(int value) {
        volatileInt = 0;
        for (int i = 0; i < 32; i++) {
            volatileInt++;
        }
        return volatileInt;
    }

    static long doIt(int value) {
        long sum = 0;

        sum += playWithRegularInt(value);

        sum += playWithVolatileInt(value);

        return sum;
    }

    public static void main(String [] args) {
        long sum = 0;
        for (int i = 0; i < 1000000000; i++) {
            sum += doIt(i);
            if (i % 1000000 == 0) {
                System.out.print(".");
            }
        }
        System.out.println("sum = " + sum);
    }
}
