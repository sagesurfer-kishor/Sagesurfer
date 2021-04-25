package com.modules.assessment_screener;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Assessment_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

public class AssessmentListActivity extends AppCompatActivity {
    private static final String TAG = AssessmentListActivity.class.getSimpleName();
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout errorLayout;
    private TextView errorText, formCount;
    private AppCompatImageView errorIcon;
    public ArrayList<AssessmentScreener> assessmentScreenerList = new ArrayList<>();

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_assessment_list);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
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

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        titleText.setPadding(50, 0, 0, 0);
        titleText.setText(getResources().getString(R.string.assessment_screner));

        initUI();
    }

    private void initUI() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        listView = (ListView) findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(10);
        listView.setOnItemClickListener(onItemClick);

        errorLayout = (LinearLayout) findViewById(R.id.linealayout_error);
        errorText = (TextView) findViewById(R.id.textview_error_message);
        formCount = (TextView) findViewById(R.id.form_count);
        errorIcon = (AppCompatImageView) findViewById(R.id.imageview_error_icon);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAssessmentScreenerList();
    }

    @SuppressLint("SetTextI18n")
    private void getAssessmentScreenerList() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_ASSESSMENT_SCREENER);
        requestMap.put(General.CLIENT_ID, Preferences.get(General.NOTE_USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.v("LIST-->", response);
                if (response != null) {
                    assessmentScreenerList = Assessment_.parseAssessmentScreener(response, getApplicationContext(), TAG);
                    if (assessmentScreenerList.size() > 0) {
                        if (assessmentScreenerList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            formCount.setText("LIST OF SUBMITTED FORMS(" + assessmentScreenerList.size() + ")");
                            AssessmentScreenerAdapter assessmentScreenerAdapter = new AssessmentScreenerAdapter(this, assessmentScreenerList);
                            listView.setAdapter(assessmentScreenerAdapter);
                        } else {
                            showError(true, assessmentScreenerList.get(0).getStatus());
                        }
                    } else {
                        showError(true, 2);
                    }
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, getApplicationContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    // Handle on click for list item to open respective form in details
    private final AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent detailsIntent = new Intent(getApplicationContext(), AssessmentFormShowActivity.class);
            detailsIntent.putExtra(Actions_.SUBMITED_LIST, assessmentScreenerList.get(position));
            startActivity(detailsIntent);
        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
