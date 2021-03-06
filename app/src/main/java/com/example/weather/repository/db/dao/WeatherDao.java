package com.example.weather.repository.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.weather.repository.db.entity.WeatherEntity;
import java.util.List;

@Dao
public interface WeatherDao {

    @Query("SELECT * FROM " + WeatherEntity.TABLE_NAME + " WHERE " + WeatherEntity.COLUMN_DATE_TEXT + " >= :currentTimeMs" + " LIMIT 7")
    LiveData<List<WeatherEntity>> loadWeatherForecast(long currentTimeMs);

    @Query("SELECT * FROM " + WeatherEntity.TABLE_NAME + " WHERE " + WeatherEntity.COLUMN_ID + " = :id")
    LiveData<WeatherEntity> loadForecastById(long id);

    @Query("SELECT COUNT(*) FROM " + WeatherEntity.TABLE_NAME)
    int count();

    /**
     * Inserts a weatherEntity into the table.
     *
     * @param weatherEntity A new weather.F
     * @return The row ID of the newly inserted weatherEntity.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(WeatherEntity weatherEntity);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<WeatherEntity> weatherEntities);

    /**
     * Update the weather. The weather is identified by the row ID.
     * @param WeatherEntity The weather to update.
     * @return A number of rows updated. This should always be {@code 1}.
     */
    @Update
    int update(WeatherEntity WeatherEntity);

    @Query("DELETE FROM " + WeatherEntity.TABLE_NAME)
    int deleteAllWeatherEntries();

    @Query("DELETE FROM " + WeatherEntity.TABLE_NAME + " WHERE " + WeatherEntity.COLUMN_ID + " = :id")
    int deleteById(long id);

}
