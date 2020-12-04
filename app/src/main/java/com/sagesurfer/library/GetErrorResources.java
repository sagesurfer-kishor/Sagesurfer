package com.sagesurfer.library;

import android.app.Activity;
import android.content.Context;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.snack.ShowToast;

/**
 * @author Monika M(monikam@sagesurfer.com)
 *         Created on 3/31/2018
 *         Last Modified on 3/31/2018
 */

/*
* This class provides error message and icon based on respective error code
*/

public class GetErrorResources {

    public static String getMessage(int status, Context context) {
        String message = context.getResources().getString(R.string.no_record_found);
        if (status == 3) {
            message = context.getResources().getString(R.string.record_already_exists);
        }
        if (status == 11) {
            message = context.getResources().getString(R.string.internal_error_occurred);
        }
        if (status == 12) {
            message = context.getResources().getString(R.string.network_error_occurred);
        }
        if (status == 13) {
            message = context.getResources().getString(R.string.authentication_failed);
        }
        if (status == 20) {
            message = context.getResources().getString(R.string.loading);
        }
        return message;
    }

    public static String getGalleryMessage(int status, Context context) {
        String message = context.getResources().getString(R.string.no_gallery_found);
        if (status == 3) {
            message = context.getResources().getString(R.string.record_already_exists);
        }
        if (status == 11) {
            message = context.getResources().getString(R.string.internal_error_occurred);
        }
        if (status == 12) {
            message = context.getResources().getString(R.string.network_error_occurred);
        }
        if (status == 13) {
            message = context.getResources().getString(R.string.authentication_failed);
        }
        if (status == 20) {
            message = context.getResources().getString(R.string.loading);
        }
        return message;
    }

    public static int getIcon(int status) {
        int icon = R.drawable.vi_data_not_fount_error;
        if (status == 11) {
            icon = R.drawable.vi_internal_error_error;
        }
        if (status == 12) {
            icon = R.drawable.vi_network_error_error;
        }
        if (status == 13) {
            icon = R.drawable.vi_internal_error_error;
        }
        return icon;
    }

    public static void showError(int status, Activity activity) {
        switch (status) {
            case 1:
                ShowToast.successful(activity.getResources().getString(R.string.successful), activity.getApplicationContext());
                break;
            case 2:
                ShowToast.failed(activity.getResources().getString(R.string.action_failed), activity.getApplicationContext());
                break;
            case 11:
                ShowToast.internalErrorOccurred(activity.getApplicationContext());
                break;
            case 12:
                ShowToast.networkError(activity.getApplicationContext());
                break;
            case 13:
                ShowToast.authenticationFailed(activity.getApplicationContext());
                break;
        }
    }
}
