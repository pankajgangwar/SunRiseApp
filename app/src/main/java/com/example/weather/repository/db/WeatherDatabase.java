package com.example.weather.repository.db;

import android.content.Context;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.weather.repository.db.dao.WeatherDao;
import com.example.weather.repository.db.entity.WeatherEntity;

@Database(entities = {WeatherEntity.class}, version = 1, exportSchema = false)
public abstract class WeatherDatabase extends RoomDatabase {

    public abstract WeatherDao weather();

    private static WeatherDatabase sInstance;
    private static final Object sLock = new Object();
    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public static WeatherDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        WeatherDatabase.class, "Weather.db")

                        .build();
            }
            return sInstance;
        }
    }

    @VisibleForTesting
    public static void switchToInMemory(Context context) {
        sInstance = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
                WeatherDatabase.class).build();
    }

}
