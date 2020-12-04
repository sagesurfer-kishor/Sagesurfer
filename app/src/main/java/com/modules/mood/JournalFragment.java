package com.modules.mood;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.modules.calendar.CustomCalendar;
import com.sagesurfer.adapters.MoodJournalListAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.MoodJournal_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.MoodParser_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * Created by Monika on 11/13/2018.
 */

public class JournalFragment extends Fragment {
    private static final String TAG = JournalFragment.class.getSimpleName();

    private SwipeMenuListView listView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    public ArrayList<MoodJournal_> journalArrayList;

    private Activity activity;

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

        View view = inflater.inflate(R.layout.fragment_mood_journal, null);
        activity = getActivity();
        //CustomCalendar.getInitialCalendarInfo();

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);
        listView = (SwipeMenuListView) view.findViewById(R.id.swipe_menu_listview);
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
        fetchJournalData();
    }

    //  make network call to fetch journal data
    private void fetchJournalData() {
        int status = 11;
        String month = GetCounters.checkDigit(CustomCalendar.mMonth + 1);
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.MOOD_STATUS);
        requestMap.put(General.DATE, "" + 0);
        requestMap.put(General.MONTH, "" + month);
        requestMap.put(General.YEAR, "" + CustomCalendar.mYear);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_MOOD_DASHBOARD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                Log.e("responseJournal",response);
                if(response != null) {
                    journalArrayList = MoodParser_.parseJournal(response, Actions_.MOOD_STATUS, activity.getApplicationContext(), TAG);
                    if (journalArrayList.size() > 0) {
                        if (journalArrayList.get(0).getData().get(0).getMood().get(0).getStatus() == 1) {
                            showError(false, 1);
                            MoodJournalListAdapter journalListAdapter = new MoodJournalListAdapter(activity, journalArrayList.get(0).getData());
                            listView.setAdapter(journalListAdapter);
                        } else {
                            showError(true, journalArrayList.get(0).getData().get(0).getMood().get(0).getStatus());
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
            listView.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, activity.getApplicationContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }
}
