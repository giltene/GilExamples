
public class InlineCacheExample {

    static class ClassWithColor {
        protected final int color;

        ClassWithColor(final int color) {
            this.color = color;
        }

        public int getColor() {
            return color;
        }
    }

    static class SubClassWithColor extends ClassWithColor {
        SubClassWithColor(final int color) {
            super(color);
        }

        public int getColor() {
            return color + 1;
        }
    }

    static long playWithColorA(ClassWithColor thingWithColor) {
        long sum = 0;
        for (int i = 0; i < 1000000; i++) {
            sum += thingWithColor.getColor() & 0xffff;
        }
        return sum;
    }

    static long playWithColorB(ClassWithColor thingWithColor) {
        long sum = 0;
        for (int i = 0; i < 1000000; i++) {
            sum += thingWithColor.getColor() & 0xffff;
        }
        return sum;
    }

    static Long compareToLong = new Long(0xffff);

    static long playWithEqualsA(Object thing) {
        long sum = 0;
        for (int i = 0; i < 1000000; i++) {
            sum += thing.equals(compareToLong) ? 1 : 0;
        }
        return sum;
    }

    static long playWithEqualsB(Object thing) {
        long sum = 0;
        for (int i = 0; i < 1000000; i++) {
            sum += thing.equals(compareToLong) ? 1 : 0;
        }
        return sum;
    }



    public static long doIt(int color) {
        long sum = 0;
        ClassWithColor thingOneWithColor = new ClassWithColor(color);
        ClassWithColor thingTwoWithColor = new SubClassWithColor(color);
        Long colorLong = new Long(color);

        // playWithColorA and playWithEqualsA are monomorphic:
        sum += playWithColorA(thingOneWithColor);
        sum += playWithEqualsA(colorLong);

        // playWithColorB and playWithEqualsB are megamorphic:
        sum += playWithColorB(((color & 1) == 1) ? thingOneWithColor : thingTwoWithColor);
        sum += playWithEqualsB(((color & 1) == 1) ? colorLong : thingTwoWithColor);

        return sum;
    }

    public static void main(String [] args) {
        long sum = 0;
        for (int i = 0; i < 100000; i++) {
            sum += doIt(i);
        }
        System.out.println("sum = " + sum);
    }
}
