package iriemo.bangaloreweather;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

public class DetailActivity extends AppCompatActivity
{
	private static final String LOG_TAG = DetailActivity.class.getSimpleName();
	
	 public static final String DATE_KEY = "forecast_date";
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_detail);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDefaultDisplayHomeAsUpEnabled(true);
		
		if(savedInstanceState == null) {
			
			String date = getIntent().getStringExtra(DATE_KEY);
			
			Bundle arguments = new Bundle();
			arguments.putString(DetailActivity.DATE_KEY, date);
			
			DetailFragment fragment = new DetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().add(R.id.weather_detail_container, fragment).commit();
		}
		

		/*Intent intent = getIntent();
		if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT))
		{
			String description = getIntent().getStringExtra(Intent.EXTRA_TEXT);
		
			descriptionTextView = (TextView)findViewById(R.id.description);
			
			descriptionTextView.setText(description);
		}*/
		
		Log.d(LOG_TAG, "onCreate Called");
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		Log.d(LOG_TAG, "onStart Called");
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.d(LOG_TAG, "onRestart Called");
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(LOG_TAG, "onResume Called");
	}
	
	 @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(LOG_TAG, "onPause Called");
	}
	 
	 @Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d(LOG_TAG, "onStop Called");
	}
	 
	 @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(LOG_TAG, "onDestroy Called");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "onCreateOptionsMenu Called");
		return true;
	}
	
	
	ServiceConnection serviceConnection = new ServiceConnection()
	{
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
		}
	};
	

}
