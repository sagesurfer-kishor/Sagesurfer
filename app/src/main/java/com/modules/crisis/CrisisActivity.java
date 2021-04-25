package com.modules.crisis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Crisis_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

public class CrisisActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CrisisFragment.class.getSimpleName();
    //private ArrayList<Teams_> teamsArrayList;
    private CrisisCount crisisCount;
    private int group_id = 0;
    private TextView latestCrisis, resolvedCount, activeCount, episodeCount, frequentIntervention, dateText, frequentInterventionTag;
    private LinearLayout errorLayout;
    private ScrollView contentView;
    private TextView errorText;
    private AppCompatImageView errorIcon;

    Toolbar toolbar;
    Teams_ team_;

    @SuppressLint({"InflateParams", "RestrictedApi"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_crisis);

        toolbar = (Toolbar) findViewById(R.id.crisis_layout_toolbar);
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

        RelativeLayout crisisRiskLayout = (RelativeLayout) findViewById(R.id.crisis_risk_layout);
        crisisRiskLayout.setOnClickListener(this);
        RelativeLayout supportPersonLayout = (RelativeLayout) findViewById(R.id.crisis_support_person_layout);
        supportPersonLayout.setOnClickListener(this);
        RelativeLayout triggerLayout = (RelativeLayout) findViewById(R.id.crisis_trigger_layout);
        triggerLayout.setOnClickListener(this);
        RelativeLayout interventionLayout = (RelativeLayout) findViewById(R.id.crisis_intervention_layout);
        interventionLayout.setOnClickListener(this);

        int color = GetColor.getHomeIconBackgroundColorColorParse(true);
        errorText = (TextView) findViewById(R.id.crisis_error_message);
        frequentInterventionTag = (TextView) findViewById(R.id.crisis_fragment_frequent_intervention_tag);
        frequentInterventionTag.setTextColor(color);
        latestCrisis = (TextView) findViewById(R.id.crisis_fragment_latest_crisis);
        resolvedCount = (TextView) findViewById(R.id.crisis_fragment_crisis_resolved);
        activeCount = (TextView) findViewById(R.id.crisis_fragment_active_crisis);
        episodeCount = (TextView) findViewById(R.id.crisis_fragment_crisis_episode);
        frequentIntervention = (TextView) findViewById(R.id.crisis_fragment_frequent_intervention);
        dateText = (TextView) findViewById(R.id.crisis_fragment_last_date);

        errorIcon = (AppCompatImageView) findViewById(R.id.crisis_error_icon);

        errorLayout = (LinearLayout) findViewById(R.id.crisis_error_layout);
        contentView = (ScrollView) findViewById(R.id.crisis_fragment_content_layout);

        Intent data = getIntent();
        if (data != null && data.hasExtra(General.TEAM)) {
            team_ = (Teams_) data.getSerializableExtra(General.TEAM);
        } else {
            onBackPressed();
        }
        if (team_ == null) {
            onBackPressed();
        }

    }

    // set data to respective fields
    private void setData() {
        String dateString = "Last Crisis " + GetTime.getTodayMm(crisisCount.getTimestamp());
        dateText.setText(dateString);
        resolvedCount.setText(crisisCount.getSuccessful_count());
        activeCount.setText(crisisCount.getActive_count());
        episodeCount.setText(crisisCount.getTotal_count());
        latestCrisis.setText(crisisCount.getLatest_crisis());
        frequentIntervention.setText(crisisCount.getMost_frequent_used());
    }

    /*// Open team selector dialog
    @SuppressLint("CommitTransaction")
    private void openTeamSelector() {
        Bundle bundle = new Bundle();
        DialogFragment dialogFrag = new SingleTeamSelectorDialog();
        bundle.putSerializable(General.TEAM_LIST, teamsArrayList);
        bundle.putInt("menu_id", 5);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getFragmentManager().beginTransaction(), General.TEAM_LIST);
    }*/

    // Make network call to  get crisis counter
    private void getCount() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.CRISIS_COUNT);
        requestMap.put("gid", "" + group_id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CRISIS_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    ArrayList<CrisisCount> crisisCountArrayList = Crisis_.parseCounts(response, TAG, getApplicationContext());
                    if (crisisCountArrayList != null && crisisCountArrayList.size() > 0) {
                        showError(false, 1);
                        crisisCount = crisisCountArrayList.get(0);
                        if (crisisCount.getStatus() == 1) {
                            setData();
                        } else {
                            activeCount.setText("0");
                            episodeCount.setText("0");
                            resolvedCount.setText("0");
                            latestCrisis.setText(getApplicationContext().getResources().getString(R.string.na));
                            frequentIntervention.setText(getApplicationContext().getResources().getString(R.string.na));
                            dateText.setText(getApplicationContext().getResources().getString(R.string.na));
                        }
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showError(true, 11);
    }

    // show error messages as per result/status code
    private void showError(boolean isError, int status) {
        if (!isError) {
            errorLayout.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);
            return;
        }
        if (status == 22) {
            errorText.setText(getApplicationContext().getResources().getString(R.string.select_team));
            errorIcon.setImageResource(R.drawable.vi_select_team_error);

            errorLayout.setVisibility(View.VISIBLE);
            contentView.setVisibility(View.GONE);

        } else {
            errorLayout.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);

            errorLayout.setVisibility(View.VISIBLE);
            contentView.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, getApplicationContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        }
    }

    /*// Get selected team choices
    public void GetChoice(Teams_ teams_, boolean isSelected) {
        if (isSelected) {
            group_id = teams_.getId();
            teamSelected.setText(teams_.getName());
            Preferences.save(General.GROUP_ID, "" + group_id);
            Preferences.save(General.GROUP_NAME, teams_.getName());
            Preferences.save(General.OWNER_ID, teams_.getOwnerId());
            showError(false, 1);
            getCount();
        }
    }*/

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.crisis_risk_layout:
                intent = new Intent(getApplicationContext(), CrisisRiskListActivity.class);
                intent.putExtra(General.ACTION, Actions_.CRISIS_RISK);
                //startActivity(intent, ActivityTransition.moveToNextAnimation(getApplicationContext()));
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.crisis_support_person_layout:
                intent = new Intent(getApplicationContext(), SupportContactListActivity.class);
                //startActivity(intent, ActivityTransition.moveToNextAnimation(getApplicationContext()));
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.crisis_trigger_layout:
                intent = new Intent(getApplicationContext(), CrisisRiskListActivity.class);
                intent.putExtra(General.ACTION, Actions_.TRIGGERS);
                //startActivity(intent, ActivityTransition.moveToNextAnimation(getApplicationContext()));
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.crisis_intervention_layout:
                intent = new Intent(getApplicationContext(), InterventionActivity.class);
                //startActivity(intent, ActivityTransition.moveToNextAnimation(getApplicationContext()));
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //showError(true, 22);
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        /*if (teamsArrayList == null || teamsArrayList.size() <= 0) {
            teamsArrayList = PerformGetTeamsTask.get(Actions_.ALL_TEAMS, getApplicationContext(), TAG);
        }*/
        /*if (group_id != 0) {
            getCount();
        }*/
        group_id = team_.getId();
        Preferences.save(General.GROUP_ID, "" + group_id);
        Preferences.save(General.GROUP_NAME, team_.getName());
        Preferences.save(General.OWNER_ID, team_.getOwnerId());
        showError(false, 1);
        getCount();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0, 0);
        finish();
    }
}

