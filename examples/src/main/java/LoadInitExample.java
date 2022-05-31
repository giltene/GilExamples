public class LoadInitExample {

    public static void main(String[] args) {
        new MyBean();
    }

    static class MyBean {
        static {
            System.out.println("Class MyBean initialized.");
        }

        static A a = new A();
        static B b;

        C c = new C();
        D d;
        E e;
        A a2;

        public MyBean() {
            System.out.println("Constructor MyBean() called.");
            d = new D();
            a2 = e;
        }
    }

    static class A {
        static {System.out.println("Class A initialized.");}
    }

    static class B {
        static {System.out.println("Class B initialized.");}
    }

    static class C {
        static {System.out.println("Class C initialized.");}
    }

    static class D {
        static {System.out.println("Class D initialized.");}
    }

    static class E extends A {
        static {System.out.println("Class E initialized.");}
    }

    static class F {
        static {System.out.println("Class F initialized.");}
    }
}
