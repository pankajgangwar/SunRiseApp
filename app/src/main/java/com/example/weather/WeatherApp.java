package com.example.weather;

import android.app.Application;
import android.content.Context;

import com.example.weather.repository.db.WeatherDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
