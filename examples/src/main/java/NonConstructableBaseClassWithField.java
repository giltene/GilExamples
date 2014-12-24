import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class NonConstructableBaseClassWithField {
    private final int fieldA;

    private static final Class[] BASECLASS_CONSTRUCTOR_ARG_TYPES = {int.class};

    public static NonConstructableBaseClassWithField newInstance(final int argA) throws NoSuchMethodException {
        return newInstance(NonConstructableBaseClassWithField.class, argA);
    }

    public static <C extends NonConstructableBaseClassWithField> C newInstance(final Class<C> subClassToConstruct,
                                                                      final int argA) throws NoSuchMethodException {
        final Constructor<C> constructor = subClassToConstruct.getConstructor(BASECLASS_CONSTRUCTOR_ARG_TYPES);
        return newInstance(constructor, argA);
    }

    public static <C extends NonConstructableBaseClassWithField> C newInstance(final Constructor<C> constructor,
                                                                      final Object... constructorArgs) throws NoSuchMethodException {
        if (constructorArgs.length < 1) {
            throw new IllegalArgumentException("Constructor must have 2 or more args");
        }

        ConstructorMagic constructorMagic = getConstructorMagic();

        constructorMagic.setActive(true);
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
            constructorMagic.setActive(false);
        }
    }

    public NonConstructableBaseClassWithField(final int argA) {
        checkConstructorMagic();
        fieldA = argA;
    }

    public int getFieldA() {
        return fieldA;
    }

    // ConstructorMagic support:

    private static class ConstructorMagic {
        private boolean isActive() {
            return active;
        }

        private void setActive(boolean active) {
            this.active = active;
        }

        boolean active = false;
    }

    private static final ThreadLocal<ConstructorMagic> threadLocalConstructorMagic = new ThreadLocal<ConstructorMagic>();

    private static ConstructorMagic getConstructorMagic() {
        ConstructorMagic constructorMagic = threadLocalConstructorMagic.get();
        if (constructorMagic == null) {
            constructorMagic = new ConstructorMagic();
            threadLocalConstructorMagic.set(constructorMagic);
        }
        return constructorMagic;
    }

    private static void checkConstructorMagic() {
        final ConstructorMagic constructorMagic = threadLocalConstructorMagic.get();
        if ((constructorMagic == null) || !constructorMagic.isActive()) {
            throw new IllegalArgumentException("Bad magic construction parameter (not in active set)");
        }
        constructorMagic.setActive(false);
    }
}
