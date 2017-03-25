package bftsmart.demo.monitoringsystem.util;

import java.io.*;

public class SerializableUtil {

    public static <T> byte[] serialize(T object) {

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(object);
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public static <T> T deserialize(byte[] data) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(in);
            return (T) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
