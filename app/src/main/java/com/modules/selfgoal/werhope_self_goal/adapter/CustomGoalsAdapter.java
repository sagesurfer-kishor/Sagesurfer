package com.modules.selfgoal.werhope_self_goal.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.models.Goal_;
import com.sagesurfer.network.MakeCall;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.storage.preferences.Preferences;

import java.util.HashMap;
import java.util.List;

import okhttp3.Request;
import okhttp3.RequestBody;


public class CustomGoalsAdapter extends ArrayAdapter<Goal_> {
    private static final String TAG = CustomGoalsAdapter.class.getSimpleName();
    private final List<Goal_> goalArrayList;
    private final Context mContext;
    private final Activity activity;
    private Boolean showGraph = false;

    public CustomGoalsAdapter(Activity activity, Boolean showGraph_, List<Goal_> goalArrayList) {
        super(activity, 0, goalArrayList);
        this.goalArrayList = goalArrayList;
        this.mContext = activity.getApplicationContext();
        this.activity = activity;
        this.showGraph = showGraph_;
    }

    @Override
    public int getCount() {
        return goalArrayList.size();
    }

    @Override
    public Goal_ getItem(int position) {
        if (goalArrayList != null && goalArrayList.size() > 0) {
            return goalArrayList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return goalArrayList.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.default_goal_list_item, parent, false);

            viewHolder.goalNames = (TextView) view.findViewById(R.id.goal_names);
            viewHolder.goalYesNoImg = (ImageView) view.findViewById(R.id.yes_no_goal_img);
            viewHolder.defaultGoalStatus = (LinearLayout) view.findViewById(R.id.linearlayout_default_goal_status);
            viewHolder.allAction = (LinearLayout) view.findViewById(R.id.all_action);
            viewHolder.defaultGoalDetails = (RelativeLayout) view.findViewById(R.id.default_goal_details_layout);
            viewHolder.webviewGoalStatus = (WebView) view.findViewById(R.id.webview_goal_status);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (goalArrayList.get(position).getStatus() == 1) {
            viewHolder.goalNames.setText(goalArrayList.get(position).getName());

            if (showGraph) {
                showGoalGraph(viewHolder, position);
            }

            viewHolder.defaultGoalDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showGoalGraph(viewHolder, position);
                }
            });

            viewHolder.allAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showGoalGraph(viewHolder, position);
                }
            });

            viewHolder.goalYesNoImg.setVisibility(View.GONE);

            viewHolder.goalYesNoImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.defaultGoalStatus.setVisibility(View.GONE);
                }
            });

            viewHolder.goalNames.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.defaultGoalStatus.setVisibility(View.GONE);
                }
            });
        }

        return view;
    }

    private void showGoalGraph(ViewHolder viewHolder, int position) {
        viewHolder.defaultGoalStatus.setVisibility(View.VISIBLE);

        viewHolder.webviewGoalStatus.getSettings().setLoadWithOverviewMode(true);
        viewHolder.webviewGoalStatus.setInitialScale(120);
        viewHolder.webviewGoalStatus.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        viewHolder.webviewGoalStatus.setScrollbarFadingEnabled(false);

        viewHolder.webviewGoalStatus.setWebChromeClient(new WebChromeClient());
        viewHolder.webviewGoalStatus.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        viewHolder.webviewGoalStatus.getSettings().setLoadsImagesAutomatically(true);
        viewHolder.webviewGoalStatus.getSettings().setJavaScriptEnabled(true);
        viewHolder.webviewGoalStatus.getSettings().setDomStorageEnabled(true);
        viewHolder.webviewGoalStatus.getSettings().setUseWideViewPort(true);
        viewHolder.webviewGoalStatus.getSettings().setAllowFileAccessFromFileURLs(true);
        viewHolder.webviewGoalStatus.getSettings().setAllowUniversalAccessFromFileURLs(true);
        viewHolder.webviewGoalStatus.setVerticalScrollBarEnabled(false);
        viewHolder.webviewGoalStatus.setHorizontalScrollBarEnabled(false);
        viewHolder.webviewGoalStatus.getSettings().setLoadsImagesAutomatically(true);
        viewHolder.webviewGoalStatus.getSettings().setBuiltInZoomControls(true);
        viewHolder.webviewGoalStatus.getSettings().setUserAgentString("Android WebView");

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PEER_PARTICIPANT);

        if (CheckRole.isWerHope(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        } else {
            requestMap.put(General.CONSUMER_ID, Preferences.get(General.USER_ID));
        }
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GOAL_ID, String.valueOf(goalArrayList.get(position).getId()));
        requestMap.put(General.HC_ROLE, Preferences.get(General.ROLE_ID));

        String url = Preferences.get(General.DOMAIN) + Urls_.PLATFORM_REPORTS;
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
                viewHolder.webviewGoalStatus.loadUrl(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private class ViewHolder {
        TextView goalNames;
        ImageView goalYesNoImg;
        LinearLayout defaultGoalStatus, allAction;
        RelativeLayout defaultGoalDetails;
        WebView webviewGoalStatus;
    }
}
