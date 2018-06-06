package edu.teco.smartaqnet.buffering;

import android.app.Activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class SmartAQDataQueue {

    private ObjectQueue<String> smartAQDataqueue;

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

    public ObjectQueue<String> getSmartAQDataQueue(){
        return smartAQDataqueue;
    }

}
