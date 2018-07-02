package edu.teco.smartaqnet.sensorthings;


import android.content.Context;

import com.google.gson.annotations.SerializedName;

import edu.teco.smartaqnet.dataprocessing.SmartAQDataObject;

/**
 * Describes an Observation item as excpected by Frostserver and described
 * at http://developers.sensorup.com/docs/
 * The annotation @SerializedName is needed to adjust attribute names
 * to identifier as defined in SensorThings API
 */
public class Observation {

    private String phenomenonTime;
    private String resultTime;
    private String result;
    @SerializedName("@iot.id")
    private String id;
    @SerializedName("Datastream")
    //Todo: Passenden Datastream holen
    private Datastream datastream;
    @SerializedName("FeatureOfInterest")
    private FeatureOfInterest feature;

    /**
     * Instantiates a new Observation.
     *
     * @param data       actually measured data from sensor
     * @param datastream Datastream that the observation belongs to
     * @param context    context needed for GPS call in Feature //TODO: GPS should not be called by Feature class
     */
    public Observation(SmartAQDataObject data, Datastream datastream, Context context){
        String uid =  UUID.md5();
        this.phenomenonTime = data.getTimeStamp();
        this.resultTime = data.getTimeStamp();
        this.result = data.getBleDustData();
        this.id = uid;
        this.feature = new FeatureOfInterest(context);
        this.datastream = datastream;
    }

}

