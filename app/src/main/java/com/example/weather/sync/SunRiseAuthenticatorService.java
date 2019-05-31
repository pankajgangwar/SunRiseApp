package com.example.weather.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.weather.sync.SunRiseAuthenticator;

public class SunRiseAuthenticatorService extends Service {

	private SunRiseAuthenticator mAuthenticator;
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		mAuthenticator = new SunRiseAuthenticator(this);
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mAuthenticator.getIBinder();
	}
	
	

}
