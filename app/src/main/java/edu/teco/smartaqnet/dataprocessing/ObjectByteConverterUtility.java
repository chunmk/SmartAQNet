package edu.teco.smartaqnet.dataprocessing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

final public class ObjectByteConverterUtility {

    /** Reusable byte output buffer. */
    private final static DirectByteArrayOutputStream bytes = new DirectByteArrayOutputStream();

    public static byte[] convertToByte(Object o){
        bytes.reset();
        try {
            converter.toStream(o, bytes);
        } catch (Exception e){
            //TODO: Handle Exception
            e.printStackTrace();
        }
        return bytes.getArray();
    }

    public static Object convertFromByte(byte[] bytes){
        try {
            return converter.from(bytes);
        } catch (Exception e){
            //TODO: Handle Exception
            e.printStackTrace();
        }
        return null;
    }

    public static ObjectQueue.Converter converter = new ObjectQueue.Converter() {

        @Override
        public Object from(byte[] data) throws IOException {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            try {
                return is.readObject();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void toStream(Object obj, OutputStream bytes) {
            try{
                ObjectOutputStream os = new ObjectOutputStream(bytes);
                os.writeObject(obj);
            } catch (Exception e) {
                //TODO: Handle exception
                e.printStackTrace();
            }
        }
    };


     /** Enables direct access to the internal array. Avoids unnecessary copying. */
    private static final class DirectByteArrayOutputStream extends ByteArrayOutputStream {
        DirectByteArrayOutputStream() {
        }

        /**
         * Gets a reference to the internal byte array.  The {@link #size()} method indicates how many
         * bytes contain actual data added since the last {@link #reset()} call.
         */
        byte[] getArray() {
            return buf;
        }
    }
}
