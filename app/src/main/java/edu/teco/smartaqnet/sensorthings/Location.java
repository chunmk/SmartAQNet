package edu.teco.smartaqnet.sensorthings;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import static edu.teco.smartaqnet.sensorthings.UUID.md5;

public class Location {

    private String name = "Actual position";
    private String description = "GPS Data from Smartphone";
    private String encodingType = "application/vnd.geo+json";
    @SerializedName("@iot.id")
    private String id;
    @SerializedName("Things")
    private List<Thing> things;
    private GPSLocation location;

    public Location() {
        String uid = md5();
        location  = new GPSLocation();
        id = uid;
        things = new ArrayList<>();
        //Todo: passendes Thing holen
        things.add(new Thing("Thing"));
    }
}
