package com.sagesurfer.library;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

public class TimeConverter {

    private static final String TAG = TimeConverter.class.getSimpleName();

    public static String getCurrentTime() {
        Long tsLong = System.currentTimeMillis() / 1000;
        return tsLong.toString();
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");//dd/MM/yyyy
        Date now = new Date();
        return sdfDate.format(now);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTime12Hrs(String time) {
        if (time != null && time.length() > 0) {
            DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
            Long timestamp = Long.parseLong(time);
            if (timestamp < 1000000000000L) {
                timestamp *= 1000;
            }
            Date result_date = new Date(timestamp);
            return dateFormat.format(result_date);
        } else {
            return "00:00:00";
        }
    }

    public static String getCurrentTime12Hrs() {
        String currentTime = getCurrentTime();
        String currentTime12Hrs = getTime12Hrs(currentTime);
        return currentTime12Hrs;
    }
}

