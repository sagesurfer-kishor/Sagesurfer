package com.sagesurfer.tasks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.CometChatExtension;
import com.cometchat.pro.exceptions.CometChatException;
import com.firebase.MessagingService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sagesurfer.collaborativecares.Application;
import com.sagesurfer.collaborativecares.LoginActivity;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.DeviceInfo;
import com.sagesurfer.network.AppConfig;
import com.sagesurfer.network.MakeCall;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.secure.KeyMaker_;
import com.storage.preferences.Preferences;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


import okhttp3.FormBody;
import okhttp3.RequestBody;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

public class PerformLogoutTask {

    private static final String TAG = PerformLogoutTask.class.getSimpleName();
    private static SharedPreferences loginPreferences;
    private static SharedPreferences.Editor loginPrefsEditor;
    private static SharedPreferences sp;
    private static SharedPreferences.Editor spEditor;

    public static void logout(Activity activity) {
        Logout logout = new Logout(activity);
        logout.execute();
    }

    private static class Logout extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog mProgressDialog;
        @SuppressLint("StaticFieldLeak")
        final Activity activity;

        Logout(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(activity);
            }

            // for quote layout showing
            Preferences.save("showQuote", false);
            mProgressDialog.setMessage("Logout Successfully");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HashMap<String, String> keyMap = KeyMaker_.getKey();
            RequestBody logoutBody = new FormBody.Builder()
                    .add(General.USER_ID, Preferences.get(General.USER_ID))
                    .add(General.KEY, keyMap.get(General.KEY))
                    .add(General.TOKEN, keyMap.get(General.TOKEN))
                    .add(General.IMEI, DeviceInfo.getDeviceId(activity))
                    .build();
            try {
                MakeCall.post(Preferences.get(General.DOMAIN) + "/" + Urls_.LOGOUT_URL, logoutBody, TAG, activity.getApplicationContext(), activity);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            safeLogout(activity);

            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            if (mProgressDialog != null) {
                mProgressDialog.cancel();
            }
        }
    }

    // reset shared preferences values to logout state
    private static void safeLogout(Activity activity) {

        /*cometchat account login for particular user
         *code added by rahulmsk*/

        FirebaseMessaging.getInstance().unsubscribeFromTopic(AppConfig.AppDetails.APP_ID + "_" + CometChatConstants.RECEIVER_TYPE_USER + "_" +
                Preferences.get(General.COMET_CHAT_ID)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, Preferences.get(General.COMET_CHAT_ID) + " Unsubscribed Success");

                CometChat.logout(new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.i(TAG, "cometchat logout onSuccess: ");
                    }

                    @Override
                    public void onError(CometChatException e) {
                        Log.i(TAG, "cometchat logout failed: " + e.getMessage());
                    }
                });
            }
        });


        //MessagingService.unsubscribeUserNotification(Preferences.get(General.COMET_CHAT_ID));
        //MessagingService.unsubscribeGroupNotification(Preferences.get(General.COMET_CHAT_ID));
        Preferences.clear();

        /*Preferences.removeKey("regId");
        Preferences.removeKey(General.PASSWORD);
        Preferences.removeKey(General.USER_ID);
        Preferences.removeKey(General.TIMEZONE);
        Preferences.removeKey(General.NAME);
        Preferences.removeKey(General.FIRST_NAME);
        Preferences.removeKey(General.LAST_NAME);
        Preferences.removeKey(General.USERNAME);
        Preferences.removeKey(General.EMAIL);
        Preferences.removeKey(General.ROLE);
        Preferences.removeKey(General.ROLE_ID);
        Preferences.removeKey(General.URL_IMAGE);
        Preferences.removeKey(General.LOCAL_IMAGE);
        Preferences.removeKey(General.GROUP_ID);
        Preferences.removeKey(General.GROUP_NAME);
        Preferences.removeKey(General.IS_REVIEWER);
        Preferences.removeKey(General.API_KEY);
        Preferences.removeKey(General.LAST_UPDATED);
        Preferences.removeKey(General.DOMAIN);
        Preferences.removeKey(General.DOMAIN_CODE);
        Preferences.removeKey(General.CHAT_URL);
        Preferences.removeKey(General.IS_LANDING_QUESTION_FILLED);
        Preferences.removeKey(General.SHOW_BEHAVIOURAL_FILLED);
        Preferences.removeKey(General.IS_FORM_SYNC_LANDING_QUESTION);
        Preferences.removeKey(General.OWNER_ID);*/
        //screen.messagelist.Preferences.clear();


        loginPreferences = activity.getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        loginPrefsEditor.clear();
        loginPrefsEditor.apply();

        sp = activity.getSharedPreferences("login", MODE_PRIVATE);
        spEditor = sp.edit();
        spEditor.clear();
        spEditor.apply();

        Preferences.save(General.IS_LOGIN, 0);
        Preferences.save(General.IS_COMETCHAT_LOGIN_SUCCESS, false);
        Application.getInstance().clearApplicationData();

        Intent loginIntent = new Intent(activity.getApplicationContext(), LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(loginIntent);
        activity.finish();
    }
}
