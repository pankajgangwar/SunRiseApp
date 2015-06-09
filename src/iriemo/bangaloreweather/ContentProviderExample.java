package iriemo.bangaloreweather;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.UserDictionary;
import android.provider.UserDictionary.Words;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

public class ContentProviderExample extends ActionBarActivity 
{
	ContentResolver contentResolver;
	
	ListView dictionaryListView;
	
	private static final String LOG_TAG = ContentProviderExample.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_content_provider_example);
		
		contentResolver = getContentResolver();
		
		dictionaryListView = (ListView)findViewById(R.id.listview_user_dictionary);
		
		Cursor cursor = contentResolver.query(UserDictionary.Words.CONTENT_URI, null, null, null, null);
		
		
		String []from = new String[]{Words.WORD,Words.FREQUENCY};
		int to[] =   new int[] { android.R.id.text1, android.R.id.text2 };
		
	   SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item, cursor, from,to , 0);
	
	   dictionaryListView.setAdapter(cursorAdapter);
	   
	   Log.i(LOG_TAG, "onCreate Called");
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		
		Log.i(LOG_TAG, "onRestart Called");
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		Log.i(LOG_TAG, "onResume Called");
	}
	 @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		Log.i(LOG_TAG, "onPause Called");
	}
	 
	 @Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		Log.i(LOG_TAG, "onStop Called");
	}
	 
	 @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		Log.i(LOG_TAG, "onDestroy Called");
	}
	 
	 @Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		 Log.i(LOG_TAG, "onPrepareOptions Menu Called");
		return super.onPrepareOptionsMenu(menu);
	}
	
}
