package com.modules.re_assignment.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.modules.re_assignment.adapter.StudDetailsAdapter;
import com.modules.re_assignment.model.ReAssignment;
import com.modules.re_assignment.model.StudAssignData;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Journaling_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

public class ReAssignmentDetailsActivity extends AppCompatActivity {
    private static final String TAG = ReAssignmentDetailsActivity.class.getSimpleName();
    private Toolbar toolbar;
    private StudAssignData studAssignData;
    private TextView studentName;
    private RecyclerView studentRecyclerView;
    private LinearLayout errorLayout, detailsLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private StudDetailsAdapter studDetailsAdapter;
    private ArrayList<ReAssignment> reAssignmentDetails = new ArrayList<>();

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_re_assignment_details);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
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
        titleText.setText("Reassignment Details");
        titleText.setPadding(50,0,0,0);

        studentName = findViewById(R.id.student_name);
        errorLayout = (LinearLayout) findViewById(R.id.linealayout_error);
        errorText = (TextView) findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) findViewById(R.id.imageview_error_icon);
        detailsLayout = (LinearLayout) findViewById(R.id.details_layout);

        studentRecyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ReAssignmentDetailsActivity.this);
        studentRecyclerView.setLayoutManager(mLayoutManager);
        studentRecyclerView.setItemAnimator(new DefaultItemAnimator());

        Intent data = getIntent();
        if (data != null && data.hasExtra(General.REASSIGNMENT_DATA)) {
            studAssignData = (StudAssignData) data.getSerializableExtra(General.REASSIGNMENT_DATA);
            studentName.setText(ChangeCase.toTitleCase(studAssignData.getName()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        //load the data of student details
        assignmentStudentDetails();
    }

    private void assignmentStudentDetails() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, General.GET_REASSIGNMENT_DETAILS);
        requestMap.put(General.STUD_ID, String.valueOf(studAssignData.getId()));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_WERHOPE_ASSIGNMENT;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    reAssignmentDetails = Journaling_.parseReAssignment(response, this, TAG);
                    if (reAssignmentDetails.size() > 0) {
                        if (reAssignmentDetails.get(0).getStatus() == 1) {
                            showError(false, 1);
                            studDetailsAdapter = new StudDetailsAdapter(this, reAssignmentDetails);
                            studentRecyclerView.setAdapter(studDetailsAdapter);
                        } else {
                            showError(true, 2);
                            Toast.makeText(this, "No Coach reassignment available.", Toast.LENGTH_LONG).show();
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
        showError(true, 20);
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            studentRecyclerView.setVisibility(View.GONE);
            detailsLayout.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, this));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            studentRecyclerView.setVisibility(View.VISIBLE);
            detailsLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
