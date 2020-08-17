package com.example.weather.repository.db.remotedata;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Pankaj Kumar on 17/August/2020
 */
class Weather {
    @SerializedName("weather")
    List<WeatherDescription> weatherDescriptionList;

    @SerializedName("temp")
    Temperature temperature;

    @SerializedName("dt")
    long date;

    @SerializedName("pressure")
    int pressure;

    @SerializedName("humidity")
    int humidity;

    public List<WeatherDescription> getWeatherDescription() {
        return weatherDescriptionList;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public long getDate() {
        return date;
    }

    public int getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }
}
