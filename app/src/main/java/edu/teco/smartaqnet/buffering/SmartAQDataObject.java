package edu.teco.smartaqnet.buffering;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SmartAQDataObject implements Serializable{
    private String bleDustData;
    private static final long serialVersionUID = 6526472154622876147L;

    public void setBleDustData(String bleDustData){
        this.bleDustData = bleDustData;
    }

    public String getBleDustData(){
        return bleDustData;
    }
    @NonNull
    private void writeObject( ObjectOutputStream oos ) throws IOException
    {
        oos.defaultWriteObject();  // Schreib Name, aber kein Alter
    }

    @NonNull
    private void readObject( ObjectInputStream ois ) throws IOException
    {
        try
        {
            ois.defaultReadObject(); // Lies Name, aber ohne Alter
        }
        catch ( ClassNotFoundException e )
        {
            throw new IOException( "No class found. HELP!!" );
        }
    }
}
