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
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.modules.calendar.CustomCalendar;
import com.modules.team.ContactRecycleViewAdapter;
import com.sagesurfer.adapters.MoodStatsActivityCountAdapter;
import com.sagesurfer.adapters.MoodStatsMoodCountAdapter;
import com.sagesurfer.adapters.MoodStatsPendingEntriesAdapter;
import com.sagesurfer.collaborativecares.BuildConfig;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.MoodStats_;
import com.sagesurfer.network.MakeCall;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.MoodParser_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Monika on 11/13/2018.
 */

public class StatsFragment extends Fragment {
    private static final String TAG = StatsFragment.class.getSimpleName();

    @BindView(R.id.linearlayout_mood_stats)
    LinearLayout linearLayoutMoodStats;
    @BindView(R.id.linearlayout_mood)
    LinearLayout linearLayoutMood;
    @BindView(R.id.webview_mood)
    WebView webViewMood;
    @BindView(R.id.linearlayout_mood_count)
    LinearLayout linearLayoutMoodCount;
    @BindView(R.id.recycler_view_mood_count)
    RecyclerView recyclerViewMoodCount;
    @BindView(R.id.linearlayout_activity_count)
    LinearLayout linearLayoutActivityCount;
    @BindView(R.id.recycler_view_activity_count)
    RecyclerView recyclerViewActivityCount;
    @BindView(R.id.textview_no_activity)
    TextView textViewNoActivity;
    @BindView(R.id.linearlayout_pending_entries)
    LinearLayout linearLayoutPendingEntries;
    @BindView(R.id.recycler_view_pending_entries)
    RecyclerView recyclerViewPendingEntries;
    @BindView(R.id.textview_no_mood)
    TextView textViewNoMood;

    public ArrayList<MoodStats_> statsArrayList = new ArrayList<MoodStats_>();
    private Activity activity;
    private Unbinder unbinder;
    private MoodStatsMoodCountAdapter moodStatsMoodCountAdapter;
    private MoodStatsActivityCountAdapter moodStatsActivityCountAdapter;
    private MoodStatsPendingEntriesAdapter moodStatsPendingEntriesAdapter;

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

        View view = inflater.inflate(R.layout.fragment_mood_stats, null);
        activity = getActivity();
        unbinder = ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewMoodCount.setLayoutManager(layoutManager);
        moodStatsMoodCountAdapter = new MoodStatsMoodCountAdapter(activity, statsArrayList);
        recyclerViewMoodCount.setAdapter(moodStatsMoodCountAdapter);

        layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewActivityCount.setLayoutManager(layoutManager);
        moodStatsActivityCountAdapter = new MoodStatsActivityCountAdapter(activity, statsArrayList);
        recyclerViewActivityCount.setAdapter(moodStatsActivityCountAdapter);

        layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewPendingEntries.setLayoutManager(layoutManager);
        moodStatsPendingEntriesAdapter = new MoodStatsPendingEntriesAdapter(activity, statsArrayList);
        recyclerViewPendingEntries.setAdapter(moodStatsPendingEntriesAdapter);

        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
            linearLayoutPendingEntries.setVisibility(View.GONE);
        }
        else {
            linearLayoutPendingEntries.setVisibility(View.VISIBLE);
        }


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchMoodGraph();
        fetchMoodCountData();
        fetchActivityCountData();
        fetchPendingMoodData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    //make network call to get Mood graph from server
    private void fetchMoodGraph() {
        webViewMood.getSettings().setLoadWithOverviewMode(true);
        webViewMood.setInitialScale(70);
        webViewMood.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webViewMood.setScrollbarFadingEnabled(false);
        webViewMood.setWebChromeClient(new WebChromeClient());
        webViewMood.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webViewMood.getSettings().setLoadsImagesAutomatically(true);
        webViewMood.getSettings().setJavaScriptEnabled(true);
        webViewMood.getSettings().setDomStorageEnabled(true);
        webViewMood.getSettings().setUseWideViewPort(true);
        webViewMood.getSettings().setAllowFileAccessFromFileURLs(true);
        webViewMood.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webViewMood.setVerticalScrollBarEnabled(false);
        webViewMood.setHorizontalScrollBarEnabled(false);
        webViewMood.getSettings().setLoadsImagesAutomatically(true);
        webViewMood.getSettings().setBuiltInZoomControls(true);
        webViewMood.getSettings().setUserAgentString("Android WebView");

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_MOOD_GRAPH);
        requestMap.put(General.MONTH, "" + (CustomCalendar.mMonth + 1));
        requestMap.put(General.YEAR, "" + CustomCalendar.mYear);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_MOOD_DASHBOARD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                Request request_new = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .tag(TAG)
                        .build();
                url = url + "?" + MakeCall.bodyToString(request_new);

                Log.e(TAG, "Url -> " + url);
                webViewMood.loadUrl(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //  make network call to fetch mood count for a selected month
    private void fetchMoodCountData() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.MOOD_COUNT);
        requestMap.put(General.MONTH, "" + (CustomCalendar.mMonth + 1));
        requestMap.put(General.YEAR, "" + CustomCalendar.mYear);

        if (Preferences.get(General.CONSUMER_ID) == null) {
            requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        } else {
            requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        }


        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_MOOD_DASHBOARD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if(response != null) {
                    statsArrayList = MoodParser_.parseStatsCount(response, Actions_.MOOD_COUNT, activity.getApplicationContext(), TAG);
                    if (statsArrayList.size() > 0) {
                        if (statsArrayList.get(0).getStatus() == 1) {
                            moodStatsMoodCountAdapter = new MoodStatsMoodCountAdapter(activity, statsArrayList);
                            recyclerViewMoodCount.setAdapter(moodStatsMoodCountAdapter);
                        } else {
                            showError(false);
                        }
                    } else {
                        showError(false);
                    }
                } else {
                    showError(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showError(true);
        }
    }

    //  make network call to fetch activity count for a selected month
    private void fetchActivityCountData() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ACTIVITY_PERFORMED_COUNT);
        requestMap.put(General.MONTH, "" + (CustomCalendar.mMonth + 1));
        requestMap.put(General.YEAR, "" + CustomCalendar.mYear);
        if (Preferences.get(General.CONSUMER_ID) == null) {
            requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        } else {
            requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        }
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_MOOD_DASHBOARD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if(response != null) {
                    statsArrayList = MoodParser_.parseStatsCount(response, Actions_.ACTIVITY_PERFORMED_COUNT, activity.getApplicationContext(), TAG);
                    if (statsArrayList.size() > 0) {
                        if (statsArrayList.get(0).getStatus() == 1) {
                            moodStatsActivityCountAdapter = new MoodStatsActivityCountAdapter(activity, statsArrayList);
                            recyclerViewActivityCount.setAdapter(moodStatsActivityCountAdapter);
                        } else {
                            showError(true);
                        }
                    } else {
                        showError(true);
                    }
                } else {
                    showError(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showError(true);
        }
    }

    //  make network call to fetch activity count for a selected month
    private void fetchPendingMoodData() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.PENDING_MOOD);
        if (Preferences.get(General.CONSUMER_ID) == null) {
            requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        } else {
            requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        }
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_MOOD_DASHBOARD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if(response != null) {
                    statsArrayList = MoodParser_.parseStatsCount(response, Actions_.PENDING_MOOD, activity.getApplicationContext(), TAG);
                    if (statsArrayList.size() > 0) {
                        if (statsArrayList.get(0).getStatus() == 1) {
                            moodStatsPendingEntriesAdapter = new MoodStatsPendingEntriesAdapter(activity, statsArrayList);
                            recyclerViewPendingEntries.setAdapter(moodStatsPendingEntriesAdapter);
                        } else {
                            showError(true);
                        }
                    } else {
                        showError(true);
                    }
                } else {
                    showError(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showError(true);
        }
    }

    private void showError(boolean isMood) {
        if (isMood) {
            recyclerViewMoodCount.setVisibility(View.GONE);
            textViewNoMood.setVisibility(View.VISIBLE);
        } else {
            recyclerViewActivityCount.setVisibility(View.GONE);
            textViewNoActivity.setVisibility(View.VISIBLE);
        }
    }
}
