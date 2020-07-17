import java.io.File;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoinIOingExample {

    static final ForkJoinPool pool = new ForkJoinPool();

    static public class IOingRecursiveAction extends RecursiveAction {

        private long units = 0;

        public IOingRecursiveAction(long units) {
            this.units = units;
        }

        @Override
        protected void compute() {
            if (units > 1) {
                IOingRecursiveAction subActionOne = new IOingRecursiveAction(units / 2);
                IOingRecursiveAction subActionTwo = new IOingRecursiveAction(units - subActionOne.units);
                subActionOne.fork();
                subActionTwo.fork();
            } else {
                System.out.println("Compute....");
                try {
                    File file = new File("doof");
                    if (file.exists()) {
                        System.out.println("Found doof!");
                    }
                } catch (Exception ex) {
                    System.out.println("Grrr... caught exception while trying to look for doof: " + ex);
                }
            }
        }
    }

    static void doIt() {
        pool.invoke(new IOingRecursiveAction(7));
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
        }
    }

    public static void main(String[] args) {
        System.out.println("IO stuff with no security manager:");
        doIt();
        System.out.println("IO stuff with security manager:");
        System.setSecurityManager(new SecurityManager());
        doIt();
    }
}
