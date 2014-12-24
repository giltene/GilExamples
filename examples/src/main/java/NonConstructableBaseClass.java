import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class NonConstructableBaseClass {

    public static NonConstructableBaseClass newInstance() throws NoSuchMethodException {
        return newInstance(NonConstructableBaseClass.class);
    }

    public static <C extends NonConstructableBaseClass> C newInstance(final Class<C> subClassToConstruct) throws NoSuchMethodException {
        final Constructor<C> constructor = subClassToConstruct.getConstructor();
        return newInstance(constructor);
    }

    public static <C extends NonConstructableBaseClass> C newInstance(final Constructor<C> constructor,
                                                                      final Object... constructorArgs) throws NoSuchMethodException {
        ConstructorMagic constructorMagic = getConstructorMagic();

        // constructorMagic.setActive(true);

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

    public NonConstructableBaseClass() {
        checkConstructorMagic();
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
