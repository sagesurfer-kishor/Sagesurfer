package com.modules.selfgoal.werhope_self_goal.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
 import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.modules.selfgoal.werhope_self_goal.model.LogBook;
import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;

/**
 * Created by Kailash Karankal on 1/10/2020.
 */
public class HourDailyListAdapter extends RecyclerView.Adapter<HourDailyListAdapter.MyViewHolder> {
    private static final String TAG = HourDailyListAdapter.class.getSimpleName();
    private final ArrayList<LogBook> goalArrayList;
    private Activity activity;

    public HourDailyListAdapter(Activity activity, ArrayList<LogBook> goalArrayList) {
        this.goalArrayList = goalArrayList;
        this.activity = activity;
    }

    @Override
    public long getItemId(int position) {
        return goalArrayList.get(position).getStatus();
    }

    @Override
    public int getItemCount() {
        return goalArrayList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_book_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {

        LogBook goal_ = goalArrayList.get(position);

        viewHolder.dateTime.setText(goalArrayList.get(position).getDate_time());

        if (goalArrayList.get(position).getGoal_status().equalsIgnoreCase("")) {
            viewHolder.status.setText("-");
        } else {
            viewHolder.status.setText(goalArrayList.get(position).getGoal_status());
        }

        if (goalArrayList.get(position).getInput().equalsIgnoreCase("")) {
            viewHolder.count.setText("-");
        } else {
            viewHolder.count.setText(String.valueOf(goalArrayList.get(position).getInput()));
        }

        if (goalArrayList.get(position).getGoal_status().equalsIgnoreCase("Completed")) {
            viewHolder.logBookDetailsLayout.setBackgroundResource(R.color.goal_hour);
        } else if (goalArrayList.get(position).getGoal_status().equalsIgnoreCase("")) {
            viewHolder.logBookDetailsLayout.setBackgroundResource(R.color.white);
        } else {
            viewHolder.logBookDetailsLayout.setBackgroundResource(R.color.goal_hour_one);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView dateTime, status, count;
        final LinearLayout logBookDetailsLayout;

        MyViewHolder(View view) {
            super(view);
            dateTime = (TextView) view.findViewById(R.id.date_time);
            status = (TextView) view.findViewById(R.id.status);
            count = (TextView) view.findViewById(R.id.count);
            logBookDetailsLayout = (LinearLayout) view.findViewById(R.id.log_book_details_layout);
        }
    }
}
