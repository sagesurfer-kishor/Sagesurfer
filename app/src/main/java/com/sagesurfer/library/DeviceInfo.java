package com.sagesurfer.library;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

/*
 * Get complete device information in one string
 */

public class DeviceInfo {
    private static final String LINE_SEPARATOR = ",";

    public static String get(Activity activity) {

        return "DeviceId:" + getDeviceId(activity) + LINE_SEPARATOR +
                "IMEI:" + getImei(activity) + LINE_SEPARATOR +
                "isTablet:" + isTablet(activity.getApplicationContext()) + LINE_SEPARATOR +
                "Model:" + getDeviceName() + LINE_SEPARATOR +
                "Release:" + getVersion() + LINE_SEPARATOR + "OS:Android";
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

/*
    private static String getBrand() {
        return Build.BRAND;
    }

    private static String getDevice() {
        return Build.DEVICE;
    }

    private static String getModel() {
        return Build.MODEL;
    }

    private static String getBuildId() {
        return Build.ID;
    }

    private static String getProduct() {
        return Build.PRODUCT;
    }

    private static String getSdk() {
        return Build.VERSION.SDK;
    }

    */

    @SuppressWarnings("SameReturnValue")
    public static String getVersion() {
        return Build.VERSION.RELEASE;
    }

    private static boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    @SuppressLint("HardwareIds")
    private static String getDeviceId(Activity activity) {
        return Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    @SuppressLint("HardwareIds")
    public static String getDeviceMAC(Activity activity) {
        WifiManager wifiManager = (WifiManager) activity.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        assert wifiManager != null;
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        return wInfo.getMacAddress();
    }

    @SuppressLint("HardwareIds")
    public static String getImei(Activity activity) {
        TelephonyManager tManager = (TelephonyManager) activity.getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            //
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "NA";
        }
        if (tManager != null) {
            //tManager.getDeviceId().length();
            //String androidid = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
            //String l = tManager.getDeviceId();


            //return tManager.getDeviceId();
        }
        return "NA";
    }

    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context _context) {
        return Settings.Secure.getString(_context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getTimeZone() {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        return tz.getID();
    }

    @SuppressLint("HardwareIds")
    public static String getDeviceIMEI(Activity activity) {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            else
                //              deviceUniqueIdentifier = tm.getDeviceId();
                if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length())
                    deviceUniqueIdentifier = "0";
        }
        return deviceUniqueIdentifier;
    }
}
