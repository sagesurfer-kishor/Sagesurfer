package com.modules.caseload;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.modules.calendar.CalenderActivity;
import com.modules.crisis.CrisisActivity;
import com.modules.mood.CCMoodActivity;
import com.modules.task.TeamTaskListActivity;
import com.modules.team.TeamDetailsActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.Caseload_;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.tasks.PerformGetTeamsTask;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Monika on 8/28/2018.
 */

public class CaseloadActionsDialogFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = CaseloadActionsDialogFragment.class.getSimpleName();

    @BindView(R.id.imageview_profile)
    ImageView imageViewProfile;
    @BindView(R.id.textview_username)
    TextView textViewUserName;
    @BindView(R.id.linearlayout_caseload_summary)
    LinearLayout summaryLinearLayout;
    @BindView(R.id.imageview_caseload_summary)
    AppCompatImageView summaryImageView;
    @BindView(R.id.linearlayout_caseload_events)
    LinearLayout eventsLinearLayout;
    @BindView(R.id.imageview_caseload_events)
    AppCompatImageView eventsImageView;
    @BindView(R.id.linearlayout_caseload_plan)
    LinearLayout planLinearLayout;
    @BindView(R.id.imageview_caseload_plan)
    AppCompatImageView planImageView;
    @BindView(R.id.linearlayout_caseload_team)
    LinearLayout teamLinearLayout;
    @BindView(R.id.imageview_caseload_team)
    AppCompatImageView teamImageView;
    @BindView(R.id.linearlayout_caseload_status)
    LinearLayout statusLinearLayout;
    @BindView(R.id.imageview_caseload_status)
    AppCompatImageView statusImageView;
    @BindView(R.id.linearlayout_caseload_progress_note)
    LinearLayout progressNoteLinearLayout;
    @BindView(R.id.imageview_caseload_progress_note)
    AppCompatImageView progressNoteImageView;
    @BindView(R.id.textview_progress_note)
    TextView progressNoteTextView;
    @BindView(R.id.linearlayout_caseload_tasklist)
    LinearLayout taskListLinearLayout;
    @BindView(R.id.imageview_caseload_tasklist)
    AppCompatImageView taskListImageView;
    @BindView(R.id.linearlayout_caseload_contact)
    LinearLayout contactLinearLayout;
    @BindView(R.id.imageview_caseload_contact)
    AppCompatImageView contactImageView;
    @BindView(R.id.linearlayout_caseload_mood)
    LinearLayout moodLinearLayout;
    @BindView(R.id.imageview_caseload_mood)
    AppCompatImageView moodImageView;

    private Caseload_ caseload_ = new Caseload_();
    private Teams_ team_ = new Teams_();
    private Intent detailsIntent;
    private Activity activity;
    private Unbinder unbinder;
    int color;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_caseload_actions_dialog, null);
        unbinder = ButterKnife.bind(this, view);
        activity = getActivity();
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Bundle data = getArguments();
        if (data.containsKey(Actions_.TEAM_DATA)) {
            caseload_ = (Caseload_) data.getSerializable(Actions_.TEAM_DATA);
            textViewUserName.setText(caseload_.getUsername());

            Glide.with(activity)
                    .load(caseload_.getImage())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(caseload_.getImage()))
                            .transform(new CircleTransform(activity)))
                    .into(imageViewProfile);

            team_.setId(caseload_.getGroup_id());
            team_.setName(caseload_.getGroup_name());
            team_.setBanner(caseload_.getBanner_img());
        } else {
            dismiss();
        }

        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage021)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage022))) {
            progressNoteTextView.setText(getResources().getString(R.string.note));
        } else {
            progressNoteTextView.setText(getResources().getString(R.string.progress_note));
        }

        color = GetColor.getHomeIconBackgroundColorColorParse(true);
        summaryImageView.setColorFilter(color);
        summaryImageView.setImageResource(R.drawable.vi_caseload_case_summary);
        summaryImageView.setOnClickListener(this);

        eventsImageView.setColorFilter(color);
        eventsImageView.setImageResource(R.drawable.vi_caseload_events);
        eventsImageView.setOnClickListener(this);

        planImageView.setColorFilter(color);
        planImageView.setImageResource(R.drawable.vi_drawer_crisisplan);
        planImageView.setOnClickListener(this);

        teamImageView.setColorFilter(color);
        teamImageView.setImageResource(R.drawable.vi_drawer_teams);
        teamImageView.setOnClickListener(this);

        statusImageView.setColorFilter(color);
        statusImageView.setImageResource(R.drawable.vi_warning);
        statusImageView.setOnClickListener(this);

        progressNoteImageView.setColorFilter(color);
        progressNoteImageView.setImageResource(R.drawable.vi_up_solid_arrow_white);
        progressNoteImageView.setOnClickListener(this);

        taskListImageView.setColorFilter(color);
        taskListImageView.setImageResource(R.drawable.vi_home_task_list);
        taskListImageView.setOnClickListener(this);

        contactImageView.setColorFilter(color);
        contactImageView.setImageResource(R.drawable.vi_caseload_contact);
        contactImageView.setOnClickListener(this);

        moodImageView.setColorFilter(color);
        moodImageView.setImageResource(R.drawable.vi_drawer_mood);
        moodImageView.setOnClickListener(this);

        if (!Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage015") && CheckRole.isCoordinator(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            planLinearLayout.setVisibility(View.VISIBLE);
        }

        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage021") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage022")) {
            planLinearLayout.setVisibility(View.GONE);
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage023")) {
            planLinearLayout.setVisibility(View.GONE);
            statusLinearLayout.setVisibility(View.GONE);
            progressNoteImageView.setClickable(false);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        assert dialog.getWindow() != null;
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d.getWindow() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            d.getWindow().setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        setStyle(Window.FEATURE_NO_TITLE, R.style.MY_DIALOG);
        d.setCancelable(true);
        d.setCanceledOnTouchOutside(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
    }

    @Override
    public void onClick(View v) {
        ArrayList<Teams_> teamArrayList;
        switch (v.getId()) {
            case R.id.imageview_caseload_summary:
                dismiss();
                Preferences.save(General.CONSUMER_ID, caseload_.getUser_id());
                Preferences.save(General.GROUP_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_NAME, caseload_.getGroup_name());
                detailsIntent = new Intent(activity.getApplicationContext(), CaseloadSummaryActivity.class);
                startActivity(detailsIntent);
                activity.overridePendingTransition(0, 0);
                break;

            case R.id.imageview_caseload_events:
                dismiss();
                Preferences.save(General.CONSUMER_ID, caseload_.getUser_id());
                Preferences.save(General.GROUP_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_NAME, caseload_.getGroup_name());
                teamArrayList = PerformGetTeamsTask.getNormalTeams(Actions_.TEAM_DATA, activity.getApplicationContext(), TAG, true, activity);
                if (teamArrayList.size() > 0) {
                    //PerformGetTeamsTask.get(Actions_.TEAM_DATA, activity.getApplicationContext(), TAG, true, activity);
                    detailsIntent = new Intent(activity.getApplicationContext(), CalenderActivity.class);
                    detailsIntent.putExtra(General.USER_ID, caseload_.getUser_id());
                    detailsIntent.putExtra(General.TEAM, team_);
                    startActivity(detailsIntent);
                    activity.overridePendingTransition(0, 0);
                }
                break;

            case R.id.imageview_caseload_plan:
                dismiss();
                Preferences.save(General.CONSUMER_ID, caseload_.getUser_id());
                Preferences.save(General.GROUP_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_NAME, caseload_.getGroup_name());
                detailsIntent = new Intent(activity.getApplicationContext(), CrisisActivity.class);
                detailsIntent.putExtra(General.TEAM, team_);
                activity.startActivity(detailsIntent);
                activity.overridePendingTransition(0, 0);
                break;

            case R.id.imageview_caseload_team:
                dismiss();
                Preferences.save(General.BANNER_IMG, caseload_.getBanner_img());
                Preferences.save(General.GROUP_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_NAME, caseload_.getGroup_name());

                detailsIntent = new Intent(activity.getApplicationContext(), TeamDetailsActivity.class);
                detailsIntent.putExtra(General.TEAM, team_);
                startActivity(detailsIntent);
                activity.overridePendingTransition(0, 0);
                break;

            case R.id.imageview_caseload_status:
                dismiss();
                Preferences.save(General.CONSUMER_ID, caseload_.getUser_id());
                Preferences.save(General.GROUP_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_NAME, caseload_.getGroup_name());
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015)) ||
                        Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage021))
                        || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage022))) {
                    detailsIntent = new Intent(activity.getApplicationContext(), CaseloadStatusPeerActivity.class);
                    detailsIntent.putExtra(General.STATUS, caseload_.getType());
                    activity.startActivity(detailsIntent);
                    activity.overridePendingTransition(0, 0);
                } else {
                    Intent detailsIntent = new Intent(activity.getApplicationContext(), CaseloadStatusGraphActivity.class);
                    startActivity(detailsIntent);
                    activity.overridePendingTransition(0, 0);
                }
                break;

            case R.id.imageview_caseload_progress_note:
                //if(!Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015))) {
                dismiss();
                Preferences.save(General.CONSUMER_ID, caseload_.getUser_id());
                Preferences.save(General.CONSUMER_NAME, caseload_.getUsername());
                Preferences.save(General.GROUP_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_ID, "" + caseload_.getGroup_id());
                detailsIntent = new Intent(activity.getApplicationContext(), CaseloadProgressNoteActivity.class);
                activity.startActivity(detailsIntent);
                activity.overridePendingTransition(0, 0);
                //}
                break;

            case R.id.imageview_caseload_tasklist:
                dismiss();
                Preferences.save(General.GROUP_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_ID, "" + caseload_.getGroup_id());
                teamArrayList = PerformGetTeamsTask.getNormalTeams(Actions_.TEAM_DATA, activity.getApplicationContext(), TAG, true, activity);
                if (teamArrayList.size() > 0) {
                    detailsIntent = new Intent(activity.getApplicationContext(), TeamTaskListActivity.class);
                    activity.startActivity(detailsIntent);
                    activity.overridePendingTransition(0, 0);
                }
                break;

            case R.id.imageview_caseload_contact:
                dismiss();
                Preferences.save(General.GROUP_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_NAME, caseload_.getGroup_name());

                detailsIntent = new Intent(activity.getApplicationContext(), CaseloadContactActivity.class);
                detailsIntent.putExtra(General.NAME, caseload_.getName());
                detailsIntent.putExtra(General.USERNAME, caseload_.getUsername());
                detailsIntent.putExtra(General.ADDRESS, caseload_.getAddress());
                detailsIntent.putExtra(General.URL_IMAGE, caseload_.getImage());
                detailsIntent.putExtra(General.PHONE, caseload_.getPhone());
                startActivity(detailsIntent);
                activity.overridePendingTransition(0, 0);
                break;
            case R.id.imageview_caseload_mood:
                dismiss();
                Preferences.save(General.CONSUMER_ID, caseload_.getUser_id());
                Preferences.save(General.CONSUMER_NAME, caseload_.getUsername());
                Preferences.save(General.GROUP_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_NAME, caseload_.getGroup_name());
                detailsIntent = new Intent(activity.getApplicationContext(), CCMoodActivity.class);
                startActivity(detailsIntent);
                activity.overridePendingTransition(0, 0);
                break;
        }
    }
}