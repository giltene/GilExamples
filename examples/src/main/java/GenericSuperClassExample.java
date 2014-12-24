
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GenericSuperClassExample {

    public static void main(String args[]) {

        // returns the superclass
        System.out.println("\nNon-generic subclass of generic class:");
        System.out.println("----------------------------------------");

        System.out.println("Class: " + IntegerArrayListClass.class);
        System.out.print("Generic superclass: ");
        Type t = IntegerArrayListClass.class.getGenericSuperclass();
        System.out.println(t);

        ParameterizedType p = (ParameterizedType)t;
        System.out.println("Generic type 0: " + p.getActualTypeArguments()[0]);

        System.out.println("\nGeneric subclass of generic class:");
        System.out.println("----------------------------------------");

        System.out.println("Class: " + GenericIntegerArrayListClass.class);
        System.out.print("Generic superclass: ");
        t = GenericIntegerArrayListClass.class.getGenericSuperclass();
        System.out.println(t);

        p = (ParameterizedType)t;
        System.out.println("Generic type 0: " + p.getActualTypeArguments()[0]);

        System.out.println("\nPartially generic subclass of generic class:");
        System.out.println("----------------------------------------");

        System.out.println("Class: " + PartiallyGenericIntegerArrayListClass.class);
        System.out.print("Generic superclass: ");
        t = PartiallyGenericIntegerArrayListClass.class.getGenericSuperclass();
        System.out.println(t);

        p = (ParameterizedType)t;
        System.out.println("Generic type 0: " + p.getActualTypeArguments()[0]);
        System.out.println("Generic type 1: " + p.getActualTypeArguments()[1]);

        System.out.println("Type of Generic type 0: " + p.getActualTypeArguments()[0].getClass());
        System.out.println("Type of Generic type 1: " + p.getActualTypeArguments()[1].getClass());

        System.out.println("typeToClass of Generic type 0: " + typeToClass(p.getActualTypeArguments()[0]));
        System.out.println("typeToClass of Generic type 1: " + typeToClass(p.getActualTypeArguments()[1]));

    }

    private static Class typeToClass(Type t) {
        if (t instanceof Class) {
            return (Class) t;
        } else if (t instanceof ParameterizedType) {
            return (Class) ((ParameterizedType)t).getRawType();
        } else {
            return null;
        }
    }
}

class IntegerArrayListClass extends ArrayList<Integer> {
    public IntegerArrayListClass() {
        // no argument constructor
    }
}

class GenericIntegerArrayListClass<T> extends ArrayList<T> {
    public GenericIntegerArrayListClass() {
        // no argument constructor
    }
}

class PartiallyGenericIntegerArrayListClass<V> extends HashMap<Integer, V> {
    public PartiallyGenericIntegerArrayListClass() {
        // no argument constructor
    }
}

