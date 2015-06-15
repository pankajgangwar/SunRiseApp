package iriemo.bangaloreweather.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import iriemo.bangaloreweather.MainActivity;
import iriemo.bangaloreweather.R;

/**
 * Created by iriemo on 12/6/15.
 */
public class GCMService  extends GcmListenerService {

    private static final String TAG = GCMService.class.getSimpleName();
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getBaseContext());

        String message = data.getString("message");

        String weather = data.getString("weather");
        String location = data.getString("location");

        String alert = "Heads up: " + weather +" in " + location + "!";

        Log.d(TAG," Alert is: " + alert);
        Log.d(TAG,"From: " + from);
        Log.d(TAG,"Message: " + message);

        sendNotification(getBaseContext(),location);
    }


    private void sendNotification(Context context, String msg) {
        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent =
                PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.art_storm)
                        .setContentTitle("Weather Alert!")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
