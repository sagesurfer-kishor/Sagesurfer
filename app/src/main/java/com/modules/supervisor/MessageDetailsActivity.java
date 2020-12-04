package com.modules.supervisor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
 import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.Supervisor_;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 29-07-2017
 * Last Modified on 14-12-2017
 */

public class MessageDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MessageDetailsActivity.class.getSimpleName();
    private int friend_id = 0;
    private ArrayList<Message_> messageArrayList;
    private ListView listView;
    private EditText messageBox;
    private AppCompatImageButton sendButton;
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

        setContentView(R.layout.message_details_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        messageArrayList = new ArrayList<>();

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

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);

        TextView postButton = (TextView) findViewById(R.id.textview_activitytoolbar_post);
        postButton.setVisibility(View.GONE);

        messageBox = (EditText) findViewById(R.id.message_details_message_box);
        listView = (ListView) findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(0);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);

        sendButton = (AppCompatImageButton) findViewById(R.id.message_details_send);
        sendButton.setOnClickListener(this);

        Intent data = getIntent();
        if (data.hasExtra(General.USER_ID)) {
            friend_id = data.getIntExtra(General.USER_ID, 0);
            String name = data.getStringExtra(General.FULL_NAME);
            titleText.setText(ChangeCase.toTitleCase(name.replaceAll("\\s+", " ")));
        } else {
            onBackPressed();
        }
    }

    // make network call to fetch messages from server
    private void getMessages() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_MESSAGE_DETAILS);
        requestMap.put("friend_id", "" + friend_id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SUPERVISOR_OPERATIONS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    messageArrayList = Supervisor_.parseMessageList(response, this, TAG);
                    Collections.reverse(messageArrayList);
                    ConversationListAdapter conversationListAdapter =
                            new ConversationListAdapter(this, messageArrayList);
                    listView.setAdapter(conversationListAdapter);
                    listView.setSelection(messageArrayList.size());
                    hideKeyboard();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // make network call to send message
    private void sendMessage(String message, View view) {
        sendButton.setEnabled(false);
        int status = 12;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.SEND_MESSAGE);
        requestMap.put("to", "" + friend_id);
        requestMap.put(General.MSG, message);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SUPERVISOR_OPERATIONS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this.getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this.getApplicationContext(), this);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    if (jsonObject.has(General.STATUS)) {
                        status = jsonObject.get(General.STATUS).getAsInt();
                    } else {
                        status = 11;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showResponses(status, view);
    }

    private void showResponses(int status, View view) {
        sendButton.setEnabled(true);
        String message;
        if (status == 1) {
            message = this.getResources().getString(R.string.successful);
        } else {
            message = this.getResources().getString(R.string.message_sent);
        }
        SubmitSnackResponse.showSnack(status, message, view, getApplicationContext());
        if (status == 1) {
            messageBox.setText("");
            getMessages();
        }
    }

    // hide keyboard
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getMessages();
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
            case R.id.message_details_send:
                hideKeyboard();
                String message = messageBox.getText().toString();
                if (message.isEmpty() || message.length() <= 0) {
                    messageBox.setError("Enter valid message");
                } else {
                    if (message.length() > 150) {
                        messageBox.setError("Max message char length allowed 150");
                    } else {
                        sendMessage(message, v);
                    }
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }
}
