package com.sagesurfer.oauth;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.sagesurfer.constant.Oauth;
import com.sagesurfer.models.Token_;
import com.sagesurfer.network.Call_;
import com.sagesurfer.network.Urls_;
import com.storage.preferences.OauthPreferences;
import com.utils.AppLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

// handle refresh token com.sagesurfer.network call

public class RefreshToken {

    private static final String TAG = RefreshToken.class.getSimpleName();

    public RefreshToken(Context _context) {
        OauthPreferences.initialize(_context);
    }

    public Token_ getRefreshToken(String user_name, String password, String domain, Context _context) {
        AppLog.i(TAG, "getRefreshToken: user_name "+user_name);
        AppLog.i(TAG, "getRefreshToken: password "+password);
        AppLog.i(TAG, "getRefreshToken: domain "+domain);
        AppLog.i(TAG, "getRefreshToken: refresh_token "+OauthPreferences.get(Oauth.REFRESH_TOKEN));


        RequestBody authBody = new FormBody.Builder()
                .add("client_id", user_name)
                .add("client_secret", password)
                .add("refresh_token", OauthPreferences.get(Oauth.REFRESH_TOKEN))
                .add("scope", "android")
                .add("grant_type", "refresh_token")
                .build();

        try {
            Call_ call_ = new Call_(domain + Urls_.TOKEN, authBody, TAG, _context);
            String json = call_.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();

            if (json != null) {
                JSONObject jsonObject = new JSONObject(json);
                return JsonParser_.token_(jsonObject);
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}