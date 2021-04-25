package com.modules.team.gallery.adapter;

import android.app.Activity;
 import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.modules.team.gallery.model.MultipleImage;
import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Kailash Karankal on 5/31/2019.
 */
public class GalleryMultipleImageAdapter extends RecyclerView.Adapter<GalleryMultipleImageAdapter.MyViewHolder> {
    private final Activity activity;
    private final List<MultipleImage> galleryList;
    private final SelectedDeleteImageAdapterListener selectedDeleteImageAdapterListener;
    private static CheckBox lastChecked = null;
    private static int lastCheckedPos = 0;
    private int showSelectionIcon;

    class MyViewHolder extends RecyclerView.ViewHolder {
        final ImageView thumbnail, galleryTick, galleryTickSelected, imageviewCancel;
        final CheckBox sinleSelection;

        MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.gallery_card_thumbnail);
            sinleSelection = (CheckBox) view.findViewById(R.id.multi_select_check_box);
            galleryTick = (ImageView) view.findViewById(R.id.gallery_tick);
            galleryTickSelected = (ImageView) view.findViewById(R.id.gallery_tick_selected);
            imageviewCancel = (ImageView) view.findViewById(R.id.imageview_cancel);
        }
    }

    public GalleryMultipleImageAdapter(Activity activity, int showIcon, ArrayList<MultipleImage> galleryList, SelectedDeleteImageAdapterListener selectedDeleteImageAdapterListener) {
        this.activity = activity;
        this.showSelectionIcon = showIcon;
        this.galleryList = galleryList;
        this.selectedDeleteImageAdapterListener = selectedDeleteImageAdapterListener;
    }

    @Override
    public GalleryMultipleImageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.multiple_gallery_layout, parent, false);

        return new GalleryMultipleImageAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GalleryMultipleImageAdapter.MyViewHolder holder, final int position) {
        final MultipleImage gallery_ = galleryList.get(position);

        holder.sinleSelection.setChecked(galleryList.get(position).isSelectImgs());
        holder.sinleSelection.setTag(new Integer(position));

        //for default check in first item
        if (position == 0 && galleryList.get(0).isSelectImgs() && holder.sinleSelection.isChecked()) {
            lastChecked = holder.sinleSelection;
            lastCheckedPos = 0;
        }

        holder.sinleSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                int clickedPos = ((Integer) cb.getTag()).intValue();

                if (cb.isChecked()) {
                    if (lastChecked != null) {
                        lastChecked.setChecked(false);
                        galleryList.get(lastCheckedPos).setSelectImgs(false);
                    }
                    lastChecked = cb;
                    lastCheckedPos = clickedPos;
                } else {
                    lastChecked = null;
                }
                galleryList.get(clickedPos).setSelectImgs(cb.isChecked());
            }
        });

        holder.sinleSelection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gallery_.setSelectImgs(true);
                    holder.sinleSelection.setButtonDrawable(R.drawable.gallery_tick_selected);
                } else {
                    gallery_.setSelectImgs(false);
                    holder.sinleSelection.setButtonDrawable(R.drawable.gallery_tick_blank);
                }
            }
        });

        Glide.with(activity.getApplicationContext())
                .load(gallery_.getFile())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_image_thumb)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.thumbnail);

        holder.galleryTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery_.setSelectImgs(true);
                holder.galleryTick.setVisibility(View.GONE);
                holder.galleryTickSelected.setVisibility(View.VISIBLE);
            }
        });

        holder.galleryTickSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery_.setSelectImgs(false);
                holder.galleryTick.setVisibility(View.VISIBLE);
                holder.galleryTickSelected.setVisibility(View.GONE);
            }
        });

        holder.imageviewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery_.setSelectImgs(true);
                selectedDeleteImageAdapterListener.onItemClicked(position);
            }
        });

        if (showSelectionIcon == 1) {
            holder.sinleSelection.setVisibility(View.VISIBLE);
        } else {
            holder.sinleSelection.setVisibility(View.GONE);
        }
    }

    public interface SelectedDeleteImageAdapterListener {
        void onItemClicked(int position);
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }
}