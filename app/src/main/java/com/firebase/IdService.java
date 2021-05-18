package com.firebase;

import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.sagesurfer.logger.Logger;
import com.storage.preferences.Preferences;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 05-09-2017
 * Last Modified on 12-12-2017
 **/

/*
 * This file contains firebase service to get firabase token and refresh token
 * One method is added to store that firebase token to shared preferences
 */


public class IdService /*extends FirebaseInstanceIdService*/ {

   /* private static final String TAG = IdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        storeRegIdInPref(refreshedToken);

        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void storeRegIdInPref(String token) {
        Logger.error(TAG, "firebase id: " + token, getApplicationContext());
        Preferences.save("regId_save", false);
        Preferences.initialize(getApplicationContext());
        Preferences.save("regId", token);
    }*/
}

