package com.modules.dashboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.ArrayOperations;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.network.MakeCall;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author girish M (girish@sagesurfer.com)
 *         Created on 06/06/17.
 *         Last Modified on 13/12/2017.
 */

public class ReportsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ReportsFragment.class.getSimpleName();
    private String consumer_id = "0";
    private int cWidth = 0, cHeight = 0;
    private ArrayList<String> dimensionArray, medicalDimensions;

    private Button msqButton, psqButton;
    private LinearLayout layoutOne, layoutTwo;
    private WebView clinicalWebView, medicalWebView;

    private Activity activity;
    private MainActivityInterface mainActivityInterface;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mainActivityInterface = (MainActivityInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MainActivityInterface");
        }
    }

    public static ReportsFragment newInstance(String _id) {
        ReportsFragment myFragment = new ReportsFragment();
        Bundle args = new Bundle();
        args.putString(General.ID, _id);
        myFragment.setArguments(args);
        return myFragment;
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.cg_dash_reports_layout, null);

        activity = getActivity();
        Preferences.initialize(activity.getApplicationContext());
        dimensionArray = new ArrayList<>();
        medicalDimensions = new ArrayList<>();

        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.reports));
        mainActivityInterface.setToolbarBackgroundColor();

        clinicalWebView = (WebView) view.findViewById(R.id.cg_dashboard_clinical_assessment_web_view);
        medicalWebView = (WebView) view.findViewById(R.id.cg_dashboard_medical_adherence_web_view);

        msqButton = (Button) view.findViewById(R.id.cg_dash_report_msq);
        msqButton.setOnClickListener(this);

        psqButton = (Button) view.findViewById(R.id.cg_dash_report_psq);
        psqButton.setOnClickListener(this);

        layoutOne = (LinearLayout) view.findViewById(R.id.web_view_one_layout);

        layoutTwo = (LinearLayout) view.findViewById(R.id.web_view_two_layout);

        Bundle data = getArguments();
        if (data.containsKey(General.ID)) {
            dimensions();
            dimensionsMedical();
            consumer_id = data.getString(General.ID);
        }

        layoutOne.post(new Runnable() {
            @Override
            public void run() {
                cWidth = layoutOne.getWidth();
                cHeight = layoutOne.getHeight();
                showClinical("msq");
            }
        });
        return view;
    }

    // used to set dimension for clinical web view report section
    private void dimensions() {
        dimensionArray.clear();
        dimensionArray.add(0, "0");
        dimensionArray.add(1, "0");

        ViewTreeObserver viewTreeObserver = clinicalWebView.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int height = clinicalWebView.getMeasuredHeight();
                int width = clinicalWebView.getMeasuredWidth();
                if (height != 0) {
                    clinicalWebView.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                if (height != 0 && width != 0) {
                    dimensionArray.clear();
                    dimensionArray.add(0, "" + width);
                    dimensionArray.add(1, "" + height);
                }
                return false;
            }
        });
    }

    // used to set dimension for medical web view report section
    private void dimensionsMedical() {
        medicalDimensions.clear();
        medicalDimensions.add(0, "0");
        medicalDimensions.add(1, "0");

        ViewTreeObserver viewTreeObserver = medicalWebView.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int height = medicalWebView.getMeasuredHeight();
                int width = medicalWebView.getMeasuredWidth();
                if (height != 0) {
                    medicalWebView.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                if (height != 0 && width != 0) {
                    medicalDimensions.clear();
                    medicalDimensions.add(0, "" + width);
                    medicalDimensions.add(1, "" + height);
                } else {
                    layoutTwo.post(new Runnable() {
                        @Override
                        public void run() {
                            medicalDimensions.clear();
                            medicalDimensions.add(0, "" + layoutTwo.getWidth());
                            medicalDimensions.add(1, "" + layoutTwo.getHeight());
                        }
                    });
                }
                return false;
            }
        });
    }

    // show msq/phq report in web view
    @SuppressLint("SetJavaScriptEnabled")
    private void showClinical(String type) {
        clinicalWebView.setWebViewClient(new WebViewClient());
        clinicalWebView.getSettings().setJavaScriptEnabled(true);
        clinicalWebView.getSettings().setDomStorageEnabled(true);
        clinicalWebView.getSettings().setUseWideViewPort(true);
        clinicalWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        clinicalWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        clinicalWebView.setVerticalScrollBarEnabled(false);
        clinicalWebView.setHorizontalScrollBarEnabled(false);
        clinicalWebView.getSettings().setLoadsImagesAutomatically(true);
        clinicalWebView.getSettings().setBuiltInZoomControls(true);
        String size = ArrayOperations.stringListToString(dimensionArray);
        if (size.equalsIgnoreCase("0,0")) {
            size = cWidth + "," + cHeight;
        }
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CARE_GIVER_FORM;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put("youth_id", consumer_id);
        requestMap.put(General.TYPE, type);
        requestMap.put(General.SIZE, size);
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);

        Request request_new = new Request.Builder()
                .url(url)
                .post(requestBody)
                .tag(TAG)
                .build();
        url = url + "?" + MakeCall.bodyToString(request_new);
        clinicalWebView.loadUrl(url);
        showMedical();
    }

    // show medication report in web view
    @SuppressLint("SetJavaScriptEnabled")
    private void showMedical() {
        medicalWebView.setWebViewClient(new WebViewClient());
        medicalWebView.getSettings().setJavaScriptEnabled(true);
        medicalWebView.getSettings().setDomStorageEnabled(true);
        medicalWebView.getSettings().setUseWideViewPort(true);
        medicalWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        medicalWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        medicalWebView.setVerticalScrollBarEnabled(false);
        medicalWebView.setHorizontalScrollBarEnabled(false);
        medicalWebView.getSettings().setLoadsImagesAutomatically(true);
        medicalWebView.getSettings().setBuiltInZoomControls(true);

        String size = ArrayOperations.stringListToString(medicalDimensions);
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CARE_GIVER_FORM;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put("youth_id", consumer_id);
        requestMap.put(General.TYPE, "medication");
        requestMap.put(General.SIZE, size);
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);

        Request request_new = new Request.Builder()
                .url(url)
                .post(requestBody)
                .tag(TAG)
                .build();
        url = url + "?" + MakeCall.bodyToString(request_new);
        medicalWebView.loadUrl(url);
    }

    private void toggleReportType(boolean isMsq) {
        if (isMsq) {
            msqButton.setBackgroundResource(R.drawable.primary_rounded_rectangle);
            psqButton.setBackgroundResource(R.drawable.white_rounded_rectangle);
        } else {
            psqButton.setBackgroundResource(R.drawable.primary_rounded_rectangle);
            msqButton.setBackgroundResource(R.drawable.white_rounded_rectangle);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cg_dash_report_msq:
                showClinical("msq");
                toggleReportType(true);
                break;
            case R.id.cg_dash_report_psq:
                showClinical("phq");
                toggleReportType(false);
                break;
        }
    }
}