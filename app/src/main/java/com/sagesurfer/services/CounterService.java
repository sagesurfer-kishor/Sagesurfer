package com.sagesurfer.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.sagesurfer.constant.Broadcast;
import com.sagesurfer.constant.General;
import com.sagesurfer.logger.Logger;
import com.sagesurfer.models.Counters_;
import com.sagesurfer.tasks.PerformGetCounterTask;
import com.storage.preferences.Preferences;

import java.util.Date;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

public class CounterService extends Service {
    private static final String TAG = CounterService.class.getSimpleName();
    private final Handler handler = new Handler();
    private Intent intent;
    private Context _context;

    @Override
    public void onCreate() {
        super.onCreate();
        _context = this.getApplicationContext();
        intent = new Intent(Broadcast.COUNTER_BROADCAST);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
    }

    private final Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            //Monika if-else condition added to hit PerformGetCounterTask.getCounters only if user is logged in
            Preferences.initialize(_context);
            if (Preferences.get(General.IS_LOGIN).equalsIgnoreCase("1")) {
                Counters_ counters_ = PerformGetCounterTask.getCounters(_context);
                if (counters_ != null) {
                    displayCounter();
                } else {
                    Logger.error(TAG, "no counter", _context);
                }
                handler.postDelayed(this, 10000);
            } else {
                Logger.error(TAG, "User Not Logged In", _context);
            }
        }
    };

    @SuppressWarnings("deprecation")
    private void displayCounter() {
        intent.putExtra(Broadcast.COUNTER_BROADCAST, new Date().toLocaleString());
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
    }
}
