package com.firebase;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.sagesurfer.collaborativecares.MainActivity;

import constant.StringContract;
import screen.CometChatStartCallActivity;
import utils.Utils;

public class CallNotificationAction extends BroadcastReceiver {

    String TAG = "CallNotificationAction";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: call");
        String sessionID = intent.getStringExtra(StringContract.IntentStrings.SESSION_ID);
        Log.e(TAG, "onReceive: " + intent.getStringExtra(StringContract.IntentStrings.SESSION_ID));
        if (intent.getAction().equals("Answers")) {
            CometChat.acceptCall(sessionID, new CometChat.CallbackListener<Call>() {
                @Override
                public void onSuccess(Call call) {
                    Log.e(TAG, "onSuccess: ");
                    /*Intent acceptIntent = new Intent(context, MainActivity.class);
                    acceptIntent.putExtra(StringContract.IntentStrings.SESSION_ID, call.getSessionId());
                    acceptIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(acceptIntent);*/

                    /*this below commented code was not working and was not redirecting to exact calling page so this code is commented
                    commented by rahul maske and written new code below that is working..
                    */

                    Intent intent = new Intent(context, CometChatStartCallActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(StringContract.IntentStrings.SESSION_ID,call.getSessionId());
                    //((Activity)context).finish();
                    context.startActivity(intent);
                }

                @Override
                public void onError(CometChatException e) {
                    //Toast.makeText(context, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onError: "+ e.getMessage() );
                }
            });
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(05);
        } else if (intent.getAction().equals("Decline")){
            CometChat.rejectCall(sessionID, CometChatConstants.CALL_STATUS_REJECTED, new CometChat.CallbackListener<Call>() {
                @Override
                public void onSuccess(Call call) {
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    notificationManager.cancel(05);
                    Log.e(TAG, "onSuccess: ");
                }

                @Override
                public void onError(CometChatException e) {
                    Log.e(TAG, "onError: "+e.getMessage() );
                }
            });
        }else{
            Log.i(TAG, "onReceive: notification clicked");
        }
    }
}
