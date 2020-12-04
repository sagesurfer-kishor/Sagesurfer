package com.modules.selfgoal.werhope_self_goal.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.modules.calendar.CustomCalendar;
import com.modules.calendar.CustomCalendarAdapter;
import com.modules.selfgoal.werhope_self_goal.model.LogBook;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Goal_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.SelfGoal_;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.RequestBody;

public class GoalLogBookActivity extends AppCompatActivity implements View.OnClickListener, GridView.OnItemClickListener {
    private static final String TAG = GoalLogBookActivity.class.getSimpleName();
    private GridView mCalendar;
    private AppCompatImageView mPrevious, mNext;
    private TextView mMonthName;
    protected static int mNumEventsOnDay = 0;
    private CustomCalendarAdapter mMaterialCalendarAdapter;
    public ArrayList<LogBook> goalArrayList = new ArrayList<>();
    public static ArrayList<Integer> completeDays = new ArrayList<>();
    public static ArrayList<Integer> missedDays = new ArrayList<>();
    public static ArrayList<Integer> pendingDays = new ArrayList<>();
    private AppCompatImageView postButton;
    private static Activity activity;
    private Goal_ goal;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_goal_log_book);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        activity = GoalLogBookActivity.this;

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
        titleText.setPadding(140, 0, 0, 0);
        postButton = (AppCompatImageView) findViewById(R.id.imageview_toolbar_save);
        postButton.setImageResource(R.drawable.help_icon);
        postButton.setVisibility(View.VISIBLE);
        postButton.setOnClickListener(this);

        Preferences.initialize(getApplicationContext());
        CustomCalendar.getInitialCalendarInfo();

        mPrevious = (AppCompatImageView) findViewById(R.id.calendar_fragment_month_back);
        if (mPrevious != null) {
            mPrevious.setOnClickListener(this);
        }
        mMonthName = (TextView) findViewById(R.id.calendar_fragment_month_name);
        if (mMonthName != null) {
            Calendar cal = Calendar.getInstance();
            if (cal != null) {
                String monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG,
                        Locale.getDefault()) + " " + cal.get(Calendar.YEAR);
                mMonthName.setText(monthName);
            }
        }
        mNext = (AppCompatImageView) findViewById(R.id.calendar_fragment_month_next);
        if (mNext != null) {
            mNext.setOnClickListener(this);
        }
        mCalendar = (GridView) findViewById(R.id.calendar_fragment_grid_view);
        if (mCalendar != null) {
            mCalendar.setOnItemClickListener(this);
            mMaterialCalendarAdapter = new CustomCalendarAdapter(this);
            mCalendar.setAdapter(mMaterialCalendarAdapter);

            if (CustomCalendar.mCurrentDay != -1 && CustomCalendar.mFirstDay != -1) {
                int startingPosition = 6 + CustomCalendar.mFirstDay;
                int currentDayPosition = startingPosition + CustomCalendar.mCurrentDay;

                mCalendar.setItemChecked(currentDayPosition, true);

                if (mMaterialCalendarAdapter != null) {
                    mMaterialCalendarAdapter.notifyDataSetChanged();
                }
            }
        }

        Intent data = getIntent();
        if (data.hasExtra("map")) {
            goal = (Goal_) data.getSerializableExtra("map");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_toolbar_save:
                helpDialogActivity();
                break;
            case R.id.calendar_fragment_month_back:
                completeDays.clear();
                missedDays.clear();
                pendingDays.clear();
                CustomCalendar.previousOnClick(mPrevious, mMonthName, mCalendar, mMaterialCalendarAdapter);
                break;
            case R.id.calendar_fragment_month_next:
                completeDays.clear();
                missedDays.clear();
                pendingDays.clear();
                CustomCalendar.nextOnClick(mNext, mMonthName, mCalendar, mMaterialCalendarAdapter);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.calendar_fragment_grid_view:
                CustomCalendar.selectCalendarDay(mMaterialCalendarAdapter, position);
                mNumEventsOnDay = -1;
                break;
            default:
                break;
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        Preferences.save(Actions_.GET_LOG, true);
        Preferences.save(General.IS_FROM_CASELOAD, false);
        Preferences.save(General.IS_FROM_DAILYPLANNER, false);
        Preferences.save(Actions_.GET_LOGBOOK_DOTS, false);
        //Called API for display list of hourly_logs
        getGoalLogAPICalled();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Preferences.save(Actions_.GET_LOG, false);
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

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray get_logArray = jsonObject.getJSONArray("get_log");
                    JSONObject mainObject = get_logArray.getJSONObject(0);

                    JSONArray completeArray = mainObject.getJSONArray("completed");
                    JSONArray missedArray = mainObject.getJSONArray("missed");
                    JSONArray pendingArray = mainObject.getJSONArray("pending");

                    if (completeArray.length() > 0) {
                        for (int i = 0; i < completeArray.length(); i++) {
                            String object = completeArray.getString(i).substring(8, 10);
                            completeDays.add(Integer.parseInt(String.valueOf(object)));
                        }
                    }

                    if (missedArray.length() > 0) {
                        for (int j = 0; j < missedArray.length(); j++) {
                            String object = missedArray.getString(j).substring(8, 10);
                            missedDays.add(Integer.parseInt(String.valueOf(object)));
                        }
                    }

                    if (pendingArray.length() > 0) {
                        for (int k = 0; k < pendingArray.length(); k++) {
                            String object = pendingArray.getString(k).substring(8, 10);
                            pendingDays.add(Integer.parseInt(String.valueOf(object)));
                        }
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
