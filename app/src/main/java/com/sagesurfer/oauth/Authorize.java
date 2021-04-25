package com.sagesurfer.oauth;

import android.app.Activity;
import android.content.Context;

import com.sagesurfer.callbacks.AuthorizationCallbacks;
import com.sagesurfer.network.Call_;
import com.sagesurfer.network.Urls_;
import com.storage.preferences.OauthPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

/*
*  Make a com.sagesurfer.network call to authorize user for OAuth token system
*  At a time of login user will get client id and secret which will be used here to authorize
*  With successful authorization user will get oauth token and refresh token in return
*/

public class Authorize {

    private AuthorizationCallbacks callbacks;
    private static final String TAG = Authorize.class.getSimpleName();

    public Authorize(Activity activity) {
        callbacks = (AuthorizationCallbacks) activity;
        OauthPreferences.initialize(activity);
    }

    public void getAuthorized(String user_name, String password, String domain, Context _context) {
        RequestBody authBody = new FormBody.Builder()
                .add("client_id", user_name)
                .add("client_secret", password)
                .add("redirect_uri", domain)
                .add("state", "android")
                .add("scope", "android")
                .add("response_type", "code")
                .build();
        Call_ call_ = new Call_(domain + Urls_.AUTHORIZE, authBody, TAG, _context);
        try {
            String json = call_.execute().get();
            if (json != null) {
                JSONObject jsonObject = new JSONObject(json);
                int result = JsonParser_.authorization(jsonObject);
                if (result == 1) {
                    callbacks.authorizationSuccessCallback(jsonObject);
                } else {
                    callbacks.authorizationFailCallback(new JSONObject(ErrorGenerator_.errorAuthorization(result)));
                }
            } else {
                callbacks.authorizationFailCallback(new JSONObject(ErrorGenerator_.errorAuthorization(12)));
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
    }
}
