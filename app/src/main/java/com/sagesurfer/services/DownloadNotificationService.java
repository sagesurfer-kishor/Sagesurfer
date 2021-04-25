package com.sagesurfer.services;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

@SuppressLint("NewApi")
public class DownloadNotificationService extends NotificationListenerService {
    private static final String TAG = DownloadNotificationService.class.getSimpleName();
    private static final String NOT_TAG = "com.sagesurfer.NOTIFICATION_LISTENER";
    private static final String NOT_POSTED = "POSTED";
    private static final String NOT_REMOVED = "REMOVED";
    private static final String NOT_EVENT_KEY = "not_key";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Intent i = new Intent(NOT_TAG);
        i.putExtra(NOT_EVENT_KEY, NOT_POSTED);
        sendBroadcast(i);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Intent i = new Intent(NOT_TAG);
        i.putExtra(NOT_EVENT_KEY, NOT_REMOVED);
        sendBroadcast(i);
    }

}