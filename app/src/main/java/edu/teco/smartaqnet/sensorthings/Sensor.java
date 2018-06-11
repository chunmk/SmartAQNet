package edu.teco.smartaqnet.sensorthings;

import com.google.gson.annotations.SerializedName;

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
