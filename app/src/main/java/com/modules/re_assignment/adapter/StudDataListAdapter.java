package com.modules.re_assignment.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
 import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.modules.re_assignment.activity.ReAssignmentDetailsActivity;
import com.modules.re_assignment.model.ReAssignment;
import com.modules.re_assignment.model.StudAssignData;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Journaling_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.RequestBody;

/**
 * Created by Kailash Karankal on 12/20/2019.
 */
public class StudDataListAdapter extends RecyclerView.Adapter<StudDataListAdapter.MyViewHolder> {
    private static final String TAG = StudDataListAdapter.class.getSimpleName();
    private final ArrayList<StudAssignData> studAssignDataArrayList;
    private Activity activity;
    private ArrayList<ReAssignment> reAssignmentDetails = new ArrayList<>();

    public StudDataListAdapter(Activity activity, ArrayList<StudAssignData> studAssignDataArrayList1) {
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.re_assignment_child_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        final StudAssignData studAssignData = studAssignDataArrayList.get(position);

        viewHolder.studName.setText(studAssignData.getName());
        viewHolder.assignedFrom.setText(getDate(studAssignData.getAssigned_date()));

        viewHolder.reAssignmentLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignmentStudentDetails(studAssignDataArrayList.get(position).getId(), studAssignDataArrayList.get(position));
            }
        });

    }

    private void assignmentStudentDetails(int id, StudAssignData studAssignData) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, General.GET_REASSIGNMENT_DETAILS);
        requestMap.put(General.STUD_ID, String.valueOf(id));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_WERHOPE_ASSIGNMENT;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    reAssignmentDetails = Journaling_.parseReAssignment(response, activity, TAG);
                    if (reAssignmentDetails.size() > 0) {
                        if (reAssignmentDetails.get(0).getStatus() == 1) {
                            Intent assignmentDetails = new Intent(activity, ReAssignmentDetailsActivity.class);
                            assignmentDetails.putExtra(General.REASSIGNMENT_DATA, studAssignData);
                            activity.startActivity(assignmentDetails);
                        } else {
                            Toast.makeText(activity, "No Coach reassignment available.", Toast.LENGTH_LONG).show();
                        }
                    }
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView studName, assignedFrom, reAssignmentLog;

        MyViewHolder(View view) {
            super(view);
            studName = (TextView) view.findViewById(R.id.stude_name);
            assignedFrom = (TextView) view.findViewById(R.id.assigned_date);
            reAssignmentLog = (TextView) view.findViewById(R.id.re_assignment_log);
        }
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy | hh:mm a", cal).toString();
        return date;
    }
}