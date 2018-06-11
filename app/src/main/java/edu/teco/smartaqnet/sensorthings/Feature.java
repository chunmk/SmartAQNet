package edu.teco.smartaqnet.sensorthings;

import edu.teco.smartaqnet.gps.GPSData;

public class Feature {
    String type = "Point";
    String coordinates;

    public Feature(){
        GPSData gps = new GPSData();
        coordinates = "[-114.133, 51.08]"; //gps.getLocation().toString();
    }
}
