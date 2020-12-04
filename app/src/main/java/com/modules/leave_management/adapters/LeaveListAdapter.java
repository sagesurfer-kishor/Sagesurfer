package com.modules.leave_management.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
 import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.modules.leave_management.activity.LeaveApplicationActivity;
import com.modules.leave_management.models.Leave;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.storage.preferences.Preferences;

import java.util.ArrayList;

/**
 * Created by Kailash Karankal on 12/19/2019.
 */
public class LeaveListAdapter extends RecyclerView.Adapter<LeaveListAdapter.MyViewHolder> {
    private final ArrayList<Leave> leaveManagementArrayList;
    private Activity activity;

    public LeaveListAdapter(Activity activity, ArrayList<Leave> leaveManagementArrayList1) {
        this.leaveManagementArrayList = leaveManagementArrayList1;
        this.activity = activity;
    }

    @Override
    public long getItemId(int position) {
        return leaveManagementArrayList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return leaveManagementArrayList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.supervisor_leave_management_child_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {

        final Leave leaveManagement = leaveManagementArrayList.get(position);

        viewHolder.fromDate.setText(GetTime.dateCaps(leaveManagement.getFrom_date()));
        viewHolder.toDate.setText(GetTime.dateCaps(leaveManagement.getTo_date()));
        viewHolder.reasonTxt.setText(leaveManagement.getReason());
        viewHolder.deleteLeave.setVisibility(View.GONE);

        viewHolder.editLeave.setColorFilter(Color.parseColor("#333333"));
        viewHolder.deleteLeave.setColorFilter(Color.parseColor("#333333"));

        if (Integer.parseInt(Preferences.get(General.NOTIFICATION_LEAVE_ID)) == leaveManagement.getId()) {
            viewHolder.leaveDetails.setBackgroundResource(R.drawable.white_rounded_rectangle_blue_border);
            Preferences.save(General.NOTIFICATION_LEAVE_ID, 0);
        }

        viewHolder.editLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent leaveApplicationDetails = new Intent(activity, LeaveApplicationActivity.class);
                leaveApplicationDetails.putExtra(General.LEAVE_UPDATE, General.LEAVE_UPDATE);
                leaveApplicationDetails.putExtra(General.LEAVE_UPDATE_SUPERVISOR, true);
                leaveApplicationDetails.putExtra(General.LEAVE_DATA, leaveManagementArrayList.get(position));
                activity.startActivity(leaveApplicationDetails);
            }
        });

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView fromDate, toDate, reasonTxt;
        final ImageView deleteLeave, editLeave;
        final LinearLayout leaveDetails;

        MyViewHolder(View view) {
            super(view);
            fromDate = (TextView) view.findViewById(R.id.from_date_txt);
            toDate = (TextView) view.findViewById(R.id.to_date_txt);
            reasonTxt = (TextView) view.findViewById(R.id.reason);

            deleteLeave = (ImageView) view.findViewById(R.id.delete_leave);
            editLeave = (ImageView) view.findViewById(R.id.edit_leave);
            leaveDetails = (LinearLayout) view.findViewById(R.id.leave_details);
        }
    }
}