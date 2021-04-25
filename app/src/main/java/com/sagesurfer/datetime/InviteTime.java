package com.sagesurfer.datetime;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

public class InviteTime {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String[] remainingTime(long time) {
        String[] remaining = new String[2];

        long now = System.currentTimeMillis();
        if (time < 1000000000000L) {
            time *= 1000;
        }
        if (time > now || time <= 0) {
            remaining[0] = "now";
            remaining[1] = "";
            return remaining;
        }
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            remaining[0] = "now";
            remaining[1] = "";
            return remaining;
        } else if (diff < 2 * MINUTE_MILLIS) {
            remaining[0] = "1 Min";
            remaining[1] = "Remaining";
            return remaining;
        } else if (diff < 50 * MINUTE_MILLIS) {
            remaining[0] = diff / MINUTE_MILLIS + " Min";
            remaining[1] = "Remaining";
            return remaining;
        } else if (diff < 90 * MINUTE_MILLIS) {
            remaining[0] = diff / MINUTE_MILLIS + "1 Hr";
            remaining[1] = "Remaining";
            return remaining;
        } else if (diff < 24 * HOUR_MILLIS) {
            remaining[0] = diff / HOUR_MILLIS + " Hr";
            remaining[1] = "Remaining";
            return remaining;
        } else if (diff < 48 * HOUR_MILLIS) {
            remaining[0] = "1 Day";
            remaining[1] = "Remaining";
            return remaining;
        } else if ((diff / DAY_MILLIS) > 3) {
            String[] dateArray = getDate(time).split(",");
            remaining[0] = getDateTh(dateArray[0]);
            remaining[1] = dateArray[1].trim();
            return remaining;
        } else {
            remaining[0] = diff / DAY_MILLIS + " Day";
            remaining[1] = "Remaining";
            return remaining;
        }
    }

    @SuppressLint("SimpleDateFormat")
    private static String getDate(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd, MMM yyyy");
        Date result_date = new Date(time);
        return sdf.format(result_date);
    }

    @SuppressLint("SimpleDateFormat")
    private static String getDateTh(String date) {
        if (date.endsWith("1") && !date.endsWith("11"))
            return Integer.parseInt(date) + "st";//new SimpleDateFormat("EE MMM d'st', yyyy");
        else if (date.endsWith("2") && !date.endsWith("12"))
            return Integer.parseInt(date) + "nd";//format = new SimpleDateFormat("EE MMM d'nd', yyyy");
        else if (date.endsWith("3") && !date.endsWith("13"))
            return Integer.parseInt(date) + "rd";//format = new SimpleDateFormat("EE MMM d'rd', yyyy");
        else
            return Integer.parseInt(date) + "th";//format = new SimpleDateFormat("EE MMM d'th', yyyy");
    }

}
