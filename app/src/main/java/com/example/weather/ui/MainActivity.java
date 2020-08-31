package com.example.weather.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.example.weather.repository.remotedata.FetchWeatherWorker;
import com.example.weather.R;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if(!pref.getBoolean(getString(R.string.pref_key_work_schedule), false)){
            schedulePeriodicWork();
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(getString(R.string.pref_key_work_schedule), true);
            editor.apply();
        }
    }

    private void schedulePeriodicWork(){
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest saveRequest =
                new PeriodicWorkRequest.Builder(FetchWeatherWorker.class, 12, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .build();

        WorkManager.getInstance(this).enqueue(saveRequest);
    }
}
