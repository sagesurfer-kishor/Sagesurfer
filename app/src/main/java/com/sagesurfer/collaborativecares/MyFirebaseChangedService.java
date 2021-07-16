package com.sagesurfer.collaborativecares;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.cometchat.pro.core.Call;
import com.cometchat.pro.helpers.CometChatHelper;
import com.cometchat.pro.models.BaseMessage;
import com.firebase.CallNotificationAction;
import com.firebase.Config;
import com.firebase.NotificationUtils;
import com.google.firebase.messaging.RemoteMessage;
import com.sagesurfer.constant.General;
import com.sagesurfer.logger.Logger;
import com.storage.preferences.Preferences;
import com.utils.AppLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import constant.StringContract;

public class MyFirebaseChangedService extends FirebaseMessagingService{
    private static final String TAG = "MyFirebaseService";
    private JSONObject json;
    private Intent intent;
    private int count=0;
    private Call call;
    public static String token;
    private static final int REQUEST_CODE = 12;
    private NotificationUtils notificationUtils;
    private boolean isCall;

    @Override
    public void onNewToken(String s) {
        token  = s;
        Preferences.save("regId_save", false);
        Preferences.initialize(getApplicationContext());
        Preferences.save("regId", token);
        Preferences.save("device_token", token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            count++;
            json = new JSONObject(remoteMessage.getData());
            AppLog.d(TAG, "JSONObject: "+json.toString());
            JSONObject messageData = new JSONObject(json.getString("message"));
            BaseMessage baseMessage = CometChatHelper.processMessage(new JSONObject(remoteMessage.getData().get("message")));
            if (baseMessage instanceof Call){
                call = (Call)baseMessage;
                isCall=true;
            }
            showNotifcation(baseMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            AppLog.i(TAG, "From DATA" + remoteMessage.getFrom());
            Logger.error("Debug", "Firebase Notification payload12 : " + remoteMessage.getData().toString(), getApplicationContext());
            JSONObject json = new JSONObject(remoteMessage.getData());
            AppLog.i(TAG, "onMessageReceived: data " + json.toString());
            handleDataMessage(data);
        } else {
            AppLog.i(TAG, "From notification" + remoteMessage.getFrom());
        }
    }

    public Bitmap getBitmapFromURL(String strURL) {
        if (strURL!=null) {
            try {
                URL url = new URL(strURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    private void showNotifcation(BaseMessage baseMessage) {

        try {
            int m = (int) ((new Date().getTime()));
            String GROUP_ID = "group_id";

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"2")
                    .setSmallIcon(R.drawable.cc)
                    .setContentTitle(json.getString("title"))
                    .setContentText(json.getString("alert"))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setLargeIcon(getBitmapFromURL(baseMessage.getSender().getAvatar()))
                    .setGroup(GROUP_ID)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

            NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(this,"2")
                    .setContentTitle("CometChat")
                    .setContentText(count+" messages")
                    .setSmallIcon(R.drawable.cc)
                    .setGroup(GROUP_ID)
                    .setGroupSummary(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            if (isCall){
                builder.setGroup(GROUP_ID+"Call");
                if (json.getString("alert").equals("Incoming audio call") || json.getString("alert").equals("Incoming video call")) {
                    builder.setOngoing(true);
                    builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                    builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
                    builder.addAction(0, "Answers", PendingIntent.getBroadcast(getApplicationContext(), REQUEST_CODE, getCallIntent("Answer"), PendingIntent.FLAG_UPDATE_CURRENT));
                    builder.addAction(0, "Decline", PendingIntent.getBroadcast(getApplicationContext(), 1, getCallIntent("Decline"), PendingIntent.FLAG_UPDATE_CURRENT));
                }
                notificationManager.notify(05,builder.build());
            }
            else {
                notificationManager.notify(baseMessage.getId(), builder.build());
                notificationManager.notify(0, summaryBuilder.build());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Intent getCallIntent(String title){
        Intent callIntent = new Intent(getApplicationContext(), CallNotificationAction.class);
        callIntent.putExtra(StringContract.IntentStrings.SESSION_ID,call.getSessionId());
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.setAction(title);
        return callIntent;
    }
    private void handleDataMessage(Map<String, String> datamap) {
        try {
            String dataobj = datamap.get("data");
            JSONObject data = new JSONObject(dataobj);
            AppLog.e(TAG, "Jdata" + data.toString());
            String title = data.optString("title");
            String message = data.optString("message");

//         String message = "This is demo app";
            String imageUrl = data.optString("image");
            String type = data.optString("menu_id");
            String timestamp = data.optString("timestamp");
            String groupId = data.optString("groupid");
            String isModerator = data.optString("ismodrator");
            String ampm = data.optString("ampm");

            Preferences.save(General.GROUP_ID, groupId);
            Preferences.save(General.IS_MODERATOR, isModerator);
            Preferences.save(General.IS_PUSH_NOTIFICATION, true);
            Preferences.save(General.GOAL_AM_PM, ampm);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            timestamp = sdf.format(new Date());

            //List<String> post1Comments = new ArrayList<>();
            // Collect comments of a certain post
            //post1Comments.add(groupId);
            // Attach comments to post
            List<String> notificationValues = new ArrayList<String>();
            notificationValues.add(0, title);
            notificationValues.add(1, message);
            notificationValues.add(2, type);
            notificationValues.add(3, groupId);
            Config.mapOfPosts.put(timestamp, notificationValues);

//            for (Map.Entry<String, List<String>> entry : Config.mapOfPosts.entrySet()) {
//                String key = entry.getKey();
//                //String value = entry.getValue();
//                System.out.printf("%s -> %s%n", entry.getKey(), entry.getValue().get(0) + "-" + entry.getValue().get(1) + "-" + entry.getValue().get(2) + "-" + entry.getValue().get(3));
//            }

//            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//                // app is in foreground, broadcast the push message
//                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
//                pushNotification.putExtra(General.MESSAGE, message);
//                pushNotification.putExtra(General.TIMESTAMP, timestamp);
//                pushNotification.putExtra(General.TITLE, title);
//                pushNotification.putExtra(General.TYPE, type);
//                //pushNotification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//                // play notification sound
//                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//                notificationUtils.playNotificationSound();
//
//                if (TextUtils.isEmpty(imageUrl)) {
//                    showNotificationMessage(getApplicationContext(),
//                            title,
//                            message,
//                            timestamp, type, pushNotification);
//                } else {
//                    // if image is present, show notification with image
//                    showNotificationMessageWithBigImage(getApplicationContext(),
//                            title, message, timestamp, type, pushNotification, imageUrl);
//                }
//
//            } else {
            // app is in background, show the notification in notification tray
            Intent resultIntent;
            //commented on 29/04/19 as need to redirect on team announcement, team task list and team events
                /*if(Integer.parseInt(Preferences.get(General.GROUP_ID)) != 0) {
                    resultIntent = new Intent(getApplicationContext(), TeamDetailsActivity.class);
                } else {
                    resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                }*/
            resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            resultIntent.putExtra(General.GROUP_ID, Preferences.get(General.GROUP_ID));
            resultIntent.putExtra(General.MESSAGE, message);
            resultIntent.putExtra(General.TIMESTAMP, timestamp);
            resultIntent.putExtra(General.TITLE, title);
            resultIntent.putExtra(General.TYPE, type);
            // check for image attachment

            AppLog.i(TAG, "Condition");
            if (TextUtils.isEmpty(imageUrl)) {
                AppLog.i(TAG, "Image URL without");
                showNotificationMessage(getApplicationContext(), title, message, timestamp, type, resultIntent);
            } else {
                AppLog.i(TAG, "Image URL with");
                // if image is present, show notification with image
                showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, type, resultIntent, imageUrl);
            }
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Show text notification
    private void showNotificationMessage(Context context,
                                         String title,
                                         String message,
                                         String timeStamp,
                                         String type,
                                         Intent intent) {

        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //timeStamp is notification id to make notification stack
        notificationUtils.showNotificationMessage(title, message, timeStamp, type, intent);

    }

    // Show notifications with image or image with text
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, String type, Intent intent, String imageUrl) {
//        if (!isChatScreen) {
        notificationUtils = new NotificationUtils(context);
        AppLog.e(TAG, "showNotificationMessageWithBigImage: ");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, type, intent, imageUrl);
//        } else {
//            Log.i(TAG, "showNotificationMessageWithBigImage: Is chat screen");
//        }
    }

}
