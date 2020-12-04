package com.modules.team.gallery.adapter;


import android.app.Activity;
import android.content.Intent;
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
import com.modules.team.gallery.activity.SelectedImageListActivity;
import com.modules.team.gallery.fragment.GalleryListFragment;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.models.Gallery_;
import com.storage.preferences.Preferences;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Kailash Karankal on 7/17/2019.
 */
public class ListViewGalleryAdapter extends RecyclerView.Adapter<ListViewGalleryAdapter.MyViewHolder> {
    private final Activity activity;
    private final List<Gallery_> galleryList;

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView name;
        final TextView count;
        final ImageView thumbnail, galleryTick, galleryTickSelected;
        final RelativeLayout mainContainer;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.gallery_card_title);
            name = (TextView) view.findViewById(R.id.gallery_card_name);
            count = (TextView) view.findViewById(R.id.gallery_card_count);
            thumbnail = (ImageView) view.findViewById(R.id.gallery_card_thumbnail);
            galleryTick = (ImageView) view.findViewById(R.id.gallery_tick);
            galleryTickSelected = (ImageView) view.findViewById(R.id.gallery_tick_selected);
            mainContainer = (RelativeLayout) view.findViewById(R.id.gallery_list_item_card_main_container);
        }
    }

    public ListViewGalleryAdapter(Activity activity, List<Gallery_> galleryList) {
        this.activity = activity;
        this.galleryList = galleryList;
    }

    @Override
    public ListViewGalleryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_list_view_item_layout, parent, false);

        return new ListViewGalleryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListViewGalleryAdapter.MyViewHolder holder, final int position) {
        final Gallery_ gallery_ = galleryList.get(position);
        holder.title.setText(gallery_.getName());
        holder.count.setText("(" + String.valueOf(gallery_.getCount()) + ")");
        holder.name.setText(gallery_.getFullName());

        //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
        Glide.with(activity.getApplicationContext())
                .load(gallery_.getImage())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_image_thumb)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.thumbnail);

        holder.mainContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageIntent = new Intent(activity.getApplicationContext(), SelectedImageListActivity.class);
                imageIntent.putExtra(General.ID, gallery_.getId());
                imageIntent.putExtra(General.TITLE, gallery_.getName());
                imageIntent.putExtra(General.ADDED_BY, gallery_.getAdded_by());
                imageIntent.putExtra(General.IS_MODERATOR, gallery_.getIs_modarator());
                activity.startActivity(imageIntent);
                activity.overridePendingTransition(0, 0);
            }
        });

        if (((Integer.parseInt(Preferences.get(General.GROUP_OWNER_ID)) == gallery_.getAdded_by()) && (Integer.parseInt(Preferences.get(General.USER_ID)) == gallery_.getAdded_by())) || (Integer.parseInt(Preferences.get(General.USER_ID)) == (Integer.parseInt(Preferences.get(General.GROUP_OWNER_ID))))) {
            holder.galleryTick.setVisibility(View.VISIBLE);
        } else if ((Integer.parseInt(Preferences.get(General.USER_ID)) == gallery_.getAdded_by()) || (gallery_.getIs_modarator() == 1 && gallery_.getAdded_by() != Integer.parseInt(Preferences.get(General.GROUP_OWNER_ID)))) {
            holder.galleryTick.setVisibility(View.VISIBLE);
        } else {
            holder.galleryTick.setVisibility(View.GONE);
        }

        holder.galleryTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery_.setSelectImgs(true);
                holder.galleryTick.setVisibility(View.GONE);
                holder.galleryTickSelected.setVisibility(View.VISIBLE);

                GalleryListFragment.showDeleteButton();
            }
        });

        holder.galleryTickSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery_.setSelectImgs(false);
                holder.galleryTick.setVisibility(View.VISIBLE);
                holder.galleryTickSelected.setVisibility(View.GONE);

                GalleryListFragment.hideDeleteButton();
            }
        });
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }
}