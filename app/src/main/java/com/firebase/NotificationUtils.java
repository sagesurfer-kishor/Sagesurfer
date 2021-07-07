package com.firebase;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.core.app.NotificationCompat;

import com.sagesurfer.collaborativecares.MainActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author kailash karankal
 **/

/*
 * This file contains common Notifications creator for firebase
 */

public class NotificationUtils {

    private static String TAG = NotificationUtils.class.getSimpleName();

    private static final String NOTIFICATION_GROUP = "com.sagesurfer.collaborativecares.notification_type";

    private static final int NOTIFICATION_GROUP_SUMMARY_ID = 1;
    private static final int NOTIFICATION_ID = 11;

    private Context mContext;
    private NotificationManager notificationManager;

    private Random random = new Random();
    private int singleNotificationId, bundleNotificationId;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void showNotificationMessage(String title, String message, String timeStamp, String type, Intent intent) {
        showNotificationMessage(title, message, timeStamp, type, intent, null);
    }

    public void showNotificationMessage(final String title, final String message, final String timeStamp, final String type, Intent intent, String imageUrl) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;

        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // notification icon
        final int icon = R.drawable.ic_sage_icon;

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        bundleNotificationId = random.nextInt(9999 - 1000) + 1000; //For showing multiple push notification

        final PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, bundleNotificationId, intent, PendingIntent.FLAG_ONE_SHOT);

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/raw/notification");

        if (!TextUtils.isEmpty(imageUrl)) {

            if (imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {

                Bitmap bitmap = getBitmapFromURL(imageUrl);

                if (bitmap != null) {
                    showBigNotification(bitmap, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                } else {
                    showSmallNotification(icon, title, message, timeStamp, type, resultPendingIntent, alarmSound);
                }
            }
        } else {
            showSmallNotification(icon, title, message, timeStamp, type, resultPendingIntent, alarmSound);
            playNotificationSound();
        }
    }

    private void showSmallNotification(int icon, String title, String message, String timeStamp, String type, PendingIntent resultPendingIntent, Uri alarmSound) {

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(message);
        NotificationCompat.Builder notification;

        //We need to update the bundle notification every time a new notification comes up.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannels().size() < 2) {
                @SuppressLint("WrongConstant")
                NotificationChannel groupChannel = new NotificationChannel(mContext.getString(R.string.default_notification_channel_id), mContext.getString(R.string.notifications_admin_channel_name), NotificationManager.IMPORTANCE_LOW);
                notificationManager.createNotificationChannel(groupChannel);
                @SuppressLint("WrongConstant")
                NotificationChannel channel = new NotificationChannel(mContext.getString(R.string.default_notification_channel_id), mContext.getString(R.string.notifications_admin_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new NotificationCompat.Builder(mContext, mContext.getString(R.string.default_notification_channel_id))
                    .setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setStyle(inboxStyle)
                    .setWhen(getTimeMilliSec(timeStamp))
                    .setSmallIcon(R.drawable.ic_sage_icon)
                    .setContentText(message)
                    .setGroup(NOTIFICATION_GROUP);
        } else {
            notification = new NotificationCompat.Builder(mContext, mContext.getString(R.string.default_notification_channel_id))
                    .setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setStyle(inboxStyle)
                    .setWhen(getTimeMilliSec(timeStamp))
                    .setSmallIcon(R.drawable.ic_sage_icon)
                  //  .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .setContentText(message)
                    .setGroup(NOTIFICATION_GROUP);
        }

        bundleNotificationId = random.nextInt(9999 - 1000) + 1000; //For showing multiple push notification

        Intent resultIntent = new Intent(mContext, MainActivity.class);
        resultIntent.putExtra(General.MESSAGE, message);
        resultIntent.putExtra(General.TIMESTAMP, timeStamp);
        resultIntent.putExtra(General.TITLE, title);
        resultIntent.putExtra(General.TYPE, type);
        resultIntent.putExtra("notification_id", bundleNotificationId);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        resultPendingIntent = PendingIntent.getActivity(mContext, bundleNotificationId, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifications = new NotificationCompat.Builder(mContext, mContext.getString(R.string.default_notification_channel_id))
                .setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(inboxStyle)
                .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(R.drawable.ic_sage_icon)
               // .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .setGroup(NOTIFICATION_GROUP);

        notificationManager.notify(bundleNotificationId, notifications.build());

    }

    public void removenotification(int notification_id) {
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notification_id);
    }

    private void showBigNotification(Bitmap bitmap, int icon, String title, String message, String timeStamp,
                                     PendingIntent resultPendingIntent, Uri alarmSound) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(bigPictureStyle)
                .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(R.drawable.ic_sage_icon)
               // .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .setGroup(NOTIFICATION_GROUP);

        final Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, notification);
        // [END create_notification]

        updateNotificationSummary(bitmap, builder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
    }

    /**
     * Adds/updates/removes the notification summary as necessary.
     */
    protected void updateNotificationSummary(Bitmap bitmap, NotificationCompat.Builder mBuilder,
                                             int icon, String title, String message, String timeStamp,
                                             PendingIntent resultPendingIntent, Uri alarmSound) {

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);
        NotificationCompat.Builder notifications;

        if (Config.mapOfPosts.size() > 1) {
            // Add/update the notification summary.
            notifications = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setStyle(bigPictureStyle)
                    .setWhen(getTimeMilliSec(timeStamp))
                    .setSmallIcon(R.drawable.ic_sage_icon)
                    //.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .setContentText(message)
                    .setGroup(NOTIFICATION_GROUP)
                    .setGroupSummary(true);

            final Notification notification = notifications.build();
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_GROUP_SUMMARY_ID, notification);
        } else {
            // Remove the notification summary.
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIFICATION_GROUP_SUMMARY_ID);
        }
    }

    /**
     * Adds/updates/removes the notification summary as necessary.
     */
    protected void updateNotificationSummary(NotificationCompat.Builder mBuilder,
                                             int icon, String title, String message, String timeStamp,
                                             PendingIntent resultPendingIntent, Uri alarmSound) {

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(message);

        NotificationCompat.Builder notifications;

        if (Config.mapOfPosts.size() > 1) {
            // Add/update the notification summary.
            notifications = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setStyle(inboxStyle)
                    .setWhen(getTimeMilliSec(timeStamp))
                    .setSmallIcon(R.drawable.ic_sage_icon)
                  //  .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .setContentText(message)
                    .setGroup(NOTIFICATION_GROUP)
                    .setGroupSummary(true);

            final Notification notification = notifications.build();
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_GROUP_SUMMARY_ID, notification);
        } else {
            // Remove the notification summary.
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIFICATION_GROUP_SUMMARY_ID);
        }
    }

    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Playing notification sound
    public void playNotificationSound() {
//        try {
//            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/raw/notification");
//            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
//            r.play();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert am != null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancelAll();
    }

    @SuppressLint("SimpleDateFormat")
    private static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
