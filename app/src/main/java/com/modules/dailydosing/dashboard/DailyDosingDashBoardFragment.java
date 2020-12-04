package com.modules.dailydosing.dashboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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
import android.widget.ProgressBar;
 import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.modules.dailydosing.model.DailyDosing;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.network.MakeCall;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.ReportsParser_;
import com.sagesurfer.parser.SelfGoal_;
import com.sagesurfer.parser.SenjamDailyDosingList_;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Unbinder;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Monika on 8/3/2018.
 */

public class DailyDosingDashBoardFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = DailyDosingDashBoardFragment.class.getSimpleName();

    LinearLayout linearLayoutSpinnerPatient;
    TextView mTxtSpinnerPatient;
    LinearLayout linearLayoutDailyDosingDashBoard, linearLayoutDailyDosingStatus;
    TextView textViewLabel;
    WebView webViewDailyDosingStatus;
    ProgressBar progressBar;
    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private String selectedPatientId = "0";

    private LinearLayout errorLayout;
    private ArrayList<DailyDosing> dailyDosingArrayList = new ArrayList<>();
    private int PATIENT_REQUEST_CODE = 1;
    private static final String KEY_ID = "key_id";
    private static final String KEY_VALUE = "key_value";
    private CardView mCardViewPatientSpinner;
    private String name;
    public boolean isDailyDosingReport = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;
    }

    @Nullable
    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));
        isDailyDosingReport = getArguments().getBoolean("isDailyDosingReport");
        View view = inflater.inflate(R.layout.fragment_daily_dosing_dash_board, null);
        activity = getActivity();

        // this boolean is for check where patient click on daily dosing dashboard or daily dosing report and the will be set as per condition
        if (isDailyDosingReport) {
            mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.daily_dosing_report_title));
        } else {
            mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.daily_dosing_dashboard_title));
        }


        mVariableDeclarationFunction(view);

        mClickEventFunction();

        if (Preferences.get(General.ROLE_ID).equals(General.USER_ROLL_SENJAM_PATIENT)) {
            mCardViewPatientSpinner.setVisibility(View.GONE);
            linearLayoutDailyDosingDashBoard.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
            fetchPatientGraph();
        } else {
            errorLayout.setVisibility(View.VISIBLE);
            mCardViewPatientSpinner.setVisibility(View.VISIBLE);
            getSelectPatientData();
        }

        return view;
    }


    private void mVariableDeclarationFunction(View view) {

        linearLayoutSpinnerPatient = view.findViewById(R.id.linear_spinner_patient);
        mTxtSpinnerPatient = view.findViewById(R.id.spinner_patient);
        linearLayoutDailyDosingDashBoard = view.findViewById(R.id.linearlayout_daily_dosing_dashboard);
        linearLayoutDailyDosingStatus = view.findViewById(R.id.linearlayout_daily_dosing_status);
        webViewDailyDosingStatus = view.findViewById(R.id.webview_daily_dosing_status);
        textViewLabel = view.findViewById(R.id.textview_label);
        progressBar = view.findViewById(R.id.progress_bar);
        mCardViewPatientSpinner = view.findViewById(R.id.cardview_daily_dosing_patient_spinner);
        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);

        linearLayoutDailyDosingDashBoard.setVisibility(View.GONE);

        // this condition for set label where patient is from daily dosing report or daily dosing dashboard
        if (isDailyDosingReport) {
            textViewLabel.setText(activity.getResources().getString(R.string.daily_dosing_report));
        } else {
            textViewLabel.setText(activity.getResources().getString(R.string.daily_dosing_dashboard));
        }

    }

    private void mClickEventFunction() {

        mCardViewPatientSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, DailyDosingPatientSelectActivity.class);
                intent.putExtra("dailyDosingArrayList", dailyDosingArrayList);
                intent.putExtra("selectedPatientID", selectedPatientId);
                startActivityForResult(intent, PATIENT_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // this is for selected Client name and set name on spinner
        if (resultCode == PATIENT_REQUEST_CODE) {
            name = data.getStringExtra(KEY_VALUE);
            selectedPatientId = data.getStringExtra(KEY_ID);
            mTxtSpinnerPatient.setText(name);
            linearLayoutDailyDosingDashBoard.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
            fetchPatientGraph();
        }
    }

    // Api For fetching selected Patient List
    private void getSelectPatientData() {
        String action = Actions_.GET_CLIENT_FOR_GRAPH;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SENJAM_CHARTS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                Log.e("PatientListResponse", response);
                if (response != null) {
                    dailyDosingArrayList = SenjamDailyDosingList_.parseDailyDosingPatientList(response, action, activity, TAG);
                }
            } catch (Exception e) {
//                e.printStackTrace();
                Log.e("ErrorPatient", "" + e.getMessage());
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setToolbarBackgroundColor();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    //make network call get Number of Missed Dosages graph from server
    private void fetchPatientGraph() {
        int status = 11;
        webViewDailyDosingStatus.getSettings().setLoadWithOverviewMode(true);
        webViewDailyDosingStatus.setInitialScale(150);
        webViewDailyDosingStatus.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webViewDailyDosingStatus.setScrollbarFadingEnabled(false);

        webViewDailyDosingStatus.setWebChromeClient(new WebChromeClient());
        webViewDailyDosingStatus.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webViewDailyDosingStatus.getSettings().setLoadsImagesAutomatically(true);
        webViewDailyDosingStatus.getSettings().setJavaScriptEnabled(true);
        webViewDailyDosingStatus.getSettings().setDomStorageEnabled(true);
        webViewDailyDosingStatus.getSettings().setUseWideViewPort(true);
        webViewDailyDosingStatus.getSettings().setAllowFileAccessFromFileURLs(true);
        webViewDailyDosingStatus.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webViewDailyDosingStatus.setVerticalScrollBarEnabled(false);
        webViewDailyDosingStatus.setHorizontalScrollBarEnabled(false);
        webViewDailyDosingStatus.getSettings().setLoadsImagesAutomatically(true);
        webViewDailyDosingStatus.getSettings().setBuiltInZoomControls(true);
        webViewDailyDosingStatus.getSettings().setUserAgentString("Android WebView");

        HashMap<String, String> requestMap = new HashMap<>();

        // here if patient is click on daily dosing report then shows report graph
        // here if patient is click on daily dosing dashboard then shows report graph
        if (isDailyDosingReport) {
            requestMap.put(General.ACTION, Actions_.DAILY_DOSING_CLIENT_GRAPH);
        } else {
            requestMap.put(General.ACTION, Actions_.DAILY_DOSING_COMPLIANCE_GRAPH);
        }

        if (Preferences.get(General.ROLE_ID).equals(General.USER_ROLL_SENJAM_PATIENT)) {
            requestMap.put(General.CLIENT_ID, Preferences.get(General.USER_ID));
        } else {
            requestMap.put(General.CLIENT_ID, selectedPatientId);
        }


//        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_SENJAM_CHARTS;
        Log.e("URL", url);
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
                webViewDailyDosingStatus.loadUrl(url);
                progressBar.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
