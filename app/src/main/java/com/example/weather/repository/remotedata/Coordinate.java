package com.example.weather.repository.remotedata;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pankaj Kumar on 17/August/2020
 */
class Coordinate {
    @SerializedName("lon")
    double lon;

    @SerializedName("lat")
    double lat;

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }
}
