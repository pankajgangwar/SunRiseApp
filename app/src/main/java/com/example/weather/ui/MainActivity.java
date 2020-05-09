package com.example.weather.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.weather.FetchWeatherWorker;
import com.example.weather.R;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fetchWeatherWithWorker();
    }

    private void fetchWeatherWithWorker(){
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest saveRequest =
                new PeriodicWorkRequest.Builder(FetchWeatherWorker.class, 12, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .build();
        WorkManager.getInstance().enqueue(saveRequest);
    }
}
