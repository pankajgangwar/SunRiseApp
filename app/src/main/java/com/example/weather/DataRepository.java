package com.example.weather;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.weather.repository.db.WeatherDatabase;
import com.example.weather.repository.db.entity.WeatherEntity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DataRepository {

    private static DataRepository sInstance;

    private final WeatherDatabase mDatabase;

    private MediatorLiveData<List<WeatherEntity>> mObservableWeather;

    private DataRepository(final WeatherDatabase database) {
        mDatabase = database;
        mObservableWeather = new MediatorLiveData<>();
        Date dt = new Date();
        Calendar c =  Calendar.getInstance();
        c.setTime(dt);
        c.set(Calendar.HOUR_OF_DAY, 6);
        c.add(Calendar.MINUTE, 30);
        long currentTimeMs = c.getTimeInMillis() / 1000;
        Log.d("Time","time from system clock " + currentTimeMs);
        mObservableWeather.addSource(mDatabase.weather().loadWeatherForecast(currentTimeMs),
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
