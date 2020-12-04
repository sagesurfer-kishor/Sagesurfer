package com.modules.caseload.werhope.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.modules.assessment_screener.AssessmentListActivity;
import com.modules.calendar.CalenderActivity;
import com.modules.caseload.CaseloadContactActivity;
import com.modules.caseload.mhaw.activity.MhawProgressNoteActivity;
import com.modules.caseload.werhope.activity.ProgressNoteActivity;
import com.modules.mood.CCMoodActivity;
import com.modules.task.TeamTaskListActivity;
import com.modules.team.TeamDetailsActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.Caseload_;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.tasks.PerformGetTeamsTask;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class CaseloadListNewAdapter extends RecyclerView.Adapter<CaseloadListNewAdapter.MyViewHolder> {
    private static final String TAG = CaseloadListNewAdapter.class.getSimpleName();
    private final ArrayList<Caseload_> caseloadList;
    private ArrayList<Teams_> teamArrayList;
    private Teams_ team_ = new Teams_();
    private int color;
    private Intent detailsIntent;
    private Activity activity;

    public CaseloadListNewAdapter(Activity activity, ArrayList<Caseload_> caseloadList) {
        this.caseloadList = caseloadList;
        this.activity = activity;
    }

    @Override
    public long getItemId(int position) {
        return caseloadList.get(position).getUser_id();
    }

    @Override
    public int getItemCount() {
        return caseloadList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage023))) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_caseload_cardview_item, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_tarzana_caseload_cardview_item, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {

        final Caseload_ caseload_ = caseloadList.get(position);

        viewHolder.nameText.setText(ChangeCase.toTitleCase(caseloadList.get(position).getUsername()));
        viewHolder.contactedText.setText(caseloadList.get(position).getContacted_last());

        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage023))) {
            viewHolder.staff.setVisibility(View.GONE);
            viewHolder.staffDisplayName.setVisibility(View.GONE);
            if (caseloadList.get(position).getGrade() == 1) {
                viewHolder.grade.setText(caseloadList.get(position).getGrade() + "st");
            } else if (caseloadList.get(position).getGrade() == 2) {
                viewHolder.grade.setText(caseloadList.get(position).getGrade() + "nd");
            } else if (caseloadList.get(position).getGrade() == 3) {
                viewHolder.grade.setText(caseloadList.get(position).getGrade() + "rd");
            } else {
                viewHolder.grade.setText(caseloadList.get(position).getGrade() + "th");
            }

            if (caseloadList.get(position).getLast_progress_date().equalsIgnoreCase("")) {
                viewHolder.lastNoteDate.setText("N/A");
            } else {
                viewHolder.lastNoteDate.setText(GetTime.dateValue(Long.parseLong(caseloadList.get(position).getLast_progress_date())));
            }

        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage026))) {
            viewHolder.grade.setVisibility(View.GONE);
            viewHolder.gradeDisplayName.setVisibility(View.GONE);
            if (caseloadList.get(position).getStaff_last_contacted().equalsIgnoreCase("")) {
                viewHolder.staff.setText("N/A");
            } else {
                viewHolder.staff.setText(caseloadList.get(position).getStaff_last_contacted());
            }

            if (caseloadList.get(position).getLast_progress_date().equalsIgnoreCase("")) {
                viewHolder.lastNoteDate.setText("N/A");
            } else {
                viewHolder.lastNoteDate.setText(GetTime.dateValue(Long.parseLong(caseloadList.get(position).getLast_progress_date())));
            }

        } else {
            if (caseloadList.get(position).getLast_team_interaction() == null || caseloadList.get(position).getLast_team_interaction().trim().length() <= 0) {
                viewHolder.lastNoteDate.setText("N/A");
            } else {
                viewHolder.lastNoteDate.setText(caseloadList.get(position).getLast_team_interaction());
//                viewHolder.lastNoteDate.setText(caseloadList.get(position).getContacted_last());
            }
        }

        if (caseloadList.get(position).getLast_mood().equalsIgnoreCase("")) {
            viewHolder.lastMood.setText("N/A");
        } else {
            viewHolder.lastMood.setText(ChangeCase.toTitleCase(caseloadList.get(position).getLast_mood()));
        }

        viewHolder.teamName.setText(caseloadList.get(position).getGroup_name());

        Glide.with(activity)
                .load(caseloadList.get(position).getImage())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(caseloadList.get(position).getImage()))
                        .transform(new CircleTransform(activity)))
                .into(viewHolder.profileImage);

      /*  if (caseloadList.get(position).getMood_image().equals("") || caseloadList.get(position).getMood_image() == null) {
            viewHolder.moodImg.setVisibility(View.GONE);
        } else {*/
        Glide.with(activity)
                .load(caseloadList.get(position).getMood_image())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(caseloadList.get(position).getMood_image())))
                .into(viewHolder.moodImg);
        //  }


        viewHolder.detailsRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.upArrow.setVisibility(View.VISIBLE);
                viewHolder.downArrow.setVisibility(View.GONE);
                viewHolder.caseloadActionsLayout.setVisibility(View.VISIBLE);
                viewHolder.detailsLayout.setVisibility(View.VISIBLE);
            }
        });

        viewHolder.downArrow.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.upArrow.setVisibility(View.VISIBLE);
                viewHolder.downArrow.setVisibility(View.GONE);
                viewHolder.caseloadActionsLayout.setVisibility(View.VISIBLE);
                viewHolder.detailsLayout.setVisibility(View.VISIBLE);
            }
        });

        viewHolder.upArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.upArrow.setVisibility(View.GONE);
                viewHolder.downArrow.setVisibility(View.VISIBLE);
                viewHolder.caseloadActionsLayout.setVisibility(View.GONE);
                viewHolder.detailsLayout.setVisibility(View.GONE);
            }
        });

        color = R.color.black;
        viewHolder.progressNoteImageView.setColorFilter(color);
        viewHolder.eventsImageView.setColorFilter(color);
        viewHolder.taskListImageView.setColorFilter(color);
        viewHolder.teamImageView.setColorFilter(color);
        viewHolder.moodImageView.setColorFilter(color);
        viewHolder.contactImageView.setColorFilter(color);

        viewHolder.progressNoteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage023))) {
                    Preferences.save(General.CONSUMER_ID, caseload_.getUser_id());
                    Preferences.save(General.CONSUMER_NAME, caseload_.getUsername());
                    Preferences.save(General.GROUP_ID, "" + caseload_.getGroup_id());
                    Preferences.save(General.TEAM_ID, "" + caseload_.getGroup_id());
                    Preferences.save(General.NOTE_USER_ID, "" + caseload_.getUser_id());
                    detailsIntent = new Intent(activity, ProgressNoteActivity.class);
                    activity.startActivity(detailsIntent);
                } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage026))) {
                    Preferences.save(General.CONSUMER_ID, caseload_.getUser_id());
                    Preferences.save(General.CONSUMER_NAME, caseload_.getUsername());
                    Preferences.save(General.GROUP_ID, "" + caseload_.getGroup_id());
                    Preferences.save(General.TEAM_ID, "" + caseload_.getGroup_id());
                    Preferences.save(General.NOTE_USER_ID, "" + caseload_.getUser_id());
                    detailsIntent = new Intent(activity, MhawProgressNoteActivity.class);
                    activity.startActivity(detailsIntent);
                } else {
                    detailsIntent = new Intent(activity, AssessmentListActivity.class);
                    Preferences.save(General.NOTE_USER_ID, "" + caseload_.getUser_id());
                    activity.startActivity(detailsIntent);
                }
            }
        });

        viewHolder.eventsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.save(General.CONSUMER_ID, caseload_.getUser_id());
                Preferences.save(General.GROUP_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_NAME, caseload_.getGroup_name());
                teamArrayList = PerformGetTeamsTask.get(Actions_.TEAM_DATA, activity.getApplicationContext(), TAG, true, activity);
                if (teamArrayList.size() > 0) {
                    detailsIntent = new Intent(activity.getApplicationContext(), CalenderActivity.class);
                    detailsIntent.putExtra(General.USER_ID, caseload_.getUser_id());
                    detailsIntent.putExtra(General.TEAM, team_);
                    activity.startActivity(detailsIntent);
                }
            }
        });

        viewHolder.taskListImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.save(General.GROUP_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_ID, "" + caseload_.getGroup_id());
                teamArrayList = PerformGetTeamsTask.get(Actions_.TEAM_DATA, activity.getApplicationContext(), TAG, true, activity);
                if (teamArrayList.size() > 0) {
                    detailsIntent = new Intent(activity.getApplicationContext(), TeamTaskListActivity.class);
                    activity.startActivity(detailsIntent);
                }
            }
        });

        viewHolder.moodImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.save(General.CONSUMER_ID, caseload_.getUser_id());
                Preferences.save(General.CONSUMER_NAME, caseload_.getUsername());
                Preferences.save(General.GROUP_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_NAME, caseload_.getGroup_name());
                detailsIntent = new Intent(activity.getApplicationContext(), CCMoodActivity.class);
                activity.startActivity(detailsIntent);
            }
        });

        viewHolder.teamImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.save(General.BANNER_IMG, caseload_.getBanner_img());
                Preferences.save(General.GROUP_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_NAME, caseload_.getGroup_name());

                detailsIntent = new Intent(activity.getApplicationContext(), TeamDetailsActivity.class);
                team_.setId(caseload_.getGroup_id());
                team_.setName(caseload_.getGroup_name());
                team_.setBanner(caseload_.getBanner_img());
                team_.setType(1);
                detailsIntent.putExtra(General.TEAM, team_);
                detailsIntent.putExtra("showIcon", true);
                activity.startActivity(detailsIntent);
            }
        });

        viewHolder.contactImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.save(General.GROUP_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_NAME, caseload_.getGroup_name());

                detailsIntent = new Intent(activity.getApplicationContext(), CaseloadContactActivity.class);
                detailsIntent.putExtra(General.NAME, caseload_.getUsername());
                detailsIntent.putExtra(General.USERNAME, caseload_.getUsername());
                detailsIntent.putExtra(General.ADDRESS, caseload_.getAddress());
                detailsIntent.putExtra(General.URL_IMAGE, caseload_.getImage());
                detailsIntent.putExtra(General.PHONE, caseload_.getPhone());
                activity.startActivity(detailsIntent);
            }
        });
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView nameText, parentName, contactedText;
        final ImageView profileImage, moodImg, downArrow, upArrow;
        final LinearLayout progressNoteLinearLayout, taskLinearLayout, eventsLinearLayout, detailsLayout;
        final RelativeLayout detailsRelativeLayout;
        final LinearLayout teamLinearLayout, contactLinearLayout, moodLinearLayout, caseloadActionsLayout;
        final TextView grade, gradeDisplayName, staff, staffDisplayName, lastNoteDate, lastMood, teamName;
        final AppCompatImageView progressNoteImageView, eventsImageView, taskListImageView, teamImageView;
        final AppCompatImageView moodImageView, contactImageView;

        MyViewHolder(View view) {
            super(view);
            nameText = (TextView) view.findViewById(R.id.textview_caseload_name);
            parentName = (TextView) view.findViewById(R.id.textview_caseload_parent_name);
            contactedText = (TextView) view.findViewById(R.id.textview_caseload_contacted_at);
            profileImage = (ImageView) view.findViewById(R.id.imageview_profile);

            progressNoteLinearLayout = (LinearLayout) view.findViewById(R.id.linearlayout_caseload_progress);
            eventsLinearLayout = (LinearLayout) view.findViewById(R.id.linearlayout_caseload_events);
            taskLinearLayout = (LinearLayout) view.findViewById(R.id.linearlayout_caseload_task_list);
            teamLinearLayout = (LinearLayout) view.findViewById(R.id.linearlayout_caseload_team);
            moodLinearLayout = (LinearLayout) view.findViewById(R.id.linearlayout_caseload_mood);
            contactLinearLayout = (LinearLayout) view.findViewById(R.id.linearlayout_caseload_contact);
            caseloadActionsLayout = (LinearLayout) view.findViewById(R.id.relativelayout_caseload_actions);
            detailsLayout = (LinearLayout) view.findViewById(R.id.details_layout);

            moodImg = (ImageView) view.findViewById(R.id.caseload_mood_img);
            downArrow = (ImageView) view.findViewById(R.id.down_arrow);
            upArrow = (ImageView) view.findViewById(R.id.up_arrow);

            detailsRelativeLayout = (RelativeLayout) view.findViewById(R.id.relativelayout_caseload_details);

            progressNoteImageView = (AppCompatImageView) view.findViewById(R.id.imageview_caseload_progress_note);
            eventsImageView = (AppCompatImageView) view.findViewById(R.id.imageview_caseload_events);
            taskListImageView = (AppCompatImageView) view.findViewById(R.id.imageview_caseload_task);
            teamImageView = (AppCompatImageView) view.findViewById(R.id.imageview_caseload_team);
            moodImageView = (AppCompatImageView) view.findViewById(R.id.imageview_caseload_mood);
            contactImageView = (AppCompatImageView) view.findViewById(R.id.imageview_caseload_contact);

            grade = (TextView) view.findViewById(R.id.grade_txt);
            gradeDisplayName = (TextView) view.findViewById(R.id.grad_display_name);
            staff = (TextView) view.findViewById(R.id.staff_txt);
            staffDisplayName = (TextView) view.findViewById(R.id.staff_display_name);
            lastNoteDate = (TextView) view.findViewById(R.id.last_note_date);
            lastMood = (TextView) view.findViewById(R.id.mood_type);
            teamName = (TextView) view.findViewById(R.id.team_name_txt);
        }
    }
}
