package edu.teco.smartaqnet.sensorthings;


import android.content.Context;

import com.google.gson.annotations.SerializedName;

import edu.teco.smartaqnet.dataprocessing.SmartAQDataObject;

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

    public Observation(SmartAQDataObject data, Datastream datastream, Context context){
        String timeStamp = TimestampUtils.getISO8601StringForCurrentDate();
        String uid =  UUID.md5();
        this.phenomenonTime = data.getTimeStamp();
        this.resultTime = data.getTimeStamp();
        this.result = data.getBleDustData();
        this.id = uid;
        this.feature = new FeatureOfInterest(context);
        this.datastream = datastream;
    }

}

