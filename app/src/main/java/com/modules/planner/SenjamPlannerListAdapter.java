package com.modules.planner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.format.DateFormat;
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
import com.sagesurfer.models.DailyPlanner_;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SenjamPlannerListAdapter extends RecyclerView.Adapter<SenjamPlannerListAdapter.MyViewHolder> {
    private static final String TAG = SenjamPlannerListAdapter.class.getSimpleName();
    List<DailyPlanner_> dailyPlannerList;

    private Activity activity;
    private SenjamPlannerListAdapterListener senjamPlannerListAdapterListener;

    public SenjamPlannerListAdapter(Activity activity, List<DailyPlanner_> dailyPlannerList, SenjamPlannerListAdapterListener senjamPlannerListAdapterListener) {
        this.dailyPlannerList = dailyPlannerList;
        this.activity = activity;
        this.senjamPlannerListAdapterListener = senjamPlannerListAdapterListener;
    }

//    @Override
//    public long getItemId(int position) {
//        return Long.parseLong(dailyDosingArrayList.get(position).getId());
//    }

    @Override
    public int getItemCount() {
        return dailyPlannerList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_senjam_planner_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {

        DailyPlanner_ dailyPlanner_ = dailyPlannerList.get(position);
        viewHolder.mTextDosageDate.setText(getDate(String.valueOf(dailyPlanner_.getGoal().getLast_updated())));
        viewHolder.mTextDosageTakenYes.setText(dailyPlanner_.getGoal().getYes());
        viewHolder.mTextDosageTakenNo.setText(dailyPlanner_.getGoal().getNo());


        /*this condition is for complete, partially complete and incomplete status indicator*/
        if (dailyPlanner_.getGoal().getToday_status() == 1) {
            String y = dailyPlanner_.getGoal().getYes();
            String n = dailyPlanner_.getGoal().getNo();

            if (y != null && n != null) {
                if (!y.equals("0") && !n.equals("0")) {
                    viewHolder.mTextDosageStatus.setText(General.GOAL_STATUS_COMPLETE);
                    viewHolder.mImageViewIndicator.setImageDrawable(activity.getResources().getDrawable(R.drawable.senjam_status_green));

                }else if(y.equals("2")){
                    viewHolder.mTextDosageStatus.setText(General.GOAL_STATUS_COMPLETE);
                    viewHolder.mImageViewIndicator.setImageDrawable(activity.getResources().getDrawable(R.drawable.senjam_status_green));
                }
            }


        } else if (dailyPlanner_.getGoal().getToday_status() == 2) {
            viewHolder.mTextDosageStatus.setText(General.GOAL_STATUS_PARTIAL_COMPLETE);
            viewHolder.mImageViewIndicator.setImageDrawable(activity.getResources().getDrawable(R.drawable.senjam_status_orange));
        } else if (dailyPlanner_.getGoal().getToday_status() == 3) {
            viewHolder.mTextDosageStatus.setText(General.GOAL_STATUS_INCOMPLETE);
            viewHolder.mImageViewIndicator.setImageDrawable(activity.getResources().getDrawable(R.drawable.senjam_status_red));
        }


//        viewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                senjamPlannerListAdapterListener.onSenjamPlannerLayoutClicked(dailyDosingArrayList.get(position));
//            }
//        });
    }

    private String getDate(String original_date) {
//        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
//        SimpleDateFormat convertFormat = new SimpleDateFormat("MMM dd, yyyy");
//        try {
//            Date originalDate = originalFormat.parse(original_date);
//            return convertFormat.format(originalDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return original_date;

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
//        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy", cal).toString();
        return date;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private final CardView mCardView;
        private TextView mTextDosageDate, mTextDosageTakenYes, mTextDosageTakenNo, mTextDosageStatus;
        private ImageView mImageViewIndicator;

        MyViewHolder(View view) {
            super(view);
            mCardView = view.findViewById(R.id.cardView);
            mTextDosageDate = view.findViewById(R.id.txt_dosage_date_value);
            mTextDosageTakenYes = view.findViewById(R.id.txt_dosage_taken_yes_value);
            mTextDosageTakenNo = view.findViewById(R.id.txt_dosage_taken_no_value);
            mTextDosageStatus = view.findViewById(R.id.txt_dosage_status_Value);
            mImageViewIndicator = view.findViewById(R.id.image_view_daily_dosing_indicator);
        }
    }

    public interface SenjamPlannerListAdapterListener {
        void onSenjamPlannerLayoutClicked(DailyPlanner_ dailyPlanner);
    }
}

