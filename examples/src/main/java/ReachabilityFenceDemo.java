import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ReachabilityFenceDemo {

    static volatile Object o = null;
    static final int loopCount = 1024 * 1024 * 2;

    static ReferenceQueue<Object> q = new ReferenceQueue<>();
    static ArrayList<MyRef> refs = new ArrayList<>(20);

    static MyRef<Object> p1;
    static MyRef<Object> p2;

    static {
        for (int i = 0; i < 10; i++) {
            refs.add(new MyRef<Object>(new Object(), q, "loopobj " + i));
        }
    }

    static class MyRef<T> extends WeakReference<T> {
        String name;

        MyRef(T referent, ReferenceQueue<T> q, String name) {
            super(referent, q);
            this.name = name;
        }
    }

    static void poll() {
        Reference p;
        while ((p = q.poll()) != null) {
            System.out.printf("%s got collected\n", ((MyRef) p).name);
        }
    }

    static void doof(int doofNum) {
        Object o1 = new byte[1024];
        Object o2 = new byte[1024];
        refs.add(new MyRef<>(o1, q, "doof#" + doofNum + "-o1"));
        refs.add(new MyRef<>(o2, q, "doof#" + doofNum + "-o2"));

        try {
            System.gc();
            for (int i = 0; i < loopCount; i++) {
                o = new byte[1024];
            }
            System.gc();
            poll();
        } finally {
            Reference.reachabilityFence(o1);
        }
        System.out.printf("doof #%d past reachability fence.\n", doofNum);
        System.gc();
        for (int i = 0; i < loopCount; i++) {
            o = new byte[1024];
        }
        System.gc();
        poll();
        System.out.println("returning from doof #" + doofNum);
    }

    public static void main(final String[] args) {
        for (int i = 0; i < 5; i++) {
            System.out.printf("doof #%d:\n", i);
            doof(i);
            System.gc();
            poll();
        }
        System.out.printf("Back from doofs.\n");
        for (int i = 0; i < loopCount; i++) {
            o = new byte[1024];
        }
        System.gc();
        poll();
        System.out.println("refs.size() = " + refs.size());
    }
}
