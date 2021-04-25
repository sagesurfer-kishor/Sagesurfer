package com.modules.onetime_dailysurvey.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.modules.caseload.senjam.model.SenjamListModel;
import com.modules.onetime_dailysurvey.adapter.OneTimeSurveyDetailsAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

public class OnTimeSurveyDetailsActivity extends AppCompatActivity implements OneTimeSurveyDetailsAdapter.OneTimeDailySurveyDetailsAdapterListener {
    private static final String TAG = OnTimeSurveyDetailsActivity.class.getSimpleName();
    public ArrayList<SenjamListModel> senjamListModel = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private int added_by_id;


    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_senjam_sows_details);
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
        titleText.setText(getResources().getString(R.string.one_time_survey_details));

        initUI();

        //get Data from Intent and set into the adapter
        Intent data = getIntent();
        Bundle extra = getIntent().getExtras();

        if (extra != null){

            // this condition work when user come from notification oneTimeSurvey detail Api call and set Data
            // else it will set data into OneTimeSurveyDetailsAdapter
            if (data.hasExtra(General.SOWS_FROM_NOTIFICATION)) {
                senjamListModel = new ArrayList<>();
                added_by_id = data.getIntExtra("added_by_id", 0);
                onTimeSurveyDetailAPICalled();
            }
            else {
                senjamListModel = ((ArrayList<SenjamListModel>) getIntent().getSerializableExtra("senjamListModelDetailArrayList"));
                OneTimeSurveyDetailsAdapter oneTimeSurveyDetailsAdapter = new OneTimeSurveyDetailsAdapter(this, senjamListModel, this);
                recyclerView.setAdapter(oneTimeSurveyDetailsAdapter);
            }
        }



    }

    // Api call for One Time Survey List
    @SuppressLint("LongLogTag")
    private void onTimeSurveyDetailAPICalled() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.VIEW_ONE_TIME_DETAILS);
        requestMap.put(General.PATIENT_ID, String.valueOf(added_by_id));
        Log.e("requestMap", requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_SENJAM_SURVEY;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("onTimeSurveyListResponse", response);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getInt("status")==1) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Actions_.VIEW_ONE_TIME_DETAILS);
                        ArrayList<SenjamListModel> senjamListModelDetailArrayList = new ArrayList<>();
                        SenjamListModel model = new SenjamListModel();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            SenjamListModel model1 = new SenjamListModel();
                            model1.setQuestion(object.getString(General.QUESTION));
                            model1.setAns_id(object.getString(General.ANS_ID));
                            model1.setAns(object.getString(General.ANS));

                            senjamListModelDetailArrayList.add(model1);
                        }
                        model.setAdded_date(jsonObject.getInt(General.ADDED_DATE));
                        model.setStatus(jsonObject.getInt(General.STATUS));
                        senjamListModel.add(model);// data will bet Set into ArrayList

                        // ArrayList Bind Here and Set Data into OneTimeSurveyDetailsAdapter
                        OneTimeSurveyDetailsAdapter oneTimeSurveyDetailsAdapter = new OneTimeSurveyDetailsAdapter(this, senjamListModelDetailArrayList, this);
                        recyclerView.setAdapter(oneTimeSurveyDetailsAdapter);
                    }
                    else {
                        //showError(true,jsonObject.getInt("status"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("erroronTimeSurveyList", e.getMessage());
            }
        }
    }

    // Variable Declaration Function
    private void initUI() {
        recyclerView = findViewById(R.id.layout_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLinearLayoutManager);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onOneTimeDailySurveyNoteDetailsLayoutClicked(SenjamListModel senjamListModel) {

    }
}
