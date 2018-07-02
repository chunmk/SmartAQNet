package edu.teco.smartaqnet.sensorthings;

import com.google.gson.annotations.SerializedName;

/**
 * Describes a Sensor item as excpected by Frostserver and described
 * at http://developers.sensorup.com/docs/
 * The annotation @SerializedName is needed to adjust attribute names
 * to identifier as defined in SensorThings API
 */
public class Sensor {
    String name = "SDS011";
    String description =  "Simple Dust Sensor";
    String encodingType = "application/x-www-form-urlencoded";
    String metadata =  "https://luftdaten.info/messgenauigkeit/";
    @SerializedName("@iot.id")
    String id = "SDS011 Sensor";

    public Sensor(){

    }
}
