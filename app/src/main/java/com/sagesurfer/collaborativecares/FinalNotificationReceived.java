package com.sagesurfer.collaborativecares;

import android.util.Log;

import androidx.annotation.NonNull;

import com.cometchat.pro.helpers.CometChatHelper;
import com.cometchat.pro.models.BaseMessage;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class FinalNotificationReceived extends FirebaseMessagingService {
    private static final String TAG = "FinalNotificationReceiv";
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
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

       /* try {


           *//* if (remoteMessage.getData().optString("receiverType"))
            BaseMessage baseMessage = CometChatHelper.processMessage(new JSONObject(remoteMessage.getData().get("message")));
*//*
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onNewToken(@NonNull String s) {
        Log.d(TAG, "Refreshed token: " + s);
    }
}
