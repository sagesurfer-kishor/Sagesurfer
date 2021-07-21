package com.sagesurfer.collaborativecares;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.modules.caseload.observer.AppObserver;
import com.sagesurfer.directory.DirectoryList;
import com.sagesurfer.network.AppConfig;
import java.io.File;
import constant.StringContract;
import listeners.CometChatCallListener;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.storage.preferences.Preferences;
import com.storage.preferences.StorageHelper;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Kailash on 7/18/2018.
 */

public class Application extends MultiDexApplication {
    public static final String TAG = Application.class.getSimpleName();

    private static Application instance;
    private static AppObserver observer;

    private FirebaseCrashlytics crashlytics;

    @Override
    protected void attachBaseContext(Context newBase) {
        MultiDex.install(newBase);
        super.attachBaseContext(newBase);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        observer = new AppObserver(this);

        if (!BuildConfig.DEBUG) {
            crashlytics = FirebaseCrashlytics.getInstance();
            crashlytics.log("Start logging!");
        }

        FirebaseMessaging.getInstance().subscribeToTopic("global");

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "FirebaseMessaging failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult();
                        //StorageHelper.setTOKEN(getApplicationContext(), token);
                        Log.i(Application.class.getSimpleName(), "Token: " + token);

                        StorageHelper.setTOKEN(getApplicationContext(), token);

                        Preferences.initialize(getApplicationContext());
                        Preferences.save("regId_save", false);
                        Preferences.save("regId", token);

                    }
                });

        //createNotificationChannel();

        AppSettings appSettings = new AppSettings.AppSettingsBuilder().
                subscribePresenceForAllUsers().setRegion(AppConfig.AppDetails.REGION).build();
        CometChat.init(this, AppConfig.AppDetails.APP_ID, appSettings,
                new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        StringContract.AppInfo.API_KEY = AppConfig.AppDetails.API_KEY;
                        CometChat.setSource("ui-kit", "android", "java");
                        Log.d(TAG, "onSuccess: " + s);
                    }

                    @Override
                    public void onError(CometChatException e) {
                        Toast.makeText(Application.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        CometChatCallListener.addCallListener(TAG, this);



    }

    public AppObserver getObserver() {
        return observer;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static synchronized Application getInstance() {
        if (instance == null) {
            instance = new Application();
            if (observer == null) {
                observer = new AppObserver(instance);
            }
        }
        return instance;
    }

    public void clearApplicationData() {
        File root = new File(DirectoryList.DIR_MAIN);
        if (root.exists()) {
            String[] children = root.list();
            if (children != null) {
                for (String s : children) {
                    if (!s.equals("lib")) {
                        deleteDir(new File(root, s));
                        Log.i("TAG", "File /Collaborative Care/" + s + " DELETED");
                    }
                }
            } else {
                Log.e("TAG", "LogOut Successfully");
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.channel);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("2", name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}