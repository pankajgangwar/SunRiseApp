package iriemo.bangaloreweather;

import sync.SunRiseSyncAdapter;
import iriemo.bangaloreweather.ForecastFragment.Callback;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

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

        if (!checkPlayServices()) {

            Toast.makeText(this, "Google Play Service Not available", Toast.LENGTH_LONG).show();

            /*Store Registeration id as null */
            SharedPreferences preferences = getSharedPreferences(getString(R.string.pref_user), MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getString(R.string.gcm_sender_id), null).commit();

        } else {
            if(!getSharedPreferences(getString(R.string.pref_user),MODE_PRIVATE).contains(getString(R.string.gcm_sender_id))){
                getRegIdTask.execute();
            }

        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(LOG_TAG, "This device is not supported");
                finish();
            }
            return false;
        }
        return true;
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

    AsyncTask<Void,Void,String> getRegIdTask = new AsyncTask<Void, Void, String>() {

        @Override
        protected String doInBackground(Void... params) {
            InstanceID instanceID = InstanceID.getInstance(MainActivity.this);
            try {
                String regId = instanceID.getToken(getString(R.string.gcm_sender_id), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                SharedPreferences preferences = getSharedPreferences(getString(R.string.pref_user), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(getString(R.string.gcm_sender_id), regId).commit();
                return regId;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.i(LOG_TAG,"RegId: " + s);
//                Toast.makeText(MainActivity.this,"RegId: " + s,Toast.LENGTH_LONG).show();
        }
    };


}
