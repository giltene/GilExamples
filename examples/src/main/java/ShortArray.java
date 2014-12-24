import java.lang.reflect.Constructor;

public class ShortArray extends NonConstructableBaseClass {
    private short[] array;

    public static ShortArray newInstance(int length)
            throws NoSuchMethodException {
        return NonConstructableBaseClass.newInstance(fullConstructor, length);
    }

    public static <C extends NonConstructableBaseClass> C newInstance(final Constructor<C> constructor,
                                                       final Object... constructorArgs) throws NoSuchMethodException {
        return NonConstructableBaseClass.newInstance(constructor, constructorArgs);
    }

    public ShortArray(final int length) {
        array = new short[length];
    }

    public int getLength() {
        return array.length;
    }

    public short get(int index) {
        return array[index];
    }

    public void set(int index, short value) {
        array[index] = value;
    }

    static final Class[] fullConstructorArgTypes = {int.class};
    static final Constructor<ShortArray> fullConstructor;

    static {
        try {
            fullConstructor = ShortArray.class.getConstructor(fullConstructorArgTypes);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }
}

