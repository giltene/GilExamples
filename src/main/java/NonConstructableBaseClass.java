import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NonConstructableBaseClass<T> {
    private final int fieldA;
    private final Class<T> memberClass;

    private static final Class[] EMPTY_ARG_TYPES = new Class[0];
    private static final Object[] EMPTY_ARGS = new Object[0];

    public static <T> NonConstructableBaseClass<T> newInstance(Class<NonConstructableBaseClass<T>> subClassToConstruct,
                                                               Class<T> memberClass,
                                                               int argA) throws NoSuchMethodException {
        return newInstance(subClassToConstruct, memberClass, argA, EMPTY_ARG_TYPES, EMPTY_ARGS);
    }

    public static <T> NonConstructableBaseClass<T> newInstance(Class<NonConstructableBaseClass<T>> subClassToConstruct,
                                                          Class<T> memberClass,
                                                          int argA,
                                                          final Class[] initArgTypes,
                                                          final Object... initArgs) throws NoSuchMethodException {
        final Class[] myInitArgTypes = new Class[initArgTypes.length + 3];
        myInitArgTypes[0] = Object.class;
        myInitArgTypes[1] = Class.class;
        myInitArgTypes[2] = int.class;
        System.arraycopy(initArgTypes, 0, myInitArgTypes, 3, initArgTypes.length);
        final Constructor<NonConstructableBaseClass<T>> constructor = subClassToConstruct.getConstructor(myInitArgTypes);

        ConstructorMagic constructorMagic = new ConstructorMagic();
        try {
            final Object[] myInitArgs = new Object[initArgs.length + 3];
            myInitArgs[0] = constructorMagic;
            myInitArgs[1] = memberClass;
            myInitArgs[2] = (Integer) argA;
            System.arraycopy(initArgs, 0, myInitArgs, 3, initArgs.length);
            return constructor.newInstance(myInitArgs);
        } catch (final InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (final IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (final InvocationTargetException ex) {
            throw new RuntimeException(ex);
        } finally {
            // Get rid of the constructorMagic in all cases, just in case someone tries to cache and cheat:
            activeMagics.remove(constructorMagic);
        }
    }

    public NonConstructableBaseClass(Object constructorMagic, Class<T> memberClass, int argA) {
        if (!(constructorMagic instanceof ConstructorMagic))
            throw new IllegalArgumentException("Bad magic construction parameter (type mismatch)");
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


    // ConstructorMagic support:

    private static class ConstructorMagic {
        private final Thread thread = Thread.currentThread();
        Thread getThread() {
            return thread;
        }

        ConstructorMagic() {
            activeMagics.add(this);
        }
    }

    private static Set<ConstructorMagic> activeMagics = Collections.synchronizedSet(new HashSet<ConstructorMagic>());

    private static void checkConstructorMagic(ConstructorMagic magic) {
        if (magic.getThread() != Thread.currentThread())
            throw new IllegalArgumentException("Bad magic construction parameter (thread mismatch)");
        if (!activeMagics.contains(magic))
            throw new IllegalArgumentException("Bad magic construction parameter (not in active set)");
        activeMagics.remove(magic);
    }
}
