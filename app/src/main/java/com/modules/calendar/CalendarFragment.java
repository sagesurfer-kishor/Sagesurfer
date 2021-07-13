package com.modules.calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.modules.planner.PlannerEventLocationActivity;
import com.modules.team.TeamDetailsActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.models.Event_;
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

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 09-08-2017
 * Last Modified on 13-12-2017
 */

@SuppressLint("StaticFieldLeak")
public class CalendarFragment extends Fragment implements View.OnClickListener, GridView.OnItemClickListener, EventListAdapter.EventListAdapterListener {
    private static final String TAG = CalendarFragment.class.getSimpleName();

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
    private MainActivityInterface mainActivityInterface;
    private FloatingActionButton fab;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        //noinspection deprecation
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View rootView = inflater.inflate(R.layout.calendar_fragment_layout, container, false);

        activity = getActivity();
        eventArrayList = new ArrayList<>();

        Preferences.initialize(activity.getApplicationContext());

        if (rootView != null) {
            CustomCalendar.getInitialCalendarInfo();
            countText = (TextView) rootView.findViewById(R.id.calendar_fragment_invite_counter);
            mPrevious = (AppCompatImageView) rootView.findViewById(R.id.calendar_fragment_month_back);
            if (mPrevious != null) {
                mPrevious.setOnClickListener(this);
            }
            mMonthName = (TextView) rootView.findViewById(R.id.calendar_fragment_month_name);
            if (mMonthName != null) {
                Calendar cal = Calendar.getInstance();
                if (cal != null) {
                    String monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + cal.get(Calendar.YEAR);
                    mMonthName.setText(monthName);
                }
            }
            mNext = (AppCompatImageView) rootView.findViewById(R.id.calendar_fragment_month_next);
            if (mNext != null) {
                mNext.setOnClickListener(this);
            }
            mCalendar = (GridView) rootView.findViewById(R.id.calendar_fragment_grid_view);
            if (mCalendar != null) {
                mCalendar.setOnItemClickListener(this);
                mMaterialCalendarAdapter = new CustomCalendarAdapter(getActivity());
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
            mSavedEventsListView = (ListView) rootView.findViewById(R.id.saved_events_listView);
            mSavedEventsListView.setDividerHeight(0);

            AppCompatImageView inviteIcon = (AppCompatImageView) rootView.findViewById(R.id.calendar_fragment_invitation_icon);
            inviteIcon.setOnClickListener(this);

            fab = (FloatingActionButton) rootView.findViewById(R.id.calendar_fragment_add_float);
            fab.setOnClickListener(this);

            //Changed after discussion with Sagar and Nirmal as Sagar have implemented owner_id and isModerator on backend for all instances
            if ((Preferences.get(General.GROUP_OWNER_ID) != null
                    &&
                    Preferences.get(General.GROUP_OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID)))
                    || (Preferences.get(General.IS_MODERATOR) != null && Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1"))
                    || (Preferences.get(General.IS_CASE_MANAGER) != null && Preferences.get(General.IS_CASE_MANAGER).equalsIgnoreCase("1"))
                    || (Preferences.get(General.IS_CC) != null && Preferences.get(General.IS_CC).equalsIgnoreCase("1"))
                    || General.isCurruntUserHasPermissionToCreateEvent()) {
                fab.setVisibility(View.VISIBLE);
            } else {
                fab.setVisibility(View.GONE);
            }





            // added by mayur
            if (Preferences.get(General.ROLE).equalsIgnoreCase("Lead Peer Support Specialist")
                    || Preferences.get(General.ROLE).equalsIgnoreCase("Coach")
                    || Preferences.get(General.ROLE).equalsIgnoreCase("System Administrator")
                    || Preferences.get(General.ROLE).equalsIgnoreCase("Peer Mentor")
                    || Preferences.get(General.ROLE).equalsIgnoreCase("Care Coordinator")
                    || Preferences.get(General.ROLE).equalsIgnoreCase("Case Manager")
                    || Preferences.get(General.ROLE).equalsIgnoreCase("Consumer-Adult")
                    || Preferences.get(General.ROLE).equalsIgnoreCase("Parent/Guardian")) {
                fab.setVisibility(View.VISIBLE);
            }

            errorText = (TextView) rootView.findViewById(R.id.textview_error_message);
            errorIcon = (AppCompatImageView) rootView.findViewById(R.id.imageview_error_icon);
            errorLayout = (LinearLayout) rootView.findViewById(R.id.linealayout_error);
        }
        return rootView;
    }

    // Make network call to get calendar counters
    static void getCounters() {
        mSavedEventDays = new ArrayList<>();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, "calendar_counts");
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));
        requestMap.put("year", "" + CustomCalendar.mYear);
        requestMap.put("month", "" + (CustomCalendar.mMonth + 1));
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
    public void getEvents(int position) {
        int selectedDate = position - (6 + CustomCalendar.mFirstDay);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_EVENTS);
        requestMap.put("date", CustomCalendar.mYear + "-" + (CustomCalendar.mMonth + 1) + "-" + selectedDate);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CALENDAR;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    eventArrayList = Alerts_.parseEvents(response, activity.getApplicationContext(), TAG);
                    if (eventArrayList.size() > 0) {
                        if (eventArrayList.get(0).getStatus() == 1) {
                            saveEventList(eventArrayList);
                            errorLayout.setVisibility(View.GONE);
                            mSavedEventsListView.setVisibility(View.VISIBLE);
                            mSavedEventsAdapter = new EventListAdapter(activity, eventArrayList, this);
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
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.calendar));
        mainActivityInterface.setToolbarBackgroundColor();
        fab.setBackgroundTintList(ColorStateList.valueOf(GetColor.getHomeIconBackgroundColorColorParse(true)));
        Preferences.save(General.IS_FROM_CASELOAD, false);
        getCounters();

        int today = CustomCalendar.mCurrentDay + 6 + CustomCalendar.mFirstDay;
        getEvents(today);

        try{
            ((TeamDetailsActivity) getActivity()).inviteButtonSetVisibility();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mSavedEventsListView != null) {
            mSavedEventsAdapter = new EventListAdapter(getActivity(), eventArrayList, this);
            mSavedEventsListView.setAdapter(mSavedEventsAdapter);
            mSavedEventsListView.setOnItemClickListener(this);
            // Show current day saved events on load
            int today = CustomCalendar.mCurrentDay + 6 + CustomCalendar.mFirstDay;
            getEvents(today);
        }
    }

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
                    Intent detailsIntent = new Intent(activity.getApplicationContext(), InviteListActivity.class);
                    detailsIntent.putExtra(General.TEAM_ID, Preferences.get(General.GROUP_ID));
                    startActivity(detailsIntent);
                    activity.overridePendingTransition(0, 0);
                    break;
                case R.id.calendar_fragment_add_float:
                    Intent createIntent = new Intent(activity.getApplicationContext(), CreateEventActivity.class);
                    createIntent.putExtra(General.TEAM_ID, Preferences.get(General.GROUP_ID));
                    startActivity(createIntent);
                    activity.overridePendingTransition(0, 0);
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

