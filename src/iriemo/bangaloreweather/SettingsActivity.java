package iriemo.bangaloreweather;

import sync.SunRiseSyncAdapter;
import iriemo.bangaloreweather.data.WeatherContract;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

public class SettingsActivity extends AppCompatActivity
{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
		
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public Intent getParentActivityIntent() {
		// TODO Auto-generated method stub
													// This Flag indicate that we should check if the main  activity
													//is already running in our task, And to use that one, instead of creating a new main 
													// activity instance.
		return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	}
	/*
	 * PrefernceFragment to support API level 11 onwards
	 */
		public static class PrefsFragment extends PreferenceFragment implements OnPreferenceChangeListener
		{
			@Override
			public void onCreate(Bundle savedInstanceState) 
			{
				// TODO Auto-generated method stub
				super.onCreate(savedInstanceState);
				
				addPreferencesFromResource(R.xml.pref_general);
				
				bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));
				bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_temp_units_key)));
			}
			
	
			private void bindPreferenceSummaryToValue(Preference findPreference) 
			{
				findPreference.setOnPreferenceChangeListener(this);
				
				onPreferenceChange(findPreference, PreferenceManager.getDefaultSharedPreferences(findPreference.getContext()).getString(findPreference.getKey(), ""));
				
			}
	
			@Override
			public boolean onPreferenceChange(Preference preference, Object value) 
			{
				String stringValue = value.toString();
				
			if(preference.getKey().equals(getString(R.string.pref_location_key))) {
				
				/*FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
				weatherTask.execute(stringValue);*/
                Utility.resetLocationStatus(getActivity());
				SunRiseSyncAdapter.syncImmediately(getActivity());
				
			} else {
				
				getActivity().getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null);
				
			}
				if(preference instanceof ListPreference)
				{
					ListPreference listPreference = (ListPreference)preference;
					
					int prefIndex = listPreference.findIndexOfValue(stringValue);
					
					if(prefIndex >= 0)
					{
						preference.setSummary(listPreference.getEntries()[prefIndex]);
					
					}
					else
					{
						preference.setSummary(stringValue);
					}
				}
				if(preference instanceof EditTextPreference)
				{
					preference.setSummary(stringValue);
				}
				
				if(preference instanceof CheckBoxPreference)
				{
					preference.setSummary(stringValue);
				}
				return true;
			}
	
		}
	
	
	
}
