import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class NonConstructableGenericBaseClass<T> {
    private final int fieldA;
    private final Class<T> memberClass;

    private static final Class[] BASECLASS_CONSTRUCTOR_ARG_TYPES = { Class.class, int.class };

    @SuppressWarnings("unchecked")
    public static <T> NonConstructableGenericBaseClass<T> newInstance(final Class<T> memberClass,
                                                                      final int argA) throws NoSuchMethodException {
        final Constructor<NonConstructableGenericBaseClass> constructor =
                NonConstructableGenericBaseClass.class.getConstructor(BASECLASS_CONSTRUCTOR_ARG_TYPES);
        return newInstance(constructor, memberClass, argA);
    }

    public static <C extends NonConstructableGenericBaseClass<T>, T> C
            newInstance(final Class<C> subClassToConstruct,
                        final Class<T> memberClass,
                        final int argA) throws NoSuchMethodException {
        final Constructor<C> constructor = subClassToConstruct.getConstructor(BASECLASS_CONSTRUCTOR_ARG_TYPES);
        return newInstance(constructor, memberClass, argA);
    }

    public static <C extends NonConstructableGenericBaseClass<T>, T> C
            newInstance(final Constructor<C> constructor,
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

    public NonConstructableGenericBaseClass(final Class<T> memberClass, final int argA) {
        checkConstructorMagic();

        fieldA = argA;
        this.memberClass = memberClass;
    }

    public int getFieldA() {
        return fieldA;
    }

    public Class<T> getMemberClass() {
        return memberClass;
    }

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
