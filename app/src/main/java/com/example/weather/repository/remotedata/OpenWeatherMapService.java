package com.example.weather.repository.remotedata;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Pankaj Kumar on 17/August/2020
 */
interface OpenWeatherMapService {
    @GET("/data/2.5/forecast/daily")
    Call<WeatherDataMain> listForecast(@Query("q") String city,
                                       @Query("cnt") int cnt,
                                       @Query("appid") String key);
}
