package edu.teco.smartaqnet.sensorthings;

import android.content.Context;

import java.util.ArrayList;

import edu.teco.smartaqnet.gps.GPSTracker;

/**
 * Describes a Feature item as excpected by Frostserver and described
 * at http://developers.sensorup.com/docs/
 */
public class Feature {

    String type = "Point";
    ArrayList<Double> coordinates;

    /**
     * Feature should hold coordinates for actually measured data,
     * therefore gps data are requested at creation
     *
     * @param context the context
     */
    public Feature(Context context){
        //TODO: Remove gps from here, avoiding creation of gps object for every feature
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
        gps.stopUsingGPS();
    }
}
