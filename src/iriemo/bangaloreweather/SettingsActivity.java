package iriemo.bangaloreweather;

import sync.SunRiseSyncAdapter;
import iriemo.bangaloreweather.data.WeatherContract;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
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
		public static class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener,Preference.OnPreferenceChangeListener
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

            @Override
            public void onResume() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                sp.registerOnSharedPreferenceChangeListener(this);
                super.onResume();
            }

            @Override
            public void onPause() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                sp.unregisterOnSharedPreferenceChangeListener(this);
                super.onPause();
            }

            private void bindPreferenceSummaryToValue(Preference findPreference)
			{
				findPreference.setOnPreferenceChangeListener(this);
				
				onPreferenceChange(findPreference, PreferenceManager.getDefaultSharedPreferences(findPreference.getContext()).getString(findPreference.getKey(), ""));
				
			}
	
			@Override
			public boolean onPreferenceChange(Preference preference, Object value) 
			{
                setPreferenceSummary(preference,value);
                return  true;
			}

            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                if(key.equals(getString(R.string.pref_location_key))){
                    SunRiseSyncAdapter.syncImmediately(getActivity());
                } else if(key.equals(getString(R.string.pref_temp_units_key))){
                    getActivity().getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI,null);
                }
            }

            private void setPreferenceSummary(Preference preference,Object value){
                String stringValue = value.toString();
                String key = preference.getKey();

                if(preference instanceof  ListPreference){
                    ListPreference listPreference = (ListPreference)preference;
                    int prefIndex = listPreference.findIndexOfValue(stringValue);
                    if(prefIndex >= 0){
                        preference.setSummary(listPreference.getEntries()[prefIndex]);
                    }
                }else if(key.equals(getString(R.string.pref_location_key))){
                    @SunRiseSyncAdapter.LocationStatus int status = Utility.getLocationStatus(getActivity());
                    switch (status){
                        case SunRiseSyncAdapter.LOCATION_STATUS_OK:
                            preference.setSummary(stringValue);
                            break;
                        case SunRiseSyncAdapter.LOCATION_STATUS_UNKNOWN:
                            preference.setSummary(getString(R.string.pref_location_unknown_description,value.toString()));
                            break;
                        case SunRiseSyncAdapter.LOCATION_STATUS_INVALID:
                            preference.setSummary(getString(R.string.pref_location_error_description,value.toString()));
                            break;
                        default:
                            preference.setSummary(stringValue);
                    }

                }
                else{
                    preference.setSummary(stringValue);
                }
            }
        }
	
	
	
}
