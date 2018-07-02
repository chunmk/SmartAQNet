package edu.teco.smartaqnet.sensorthings;

import com.google.gson.annotations.SerializedName;

/**
 * Describes a Datastream item as excpected by Frostserver and described
 * at http://developers.sensorup.com/docs/
 * The annotation @SerializedName is needed to adjust attribute names
 * to identifier as defined in SensorThings API
 */
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


    /**
     * Instantiates a new Datastream.
     *
     * @param thing_name the thing name
     */
    public Datastream(String thing_name){
        this.thing = new Thing(thing_name);
        this.id = thing_name + "_Datastream";
    }

    /**
     * Get Thing
     *
     * @return the thing
     */
    public Thing getThing(){
        return thing;
    }

    /**
     * Get Sensor.
     *
     * @return the sensor
     */
    public Sensor getSensor() {
        return sensor;
    }

    /**
     * Gets observedProperty.
     *
     * @return the observedProperty
     */
    public ObservedProperty getObservedProperty() {
        return observedProperty;
    }
}
