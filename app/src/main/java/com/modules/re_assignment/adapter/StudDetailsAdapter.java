package com.modules.re_assignment.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.modules.re_assignment.model.ReAssignment;
import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Kailash Karankal on 12/20/2019.
 */
public class StudDetailsAdapter extends RecyclerView.Adapter<StudDetailsAdapter.MyViewHolder> {
    private final ArrayList<ReAssignment> studAssignDataArrayList;
    private Activity activity;

    public StudDetailsAdapter(Activity activity, ArrayList<ReAssignment> studAssignDataArrayList1) {
        this.studAssignDataArrayList = studAssignDataArrayList1;
        this.activity = activity;
    }

    @Override
    public long getItemId(int position) {
        return studAssignDataArrayList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return studAssignDataArrayList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_details_assignment, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {

        final ReAssignment studAssignData = studAssignDataArrayList.get(position);

        viewHolder.studName.setText(studAssignData.getName());
        viewHolder.date.setText(getDate(studAssignData.getAdded_date()));
        viewHolder.reason.setText(studAssignData.getReason());
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView studName, date, reason;

        MyViewHolder(View view) {
            super(view);
            studName = (TextView) view.findViewById(R.id.coach_name);
            date = (TextView) view.findViewById(R.id.date_txt);
            reason = (TextView) view.findViewById(R.id.reason_txt);
        }
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy | hh:mm a", cal).toString();
        return date;
    }
}