package edu.teco.smartaqnet.sensorthings;

import com.google.gson.annotations.SerializedName;

public class Datastream {
    private String name = "Teco SmartAQNet";
    private String description = "Datastream for recording particle density ";
    private String observationType =  "Testobservation";
    @SerializedName("@iot.id")
    private String id;
    @SerializedName("Thing")
    private Thing thing;
    @SerializedName("ObservedProperty")
    private ObservedProperty observedProperty = new ObservedProperty();
    @SerializedName("Sensor")
    private Sensor sensor = new Sensor();


    public Datastream(String thing_name){
        this.thing = new Thing(thing_name);
        this.id = thing_name + "_Datastream";
    }
    public Thing getThing(){
        return thing;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public ObservedProperty getObservedProperty() {
        return observedProperty;
    }
}
