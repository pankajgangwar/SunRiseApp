package com.example.weather.db.entity;

import android.content.ContentValues;
import android.provider.BaseColumns;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.weather.Util;
import com.example.weather.WeatherApp;
import com.example.weather.model.Weather;


@Entity(tableName = WeatherEntity.TABLE_NAME,
        indices = {@Index(value = {WeatherEntity.COLUMN_DATE_TEXT},
                    unique = true)})
public class WeatherEntity implements Weather {

    // WeatherEntity id as returned by API, to identify the icon to be used
    public static final String TABLE_NAME = "weather";

    /** The name of the ID column. */
    public static final String COLUMN_ID = BaseColumns._ID;

    public static final String COLUMN_LOCATION = "location";

    // Date, stored as Text with format yyyy-MM-dd
    public static final String COLUMN_DATE_TEXT = "date";

    // Short description and long description of the weather, as provided by API.
    // e.g "clear" vs "sky is clear".
    public static final String COLUMN_SHORT_DESC = "short_desc";

    // Min and max temperatures for the day (stored as floats)
    public static final String COLUMN_MIN_TEMP = "min";
    public static final String COLUMN_MAX_TEMP = "max";


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = COLUMN_ID)
    public long id;


    @ColumnInfo(name = WeatherEntity.COLUMN_DATE_TEXT)
    public String mDate;

    // Short description and long description of the weather, as provided by API.
    // e.g "clear" vs "sky is clear".
    @ColumnInfo(name = WeatherEntity.COLUMN_SHORT_DESC)
    public String mShortDesc;

    // Min and max temperatures for the day (stored as floats)
    @ColumnInfo(name = WeatherEntity.COLUMN_MIN_TEMP)
    public double mMinTemp;

    @ColumnInfo(name = WeatherEntity.COLUMN_MAX_TEMP)
    public double mMaxTemp;

    @ColumnInfo(name = WeatherEntity.COLUMN_LOCATION)
    public String mLocation;

    public WeatherEntity(){

    }

    @Ignore
    public WeatherEntity(long id, String mDate, String mShortDesc, double mMinTemp, double mMaxTemp, String mLocation) {
        this.id = id;
        this.mDate = mDate;
        this.mShortDesc = mShortDesc;
        this.mMinTemp = mMinTemp;
        this.mMaxTemp = mMaxTemp;
        this.mLocation = mLocation;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getDate() {
        return Util.getFriendlyDayString(WeatherApp.getAppContext(), mDate);
    }

    @Override
    public String getDescription() {
        return mShortDesc;
    }

    @Override
    public String getMaxTemp() {
        return Util.formatTemperature(WeatherApp.getAppContext(),mMaxTemp);
    }

    @Override
    public String getMinTemp() {
        return Util.formatTemperature(WeatherApp.getAppContext(),mMinTemp);
    }

    @Override
    public String getLocation() {
        return mLocation;
    }
}
