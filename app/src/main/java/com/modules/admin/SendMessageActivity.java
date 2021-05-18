package com.modules.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Friends_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Error_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.HashMap;

import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 24-08-2017
 * Last Modified on 13-12-2017
 */

/*
 * This class is used to send message to user from supervisor only.
 */

public class SendMessageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = SendMessageActivity.class.getSimpleName();
    private EditText messageBox;
    private Friends_ friends_;
    Toolbar toolbar;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.send_message_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_left_arrow_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        titleText.setText(this.getResources().getString(R.string.message));

        TextView postButton = (TextView) findViewById(R.id.textview_activitytoolbar_post);
        postButton.setVisibility(View.VISIBLE);
        postButton.setText(this.getResources().getString(R.string.send));
        postButton.setOnClickListener(this);

        messageBox = (EditText) findViewById(R.id.send_message_box);

        Intent data = getIntent();
        if (data != null) {
            friends_ = (Friends_) data.getSerializableExtra(General.USERNAME);
            setData();
        }
    }

    // Set data to appropriate field
    private void setData() {
        TextView nameText = (TextView) findViewById(R.id.admin_user_list_item_name);
        TextView roleText = (TextView) findViewById(R.id.admin_user_list_item_role);
        ImageView image = (ImageView) findViewById(R.id.admin_user_list_item_photo);

        nameText.setText(ChangeCase.toTitleCase(friends_.getName()));
        roleText.setText(friends_.getRole());

        //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
        // set image to image view
        Glide.with(getApplicationContext())
                .load(friends_.getPhoto())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_group_default)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL) //.RESULT
                        .transform(new CircleTransform(getApplicationContext())))
                .into(image);
    }

    // Validate message before sending
    private boolean validate(String message) {
        if (message == null) {
            messageBox.setError("Invalid Message");
            return false;
        }
        if (message.length() <= 2) {
            messageBox.setError("Min 2 char required");
            return false;
        }
        if (message.length() > 250) {
            messageBox.setError("Max 250 char allowed");
            return false;
        }
        return true;
    }

    //Make network call to send/post message
    private void sendMessage(String message, View view) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.SEND_MESSAGE);
        requestMap.put("to", "" + friends_.getUserId());
        requestMap.put(General.MSG, message);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SUPERVISOR_OPERATIONS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    if (Error_.oauth(response, getApplicationContext()) == 13) {
                        showResponses(13, view);
                        return;
                    }
                    JsonObject object = GetJson_.getJson(response);
                    if (object.has(General.STATUS)) {
                        showResponses(object.get(General.STATUS).getAsInt(), view);
                    } else {
                        showResponses(12, view);
                        return;
                    }
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showResponses(11, view);
    }

    private void showResponses(int status, View view) {
        String message = "";
        if (status == 1) {
            message = this.getResources().getString(R.string.successful);
            onBackPressed();
        } else if (status == 2) {
            message = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, view, getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0,0);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_activitytoolbar_post:
                String message = messageBox.getText().toString();
                if (validate(message)) {
                    sendMessage(message, v);
                }
                break;
        }
    }
}