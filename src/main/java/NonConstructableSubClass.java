
public class NonConstructableSubClass<T> extends NonConstructableBaseClass<T> {
    private final int fieldB;
    private final int fieldC;

    @SuppressWarnings("unchecked")
    public static <T> NonConstructableSubClass<T> newInstance(Class<T> memberClass, int argA)
            throws NoSuchMethodException {
        return (NonConstructableSubClass<T>)
                NonConstructableBaseClass.newInstance(NonConstructableSubClass.class, memberClass, argA);
    }

    @SuppressWarnings("unchecked")
    public static <T> NonConstructableSubClass<T> newInstance(Class<T> memberClass, int argA, int argB, int argC)
            throws NoSuchMethodException {
        Class additionalConstructorArgTypes[] = {int.class, int.class};
        return (NonConstructableSubClass<T>)
                NonConstructableBaseClass.newInstance(NonConstructableSubClass.class, memberClass, argA,
                        additionalConstructorArgTypes, argB, argC);
    }

    public NonConstructableSubClass(Object constructorMagic, Class<T> memberClass, int argA, int argB, int argC) {
        super(constructorMagic, memberClass, argA);
        fieldB = argB;
        fieldC = argC;
    }

    public NonConstructableSubClass(Object constructorMagic, Class<T> memberClass, int argA) {
        super(constructorMagic, memberClass, argA);
        fieldB = 7;
        fieldC = 17;
    }

    public int getFieldB() {
        return fieldB;
    }

    public int getFieldC() {
        return fieldC;
    }
}

