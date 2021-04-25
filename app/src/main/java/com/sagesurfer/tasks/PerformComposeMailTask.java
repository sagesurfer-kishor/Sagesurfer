package com.sagesurfer.tasks;

import android.app.Activity;

import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.storage.preferences.Preferences;

import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 20-07-2017
 * Last Modified on 15-12-2017
 */

public class PerformComposeMailTask {

    private static final String TAG = PerformComposeMailTask.class.getSimpleName();

    public static String delete(String msg_id, String id, String folder, Activity activity) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DELETE);
        requestMap.put(General.MSG_ID, msg_id);
        requestMap.put(General.ID, id);
        requestMap.put(General.TYPE, folder);
        return makeCall(requestMap, activity);
    }

    public static String compose(String to, String cc, String subject, String message,
                                 String attachment, String old_attachment, String msg_id, Activity activity) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.COMPOSE_MAIL);
        requestMap.put("to", to);
        requestMap.put("cc", cc);
        requestMap.put(General.MSG_ID, msg_id);
        requestMap.put(General.SUBJECT, subject);
        requestMap.put(General.MSG, message);
        requestMap.put(General.ATTACHMENT, old_attachment);
        requestMap.put("new_attachment", attachment);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        return makeCall(requestMap, activity);
    }

    public static String draft(String to, String cc, String subject, String message,
                               String attachment, String msg_id, String old_attachment, Activity activity) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DRAFT);
        requestMap.put("to", to);
        requestMap.put("cc", cc);
        requestMap.put(General.MSG_ID, msg_id);
        requestMap.put(General.SUBJECT, subject);
        requestMap.put(General.MSG, message);
        requestMap.put(General.ATTACHMENT, old_attachment);
        requestMap.put("new_attachment", attachment);
        return makeCall(requestMap, activity);
    }

    public static String reply(String to, String cc, String subject, String message, String attachment,
                               String msg_id, Activity activity) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.REPLY);
        requestMap.put("to", to);
        requestMap.put("cc", cc);
        requestMap.put("mid", msg_id);
        requestMap.put(General.SUBJECT, subject);
        requestMap.put(General.MSG, message);
        requestMap.put(General.ATTACHMENT, attachment);
        return makeCall(requestMap, activity);
    }

    public static String reply_all(String to, String cc, String subject, String message,
                                   String attachment, String mid, Activity activity) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.REPLY_ALL);
        requestMap.put("to", to);
        requestMap.put("cc", cc);
        requestMap.put("mid", mid);
        requestMap.put(General.SUBJECT, subject);
        requestMap.put(General.MSG, message);
        requestMap.put(General.ATTACHMENT, attachment);
        return makeCall(requestMap, activity);
    }

    public static String forward(String to, String cc, String subject, String message, String attachment,
                                 String mid, String old_attachment, Activity activity) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.FORWARD);
        requestMap.put("to", to);
        requestMap.put("cc", cc);
        requestMap.put("mid", mid);
        requestMap.put(General.SUBJECT, subject);
        requestMap.put(General.MSG, message);
        requestMap.put(General.ATTACHMENT, old_attachment);
        requestMap.put("new_attachment", attachment);
        return makeCall(requestMap, activity);
    }

    private static String makeCall(HashMap<String, String> requestMap, Activity activity) {
        String response = null;
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_POSTCARD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return response;
    }
}
