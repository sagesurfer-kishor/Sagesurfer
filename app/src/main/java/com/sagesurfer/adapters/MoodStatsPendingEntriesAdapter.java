package com.sagesurfer.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.modules.mood.MoodAddActivity;
import com.sagesurfer.animation.ActivityTransition;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.models.MoodStats_;
import com.storage.preferences.Preferences;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Monika on 11/13/2018.
 */

public class MoodStatsPendingEntriesAdapter extends RecyclerView.Adapter<MoodStatsPendingEntriesAdapter.MyViewHolder> {
    private static final String TAG = MoodStatsPendingEntriesAdapter.class.getSimpleName();

    private final Activity activity;
    private ArrayList<MoodStats_> moodStatsList = new ArrayList<>();

    class MyViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout linearLayoutMoodStatsItem;
        final TextView textViewActivityName;
        final TextView textViewCount;
        final RelativeLayout activityRelativeLayout;
        final ImageView activityImageView;
        final ImageView moodImageView;

        MyViewHolder(View view) {
            super(view);
            linearLayoutMoodStatsItem = (LinearLayout) view.findViewById(R.id.linearlayout_mood_stats_item);
            textViewActivityName = (TextView) view.findViewById(R.id.textview_activity_name);
            textViewCount = (TextView) view.findViewById(R.id.textview_count);
            activityRelativeLayout = (RelativeLayout) view.findViewById(R.id.relativelayout_image);
            activityImageView = (ImageView) view.findViewById(R.id.imageview_activity);
            moodImageView = (ImageView) view.findViewById(R.id.imageview_mood);
        }
    }

    public MoodStatsPendingEntriesAdapter(Activity activity, ArrayList<MoodStats_> contactList) {
        this.activity = activity;
        this.moodStatsList = contactList;
    }

    @Override
    public int getItemCount() {
        return moodStatsList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mood_stats_recycler_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.moodImageView.setVisibility(View.GONE);
        holder.activityRelativeLayout.setVisibility(View.VISIBLE);
        holder.textViewActivityName.setVisibility(View.VISIBLE);
        //holder.textViewActivityName.setText(moodStatsList.get(position).getActivity());
        holder.textViewCount.setText(moodStatsList.get(position).getDay() + " " + moodStatsList.get(position).getMonth());

        try {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            String inputDateStr = moodStatsList.get(position).getDate();
            Date date = inputFormat.parse(inputDateStr);
            DateFormat outputFormat = new SimpleDateFormat("EEE");
            String outputDateStr = outputFormat.format(date);
            holder.textViewActivityName.setText(outputDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (moodStatsList.get(position).getIs_added() == 1) {
            holder.activityRelativeLayout.setBackgroundResource(R.drawable.circle_gray_corners_fill);
            holder.activityImageView.setImageResource(R.drawable.vi_check_green);
        } else {
            holder.activityRelativeLayout.setBackgroundResource(R.drawable.circle_red_corners);
            int color = Color.parseColor("#e63333"); //red
            holder.activityImageView.setColorFilter(color);
            holder.activityImageView.setImageResource(R.drawable.vi_cancel_gray);
        }
        holder.linearLayoutMoodStatsItem.setTag(position);


        holder.linearLayoutMoodStatsItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                    int position = (Integer) v.getTag();
                    MoodStats_ moodStats_ = moodStatsList.get(position);
                    Preferences.save(General.SELECTED_MOOD_FRAGMENT, activity.getResources().getString(R.string.add_mood));
                /*android.app.DialogFragment dialogFrag = new MoodAddActivity();
                Bundle args = new Bundle();
                args.putSerializable(Actions_.PENDING_MOOD, moodStats_);
                dialogFrag.setArguments(args);
                dialogFrag.show(activity.getFragmentManager().beginTransaction(), Actions_.ADD_MOOD);*/
                    Preferences.save(General.IS_ADD_MOOD_ACTIVITY_BACK_PRESSED, true);
                    Intent createIntent = new Intent(activity.getApplicationContext(), MoodAddActivity.class);
                    createIntent.putExtra(Actions_.PENDING_MOOD, moodStats_);
                   // activity.startActivity(createIntent, ActivityTransition.moveToNextAnimation(activity.getApplicationContext()));

                    activity.startActivity(createIntent);
                    activity.overridePendingTransition(0, 0);
                }
            }
        });
    }
}