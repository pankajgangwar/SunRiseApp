package com.example.weather;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.room.Room;

import com.example.weather.db.WeatherDatabase;
import com.example.weather.db.entity.WeatherEntity;

import java.util.List;


public class DataRepository {

    private static DataRepository sInstance;

    private final WeatherDatabase mDatabase;

    private MediatorLiveData<List<WeatherEntity>> mObservableWeather;

    private DataRepository(final WeatherDatabase database) {
        mDatabase = database;
        mObservableWeather = new MediatorLiveData<>();

        mObservableWeather.addSource(mDatabase.weather().loadWeatherForecast(),
                weatherEntities -> {
                    mObservableWeather.postValue(weatherEntities);
                });
    }

    public static DataRepository getInstance(final WeatherDatabase database) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository(database);
                }
            }
        }
        return sInstance;
    }

    public LiveData<List<WeatherEntity>> getForecast() {
        return mObservableWeather;
    }

}