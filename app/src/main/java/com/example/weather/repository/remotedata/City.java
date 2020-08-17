package com.example.weather.repository.remotedata;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pankaj Kumar on 17/August/2020
 */
class City {
    @SerializedName("name")
    String cityName;

    @SerializedName("coord")
    Coordinate coordinates;

    @SerializedName("country")
    String country;

    public String getCityName() {
        return cityName;
    }

    public Coordinate getCoordinates() {
        return coordinates;
    }

    public String getCountry() {
        return country;
    }
}
