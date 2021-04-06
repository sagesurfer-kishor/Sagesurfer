package com.sagesurfer.collaborativecares;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;


import androidx.appcompat.app.AppCompatActivity;

import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.Settings.UIKitSettings;
import com.sagesurfer.constant.Chat;
import com.sagesurfer.constant.General;
import com.sagesurfer.directory.MakeDirectory;
import com.sagesurfer.network.AppConfig;
import com.storage.preferences.Preferences;

import org.json.JSONObject;


/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initializeCometChat();
    }

    private void initializeCometChat() {
        AppSettings appSettings=new AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(AppConfig.AppDetails.REGION).build();

        CometChat.init(this, AppConfig.AppDetails.APP_ID,appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successMessage) {
                UIKitSettings.setAPIKey(AppConfig.AppDetails.API_KEY);
                CometChat.setSource("ui-kit","android","java");
                Log.d(TAG, "Initialization completed successfully");
            }
            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Initialization failed with exception: " + e.getMessage());
            }
        });
    }

    //enable or display log print
    // 1 ==  enable
    // 1 !=  disable
    private void logPrint(int status) {
        Preferences.initialize(getApplicationContext());
        Preferences.save("debug", "" + status);
        Preferences.save("info", "" + status);
        Preferences.save("verbose", "" + status);
        Preferences.save("error", "" + status);
    }

    @Override
    protected void onStart() {
        super.onStart();
        logPrint(1);

        new Handler().postDelayed(() -> {
            if (Preferences.contains(General.IS_LOGIN) &&
                    Preferences.get(General.IS_LOGIN).equalsIgnoreCase("1")) {
                mainIntent();
            } else {
                loginIntent();
            }
        }, 2000);
    }

    private void mainIntent() {
        MakeDirectory.makeDirectories();
        if (Preferences.getBoolean(General.IS_COMETCHAT_LOGIN_SUCCESS)) {
            String domainCode=Preferences.get(General.DOMAIN_CODE);
            if (domainCode.equalsIgnoreCase("sage013")
                    || domainCode.equalsIgnoreCase("sage016")
                    || domainCode.equalsIgnoreCase("sage021")
                    || domainCode.equalsIgnoreCase("sage023")
                    || domainCode.equalsIgnoreCase("sage024")
                    || domainCode.equalsIgnoreCase("sage008")
                    || domainCode.equalsIgnoreCase("sage006")
                    || domainCode.equalsIgnoreCase("sage026")
                    || domainCode.equalsIgnoreCase("sage027")
                    || domainCode.equalsIgnoreCase("sage036")) {
                Preferences.save(General.IS_COMETCHAT_LOGIN_SUCCESS, true);
                moveToDashboard();
            } else {
                try {

                    String Uid = Preferences.get(General.COMET_CHAT_ID);

                    CometChat.login(Uid, AppConfig.AppDetails.API_KEY, new CometChat.CallbackListener<User>() {

                        @Override
                        public void onSuccess(User user) {
                            Log.d(TAG, "Login Successful : " + user.toString());
                            moveToDashboard();

                        }

                        @Override
                        public void onError(CometChatException e) {
                            Log.d(TAG, "Login failed with exception: " + e.getMessage());
                            Preferences.save(General.IS_COMETCHAT_LOGIN_SUCCESS, false);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    Preferences.save(General.IS_COMETCHAT_LOGIN_SUCCESS, false);
                    moveToDashboard();
                }
            }
        } else {
            moveToDashboard();
        }
    }

    private void moveToDashboard() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        if (getIntent().getExtras() != null) {
            // App has data from bundle. Need to check if its from push notification
            Bundle bundle = getIntent().getExtras();
            //bundle must contain all info sent in "data" field of the notification
            if (bundle.getString("message") != null && !bundle.getString("message").isEmpty()) {
                try {
                    JSONObject messageData = new JSONObject(bundle.getString("message"));
                    String team_logs_id = messageData.getJSONObject("data").getJSONObject("metadata").optString("team_logs_id");
                    intent.putExtra("team_logs_id", team_logs_id);
                    intent.putExtra("receiver", messageData.optString("receiver"));
                    intent.putExtra("sender", messageData.optString("sender"));
                    intent.putExtra("receiverType", messageData.optString("receiverType"));
                } catch (Exception ignored) {

                }
            }
        }
        startActivity(intent);
        finish();
    }

    private void loginIntent() {
        Preferences.save(General.IS_LANDING_QUESTION_FILLED, true);
        Preferences.save(General.IS_FORM_SYNC_LANDING_QUESTION, false);

        Intent mainIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
