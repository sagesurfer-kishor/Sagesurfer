package com.modules.onetime_dailysurvey.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.modules.caseload.senjam.model.SenjamListModel;
import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class OneTimeSurveyAdapter extends RecyclerView.Adapter<OneTimeSurveyAdapter.MyViewHolder> {
    private final ArrayList<SenjamListModel> senjamListModelArrayList;

    private Activity activity;
    private OneTimeDailySurveyAdapterListener oneTimeDailySurveyAdapterListener;

    public OneTimeSurveyAdapter(Activity activity, ArrayList<SenjamListModel> senjamListModelArrayList, OneTimeDailySurveyAdapterListener oneTimeDailySurveyAdapterListener) {
        this.senjamListModelArrayList = senjamListModelArrayList;
        this.activity = activity;
        this.oneTimeDailySurveyAdapterListener = oneTimeDailySurveyAdapterListener;
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(senjamListModelArrayList.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return senjamListModelArrayList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_senjam_sows_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        viewHolder.mTextDateTime.setText(getDate(senjamListModelArrayList.get(position).getAdded_date()));

        viewHolder.mTextNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oneTimeDailySurveyAdapterListener.onOneTimeDailySurveyNoteDetailsLayoutClicked(senjamListModelArrayList.get(position));
            }
        });
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy, hh:mm:ss a", cal).toString();
        return date;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout mainRelativeLayout;
        private TextView mTextNumber, mTextDateTime;

        MyViewHolder(View view) {
            super(view);
            mainRelativeLayout =  view.findViewById(R.id.note_details_layout);
            mTextNumber = view.findViewById(R.id.txt_number);
            mTextDateTime = view.findViewById(R.id.txt_date_time);
        }
    }

    public interface OneTimeDailySurveyAdapterListener {
        void onOneTimeDailySurveyNoteDetailsLayoutClicked(SenjamListModel senjamListModel);
    }
}

