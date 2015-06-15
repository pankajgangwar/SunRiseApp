package iriemo.bangaloreweather.service;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by iriemo on 12/6/15.
 */
public class GCMService  extends GcmListenerService {

    private static final String TAG = GCMService.class.getSimpleName();

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getBaseContext());

        String message = data.getString("message");
        Log.d(TAG,"From: " + from);
        Log.d(TAG,"Message: " + message);

        sendNotification(message);
    }


    private void sendNotification(String message) {

    }
}
