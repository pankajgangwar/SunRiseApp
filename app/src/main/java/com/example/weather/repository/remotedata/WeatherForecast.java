package com.example.weather.repository.remotedata;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Pankaj Kumar on 17/August/2020
 */
class WeatherForecast {

    @SerializedName("list")
    List<Weather> forecastList;

    public List<Weather> getForecastList() {
        return forecastList;
    }
}
