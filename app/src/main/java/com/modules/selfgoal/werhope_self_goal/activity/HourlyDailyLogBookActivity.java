package com.modules.selfgoal.werhope_self_goal.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.modules.selfgoal.werhope_self_goal.model.LogBook;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.Goal_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.SelfGoal_;
import com.storage.preferences.Preferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.RequestBody;

public class HourlyDailyLogBookActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = HourlyDailyLogBookActivity.class.getSimpleName();
    private AppCompatImageView postButton;
    private Goal_ goal;
    private RecyclerView recyclerView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private LinearLayoutManager mLinearLayoutManager;
    public ArrayList<LogBook> goalArrayList = new ArrayList<>();

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_hourly_daily_log_book);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
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
        titleText.setText(getResources().getString(R.string.log_book));
        titleText.setPadding(150, 0, 0, 0);
        postButton = (AppCompatImageView) findViewById(R.id.imageview_toolbar_save);
        postButton.setImageResource(R.drawable.help_icon);
        postButton.setVisibility(View.VISIBLE);
        postButton.setOnClickListener(this);

        Intent data = getIntent();
        if (data.hasExtra("map")) {
            goal = (Goal_) data.getSerializableExtra("map");
        }

        initUI();
    }

    private void initUI() {
        recyclerView = (RecyclerView) findViewById(R.id.swipe_refresh_layout_recycler_view);
        recyclerView.setBackgroundColor(this.getResources().getColor(R.color.screen_background));
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLinearLayoutManager);

        errorText = (TextView) findViewById(R.id.swipe_refresh_recycler_view_error_message);
        errorIcon = (AppCompatImageView) findViewById(R.id.swipe_refresh_recycler_view__error_icon);
        errorLayout = (LinearLayout) findViewById(R.id.swipe_refresh_recycler_view_error_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Called API for display list of hourly_logs
        getGoalLogAPICalled();
    }

    private void getGoalLogAPICalled() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_LOG);
        requestMap.put(General.ID, String.valueOf(goal.getId()));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.DATE, new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date()));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    goalArrayList = SelfGoal_.logBook(response, Actions_.GET_LOG, this, TAG);
                    if (goalArrayList.size() > 0) {
                        if (goalArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            HourDailyListAdapter hourDailyListAdapter = new HourDailyListAdapter(this, goalArrayList);
                            recyclerView.setAdapter(hourDailyListAdapter);
                        } else {
                            showError(true, goalArrayList.get(0).getStatus());
                        }
                    } else {
                        showError(true, goalArrayList.get(0).getStatus());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, this));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_toolbar_save:
                helpDialogActivity();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void helpDialogActivity() {
        View view = getLayoutInflater().inflate(R.layout.dialog_logbook_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM;

        Button okayBtn = view.findViewById(R.id.button_okay);
        LinearLayout helpLayout = view.findViewById(R.id.help_layout);

        helpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        okayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}
