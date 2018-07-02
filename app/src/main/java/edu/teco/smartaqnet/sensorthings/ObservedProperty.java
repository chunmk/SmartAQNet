package edu.teco.smartaqnet.sensorthings;

import com.google.gson.annotations.SerializedName;

/**
 * Describes an ObservedProperty item as excpected by Frostserver and described
 * at http://developers.sensorup.com/docs/
 * The annotation @SerializedName is needed to adjust attribute names
 * to identifier as defined in SensorThings API
 */
public class ObservedProperty {

    String name = "Particle density";
    String definition = "https://www.epa.gov/pm-pollution";
    String description = "Particualte matter per cubic meter air";
    @SerializedName("@iot.id")
    String id = "Teco-AQnode Particle density";

    public ObservedProperty(){

    }
}
