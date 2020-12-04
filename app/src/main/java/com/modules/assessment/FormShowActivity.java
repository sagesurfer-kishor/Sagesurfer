package com.modules.assessment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.content.ContextCompat;
 import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.logger.Logger;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Assessment_;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.tasks.PerformReadTask;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.RequestBody;


/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 19-07-2017
 * Last Modified on 13-12-2017
 */


public class FormShowActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = FormShowActivity.class.getSimpleName();

    private Forms_ forms_;
    Toolbar toolbar;
    String url = "", label = "", record_id = "", form_id = "";
    private ArrayList<Forms_> formsArrayList;
    private ListView listView;
    private LinearLayout swipeRefreshLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private WebView webView;
    private TextView formNo, formDate;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.web_view_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
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

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);

        TextView postButton = (TextView) findViewById(R.id.textview_activitytoolbar_post);
        postButton.setVisibility(View.GONE);

        AppCompatImageButton menuButton = (AppCompatImageButton) findViewById(R.id.imagebutton_activitytoolbar_menu);
        menuButton.setVisibility(View.GONE);


        webView = (WebView) findViewById(R.id.web_view_layout_view);
        listView = (ListView) findViewById(R.id.swipe_menu_listview);
        swipeRefreshLayout = (LinearLayout) findViewById(R.id.swipe_refresh_layout);
        listView.setDividerHeight(3);
        mSwipeRefreshLayout = findViewById(R.id.list_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        formNo = findViewById(R.id.form_no);
        formDate = findViewById(R.id.date);


        Intent data = getIntent();
        if (data.hasExtra(Actions_.GET_LIST)) {
            forms_ = (Forms_) data.getSerializableExtra(Actions_.GET_LIST);
            titleText.setText(forms_.getForm_name());
            record_id = String.valueOf(forms_.getRecord_id());
            showForm();
        } else if (data.hasExtra(Actions_.SUBMITED_LIST)) { //From Assessment Listing
            forms_ = (Forms_) data.getSerializableExtra(Actions_.SUBMITED_LIST);
            titleText.setText("FORM DETAILS");
            titleText.setPadding(120, 0, 0, 0);
            record_id = String.valueOf(forms_.getRecord_id());
            form_id = String.valueOf(forms_.getForm_id());

            swipeRefreshLayout.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);

            showSubmittedForm();

            formNo.setText(forms_.getForm_name());
            formDate.setText(getDate(forms_.getTimestamp()));

        } else if (data.hasExtra(General.ASSESSMENT_RECORD_ID)) { //From Push Notification Main Activity, NotificationFragment onItemClick
            record_id = data.getStringExtra(General.ASSESSMENT_RECORD_ID);
            label = data.getStringExtra("assessment_form_name");
            titleText.setText(label);
            showForm();
        } else {
            if (data.hasExtra(General.URL)) {
                url = data.getStringExtra(General.URL);
                label = data.getStringExtra(General.LABLE);
                titleText.setText(label);
                showCompareGraph();
            } else {
                onBackPressed();
            }
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }

    private void showSubmittedForm() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_FORM_DETAILS);
        requestMap.put("record_id", record_id);
        requestMap.put("form_id", form_id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_FORM_BUILDER;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    formsArrayList = Assessment_.parseSubmittedFormData(response, this, TAG);
                    if (formsArrayList.size() > 0) {
                        if (formsArrayList.get(0).getStatus() == 1) {
                            SubmittedFormListAdapter subFormListAdapter = new SubmittedFormListAdapter(this, formsArrayList);
                            listView.setAdapter(subFormListAdapter);
                        }
                    }
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Show form in web view by creating view url from available information
    @SuppressLint("SetJavaScriptEnabled")
    private void showForm() {
        String url = Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "")
                + "/front_render_native_form.php?record_id="
                + record_id + "&user_id=" + Preferences.get(General.USER_ID);
        Logger.debug(TAG, "Form URL: " + url, getApplicationContext());
        WebView webView = (WebView) findViewById(R.id.web_view_layout_view);
        webView.addJavascriptInterface(new WebAppInterface(getApplicationContext()), "Android");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl(url);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void showCompareGraph() {
        Logger.debug(TAG, "Form URL: " + url, getApplicationContext());
        WebView webView = (WebView) findViewById(R.id.web_view_layout_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl(url);
    }

    // Public class added to be called from web server javascript page
    // This class gives feature to close/dismiss form after successful submission
    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void startNewActivity() {
            close();
        }
    }

    // Close activity with positive feedback
    private void close() {
        ShowToast.toast("Thank you for submitting the form", getApplicationContext());
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        this.overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));

        // Make network call to read entry
        int status = PerformReadTask.readAlert("" + record_id, General.ASSESSMENT, TAG, getApplicationContext(), this);
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy | hh:mm a", cal).toString();
        return date;
    }
}
