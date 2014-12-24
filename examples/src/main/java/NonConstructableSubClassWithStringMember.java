import java.lang.reflect.Constructor;

public class NonConstructableSubClassWithStringMember extends NonConstructableGenericBaseClass<String> {
    private final int fieldB;
    private final int fieldC;

    public static NonConstructableSubClassWithStringMember newInstance(int argA)
            throws NoSuchMethodException {
        return NonConstructableGenericBaseClass.newInstance(NonConstructableSubClassWithStringMember.class, String.class, argA);
    }

    public static NonConstructableSubClassWithStringMember newInstance(int argA, int argB, int argC)
            throws NoSuchMethodException {
        return NonConstructableGenericBaseClass.newInstance(fullConstructor,
                                                            String.class, argA, argB, argC);
    }

    public NonConstructableSubClassWithStringMember(Class<String> memberClass,
                                                    int argA, int argB, int argC) {
        super(memberClass, argA);
        fieldB = argB;
        fieldC = argC;
    }

    public NonConstructableSubClassWithStringMember(Class<String> memberClass, int argA) {
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
    static final Constructor<NonConstructableSubClassWithStringMember> fullConstructor;

    static {
        try {
            fullConstructor = NonConstructableSubClassWithStringMember.class.getConstructor(fullConstructorArgTypes);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }
}

