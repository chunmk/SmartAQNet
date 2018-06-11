package edu.teco.smartaqnet;

import android.app.Activity;
import android.app.Application;

import edu.teco.smartaqnet.sensorthings.Datastream;

public class ApplicationGlobal extends Application{

    private Datastream datastream;

    public ApplicationGlobal(){
        super();
    }

    public void setDatastream(Datastream datastream){
        this.datastream = datastream;
    }

    public Datastream getDatastream() {
        return datastream;
    }
}
