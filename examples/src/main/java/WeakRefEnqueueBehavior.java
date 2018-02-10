import java.lang.ref.*;

public class WeakRefEnqueueBehavior {
    private static int MAX_NUM_GCS = 2;
    private static Object obj1 = new Object();
    private static Object obj2 = new Object();
    private static ReferenceQueue<Object> queue = new ReferenceQueue<>();

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        WeakReference<Object> wr1 = new WeakReference<Object>(obj1, queue);
        WeakReference<Object> wr11 = new WeakReference<Object>(obj1, queue);
        WeakReference<Object> wr2 = new WeakReference<Object>(obj2, queue);
        obj1 = null;
        obj2 = null;

        wr1.enqueue();

        for (int num_gcs = 0; num_gcs < MAX_NUM_GCS; num_gcs++) {
            System.out.println("[" + num_gcs + "] wr1[" + wr1 + "].get() = " + wr1.get());
            System.out.println("[" + num_gcs + "] wr11[" + wr11 + "].get() = " + wr11.get());
            System.out.println("[" + num_gcs + "] wr2[" + wr2 + "].get() = " + wr2.get());
            System.out.println("System.gc()...");
            System.gc();
        }

        WeakReference<Object> wr;
        while ((wr = (WeakReference<Object>) queue.poll()) != null) {
            System.out.println("Removed wr = " + wr);
        }

        for (int num_gcs = 0; num_gcs < MAX_NUM_GCS; num_gcs++) {
            System.out.println("[" + num_gcs + "] wr1[" + wr1 + "].get() = " + wr1.get());
            System.out.println("[" + num_gcs + "] wr11[" + wr11 + "].get() = " + wr11.get());
            System.out.println("[" + num_gcs + "] wr2[" + wr2 + "].get() = " + wr2.get());
            System.out.println("System.gc()...");
            System.gc();
        }

        System.out.println("Nulling out wr1:");
        wr1 = null;

        for (int num_gcs = 0; num_gcs < MAX_NUM_GCS; num_gcs++) {
            System.out.println("[" + num_gcs + "] wr11[" + wr11 + "].get() = " + wr11.get());
            System.out.println("System.gc()...");
            System.gc();
        }
    }
}
