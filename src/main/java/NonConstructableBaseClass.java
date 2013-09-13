import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

public class NonConstructableBaseClass {
    private final int fieldA;

    private static final Class[] BASECLASS_CONSTRUCTOR_ARG_TYPES = {Object.class, int.class};

    public static NonConstructableBaseClass newInstance(final int argA) throws NoSuchMethodException {
        return newInstance(NonConstructableBaseClass.class, argA);
    }

    public static <C extends NonConstructableBaseClass> C newInstance(final Class<C> subClassToConstruct,
                                                                      final int argA) throws NoSuchMethodException {
        final Constructor<C> constructor = subClassToConstruct.getConstructor(BASECLASS_CONSTRUCTOR_ARG_TYPES);
        return newInstance(constructor, null /* constructorMagic placeholder */, argA);
    }

    public static <C extends NonConstructableBaseClass> C newInstance(final Constructor<C> constructor,
                                                                      final Object... constructorArgs) throws NoSuchMethodException {
        if (constructorArgs.length < 2) {
            throw new IllegalArgumentException("Constructor must have 2 or more args");
        }

        final ConstructorMagic constructorMagic = new ConstructorMagic();
        try {
            activeMagicObjects.put(constructorMagic, constructorMagic);
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

    public NonConstructableBaseClass(final Object constructorMagic, final int argA) {
        if (!(constructorMagic instanceof ConstructorMagic)) {
            throw new IllegalArgumentException("Bad magic construction parameter (type mismatch)");
        }
        checkConstructorMagic((ConstructorMagic) constructorMagic);

        fieldA = argA;
    }

    public int getFieldA() {
        return fieldA;
    }

    // ConstructorMagic support:

    private static class ConstructorMagic {
        private final Thread thread = Thread.currentThread();
        Thread getThread() {
            return thread;
        }
    }

    private static final ConcurrentHashMap<ConstructorMagic, ConstructorMagic> activeMagicObjects =
            new ConcurrentHashMap<ConstructorMagic, ConstructorMagic>();

    private static void checkConstructorMagic(final ConstructorMagic magic) {
        if (magic.getThread() != Thread.currentThread()) {
            throw new IllegalArgumentException("Bad magic construction parameter (thread mismatch)");
        }
        if (activeMagicObjects.remove(magic) == null) {
            throw new IllegalArgumentException("Bad magic construction parameter (not in active set)");
        }
    }
}
