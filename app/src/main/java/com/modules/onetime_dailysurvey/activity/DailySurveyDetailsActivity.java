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
import android.widget.Toast;

import com.modules.caseload.senjam.model.SenjamListModel;
import com.modules.onetime_dailysurvey.adapter.DailySurveyDetailsAdapter;
import com.modules.onetime_dailysurvey.model.DailySurveyModel;
import com.modules.onetime_dailysurvey.model.QuestionModel;
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

public class DailySurveyDetailsActivity extends AppCompatActivity implements DailySurveyDetailsAdapter.DailySurveyDetailsAdapterListener {
    private static final String TAG = DailySurveyDetailsActivity.class.getSimpleName();
    public ArrayList<SenjamListModel> senjamListModel = new ArrayList<>();
    private RecyclerView recyclerView;
    private SenjamListModel listModel;
    private LinearLayoutManager mLinearLayoutManager;
    private String sowsID;
    private long refId;
    private int added_by_id;
    private ArrayList<DailySurveyModel> dailySurveyModelArrayList = new ArrayList<>();


    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_senjam_daily_survey_details);
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
        titleText.setText(getResources().getString(R.string.daily_survey_details));

        initUI();

        //get Data from Intent
        Intent data = getIntent();
        if (data.hasExtra(General.SOWS_DETAILS)) {

            // this condition work when user come from notification Daily Survey Detail Api call and set Data
            listModel = (SenjamListModel) data.getSerializableExtra(General.SOWS_DETAILS);
            assert listModel != null;
            sowsID = listModel.getId();
            dailySurveyDetailAPICalled(sowsID);
        } else if (data.hasExtra(General.SOWS_FROM_NOTIFICATION)) {
            refId = data.getLongExtra(General.SOWS_FROM_NOTIFICATION, 0);
            added_by_id = data.getIntExtra("added_by_id", 0);
            dailySurveyDetailAPICalled(String.valueOf(refId));
        }

    }

    // Variable Declaration Function
    private void initUI() {
        recyclerView = findViewById(R.id.layout_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLinearLayoutManager);

    }

    // Api call for Daily Survey Detail
    @SuppressLint("SetTextI18n")
    private void dailySurveyDetailAPICalled(String sowsID) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_DETAILS_DAILY_SURVEY);
        requestMap.put(General.ID, sowsID);
        Log.e("requestMap", requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_SENJAM_SURVEY;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("DetailsResponse", response);

                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject getDetailDailySurvey = jsonObject.getJSONObject(Actions_.GET_DETAILS_DAILY_SURVEY);


                    JSONObject rateYourImmuneFitness = getDetailDailySurvey.getJSONObject(General.RATE_YOUR_IMMUNE_FITNESS);
                    setQuestionListData(rateYourImmuneFitness);

                    JSONObject respiratoryInfectionSymptom = getDetailDailySurvey.getJSONObject(General.RESPIRATORY_INFECTION_SYMPTOM);
                    setQuestionListData(respiratoryInfectionSymptom);

                    JSONObject covid19SpecificQuestion = getDetailDailySurvey.getJSONObject(General.COVID_19_SPECIFIC_QUESTION);
                    setQuestionListData(covid19SpecificQuestion);

                    // ArrayList Bind here and Set into DailySurveyDetailsAdapter
                    DailySurveyDetailsAdapter oneTimeSurveyDetailsAdapter = new DailySurveyDetailsAdapter(this, dailySurveyModelArrayList, this);
                    recyclerView.setAdapter(oneTimeSurveyDetailsAdapter);

                } else {
                    Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("errorPNListActivity", e.getMessage());
            }
        }
    }

    // here set Question List from api and add into ArrayList
    private void setQuestionListData(JSONObject object) {
        ArrayList<QuestionModel> questionModelArrayList = new ArrayList<>();
        DailySurveyModel dailySurveyModel = new DailySurveyModel();
        try {

            JSONArray jsonArray = object.getJSONArray(General.QUESTIONS);

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject object1 = jsonArray.getJSONObject(j);
                QuestionModel questionModel = new QuestionModel();
                questionModel.setId(object1.getString(General.ID));
                questionModel.setQues(object1.getString(General.QUES));
                questionModel.setAns_id(object1.getString(General.ANS_ID));
                questionModel.setAnswer(object1.getString(General.ANSWER));
                questionModelArrayList.add(questionModel);
            }

            String title = object.getString(General.TITLE);
            dailySurveyModel.setTitle(title);
            dailySurveyModel.setDescription(object.getString(General.DESCRIPTION));
            dailySurveyModel.setQuestions(questionModelArrayList);
            dailySurveyModelArrayList.add(dailySurveyModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDailySurveyNoteDetailsLayoutClicked(SenjamListModel senjamListModel) {

    }
}
