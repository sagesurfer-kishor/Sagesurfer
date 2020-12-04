package com.modules.mood;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.modules.calendar.CalendarFragment;
import com.modules.calendar.CustomCalendar;
import com.modules.calendar.CustomCalendarAdapter;
import com.sagesurfer.adapters.MoodCalendarListAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.MoodJournalDataMood_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.MoodParser_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * Created by Monika on 11/13/2018.
 */

public class MoodCalendarFragment extends Fragment implements GridView.OnItemClickListener{
    private static final String TAG = MoodCalendarFragment.class.getSimpleName();

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    public ArrayList<MoodJournalDataMood_> moodCalendarArrayList = new ArrayList<MoodJournalDataMood_>();
    protected static MoodCalendarListAdapter moodCalendarListAdapter;
    private Activity activity;

    private GridView mCalendar;
    private CustomCalendarAdapter mMaterialCalendarAdapter;
    protected static int mNumEventsOnDay = 0;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.fragment_mood_calendar, null);
        activity = getActivity();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);

        //CustomCalendar.getInitialCalendarInfo();
        CalendarFragment.mSavedEventDays = new ArrayList<>(); //reinitialise for not showing event data present dot
        mCalendar = (GridView) view.findViewById(R.id.calendar_fragment_grid_view);
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
        listView = (ListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(0);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (listView != null) {
            moodCalendarListAdapter = new MoodCalendarListAdapter(getActivity(), moodCalendarArrayList);
            listView.setAdapter(moodCalendarListAdapter);
            // Show current day mood
            int today = CustomCalendar.mCurrentDay + 6 + CustomCalendar.mFirstDay;
            getMoods(today);
        }
    }

    //Make network call to fetch event records from server
    public void getMoods(int position) {
        int status = 11;
        String month = GetCounters.checkDigit(CustomCalendar.mMonth + 1);
        int selectedDate = position - (6 + CustomCalendar.mFirstDay);
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.MOOD_STATUS);
        requestMap.put(General.DATE, CustomCalendar.mYear + "-" + month + "-" + selectedDate);
        requestMap.put(General.MONTH, "" + month);
        requestMap.put(General.YEAR, "" + CustomCalendar.mYear);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_MOOD_DASHBOARD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if(response != null) {
                    moodCalendarArrayList = MoodParser_.parseCalendar(response, Actions_.MOOD_STATUS, activity.getApplicationContext(), TAG);
                    if (moodCalendarArrayList.size() > 0) {
                        if (moodCalendarArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            moodCalendarListAdapter = new MoodCalendarListAdapter(activity, moodCalendarArrayList);
                            listView.setAdapter(moodCalendarListAdapter);
                        } else {
                            showError(true, moodCalendarArrayList.get(0).getStatus());
                        }
                    } else {
                        showError(true, 2);
                    }
                } else {
                    showError(true, 11);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showError(true, status);
        }
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, activity.getApplicationContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.calendar_fragment_grid_view:
                CustomCalendar.selectCalendarDay(mMaterialCalendarAdapter, position);
                mNumEventsOnDay = -1;
                getMoods(position);
                break;
            default:
                break;
        }
    }
}
