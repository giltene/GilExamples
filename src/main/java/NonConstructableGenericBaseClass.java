import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NonConstructableGenericBaseClass<T> {
    private final int fieldA;
    private final Class<T> memberClass;

    private static final Class[] BASECLASS_CONSTRUCTOR_ARG_TYPES = { Object.class, Class.class, int.class };

    @SuppressWarnings("unchecked")
    public static <T> NonConstructableGenericBaseClass<T> newInstance(final Class<T> memberClass,
                                                                      final int argA) throws NoSuchMethodException {
        final Constructor<NonConstructableGenericBaseClass> constructor =
                NonConstructableGenericBaseClass.class.getConstructor(BASECLASS_CONSTRUCTOR_ARG_TYPES);
        return newInstance(constructor, null /* constructorMagic placeholder*/, memberClass, argA);
    }

    public static <C extends NonConstructableGenericBaseClass<T>, T> C
            newInstance(final Class<C> subClassToConstruct,
                        final Class<T> memberClass,
                        final int argA) throws NoSuchMethodException {
        final Constructor<C> constructor = subClassToConstruct.getConstructor(BASECLASS_CONSTRUCTOR_ARG_TYPES);
        return newInstance(constructor, null /* constructorMagic placeholder*/, memberClass, argA);
    }

    public static <C extends NonConstructableGenericBaseClass<T>, T> C
            newInstance(final Constructor<C> constructor,
                        final Object... constructorArgs) throws NoSuchMethodException {
        if (constructorArgs.length < 2) {
            throw new IllegalArgumentException("Constructor must have 2 or more args");
        }

        final ConstructorMagic constructorMagic = new ConstructorMagic();
        try {
            activeMagicObjects.add(constructorMagic);
            constructorArgs[0] = constructorMagic;
            return constructor.newInstance(constructorArgs);
        } catch (final InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (final IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (final InvocationTargetException ex) {
            throw new RuntimeException(ex);
        } finally {
            // Get rid of the constructorMagic in all cases, just in case someone tries to cache and cheat:
            activeMagicObjects.remove(constructorMagic);
        }
    }

    public NonConstructableGenericBaseClass(final Object constructorMagic, final Class<T> memberClass, final int argA) {
        if (!(constructorMagic instanceof ConstructorMagic)) {
            throw new IllegalArgumentException("Bad magic construction parameter (type mismatch)");
        }
        checkConstructorMagic((ConstructorMagic) constructorMagic);

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
        private final Thread thread = Thread.currentThread();
        Thread getThread() {
            return thread;
        }
    }

    private static final Set<ConstructorMagic> activeMagicObjects =
            Collections.synchronizedSet(new HashSet<ConstructorMagic>());

    private static void checkConstructorMagic(final ConstructorMagic magic) {
        if (magic.getThread() != Thread.currentThread()) {
            throw new IllegalArgumentException("Bad magic construction parameter (thread mismatch)");
        }
        if (!activeMagicObjects.contains(magic)) {
            throw new IllegalArgumentException("Bad magic construction parameter (not in active set)");
        }

        activeMagicObjects.remove(magic);
    }
}
