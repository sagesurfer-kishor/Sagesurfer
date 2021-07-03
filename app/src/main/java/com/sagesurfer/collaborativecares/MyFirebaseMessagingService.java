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
import android.util.Log;

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
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

                Log.d(TAG, "From: " + remoteMessage.getFrom());
        if(remoteMessage.getData().toString().contains("alert"))
        {
            // commet chat notification
            Log.d(TAG, "cometchat message: " + remoteMessage.getData());
        }else
        {
            // our normal notifcation
            Log.d(TAG, "normal message: " + remoteMessage.getData());
        }
    }





    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        Preferences.save("regId_save", false);
        Preferences.initialize(getApplicationContext());
        Preferences.save("regId", token);
        Preferences.save("device_token", token);
    }
}