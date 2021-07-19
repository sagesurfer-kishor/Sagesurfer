package com.utils

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.sagesurfer.collaborativecares.R;


object MessageUtils {

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- SHOW TOAST -=-=-=-=-=-=-=-=-=-=-=-=-=- */
      fun showToast(context: Context, message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

     fun showToast(context: Context, @StringRes resId: Int) {
        val toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- SHOW ALERT -=-=-=-=-=-=-=-=-=-=-=-=-=- */

     fun showAlert(context: Activity, title: String, message: String) {
        if (!context.isFinishing)
            MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.common_btn_ok) { dialogInterface, _ -> dialogInterface.dismiss() }
                .create()
                .show()
    }

    fun showAlertDialog(activity: Activity, message: String) {
        if (!activity.isFinishing)
            MaterialAlertDialogBuilder(activity)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.common_btn_ok) { dialogInterface, _ -> dialogInterface.dismiss() }
                .create()
                .show()
    }


     fun showAlert(activity: Activity, message: String) {
        if (!activity.isFinishing)
            MaterialAlertDialogBuilder(activity)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.common_btn_ok) { dialogInterface, _ -> dialogInterface.dismiss() }
                .create()
                .show()
    }

     fun showAlert(context: Activity, @StringRes resId: Int) {
        if (!context.isFinishing)
            MaterialAlertDialogBuilder(context)
                .setMessage(resId)
                .setCancelable(false)
                .setPositiveButton(R.string.common_btn_ok) { dialogInterface, _ -> dialogInterface.dismiss() }
                .create()
                .show()
    }

    fun showAlert(
        context: Activity, message: String, listener: OnOkClickListener?
    ) {
        if (!context.isFinishing)
            MaterialAlertDialogBuilder(context)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.common_btn_ok) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    listener?.onOkClick()
                }
                .create()
                .show()
    }

    fun showAlert(
        context: Activity, @StringRes resId: Int, listener: OnOkClickListener?
    ) {
        if (!context.isFinishing)
            MaterialAlertDialogBuilder(context)
                .setMessage(resId)
                .setCancelable(false)
                .setPositiveButton(R.string.common_btn_ok) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    listener?.onOkClick()
                }
                .create()
                .show()
    }

    fun showAlert(
        context: Activity, @StringRes resId_msg: Int, @StringRes resId_Title: Int,
        @StringRes resId_posBtn: Int, @StringRes resId_negBtn: Int, listener: OnOkClickListener?
    ) {
        if (!context.isFinishing)
            MaterialAlertDialogBuilder(context)
                .setMessage(resId_msg)
                // .setTitle(resId_Title);
                .setCancelable(false)
                .setNegativeButton(resId_negBtn) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(resId_posBtn) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    listener?.onOkClick()
                }
                .create()
                .show()
    }

    fun showConfirmation(
        context: Activity, @StringRes resId_msg: Int, @StringRes resId_Title: Int,
        @StringRes resId_posBtn: Int, @StringRes resId_negBtn: Int, listener: OnOkClickListener?
    ) {
        if (!context.isFinishing)
            MaterialAlertDialogBuilder(context)
                .setMessage(resId_msg)
                // .setTitle(resId_Title);
                .setCancelable(false)
                .setPositiveButton(resId_negBtn) { dialog, _ -> dialog.dismiss() }
                .setNegativeButton(resId_posBtn) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    listener?.onOkClick()
                }
                .create()
                .show()
    }

    interface OnOkClickListener {
        fun onOkClick()
    }

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- SHOW SNACK-BAR -=-=-=-=-=-=-=-=-=-=-=-=-=- */
    fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }

    fun showSnackBar(view: View, @StringRes resId: Int) {
        Snackbar.make(view, resId, Snackbar.LENGTH_LONG).show()
    }

    fun showSnackBar(activity: Activity, message: String) {
        Snackbar.make(activity.window.decorView.rootView, message, Snackbar.LENGTH_LONG).show()
    }

}