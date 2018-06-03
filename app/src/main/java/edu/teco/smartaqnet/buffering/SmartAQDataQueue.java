package edu.teco.smartaqnet.buffering;

import android.app.Activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;

public class SmartAQDataQueue {

    private ObjectQueue<String> smartAQDataqueue;

    public SmartAQDataQueue(Activity mainActivity){
        File outputDir = mainActivity.getCacheDir();
        String path = outputDir.toString() + "data.tmp";
        File outputFile = new File (path);
        try{
            QueueFile queueFile = new QueueFile.Builder(outputFile).build();
            ObjectQueue.Converter converter = new ObjectQueue.Converter() {

                @Override
                public Object from(byte[] data) throws IOException {
                    ByteArrayInputStream in = new ByteArrayInputStream(data);
                    ObjectInputStream is = new ObjectInputStream(in);
                    try {
                        return is.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void toStream(Object obj, OutputStream bytes) {
                    bytes  = new ByteArrayOutputStream();
                }
            };
            smartAQDataqueue = ObjectQueue.create(queueFile, converter);
        } catch (IOException e) {
            //TODO: Fehlerbehandlung FIFO Datei nicht erstellbar
                e.printStackTrace();
        }
    }

    public ObjectQueue<String> getSmartAQDataQueue(){
        return smartAQDataqueue;
    }

}
