package com.sagesurfer.tasks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.network.MakeCall;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.secure.KeyMaker_;

import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

public class PerformServerTask extends AsyncTask<Context, Void, String> {

    private static final String TAG = PerformServerTask.class.getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private final Activity activity;

    public PerformServerTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Context... params) {
        Context _context = params[0];

        HashMap<String, String> keyMap = KeyMaker_.getKey();
        RequestBody formBody = new FormBody.Builder()
                .add(General.KEY, keyMap.get(General.KEY))
                .add(General.TOKEN, keyMap.get(General.TOKEN))
                .add(General.ACTION, Actions_.GET_INSTANCES)
                .add(General.VERSION, General.INSTANCE_VERSION)
                .build();
        try {
            return MakeCall.post(Urls_.SERVER_LIST_URL, formBody, TAG, _context, activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
