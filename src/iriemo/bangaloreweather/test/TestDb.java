package iriemo.bangaloreweather.test;

import iriemo.bangaloreweather.data.WeatherDbHelper;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

public class TestDb extends AndroidTestCase {

	public void testCreateDb() throws Throwable {
		mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
		SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();
		assertEquals(true,db.isOpen());
		db.close();
	}
}
