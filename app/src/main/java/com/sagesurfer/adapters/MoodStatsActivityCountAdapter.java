package com.sagesurfer.adapters;

import android.app.Activity; 
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.models.MoodStats_;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Monika on 11/13/2018.
 */

public class MoodStatsActivityCountAdapter extends RecyclerView.Adapter<MoodStatsActivityCountAdapter.MyViewHolder> {
    private static final String TAG = MoodStatsActivityCountAdapter.class.getSimpleName();

    private ArrayList<MoodStats_> moodStatsList = new ArrayList<>();
    private final Activity activity;

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewActivityName;
        final TextView textViewCount;
        final RelativeLayout activityRelativeLayout;
        final ImageView activityImageView;
        final ImageView moodImageView;

        MyViewHolder(View view) {
            super(view);
            textViewActivityName = (TextView) view.findViewById(R.id.textview_activity_name);
            textViewCount = (TextView) view.findViewById(R.id.textview_count);
            activityRelativeLayout = (RelativeLayout) view.findViewById(R.id.relativelayout_image);
            activityImageView = (ImageView) view.findViewById(R.id.imageview_activity);
            moodImageView = (ImageView) view.findViewById(R.id.imageview_mood);
        }
    }

    public MoodStatsActivityCountAdapter(Activity activity, ArrayList<MoodStats_> contactList) {
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
        holder.textViewActivityName.setText(moodStatsList.get(position).getActivity());
        holder.textViewCount.setText("" + moodStatsList.get(position).getCount());

        Glide.with(activity)
                .load(moodStatsList.get(position).getUrl())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.activityImageView);
    }
}