package com.modules.crisis;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.modules.team.TeamDetailsActivity;
import com.sagesurfer.animation.ActivityTransition;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.interfaces.MainActivityInterface;
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

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 07-09-2017
 * Last Modified on 13-12-2017
 **/

public class CrisisFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = CrisisFragment.class.getSimpleName();
    private ArrayList<Teams_> teamsArrayList;
    private CrisisCount crisisCount;
    private int group_id = 0;

    private TextView latestCrisis, resolvedCount, activeCount, episodeCount, frequentIntervention, dateText, frequentInterventionTag;
    private LinearLayout errorLayout;
    private ScrollView contentView;
    private TextView errorText;
    private AppCompatImageView errorIcon;

    private Activity activity;
    private MainActivityInterface mainActivityInterface;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.crisis_fragment_layout, null);

        activity = getActivity();

        RelativeLayout crisisRiskLayout = (RelativeLayout) view.findViewById(R.id.crisis_risk_layout);
        crisisRiskLayout.setOnClickListener(this);
        RelativeLayout supportPersonLayout = (RelativeLayout) view.findViewById(R.id.crisis_support_person_layout);
        supportPersonLayout.setOnClickListener(this);
        RelativeLayout triggerLayout = (RelativeLayout) view.findViewById(R.id.crisis_trigger_layout);
        triggerLayout.setOnClickListener(this);
        RelativeLayout interventionLayout = (RelativeLayout) view.findViewById(R.id.crisis_intervention_layout);
        interventionLayout.setOnClickListener(this);

        int  color = GetColor.getHomeIconBackgroundColorColorParse(true);
        errorText = (TextView) view.findViewById(R.id.crisis_error_message);
        frequentInterventionTag = (TextView) view.findViewById(R.id.crisis_fragment_frequent_intervention_tag);
        frequentInterventionTag.setTextColor(color);
        latestCrisis = (TextView) view.findViewById(R.id.crisis_fragment_latest_crisis);
        resolvedCount = (TextView) view.findViewById(R.id.crisis_fragment_crisis_resolved);
        activeCount = (TextView) view.findViewById(R.id.crisis_fragment_active_crisis);
        episodeCount = (TextView) view.findViewById(R.id.crisis_fragment_crisis_episode);
        frequentIntervention = (TextView) view.findViewById(R.id.crisis_fragment_frequent_intervention);
        dateText = (TextView) view.findViewById(R.id.crisis_fragment_last_date);

        errorIcon = (AppCompatImageView) view.findViewById(R.id.crisis_error_icon);

        errorLayout = (LinearLayout) view.findViewById(R.id.crisis_error_layout);
        contentView = (ScrollView) view.findViewById(R.id.crisis_fragment_content_layout);

        return view;
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
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    ArrayList<CrisisCount> crisisCountArrayList = Crisis_.parseCounts(response, TAG, activity.getApplicationContext());
                    if (crisisCountArrayList != null && crisisCountArrayList.size() > 0) {
                        showError(false, 1);
                        crisisCount = crisisCountArrayList.get(0);
                        if (crisisCount.getStatus() == 1) {
                            setData();
                        } else {
                            activeCount.setText("0");
                            episodeCount.setText("0");
                            resolvedCount.setText("0");
                            latestCrisis.setText(activity.getApplicationContext().getResources().getString(R.string.na));
                            frequentIntervention.setText(activity.getApplicationContext().getResources().getString(R.string.na));
                            dateText.setText(activity.getApplicationContext().getResources().getString(R.string.na));
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
            errorText.setText(activity.getApplicationContext().getResources().getString(R.string.select_team));
            errorIcon.setImageResource(R.drawable.vi_data_not_fount_error);

            errorLayout.setVisibility(View.VISIBLE);
            contentView.setVisibility(View.GONE);

        } else {
            errorLayout.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);

            errorLayout.setVisibility(View.VISIBLE);
            contentView.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, activity.getApplicationContext()));
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
            /*case R.id.crisis_team_selector:
                if (teamsArrayList == null || teamsArrayList.size() <= 0) {
                    ShowSnack.viewWarning(v, activity.getApplicationContext().getResources()
                            .getString(R.string.teams_unavailable), activity.getApplicationContext());
                } else {
                    openTeamSelector();
                }
                break;*/
            case R.id.crisis_risk_layout:
                intent = new Intent(activity.getApplicationContext(), CrisisRiskListActivity.class);
                intent.putExtra(General.ACTION, Actions_.CRISIS_RISK);
                startActivity(intent);
                activity.overridePendingTransition(0, 0);
                break;
            case R.id.crisis_support_person_layout:
                intent = new Intent(activity.getApplicationContext(), SupportContactListActivity.class);
                startActivity(intent);
                activity.overridePendingTransition(0, 0);
                break;
            case R.id.crisis_trigger_layout:
                intent = new Intent(activity.getApplicationContext(), CrisisRiskListActivity.class);
                intent.putExtra(General.ACTION, Actions_.TRIGGERS);
                startActivity(intent);
                activity.overridePendingTransition(0, 0);
                break;
            case R.id.crisis_intervention_layout:
                intent = new Intent(activity.getApplicationContext(), InterventionActivity.class);
                startActivity(intent);
                activity.overridePendingTransition(0, 0);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //showError(true, 22);
        //mainActivityInterface.hideRevealView();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.crisis_plan));
        mainActivityInterface.setToolbarBackgroundColor();
        group_id = Integer.parseInt(Preferences.get(General.GROUP_ID));
        showError(false, 1);
        getCount();
        /*if (teamsArrayList == null || teamsArrayList.size() <= 0) {
            teamsArrayList = PerformGetTeamsTask.get(Actions_.ALL_TEAMS, activity.getApplicationContext(), TAG);
        }
        if (group_id != 0) {
            getCount();
        }*/

        try{
            ((TeamDetailsActivity) getActivity()).inviteButtonSetVisibility();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
