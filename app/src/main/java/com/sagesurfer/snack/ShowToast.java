package com.sagesurfer.snack;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.sagesurfer.constant.Warnings;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

/*
 * global class to show toast message
 */

public class ShowToast {
    public static Toast mToastToShow;

    public static void successful(String message, Context _context) {
        Toast.makeText(_context, message, Toast.LENGTH_LONG).show();
    }

    public static void failed(String message, Context _context) {
        Toast.makeText(_context, message, Toast.LENGTH_LONG).show();
    }

    public static void networkError(Context _context) {
        Toast.makeText(_context, Warnings.NETWORK_ERROR_OCCURRED, Toast.LENGTH_LONG).show();
    }

    public static void authenticationFailed(Context _context) {
        Toast.makeText(_context, Warnings.AUTHENTICATION_FAILED, Toast.LENGTH_LONG).show();
    }

    public static void internalErrorOccurred(Context _context) {
        Toast.makeText(_context, Warnings.INTERNAL_ERROR_OCCURRED, Toast.LENGTH_LONG).show();
    }

    public static void toast(String message, Context _context) {
        Toast.makeText(_context, message, Toast.LENGTH_LONG).show();
    }

    public static void fileNotFound(Context context) {
        Toast.makeText(context, "File Not Found", Toast.LENGTH_SHORT).show();
    }

    public static void urlNotFound(Context context) {
        Toast.makeText(context, "Invalid File Url", Toast.LENGTH_SHORT).show();
    }

    public static void showLongerDurationToast(String message, Context context) {
        // Set the toast and duration
        int toastDurationInMilliSeconds = 10000;
        mToastToShow = Toast.makeText(context, message, Toast.LENGTH_LONG);

        // Set the countdown to display the toast
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 1000) {
            public void onTick(long millisUntilFinished) {
                mToastToShow.show();
            }

            public void onFinish() {
                mToastToShow.cancel();
            }
        };

        // Show the toast and starts the countdown
        mToastToShow.show();
        toastCountDown.start();
    }
}
