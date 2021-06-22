package com.modules.calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.modules.planner.PlannerEventLocationActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.models.Event_;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Alerts_;
import com.sagesurfer.parser.GetJson_;
import com.storage.database.operations.DatabaseInsert_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.RequestBody;

public class CalenderActivity extends AppCompatActivity implements View.OnClickListener, GridView.OnItemClickListener, EventListAdapter.EventListAdapterListener {
    private static final String TAG = CalenderActivity.class.getSimpleName();
    private GridView mCalendar;
    private AppCompatImageView mPrevious, mNext;
    private TextView mMonthName;
    private static TextView countText;
    protected static ListView mSavedEventsListView;
    public static ArrayList<Integer> mSavedEventDays;
    private ArrayList<Event_> eventArrayList;
    protected static int mNumEventsOnDay = 0;
    protected static EventListAdapter mSavedEventsAdapter;
    private CustomCalendarAdapter mMaterialCalendarAdapter;
    private static Activity activity;
    private FloatingActionButton fab;
    Toolbar toolbar;
    Teams_ team_;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;

    @SuppressLint({"InflateParams", "RestrictedApi"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_calender);

        activity = CalenderActivity.this;
        toolbar = (Toolbar) findViewById(R.id.calender_layout_toolbar);
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

        eventArrayList = new ArrayList<>();

        Preferences.initialize(getApplicationContext());

        CustomCalendar.getInitialCalendarInfo();
        countText = (TextView) findViewById(R.id.calendar_fragment_invite_counter);
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
        mSavedEventsListView = (ListView) findViewById(R.id.saved_events_listView);
        mSavedEventsListView.setDividerHeight(0);

        AppCompatImageView inviteIcon = (AppCompatImageView) findViewById(R.id.calendar_fragment_invitation_icon);
        inviteIcon.setOnClickListener(this);

        fab = (FloatingActionButton) findViewById(R.id.calendar_fragment_add_float);
        fab.setOnClickListener(this);
        /*if(Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015))){
            if (SosValidation.checkId(Integer.parseInt(Preferences.get(General.ROLE_ID)))
                    || Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1")) {
                fab.setVisibility(View.VISIBLE);
            } else {
                fab.setVisibility(View.GONE);
            }
        } else {
            if (SosValidation.checkId(Integer.parseInt(Preferences.get(General.ROLE_ID))) || SosValidation.checkParentId(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                fab.setVisibility(View.VISIBLE);
            } else {
                fab.setVisibility(View.GONE);
            }
        }*/

        //Changed after discussion with Sagar and Nirmal as Sagar have implemented owner_id and isModerator on backend for all instances
        if (Preferences.get(General.OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID))
                || Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1")
                || Preferences.get(General.IS_CC).equalsIgnoreCase("1")
                || Preferences.get(General.ROLE_ID).equals(General.UserRoleMHAW_CPST_Clinician)) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.INVISIBLE);
        }

        Intent data = getIntent();
        if (data != null && data.hasExtra(General.TEAM)) {
            team_ = (Teams_) data.getSerializableExtra(General.TEAM);
        } else {
            onBackPressed();
        }
        if (team_ == null) {
            onBackPressed();
        }

        errorText = (TextView) findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) findViewById(R.id.imageview_error_icon);
        errorLayout = (LinearLayout) findViewById(R.id.linealayout_error);
    }

    // Make network call to get calendar counters
    static void getCounters() {
        mSavedEventDays = new ArrayList<>();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, "calendar_counts");
        requestMap.put("year", "" + CustomCalendar.mYear);
        requestMap.put("month", "" + (CustomCalendar.mMonth + 1));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, Preferences.get(General.TEAM_ID));
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CALENDAR;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    String calendar_counts = jsonObject.get("calendar_counts").getAsString();
                    if (calendar_counts.trim().length() > 0) {
                        List<String> countList = Arrays.asList(calendar_counts.split("\\s*,\\s*"));
                        for (int i = 0; i < countList.size(); i++) {
                            mSavedEventDays.add(Integer.parseInt(countList.get(i)));
                        }
                    }
                    int invite_count = jsonObject.get("invitation").getAsInt();
                    if (invite_count <= 0) {
                        countText.setVisibility(View.GONE);
                    } else {
                        countText.setVisibility(View.VISIBLE);
                        countText.setText(GetCounters.convertCounter(invite_count));
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Make network call to fetch event records from server
    private void getEvents(int position) {
        int selectedDate = position - (6 + CustomCalendar.mFirstDay);
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_EVENTS);
        requestMap.put("date", CustomCalendar.mYear + "-" + (CustomCalendar.mMonth + 1) + "-" + selectedDate);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, Preferences.get(General.TEAM_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CALENDAR;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    eventArrayList = Alerts_.parseEvents(response, getApplicationContext(), TAG);
                    if (eventArrayList.size() > 0) {
                        if (eventArrayList.get(0).getStatus() == 1) {
                            saveEventList(eventArrayList);
                            errorLayout.setVisibility(View.GONE);
                            mSavedEventsListView.setVisibility(View.VISIBLE);
                            mSavedEventsAdapter = new EventListAdapter(CalenderActivity.this, eventArrayList, this);
                            mSavedEventsListView.setAdapter(mSavedEventsAdapter);
                        } else {
                            errorLayout.setVisibility(View.VISIBLE);
                            mSavedEventsListView.setVisibility(View.GONE);
                        }
                    } else {
                        errorLayout.setVisibility(View.VISIBLE);
                        mSavedEventsListView.setVisibility(View.GONE);
                    }
                } else {
                    errorLayout.setVisibility(View.VISIBLE);
                    mSavedEventsListView.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Preferences.save(General.IS_FROM_CASELOAD, true);
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        fab.setBackgroundTintList(ColorStateList.valueOf(GetColor.getHomeIconBackgroundColorColorParse(true)));

        if (mSavedEventsListView != null) {
            mSavedEventsAdapter = new EventListAdapter(CalenderActivity.this, eventArrayList, this);
            mSavedEventsListView.setAdapter(mSavedEventsAdapter);
            mSavedEventsListView.setOnItemClickListener(this);
            // Show current day saved events on load
            int today = CustomCalendar.mCurrentDay + 6 + CustomCalendar.mFirstDay;
            //  showSavedEventsListView(today);
            getEvents(today);
        }

        getCounters();
    }


    /*@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mSavedEventsListView != null) {
            mSavedEventsAdapter = new EventListAdapter(CalenderActivity.this, eventArrayList);
            mSavedEventsListView.setAdapter(mSavedEventsAdapter);
            mSavedEventsListView.setOnItemClickListener(this);
            // Show current day saved events on load
            int today = CustomCalendar.mCurrentDay + 6 + CustomCalendar.mFirstDay;
            //  showSavedEventsListView(today);
            getEvents(today);
        }
    }*/

    @Override
    public void onClick(View view) {
        if (view != null) {
            switch (view.getId()) {
                case R.id.calendar_fragment_month_back:
                    mSavedEventDays.clear();
                    eventArrayList.clear();
                    mSavedEventsAdapter.notifyDataSetChanged();
                    CustomCalendar.previousOnClick(mPrevious, mMonthName, mCalendar, mMaterialCalendarAdapter);
                    break;
                case R.id.calendar_fragment_month_next:
                    mSavedEventDays.clear();
                    eventArrayList.clear();
                    mSavedEventsAdapter.notifyDataSetChanged();
                    CustomCalendar.nextOnClick(mNext, mMonthName, mCalendar, mMaterialCalendarAdapter);
                    break;
                case R.id.calendar_fragment_invitation_icon:
                    Intent detailsIntent = new Intent(getApplicationContext(), InviteListActivity.class);
                    detailsIntent.putExtra(General.TEAM_ID, Preferences.get(General.TEAM_ID));
                    startActivity(detailsIntent);
                    overridePendingTransition(0, 0);
                    break;
                case R.id.calendar_fragment_add_float:
                    Intent createIntent = new Intent(getApplicationContext(), CreateEventActivity.class);
                    createIntent.putExtra(General.TEAM_ID, Preferences.get(General.GROUP_ID));
                    startActivity(createIntent);
                    overridePendingTransition(0, 0);
                    break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.calendar_fragment_grid_view:
                CustomCalendar.selectCalendarDay(mMaterialCalendarAdapter, position);
                mNumEventsOnDay = -1;
                getEvents(position);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        finish();
    }

    // Save newly added records to database
    private void saveEventList(final ArrayList<Event_> list) {
        final Handler handler = new Handler();  //Optional. Define as a variable in your activity.
        Runnable r = new Runnable() {
            @Override
            public void run() {
                DatabaseInsert_ databaseInsert_ = new DatabaseInsert_(activity.getApplicationContext());
                for (int i = 0; i < list.size(); i++) {
                    databaseInsert_.insertEvent(list.get(i));
                }
                handler.post(new Runnable()  //If you want to update the UI, queue the code on the UI thread
                {
                    public void run() {
                        //Code to update the UI
                    }
                });
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    @Override
    public void onItemClicked(int position, String itemView) {
        if (itemView.equalsIgnoreCase("location")) {
            Intent meetIntent = new Intent(activity.getApplicationContext(), PlannerEventLocationActivity.class);
            meetIntent.putExtra(General.LOCATION, eventArrayList.get(position).getLocation());
            activity.startActivity(meetIntent);
            activity.overridePendingTransition(0, 0);
        }
    }
}

