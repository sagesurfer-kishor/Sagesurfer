package com.sagesurfer.tasks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.icu.util.ULocale;
import android.os.AsyncTask;

import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.DeviceInfo;
import com.sagesurfer.network.MakeCall;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.secure.KeyMaker_;
import com.sagesurfer.secure._Base64;
import com.storage.preferences.Preferences;

import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

public class PerformLoginTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = PerformLoginTask.class.getSimpleName();
    private final String username;
    private final String password;
    private final String start_time;
    @SuppressLint("StaticFieldLeak")
    private final Activity activity;

    public PerformLoginTask(String username, String password, String start_time, Activity activity) {
        this.username = username;
        this.password = password;
        this.start_time = start_time;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Void... params) {
        HashMap<String, String> keyMap = KeyMaker_.getKey();
        String info = DeviceInfo.get(activity);
        RequestBody loginBody = null;
        try {
            loginBody = new FormBody.Builder()
                    .add(General.KEY, keyMap.get(General.KEY))
                    .add(General.TOKEN, keyMap.get(General.TOKEN))
                    .add(General.TIMEZONE, DeviceInfo.getTimeZone())
                    .add(General.DEVICE, "a")
                    .add(General.INFO, _Base64.encode(info))
                    .add(General.UID, DeviceInfo.getDeviceId(activity.getApplicationContext()))
                    .add(General.USERNAME, username)
                    .add(General.PASSWORD, password)
                    .add(General.START_TIME, start_time)
                    .add(General.END_TIME, GetTime.getChatTimestamp())
                    .add(General.IP, DeviceInfo.getDeviceMAC(activity))
                    .add(General.IMEI, DeviceInfo.getDeviceId(activity))
                    .add(General.DOMAIN_CODE, Preferences.get(General.DOMAIN_CODE))
                    .add(General.MODELNO, DeviceInfo.getDeviceName())
                    .add("version", activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName)
                    .add("country", activity.getResources().getConfiguration().locale.getCountry())
                    .add("city", "")
                    .build();


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        try {
            return MakeCall.post(Preferences.get(General.DOMAIN) + Urls_.LOGIN_URL, loginBody, TAG, activity.getApplicationContext(), activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
