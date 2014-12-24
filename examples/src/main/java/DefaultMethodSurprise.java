
public class DefaultMethodSurprise {
    public static interface Fooey {
        void fooey();

//        default void foo(int i) {
//            System.out.println("IFoo.foo(int): " + i);
//        }
    }

    public static class Foo implements Fooey {
        public void fooey() {}

        public void foo(long l) {
            System.out.println("Foo.foo(long): " + l);
        }
    }

    public static void main(String[] args) {
        Foo foo = new Foo();
        foo.foo(42);  // prints IFoo.foo(int): 42
        foo.foo(42L);  // prints IFoo.foo(int): 42
//        Fooey fooey = foo;
//        fooey.foo(42); // prints IFoo.foo(int): 42
    }
}
