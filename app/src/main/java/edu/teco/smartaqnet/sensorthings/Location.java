package edu.teco.smartaqnet.sensorthings;

import com.google.gson.annotations.SerializedName;

import edu.teco.smartaqnet.gps.GPSData;

import static edu.teco.smartaqnet.sensorthings.UUID.md5;

public class Location {

    private String name = "Actual position";
    private String description = "GPS Data from Smartphone";
    private String encodingType = "Location";
    private String location;
    @SerializedName("@iot.id")
    private String id;
    private Thing thing;
    private Sensor sensor;
    private ObservedProperty observedProperty;

    public Location() {
        String uid = md5();
        GPSData gps = new GPSData();
        location  = gps.getLocation().toString();
        id = uid;
        thing = new Thing();
        sensor = new Sensor();
        observedProperty = new ObservedProperty();
    }
}
