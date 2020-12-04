package com.modules.dailydosing.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.modules.dailydosing.model.DailyDosing;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SenjamDailyDosingListAdapter extends RecyclerView.Adapter<SenjamDailyDosingListAdapter.MyViewHolder> {
    private static final String TAG = SenjamDailyDosingListAdapter.class.getSimpleName();
    private final ArrayList<DailyDosing> dailyDosingArrayList;

    private Activity activity;
    private SenjamDailyDosingListAdapterListener senjamDailyDosingListAdapterListener;

    public SenjamDailyDosingListAdapter(Activity activity, ArrayList<DailyDosing> dailyDosingArrayList, SenjamDailyDosingListAdapterListener senjamDailyDosingListAdapterListener) {
        this.dailyDosingArrayList = dailyDosingArrayList;
        this.activity = activity;
        this.senjamDailyDosingListAdapterListener = senjamDailyDosingListAdapterListener;
    }

//    @Override
//    public long getItemId(int position) {
//        return Long.parseLong(dailyDosingArrayList.get(position).getId());
//    }

    @Override
    public int getItemCount() {
        return dailyDosingArrayList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_senjam_daily_dosing_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        viewHolder.mTextDosageDate.setText(getDate(dailyDosingArrayList.get(position).getDate()));
        viewHolder.mTextDosageTakenYes.setText(dailyDosingArrayList.get(position).getYes());
        viewHolder.mTextDosageTakenNo.setText(dailyDosingArrayList.get(position).getNo());
        viewHolder.mTextDosageStatus.setText(dailyDosingArrayList.get(position).getGoal_status());

        /*this condition is for complete, partially complete and incomplete status indicator*/
        if (dailyDosingArrayList.get(position).getGoal_status_id().equalsIgnoreCase(General.GOAL_STATUS_ID_COMPLETE)){
            viewHolder.mImageViewIndicator.setImageDrawable(activity.getResources().getDrawable(R.drawable.senjam_status_green));
        }else if (dailyDosingArrayList.get(position).getGoal_status_id().equalsIgnoreCase(General.GOAL_STATUS_ID_PARTIAL_COMPLETE)){
            viewHolder.mImageViewIndicator.setImageDrawable(activity.getResources().getDrawable(R.drawable.senjam_status_orange));
        }else if (dailyDosingArrayList.get(position).getGoal_status_id().equalsIgnoreCase(General.GOAL_STATUS_ID_INCOMPLETE)){
            viewHolder.mImageViewIndicator.setImageDrawable(activity.getResources().getDrawable(R.drawable.senjam_status_red));
        }
    }

    private String getDate(String original_date) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat convertFormat = new SimpleDateFormat("MMM dd, yyyy");
        try {
            Date originalDate = originalFormat.parse(original_date);
            return convertFormat.format(originalDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return original_date;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private final CardView mCardView;
        private TextView  mTextDosageDate, mTextDosageTakenYes, mTextDosageTakenNo, mTextDosageStatus;
        private ImageView mImageViewIndicator;

        MyViewHolder(View view) {
            super(view);
            mCardView =  view.findViewById(R.id.cardView);
            mTextDosageDate = view.findViewById(R.id.txt_dosage_date_value);
            mTextDosageTakenYes = view.findViewById(R.id.txt_dosage_taken_yes_value);
            mTextDosageTakenNo = view.findViewById(R.id.txt_dosage_taken_no_value);
            mTextDosageStatus = view.findViewById(R.id.txt_dosage_status_Value);
            mImageViewIndicator = view.findViewById(R.id.image_view_daily_dosing_indicator);
        }
    }

    public interface SenjamDailyDosingListAdapterListener {
        void onDailyDosingLayoutClicked(DailyDosing dailyDosing);
    }
}

