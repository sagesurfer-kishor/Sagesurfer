package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.modules.notification.Notification;
import com.sagesurfer.constant.Actions_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Kailash Karankal
 * Created on 21-12-2017
 * Last Modified on 21-12-2017
 */

public class Notifications_ {

    public static ArrayList<Notification> parseSpams(String response, Context _context, String TAG) {
        ArrayList<Notification> notificationArrayList = new ArrayList<>();
        if (response == null) {
            Notification notification = new Notification();
            notification.setStatus(12);
            notificationArrayList.add(notification);
            return notificationArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Notification notification = new Notification();
            notification.setStatus(13);
            notificationArrayList.add(notification);
            return notificationArrayList;
        }
        if (Error_.noData(response, Actions_.NOTIFICATION, _context) == 2) {
            Notification notification = new Notification();
            notification.setStatus(2);
            notificationArrayList.add(notification);
            return notificationArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, Actions_.NOTIFICATION);

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Notification>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, Actions_.NOTIFICATION).toString(), listType);
        } else {
            Notification notification = new Notification();
            notification.setStatus(11);
            notificationArrayList.add(notification);
            return notificationArrayList;
        }
    }


    public static ArrayList<Notification> filterNotificationData(String response, Context _context, String TAG) {
        ArrayList<Notification> notificationArrayList = new ArrayList<>();
        if (response == null) {
            Notification notification = new Notification();
            notification.setStatus(12);
            notificationArrayList.add(notification);
            return notificationArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Notification notification = new Notification();
            notification.setStatus(13);
            notificationArrayList.add(notification);
            return notificationArrayList;
        }
        if (Error_.noData(response, Actions_.NOTIFICATION, _context) == 2) {
            Notification notification = new Notification();
            notification.setStatus(2);
            notificationArrayList.add(notification);
            return notificationArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, Actions_.NOTIFICATION);

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Notification>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, Actions_.NOTIFICATION).toString(), listType);
        } else {
            Notification notification = new Notification();
            notification.setStatus(11);
            notificationArrayList.add(notification);
            return notificationArrayList;
        }
    }

    public static ArrayList<Notification> filterNotificationDateWiseData(String response, Context _context, String TAG) {
        ArrayList<Notification> notificationArrayList = new ArrayList<>();
        if (response == null) {
            Notification notification = new Notification();
            notification.setStatus(12);
            notificationArrayList.add(notification);
            return notificationArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Notification notification = new Notification();
            notification.setStatus(13);
            notificationArrayList.add(notification);
            return notificationArrayList;
        }
        if (Error_.noData(response, Actions_.NOTIFICATION, _context) == 2) {
            Notification notification = new Notification();
            notification.setStatus(2);
            notificationArrayList.add(notification);
            return notificationArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, Actions_.NOTIFICATION);

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Notification>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, Actions_.NOTIFICATION).toString(), listType);
        } else {
            Notification notification = new Notification();
            notification.setStatus(11);
            notificationArrayList.add(notification);
            return notificationArrayList;
        }
    }


    public static ArrayList<Notification> parseNotificationData(String response, Context _context, String TAG) {
        ArrayList<Notification> notificationArrayList = new ArrayList<>();
        if (response == null) {
            Notification notification = new Notification();
            notification.setStatus(12);
            notificationArrayList.add(notification);
            return notificationArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Notification notification = new Notification();
            notification.setStatus(13);
            notificationArrayList.add(notification);
            return notificationArrayList;
        }
        if (Error_.noData(response, Actions_.NOTIFICATION_FILTER, _context) == 2) {
            Notification notification = new Notification();
            notification.setStatus(2);
            notificationArrayList.add(notification);
            return notificationArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, Actions_.NOTIFICATION_FILTER);

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Notification>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, Actions_.NOTIFICATION_FILTER).toString(), listType);
        } else {
            Notification notification = new Notification();
            notification.setStatus(11);
            notificationArrayList.add(notification);
            return notificationArrayList;
        }
    }

}
