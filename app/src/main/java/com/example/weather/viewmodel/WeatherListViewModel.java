package com.example.weather.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import com.example.weather.DataRepository;
import com.example.weather.WeatherApp;
import com.example.weather.repository.db.entity.WeatherEntity;

import java.util.List;

public class WeatherListViewModel extends AndroidViewModel {

    private final DataRepository mRepository;

    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private final MediatorLiveData<List<WeatherEntity>> mObservableForecast;

    public WeatherListViewModel(@NonNull Application application) {
        super(application);

        mObservableForecast = new MediatorLiveData<>();
        mObservableForecast.setValue(null);

        mRepository = ((WeatherApp) application).getRepository();
        LiveData<List<WeatherEntity>> weather_forecast = mRepository.getForecast();

        mObservableForecast.addSource(weather_forecast, mObservableForecast::setValue);
    }

    public LiveData<List<WeatherEntity>> getWeatherForecast() {
        return mObservableForecast;
    }
}
