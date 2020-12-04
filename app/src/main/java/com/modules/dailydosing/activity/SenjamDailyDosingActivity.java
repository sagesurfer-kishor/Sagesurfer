package com.modules.dailydosing.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.modules.caseload.senjam.activity.AddDoctorsNoteActivity;
import com.modules.dailydosing.adapter.SenjamDailyDosingListAdapter;
import com.modules.dailydosing.model.DailyDosing;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.SenjamDailyDosingList_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

public class SenjamDailyDosingActivity extends AppCompatActivity implements View.OnClickListener, SenjamDailyDosingListAdapter.SenjamDailyDosingListAdapterListener {
    private static final String TAG = SenjamDailyDosingActivity.class.getSimpleName();
    public ArrayList<DailyDosing> dailyDosingArrayList = new ArrayList<>(), searchdailyDosingArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayout errorLayout;
    private TextView errorText, totalCount;
    private AppCompatImageView errorIcon;
    private LinearLayoutManager mLinearLayoutManager;
    private List<String> moodIds = new ArrayList<String>();
    private LinearLayout mLinearLayoutDailyDosing;


    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_senjam_daily_dosing);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
        titleText.setPadding(100, 0, 0, 0);
        titleText.setText(getResources().getString(R.string.daily_dosing_compliance));

        initUI();
    }

    // Variable Declaration Function
    private void initUI() {
        recyclerView = (RecyclerView) findViewById(R.id.swipe_refresh_layout_recycler_view);
        recyclerView.setBackgroundColor(this.getResources().getColor(R.color.screen_background));
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLinearLayoutManager);

        totalCount = (TextView) findViewById(R.id.txt_total_value);
        errorText = (TextView) findViewById(R.id.swipe_refresh_recycler_view_error_message);
        errorIcon = (AppCompatImageView) findViewById(R.id.swipe_refresh_recycler_view__error_icon);
        errorLayout = (LinearLayout) findViewById(R.id.swipe_refresh_recycler_view_error_layout);

        mLinearLayoutDailyDosing = findViewById(R.id.linear_add_layout);
        mLinearLayoutDailyDosing.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //Called API for Daily Dosing List
        dailyDosingListAPICalled();
    }


    //Api call for Daily Dosing List
    @SuppressLint("SetTextI18n")
    private void dailyDosingListAPICalled() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_SELFGOAL_DOSAGE_SENJAM);
        requestMap.put(General.PATIENT_ID, Preferences.get(General.CONSUMER_ID));

        Log.e("requestMapdailydosing", requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("dailyDosingList", "" + response);
                if (response != null) {
                    dailyDosingArrayList = SenjamDailyDosingList_.parseDailyDosingList(response, Actions_.GET_SELFGOAL_DOSAGE_SENJAM, this, TAG);
                    if (dailyDosingArrayList.size() > 0) {
                        if (dailyDosingArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            totalCount.setText("" + dailyDosingArrayList.size());

                            /*All ArrayList Bind Here and Set into SenjamDailyDosingListAdapter*/
                            SenjamDailyDosingListAdapter senjamDailyDosingListAdapter = new SenjamDailyDosingListAdapter(this, dailyDosingArrayList, this);
                            recyclerView.setAdapter(senjamDailyDosingListAdapter);

                        } else if (dailyDosingArrayList.get(0).getStatus() == 2) {
                            showError(true, 2);
                            totalCount.setText("0");
//                            Toast.makeText(this, "NO DATA", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
//                        showError(true, progressList.get(0).getStatus());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("errorDailyDosingListApi", e.getMessage());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.swipe_refresh_layout_recycler_view_float:
                Intent addProgressNoteIntent = new Intent(this, AddDoctorsNoteActivity.class);
                startActivity(addProgressNoteIntent);
                break;
        }
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, getApplicationContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public void onDailyDosingLayoutClicked(DailyDosing dailyDosing) {
    }
}
