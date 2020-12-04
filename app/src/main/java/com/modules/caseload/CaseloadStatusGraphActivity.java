package com.modules.caseload;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.network.MakeCall;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.storage.preferences.Preferences;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author girish M (girish@sagesurfer.com)
 * Created on 05/05/2017.
 * Last Modified on 05/05/2017.
 */

public class CaseloadStatusGraphActivity extends AppCompatActivity {//implements View.OnClickListener

    private static final String TAG = CaseloadStatusGraphActivity.class.getSimpleName();

    @BindView(R.id.webview_missed_dosages)
    WebView webViewMissedDosages;
    @BindView(R.id.webview_phq9)
    WebView webViewPHQ9;
    @BindView(R.id.webview_msq)
    WebView webViewMSQ;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    Toolbar toolbar;

    @SuppressWarnings("deprecation")
    @Override
    public void onStart() {
        super.onStart();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        progressBar.setVisibility(View.VISIBLE);
        fetchCaseloadStatusMissedDosagesGraph();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.fragment_caseload_status_graph_dialog);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.caseload_status_toolbar);
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
    }

    //make network call get Number of Missed Dosages graph from server
    private void fetchCaseloadStatusMissedDosagesGraph() {
        /*webViewMissedDosages.setWebChromeClient(new WebChromeClient());
        webViewMissedDosages.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webViewMissedDosages.getSettings().setLoadsImagesAutomatically(true);
        webViewMissedDosages.getSettings().setJavaScriptEnabled(true);
        webViewMissedDosages.getSettings().setDomStorageEnabled(true);
        webViewMissedDosages.getSettings().setUseWideViewPort(true);
        webViewMissedDosages.getSettings().setAllowFileAccessFromFileURLs(true);
        webViewMissedDosages.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webViewMissedDosages.setVerticalScrollBarEnabled(false);
        webViewMissedDosages.setHorizontalScrollBarEnabled(false);
        webViewMissedDosages.getSettings().setLoadsImagesAutomatically(true);
        webViewMissedDosages.getSettings().setBuiltInZoomControls(true);*/
        webViewMissedDosages.getSettings().setLoadWithOverviewMode(true);
        webViewMissedDosages.setInitialScale(200);
        webViewMissedDosages.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webViewMissedDosages.setScrollbarFadingEnabled(false);
        webViewMissedDosages.setWebChromeClient(new WebChromeClient());
        webViewMissedDosages.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webViewMissedDosages.getSettings().setLoadsImagesAutomatically(true);
        webViewMissedDosages.getSettings().setJavaScriptEnabled(true);
        webViewMissedDosages.getSettings().setDomStorageEnabled(true);
        webViewMissedDosages.getSettings().setUseWideViewPort(true);
        webViewMissedDosages.getSettings().setAllowFileAccessFromFileURLs(true);
        webViewMissedDosages.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webViewMissedDosages.setVerticalScrollBarEnabled(false);
        webViewMissedDosages.setHorizontalScrollBarEnabled(false);
        webViewMissedDosages.getSettings().setLoadsImagesAutomatically(true);
        webViewMissedDosages.getSettings().setBuiltInZoomControls(true);
        webViewMissedDosages.getSettings().setUserAgentString("Android WebView");

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_MISSED_DOSAGES_GRAPH);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, Preferences.get(General.TEAM_ID));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                Request request_new = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .tag(TAG)
                        .build();
                url = url + "?" + MakeCall.bodyToString(request_new);

                Log.e(TAG, "Url -> " + url);
                webViewMissedDosages.loadUrl(url);

                fetchCaseloadStatusPHQ9();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //make network call get PHQ9 graph from server
    private void fetchCaseloadStatusPHQ9() {
        /*webViewPHQ9.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webViewPHQ9.getSettings().setJavaScriptEnabled(true);
        webViewPHQ9.getSettings().setDomStorageEnabled(true);
        webViewPHQ9.getSettings().setUseWideViewPort(true);
        webViewPHQ9.getSettings().setAllowFileAccessFromFileURLs(true);
        webViewPHQ9.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webViewPHQ9.setVerticalScrollBarEnabled(false);
        webViewPHQ9.setHorizontalScrollBarEnabled(false);
        webViewPHQ9.getSettings().setLoadsImagesAutomatically(true);
        webViewPHQ9.getSettings().setBuiltInZoomControls(true);*/

        webViewPHQ9.getSettings().setLoadWithOverviewMode(true);
        webViewPHQ9.setInitialScale(200);
        webViewPHQ9.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webViewPHQ9.setScrollbarFadingEnabled(false);
        webViewPHQ9.setWebChromeClient(new WebChromeClient());
        webViewPHQ9.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webViewPHQ9.getSettings().setLoadsImagesAutomatically(true);
        webViewPHQ9.getSettings().setJavaScriptEnabled(true);
        webViewPHQ9.getSettings().setDomStorageEnabled(true);
        webViewPHQ9.getSettings().setUseWideViewPort(true);
        webViewPHQ9.getSettings().setAllowFileAccessFromFileURLs(true);
        webViewPHQ9.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webViewPHQ9.setVerticalScrollBarEnabled(false);
        webViewPHQ9.setHorizontalScrollBarEnabled(false);
        webViewPHQ9.getSettings().setLoadsImagesAutomatically(true);
        webViewPHQ9.getSettings().setBuiltInZoomControls(true);
        webViewPHQ9.getSettings().setUserAgentString("Android WebView");

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PHQ9_GRAPH);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, Preferences.get(General.TEAM_ID));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                Request request_new = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .tag(TAG)
                        .build();
                url = url + "?" + MakeCall.bodyToString(request_new);
                Log.e(TAG, "Url -> " + url);
                webViewPHQ9.loadUrl(url);

                fetchCaseloadStatusMSQ();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //make network call get MSQ graph from server
    private void fetchCaseloadStatusMSQ() {
        /*webViewMSQ.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                *//*if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }*//*
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity.getApplicationContext(), "Error:" + description, Toast.LENGTH_SHORT).show();

            }
        });
        webViewMSQ.getSettings().setJavaScriptEnabled(true);
        webViewMSQ.getSettings().setDomStorageEnabled(true);
        webViewMSQ.getSettings().setUseWideViewPort(true);
        webViewMSQ.getSettings().setAllowFileAccessFromFileURLs(true);
        webViewMSQ.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webViewMSQ.setVerticalScrollBarEnabled(false);
        webViewMSQ.setHorizontalScrollBarEnabled(false);
        webViewMSQ.getSettings().setLoadsImagesAutomatically(true);
        webViewMSQ.getSettings().setBuiltInZoomControls(true);*/
        webViewMSQ.getSettings().setLoadWithOverviewMode(true);
        webViewMSQ.setInitialScale(200);
        webViewMSQ.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webViewMSQ.setScrollbarFadingEnabled(false);
        webViewMSQ.setWebChromeClient(new WebChromeClient());
        webViewMSQ.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webViewMSQ.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), "Error:" + description, Toast.LENGTH_SHORT).show();

            }
        });
        webViewMSQ.getSettings().setLoadsImagesAutomatically(true);
        webViewMSQ.getSettings().setJavaScriptEnabled(true);
        webViewMSQ.getSettings().setDomStorageEnabled(true);
        webViewMSQ.getSettings().setUseWideViewPort(true);
        webViewMSQ.getSettings().setAllowFileAccessFromFileURLs(true);
        webViewMSQ.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webViewMSQ.setVerticalScrollBarEnabled(false);
        webViewMSQ.setHorizontalScrollBarEnabled(false);
        webViewMSQ.getSettings().setLoadsImagesAutomatically(true);
        webViewMSQ.getSettings().setBuiltInZoomControls(true);
        webViewMSQ.getSettings().setUserAgentString("Android WebView");

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_MSQ_GRAPH);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, Preferences.get(General.TEAM_ID));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                Request request_new = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .tag(TAG)
                        .build();
                url = url + "?" + MakeCall.bodyToString(request_new);
                Log.e(TAG, "Url -> " + url);
                webViewMSQ.loadUrl(url);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
