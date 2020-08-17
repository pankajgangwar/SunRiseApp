package com.example.weather.repository.db;

import com.example.weather.repository.db.entity.WeatherEntity;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class that holds values to be used for testing.
 */
public class TestWeatherData {

    static final WeatherEntity WEATHER_ENTITY = new WeatherEntity(1,"2019-06-01",
            "Hot and Humid",35.2,42.5,"Bangalore");

    static final WeatherEntity WEATHER_ENTITY2 = new WeatherEntity(2,"2019-06-02",
            "Windy",28.2,35.5,"Bangalore");

    static final List<WeatherEntity> WEATHER_ENTITIES = Arrays.asList(WEATHER_ENTITY, WEATHER_ENTITY2);

}
