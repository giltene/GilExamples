import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class ExtendedWeakReference<T> extends WeakReference {
    /**
     * Indicate whether this Reference has been cleared
     *
     * @return True if the reference is cleared. False otherwise.
     */
    boolean isCleared()
    {
        return this.get() == null;
    }

    /**
     * Indicate whether this Reference refers to a given object
     *
     * @param o The object to which this Reference may or may not be referring
     * @return True if this Reference's referent is o. False otherwise.
     */
    boolean isReferringTo(final T o)
    {
        return this.get() == o;
    }
}
