import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SubclassableShortArray {
    private short[] array;
    private static final Class[] BASECLASS_CONSTRUCTOR_ARG_TYPES = {int.class};

    public static SubclassableShortArray newInstance(final int length) throws NoSuchMethodException {
        return newInstance(SubclassableShortArray.class, length);
    }

    public static <C extends SubclassableShortArray> C newInstance(final Class<C> subClassToConstruct,
                                                                      final int length) throws NoSuchMethodException {
        final Constructor<C> constructor = subClassToConstruct.getConstructor(BASECLASS_CONSTRUCTOR_ARG_TYPES);
        return newInstance(constructor, length);
    }

    public static <C extends SubclassableShortArray> C newInstance(final Constructor<C> constructor,
                                                                      final Object... constructorArgs) throws NoSuchMethodException {
        if (constructorArgs.length < 1) {
            throw new IllegalArgumentException("Constructor must have 2 or more args");
        }

        threadLocalConstructorMagic.set(new ConstructorMagic());
        try {
            return constructor.newInstance(constructorArgs);
        } catch (final InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (final IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (final InvocationTargetException ex) {
            throw new RuntimeException(ex);
        } finally {
            // Get rid of the constructorMagic in all cases, just in case someone tries to cache and cheat:
            threadLocalConstructorMagic.set(null);
        }
    }

    public SubclassableShortArray(final int length) {
        checkConstructorMagic();
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

    // ConstructorMagic support:

    private static class ConstructorMagic {
    }

    private static final ThreadLocal<ConstructorMagic> threadLocalConstructorMagic = new ThreadLocal<ConstructorMagic>();

    private static void checkConstructorMagic() {
        final ConstructorMagic constructorMagic = threadLocalConstructorMagic.get();
        threadLocalConstructorMagic.set(null);
        if (constructorMagic == null) {
            throw new IllegalArgumentException("Bad magic construction parameter (not in active set)");
        }
    }
}
