package com.example.weather.data;

import com.example.weather.data.WeatherContract.LocationEntry;
import com.example.weather.data.WeatherContract.WeatherEntry;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class WeatherProvider extends ContentProvider {

	private static final int WEATHER = 100;
	private static final int WEATHER_WITH_LOCATION = 101;
	private static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
	private static final int LOCATION = 300;
	private static final int LOCATION_ID = 301;
	
	private static final UriMatcher sUriMatcher = buildUriMatcher();
	
	private WeatherDbHelper weatherDbHelper;
	
	private static UriMatcher buildUriMatcher(){
		
		 // I know what you're thinking. Why create a UriMatcher when you can use regular
		// expressions instead? Because you're not crazy, that's why.
		 
		// All paths added to the UriMatcher have a corresponding code to return when a match is
		// found. The code passed into the constructor represents the code to return for the root
		// URI. It's common to use NO_MATCH as the code for this case. 
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = WeatherContract.CONTENT_AUTHORITY;
		
		   // For each type of URI you want to add, create a corresponding code.
		matcher.addURI(authority, WeatherContract.PATH_WEATHER,WEATHER);
		matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION);
		matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*/*", WEATHER_WITH_LOCATION_AND_DATE);
		
		matcher.addURI(authority, WeatherContract.PATH_LOCATION, LOCATION);
		matcher.addURI(authority, WeatherContract.PATH_LOCATION + "/#", LOCATION_ID);
		
		return matcher;
	}
	
	private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;
	
	static{
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
        sWeatherByLocationSettingQueryBuilder.setTables(
                WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        WeatherContract.LocationEntry.TABLE_NAME +
                        " ON " + WeatherContract.WeatherEntry.TABLE_NAME +
                        "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
                        " = " + WeatherContract.LocationEntry.TABLE_NAME +
                        "." + WeatherContract.LocationEntry._ID);
    }
	
			 private static final String sLocationSettingSelection =
			 WeatherContract.LocationEntry.TABLE_NAME+
			 "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTINGS + " = ? ";
	 
			 private static final String sLocationSettingWithStartDateSelection =
			 WeatherContract.LocationEntry.TABLE_NAME+
			 "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTINGS + " = ? AND " +
			 WeatherContract.WeatherEntry.COLUMN_DATETEXT + " >= ? ";
			 
			 private static final String sLocationSettingAndDaySelection =
					 WeatherContract.LocationEntry.TABLE_NAME +
					 "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTINGS + " = ? AND " +
					 WeatherContract.WeatherEntry.COLUMN_DATETEXT + " = ? "; 
			  
			 private Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder) {
				 String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
				 String startDate = WeatherContract.WeatherEntry.getStartDateFromUri(uri);
				 String[] selectionArgs;
				 String selection;
				 if (startDate == null) {
				 selection = sLocationSettingSelection;
				 selectionArgs = new String[]{locationSetting};
				 } else {
				 selectionArgs = new String[]{locationSetting, startDate};
				 selection = sLocationSettingWithStartDateSelection;
				 }
				 return sWeatherByLocationSettingQueryBuilder.query(weatherDbHelper.getReadableDatabase(),
				 projection,
				 selection,
				 selectionArgs,
				 null,
				 null,
				 sortOrder
				 );
				 }
			 
			 private Cursor getWeatherByLocationSettingWithDate(Uri uri, String[] projection, String sortOrder) {
				 
					 String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
					 String date = WeatherContract.WeatherEntry.getDateFromUri(uri);
					  
					 return sWeatherByLocationSettingQueryBuilder.query(weatherDbHelper.getReadableDatabase(),
					 projection,
					 sLocationSettingAndDaySelection,
					 new String[]{locationSetting, date},
					 null,
					 null,
					 sortOrder
					 );
					 } 

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		weatherDbHelper = new WeatherDbHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		 // Here's the switch statement that, given a URI, will determine what kind of request it is,
		// and query the database accordingly. 
		Cursor retCursor;
		
		switch (sUriMatcher.match(uri)) 
		{
		// "weather/*/*"
		case WEATHER_WITH_LOCATION_AND_DATE:
			retCursor = getWeatherByLocationSettingWithDate(uri, projection, sortOrder);
			
			break;
			
		// "weather/*"	
		case WEATHER_WITH_LOCATION:
			retCursor = getWeatherByLocationSetting(uri, projection, sortOrder);
			
			break;
			
			// "weather"
		case WEATHER:
			retCursor = weatherDbHelper.getReadableDatabase().query(
					WeatherContract.WeatherEntry.TABLE_NAME, 
					projection, 
					selection, 
					selectionArgs, 
					null, 
					null, 
					sortOrder
					);
			
			break;
			
			// "location/*"
		case LOCATION_ID:
			retCursor = weatherDbHelper.getReadableDatabase().query(
					WeatherContract.LocationEntry.TABLE_NAME, 
					projection, 
					WeatherContract.LocationEntry._ID + " = " + ContentUris.parseId(uri) + "'", 
					null, 
					null, 
					null, 
					sortOrder
					);
			break;
			
			// "location"
		case LOCATION:
			retCursor = weatherDbHelper.getReadableDatabase().query(
					WeatherContract.LocationEntry.TABLE_NAME, 
					projection, 
					selection, 
					selectionArgs, 
					null, 
					null, 
					sortOrder
					);
			
			break;
			
		default:
			 throw new UnsupportedOperationException("Unknown uri: " + uri);
			
		}
		
		// This causes the cursor to register a content observer. To watch for changes that
		// happen to that URI and any of its descendents(Any Uri that begins with this Path). 
		retCursor.setNotificationUri(getContext().getContentResolver(), uri);
		return retCursor;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		final int match = sUriMatcher.match(uri);
		switch (match)
		{
		case WEATHER_WITH_LOCATION_AND_DATE: 
			return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
			
		case WEATHER_WITH_LOCATION:
			return WeatherContract.WeatherEntry.CONTENT_TYPE;
			
		case WEATHER:
			return WeatherContract.WeatherEntry.CONTENT_TYPE;
			
		case LOCATION:
			return WeatherContract.LocationEntry.CONTENT_TYPE;
			
		case LOCATION_ID:
			return WeatherContract.LocationEntry.CONTENT_ITEM_TYPE;

		default:
			throw new UnsupportedOperationException("Unkown uri: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		final SQLiteDatabase db = weatherDbHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		Uri returnUri;
		
		switch (match) 
		{
		case WEATHER:
		{
			long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, values);
			if(_id > 0)
				returnUri = WeatherContract.WeatherEntry.buildWeatherUri(_id);
			else
				throw new android.database.SQLException("Failed to insert row into " + uri);
			break;
		}
			
		case LOCATION:
		{
			long _id = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, values);
			if(_id > 0)
				returnUri = WeatherContract.LocationEntry.buildLocationUri(_id);
			else
				throw new android.database.SQLException("Failed to insert row into " + uri);
			break;
		}

		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		
		// To notify any observer that need to know about Uri is changed
		getContext().getContentResolver().notifyChange(returnUri, null);
		return returnUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		final SQLiteDatabase db = weatherDbHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		int rowsDeleted;
		switch (match) {
		case WEATHER:
			
			rowsDeleted =  db.delete(WeatherContract.WeatherEntry.TABLE_NAME, selection, selectionArgs);
			break;
		case LOCATION:
			
			rowsDeleted =  db.delete(WeatherContract.LocationEntry.TABLE_NAME, selection, selectionArgs);
			break;

		default:
			throw new UnsupportedOperationException("Unable to delete. Unkown uri: " + uri);
		}
		
		if(null == selection || 0 != rowsDeleted)
		{
			getContext().getContentResolver().notifyChange(uri, null);
		}
		
		return rowsDeleted;
		
		
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		final SQLiteDatabase db = weatherDbHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		int rowsUpdated;
		switch (match) {
		case WEATHER:
			
			rowsUpdated =  db.update(WeatherContract.WeatherEntry.TABLE_NAME, values, selection, selectionArgs);
			
			break;
			
		case LOCATION:
			
			rowsUpdated =   db.update(WeatherContract.LocationEntry.TABLE_NAME, values, selection, selectionArgs);
			break;
			
		default:
			throw new UnsupportedOperationException("Unable to delete. Unkown uri: " + uri);
		}
		
		if(0 != rowsUpdated)
		{
			getContext().getContentResolver().notifyChange(uri, null);
		}
		
		return rowsUpdated;
		
	}

	//Perform bulk of insert's in a Single Transaction
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		// TODO Auto-generated method stub
		final SQLiteDatabase db = weatherDbHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		
		switch (match) {
		
		case WEATHER:
		{
			db.beginTransaction();
			int returnCount = 0;
			try{
				for(ContentValues value : values){
					long _id = db.insert(WeatherEntry.TABLE_NAME, null, value);
					if( -1 != _id)
					{
						returnCount++;
					}
				}
				db.setTransactionSuccessful();
			}
			finally{
				db.endTransaction();
			}
			getContext().getContentResolver().notifyChange(uri, null);
			return returnCount;
		}

		case LOCATION:
		{
			db.beginTransaction();
			int returnCount = 0;
			try{
				for(ContentValues value : values){
					long _id = db.insert(LocationEntry.TABLE_NAME, null, value);
					if( -1 != _id)
					{
						returnCount++;
					}
				}
				db.setTransactionSuccessful();
			}
			finally{
				db.endTransaction();
			}
			getContext().getContentResolver().notifyChange(uri, null);
			return returnCount;
		}
			
			
		default:
			return super.bulkInsert(uri, values);
		}
	}
	
	
	
	
}