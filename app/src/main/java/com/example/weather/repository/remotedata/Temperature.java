package com.example.weather.repository.remotedata;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pankaj Kumar on 17/August/2020
 */
class Temperature {

    @SerializedName("min")
    double minTemp;

    @SerializedName("max")
    double maxTemp;

    public double getMinTemp() {
        return minTemp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }
}
