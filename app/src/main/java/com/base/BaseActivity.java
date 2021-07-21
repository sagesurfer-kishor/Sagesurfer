package com.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.api.ApiService;
import com.modules.appointment.activity.AddAppointmentActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.constant.Oauth;
import com.sagesurfer.library.DeviceInfo;
import com.sagesurfer.models.Token_;
import com.sagesurfer.oauth.RefreshToken;
import com.storage.preferences.OauthPreferences;
import com.storage.preferences.Preferences;

import am.appwise.components.ni.NoInternetDialog;
import okhttp3.FormBody;
import okhttp3.RequestBody;


public abstract class BaseActivity extends AppCompatActivity {

    public static String TAG=BaseActivity.class.getSimpleName();

    private ProgressDialog pDialog;

    public ApiService mApiService;

    private static RefreshToken refreshToken;

    private boolean arePermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED)
                return false;

        }
        return true;
    }
    // make call to fetch oauth token
    public String getToken(Context _context) {

        showDialog();

        String access_token = null;
        Token_ token;
        OauthPreferences.initialize(_context);
        String i = OauthPreferences.get(Oauth.EXPIRES_AT);
        try {
            if (System.currentTimeMillis() >= Long.parseLong(OauthPreferences.get(Oauth.EXPIRES_AT))) {
                token = refreshToken.getRefreshToken(Preferences.get(Oauth.CLIENT_ID),
                        Preferences.get(Oauth.CLIENT_SECRET),
                        Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, ""),
                        _context);

                if (token.getStatus() == 1) {
                    access_token = token.getAccessToken();
                } else if (token.getStatus() == 12) {
                    getToken(_context);
                }
            } else {
                access_token = OauthPreferences.get(Oauth.ACCESS_TOKEN);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        hideDialog();
        return access_token;
    }


    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public abstract void onButtonEvent(int code);

    public boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // test for connection
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            Log.v("", "Internet Connection Not Available");
            NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(BaseActivity.this).build();
            noInternetDialog.showDialog();
            //Toast.makeText(context, context.getResources().getString(R.string.network_connectivity_not_available), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public String getModel() {
        return DeviceInfo.getDeviceName();
    }

    public String getIMEI() {
        return DeviceInfo.getImei(BaseActivity.this);
    }

    public String getTimeZone() {
        return DeviceInfo.getTimeZone();
    }


    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public void hideDialog() {
        try {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showDialog() {
        try {
            pDialog = new ProgressDialog(BaseActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage(getString(R.string.lbl_please_wait));
            pDialog.setCanceledOnTouchOutside(false);


            pDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showAlertDialog(String title, String message, final int code) {

        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                BaseActivity.this);

        // Setting Dialog Title
        alertDialog2.setTitle(title);

        // Setting Dialog Message
        alertDialog2.setMessage(message);

        // Setting Icon to Dialog
        alertDialog2.setIcon(R.drawable.icon_launcher);
        alertDialog2.setCancelable(false);

        // Setting Positive "OK" Btn
        alertDialog2.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        onButtonEvent(code);
                        dialog.dismiss();
                    }
                });

        // Showing Alert Dialog
        alertDialog2.show();
    }
}
