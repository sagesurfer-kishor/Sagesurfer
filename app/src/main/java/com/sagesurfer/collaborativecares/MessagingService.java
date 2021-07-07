package com.sagesurfer.collaborativecares;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.helpers.CometChatHelper;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.User;
import com.firebase.CallNotificationAction;
import com.firebase.Config;
import com.firebase.NotificationUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.modules.cometchat_7_30.LastConversion.FragmentLastConversation;
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

import constant.StringContract;
import utils.Utils;

/**
 * @author Kailash Karankal
 **/
/*
 * This file contains firebase messaging service to get firabase push messages
 * Methods is added to parse and show push notifications code co
 */
public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = MessagingService.class.getSimpleName();
    private NotificationUtils notificationUtils;
    private JSONObject json;
    private int count = 0;
    private Call call;
    Intent intentMain;
    Intent callingIntent;
    JSONObject messageData;
    public static String token;
    private static final int REQUEST_CODE = 12;
    private boolean isCall;

    private boolean isChatScreen, IsFriendListingPage, IsGroupListingPage;
    SharedPreferences preferencesCheckCurrentActivity;
    SharedPreferences.Editor editor;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        /* this preferences used to know that chat screen is open or not if it is open we are not going to show notification*/
        preferencesCheckCurrentActivity = getSharedPreferences("preferencesCheckCurrentActivity", MODE_PRIVATE);
        isChatScreen = preferencesCheckCurrentActivity.getBoolean("IsChatScreen", false);
        IsFriendListingPage = preferencesCheckCurrentActivity.getBoolean("IsFriendListingPage", false);
        IsGroupListingPage = preferencesCheckCurrentActivity.getBoolean("IsGroupListingPage", false);
        intentMain = new Intent(this, MainActivity.class);
        callingIntent = new Intent(this, FragmentLastConversation.class);

        Log.e(TAG , " notification received.. "+ remoteMessage.toString());
        Logger.error("Debug", "Firebase Notification payload : " + remoteMessage.getData().toString(), getApplicationContext());
        Preferences.save(General.IS_PUSH_NOTIFICATION_SENT, false);

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload
        if (remoteMessage.getData().size() > 0) {
            try {
                if (remoteMessage.getData().containsKey("action")) {
                    // if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                    Logger.error("Debug", "Firebase Notification payload11 : " + remoteMessage.getData().toString(), getApplicationContext());
                    //  }
                } else {
                    Logger.error("Debug", "Firebase Notification payload12 : " + remoteMessage.getData().toString(), getApplicationContext());
                    JSONObject json = new JSONObject(remoteMessage.getData());
                    Log.i(TAG, "onMessageReceived: data " + json.toString());
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

            Log.e(TAG, "received push notification JSONObject: " + messageData);

            BaseMessage baseMessage = CometChatHelper.processMessage(new JSONObject(remoteMessage.getData().get("message")));
            if (baseMessage instanceof Call) {
                call = (Call) baseMessage;
                isCall = true;
                //if (!AppInfo.isAppRunning(getApplicationContext(), "com.sagesurfer.collaborativecares")) {
                showNotifcation(baseMessage);
                //}
            } else {

                showNotifcation(baseMessage);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG, "onMessageReceived: error " + e.getMessage());
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

    /*public static void subscribeGroupNotification(String GUID) {
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
    }*/

    @Override
    public void onNewToken(String s) {
        token = s;
        Log.d(TAG, "onNewToken: " + s);
    }


    private void handleNotification(String message) {
        Log.d(TAG, "handleNotification: ");
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
        Log.d(TAG, "handleDataMessage" + json);
        try {
            JSONObject data = json.getJSONObject("data");
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
        Log.e(TAG, "showNotificationMessage: ");
        if (!isChatScreen) {
            notificationUtils = new NotificationUtils(context);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //timeStamp is notification id to make notification stack
            notificationUtils.showNotificationMessage(title, message, timeStamp, type, intent);
        } else {
            Log.i(TAG, "showNotificationMessage: Is chat screen");
        }
    }

    // Show notifications with image or image with text
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, String type, Intent intent, String imageUrl) {
        if (!isChatScreen) {
            notificationUtils = new NotificationUtils(context);
            Log.e(TAG, "showNotificationMessageWithBigImage: ");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notificationUtils.showNotificationMessage(title, message, timeStamp, type, intent, imageUrl);
        } else {
            Log.i(TAG, "showNotificationMessageWithBigImage: Is chat screen");
        }
    }

    private void showNotifcation(BaseMessage baseMessage) {
        try {
            if (!isChatScreen) {
                Log.e(TAG, "showNotifcation: " + json.toString());
                int m = (int) ((new Date().getTime()));
                String GROUP_ID = "group_id";
                NotificationCompat.Builder builder;

                builder = new NotificationCompat.Builder(this, "2")
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
                    Utils.startCallIntent(getApplicationContext(), (User) call.getCallInitiator(), call.getType(), false, call.getSessionId());
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
                Log.i(TAG, "showNotifcation: Is chat screen");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "showNotifcation: error 2" + e.getMessage());

        }
    }

    private Intent getCallIntent(String title) {
        Log.e(TAG, "getCallIntent: ");
        Intent callIntent = new Intent(getApplicationContext(), CallNotificationAction.class);
        callIntent.putExtra(StringContract.IntentStrings.SESSION_ID, call.getSessionId());
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.setAction(title);
        return callIntent;
    }

    private PendingIntent getIntent() {
        if (Preferences.contains(General.IS_LOGIN) && Preferences.get(General.IS_LOGIN).equalsIgnoreCase("1")) {
            try {
                if (!isCall) {
                    if (messageData.has("type")) {
                        /*Here we are redirecting from push to group when user added in group
                         * added by rahul*/
                        String type = messageData.getString("type");
                        Log.i(TAG, "getIntent: type  " + messageData.getString("type"));
                        if (type.equals("groupMember")) {
                            String team_logs_id = messageData.getJSONObject("data").optString("team_logs_id");
                            intentMain.putExtra("team_logs_id", team_logs_id);
                            intentMain.putExtra("receiver", messageData.optString("receiver"));
                            intentMain.putExtra("sender", messageData.optString("sender"));
                            intentMain.putExtra("receiverType", messageData.optString("receiverType"));
                            intentMain.putExtra("username", "" + json.get("title"));
                            intentMain.putExtra("type", "groupMember");
                        } else if (type.equals("extension_whiteboard")) {
                            Log.i(TAG, "getIntent: extension_whiteboard block");
                            String team_logs_id = messageData.getJSONObject("data").optString("team_logs_id");
                            intentMain.putExtra("team_logs_id ", team_logs_id);
                            intentMain.putExtra("receiver ", messageData.optString("receiver"));
                            intentMain.putExtra("sender ", messageData.optString("sender"));
                            intentMain.putExtra("receiverType ", messageData.optString("receiverType"));
                            intentMain.putExtra("username ", "" + json.get("title"));
                            intentMain.putExtra("type ", type);
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
                                    Log.i(TAG, "getIntent: group " + messageData.optString("receiver"));
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
                    String category = messageData.getString("category");
                    Log.i(TAG, "getIntent: teamLogID " + team_logs_id + " receiver" + receiver + " sender" + sender);

                    intentMain.putExtra("receiver", "" + receiver);
                    intentMain.putExtra("sender", "" + sender);
                    intentMain.putExtra("lastActiveAt", "" + lastActiveAt);
                    intentMain.putExtra("uid", "" + uid);
                    intentMain.putExtra("role", "" + role);
                    intentMain.putExtra("name", "" + name);
                    intentMain.putExtra("avatar", "" + avatar);
                    intentMain.putExtra("status", "" + status);
                    intentMain.putExtra("callType", "" + callType);
                    intentMain.putExtra("sessionid", "" + sessionid);
                    intentMain.putExtra("category", "" + category);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else
            // User has not logged in. Send him to login screen
            intentMain = new Intent(this, LoginActivity.class);
        return PendingIntent.getActivity(this, REQUEST_CODE, intentMain, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    /*was created for call redirection but later cancel to use and use old intent*/
    private PendingIntent callPendingIntent() {
        if (Preferences.contains(General.IS_LOGIN) && Preferences.get(General.IS_LOGIN).equalsIgnoreCase("1")) {
            String team_logs_id = null;
            try {
                team_logs_id = messageData.getJSONObject("data").getJSONObject("entities").getJSONObject("on")
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
                String category = messageData.getString("category");
                Log.i(TAG, "getIntent: teamLogID " + team_logs_id + " receiver" + receiver + " sender" + sender);

                callingIntent.putExtra("receiver", "" + receiver);
                callingIntent.putExtra("sender", "" + sender);
                callingIntent.putExtra("lastActiveAt", "" + lastActiveAt);
                callingIntent.putExtra("uid", "" + uid);
                callingIntent.putExtra("role", "" + role);
                callingIntent.putExtra("name", "" + name);
                callingIntent.putExtra("avatar", "" + avatar);
                callingIntent.putExtra("status", "" + status);
                callingIntent.putExtra("callType", "" + callType);
                callingIntent.putExtra("sessionid", "" + sessionid);
                callingIntent.putExtra("category", "" + category);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else
            callingIntent = new Intent(this, LoginActivity.class);
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
