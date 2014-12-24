
public class DefaultMethodSurpriseHelper2 {
    public static class Foo implements DefaultMethodSurprise.Fooey {
        public void fooey() {}

        public void foo(long l) {
            System.out.println("Foo.foo(long): " + l);
        }
    }
}
