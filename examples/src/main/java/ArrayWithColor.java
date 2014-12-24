import java.lang.reflect.Constructor;

public class ArrayWithColor extends ShortArray {
    final int color;

    ArrayWithColor(int length, int color) {
        super(length);
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    // Boilerplate:

    public static ArrayWithColor newInstance(int length, int color)
            throws NoSuchMethodException {
        return NonConstructableBaseClass.newInstance(fullConstructor, length, color);
    }

    static final Class[] fullConstructorArgTypes = {int.class, int.class};
    static final Constructor<ArrayWithColor> fullConstructor;

    static {
        try {
            fullConstructor = ArrayWithColor.class.getConstructor(fullConstructorArgTypes);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }
}
