package com.example.weather.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SunRiseSyncService extends Service {

	private static final Object sSyncAdapterLock = new Object();
	private static SunRiseSyncAdapter sSunRiseSyncAdapter = null;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		Log.d("SunRiseSyncService", "onCreate - SunshineSyncService");
		
		synchronized (sSyncAdapterLock) {
			
		if(sSunRiseSyncAdapter == null)	 {
			sSunRiseSyncAdapter = new SunRiseSyncAdapter(getApplicationContext(), true);

			}
		}
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return sSunRiseSyncAdapter.getSyncAdapterBinder();
	}

}
