package edu.teco.smartaqnet.sensorthings;

import com.google.gson.annotations.SerializedName;

public class Thing {
    String name = "Dust monitoring System";
    String description = "Test for SmartAQNet Android app development at teco.edu";
    @SerializedName("@iot.id")
    private String id = "Teco-AQNode";

    public Thing(String id){
        this.id = id;
    }
}
