package com.example.weather.repository.db.remotedata;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pankaj Kumar on 17/August/2020
 */
class WeatherDescription {

    @SerializedName("main")
    String mainDescription;

    @SerializedName("description")
    String detailDescription;

    @SerializedName("icon")
    double iconId;

    public String getMainDescription() {
        return mainDescription;
    }

    public String getDetailDescription() {
        return detailDescription;
    }

    public double getIconId() {
        return iconId;
    }
}
