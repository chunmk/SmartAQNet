package edu.teco.smartaqnet.sensorthings;


import com.google.gson.annotations.SerializedName;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import edu.teco.smartaqnet.gps.GPSData;

public class Observation {

    private String name;
    private String phenomenonTime;
    private String resultTime;
    private String result;
    @SerializedName("@iot.id")
    private String id;
    private Thing thing;
    private Sensor sensor;
    private ObservedProperty observedProperty;

    public Observation(String result){
        Date time = Calendar.getInstance().getTime();
        String timeStamp = time.toString();
        String uid =  UUID.md5();
        this.phenomenonTime = timeStamp;
        this.resultTime = timeStamp;
        this.result = result;
        this.id = uid;
        thing = new Thing();
        sensor = new Sensor();
        observedProperty = new ObservedProperty();

    }

}

