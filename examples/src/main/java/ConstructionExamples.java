
import java.util.HashMap;

public class ConstructionExamples {

    static Object argObj;
    static Object argObj2;

    static HashMap<Integer, Object> objs = new HashMap<Integer, Object>();

    {
        objs.put(1, new Object());
        objs.put(2, new Object());
    }

    ConstructionExamples() {

    }

    ConstructionExamples(int a) {

    }

    ConstructionExamples(Object a) {

    }

    ConstructionExamples(Object a, Object b) {

    }

    public static ConstructionExamples noArgs() {
        ConstructionExamples o = new ConstructionExamples();
        return o;
    }

    public static ConstructionExamples OneConstIntArg() {
        ConstructionExamples o = new ConstructionExamples(5);
        return o;
    }

    public static ConstructionExamples OneSimpleArg() {
        ConstructionExamples o = new ConstructionExamples(argObj);
        return o;
    }

    public static ConstructionExamples TwoSimpleArgs() {
        ConstructionExamples o = new ConstructionExamples(argObj, argObj2);
        return o;
    }

    public static ConstructionExamples TwoEvaluatedArgs() {
        ConstructionExamples o = new ConstructionExamples(objs.get(1), objs.get(2));
        return o;
    }
}
