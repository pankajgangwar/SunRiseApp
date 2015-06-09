package iriemo.bangaloreweather;

import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MyService extends Service {

	private IBinder iBinder = new LocalBinder();
	int i = 0;
	TimerTask timerTask;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		timerTask = new TimerTask()
		{
			
			@Override
			public void run() {
				if(i != 100)
				i++;
			}
		};
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return iBinder;
	}

	
	public class LocalBinder extends Binder {
		MyService getBinder(){
			return MyService.this;
		}
	}
}
