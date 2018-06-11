package edu.teco.smartaqnet.sensorthings;


import edu.teco.smartaqnet.gps.GPSData;

public class GPSLocation {
    private String type;
    private String coordinates;

    public GPSLocation(){
        GPSData gps = new GPSData();
        //gps.getLocation().toString();
        this.type = "Point";
        this.coordinates = "[-114.133, 51.08]";
    }
}
