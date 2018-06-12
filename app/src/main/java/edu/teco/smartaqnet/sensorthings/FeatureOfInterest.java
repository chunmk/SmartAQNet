package edu.teco.smartaqnet.sensorthings;

import android.content.Context;

public class FeatureOfInterest {
    String name = "GPS-Daten";
    String description = "Actual Position";
    String encodingType = "application/vnd.geo+json";
    Feature feature;

    public FeatureOfInterest(Context context){
        this.feature = new Feature(context);
    }
}
