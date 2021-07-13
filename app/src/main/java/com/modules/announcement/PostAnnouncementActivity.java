package com.modules.announcement;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.DeviceInfo;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.KeyboardOperation;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.secure._Base64;
import com.sagesurfer.snack.ShowLoader;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.storage.preferences.Preferences;
import com.utils.AppLog;

import java.util.HashMap;

import okhttp3.RequestBody;


/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 12-07-2017
 * Last Modified on 13-12-2017
 */


public class PostAnnouncementActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView teamSelector, messageBoxLabel;
    private EditText messageBox;
    private static final String TAG = PostAnnouncementActivity.class.getSimpleName();
    private int group_id = 0;
    private String start_time = "0";
    //private ArrayList<Teams_> teamsArrayList;
    private ShowLoader showLoader;

    Toolbar toolbar;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.team_talk_post_layout);

        showLoader = new ShowLoader();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_cancel_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        titleText.setPadding(50, 0, 0, 0);
        titleText.setText(this.getResources().getString(R.string.post_announcements));

        AppCompatImageView postButton = (AppCompatImageView) findViewById(R.id.imageview_toolbar_save);
        postButton.setVisibility(View.VISIBLE);
        postButton.setOnClickListener(this);

        messageBoxLabel = (TextView) findViewById(R.id.create_team_talk_title_box_label);
        messageBoxLabel.setText(this.getResources().getString(R.string.message));
        messageBox = (EditText) findViewById(R.id.et_title);
        TextView descriptionBoxLabel = (TextView) findViewById(R.id.create_team_talk_description_box_label);
        descriptionBoxLabel.setVisibility(View.GONE);
        TextView descriptionBoxValidate = (TextView) findViewById(R.id.textview_description_validate);
        descriptionBoxValidate.setVisibility(View.GONE);
        EditText descriptionBox = (EditText) findViewById(R.id.et_team_talk_description);
        descriptionBox.setVisibility(View.GONE);

        teamSelector =  findViewById(R.id.tv_selected_team_name);
        teamSelector.setText(""+Preferences.get(General.TEAM_NAME));


    }

    // Validate if all necessary fields for valid data
    private boolean validate(String message, View view) {
        if (group_id == 0) {
            ShowSnack.viewWarning(view, this.getResources().getString(R.string.please_select_team), getApplicationContext());
            return false;
        }
        if (message.isEmpty() || message.length() < 2 || message.length() > 500) {
            if (message.length() > 500) {
                messageBox.setError("Max length allowed is 500 Char");
            } else if (message.length() < 2) {
                messageBox.setError("Min length allowed is 2 Char");
            } else {
                messageBox.setError("Enter valid message");
            }
            return false;
        }
        return true;
    }

    // Make network call to post new announcement
    private void postThread(String message, View view) {
        int status = 12;
        String info = DeviceInfo.get(this);
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.POST_ANNOUNCEMENT);
        requestMap.put(General.MSG, message);
        requestMap.put("gid", "" + group_id);
        requestMap.put(General.START_TIME, start_time);
        requestMap.put(General.END_TIME, GetTime.getChatTimestamp());
        requestMap.put(General.INFO, _Base64.encode(info));
        requestMap.put(General.IP, DeviceInfo.getDeviceMAC(this));
        requestMap.put(General.TIMEZONE, Preferences.get(General.TIMEZONE));
        requestMap.put(General.DOMAIN_CODE, Preferences.get(General.DOMAIN_CODE));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CC_OPERATIONS_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                JsonArray jsonArray = GetJson_.getArray(response, General.MESSAGE);
                if (jsonArray != null) {
                    JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                    status = jsonObject.get(General.STATUS).getAsInt();
                } else {
                    status = 11;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showResponses(status, view);
    }

    private void showResponses(int status, View view) {
        String message;
        if (status == 1) {
            message = this.getResources().getString(R.string.successful);
        } else {
            message = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, view, getApplicationContext());
        showLoader.dismissPostingDialog();
        if (status == 1) {
            onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        start_time = GetTime.getChatTimestamp();
        Preferences.initialize(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        group_id = Integer.parseInt(Preferences.get(General.GROUP_ID));
        String group_name = Preferences.get(General.GROUP_NAME);
       // teamSelector.setText(group_name);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putSerializable(General.TEAM_LIST, teamsArrayList);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        finish();
    }

    @Override
    public void onClick(View v) {
        KeyboardOperation.hide(getApplicationContext(), messageBox.getWindowToken());
        switch (v.getId()) {
            case R.id.imageview_toolbar_save:
                String title = messageBox.getText().toString().trim();
                if (validate(title, v)) {
                    showLoader.showPostingDialog(this);
                    postThread(title, v);
                }
                break;
        }
    }
}
