package com.example.weather;

import com.bumptech.glide.Glide;
import com.example.weather.data.WeatherContract;
import com.example.weather.data.WeatherContract.WeatherEntry;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = DetailFragment.class.getSimpleName();
	private String mLocation;
	private String mForecast;
	
	ShareActionProvider mShareActionProvider;
	
	public static final String DATE_KEY = "forecast_date";
	
	public static final String ActionBarMenu_Key = "actionbar_Menu";
	
	private static final String LOCATION_KEY = "location";
	
	private static final String FORECAST_SHARE_HASHTAG = "#SunRiseApp";
	
	private static final int DETAIL_LOADER = 0; 
	
	private static final String [] FORECAST_COLUMNS =  {  
															WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
															WeatherEntry.COLUMN_DATETEXT,
															WeatherEntry.COLUMN_SHORT_DESC,
															WeatherEntry.COLUMN_MAX_TEMP,
															WeatherEntry.COLUMN_MIN_TEMP,
															WeatherEntry.COLUMN_HUMIDITY,
															WeatherEntry.COLUMN_PRESSURE,
															WeatherEntry.COLUMN_WIND_SPEED,
															WeatherEntry.COLUMN_DEGREES,
															WeatherEntry.COLUMN_WEATHER_ID,
															WeatherContract.LocationEntry.COLUMN_LOCATION_SETTINGS
															
															};
	
	private static final String LOG_TAG = DetailFragment.class.getSimpleName();
	
	private ImageView mIconView;
	private TextView mDateView;
	private TextView mDayView;
	private TextView mDescriptionView;
	private TextView mHighTempView;
	private TextView mLowTempView;
	private TextView mWindView;
	private TextView mPressureView;
	private TextView mHumidityView;
	
	public DetailFragment() {
		super();
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
		
		mIconView = (ImageView)rootView.findViewById(R.id.icon_detail);
		mDayView = (TextView)rootView.findViewById(R.id.detail_day_textview);
		mDateView = (TextView)rootView.findViewById(R.id.detail_date_textview);
		mDescriptionView = (TextView)rootView.findViewById(R.id.detail_forecast_textview);
		mHighTempView = (TextView)rootView.findViewById(R.id.detail_high_textview);
		mLowTempView = (TextView)rootView.findViewById(R.id.detail_low_textview);
		mWindView = (TextView)rootView.findViewById(R.id.detail_wind_textview);
		mPressureView = (TextView)rootView.findViewById(R.id.detail_pressure_textview);
		mHumidityView = (TextView)rootView.findViewById(R.id.detail_humidity_textview);
		
		 
		return rootView;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		Bundle arguments = getArguments();
		
		if( arguments != null && arguments.containsKey(DetailActivity.DATE_KEY )
				
				&& mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
			
			getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
		}
		
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		if(savedInstanceState != null) {
			
			mLocation = savedInstanceState.getString(LOCATION_KEY);
		}
		
		Bundle arguments = getArguments();
		
		if(arguments != null && arguments.containsKey(DetailActivity.DATE_KEY)) {
			
			getLoaderManager().initLoader(DETAIL_LOADER, null, this);
			
		}
		
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);

		Bundle bundle = getArguments();
		boolean isTwoPane = bundle.getBoolean(ActionBarMenu_Key);
		inflater.inflate(R.menu.detaila_activity_menu, menu);

		// Inflate the menu; this adds items to the action bar if it is present.
		MenuItem menuItem = menu.findItem(R.id.shareWeatherInfo);
		MenuItem menuItem2 = menu.findItem(R.id.detailSettings);

		if (isTwoPane) {
			menuItem2.setVisible(false);
		}
		mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

		if (mForecast != null) {
			//mShareActionProvider.setShareIntent(createShareForecastIntent());
		}
	}
	
	private Intent createShareForecastIntent() {
		
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
		shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);
		return shareIntent;
	}
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putString(LOCATION_KEY, mLocation);
		
		super.onSaveInstanceState(outState);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId() == R.id.detailSettings)
		{
				 Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
				 startActivity(settingsIntent);
				 return true;
		}
		/*else if(item.getItemId() == R.id.thirdActivity) {
			
			Intent thirdActivity = new Intent(getActivity(), ContentProviderExample.class);
			startActivity(thirdActivity);
			return true;
		}
		else if(item.getItemId() == R.id.startService) {
			
			Intent serviceIntent = new Intent(getActivity(), MyService.class);
			getActivity().startService(serviceIntent);
		}*/
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		
		String dateStr = getArguments().getString(DetailActivity.DATE_KEY);
		
		String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";
		
		mLocation = Utility.getPreferredLocation(getActivity());
		
		Uri weatherLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(mLocation, dateStr);
		
		CursorLoader cursorLoader = new CursorLoader(
														getActivity(),
														weatherLocationUri, 
														FORECAST_COLUMNS, 
														null, 
														null, 
														sortOrder
														
													);
		
		return cursorLoader;
	}


	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
		Log.d(TAG,"onLoadFinish");

		if (!data.moveToFirst()) { return; }

		int weatherId = data.getInt(data.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID));

//		 mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

		Glide.with(getActivity())
				.load(Utility.getArtUrlForWeatherCondition(getActivity(),weatherId))
				//.error(Utility.getArtResourceForWeatherCondition(weatherId))
				//.crossFade()
				.into(mIconView);

		String date = data.getString(data.getColumnIndex(WeatherEntry.COLUMN_DATETEXT));

		String friendlyDayText = Utility.getDayName(getActivity(), date);
		String dateText = Utility.getFormattedMonthDay(getActivity(), date);

		mDateView.setText(dateText);
		mDayView.setText(friendlyDayText);

		String weatherDescription = data.getString(data.getColumnIndex(WeatherEntry.COLUMN_SHORT_DESC));
		mDescriptionView.setText(weatherDescription);

		mIconView.setContentDescription(weatherDescription);

		boolean isMetric = Utility.isMetric(getActivity());

		String high = Utility.formatTemperature(getActivity(),data.getDouble(data.getColumnIndex(WeatherEntry.COLUMN_MAX_TEMP)), isMetric);

		mHighTempView.setText(high);

		String low = Utility.formatTemperature(getActivity(),
				data.getDouble(data.getColumnIndex(WeatherEntry.COLUMN_MIN_TEMP)), isMetric);
		mLowTempView.setText(low);

		int humidity = data.getInt(data.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY));
		mHumidityView.setText("Humidity: " + humidity+"%");

		float windDegrees = data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_DEGREES));

		float windSpeed = data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_WIND_SPEED));
		mWindView.setText(Utility.getFormattedWind(getActivity(), windSpeed, windDegrees));

		double pressure = data.getDouble(data.getColumnIndex(WeatherEntry.COLUMN_PRESSURE));
		mPressureView.setText("Pressure: " +pressure + " hpa");
		// We still need this for the share intent
		mForecast = String.format("%s - %s - %s/%s", dateText, weatherDescription, high, low);
		Log.v(LOG_TAG, "Forecast String: " + mForecast);

		// If onCreateOptionsMenu has already happened, we need to update the share intent now.
		if (mShareActionProvider != null) {

			//mShareActionProvider.setShareIntent(createShareForecastIntent());

		}
	}
	
}