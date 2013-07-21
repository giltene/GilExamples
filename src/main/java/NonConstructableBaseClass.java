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

    public static <T> NonConstructableBaseClass<T> newInstance(Class<T> memberClass,
                                                               int argA) throws NoSuchMethodException {
        return newInstance(NonConstructableBaseClass.class, memberClass, argA, EMPTY_ARG_TYPES, EMPTY_ARGS);
    }

    public static <C extends NonConstructableBaseClass<T>, T> C newInstance(Class<C> subClassToConstruct,
                                                                            Class<T> memberClass,
                                                                            int argA) throws NoSuchMethodException {
        return newInstance(subClassToConstruct, memberClass, argA, EMPTY_ARG_TYPES, EMPTY_ARGS);
    }

    public static <C extends NonConstructableBaseClass<T>, T> C  newInstance(Class<C> subClassToConstruct,
                                                                             Class<T> memberClass,
                                                                             int argA,
                                                                             final Class[] additionalSubClassArgTypes,
                                                                             final Object... additionalSubClassArgs) throws NoSuchMethodException {
        final Class[] constructorArgTypes = new Class[additionalSubClassArgTypes.length + 3];
        constructorArgTypes[0] = Object.class; // Placeholder for ConstructorMagic
        constructorArgTypes[1] = Class.class;
        constructorArgTypes[2] = int.class;
        System.arraycopy(additionalSubClassArgTypes, 0, constructorArgTypes, 3, additionalSubClassArgTypes.length);
        final Constructor<C> constructor = subClassToConstruct.getConstructor(constructorArgTypes);

        final Object[] constructorArgs = new Object[additionalSubClassArgs.length + 3];
        constructorArgs[0] = null; // Placeholder for constructorMagic
        constructorArgs[1] = memberClass;
        constructorArgs[2] = (Integer) argA;
        System.arraycopy(additionalSubClassArgs, 0, constructorArgs, 3, additionalSubClassArgs.length);

        return newInstance(constructor, constructorArgs);
    }

    public static <C extends NonConstructableBaseClass<T>, T> C  newInstance(Constructor<C> constructor,
                                                                             final Object... constructorArgs) throws NoSuchMethodException {
        ConstructorMagic constructorMagic = new ConstructorMagic();
        try {
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
