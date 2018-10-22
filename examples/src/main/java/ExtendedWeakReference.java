import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class ExtendedWeakReference<T> extends WeakReference {

    public ExtendedWeakReference(T referent) { super(referent); }

    public ExtendedWeakReference(T referent, ReferenceQueue<? super T> q) { super(referent, q); }

    /**
     * Indicate whether this Reference's get() would return null.
     *
     * @return True if get() would return null. False otherwise.
     */
    public boolean isCleared()
    {
        return this.get() == null;
    }

    /**
     * Indicate whether this Reference's get() would refer to a given object
     *
     * @param o The object to which this Reference may or may not be referring
     * @return True if get() would return o. False otherwise.
     */
    public boolean isReferringTo(final T o)
    {
        return this.get() == o;
    }
}
