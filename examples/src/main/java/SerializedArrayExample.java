import java.io.*;

public class SerializedArrayExample {

    public static void main(final String[] args)  {
        Integer [] array1 = new Integer[5];
        Integer [] array2 = new Integer[5];


        try {
            FileOutputStream o = new FileOutputStream("serializedArrayExample", false);
            ObjectOutputStream out = new ObjectOutputStream(o);

            for (int i = 0; i < 5; i++) {
                array1[i] = new Integer(16 + i);
            }
            out.writeObject(array1);
            out.flush();

            for (int i = 0; i < 5; i++) {
                array2[i] = new Integer(32 + i);
            }

            out.writeObject(array2);
            out.flush();
            o.close();
        } catch (IOException ex) {

        }
    }
}
