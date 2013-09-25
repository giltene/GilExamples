import java.lang.reflect.Constructor;

public class NonConstructableSubClass extends NonConstructableBaseClass {
    private final int fieldB;
    private final int fieldC;

    public static NonConstructableSubClass newInstance()
            throws NoSuchMethodException {
        return NonConstructableBaseClass.newInstance(NonConstructableSubClass.class, 5);
    }


    public static NonConstructableSubClass newInstance(int argA)
            throws NoSuchMethodException {
        return NonConstructableBaseClass.newInstance(NonConstructableSubClass.class, argA);
    }

    public static NonConstructableSubClass newInstance(int argA, int argB, int argC)
            throws NoSuchMethodException {
        return NonConstructableBaseClass.newInstance(fullConstructor, argA, argB, argC);
    }

    public NonConstructableSubClass(int argA, int argB, int argC) {
        super(argA);
        fieldB = argB;
        fieldC = argC;
    }

    public NonConstructableSubClass(int argA) {
        super(argA);
        fieldB = 7;
        fieldC = 17;
    }

    public int getFieldB() {
        return fieldB;
    }

    public int getFieldC() {
        return fieldC;
    }

    static final Class[] fullConstructorArgTypes = {int.class, int.class, int.class};
    static final Constructor<NonConstructableSubClass> fullConstructor;

    static {
        try {
            fullConstructor = NonConstructableSubClass.class.getConstructor(fullConstructorArgTypes);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }
}

