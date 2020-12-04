package com.firebase;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.helpers.CometChatHelper;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.TextMessage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.modules.cometchat_7_30.ChatFragment_;
import com.sagesurfer.collaborativecares.LoginActivity;
import com.sagesurfer.collaborativecares.MainActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.logger.Logger;
import com.sagesurfer.network.AppConfig;
import com.storage.preferences.Preferences;

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
import java.util.Random;

import constant.StringContract;

/**
 * @author Kailash Karankal
 **/

/*
 * This file contains firebase messaging service to get firabase push messages
 * Methods is added to parse and show push notifications
 */


public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = MessagingService.class.getSimpleName();
    public static final String PUSH_CHANNEL = "push_channel";
    private NotificationUtils notificationUtils;


    private JSONObject json;
    private Intent intent;
    private int count = 0;
    private Call call;
    JSONObject messageData;
    private TextMessage textMessage;

    public static String token;
    private static final int REQUEST_CODE = 12;
    private Context mContext;
    private Random random = new Random();
    private int singleNotificationId, bundleNotificationId;

    private boolean isCall;
    private boolean isText;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("notification", remoteMessage.toString());

        Logger.error("Debug", "Firebase Notification payload : " + remoteMessage.getData().toString(), getApplicationContext());
        Preferences.save(General.IS_PUSH_NOTIFICATION_SENT, false);
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            try {
                if (remoteMessage.getData().containsKey("action")) {
                    // if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                    Logger.error("Debug", "Firebase Notification payload11 : " + remoteMessage.getData().toString(), getApplicationContext());
                    //  }
                } else {
                    Logger.error("Debug", "Firebase Notification payload12 : " + remoteMessage.getData().toString(), getApplicationContext());
                    JSONObject json = new JSONObject(remoteMessage.getData().toString());
                    handleDataMessage(json);
                }
            } catch (Exception e) {
                Logger.error(TAG, "" + e.getMessage(), getApplicationContext());
            }
        }

        //Comet chat messages
        try {
            count++;
            json = new JSONObject(remoteMessage.getData());

            messageData = new JSONObject(json.getString("message"));
            String title = messageData.getString("receiver");
            String receiverType = messageData.getString("receiverType");

            Log.e(TAG, "JSONObject: " + messageData);

            BaseMessage baseMessage = CometChatHelper.processMessage(new JSONObject(remoteMessage.getData().get("message")));
            if (baseMessage instanceof Call) {
                call = (Call) baseMessage;
                isCall = true;
            }
            showNotifcation(baseMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void subscribeUserNotification(String UID) {
        FirebaseMessaging.getInstance().subscribeToTopic(AppConfig.AppDetails.APP_ID + "_" + CometChatConstants.RECEIVER_TYPE_USER + "_" +
                UID).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, UID + " Subscribed Success");
            }
        });
    }

    public static void unsubscribeUserNotification(String UID) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(AppConfig.AppDetails.APP_ID + "_" + CometChatConstants.RECEIVER_TYPE_USER + "_" +
                UID).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, UID + " Unsubscribed Success");
            }
        });
    }

    public static void subscribeGroupNotification(String GUID) {
        FirebaseMessaging.getInstance().subscribeToTopic(AppConfig.AppDetails.APP_ID + "_" + CometChatConstants.RECEIVER_TYPE_GROUP + "_" +
                GUID).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, GUID + " Subscribed Success");
            }
        });
    }

    public static void unsubscribeGroupNotification(String GUID) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(AppConfig.AppDetails.APP_ID + "_" + CometChatConstants.RECEIVER_TYPE_GROUP + "_" +
                GUID);
    }

    @Override
    public void onNewToken(String s) {
        token = s;
        Log.e(TAG, "onNewToken: " + s);
    }


    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }
    }

    private void handleDataMessage(JSONObject json) {
        Logger.debug(TAG, "" + json, getApplicationContext());
        try {
            JSONObject data = json.getJSONObject("data");

            //{data={"image":"","is_background":false,"ampm":"pm","groupid":"0","ismodrator":"0","title":"You have daily dosage due","message":"Did you take the medicine in the night (PM)?","menu_id":"24_","timestamp":"2020-05-25 10:39:29"}}

            Preferences.initialize(getApplicationContext());
            String title = data.getString("title");
            String message = data.getString("message");
            String imageUrl = data.getString("image");
            String type = data.getString("menu_id");
            String timestamp = data.getString("timestamp");
            String groupId = data.getString("groupid");
            String isModerator = data.getString("ismodrator");
            String ampm = data.getString("ampm");
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

            for (Map.Entry<String, List<String>> entry : Config.mapOfPosts.entrySet()) {
                String key = entry.getKey();
                //String value = entry.getValue();
                System.out.printf("%s -> %s%n", entry.getKey(), entry.getValue().get(0) + "-" + entry.getValue().get(1) + "-" + entry.getValue().get(2) + "-" + entry.getValue().get(3));
            }

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra(General.MESSAGE, message);
                pushNotification.putExtra(General.TIMESTAMP, timestamp);
                pushNotification.putExtra(General.TITLE, title);
                pushNotification.putExtra(General.TYPE, type);
                //pushNotification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();

                /*if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, pushNotification);
                } else {
                    // if image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, pushNotification, imageUrl);
                }*/
            } else {
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

                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, type, resultIntent);
                } else {
                    // if image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, type, resultIntent, imageUrl);
                }
            }
        } catch (Exception e) {
            Logger.error(TAG, "" + e.getMessage(), getApplicationContext());
        }
    }

    // Show text notification
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, String type, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //timeStamp is notification id to make notification stack
        notificationUtils.showNotificationMessage(title, message, timeStamp, type, intent);
    }

    // Show notifications with image or image with text
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, String type, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, type, intent, imageUrl);
    }

    private void showNotifcation(BaseMessage baseMessage) {

        try {
            int m = (int) ((new Date().getTime()));
            String GROUP_ID = "group_id";

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "2")
                    .setSmallIcon(R.drawable.ic_sage_icon)
                    .setContentTitle(json.getString("title"))
                    .setContentText(json.getString("alert"))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setLargeIcon(getBitmapFromURL(baseMessage.getSender().getAvatar()))
                    .setGroup(GROUP_ID)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setContentIntent(getIntent())
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));


            NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(this, "2")
                    .setContentTitle("CometChat")
                    .setContentText(count + " messages")
                    .setSmallIcon(R.drawable.ic_sage_icon)
                    .setGroup(GROUP_ID)
                    .setGroupSummary(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);


            if (isCall) {
                builder.setGroup(GROUP_ID + "Call");
                if (json.getString("alert").equals("Incoming audio call") || json.getString("alert").equals("Incoming video call")) {
                    builder.setOngoing(true);
                    builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                    builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
                    builder.addAction(0, "Answers", PendingIntent.getBroadcast(getApplicationContext(), REQUEST_CODE, getCallIntent("Answers"), PendingIntent.FLAG_UPDATE_CURRENT));
                    builder.addAction(0, "Decline", PendingIntent.getBroadcast(getApplicationContext(), 1, getCallIntent("Decline"), PendingIntent.FLAG_UPDATE_CURRENT));
                }
                notificationManager.notify(05, builder.build());
            } else {

                notificationManager.notify(baseMessage.getId(), builder.build());
                notificationManager.notify(0, summaryBuilder.build());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Intent getCallIntent(String title) {
        Intent callIntent = new Intent(getApplicationContext(), CallNotificationAction.class);
        callIntent.putExtra(StringContract.IntentStrings.SESSION_ID, call.getSessionId());
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.setAction(title);
        return callIntent;
    }

    private PendingIntent getIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        if (Preferences.contains(General.IS_LOGIN) && Preferences.get(General.IS_LOGIN).equalsIgnoreCase("1")) {
            // User is logged in
            try {
                // Getting team logs id
                String team_logs_id = messageData.getJSONObject("data").getJSONObject("metadata").optString("team_logs_id");
                intent.putExtra("team_logs_id", team_logs_id);
                intent.putExtra("receiver", messageData.optString("receiver"));
                intent.putExtra("sender", messageData.optString("sender"));
                intent.putExtra("receiverType", messageData.optString("receiverType"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else
            // User has not logged in. Send him to login screen
            intent = new Intent(this, LoginActivity.class);
        return PendingIntent.getActivity(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public Bitmap getBitmapFromURL(String strURL) {
        if (strURL != null) {
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

}
