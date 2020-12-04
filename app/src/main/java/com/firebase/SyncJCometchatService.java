package com.firebase;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.modules.cometchat_7_30.ChatroomFragment_;
import com.sagesurfer.constant.General;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.storage.database.Cometchat_log;
import com.storage.database.Sync;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

public class SyncJCometchatService extends JobService {
    private static final String TAG = SyncJCometchatService.class.getSimpleName();
    private Cometchat_log db;

    List<String> s = new ArrayList<>();
    ArrayList<String> list = new ArrayList<String>();
    JSONObject json = new JSONObject();
    JSONArray array = new JSONArray();

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        db = new Cometchat_log(this);

        Cursor cursor = db.getNames();
        if (cursor.moveToFirst()) {
            do {
               /* list.add(cursor.getString(cursor.getColumnIndex(Cometchat_log.CHAT_ID)));
                list.add(cursor.getString(cursor.getColumnIndex(Cometchat_log.FROM_CHAT)));
                list.add(cursor.getString(cursor.getColumnIndex(Cometchat_log.TO_CHAT)));
                list.add(cursor.getString(cursor.getColumnIndex(Cometchat_log.COMET_CHAT_TYPE)));
                list.add(cursor.getString(cursor.getColumnIndex(Cometchat_log.CHAT_MESSAGE_ID)));
                list.add(cursor.getString(cursor.getColumnIndex(Cometchat_log.CHAT_GROUP_ID)));
                list.add(cursor.getString(cursor.getColumnIndex(Cometchat_log.PRIVATE_GROUP_ID)));
                list.add(cursor.getString(cursor.getColumnIndex(Cometchat_log.CHAT_TYPE)));
                list.add(cursor.getString(cursor.getColumnIndex(Cometchat_log.READ_CHAT)));*/


                JSONObject data = new JSONObject();
                try {
                    data.put(Cometchat_log.CHAT_ID, cursor.getString(cursor.getColumnIndex(Cometchat_log.CHAT_ID)));
                    data.put(Cometchat_log.FROM_CHAT, cursor.getString(cursor.getColumnIndex(Cometchat_log.FROM_CHAT)));
                    data.put(Cometchat_log.TO_CHAT, cursor.getString(cursor.getColumnIndex(Cometchat_log.TO_CHAT)));
                    data.put(Cometchat_log.COMET_CHAT_TYPE, cursor.getString(cursor.getColumnIndex(Cometchat_log.COMET_CHAT_TYPE)));
                    data.put(Cometchat_log.CHAT_MESSAGE_ID, cursor.getString(cursor.getColumnIndex(Cometchat_log.CHAT_MESSAGE_ID)));
                    data.put(Cometchat_log.CHAT_GROUP_ID, cursor.getString(cursor.getColumnIndex(Cometchat_log.CHAT_GROUP_ID)));
                    data.put(Cometchat_log.PRIVATE_GROUP_ID, cursor.getString(cursor.getColumnIndex(Cometchat_log.PRIVATE_GROUP_ID)));
                    data.put(Cometchat_log.CHAT_TYPE, cursor.getString(cursor.getColumnIndex(Cometchat_log.CHAT_TYPE)));
                    data.put(Cometchat_log.READ_CHAT, cursor.getString(cursor.getColumnIndex(Cometchat_log.READ_CHAT)));
                    array.put(data);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());
        }

        try {
            json.put("chat_log", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Syscall("cometchat_log", json.toString());

        Log.e("SQLIT VAlUE", json.toString());
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }


    private void Syscall(String action, String data) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.ID, data);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this);
                Log.e("sysncdata", response);

                if (response != null) {

                    Toast.makeText(this, "Data updated successfully.", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
