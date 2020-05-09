package com.example.weather;

import android.app.Application;
import android.content.Context;

import com.example.weather.db.WeatherDatabase;

public class WeatherApp extends Application {
    private static Context mContext;
    public WeatherDatabase getDatabase() {
        mContext = this;
        return WeatherDatabase.getInstance(this);
    }

    public DataRepository getRepository() {
        return DataRepository.getInstance(getDatabase());
    }

    public static Context getAppContext(){
        return mContext ;
    }
}
