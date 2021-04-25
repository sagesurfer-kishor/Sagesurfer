package com.modules.settings;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Error_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.validator.LoginValidator;
import com.storage.preferences.Preferences;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.RequestBody;

/**
 * @author Kailash Karankal
 */

public class ChangePasswordActivity extends AppCompatActivity {
    private static final String TAG = ChangePasswordActivity.class.getSimpleName();
    @BindView(R.id.edittext_changepassword_currentpassword)
    EditText editTextChangePasswordCurrentPassword;
    @BindView(R.id.edittext_changepassword_newpassword)
    EditText editTextChangePasswordNewPassword;
    @BindView(R.id.edittext_changepassword_confirmpassword)
    EditText editTextChangePasswordConfirmPassword;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_left_arrow_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView textViewActivityToolbarTitle = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        textViewActivityToolbarTitle.setText(this.getResources().getString(R.string.change_password));

        TextView textViewActivityToolbarPost = (TextView) findViewById(R.id.textview_activitytoolbar_post);
        textViewActivityToolbarPost.setVisibility(View.VISIBLE);
        textViewActivityToolbarPost.setText(this.getResources().getString(R.string.save));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        finish();
    }

    @OnClick(R.id.textview_activitytoolbar_post)
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.textview_activitytoolbar_post:
                if (validate()) {
                    changePassword();
                }
                break;
        }
    }

    private void changePassword() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.RESET_PASSWORD);
        requestMap.put(General.OLD_PASSWORD, editTextChangePasswordCurrentPassword.getText().toString().trim());
        requestMap.put(General.PASSWORD, editTextChangePasswordNewPassword.getText().toString().trim());
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_USER_SETTING;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonAddJournal = jsonObject.getAsJsonObject(Actions_.RESET_PASSWORD);
                    if (jsonAddJournal.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(ChangePasswordActivity.this, jsonAddJournal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, jsonAddJournal.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //validate all fields
    private boolean validate() {
        String current_password = editTextChangePasswordCurrentPassword.getText().toString().trim();
        String new_password = editTextChangePasswordNewPassword.getText().toString().trim();
        String confirm_password = editTextChangePasswordConfirmPassword.getText().toString().trim();

        String password = Preferences.get(General.PASSWORD);
        if (current_password.length() <= 0 || !current_password.equalsIgnoreCase(password)) {
            editTextChangePasswordCurrentPassword.setError("Please enter current password");
            return false;
        }
        if (new_password.length() <= 0 || !LoginValidator.isPassword(new_password)) {
            editTextChangePasswordNewPassword.setError("Please enter new password");
            return false;
        }
        if (confirm_password == null || confirm_password.trim().length() <= 0) {
            editTextChangePasswordConfirmPassword.setError("Please enter confirm password");
            return false;
        } else if (!new_password.equals(confirm_password)) {
            editTextChangePasswordConfirmPassword.setError("New password and confirm password did not match");
            return false;
        }

        return true;
    }

    // make network call to submit feedback
    private void submit(String message) {
        progressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.please_wait), true);
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, "add");
        requestMap.put(General.MESSAGE, message);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.FEEDBACK_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    if (Error_.oauth(response, getApplicationContext()) == 13) {
                        showResponses(13);
                        return;
                    }
                    JsonArray jsonArray = GetJson_.getArray(response, "feedback");
                    if (jsonArray != null) {
                        JsonObject object = jsonArray.get(0).getAsJsonObject();
                        if (object.has(General.STATUS)) {
                            showResponses(object.get(General.STATUS).getAsInt());
                        } else {
                            showResponses(11);
                            return;
                        }
                    } else {
                        showResponses(11);
                        return;
                    }
                } else {
                    showResponses(12);
                    return;
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showResponses(11);
    }

    private void showResponses(int status) {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        String message = "";
        if (status == 1) {
            message = this.getResources().getString(R.string.successful);
            onBackPressed();
        } else if (status == 2) {
            message = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }
}
