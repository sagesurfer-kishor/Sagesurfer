package com.modules.fms;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.DeviceInfo;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Error_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.secure._Base64;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.Preferences;

import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 01-08-2017
 *         Last Modified on 13-12-2017
 **/

/*
* This file is used to make all file operations like view,download,delete,etc
* Network call is made to perform respective file operations
*/

public class FileSharingOperations {

    private static final String TAG = FileSharingOperations.class.getSimpleName();

    public static String getView(String file_id, Context context, Activity activity) {
        String url = "";
        int status = 12;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.VIEW);
        requestMap.put(General.ID, file_id);
        RequestBody requestBody = NetworkCall_.make(requestMap, Preferences.get(General.DOMAIN)
                .replaceAll(General.INSATNCE_NAME, "/") + Urls_.FMS_DOWNLOAD_URL, TAG, context, activity);
        try {
            String response = NetworkCall_.post(Preferences.get(General.DOMAIN)
                    .replaceAll(General.INSATNCE_NAME, "/") + Urls_.FMS_DOWNLOAD_URL, requestBody, TAG, context, activity);
            if (response != null) {
                if (Error_.oauth(response, context) != 13) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    if (jsonObject.has(General.STATUS)) {
                        status = jsonObject.get(General.STATUS).getAsInt();
                        if (status == 1) {
                            url = jsonObject.get("url").getAsString(); //11/09/18 changed "link" to "url" acc. to the response
                            url = _Base64.decode(url);
                        }
                    } else {
                        status = 11;
                    }
                } else {
                    status = 13;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (status != 1) {
            switch (status) {
                case 2:
                    ShowToast.toast(context.getResources().getString(R.string.action_failed)
                            , context);
                    break;
                case 11:
                    ShowToast.internalErrorOccurred(context);
                    break;
                case 12:
                    ShowToast.networkError(context);
                    break;
                case 13:
                    ShowToast.toast(context.getResources().getString(R.string.authentication_failed)
                            , context);
                    break;
            }
        }
        return url;
    }


    public static String getDownload(String file_id, Context context, Activity activity) {
        String url = "";
        int status = 12;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DOWNLOAD);
        requestMap.put(General.ID, file_id);
        RequestBody requestBody = NetworkCall_.make(requestMap, Preferences.get(General.DOMAIN)
                .replaceAll(General.INSATNCE_NAME, "")
                + Urls_.FMS_DOWNLOAD_URL, TAG, context, activity);
        try {
            String response = NetworkCall_.post(Preferences.get(General.DOMAIN)
                            .replaceAll(General.INSATNCE_NAME, "") + Urls_.FMS_DOWNLOAD_URL, requestBody, TAG, context, activity);
            if (response != null) {
                if (Error_.oauth(response, context) != 13) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    if (jsonObject.has(General.STATUS)) {
                        status = jsonObject.get(General.STATUS).getAsInt();
                        if (status == 1) {
                            url = jsonObject.get(General.URL).getAsString();
                        }
                    } else {
                        status = 11;
                    }
                } else {
                    status = 13;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (status != 1) {
            switch (status) {
                case 2:
                    ShowToast.toast(context.getResources()
                            .getString(R.string.download_failed), context);
                    break;
                case 11:
                    ShowToast.internalErrorOccurred(context);
                    break;
                case 12:
                    ShowToast.networkError(context);
                    break;
                case 13:
                    ShowToast.toast(context.getResources().getString(R.string.authentication_failed)
                            , context);
                    break;
            }
        }
        return url;
    }

    public static int delete(int file_id, Context context, View view, Activity activity) {
        String url = Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "")
                + Urls_.FMS_DELETE_URL;
        int result = 12;

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));
        requestMap.put(General.ID, "" + file_id);
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, context, activity);
        try {
            String response = NetworkCall_.post(url, requestBody, TAG, context, activity);
            if (response != null) {
                //{"status":1}
                if (Error_.oauth(response, context) == 13) {
                    ShowSnack.viewWarning(view, context.getResources().getString(R.string.authentication_failed), context);
                } else {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    if (jsonObject.has(General.STATUS)) {
                        result = jsonObject.get(General.STATUS).getAsInt();
                    } else {
                        ShowSnack.viewWarning(view, context.getResources().getString(R.string.internal_error_occurred), context);
                    }
                }
            } else {
                ShowSnack.viewWarning(view, context.getResources().getString(R.string.authentication_failed), context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    static int markSpam(int file_id, Context context, View view, Activity activity) {
        int result = 12;
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_SPAM;

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));
        requestMap.put(General.ID, "" + file_id);
        requestMap.put(General.TYPE, General.FMS);

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, context, activity);
        try {
            String response = NetworkCall_.post(url, requestBody, TAG, context, activity);
            if (response != null) {
                //{"status":1}
                if (Error_.oauth(response, context) == 13) {
                    ShowSnack.viewWarning(view, context.getResources().getString(R.string.authentication_failed), context);
                } else {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    if (jsonObject.has(General.STATUS)) {
                        result = jsonObject.get(General.STATUS).getAsInt();
                    } else {
                        ShowSnack.viewWarning(view, context.getResources().getString(R.string.internal_error_occurred), context);
                    }
                }
            } else {
                ShowSnack.viewWarning(view, context.getResources().getString(R.string.authentication_failed), context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    static int deleteFolder(int folder_id, Context context, View view, Activity activity) {
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_FMS;
        int result = 12;

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, "delete_folder");
        requestMap.put(General.FOLDER_ID, "" + folder_id);
        requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, context, activity);

        try {
            String response = NetworkCall_.post(url, requestBody, TAG, context, activity);
            if (response != null) {
                if (Error_.oauth(response, context) == 13) {
                    ShowSnack.viewWarning(view, context.getResources()
                            .getString(R.string.authentication_failed), context);
                } else {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    if (jsonObject.has(General.STATUS)) {
                        result = jsonObject.get(General.STATUS).getAsInt();
                    } else {
                        ShowSnack.viewWarning(view, context.getResources()
                                .getString(R.string.internal_error_occurred), context);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    static int editFolder(String name, String folder_id,
                          String parent_id, String group_id, Activity activity) {
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_FMS;
        int result = 12;

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, "rename_folder");
        requestMap.put("parent_id", parent_id);
        requestMap.put(General.GROUP_ID, group_id);
        requestMap.put(General.FOLDER_ID, folder_id);
        requestMap.put(General.NAME, name);
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        try {
            String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
            if (response != null) {
                if (Error_.oauth(response, activity.getApplicationContext()) == 13) {
                    ShowToast.authenticationFailed(activity.getApplicationContext());
                } else {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    if (jsonObject.has(General.STATUS)) {
                        result = jsonObject.get(General.STATUS).getAsInt();
                    } else {
                        ShowToast.internalErrorOccurred(activity.getApplicationContext());
                    }
                }
            } else {
                result = 11;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    static int upload(String file_id, String group_id, String permission,
                      String default_permission, String folder,
                      String description,
                      String comment, String is_default, String action,
                      String start_time, Activity activity) {
        String info = DeviceInfo.get(activity);
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.ID, file_id);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, group_id);
        requestMap.put(General.PERMISSION, permission);
        requestMap.put("default_permission", default_permission);
        requestMap.put(General.FOLDER, folder);
        requestMap.put(General.DESCRIPTION, description);
        requestMap.put(General.COMMENT, comment);
        requestMap.put(General.IS_DEFAULT, is_default);
        requestMap.put(General.START_TIME, start_time);
        requestMap.put(General.END_TIME, GetTime.getChatTimestamp());
        requestMap.put(General.INFO, _Base64.encode(info));
        requestMap.put(General.IP, DeviceInfo.getDeviceMAC(activity));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_FMS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        try {
            String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
            if (response != null) {
                JsonObject jsonObject = GetJson_.getJson(response);
                if (jsonObject != null && jsonObject.has(General.STATUS)) {
                    return jsonObject.get(General.STATUS).getAsInt();
                }
            } else {
                return 12;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 11;
    }
}
