package edu.teco.smartaqnet.sensorthings;

import com.google.gson.annotations.SerializedName;

public class ObservedProperty {

    String name = "Particle density";
    String definition = "https://www.epa.gov/pm-pollution";
    String description = "Particualte matter per cubic meter air";
    @SerializedName("@iot.id")
    String id = "Teco-AQnode Particle density";

    public ObservedProperty(){

    }
}
