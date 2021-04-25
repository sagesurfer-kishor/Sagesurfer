package com.modules.caseload;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
 import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.CSOverview_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.CSOverviewParser_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * Created by Monika on 6/04/2018.
 */

public class SummaryOverviewFragment extends Fragment {

    private static final String TAG = SummaryProfileFragment.class.getSimpleName();
    private ArrayList<CSOverview_> csOverviewArrayList;

    private LinearLayout errorLayout, overviewLayout;
    private RelativeLayout relativeLayoutSummary, relativeLayoutBackgroundHistory;
    private TextView errorText, summaryText, backgroundHistoryText, summaryTitleText, backgroundHistoryTitleText;
    private AppCompatImageView errorIcon;
    //private CardView cardviewSummary, cardviewBackground;

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

        View view = inflater.inflate(R.layout.fragment_summary_overview, null);
        activity = getActivity();

        //summaryArrayList = new ArrayList<>();

        int  color = GetColor.getHomeIconBackgroundColorColorParse(true);
        overviewLayout = (LinearLayout) view.findViewById(R.id.linearlayout_overview_details);
        //cardviewSummary = (CardView) view.findViewById(R.id.cardview_summary);
        //cardviewBackground = (CardView) view.findViewById(R.id.cardview_background_history);
        relativeLayoutSummary = (RelativeLayout) view.findViewById(R.id.relativelayout_summary);
        relativeLayoutBackgroundHistory = (RelativeLayout) view.findViewById(R.id.relativelayout_background_history);
        summaryTitleText = (TextView) view.findViewById(R.id.textview_summary_title);
        summaryTitleText.setTextColor(color);
        summaryTitleText.setText(getResources().getString(R.string.summary));
        summaryText = (TextView) view.findViewById(R.id.textview_summary);
        backgroundHistoryTitleText = (TextView) view.findViewById(R.id.textview_background_history_title);
        backgroundHistoryTitleText.setTextColor(color);
        backgroundHistoryTitleText.setText(getResources().getString(R.string.background_and_history));
        backgroundHistoryText = (TextView) view.findViewById(R.id.textview_background_history);

        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);

        return view;
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            overviewLayout.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, activity.getApplicationContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            overviewLayout.setVisibility(View.VISIBLE);

            if(csOverviewArrayList.get(0).getSummary().length() == 0) {
                relativeLayoutSummary.setVisibility(View.GONE);
            } else {
                summaryText.setText(csOverviewArrayList.get(0).getSummary());
            }

            if(csOverviewArrayList.get(0).getBackground().length() == 0) {
                relativeLayoutBackgroundHistory.setVisibility(View.GONE);
            } else {
                backgroundHistoryText.setText(csOverviewArrayList.get(0).getBackground());
            }

            if(relativeLayoutSummary.getVisibility() == View.GONE && relativeLayoutBackgroundHistory.getVisibility() == View.GONE) {
                errorLayout.setVisibility(View.VISIBLE);
                overviewLayout.setVisibility(View.GONE);
                errorText.setText(GetErrorResources.getMessage(status, activity.getApplicationContext()));
                errorIcon.setImageResource(GetErrorResources.getIcon(status));
            }
        }
    }

    //make network call fetch sos from server
    private void fetchCaseloadSummaryOverview() {
        int status = 11;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_OVERVIEW_DATA);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.GROUP_ID, Preferences.get(General.TEAM_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    csOverviewArrayList = CSOverviewParser_.parseCSOverview(response, Actions_.GET_OVERVIEW_DATA, activity.getApplicationContext(), TAG);
                    if (csOverviewArrayList.size() <= 0 || csOverviewArrayList.get(0).getStatus() != 1) {
                        if (csOverviewArrayList.size() <= 0) {
                            status = 12;
                        } else {
                            status = csOverviewArrayList.get(0).getStatus();
                        }
                        showError(true, status);
                        return;
                    }
                    showError(false, status);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchCaseloadSummaryOverview();
    }
}
