package com.sagesurfer.collaborativecares;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.cometchat.pro.core.Call;
import com.cometchat.pro.helpers.CometChatHelper;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.User;
import com.firebase.CallNotificationAction;
import com.firebase.Config;
import com.firebase.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sagesurfer.constant.General;
import com.sagesurfer.logger.Logger;
import com.sagesurfer.utilities.AppInfo;
import com.storage.preferences.Preferences;
import com.utils.AppLog;

import org.jetbrains.annotations.NotNull;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import constant.StringContract;
import utils.Utils;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    private static int count = 0;

    private NotificationUtils notificationUtils;

    private JSONObject json;
    private Call call;

    Intent intentMain;
    JSONObject messageData;
    public static String token;
    private static final int REQUEST_CODE = 12;


    private boolean isCall;
    private boolean isChatScreen, IsFriendListingPage, IsGroupListingPage;
    SharedPreferences preferencesCheckCurrentActivity;
    SharedPreferences.Editor editor;


    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        AppLog.d(TAG, "data: " + remoteMessage.getData());

        preferencesCheckCurrentActivity = getSharedPreferences("preferencesCheckCurrentActivity", MODE_PRIVATE);
        isChatScreen = preferencesCheckCurrentActivity.getBoolean("IsChatScreen", false);
        IsFriendListingPage = preferencesCheckCurrentActivity.getBoolean("IsFriendListingPage", false);
        IsGroupListingPage = preferencesCheckCurrentActivity.getBoolean("IsGroupListingPage", false);

        intentMain = new Intent(this, MainActivity.class);
        Preferences.save(General.IS_PUSH_NOTIFICATION_SENT, false);


        if (remoteMessage.getData().size() > 0) {

            Map<String, String> data = remoteMessage.getData();

            AppLog.i(TAG, "From DATA" + remoteMessage.getFrom());

            Logger.error("Debug", "Firebase Notification payload12 : " + remoteMessage.getData().toString(), getApplicationContext());
            JSONObject json = new JSONObject(remoteMessage.getData());
            AppLog.i(TAG, "onMessageReceived: data " + json.toString());
            handleDataMessage(remoteMessage);

        } else {
            AppLog.i(TAG, "From notification" + remoteMessage.getFrom());
        }

    }

    private void handleDataMessage(RemoteMessage json) {
        try {
            Map<String, String> datas = json.getData();
            JSONObject object = new JSONObject(datas);

            AppLog.e(TAG, "JSON_OBJECT" + object.toString());
            String yourString = object.toString().replaceAll("\\\\", "");

            // Get the index
            int index = 8;
            String ch = "";

            yourString = yourString.substring(0, index) + ch
                    + yourString.substring(index + 1);

            index = yourString.length() - 2;

            yourString = yourString.substring(0, index) + ch
                    + yourString.substring(index + 1);

            AppLog.e(TAG, "JSON_OBJECT jsonObject" + yourString);

            Preferences.initialize(getApplicationContext());

            JSONObject mainobj = new JSONObject(yourString);

            JSONObject data = mainobj.getJSONObject("data");

            AppLog.e(TAG, "Jdata" + data.toString());

            String title = data.optString("title");
            String message = data.optString("message");

//            String message = "This is demo app";
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


    @Override
    public void onNewToken(String token) {
        AppLog.d(TAG, "Refreshed token: " + token);
        Preferences.save("regId_save", false);
        Preferences.initialize(getApplicationContext());
        Preferences.save("regId", token);
        Preferences.save("device_token", token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]


    private void sendNotification(Map<String, String> data) {

        final Intent gotoIntent = new Intent();


        gotoIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, gotoIntent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);

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


    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        AppLog.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM registration token with any
     * server-side account maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        AppLog.i(TAG, "sendRegistrationToServer " + token);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void showNotifcation(BaseMessage baseMessage) {
        try {
            if (!isChatScreen) {
                AppLog.e(TAG, "showNotifcation: " + json.toString());
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
                        .setAutoCancel(true)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setContentIntent(getIntent())
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

                NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(this, "2")
                        .setContentTitle("CometChat")
                        .setContentText(count + " messages")
                        .setSmallIcon(R.drawable.ic_sage_icon)
                        .setGroup(GROUP_ID)
                        .setAutoCancel(true)
                        .setGroupSummary(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                if (isCall) {
                    builder.setGroup(GROUP_ID + "Call");
                    //intentMain.putExtra("callJson", (Parcelable) json);
                    Utils.startCallIntent(getApplicationContext(), (User) call.getCallInitiator(), call.getType(),
                            false, call.getSessionId());

                    //intentMain.putExtra("CallInitiator",""+)
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
            } else {
                AppLog.i(TAG, "showNotifcation: Is chat screen");
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppLog.i(TAG, "showNotifcation: error 2" + e.getMessage());

        }
    }


    private Intent getCallIntent(String title) {
        AppLog.e(TAG, "getCallIntent: ");
        Intent callIntent = new Intent(getApplicationContext(), CallNotificationAction.class);
        callIntent.putExtra(StringContract.IntentStrings.SESSION_ID, call.getSessionId());
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.setAction(title);
        return callIntent;
    }

    private PendingIntent getIntent() {
        if (Preferences.contains(General.IS_LOGIN) && Preferences.get(General.IS_LOGIN).equalsIgnoreCase("1")) {
            // User is logged in
            try {
                // Getting team logs id
                if (!isCall) {
                    if (messageData.has("type")) {
                        /*Here we are redirecting from push to group when user added in group
                         * added by rahul*/
                        String type = messageData.getString("type");
                        if (type.equals("groupMember")) {
                            String team_logs_id = messageData.getJSONObject("data").optString("team_logs_id");
                            intentMain.putExtra("team_logs_id", team_logs_id);
                            intentMain.putExtra("receiver", messageData.optString("receiver"));
                            intentMain.putExtra("sender", messageData.optString("sender"));
                            intentMain.putExtra("receiverType", messageData.optString("receiverType"));
                            intentMain.putExtra("username", "" + json.get("title"));
                            intentMain.putExtra("type", "groupMember");
                        } else {
                            String team_logs_id = messageData.getJSONObject("data").getJSONObject("metadata").optString("team_logs_id");
                            /*This is a broad cast for refreshing the unread messages*/
                            if (team_logs_id.equals("0")) {
                                if (IsFriendListingPage) {
                                    Intent intent = new Intent("ActionFriend");
                                    Bundle bundle = new Bundle();
                                    bundle.putString("sender", messageData.optString("sender"));
                                    intent.putExtras(bundle);
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                                }
                            } else if (team_logs_id.equals("")) {
                                if (IsGroupListingPage) {
                                    Intent intent = new Intent("ActionGroup");
                                    Bundle bundle = new Bundle();
                                    AppLog.i(TAG, "getIntent: group " + messageData.optString("receiver"));
                                    bundle.putString("sender", messageData.optString("receiver"));
                                    intent.putExtras(bundle);
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                                }
                            }
                            intentMain.putExtra("team_logs_id", team_logs_id);
                            intentMain.putExtra("receiver", messageData.optString("receiver"));
                            intentMain.putExtra("sender", messageData.optString("sender"));
                            intentMain.putExtra("receiverType", "" + messageData.optString("receiverType"));
                            intentMain.putExtra("username", "" + json.get("title"));
                            intentMain.putExtra("type", "");
                        }
                    }
//              intent.putExtra("json", (Parcelable) json);
                } else {
                    /*Here in this we are preparing for call push notification redirection
                     * on click of notification user will redirect on calling screen
                     * added by rahul maske*/
                    String team_logs_id = messageData.getJSONObject("data").getJSONObject("entities").getJSONObject("on")
                            .getJSONObject("entity").getJSONObject("data").getJSONObject("metadata").getString("team_logs_id");
                    String receiver = messageData.getString("receiver");

                    String sender = messageData.getJSONObject("data").getJSONObject("entities").
                            getJSONObject("by").getJSONObject("entity").getString("uid");

                    String lastActiveAt = messageData.getJSONObject("data").getJSONObject("entities").getJSONObject("by").getJSONObject("entity").getString("lastActiveAt");
                    String uid = messageData.getJSONObject("data").getJSONObject("entities").getJSONObject("by").getJSONObject("entity").getString("uid");
                    String role = messageData.getJSONObject("data").getJSONObject("entities").getJSONObject("by").getJSONObject("entity").getString("role");
                    String name = messageData.getJSONObject("data").getJSONObject("entities").getJSONObject("by").getJSONObject("entity").getString("name");
                    String avatar = messageData.getJSONObject("data").getJSONObject("entities").getJSONObject("by").getJSONObject("entity").getString("avatar");
                    String status = messageData.getJSONObject("data").getJSONObject("entities").getJSONObject("by").getJSONObject("entity").getString("status");
                    String sessionid = messageData.getJSONObject("data").getJSONObject("entities").getJSONObject("on").getJSONObject("entity").getString("sessionid");

                    String callType = messageData.getString("type");
                    AppLog.i(TAG, "getIntent: teamLogID " + team_logs_id + " receiver" + receiver + " sender" + sender);
                    /*intentMain.putExtra("receiver", ""+receiver);
                    intentMain.putExtra("sender", ""+sender);*/
                    intentMain.putExtra("lastActiveAt", "" + lastActiveAt);
                    intentMain.putExtra("uid", "" + uid);
                    intentMain.putExtra("role", "" + role);
                    intentMain.putExtra("name", "" + name);
                    intentMain.putExtra("avatar", "" + avatar);
                    intentMain.putExtra("status", "" + status);
                    intentMain.putExtra("callType", "" + callType);
                    intentMain.putExtra("sessionid", "" + sessionid);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else
            // User has not logged in. Send him to login screen
            intentMain = new Intent(this, LoginActivity.class);
        return PendingIntent.getActivity(this, REQUEST_CODE, intentMain, PendingIntent.FLAG_UPDATE_CURRENT);
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