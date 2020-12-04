package com.modules.teamtalk.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

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

import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Kailash Karankal
 */

public class CreateTeamTalkActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView teamSelector;
    private EditText titleBox, descriptionBox;
    private static final String TAG = CreateTeamTalkActivity.class.getSimpleName();
    private int group_id = 0;
    private String start_time = "0";
    private ShowLoader showLoader;
    private Toolbar toolbar;

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

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage021)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage022))) {
            titleText.setText(this.getResources().getString(R.string.post_team_discussion));
        } else {
            titleText.setText(this.getResources().getString(R.string.post_team_talk));
        }

        AppCompatImageView postButton = (AppCompatImageView) findViewById(R.id.imageview_toolbar_save);
        postButton.setVisibility(View.VISIBLE);
        postButton.setOnClickListener(this);

        titleBox = (EditText) findViewById(R.id.create_team_talk_title_box);
        descriptionBox = (EditText) findViewById(R.id.create_team_talk_description_box);

        teamSelector = (TextView) findViewById(R.id.create_team_talk_select_team);
    }

    // validate input data for validity with it's min and max length
    private boolean validate(String title, String description, View view) {
        if (group_id == 0) {
            ShowSnack.viewWarning(view, this.getResources().getString(R.string.please_select_team), getApplicationContext());
            return false;
        }
        if (title == null || title.length() < 3) {
            titleBox.setError("Invalid Title \nMin Char allowed is 3");
            return false;
        }
        if (title.length() > 250) {
            titleBox.setError("Invalid Title \nMax Char allowed is 250");
            return false;
        }
        if (description == null || description.length() < 3) {
            descriptionBox.setError("Invalid Description \nMin Char allowed is 3");
            return false;
        }
        if (description.length() > 1000) {
            descriptionBox.setError("Invalid Description \nMax Char allowed is 1000");
            return false;
        }
        return true;
    }

    // make call to create team talk
    private void postThread(String title, String description, View view) {
        int status = 12;
        String info = DeviceInfo.get(this);
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.POST_TEAM_TALK);
        requestMap.put(General.GROUP_ID, "" + group_id);
        requestMap.put(General.TITLE, title);
        requestMap.put(General.DESCRIPTION, description);
        requestMap.put(General.START_TIME, start_time);
        requestMap.put(General.END_TIME, GetTime.getChatTimestamp());
        requestMap.put(General.INFO, _Base64.encode(info));
        requestMap.put(General.IP, DeviceInfo.getDeviceMAC(this));
        requestMap.put(General.DOMAIN_CODE, Preferences.get(General.DOMAIN_CODE));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TEAM_DETAILS_PAGE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                JsonObject jsonObject = GetJson_.getJson(response);
                status = jsonObject.get(General.STATUS).getAsInt();
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
        KeyboardOperation.hide(getApplicationContext(), descriptionBox.getWindowToken());
        switch (v.getId()) {
            case R.id.imageview_toolbar_save:
                String title = titleBox.getText().toString().trim();
                String description = descriptionBox.getText().toString().trim();
                if (validate(title, description, v)) {
                    showLoader.showPostingDialog(this);
                    postThread(title, description, v);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        group_id = Integer.parseInt(Preferences.get(General.GROUP_ID));
        String group_name = Preferences.get(General.GROUP_NAME);
        teamSelector.setText(group_name);
    }
}
