package com.example.weather;

import android.app.Application;
import android.content.Context;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.weather.repository.db.WeatherDatabase;
import com.example.weather.repository.remotedata.FetchWeatherWorker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WeatherApp extends Application {
    private static Context mContext;
    public WeatherDatabase getDatabase() {
        mContext = this;
        return WeatherDatabase.getInstance(this);
    }

    public ExecutorService getExecutorService(){
        return Executors.newFixedThreadPool(4);
    }

    public DataRepository getRepository() {
        return DataRepository.getInstance(getDatabase());
    }

    public static Context getAppContext(){
        return mContext ;
    }

}
