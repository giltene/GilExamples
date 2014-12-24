import sun.misc.Cleaner;
import sun.misc.Unsafe;

import java.io.Serializable;
import java.util.List;

public class TypedArrayGames {
    List[] listArray;
    Object[] objectArray;
    Class listArrayClass = listArray.getClass();
    Class c = List[].class;

    private static Unsafe theUnsafe = Unsafe.getUnsafe();

    static public class Page {
        private long pointer;
        static volatile private Page tmp;

        public Page() {
            pointer = theUnsafe.allocateMemory(1024 * 1024);
        }

        void putLongAtOffset(long value, long offset) {
            long address = pointer + offset;
            theUnsafe.putLong(address, value);
        }

        long getLongAtOffset(long offset) {
            long address = pointer + offset;
            return theUnsafe.getLong(address);
        }

        long wellOrderedGetLongAtOffset(long offset) {
            long address = pointer + offset;
            long val = theUnsafe.getLong(address);
            // StoreLoad ordering ahead of volatile store keeps page in scope:
            tmp = this;
            return val;
        }

        protected void finalize() throws Throwable {
            super.finalize(); // java.lang.Object.finalize
            theUnsafe.freeMemory(pointer);
            tmp = this;
            pointer = 0;
        }
    }

    void simpleAccessTest() {
        Page page = new Page();
        page.putLongAtOffset(50, 42);
        long val = page.getLongAtOffset(50);
        assert (val == 42);
    }

    void simpleAccessTestInlinedEquivalent() {
        Page page = new Page();
        page.putLongAtOffset(50, 42);

        // Inlined equivalent of "long val = page.getLongAtOffset(50);" :

        long address = page.pointer + 50;
        // <<<race>>> : GC can legitimately collect page right here, and run the
        // finalizer before the next line executes.
        long val = theUnsafe.getLong(address); // Boom!

        assert (val == 42);
    }



    private class DeallocatePage implements Runnable
    {
        private long pagePointer;

        private DeallocatePage(long pagePointer) {
            this.pagePointer = pagePointer;
        }

        public void run() {
            theUnsafe.freeMemory(pagePointer);
        }
    }

    private Cleaner cleaner;

    TypedArrayGames() {
        long pagePointer = 0;
        cleaner = Cleaner.create(this, new DeallocatePage(pagePointer));
    }
}
