package com.modules.home;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.modules.goal_assignment.model.AssignedGoals;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.library.ChangeCase;

import java.util.ArrayList;

/**
 * Created by Kailash Karankal on 1/9/2020.
 */
public class GoalPopUpAdapter extends RecyclerView.Adapter<GoalPopUpAdapter.MyViewHolder> {
    private static final String TAG = GoalPopUpAdapter.class.getSimpleName();
    private final ArrayList<AssignedGoals> assignedGoalsArrayList;
    private Activity activity;

    public GoalPopUpAdapter(Activity activity, ArrayList<AssignedGoals> assignedGoalsArrayList1) {
        this.assignedGoalsArrayList = assignedGoalsArrayList1;
        this.activity = activity;
    }

    @Override
    public long getItemId(int position) {
        return assignedGoalsArrayList.get(position).getWer_goal_id();
    }

    @Override
    public int getItemCount() {
        return assignedGoalsArrayList.size();
    }


    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_popup_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {

        final AssignedGoals assignedGoals = assignedGoalsArrayList.get(position);

        viewHolder.goalName.setText(ChangeCase.toTitleCase(assignedGoals.getGoal_name()));
        viewHolder.goalQuestion.setText(ChangeCase.toTitleCase(assignedGoals.getGoal_question()));

        viewHolder.yesNo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.ques1_yes) {
                    assignedGoals.setYes("1");
                    assignedGoals.setSelected(true);
                } else if (checkedId == R.id.ques1_no) {
                    assignedGoals.setNo("0");
                    assignedGoals.setSelected(false);
                }
                notifyDataSetChanged();
            }
        });

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView goalName, goalQuestion;
        final RadioGroup yesNo;
        final RadioButton yesQues, noQues;

        MyViewHolder(View view) {
            super(view);
            goalName = (TextView) view.findViewById(R.id.goal_name);
            goalQuestion = (TextView) view.findViewById(R.id.goal_question);
            yesNo = (RadioGroup) view.findViewById(R.id.ques1_radio_group);
            yesQues = (RadioButton) view.findViewById(R.id.ques1_yes);
            noQues = (RadioButton) view.findViewById(R.id.ques1_no);
        }
    }
}

