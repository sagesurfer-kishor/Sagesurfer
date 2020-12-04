package com.modules.landing_question_form.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.JsonObject;
import com.modules.landing_question_form.adapter.LandingQuestionListAdapter;
import com.modules.landing_question_form.module.LandingQuestion_;
import com.modules.landing_question_form.parser.LandingQuestionsParser_;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.tasks.PerformLogoutTask;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import okhttp3.RequestBody;

/**
 * Created by Kailash Karankal on 7/16/2019.
 */

public class LandingQuestionFormActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LandingQuestionFormActivity.class.getSimpleName();
    private Toolbar toolbar;
    private AppCompatImageView postButton;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private SwipeMenuListView listView;

    ArrayList<LandingQuestion_> landingQuestionsArrayList = new ArrayList<>();
    int optionValue = 0;
    private boolean isLandingQuestionFilled = false;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_landing_questions);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);

        toolbar.setNavigationIcon(R.drawable.vi_cancel_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        titleText.setText(this.getResources().getString(R.string.questions));

        postButton = (AppCompatImageView) findViewById(R.id.imageview_toolbar_save);
        postButton.setVisibility(View.VISIBLE);
        postButton.setOnClickListener(this);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        listView = (SwipeMenuListView) findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(0);

        errorText = (TextView) findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) findViewById(R.id.imageview_error_icon);
        errorLayout = (LinearLayout) findViewById(R.id.linealayout_error);
    }

    @Override
    public void onResume() {
        super.onResume();
        isLandingQuestionFilled = Preferences.getBoolean(General.IS_LANDING_QUESTION_FILLED);
        fetchLandingQuestions();
    }

    @Override
    public void onBackPressed() {
        isLandingQuestionFilled = Preferences.getBoolean(General.IS_LANDING_QUESTION_FILLED);
        if (!isLandingQuestionFilled) {
            PerformLogoutTask.logout(this);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    // make network call to post intake form
    private void fetchLandingQuestions() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_LANDING_QUESTIONS);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_LANDING_QUESTION;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("ResponseQuestionAnsw",response);
                JsonObject jsonObject = GetJson_.getJson(response);
                if (jsonObject.has(General.OPTION_KEY)) {
                    optionValue = jsonObject.get(General.OPTION_KEY).getAsInt();
                }
                landingQuestionsArrayList = LandingQuestionsParser_.parseLandingQuestion(response, Actions_.GET_LANDING_QUESTIONS, getApplicationContext(), TAG);
                if (landingQuestionsArrayList.size() > 0) {
                    if (landingQuestionsArrayList.get(0).getStatus() == 1) {
                        listView.setVisibility(View.VISIBLE);
                        errorLayout.setVisibility(View.GONE);
                        LandingQuestionListAdapter landingQuestionListAdapter = new LandingQuestionListAdapter(this, landingQuestionsArrayList, optionValue);
                        listView.setAdapter(landingQuestionListAdapter);
                    } else {
                        listView.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    listView.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // make network call to post intake form
    private void postLandingQuestions(View view) {
        ArrayList<String> landingQuestionSaveItemArray = new ArrayList<>();
        if (optionValue == 5) {
            for (int i = 0; i < landingQuestionsArrayList.size(); i++) {
                String landingQuestionSaveItem = "";
                String queId = String.valueOf(landingQuestionsArrayList.get(i).getQuestion().get(0).getId());
                for (int j = 0; j < landingQuestionsArrayList.get(i).getAnswer().size(); j++) {
                    if (landingQuestionsArrayList.get(i).getAnswer().get(j).isSelected()) {
                        String ansValue = String.valueOf(landingQuestionsArrayList.get(i).getAnswer().get(j).getValue());
                        landingQuestionSaveItem = queId + "_" + ansValue;
                    }
                }
                landingQuestionSaveItemArray.add(landingQuestionSaveItem);
            }
        } else { //optionValue = 2
            for (int i = 0; i < landingQuestionsArrayList.size(); i++) {
                String landingQuestionSaveItem = "";
                String queId = String.valueOf(landingQuestionsArrayList.get(i).getQuestion().get(0).getId());
                if (landingQuestionsArrayList.get(i).isSelected()) { //yes selected
                    for (int j = 0; j < landingQuestionsArrayList.get(i).getAnswer().size(); j++) {
                        if (landingQuestionsArrayList.get(i).getAnswer().get(j).isSelected()) {
                            String ansValue = String.valueOf(landingQuestionsArrayList.get(i).getAnswer().get(j).getValue());
                            landingQuestionSaveItem = queId + "_" + ansValue;
                        }
                    }
                } else {
                    String ansValue = "3"; //no selected
                    landingQuestionSaveItem = queId + "_" + ansValue;
                }
                landingQuestionSaveItemArray.add(landingQuestionSaveItem);
            }
        }

        String arrayToStringQuesAns = TextUtils.join(",", landingQuestionSaveItemArray);
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.SAVE_LANDING_QUESTIONS);
        requestMap.put(General.QUESANS, arrayToStringQuesAns);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_LANDING_QUESTION;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                landingQuestionsArrayList = LandingQuestionsParser_.parseLandingQuestion(response, Actions_.SAVE_LANDING_QUESTIONS, getApplicationContext(), TAG);
                if (landingQuestionsArrayList.size() > 0) {
                    if (landingQuestionsArrayList.get(0).getStatus() == 1) {
                        showError(false, 1, view);
                    } else {
                        showError(true, landingQuestionsArrayList.get(0).getStatus(), view);
                    }
                } else {
                    showError(true, 12, view);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showError(boolean isError, int status, View view) {
        String message;
        if (status == 1) {
            message = this.getResources().getString(R.string.landing_form_submitted_success);
            SubmitSnackResponse.showSnack(status, message, view, getApplicationContext());
            Preferences.save(General.ISMOVETOHOME, true);
            Preferences.save(General.IS_LANDING_QUESTION_FILLED, true);
            onBackPressed();
        } else {
            message = this.getResources().getString(R.string.action_failed);
            SubmitSnackResponse.showSnack(status, message, view, getApplicationContext());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_toolbar_save:
                postLandingQuestions(v);
                break;
        }
    }

    public boolean validate(View view) {
        return true;
    }
}
