package edu.teco.smartaqnet.sensorthings;


import com.google.gson.annotations.SerializedName;

public class Observation {

    private String phenomenonTime;
    private String resultTime;
    private String result;
    @SerializedName("@iot.id")
    private String id;
    @SerializedName("Datastream")
    //Todo: Passenden Datastream holen
    private Datastream data = new Datastream("thing");
    private FeatureOfInterest feature;

    public Observation(String result){
        String timeStamp = TimestampUtils.getISO8601StringForCurrentDate();
        String uid =  UUID.md5();
        this.phenomenonTime = timeStamp;
        this.resultTime = timeStamp;
        this.result = result;
        this.id = uid;
        this.feature = new FeatureOfInterest();
    }

}

