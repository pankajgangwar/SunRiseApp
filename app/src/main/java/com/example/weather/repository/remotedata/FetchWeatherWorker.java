package com.example.weather.repository.remotedata;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.weather.WeatherApp;
import com.example.weather.repository.db.WeatherDatabase;
import com.example.weather.repository.db.entity.WeatherEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.weather.Util.DATE_FORMAT;


public class FetchWeatherWorker extends Worker {

    private static final String TAG = FetchWeatherWorker.class.getSimpleName();
    final String WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/forecast/";

    private static final String LOG_TAG = FetchWeatherWorker.class.getSimpleName();
    ExecutorService executorService;
    public FetchWeatherWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        executorService = ((WeatherApp) context.getApplicationContext()).getExecutorService();
    }

    @NonNull
    @Override
    public Result doWork() {
        fetchWeatherWithRetrofit();
        return Result.success();
    }

    private void fetchWeatherWithRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WEATHER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // http://api.openweathermap.org/data/2.5/forecast/daily?q=Bangalore&cnt=14&appid=7510b3cb9e39d3c39e1af72d2679fabb

        OpenWeatherMapService service = retrofit.create(OpenWeatherMapService.class);
        final String locationQuery = "Bangalore";
        final int numdays = 14;
        final String ACCESS_KEY = "7510b3cb9e39d3c39e1af72d2679fabb";

        Call<WeatherDataMain> call = service.listForecast(locationQuery, numdays, ACCESS_KEY);
        call.enqueue(new Callback<WeatherDataMain>() {
            @Override
            public void onResponse(Call<WeatherDataMain> call, Response<WeatherDataMain> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG,"Failed response from service");
                    return;
                }
                if(response.body() != null) {
                    City city = response.body().city;
                    Coordinate coordinate = city.getCoordinates();
                    double lat = coordinate.getLat();
                    double lon = coordinate.getLon();

                    List<WeatherEntity> entityList = new ArrayList<>();
                    List<Weather> forecastList = response.body().getWeatherForecastList();
                    for (Weather w :forecastList){
                        /*Log.d(TAG, "---------------------------");
                        Log.d(TAG,"Weather description " + w.getWeatherDescription().get(0).getDetailDescription());
                        Log.d(TAG,"Weather Date " + w.getDate());
                        Log.d(TAG,"Weather Pressure " + w.getPressure());
                        Log.d(TAG,"Weather Humidity " + w.getHumidity());
                        Log.d(TAG,"Weather Temp max:" + w.getTemperature().getMaxTemp() + " min " + w.getTemperature().getMinTemp());*/

                        WeatherEntity entity = new WeatherEntity();

                        Date date = new Date(w.getDate() * 1000L);

                        entity.mDate = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(date);
                        entity.mLocation = city.getCityName();
                        entity.mMaxTemp = getTemperatureInCelsius(w.getTemperature().getMaxTemp());
                        entity.mMinTemp = getTemperatureInCelsius(w.getTemperature().getMinTemp());
                        entity.mShortDesc = w.getWeatherDescription().get(0).getDetailDescription();
                        entityList.add(entity);
                    }

                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            final WeatherDatabase db = WeatherDatabase.getInstance(getApplicationContext());
                            db.runInTransaction(() -> db.weather().insertAll(entityList));
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<WeatherDataMain> call, Throwable t) {
                Log.d(TAG,"Failed " + t.getMessage());
            }
        });
    }

    public double getTemperatureInCelsius(double kelvin){
        return kelvin - 273.15F;
    }

    protected void fetchWeatherForecast() {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;
        final String locationQuery = "Bangalore";
        try {
            // api.openweathermap.org/data/2.5/forecast/daily?q=London&units=&cnt=7
            final String LOCATION_PARAM = "query";
            final String DAYS_PARAM = "forecast_days";
            final String HOURLY_PARAMS = "hourly";

            final String ACCESS_KEY = "7510b3cb9e39d3c39e1af72d2679fabb";
            final int numdays = 10;

            Uri builtUri = Uri.parse(WEATHER_BASE_URL).buildUpon().build();
                    /*.appendQueryParameter(LOCATION_PARAM, locationQuery)
                    .appendQueryParameter(HOURLY_PARAMS, Integer.toString(1))
                    .appendQueryParameter(DAYS_PARAM, Integer.toString(numdays))
                    .build();*/

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
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "Error: " + e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            forecastJsonStr = null;
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
            getWeatherDataFromJson(forecastJsonStr, locationQuery);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (Exception e){
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
    private void getWeatherDataFromJson(String forecastJsonStr,String locationSettings)
            throws JSONException {

        // Location Information
        // Location Information
        final String OWN_COORD_LAT = "lat";
        final String OWN_COORD_LON = "lon";
        final String OWN_CITY_NAME = "name";

        final String OWN_LOCATION = "city";

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWN_FORECAST = "forecastday";

        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX_TEMP = "max";
        final String OWM_MIN_TEMP = "min";

        final String OWM_CONDITION_TEXT = "text";

        final String OWM_CONDITION = "condition";

        final String OWM_DATETIME = "dt";

        final String OWN_DAY = "day";

        final String OWN_WEATHER_ID = "id";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);

        JSONObject locationObject = forecastJson.getJSONObject(OWN_LOCATION);
        String cityName = locationObject.getString(OWN_CITY_NAME);
        JSONObject coordObj = locationObject.getJSONObject("coord");

        double lat = coordObj.getDouble(OWN_COORD_LAT);
        double lon = coordObj.getDouble(OWN_COORD_LON);

        Log.v(LOG_TAG, cityName + ", with coord: " + lat + " " + lon);

        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        List<WeatherEntity> weather_forecast = new ArrayList<>();
        for (int i = 0; i < weatherArray.length(); i++) {

            long epocTime;
            double high;
            double low;

            String description;

            JSONObject dayForecast = weatherArray.getJSONObject(i);

            epocTime = dayForecast.getLong(OWM_DATETIME);

            JSONObject dayJsonObject = dayForecast.getJSONObject("temp");
            description = dayForecast.getJSONArray("weather").getJSONObject(0).getString("description");

            high = dayJsonObject.getDouble(OWM_MAX_TEMP) - 273.15F;
            low = dayJsonObject.getDouble(OWM_MIN_TEMP) - 273.15F;

            Date date = new Date(epocTime * 1000L);

            String simpleDate = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(date);

            Log.d(LOG_TAG,"Date " + simpleDate + " Max: " + high + " Min: " + low );
            Log.d(LOG_TAG, " Description " + description + " Location " + locationSettings);

            WeatherEntity entity = new WeatherEntity();

            entity.mDate = simpleDate;
            entity.mLocation = locationSettings;
            entity.mMaxTemp = high;
            entity.mMinTemp = low;
            entity.mShortDesc = description;
            weather_forecast.add(entity);
        }

        final WeatherDatabase db = WeatherDatabase.getInstance(getApplicationContext());
        db.runInTransaction(() -> db.weather().insertAll(weather_forecast));
    }
}
