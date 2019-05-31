package com.example.weather;

import com.example.weather.data.WeatherContract;
import com.example.weather.data.WeatherContract.LocationEntry;
import com.example.weather.data.WeatherContract.WeatherEntry;

import java.util.Date;

import com.example.weather.service.SunRiseService;
import com.example.weather.sync.SunRiseSyncAdapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.w3c.dom.Text;

public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,SharedPreferences.OnSharedPreferenceChangeListener {

	private String mLocation;
	
	private static final int FORECAST_LOADER = 0; 
	
//	SimpleCursorAdapter mForecastAdapter;
	
	ForecastAdapter mForecastAdapter;
	
	private final String LOG_TAG = ForecastFragment.class.getSimpleName(); 
	
	SharedPreferences sharedPreferences;
	
	String location;
	
	SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if(key.equals(getString(R.string.pref_location_status_key))){
            updateEmptyView();
        }
    }

    /**
	* A callback interface that all activities containing this fragment must
	* implement. This mechanism allows activities to be notified of item
	* selections.
	*/ 
	public interface Callback {

		 /**
		* Callback for when an item has been selected.
		*/ 
		public void onItemSelected(String date);
		
	}

	
	private static final String[] FORECAST_COLUMNS = { 	WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
															WeatherEntry.COLUMN_DATETEXT,
															WeatherEntry.COLUMN_SHORT_DESC,
															WeatherEntry.COLUMN_MAX_TEMP,
															WeatherEntry.COLUMN_MIN_TEMP,
															LocationEntry.COLUMN_LOCATION_SETTINGS,
															WeatherEntry.COLUMN_WEATHER_ID,
															 LocationEntry.COLUMN_COORD_LAT,
															 LocationEntry.COLUMN_COORD_LONG 
														};

	public static final int COL_WEATHER_ID = 0;
	public static final int COL_WEATHER_DATE = 1;
	public static final int COL_WEATHER_DESC = 2;
	public static final int COL_WEATHER_MAX_TEMP = 3;
	public static final int COL_WEATHER_MIN_TEMP = 4;
	public static final int COL_LOCATION_SETTING = 5;
	 public static final int COL_WEATHER_CONDITION_ID = 6;
	 public static final int COL_COORD_LAT = 7;
	 public static final int COL_COORD_LONG = 8; 
	 
	 ListView mListView;
	 int previousSelectedPosition;
	 private boolean mUseTodayLayout;
	 private static final String SELECTED_KEY = "position";
    TextView emptyView;
	 
	public ForecastFragment() {
		super();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
		
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		 
	}
	
	public void setUseTodayLayout(boolean useTodayLayout) {
		
		mUseTodayLayout = useTodayLayout;
		
		if(mForecastAdapter != null) {
			mForecastAdapter.setUseTodayLayout(mUseTodayLayout);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		 // The SimpleCursorAdapter will take data from the database through the
		// Loader and use it to populate the ListView it's attached to.
		
		
		
		/*mForecastAdapter = new SimpleCursorAdapter(
		getActivity(),
		R.layout.list_item_forecast,
		null,
		// the column names to use to fill the textviews
		new String[]{WeatherEntry.COLUMN_DATETEXT,
		WeatherEntry.COLUMN_SHORT_DESC,
		WeatherEntry.COLUMN_MAX_TEMP,
		WeatherEntry.COLUMN_MIN_TEMP
		},
		// the textviews to fill with the data pulled from the columns above
		new int[]{R.id.list_item_date_textview,
		R.id.list_item_forecast_textview,
		R.id.list_item_high_textview,
		R.id.list_item_low_textview
		},
		0
		);*/
		
		mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);
		mForecastAdapter.setUseTodayLayout(mUseTodayLayout);
		/*mForecastAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				
			boolean isMetric = Utility.isMetric(getActivity());
			
			switch (columnIndex) {
			
			case COL_WEATHER_MAX_TEMP:
			case COL_WEATHER_MIN_TEMP: {
									// we have to do some formatting and possibly a conversion
									((TextView) view).setText(Utility.formatTemperature(
									cursor.getDouble(columnIndex), isMetric));
									
									return true;
									}
			
			case COL_WEATHER_DATE: {
									String dateString = cursor.getString(columnIndex);
									TextView dateView = (TextView) view;
									dateView.setText(Utility.formatDate(dateString));
									
									return true;
									}
			
			default: 
				
									}
			return false;
			}
			});*/
		
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);

		mListView = (ListView)rootView.findViewById(R.id.listview_forecast);

        emptyView = (TextView)rootView.findViewById(R.id.emptyTextView);
		
		mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.activity_main_swipe_refresh_layout);
		
		mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);
		
		mSwipeRefreshLayout.setColorSchemeResources(R.color.app_primary_color, R.color.green_color, R.color.blue_color, R.color.red_color);
		
		mListView.setAdapter(mForecastAdapter);

        mListView.setEmptyView(emptyView);
		
		if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
			
			previousSelectedPosition = savedInstanceState.getInt(SELECTED_KEY);
		} else {
			previousSelectedPosition = 0;
		}
		
		
		mListView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Cursor cursor = mForecastAdapter.getCursor();
				
				if (cursor != null && cursor.moveToPosition(position)) {
				((Callback)getActivity()).onItemSelected(cursor.getString(COL_WEATHER_DATE));
				
				}
				
				previousSelectedPosition = position;
			}
			
		});
		
		return rootView;
	}
	
	public void onSaveInstanceState(Bundle arguments) {
		
		if(previousSelectedPosition != ListView.INVALID_POSITION) {
			arguments.putInt(SELECTED_KEY, previousSelectedPosition);
		}
		
		super.onSaveInstanceState(arguments);
	};
	
	SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener()
	{
		
		@Override
		public void onRefresh() {
			refreshWeatherForeCast();
			
		}
	};
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		getLoaderManager().initLoader(FORECAST_LOADER, null, this);
		
		super.onActivityCreated(savedInstanceState);
		
	}
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main_activity_menu, menu);
		
	}
	

	@Override
	public void onResume() {
		// TODO Auto-generated method stub

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(this);
        super.onResume();

		if(mLocation != null && !Utility.getPreferredLocation(getActivity()).equals(mLocation)){
			getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
		}
	}

    @Override
    public void onPause() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        pref.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    private void refreshWeatherForeCast()
	{
		new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                updateWeatherForeCast();

            }
        }, 5000);
		
	}
	
	
	private void updateWeatherForeCast() 
	{
		mSwipeRefreshLayout.setRefreshing(false);
		
		location = Utility.getPreferredLocation(getActivity());
		
		FetchWeatherTask fetchWeatherTask = new FetchWeatherTask(getActivity());
		fetchWeatherTask.execute(location);
		
		AlarmManager am = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
		
		Intent alaramIntent = new Intent(getActivity(), SunRiseService.AlarmReceiver.class);
		alaramIntent.putExtra(SunRiseService.LOCATION_QUERY_EXTRA, location);
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alaramIntent, PendingIntent.FLAG_ONE_SHOT);
		
		am.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + 5000, pendingIntent);
		
		Intent serviceIntent = new Intent(getActivity(), SunRiseService.class);
		serviceIntent.putExtra(SunRiseService.LOCATION_QUERY_EXTRA, location);
		getActivity().startService(serviceIntent);
		
		SunRiseSyncAdapter.syncImmediately(getActivity());
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int id = item.getItemId();
		
		switch (id) {
		case R.id.showLocation :
			
			openPreferredLocationOnMap();
			return true;
			
		case R.id.settings:
			
			 Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
			 startActivity(settingsIntent);
			 return true;
			 
		/*case R.id.showEffects: 
			
			 Intent animationIntent = new Intent(getActivity(), AnimationActivity.class);
			 startActivity(animationIntent);
			
			return true;*/
			
		default:
			return super.onOptionsItemSelected(item);
		
		}
		
	}
	
	private void openPreferredLocationOnMap()
	{
		 // Using the URI scheme for showing a location found on a map. This super-handy
		// intent can is detailed in the "Common Intents" page of Android's developer site:
		// http://developer.android.com/guide/components/intents-common.html#Maps 
		
		if(mForecastAdapter != null) {
		
			Cursor cursor = mForecastAdapter.getCursor();
			if(cursor != null) {
				
				cursor.moveToPosition(0);
				
				String posLat = cursor.getString(COL_COORD_LAT);
				String posLng = cursor.getString(COL_COORD_LONG);
				
				Uri geoLocation = Uri.parse("geo:" + posLat+"," + posLng);
				
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(geoLocation);
			
		/*Uri geoLocation = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q", location).build();
		
		Intent locationIntent = new Intent(Intent.ACTION_VIEW);
		locationIntent.setData(geoLocation);*/
		
		if(intent.resolveActivity(getActivity().getPackageManager()) != null)
		{
			startActivity(intent);
		}
		else
		{
			Log.d(LOG_TAG, "Couldn't Call " + location + ", no activity found to launch");
		}
		
			}
		
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		// This is called when a new Loader needs to be created. This
		// fragment only uses one loader, so we don't care about checking the id.
		// To only show current and future dates, get the String representation for today,
		// and filter the query to return weather only for dates after or including today.
		// Only return data after today.
		String startDate = WeatherContract.getDbDateString(new Date());
		
		// Sort order: Ascending, by date.
		String sortOrder = WeatherEntry.COLUMN_DATETEXT + " ASC";
		
		mLocation = Utility.getPreferredLocation(getActivity());
		Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithStartDate(
		mLocation, startDate);
		
		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		
		CursorLoader cursorLoader = new CursorLoader(
				getActivity(),
				weatherForLocationUri,
				FORECAST_COLUMNS,
				null,
				null,
				sortOrder
				);
		
		
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
		mForecastAdapter.swapCursor(data);
		if(previousSelectedPosition != ListView.INVALID_POSITION) {

			mListView.setSelection(previousSelectedPosition);
		}

		updateEmptyView();
	}


    private void updateEmptyView(){
        if(mForecastAdapter.getCount() == 0){

            TextView textView = (TextView)getView().findViewById(R.id.emptyTextView);
            if(textView != null){
                int message = R.string.empty_forecast_list;
                @SunRiseSyncAdapter.LocationStatus int location = Utility.getLocationStatus(getActivity());
                switch (location){
                    case SunRiseSyncAdapter.LOCATION_STATUS_SERVER_DOWN:
                        message = R.string.empty_forecast_list_server_down;
                        break;
                    case SunRiseSyncAdapter.LOCATION_STATUS_SERVER_INVALID:
                        message = R.string.empty_forecast_list_server_error;
                        break;
                    case SunRiseSyncAdapter.LOCATION_STATUS_INVALID:
                        message  = R.string.empty_forecast_list_location_invalid;
                        break;
                    default:
                        if (!Utility.getNetworkStatus(getActivity())) {
                            emptyView.append("The network is not connected to fetch weather data");
                        }
                }
                textView.setText(message);
            }

            }
    }

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		mForecastAdapter.swapCursor(null);
	}

}
