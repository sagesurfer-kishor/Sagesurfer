package com.modules.calendar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
 import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.modules.postcard.DividerItemDecoration;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Alerts_;
import com.sagesurfer.parser.Invitations_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 18-07-2017
 * Last Modified on 16-11-2017
 **/


public class InviteListActivity extends AppCompatActivity {

    private static final String TAG = InviteListActivity.class.getSimpleName();
    private ArrayList<Invitations_> invitationsArrayList;

    private RecyclerView recyclerView;
    private LinearLayout errorLayout;

    Toolbar toolbar;
    String team_id = "0";

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));
        setContentView(R.layout.recyle_list_activity_layout);

        invitationsArrayList = new ArrayList<>();

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

        Preferences.initialize(this);
        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        titleText.setText(this.getResources().getString(R.string.invitations));

        TextView postButton = (TextView) findViewById(R.id.textview_activitytoolbar_post);
        postButton.setVisibility(View.GONE);

        AppCompatImageButton menuButton = (AppCompatImageButton) findViewById(R.id.imagebutton_activitytoolbar_menu);
        menuButton.setVisibility(View.GONE);

        recyclerView = (RecyclerView) findViewById(R.id.swipe_refresh_layout_recycler_view);

        errorLayout = (LinearLayout) findViewById(R.id.swipe_refresh_recycler_view_error_layout);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);
        Intent data = getIntent();
        if (data != null && data.hasExtra(General.TEAM_ID)) {
            team_id = data.getStringExtra(General.TEAM_ID);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        getInvites();
    }

    // Show respective error based on status
    private void setErrorLayout(int status) {
        AppCompatImageView errorIcon = (AppCompatImageView) findViewById(R.id.swipe_refresh_recycler_view__error_icon);
        TextView errorText = (TextView) findViewById(R.id.swipe_refresh_recycler_view_error_message);
        if (status == 1) {
            errorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            return;
        }
        errorLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        errorIcon.setImageResource(GetErrorResources.getIcon(status));
        errorText.setText(GetErrorResources.getMessage(status, getApplicationContext()));
    }

    // Make network call to fetch all invitations list from server
    private void getInvites() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_INVITATIONS);
        requestMap.put(General.GROUP_ID, team_id);
        Log.e("requestMap",requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CALENDAR;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                Log.e("Response",response);
                if (response != null) {
                    invitationsArrayList = Alerts_.parseInvite(response, getApplicationContext(), TAG);
                    Collections.reverse(invitationsArrayList);
                    if (invitationsArrayList.size() > 0) {
                        if (invitationsArrayList.get(0).getStatus() == 1) {
                            InviteListAdapter inviteListAdapter = new InviteListAdapter(this, invitationsArrayList);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
                            recyclerView.setAdapter(inviteListAdapter);
                        } else if (invitationsArrayList.get(0).getStatus() == 2){
                            Toast.makeText(this, ""+invitationsArrayList.get(0).getError(), Toast.LENGTH_SHORT).show();
                            setErrorLayout(invitationsArrayList.get(0).getStatus());
                        }
                    } else {
                        setErrorLayout(12);
                    }
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setErrorLayout(11);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        finish();
    }
}
