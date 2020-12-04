package com.modules.onetime_dailysurvey.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.modules.caseload.senjam.model.SenjamListModel;
import com.modules.onetime_dailysurvey.adapter.DailySurveyAdapter;
import com.modules.onetime_dailysurvey.adapter.OneTimeSurveyAdapter;
import com.modules.sows.activity.AddSowsNoteActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.SenjamDoctorNoteList_;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

public class OnTimeSurveyListActivity extends AppCompatActivity implements OneTimeSurveyAdapter.OneTimeDailySurveyAdapterListener, DailySurveyAdapter.DailySurveyAdapterListener, View.OnClickListener {
    private static final String TAG = OnTimeSurveyListActivity.class.getSimpleName();
    private RecyclerView recyclerViewOneTime, recyclerViewDailySurvey;
    public LinearLayoutManager mLinearLayoutManager;
    public LinearLayout mLinearLayoutOneTimeSurvey, mLinearLayoutDailySurvey, mLinearLayoutSowsNotesText;
    public TextView mTextViewOneTimeSurvey, mTextViewDailySurvey, errorText;
    public LinearLayout mLinearLayoutSearch, errorLayout;
    public CardView mCardViewOneTime, mCardViewDailySurvey;
    public int isClickedOnOneTimeOrDailySurvery = 0;
    public EditText mEditTExtSearch;
    public FloatingActionButton createOneTimeOrDailySurvey;
    private AppCompatImageView errorIcon;
    private String mId = "";
    private String searchText;
    private ArrayList<SenjamListModel> senjamListModelArrayList = new ArrayList<>();
    private ArrayList<SenjamListModel> senjamListModelDetailArrayList = new ArrayList<>();
    private ArrayList<SenjamListModel> dailySurveyModelArrayList = new ArrayList<>();
    private SenjamListModel model = new SenjamListModel();


    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_senjam_daily_survey_list);
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
        titleText.setText(getResources().getString(R.string.caseload));

        initUI();

        // Condition Checked if user is patient then we have to pass patient_id as User_id and
        // if Login with Clinician than pass patient_id
        if (Preferences.get(General.ROLE_ID).equals(General.USER_ROLL_SENJAM_PATIENT)) {
            mId = Preferences.get(General.USER_ID);
        } else {
            mId = Preferences.get(General.CONSUMER_ID);
        }


        // Search Logic implemented here
        mEditTExtSearch.addTextChangedListener(new TextWatcherExtended() {
            @Override
            public void afterTextChanged(Editable s, boolean backSpace) {
                searchText = mEditTExtSearch.getText().toString().trim();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        ClickListener();
        isCurrentUserClinician();
        oneTimeSurveyListAPICalled();
        //dailySurveyListAPICalled();

    }

    // Variable Declaration Function
    private void initUI() {

        mLinearLayoutOneTimeSurvey = findViewById(R.id.linear_txt_one_time_survey);
        mLinearLayoutDailySurvey = findViewById(R.id.linear_txt_daily_survey);
        mLinearLayoutSearch = findViewById(R.id.linearlayout_search);
        mLinearLayoutSowsNotesText = findViewById(R.id.linear_sows_notes_txt_layout);
        mEditTExtSearch = findViewById(R.id.edittext_search);
        mCardViewOneTime = findViewById(R.id.cardView_one_time);
        mCardViewDailySurvey = findViewById(R.id.cardView_daily_survey);
        mTextViewOneTimeSurvey = findViewById(R.id.txt_one_time_survey);
        mTextViewDailySurvey = findViewById(R.id.txt_daily_survey);

        errorText = (TextView) findViewById(R.id.swipe_refresh_recycler_view_error_message);
        errorIcon = (AppCompatImageView) findViewById(R.id.swipe_refresh_recycler_view__error_icon);
        errorLayout = (LinearLayout) findViewById(R.id.swipe_refresh_recycler_view_error_layout);

        recyclerViewOneTime = findViewById(R.id.layout_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewOneTime.setLayoutManager(mLinearLayoutManager);

        recyclerViewDailySurvey = findViewById(R.id.layout_recycler_view_daily);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewDailySurvey.setLayoutManager(mLinearLayoutManager);


        mLinearLayoutOneTimeSurvey.setBackground(getResources().getDrawable(R.drawable.selector_sows));
        mTextViewOneTimeSurvey.setTextColor(getResources().getColor(R.color.white1));


        createOneTimeOrDailySurvey = (FloatingActionButton) findViewById(R.id.swipe_refresh_layout_recycler_view_float);
        createOneTimeOrDailySurvey.setImageResource(R.drawable.ic_add_white);
        createOneTimeOrDailySurvey.setOnClickListener(this);

        if ((Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031))) &&
                Preferences.get(General.ROLE_ID).equals(General.USER_ROLL_SENJAM_PATIENT)) {
            createOneTimeOrDailySurvey.setVisibility(View.VISIBLE);
        } else {
            createOneTimeOrDailySurvey.setVisibility(View.GONE);
        }
    }

    //    /*this function is to check Login with patient*/
    public boolean isCurrentUserPatient() {

        boolean hasPermissionShowHideLayout = false;

        if ((Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031))) &&
                Preferences.get(General.ROLE_ID).equals(General.USER_ROLL_SENJAM_PATIENT)) {

            mLinearLayoutSowsNotesText.setVisibility(View.GONE);
//            mLinearLayoutSearch.setVisibility(View.VISIBLE);
//            mLinearLayoutRecycleNotes.setVisibility(View.VISIBLE);
            mCardViewOneTime.setVisibility(View.VISIBLE);

            hasPermissionShowHideLayout = true;


        }

        return hasPermissionShowHideLayout;
    }

    /*this function is to check Login with patient*/
    public boolean isCurrentUserClinician() {

        boolean hasPermissionShowHideLayout = false;

        if ((Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031))) &&
                Preferences.get(General.ROLE_ID).equals(General.USER_ROLL_SENJAM_CLINICIAN)) {

            if (isClickedOnOneTimeOrDailySurvery == 0) {
                /*Show One Time Survey Layout*/
                mCardViewOneTime.setVisibility(View.VISIBLE);
                mCardViewDailySurvey.setVisibility(View.GONE);
            } else {
                /*Show Daily Survey Layout*/
                mCardViewDailySurvey.setVisibility(View.VISIBLE);
                mCardViewOneTime.setVisibility(View.GONE);

            }

            hasPermissionShowHideLayout = true;


        }
        return hasPermissionShowHideLayout;
    }

    private void ClickListener() {

        mLinearLayoutOneTimeSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isClickedOnOneTimeOrDailySurvery = 0;
                mLinearLayoutOneTimeSurvey.setBackground(getResources().getDrawable(R.drawable.selector_sows));
                mLinearLayoutDailySurvey.setBackground(getResources().getDrawable(R.drawable.selector_white_notes));
                mTextViewOneTimeSurvey.setTextColor(getResources().getColor(R.color.white1));
                mTextViewDailySurvey.setTextColor(getResources().getColor(R.color.colorPrimary));
                isCurrentUserClinician();
//                dailySurveyListAPICalled();
                                oneTimeSurveyListAPICalled();
            }
        });

        mLinearLayoutDailySurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isClickedOnOneTimeOrDailySurvery = 1;
                mLinearLayoutDailySurvey.setBackground(getResources().getDrawable(R.drawable.selector_notes));
                mLinearLayoutOneTimeSurvey.setBackground(getResources().getDrawable(R.drawable.selector_white_sows));
                mTextViewOneTimeSurvey.setTextColor(getResources().getColor(R.color.colorPrimary));
                mTextViewDailySurvey.setTextColor(getResources().getColor(R.color.white1));
                isCurrentUserClinician();
//                notesProgressListAPICalled();
                dailySurveyListAPICalled();
//                onTimeSurveyListAPICalled();
            }
        });
    }

    /*Api called for One Time Survey List*/
    @SuppressLint({"SetTextI18n", "LongLogTag"})
    private void oneTimeSurveyListAPICalled() {
        senjamListModelArrayList.clear();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.VIEW_ONE_TIME_DETAILS);
        requestMap.put(General.PATIENT_ID, Preferences.get(General.CONSUMER_ID));
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
                        senjamListModelArrayList.add(model);

                        // ArrayList Will be Bind here and set into the OneTimeSurveyAdapter
                        OneTimeSurveyAdapter oneTimeSurveyAdapter = new OneTimeSurveyAdapter(this, senjamListModelArrayList, this);
                        recyclerViewOneTime.setAdapter(oneTimeSurveyAdapter);
                        showError(false, 1);
                    }
                    else {
                        showError(true,jsonObject.getInt("status"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("erroronTimeSurveyList", e.getMessage());
            }
        }
    }

    // Api Call For Daily Survey List
    @SuppressLint("SetTextI18n")
    private void dailySurveyListAPICalled() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_LIST_OF_DAILY_SURVEY);
        requestMap.put(General.PATIENT_ID, Preferences.get(General.CONSUMER_ID));
        Log.e("requestMap", requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_SENJAM_SURVEY;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("dailySurveyListResponse", response);
                if (response != null) {
                    dailySurveyModelArrayList = SenjamDoctorNoteList_.parseDoctorSowsList(response, Actions_.GET_LIST_OF_DAILY_SURVEY, this, TAG);
                    if (dailySurveyModelArrayList.size() > 0) {
                        if (dailySurveyModelArrayList.get(0).getStatus() == 1) {

                            // ArrayList Will be Bind Here And set into the DailySurveyAdapter
                            DailySurveyAdapter oneTimeSurveyAdapter = new DailySurveyAdapter(this, dailySurveyModelArrayList, this);
                            recyclerViewDailySurvey.setAdapter(oneTimeSurveyAdapter);
                            showError(false, 1);
                        } else {
                            showError(true,dailySurveyModelArrayList.get(0).getStatus());
                            //Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        showError(true,2);
                        //Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
//                e.printStackTrace();
                Log.e("errordailySurveyList", e.getMessage());
            }
        }
    }

    // this function works when data is not coming it will set Error Image and text
    private void showError(boolean isError, int status) {
        /*mCardViewOneTime.setVisibility(View.GONE);
        recyclerViewOneTime.setVisibility(View.GONE);
        mCardViewDailySurvey.setVisibility(View.GONE);
        recyclerViewDailySurvey.setVisibility(View.GONE);*/

        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            errorText.setText(GetErrorResources.getMessage(status, this));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
            if(isClickedOnOneTimeOrDailySurvery==1) { //Daily Survey Selected
                mCardViewDailySurvey.setVisibility(View.GONE);
                recyclerViewDailySurvey.setVisibility(View.GONE);
            }
            else {
                mCardViewOneTime.setVisibility(View.GONE);
                recyclerViewOneTime.setVisibility(View.GONE);
            }
        } else {
            errorLayout.setVisibility(View.GONE);
            if(isClickedOnOneTimeOrDailySurvery==1) { //Daily Survey Selected
                mCardViewDailySurvey.setVisibility(View.VISIBLE);
                recyclerViewDailySurvey.setVisibility(View.VISIBLE);
            }
            else {
                mCardViewOneTime.setVisibility(View.VISIBLE);
                recyclerViewOneTime.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // this Click Listener is used when user will click on oneTimeSurvey List item
    // it will Redirect into OneTimeSurvey Detail Screen
    @Override
    public void onOneTimeDailySurveyNoteDetailsLayoutClicked(SenjamListModel senjamListModel) {
        Intent intent = new Intent(OnTimeSurveyListActivity.this, OnTimeSurveyDetailsActivity.class);
        intent.putExtra("senjamListModelDetailArrayList", senjamListModelDetailArrayList);
        startActivity(intent);
    }

    // this Click Listener is used when user will click on Daily Survey List item
    // it will Redirect into Daily Survey Detail Screen
    @Override
    public void onDailySurveyNoteDetailsLayoutClicked(SenjamListModel senjamListModel) {
        Intent intent = new Intent(OnTimeSurveyListActivity.this, DailySurveyDetailsActivity.class);
        intent.putExtra(General.SOWS_DETAILS, senjamListModel);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.swipe_refresh_layout_recycler_view_float:
                Intent addProgressNoteIntent = new Intent(this, AddSowsNoteActivity.class);
                startActivity(addProgressNoteIntent);
                break;

        }
    }
}
