import java.lang.reflect.Constructor;

public class NonConstructableSubClass extends NonConstructableBaseClass {
    private final int fieldB;
    private final int fieldC;

    public static NonConstructableSubClass newInstance(int argA)
            throws NoSuchMethodException {
        return NonConstructableBaseClass.newInstance(NonConstructableSubClass.class, argA);
    }

    public static NonConstructableSubClass newInstance(int argA, int argB, int argC)
            throws NoSuchMethodException {
        return NonConstructableBaseClass.newInstance(fullConstructor,
                                                     null /* constructorMagic */, argA, argB, argC);
    }

    public NonConstructableSubClass(Object constructorMagic, int argA, int argB, int argC) {
        super(constructorMagic, argA);
        fieldB = argB;
        fieldC = argC;
    }

    public NonConstructableSubClass(Object constructorMagic, int argA) {
        super(constructorMagic, argA);
        fieldB = 7;
        fieldC = 17;
    }

    public int getFieldB() {
        return fieldB;
    }

    public int getFieldC() {
        return fieldC;
    }

    static final Class[] fullConstructorArgTypes = {Object.class /* magic*/, int.class, int.class, int.class};
    static final Constructor<NonConstructableSubClass> fullConstructor;

    static {
        try {
            fullConstructor = NonConstructableSubClass.class.getConstructor(fullConstructorArgTypes);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }
}

