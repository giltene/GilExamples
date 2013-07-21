/**
 * Created with IntelliJ IDEA.
 * User: gil
 * Date: 7/20/13
 * Time: 6:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class NonConstructableSubClass<T> extends NonConstructableBaseClass<T> {
    private final int fieldB;

    public static <T> NonConstructableSubClass<T> newInstance(Class<T> memberClass, int argA)
            throws NoSuchMethodException {
        final Class c = NonConstructableSubClass.class;
        final Class<NonConstructableBaseClass<T>> subClass = (Class<NonConstructableBaseClass<T>>) c;
        return (NonConstructableSubClass<T>)
                NonConstructableBaseClass.newInstance(subClass, memberClass, argA);
    }

    public static <T> NonConstructableSubClass<T> newInstance(Class<T> memberClass, int argA, int argB)
            throws NoSuchMethodException {
        Class myInitArgTypes[] = {int.class};
        final Class c = NonConstructableSubClass.class;
        final Class<NonConstructableBaseClass<T>> subClass = (Class<NonConstructableBaseClass<T>>) c;
        return (NonConstructableSubClass<T>)
                NonConstructableBaseClass.newInstance(subClass, memberClass, argA, myInitArgTypes, argB);
    }

    public NonConstructableSubClass(Object constructorMagic, Class<T> memberClass, int argA, int argB) {
        super(constructorMagic, memberClass, argA);
        fieldB = argB;
    }

    public NonConstructableSubClass(Object constructorMagic, Class<T> memberClass, int argA) {
        super(constructorMagic, memberClass, argA);
        fieldB = 7;
    }

    public int getFieldB() {
        return fieldB;
    }
}

