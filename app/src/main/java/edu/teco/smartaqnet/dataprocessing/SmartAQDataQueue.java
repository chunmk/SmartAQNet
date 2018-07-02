package edu.teco.smartaqnet.dataprocessing;

import java.io.File;
import java.io.IOException;


/**
 * Defines FIFO to hold data measured by sensor
 * For FIFO library from https://github.com/square/tape is taken
 * <dependency>
 * <groupId>com.squareup.tape2</groupId>
 * <artifactId>tape</artifactId>
 * <version>2.0.0-SNAPSHOT</version>
 * </dependency>
 * didn't work, so code has been imported
 * TODO: Should be changed to singleton as only one queue is needed
 */
public class SmartAQDataQueue {

    private ObjectQueue<SmartAQDataObject> smartAQDataqueue;

    /**
     * Instantiates a new queue.
     *
     * @param outputDir the output dir
     * TODO: Move call for outputDir to this place
     */
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

    /**
     * Get queue.
     *
     * @return the object queue
     */
    public ObjectQueue<SmartAQDataObject> getSmartAQDataQueue(){
        return smartAQDataqueue;
    }

}
