package com.modules.team;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.models.Video_;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 08-08-2017
 *         Last Modified on 14-12-2017
 */

class VideoGalleryAdapter extends RecyclerView.Adapter<VideoGalleryAdapter.MyViewHolder> {

    private final Context mContext;
    private final List<Video_> videoList;

    private final VideoAdapterListener videoAdapterListener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView name;
        final LinearLayout countLayout;
        final ImageView thumbnail;
        final LinearLayout mainContainer;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.gallery_card_title);
            name = (TextView) view.findViewById(R.id.gallery_card_name);
            countLayout = (LinearLayout) view.findViewById(R.id.gallery_card_counter_layout);
            thumbnail = (ImageView) view.findViewById(R.id.gallery_card_thumbnail);
            mainContainer = (LinearLayout) view.findViewById(R.id.gallery_list_item_card_main_container);
        }
    }


    VideoGalleryAdapter(Context mContext, List<Video_> videoList, VideoAdapterListener videoAdapterListener) {
        this.mContext = mContext;
        this.videoAdapterListener = videoAdapterListener;
        this.videoList = videoList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_list_card_item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Video_ gallery_ = videoList.get(position);
        holder.title.setText(gallery_.getTitle());
        holder.countLayout.setVisibility(View.GONE);
        holder.name.setText(gallery_.getFullName());

        //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
        Glide.with(mContext)
                .load(gallery_.getThumbnail())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_image_thumb)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.thumbnail);

        applyClickEvents(holder, position);
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.mainContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoAdapterListener.onItemClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    interface VideoAdapterListener {
        void onItemClicked(int position);
    }
}