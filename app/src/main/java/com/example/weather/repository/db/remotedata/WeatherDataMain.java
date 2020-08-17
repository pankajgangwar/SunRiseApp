package com.example.weather.repository.db.remotedata;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Pankaj Kumar on 17/August/2020
 */
class WeatherDataMain {
    @SerializedName("city")
    City city;

    @SerializedName("list")
    List<Weather> weatherForecastList;

    public City getCity() {
        return city;
    }

    public List<Weather> getWeatherForecastList() {
        return weatherForecastList;
    }
}
