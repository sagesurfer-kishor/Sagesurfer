package com.sagesurfer.collaborativecares;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;


import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


/**
 * Created by Snow on 5/31/2016.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static int count = 0;


    public static String TAG = FirebaseMessagingService.class.getSimpleName();


    @Override
    public ComponentName startService(Intent service) {
        return super.startService(service);
    }

    @Override
    public boolean isRestricted() {
        return super.isRestricted();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        return super.getApplicationInfo();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.i(MyFirebaseMessagingService.class.getSimpleName(), "onMessageReceived ");

        if (!remoteMessage.getData().isEmpty()) {

            Map<String, String> data = remoteMessage.getData();

            Log.i(TAG, "From DATA" + remoteMessage.getFrom());

            Log.i(TAG, "From DATA" + remoteMessage.getData());

            if (data != null) {
                sendNotification(data);
            }
        } else {
            Log.i(TAG, "From notification" + remoteMessage.getFrom());
        }
    }

    private void sendNotification(Map<String, String> data) {


        final Intent gotoIntent = new Intent();


        gotoIntent.setClassName(getApplicationContext(), "com.sagesurfer.collaborativecares.SplashActivity");


        gotoIntent.putExtra("title", data.get("title"));
        gotoIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, gotoIntent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);

//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this, channelId)
//                        .setSmallIcon(R.mipmap.ic_notification)
//                        .setContentTitle(data.get("title"))
//                        .setContentText(data.get("body"))
//                        .setAutoCancel(true)
//                        .setContentIntent(pendingIntent);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId);

        mBuilder.setStyle(new
                NotificationCompat.BigTextStyle().bigText(data.get("body"))
                .setBigContentTitle(data.get("title")));
        mBuilder.setSmallIcon(R.drawable.ic_notification);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(channelId,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            channel.setDescription(data.get("body"));
            channel.enableLights(true);
            channel.enableVibration(true);

            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(++count, mBuilder.build());


    }
}

