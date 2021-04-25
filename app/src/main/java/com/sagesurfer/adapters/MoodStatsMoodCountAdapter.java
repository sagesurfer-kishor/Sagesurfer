package com.sagesurfer.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class MoodStatsMoodCountAdapter extends RecyclerView.Adapter<MoodStatsMoodCountAdapter.MyViewHolder> {
    private static final String TAG = MoodStatsMoodCountAdapter.class.getSimpleName();

    private final Activity activity;
    private ArrayList<MoodStats_> moodStatsList = new ArrayList<>();

    static class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewCount;
        final ImageView moodImageView;

        MyViewHolder(View view) {
            super(view);
            textViewCount = (TextView) view.findViewById(R.id.textview_count);
            moodImageView = (ImageView) view.findViewById(R.id.imageview_mood);
        }
    }

    public MoodStatsMoodCountAdapter(Activity activity, ArrayList<MoodStats_> contactList) {
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
        holder.textViewCount.setText("" + moodStatsList.get(position).getCount());

        Glide.with(activity)
                .load(moodStatsList.get(position).getUrl())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.moodImageView);
    }
}