package com.modules.caseload.werhope.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.modules.caseload.werhope.adapter.UpdateCountAdapter;
import com.modules.caseload.werhope.model.ProgressList;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.ProgressList_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

public class UpdateCountActivity extends AppCompatActivity {
    private static final String TAG = UpdateCountActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private int mId;
    public ArrayList<ProgressList> progressList = new ArrayList<>();
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;

    @SuppressLint({"SourceLockedOrientationActivity", "RestrictedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_update_count);
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
        titleText.setPadding(100, 0, 0, 0);
        titleText.setText(getResources().getString(R.string.progress_note_log));

        mDeclarationMethod();

        Bundle extra = getIntent().getExtras();
        mId = extra.getInt("progressNoteId");

        getUpdateCountAPI(String.valueOf(mId));
    }

    private void mDeclarationMethod() {

        mRecyclerView = findViewById(R.id.recycler_update_count);
        mRecyclerView.setBackgroundColor(this.getResources().getColor(R.color.screen_background));
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        errorText = (TextView) findViewById(R.id.swipe_refresh_recycler_view_error_message);
        errorIcon = (AppCompatImageView) findViewById(R.id.swipe_refresh_recycler_view__error_icon);
        errorLayout = (LinearLayout) findViewById(R.id.swipe_refresh_recycler_view_error_layout);
    }

    @SuppressLint("SetTextI18n")
    private void getUpdateCountAPI(String Id) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PROGRESS_NOTE_LOG_WER);
        requestMap.put(General.ID, Id);

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
//                Log.e("ResponseCount",response);
                if (response != null) {
                    progressList = ProgressList_.parseProgressList(response, Actions_.GET_PROGRESS_NOTE_LOG_WER, this, TAG);
                    if (progressList.size() > 0) {
                        if (progressList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            UpdateCountAdapter progressListAdapter = new UpdateCountAdapter(this, progressList);
                            mRecyclerView.setAdapter(progressListAdapter);
                        } else {
                            showError(true, progressList.get(0).getStatus());
                        }
                    } else {
                        showError(true, progressList.get(0).getStatus());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, this));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
