package com.example.weather;

import com.example.weather.ForecastFragment.Callback;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.example.weather.sync.SunRiseSyncAdapter;

public class MainActivity extends AppCompatActivity implements Callback {

    private boolean mTwoPane = false;

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private String mLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (findViewById(R.id.weather_detail_container) != null) {

            mTwoPane = true;

            if (savedInstanceState == null) {

                Bundle args = new Bundle();
                args.putBoolean(DetailFragment.ActionBarMenu_Key, mTwoPane);
                DetailFragment detailFragment = new DetailFragment();
                detailFragment.setArguments(args);

                getSupportFragmentManager().beginTransaction().replace(R.id.weather_detail_container, detailFragment).commit();
            }

        } else {
            mTwoPane = false;
        }

        ForecastFragment forecastFragment = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
        forecastFragment.setUseTodayLayout(!mTwoPane);

        SunRiseSyncAdapter.initializeAdapter(this);
    }



    @Override
    public void onItemSelected(String date) {
        // TODO Auto-generated method stub
        if (mTwoPane) {

            Bundle args = new Bundle();
            args.putString(DetailActivity.DATE_KEY, date);
            args.putBoolean(DetailFragment.ActionBarMenu_Key, mTwoPane);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.weather_detail_container, fragment).commit();

        } else {
            Intent intent = new Intent(this, DetailActivity.class).putExtra(DetailActivity.DATE_KEY, date);
            ;
            startActivity(intent);
        }

    }

}
