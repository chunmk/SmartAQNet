package edu.teco.smartaqnet.sensorthings;

import android.content.Context;

import java.util.ArrayList;

import edu.teco.smartaqnet.gps.GPSTracker;

public class Feature {
    String type = "Point";
    ArrayList<Double> coordinates;

    public Feature(Context context){
        GPSTracker gps = new GPSTracker(context);
        coordinates = new ArrayList<>();

        //Check if GPS enabled
        if(gps.canGetLocation()) {
            coordinates.add(gps.getLatitude());
            coordinates.add(gps.getLongitude());

        } else {
//             Can't get location.
//             GPS or network is not enabled.
//             Ask user to enable GPS/network in settings.
            //TODO: Handle error
            coordinates.add(0.0);
            coordinates.add(0.0);
//            gps.showSettingsAlert();
        }
    }
}
