package iriemo.bangaloreweather.service;

import iriemo.bangaloreweather.data.WeatherContract;
import iriemo.bangaloreweather.data.WeatherContract.LocationEntry;
import iriemo.bangaloreweather.data.WeatherContract.WeatherEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class SunRiseService extends IntentService {

	private static final String LOG_TAG = SunRiseService.class.getSimpleName();
	public  static final String LOCATION_QUERY_EXTRA = "lqe";
	
	public SunRiseService() {
		super("SunRiseService");
		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		
		String locationQuery = intent.getExtras().getString(LOCATION_QUERY_EXTRA);
		
		String format = "json";
		int numdays = 14;
		String units = "metric";
		
		// These two need to be declared outside the try/catch
		// so that they can be closed in the finally block.
		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;
		// Will contain the raw JSON response as a string.
		String forecastJsonStr = null;
		try {
			
		
		// Construct the URL for the OpenWeatherMap query
		// Possible parameters are avaiable at OWM's forecast API page, at
		// http://openweathermap.org/API#forecast
			
			final String WEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";// q=560034&mode=json&units=metric&cnt=7";
			final String QUERY_PARAM = "q";
			final String FORMAT_PARAM = "mode";
			final String UNITS_PARAM = "units";
			final String DAYS_PARAM = "cnt";
			
			Uri builtUri = Uri.parse(WEATHER_BASE_URL).buildUpon()
					.appendQueryParameter(QUERY_PARAM, locationQuery)
					.appendQueryParameter(FORMAT_PARAM, format)
					.appendQueryParameter(UNITS_PARAM, units)
					.appendQueryParameter(DAYS_PARAM, Integer.toString(numdays))
					.build();
			
		URL url = new URL(builtUri.toString());
		
		Log.v(LOG_TAG, builtUri.toString());
		
		// Create the request to OpenWeatherMap, and open the connection
		urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestMethod("GET");
		urlConnection.connect();
		// Read the input stream into a String
		InputStream inputStream = urlConnection.getInputStream();
		
		StringBuffer buffer = new StringBuffer();
		
		if (inputStream == null) {
		// Nothing to do.
		forecastJsonStr = null;
		}
		
		reader = new BufferedReader(new InputStreamReader(inputStream));
		
		String line;
		
		while ((line = reader.readLine()) != null) {
		// Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
		// But it does make debugging a *lot* easier if you print out the completed
		// buffer for debugging.
		buffer.append(line + "\n");
		}
		 
		if (buffer.length() == 0) {
		// Stream was empty. No point in parsing.
		forecastJsonStr = null;
		}
		
		forecastJsonStr = buffer.toString();
		}
		
		catch (IOException e) 
		{
			Log.e(LOG_TAG, "Error ", e);
			// If the code didn't successfully get the weather data, there's no point in attempting
			// to parse it.
			forecastJsonStr = null;
		}
		finally
		{
			if (urlConnection != null) 
			{
				urlConnection.disconnect();
			}
			
			if (reader != null)
			{
				try 
				{
					reader.close();
				} catch (final IOException e) 
				{
					Log.e(LOG_TAG, "Error closing stream", e);
				}
			}
		
		}
		
		try{
			getWeatherDataFromJson(forecastJsonStr, numdays, locationQuery);
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}
	
	
	 /**
		* Take the String representing the complete forecast in JSON Format and
		* pull out the data we need to construct the Strings needed for the wireframes.
		*
		* Fortunately parsing is easy: constructor takes the JSON string and converts it
		* into an Object hierarchy for us.
		*/
		private void getWeatherDataFromJson(String forecastJsonStr, int numDays,String locationSettings)
		throws JSONException {
		 
		// Location Information	
		final String OWN_COORD = "coord";
		final String OWN_COORD_LAT = "lat";
		final String OWN_COORD_LON = "lon";
		final String OWN_CITY = "city";
		final String OWN_CITY_NAME = "name";

		// These are the names of the JSON objects that need to be extracted.
		final String OWM_LIST = "list";
		final String OWM_WEATHER = "weather";
		final String OWM_TEMPERATURE = "temp";
		final String OWM_MAX = "max";
		final String OWM_MIN = "min";
		final String OWM_DESCRIPTION = "main";
		
		final String OWM_DATETIME = "dt";
		final String OWN_PRESSURE = "pressure";
		final String OWN_HUMIDITY = "humidity";
		final String OWN_WINDSPEED = "speed";
		final String OWN_WIND_DIRECTION = "deg";
		
		final String OWN_WEATHER_ID = "id";
			
		JSONObject forecastJson = new JSONObject(forecastJsonStr);
		
		JSONObject cityObject = forecastJson.getJSONObject(OWN_CITY);
		String cityName = cityObject.getString(OWN_CITY_NAME);
		
		JSONObject coordObject = cityObject.getJSONObject(OWN_COORD);
		double lat = coordObject.getDouble(OWN_COORD_LAT);
		double lon = coordObject.getDouble(OWN_COORD_LON);
		
		Log.v(LOG_TAG, cityName + ", with coord: " + lat + " " + lon);
		 
		JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
		 
		long locationID = addLocation(locationSettings, cityName, lat, lon);
		
		Vector<ContentValues> contentValuesVector = new Vector<ContentValues>(weatherArray.length());
		
		for(int i = 0; i < weatherArray.length(); i++) {
			
			// For now, using the format "Day, description, hi/low"
			long dateTime;
			double pressure;
			int humidity;
			double windSpeed;
			double windDirection;
			
			double high;
			double low;
			
			String description;
			int  weatherId;
			 
			// Get the JSON object representing the day
			JSONObject dayForecast = weatherArray.getJSONObject(i);
			 
			// The date/time is returned as a long. We need to convert that
			// into something human-readable, since most people won't read "1400356800" as
			// "this saturday".
			dateTime = dayForecast.getLong(OWM_DATETIME);
			
			pressure = dayForecast.getDouble(OWN_PRESSURE);
			
			humidity = dayForecast.getInt(OWN_HUMIDITY);
			windSpeed = dayForecast.getDouble(OWN_WINDSPEED);
			windDirection = dayForecast.getDouble(OWN_WIND_DIRECTION);
			
			// description is in a child array called "weather", which is 1 element long.
			JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
			description = weatherObject.getString(OWM_DESCRIPTION);
			weatherId = weatherObject.getInt(OWN_WEATHER_ID);
			
			Log.i(LOG_TAG, "WEATHER ID:" + weatherId);
			 
			// Temperatures are in a child object called "temp". Try not to name variables
			// "temp" when working with temperature. It confuses everybody.
			JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
			 high = temperatureObject.getDouble(OWM_MAX);
			 low = temperatureObject.getDouble(OWM_MIN);
			 
			 ContentValues weatherValues = new ContentValues();
			 
			 weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationID);
			 weatherValues.put(WeatherEntry.COLUMN_DATETEXT, WeatherContract.getDbDateString(new java.util.Date(dateTime * 1000L)));
			 weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, humidity);
			 weatherValues.put(WeatherEntry.COLUMN_PRESSURE, pressure);
			 weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
			 weatherValues.put(WeatherEntry.COLUMN_DEGREES, windDirection);
			 weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, high);
			 weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, low);
			 weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, description);
			 weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, weatherId);
			 
			 contentValuesVector.add(weatherValues);
			 
			 if(contentValuesVector.size() > 0){
				 ContentValues[] cvArray = new ContentValues[contentValuesVector.size()];
				 contentValuesVector.toArray(cvArray);
				 getContentResolver().bulkInsert(WeatherEntry.CONTENT_URI, cvArray);
				 
			 }
//			highAndLow = formatHighLows(high, low);
			
				}
		
		}
		
		 /**
		* Helper method to handle insertion of a new location in the weather database.
		*
		* @param locationSettings The location string used to request updates from the server.
		* @param cityName A human-readable city name, e.g "Mountain View"
		* @param lat the latitude of the city
		* @param lon the longitude of the city
		* @return the row ID of the added location.
		*/ 
		private long addLocation(String locationSettings, String cityName,double lat, double lon){
			Log.v(LOG_TAG, "inserting " + cityName + ", with coord: " + lat + ", " + lon);
			
			Cursor cursor = getContentResolver().query(LocationEntry.CONTENT_URI,
					                                   new String[]{LocationEntry._ID},
					                                   LocationEntry.COLUMN_LOCATION_SETTINGS + " = ?",
					                                   new String[]{locationSettings},
					                                   null);
			
			if(cursor.moveToFirst()){
				Log.v(LOG_TAG, "Found it in the database");
				int locationIdIndex = cursor.getColumnIndex(LocationEntry._ID);
				return cursor.getLong(locationIdIndex);
			}
			else{
				Log.v(LOG_TAG, "Didn't find it in the database, inserting now");
				ContentValues locationValues = new ContentValues();
				locationValues.put(LocationEntry.COLUMN_LOCATION_SETTINGS, locationSettings);
				locationValues.put(LocationEntry.COLUMN_CITY_NAME, cityName);
				locationValues.put(LocationEntry.COLUMN_COORD_LAT, lat);
				locationValues.put(LocationEntry.COLUMN_COORD_LONG, lon);
				
				Uri locationInsertUri = getContentResolver().insert(LocationEntry.CONTENT_URI, locationValues);
				
				return ContentUris.parseId(locationInsertUri);
			}
			
		}
		
		public static class AlarmReceiver extends BroadcastReceiver {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				Intent sendIntent = new Intent(context, SunRiseService.class);
				sendIntent.putExtra(LOCATION_QUERY_EXTRA, intent.getExtras().getString(LOCATION_QUERY_EXTRA));
				context.startService(sendIntent);
			}
			
		}

}
