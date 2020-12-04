package com.modules.re_assignment.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.modules.re_assignment.model.ReAssignment;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.library.ChangeCase;

import java.util.ArrayList;

/**
 * Created by Kailash Karankal on 12/20/2019.
 */
public class ReAssignmentListAdapter extends RecyclerView.Adapter<ReAssignmentListAdapter.MyViewHolder> {
    private final ArrayList<ReAssignment> reAssignmentArrayList;
    private Activity activity;
    private LinearLayoutManager mLinearLayoutManager;
    private StudDataListAdapter studDataListAdapter;


    public ReAssignmentListAdapter(Activity activity, ArrayList<ReAssignment> reAssignmentArrayList1) {
        this.reAssignmentArrayList = reAssignmentArrayList1;
        this.activity = activity;
    }

    @Override
    public long getItemId(int position) {
        return reAssignmentArrayList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return reAssignmentArrayList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.re_assignment_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {

        final ReAssignment reAssignment = reAssignmentArrayList.get(position);

        if (reAssignment.getStudAssignDataArrayList().size() == 0) {
            viewHolder.studentList.setVisibility(View.GONE);
        } else {
            studDataListAdapter = new StudDataListAdapter(activity, reAssignment.getStudAssignDataArrayList());
            viewHolder.studentList.setAdapter(studDataListAdapter);
        }

        viewHolder.nameText.setText(ChangeCase.toTitleCase(reAssignment.getName()));

        viewHolder.leaveExpandLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.upArrow.setVisibility(View.VISIBLE);
                viewHolder.downArrow.setVisibility(View.GONE);
                viewHolder.leaveLinearLayout.setVisibility(View.VISIBLE);
                viewHolder.profileImage.setImageResource(R.drawable.leave_coach_blue_icon);
                //viewHolder.supervisorColorLayout.setBackgroundColor(R.drawable.blue_rounded_border);
                //viewHolder.leaveExpandLayout.setBackgroundColor(R.drawable.leave_rounded_border);
            }
        });

        viewHolder.downArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.upArrow.setVisibility(View.VISIBLE);
                viewHolder.downArrow.setVisibility(View.GONE);
                viewHolder.leaveLinearLayout.setVisibility(View.VISIBLE);
                viewHolder.profileImage.setImageResource(R.drawable.leave_coach_blue_icon);
                // viewHolder.supervisorColorLayout.setBackgroundColor(R.drawable.blue_rounded_border);
                // viewHolder.leaveExpandLayout.setBackgroundColor(R.drawable.leave_rounded_border);
            }
        });

        viewHolder.upArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.upArrow.setVisibility(View.GONE);
                viewHolder.downArrow.setVisibility(View.VISIBLE);
                viewHolder.leaveLinearLayout.setVisibility(View.GONE);
                viewHolder.profileImage.setImageResource(R.drawable.leave_coach_white_icon);
                //viewHolder.supervisorColorLayout.setBackgroundColor(R.drawable.leave_rounded_border);
                //viewHolder.leaveExpandLayout.setBackgroundColor(R.drawable.leave_rounded_border);
            }
        });
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView nameText;
        final ImageView profileImage, downArrow, upArrow;
        final LinearLayout leaveLinearLayout, leaveExpandLayout;
        RecyclerView studentList;
        final RelativeLayout supervisorColorLayout;

        MyViewHolder(View view) {
            super(view);
            nameText = (TextView) view.findViewById(R.id.textview_leave_name);
            profileImage = (ImageView) view.findViewById(R.id.imageview_profile);
            leaveLinearLayout = (LinearLayout) view.findViewById(R.id.leave_details);
            leaveExpandLayout = (LinearLayout) view.findViewById(R.id.linearlayout_details);
            supervisorColorLayout = (RelativeLayout) view.findViewById(R.id.supervisor_color_layout);

            downArrow = (ImageView) view.findViewById(R.id.down_arrow);
            upArrow = (ImageView) view.findViewById(R.id.up_arrow);

            studentList = (RecyclerView) view.findViewById(R.id.leave_list_recycler_view);
            mLinearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
            studentList.setLayoutManager(mLinearLayoutManager);
        }
    }
}
