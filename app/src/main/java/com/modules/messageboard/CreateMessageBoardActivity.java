package com.modules.messageboard;

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

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.MessageBoard_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Alerts_;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * Created by Monika on 2/26/2019.
 */

public class CreateMessageBoardActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = CreateMessageBoardActivity.class.getSimpleName();

    private EditText descriptionBox;
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

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        //toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.screen_background));
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
        //titleText.setTextColor(getResources().getColor(R.color.text_color_primary));
        titleText.setText(this.getResources().getString(R.string.create_new_message));

        AppCompatImageView postButton = (AppCompatImageView) findViewById(R.id.imageview_toolbar_save);
        /*int color = Color.parseColor("#a5a5a5"); //text_color_tertiary
        postButton.setColorFilter(color);
        postButton.setImageResource(R.drawable.vi_check_white);*/
        postButton.setVisibility(View.VISIBLE);
        postButton.setOnClickListener(this);

        TextView teamLabel = (TextView) findViewById(R.id.create_team_talk_select_team_label);
        TextView teamText = (TextView) findViewById(R.id.create_team_talk_select_team);
        TextView messageBoxLabel = (TextView) findViewById(R.id.create_team_talk_title_box_label);
        TextView validateText = (TextView) findViewById(R.id.textview_validate);
        EditText messageBox = (EditText) findViewById(R.id.create_team_talk_title_box);
        teamLabel.setVisibility(View.GONE);
        messageBoxLabel.setVisibility(View.GONE);
        validateText.setVisibility(View.GONE);
        teamText.setVisibility(View.GONE);
        messageBox.setVisibility(View.GONE);

        descriptionBox = (EditText) findViewById(R.id.create_team_talk_description_box);
    }

    private void showResponses(int status) {
        String message;
        if (status == 1) {
            message = this.getResources().getString(R.string.successful);
        } else {
            status = 2;
            message = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, getApplicationContext());
        if (status == 1) {
            onBackPressed();
        }
    }

    // validate all fields for proper data
    private boolean validate(String description) {
        if (description == null) {
            descriptionBox.setError("Invalid Descriptio");
            return false;
        }
        if (description.length() < 3) {
            descriptionBox.setError("Min 3 char required");
            return false;
        }
        if (description.length() > 1000) {
            descriptionBox.setError("Max 1000 char required");
            return false;
        }
        return true;
    }

    //make network call to create messageboard
    private void createMessageBoard(String description) {
        int result = 11;

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ADD_MESSAGEBOARD);
        requestMap.put(General.MSG, description);
        requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TEAM_DETAILS_PAGE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    /*if (Error_.oauth(response, getApplicationContext()) == 13) {
                        result = 13;
                        showResponses(result);
                        return;
                    }
                    JsonObject jsonObject = GetJson_.getJson(response);
                    if (jsonObject != null) {
                        if (jsonObject.has(General.STATUS)) {
                            result = jsonObject.get(General.STATUS).getAsInt();
                        }
                    }//{"add_messageboard":[{"msg":"Message Board posted succesfully.","status":1}]}*/
                    ArrayList<MessageBoard_> tempMessageBoardArrayList = Alerts_.parseMessageBoard(response, Actions_.ADD_MESSAGEBOARD, getApplicationContext(), TAG);
                    if (tempMessageBoardArrayList.size() > 0) {
                        if (tempMessageBoardArrayList.get(0).getStatus() == 1) {
                            result = 1;
                        } else {
                            result = tempMessageBoardArrayList.get(0).getStatus();
                        }
                    } else {
                        result = 12;
                    }
                } else {
                    result = 12;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showResponses(result);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_toolbar_save:
                String description = descriptionBox.getText().toString().trim();
                if (validate(description)) {
                    createMessageBoard(description);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        finish();
    }
}
