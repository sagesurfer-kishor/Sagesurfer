package com.modules.selfgoal;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sagesurfer.collaborativecares.BuildConfig;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.models.Goal_;

import java.util.ArrayList;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 15/03/2018
 * Last Modified on
 */

public class SelfGoalListAdapter extends RecyclerView.Adapter {
    private final ArrayList<Goal_> goalArrayList;
    private final static String TAG = SelfGoalListAdapter.class.getSimpleName();
    private final Activity activity;
    private final GoalListAdapterListener goalListAdapterListener;

    public SelfGoalListAdapter(Activity activity, ArrayList<Goal_> goalArrayList, GoalListAdapterListener goalListAdapterListener) {
        this.goalArrayList = goalArrayList;
        this.activity = activity;
        this.goalListAdapterListener = goalListAdapterListener;
    }

    @Override
    public int getItemCount() {
        return goalArrayList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, date, percentText, statusText, inputTxt,inputLabel;
        LinearLayout mainLayout;
        AppCompatImageView pinIcon;
        ProgressBar progressBar;

        public myViewHolder(View view) {
            super(view);
            titleText = (TextView) view.findViewById(R.id.selfgoallistitem_name);
            inputTxt = (TextView) view.findViewById(R.id.input_txt);
            inputLabel = (TextView) view.findViewById(R.id.input_label);
            date = (TextView) view.findViewById(R.id.selfgoallistitem_date);
            percentText = (TextView) view.findViewById(R.id.selfgoallistitem_percent);
            statusText = (TextView) view.findViewById(R.id.selfgoallistitem_status);

            progressBar = (ProgressBar) view.findViewById(R.id.selfgoallistitem_pb);

            pinIcon = (AppCompatImageView) view.findViewById(R.id.selfgoallistitem_location);

            mainLayout = (LinearLayout) view.findViewById(R.id.selfgoallistitem_main_layout);
        }
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.self_goal_list_item_layout, parent, false);

        return new myViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final myViewHolder viewHolder = (myViewHolder) holder;
        final Goal_ goal_ = goalArrayList.get(position);
        if (goal_.getStatus() != 1) {
            viewHolder.mainLayout.setVisibility(View.GONE);
        }
        viewHolder.mainLayout.setVisibility(View.VISIBLE);
        viewHolder.titleText.setText(goal_.getName());
        viewHolder.date.setText(GetTime.month_DdYyyy(goal_.getStart_date()) + " to " + GetTime.month_DdYyyy(goal_.getEnd_date()));
        String goalProgress = goal_.getProgress() + "%";
        viewHolder.percentText.setText(goalProgress);
        setStatus(goal_.getGoal_status(), viewHolder);
        viewHolder.progressBar.setProgress(goal_.getProgress());

        if(goal_.getGoal_status() == 1){
            if(goal_.getGoal_type() == 2){
                viewHolder.progressBar.setProgress(100);
                goalProgress = "100%";
                viewHolder.percentText.setText(goalProgress);
            }
        }

        setPin(goal_.getIs_dashboard(), viewHolder);

        if (goal_.getToday_status() == 1) {
            viewHolder.mainLayout.setBackgroundResource(R.drawable.red_rounded_border);
            viewHolder.inputTxt.setText("Input Needed");
            viewHolder.inputTxt.setTextColor(activity.getResources().getColor(R.color.self_goal_missed));
        } else if (goal_.getToday_status() == 2) {
            viewHolder.mainLayout.setBackgroundResource(R.drawable.green_rounded_border);
            viewHolder.inputTxt.setText("Input Provided");
            viewHolder.inputTxt.setTextColor(activity.getResources().getColor(R.color.self_goal_completed));
        } else {
            viewHolder.mainLayout.setBackgroundResource(R.drawable.gray_rounded_border);
            viewHolder.inputTxt.setVisibility(View.GONE);
            viewHolder.inputLabel.setVisibility(View.GONE);
        }

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goalListAdapterListener.onGoalItemClicked(position, goal_);
            }
        });
        applyReadStatus(viewHolder, goalArrayList.get(position));
    }

    private void applyReadStatus(myViewHolder holder, Goal_ goal_) {
        if (goal_.getIs_read() == 1) {
            holder.titleText.setTextColor(ContextCompat.getColor(activity, R.color.text_color_read));
        } else {
            holder.titleText.setTextColor(ContextCompat.getColor(activity, R.color.text_color_primary));
        }
    }

    public interface GoalListAdapterListener {
        void onGoalItemClicked(int position, Goal_ goal_);
    }

    private void setPin(int isDashboard, myViewHolder viewHolder) {
        if (isDashboard == 1) {
            viewHolder.pinIcon.setVisibility(View.VISIBLE);
        } else {
            viewHolder.pinIcon.setVisibility(View.GONE);
        }

        //Hide Blue Pin goal icon For Senjam. as senjam have all custom goal only
        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
            viewHolder.pinIcon.setVisibility(View.GONE);
        }
    }

    private void setStatus(int status, myViewHolder viewHolder) {
        Resources resources = activity.getApplicationContext().getResources();
        Rect bounds = viewHolder.progressBar.getProgressDrawable().getBounds();
        if (status == 0) {
            viewHolder.statusText.setBackgroundResource(R.drawable.goal_active_rounded_rectangle);
            viewHolder.progressBar.setProgressDrawable(resources.getDrawable(R.drawable.goal_progress_active));
            viewHolder.statusText.setText(resources.getString(R.string.active));
            viewHolder.percentText.setTextColor(resources.getColor(R.color.self_goal_active));
        } else if (status == 1) {
            viewHolder.statusText.setBackgroundResource(R.drawable.goal_completed_rounded_rectangle);
            viewHolder.progressBar.setProgressDrawable(resources.getDrawable(R.drawable.goal_progress_completed));
            viewHolder.statusText.setText(resources.getString(R.string.completed));
            viewHolder.percentText.setTextColor(resources.getColor(R.color.self_goal_completed));
        } else {
            viewHolder.statusText.setBackgroundResource(R.drawable.goal_missed_rounded_rectangle);
            viewHolder.progressBar.setProgressDrawable(resources.getDrawable(R.drawable.goal_progress_missed));
            viewHolder.statusText.setText(resources.getString(R.string.missed));
            viewHolder.percentText.setTextColor(resources.getColor(R.color.self_goal_missed));
        }
        viewHolder.progressBar.getProgressDrawable().setBounds(bounds);
    }
}