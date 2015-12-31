import java.util.Random;

public class RandomArrayShuffle {

    static final int lengthPow2 = 4;
    static final int length = 1 << lengthPow2;
    static final int lengthMask = length - 1;

    static class MockObject {
        int nextIndex;

        MockObject(final int nextIndex) {
            this.nextIndex = nextIndex;
        }

        public int getNextIndex() {
            return nextIndex;
        }

        public void setNextIndex(final int nextIndex) {
            this.nextIndex = nextIndex;
        }
    }

    static final MockObject array[] = new MockObject[length];

    static {
        for (int i = 0; i < length; i++) {
            array[i] = new MockObject(0);
        }
    }

    static void testArrayAccessSpeed() {
        int index = 0;
        for (int i = 0; i < length; i++) {
            int nextIndex = array[index].nextIndex;
            index = nextIndex;
        }
    }

    static void initLinear() {
        for (int i = 0; i < length; i++) {
            array[i].nextIndex = (i + 1) & lengthMask;
        }
    }

    static void initShuffled() {
        for (int i = 0; i < length; i++) {
            array[i].nextIndex = (i + 1) & lengthMask;
        }

        Random generator = new Random(42);

        System.out.println("initShuffled():");
        for (int i = 0; i < length; i++) {
            int target = generator.nextInt(length);
            int tempNextIndex = array[target].getNextIndex();
            array[target].setNextIndex(array[i].getNextIndex());
            array[i].setNextIndex(tempNextIndex);
        }
        for (int i = 0; i < length; i++) {
//            System.out.print(i + "->" + ((i + array[i].getNextIndex()) & lengthMask) + ", ");
        }
    }

    static void initShuffledComplete() {
        boolean visited[] = new boolean[length];
        Random generator = new Random(42);

        System.out.println("initShuffledComplete():");
        int index = 0;
        for (int i = 0; i < length - 1; i++) {
            visited[index] = true;
            // locate an unvisited next index starting from random point:
            int nextIndex = generator.nextInt(length);
            while (visited[nextIndex]) {
                nextIndex = (nextIndex + 1) & lengthMask;
            }
            array[index].setNextIndex(nextIndex);
            index = nextIndex;
        }
        // Last index's nextIndex should go back to 0:
        array[index].setNextIndex(0);

        for (int i = 0; i < length; i++) {
//            System.out.print(i + "->" + ((i + array[i].getNextIndex()) & lengthMask) + ", ");
        }
        System.out.println("");
    }

    static void findSquenceLength() {
        boolean visited[] = new boolean[length];
        int index = 0;
        int sequenceLength = 0;
        System.out.println("");
        for (int i = 0; i < length; i++) {
            int prevIndex = index;
            index = array[index].getNextIndex();
            if (visited[index]) {
                break;
            }
            System.out.print(prevIndex + "->" + index + ",");
            sequenceLength++;
            visited[index] = true;
        }
        System.out.println("");
        System.out.format("BBB Sequence length = %d (%7.5fx of %d)\n",
                sequenceLength, (sequenceLength * 1.0 / length), length);
    }

    public static void main(String[] args) {
        initLinear();
        for (int i = 0; i < 100; i++) {
            testArrayAccessSpeed();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }

        long startTime, endTime;

        initLinear();
        startTime = System.nanoTime();
        testArrayAccessSpeed();
        for (int i = 0; i < 100; i++) {
            testArrayAccessSpeed();
        }
        endTime = System.nanoTime();
        System.out.println("Time per linear scan = " + (endTime - startTime) / (1000.0 * 100.0) + "usec");
        findSquenceLength();

        initShuffled();
        startTime = System.nanoTime();
        testArrayAccessSpeed();
        for (int i = 0; i < 10; i++) {
            testArrayAccessSpeed();
        }
        endTime = System.nanoTime();
        System.out.println("Time per Shuffled scans = " + (endTime - startTime) / (1000.0 * 10.0) + "usec");
        findSquenceLength();

        initShuffledComplete();
        startTime = System.nanoTime();
        testArrayAccessSpeed();
        for (int i = 0; i < 10; i++) {
            testArrayAccessSpeed();
        }
        endTime = System.nanoTime();
        System.out.println("Time per CompleteShuffled scans = " + (endTime - startTime) / (1000.0 * 10.0) + "usec");
        findSquenceLength();
    }
}
