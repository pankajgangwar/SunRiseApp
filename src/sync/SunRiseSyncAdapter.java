package sync;

import iriemo.bangaloreweather.MainActivity;
import iriemo.bangaloreweather.R;
import iriemo.bangaloreweather.Utility;
import iriemo.bangaloreweather.data.WeatherContract;
import iriemo.bangaloreweather.data.WeatherContract.LocationEntry;
import iriemo.bangaloreweather.data.WeatherContract.WeatherEntry;
import iriemo.bangaloreweather.service.SunRiseService;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.bumptech.glide.Glide;

public class SunRiseSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String LOG_TAG = SunRiseSyncAdapter.class.getSimpleName();


    public static final int SYNC_INTERVAL = 60 * 180; // 3 hours
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3; // 1 hours

    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int WEATHER_NOTIFICATION_ID = 3004;

    private static final String[] NOTIFY_WEATHER_PROJECTION = new String[]{
            WeatherEntry.COLUMN_WEATHER_ID,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_SHORT_DESC
    };

    // these indices must match the projection
    private static final int INDEX_WEATHER_ID = 0;
    private static final int INDEX_MAX_TEMP = 1;
    private static final int INDEX_MIN_TEMP = 2;
    private static final int INDEX_SHORT_DESC = 3;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LOCATION_STATUS_OK, LOCATION_STATUS_SERVER_DOWN, LOCATION_STATUS_SERVER_INVALID, LOCATION_STATUS_UNKNOWN, LOCATION_STATUS_INVALID})
    public @interface LocationStatus {}

    public static final int LOCATION_STATUS_OK = 0;
    public static final int LOCATION_STATUS_SERVER_DOWN = 1;
    public static final int LOCATION_STATUS_SERVER_INVALID = 2;
    public static final int LOCATION_STATUS_UNKNOWN = 3;
    public static final int LOCATION_STATUS_INVALID = 4;


    static private void setLocationStatus(@LocationStatus int locationStatus,Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(context.getString(R.string.pref_location_status_key),locationStatus).commit();
    }

    public SunRiseSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        // TODO Auto-generated constructor stub

    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
        * Since we've created an account
		*/
        SunRiseSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
		
		/*
		* Without calling setSyncAutomatically, our periodic sync will not be enabled.
		*/
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
		
		/*
		* Finally, let's do a sync to get things started
		*/
        syncImmediately(context);
    }

    public static void initializeAdapter(Context context) {
        getSyncAccount(context);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet. If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(context.getResources().getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {
	 
	/*
	* Add the account and account type, no password or user data
	* If successful, return the Account object, otherwise report an error.
	*/
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
	/*
	* If you don't set android:syncable="true" in
	* in your <provider> element in the manifest,
	* then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
	* here.
	*/

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }


    @Override
    public void onPerformSync(Account account, Bundle bundle, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        // TODO Auto-generated method stub

        Log.v(LOG_TAG, "onPerformSync Called");

        String locationQuery = Utility.getPreferredLocation(getContext());

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
                setLocationStatus(LOCATION_STATUS_SERVER_DOWN,getContext());
            }

            forecastJsonStr = buffer.toString();
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "Error: " + e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            forecastJsonStr = null;
            setLocationStatus(LOCATION_STATUS_SERVER_DOWN,getContext());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error: ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }

        }

        try {
            getWeatherDataFromJson(forecastJsonStr, numdays, locationQuery);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            setLocationStatus(LOCATION_STATUS_SERVER_INVALID,getContext());
            e.printStackTrace();
        }

    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p/>
     * Fortunately parsing is easy: constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getWeatherDataFromJson(String forecastJsonStr, int numDays, String locationSettings)
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
        final String OWM_MESSAGE_CODE = "cod";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);

        if(forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode  = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode){
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    setLocationStatus(LOCATION_STATUS_INVALID,getContext());
                    return;
                default:
                    setLocationStatus(LOCATION_STATUS_SERVER_DOWN,getContext());
                    return;
            }
        }

        JSONObject cityObject = forecastJson.getJSONObject(OWN_CITY);
        String cityName = cityObject.getString(OWN_CITY_NAME);

        JSONObject coordObject = cityObject.getJSONObject(OWN_COORD);
        double lat = coordObject.getDouble(OWN_COORD_LAT);
        double lon = coordObject.getDouble(OWN_COORD_LON);

        Log.v(LOG_TAG, cityName + ", with coord: " + lat + " " + lon);

        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        long locationID = addLocation(locationSettings, cityName, lat, lon);

        Vector<ContentValues> contentValuesVector = new Vector<ContentValues>(weatherArray.length());

        for (int i = 0; i < weatherArray.length(); i++) {

            // For now, using the format "Day, description, hi/low"
            long dateTime;
            double pressure;
            int humidity;
            double windSpeed;
            double windDirection;

            double high;
            double low;

            String description;
            int weatherId;

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

            if (contentValuesVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[contentValuesVector.size()];
                contentValuesVector.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(WeatherEntry.CONTENT_URI, cvArray);

                Calendar cal = Calendar.getInstance(); //Get's a calendar object with the current time.
                cal.add(Calendar.DATE, -1); //Signifies yesterday's date
                String yesterdayDate = WeatherContract.getDbDateString(cal.getTime());
                getContext().getContentResolver().delete(WeatherEntry.CONTENT_URI,
                        WeatherEntry.COLUMN_DATETEXT + " <= ?",
                        new String[]{yesterdayDate});

                notifyWeather();

            }

            setLocationStatus(LOCATION_STATUS_OK,getContext());


//		highAndLow = formatHighLows(high, low);

        }

    }

    /**
     * Helper method to handle insertion of a new location in the weather database.
     *
     * @param locationSettings The location string used to request updates from the server.
     * @param cityName        A human-readable city name, e.g "Mountain View"
     * @param lat             the latitude of the city
     * @param lon             the longitude of the city
     * @return the row ID of the added location.
     */
    private long addLocation(String locationSettings, String cityName, double lat, double lon) {
        Log.v(LOG_TAG, "inserting " + cityName + ", with coord: " + lat + ", " + lon);

        Cursor cursor = getContext().getContentResolver().query(LocationEntry.CONTENT_URI,
                new String[]{LocationEntry._ID},
                LocationEntry.COLUMN_LOCATION_SETTINGS + " = ?",
                new String[]{locationSettings},
                null);

        if (cursor.moveToFirst()) {
            Log.v(LOG_TAG, "Found it in the database");
            int locationIdIndex = cursor.getColumnIndex(LocationEntry._ID);
            return cursor.getLong(locationIdIndex);
        } else {
            Log.v(LOG_TAG, "Didn't find it in the database, inserting now");
            ContentValues locationValues = new ContentValues();
            locationValues.put(LocationEntry.COLUMN_LOCATION_SETTINGS, locationSettings);
            locationValues.put(LocationEntry.COLUMN_CITY_NAME, cityName);
            locationValues.put(LocationEntry.COLUMN_COORD_LAT, lat);
            locationValues.put(LocationEntry.COLUMN_COORD_LONG, lon);

            Uri locationInsertUri = getContext().getContentResolver().insert(LocationEntry.CONTENT_URI, locationValues);

            return ContentUris.parseId(locationInsertUri);
        }

    }

    private void notifyWeather() {
        Context context = getContext();
        //checking the last update and notify if it' the first of the day
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean displayNotifications = Utility.getPreferredNotificationSettings(context);

        if (displayNotifications) {
            String lastNotificationKey = context.getString(R.string.pref_last_notification);
            long lastSync = prefs.getLong(lastNotificationKey, 0);

            if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS) {
                // Last sync was more than 1 day ago, let's send a notification with the weather.
                String locationQuery = Utility.getPreferredLocation(context);

                Uri weatherUri = WeatherEntry.buildWeatherLocationWithDate(locationQuery, WeatherContract.getDbDateString(new Date()));

                // we'll query our contentProvider, as always
                Cursor cursor = context.getContentResolver().query(weatherUri, NOTIFY_WEATHER_PROJECTION, null, null, null);

                if (cursor.moveToFirst()) {
                    int weatherId = cursor.getInt(INDEX_WEATHER_ID);
                    double high = cursor.getDouble(INDEX_MAX_TEMP);
                    double low = cursor.getDouble(INDEX_MIN_TEMP);
                    String desc = cursor.getString(INDEX_SHORT_DESC);

                    int iconId = Utility.getIconResourceForWeatherCondition(weatherId);

                    int artResourceId = Utility.getArtResourceForWeatherCondition(weatherId);
                    String artUrl = Utility.getArtUrlForWeatherCondition(context, weatherId);


                    @SuppressLint("InlinedApi")
                    int largeIconWidth = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? context.getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_width)
                            : context.getResources().getDimensionPixelSize(R.dimen.notification_large_icon_default);

                    @SuppressLint("InlinedApi")
                    int largeIconHeight = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? context.getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_height)
                            : context.getResources().getDimensionPixelSize(R.dimen.notification_large_icon_default);

                    Bitmap largeIcon;
                    try{
                        largeIcon = Glide.with(context)
                                .load(artUrl)
                                .asBitmap()
                                .error(artResourceId)
                                .fitCenter()
                                .into(largeIconWidth,largeIconHeight).get();
                    } catch (InterruptedException | ExecutionException e){
                        largeIcon = BitmapFactory.decodeResource(context.getResources(),artResourceId);
                    }

                    String title = context.getString(R.string.app_name);

                    boolean isMetric = Utility.isMetric(context);

                    // Define the text of the forecast.
                    String contentText = String.format(context.getString(R.string.format_notification),
                            desc,
                            Utility.formatTemperature(context, high, isMetric),
                            Utility.formatTemperature(context, low, isMetric));

                    //build your notification here.
                    // NotificationCompatBuilder is a very convenient way to build backward-compatible
                    // notifications. Just throw in some data.
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getContext())
                                    .setSmallIcon(iconId)
                                    .setContentTitle(title)
                                    .setLargeIcon(largeIcon)
                                    .setContentText(contentText);

                    // Make something interesting happen when the user clicks on the notification.
                    // In this case, opening the app is sufficient.
                    Intent resultIntent = new Intent(context, MainActivity.class);
                    // The stack builder object will contain an artificial back stack for the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager =
                            (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    // WEATHER_NOTIFICATION_ID allows you to update the notification later on.

                    mNotificationManager.notify(WEATHER_NOTIFICATION_ID, mBuilder.build());

                    //refreshing last sync
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong(lastNotificationKey, System.currentTimeMillis());
                    editor.commit();
                }
            }

        }
    }

}
