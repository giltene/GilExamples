import java.lang.reflect.Constructor;

public class NonConstructableGenericSubClass<T> extends NonConstructableGenericBaseClass<T> {
    private final int fieldB;
    private final int fieldC;

    @SuppressWarnings("unchecked")
    public static <T> NonConstructableGenericSubClass<T> newInstance(Class<T> memberClass, int argA)
            throws NoSuchMethodException {
        return (NonConstructableGenericSubClass<T>)
                NonConstructableGenericBaseClass.newInstance(NonConstructableGenericSubClass.class, memberClass, argA);
    }

    @SuppressWarnings("unchecked")
    public static <T> NonConstructableGenericSubClass<T> newInstance(Class<T> memberClass, int argA, int argB, int argC)
            throws NoSuchMethodException {
        return (NonConstructableGenericSubClass<T>)
                NonConstructableGenericBaseClass.newInstance(fullConstructor, memberClass, argA, argB, argC);
    }

    public NonConstructableGenericSubClass(Class<T> memberClass, int argA, int argB, int argC) {
        super(memberClass, argA);
        fieldB = argB;
        fieldC = argC;
    }

    public NonConstructableGenericSubClass(Class<T> memberClass, int argA) {
        super(memberClass, argA);
        fieldB = 7;
        fieldC = 17;
    }

    public int getFieldB() {
        return fieldB;
    }

    public int getFieldC() {
        return fieldC;
    }

    static final Class[] fullConstructorArgTypes = {Class.class, int.class, int.class, int.class};
    static final Constructor<NonConstructableGenericSubClass> fullConstructor;

    static {
        try {
            fullConstructor = NonConstructableGenericSubClass.class.getConstructor(fullConstructorArgTypes);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }
}

