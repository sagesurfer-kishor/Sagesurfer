package com.sagesurfer.snack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Window;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

/*
 * This class is used to show loader dialog
 */

public class ShowLoader {

    private ProgressDialog progress;

    public ShowLoader() {
    }

    public void showUploadDialog(Activity activity) {
        if (progress == null) {
            progress = new ProgressDialog(activity);
            progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progress.setMessage("Uploading...");
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
            progress.setIndeterminate(false);
            progress.show();
        } else {
            progress.show();
        }
    }

    public void dismissUploadDialog() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    public void showPostingDialog(Activity activity) {
        if (progress == null) {
            progress = new ProgressDialog(activity);
            progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progress.setMessage("Posting...");
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
            progress.setIndeterminate(false);
            progress.show();
        } else {
            progress.show();
        }
    }

    public void dismissPostingDialog() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    public void dismissSendProgressDialog() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }
}
