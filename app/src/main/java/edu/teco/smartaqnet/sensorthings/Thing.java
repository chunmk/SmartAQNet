package edu.teco.smartaqnet.sensorthings;

import com.google.gson.annotations.SerializedName;

/**
 * Describes an Thing item as excpected by Frostserver and described
 * at http://developers.sensorup.com/docs/
 * The annotation @SerializedName is needed to adjust attribute names
 * to identifier as defined in SensorThings API
 */
public class Thing {
    String name = "Dust monitoring System";
    String description = "Test for SmartAQNet Android app development at teco.edu";
    @SerializedName("@iot.id")
    private String id = "Teco-AQNode";

    public Thing(String id){
        this.id = id;
    }
}
