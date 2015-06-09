package iriemo.bangaloreweather;

import sync.SunRiseSyncAdapter;
import iriemo.bangaloreweather.ForecastFragment.Callback;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements Callback{
	
	private boolean mTwoPane = false;
	
	private final String LOG_TAG = MainActivity.class.getSimpleName(); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		if(findViewById(R.id.weather_detail_container) != null) {
			
			mTwoPane = true;
			
		if(savedInstanceState == null) {

			Bundle args = new Bundle();
			args.putBoolean(DetailFragment.ActionBarMenu_Key, mTwoPane);
			DetailFragment detailFragment = new DetailFragment();
			detailFragment.setArguments(args);
			
			getSupportFragmentManager().beginTransaction().replace(R.id.weather_detail_container, detailFragment).commit();
		}
		
		}
		else {
			mTwoPane = false;
		}
		
		ForecastFragment forecastFragment = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
		forecastFragment.setUseTodayLayout(!mTwoPane);
		
		SunRiseSyncAdapter.initializeAdapter(this);
	}
	
	
	protected void onStart()
	{
		super.onStart();
		
		Log.v(LOG_TAG, "onStart");
	};
	
	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		// TODO Auto-generated method stub
		Log.v(LOG_TAG,"onRetainCustomNonConfigurationInstance");
		
		return super.onRetainCustomNonConfigurationInstance();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		Log.v(LOG_TAG, "onStop");
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		Log.v(LOG_TAG, "onPause");
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		Log.v(LOG_TAG, "onDestroy");
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		
		Log.v(LOG_TAG, "onRestoreInstanceState");
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		
		Log.v(LOG_TAG, "onSaveInstanceState");
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			Toast.makeText(this, "ORIENTATION_LANDSCAPE", Toast.LENGTH_SHORT).show();
		}
		else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			Toast.makeText(this, "ORIENTATION_PORTRAIT", Toast.LENGTH_SHORT).show();
		}
		Log.v(LOG_TAG, "onConfigurationChanged");
	}


	@Override
	public void onItemSelected(String date) {
		// TODO Auto-generated method stub
		if(mTwoPane) {
			
			Bundle args = new Bundle();
			args.putString(DetailActivity.DATE_KEY, date);
			args.putBoolean(DetailFragment.ActionBarMenu_Key, mTwoPane);
			
			DetailFragment fragment = new DetailFragment();
			fragment.setArguments(args);
			
			getSupportFragmentManager().beginTransaction().replace(R.id.weather_detail_container, fragment).commit();
			
			
		} else {
			Intent intent = new Intent(this, DetailActivity.class).putExtra(DetailActivity.DATE_KEY, date);;
			startActivity(intent);
		}
		
	}
	

}
