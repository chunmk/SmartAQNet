package edu.teco.smartaqnet.dataprocessing;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/*
 * Creates an object to hold measured data with actual timestamp from sensor
 * Used to store measurements in FIFO queue, so that data could be sent
 * to server independent from measurement point in time
 */
public class SmartAQDataObject implements Serializable{
    private String bleDustData;
    //UUID needed for serialization
    private static final long serialVersionUID = 6526472154622876147L;
    private String timeStamp;

    public void setBleDustData(String bleDustData){
        this.bleDustData = bleDustData;
    }
    public void setTimeStamp(String timeStamp){ this .timeStamp = timeStamp; }
    public String getBleDustData(){
        return bleDustData;
    }
    public String getTimeStamp() {
        return timeStamp;
    }


    //Needed methods to make SmartAQDataObjects serializable
    @NonNull
    private void writeObject( ObjectOutputStream oos ) throws IOException
    {
        oos.defaultWriteObject();
    }

    @NonNull
    private void readObject( ObjectInputStream ois ) throws IOException
    {
        try
        {
            ois.defaultReadObject();
        }
        catch ( ClassNotFoundException e )
        {
            throw new IOException( "No class found. HELP!!" );
        }
    }
}
