package edu.teco.smartaqnet.sensorthings;

import android.content.Context;

/**
 * Describes a Feature of Interest item as excpected by Frostserver and described
 * at http://developers.sensorup.com/docs/
 */
public class FeatureOfInterest {
    String name = "GPS-Daten";
    String description = "Actual Position";
    String encodingType = "application/vnd.geo+json";
    Feature feature;

    public FeatureOfInterest(Context context){
        this.feature = new Feature(context);
    }
}
