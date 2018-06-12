package edu.teco.smartaqnet.dataprocessing;

import java.io.File;
import java.io.IOException;

public class SmartAQDataQueue {

    private ObjectQueue<SmartAQDataObject> smartAQDataqueue;

    public SmartAQDataQueue(String outputDir){
        String path = outputDir + "/data.tmp";
        //File not yet created, only path is registered
        File outputFile = new File (path);
        try{
            QueueFile queueFile = new QueueFile.Builder(outputFile).build();
            smartAQDataqueue = ObjectQueue.create(queueFile, ObjectByteConverterUtility.converter);
        } catch (IOException e) {
            //TODO: Fehlerbehandlung FIFO Datei nicht erstellbar
                e.printStackTrace();
        }
    }

    public ObjectQueue<SmartAQDataObject> getSmartAQDataQueue(){
        return smartAQDataqueue;
    }

}
