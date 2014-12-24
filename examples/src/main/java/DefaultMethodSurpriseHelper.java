/**
 * Created by gil on 3/18/14.
 */

public class DefaultMethodSurpriseHelper {

    public static void main(String[] args) {
        DefaultMethodSurpriseHelper2.Foo foo = new DefaultMethodSurpriseHelper2.Foo();
        foo.foo(42);  // prints IFoo.foo(int): 42
        foo.foo(42L);  // prints IFoo.foo(int): 42
//        Fooey fooey = foo;
//        fooey.foo(42); // prints IFoo.foo(int): 42
    }
}
