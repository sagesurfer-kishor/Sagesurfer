package com.modules.selfgoal;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.storage.preferences.AddGoalPreferences;

import java.util.ArrayList;
import java.util.HashMap;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 24/03/2018
 *         Last Modified on
 */

public class AddGoalImageGalleryAdapter extends RecyclerView.Adapter<AddGoalImageGalleryAdapter.ViewHolder> {

    private static final String TAG = AddGoalImageGalleryAdapter.class.getSimpleName();
    private final ArrayList<HashMap<String, String>> imageList;

    private final Activity activity;
    private final AddDialogFragment imageDialogFragment;

    AddGoalImageGalleryAdapter(Activity activity, ArrayList<HashMap<String, String>> imageList,
                               AddDialogFragment imageDialogFragment) {
        this.imageList = imageList;
        this.activity = activity;
        this.imageDialogFragment = imageDialogFragment;
    }

    @Override
    public AddGoalImageGalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.add_goal_image_gallery_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddGoalImageGalleryAdapter.ViewHolder viewHolder, int i) {
        //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
        Glide.with(activity.getApplicationContext())
                .load(getImage(i))
                .thumbnail(0.1f)
                .transition(withCrossFade(10))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_image_thumb)
                        .error(R.drawable.ic_image_thumb))
                .into(viewHolder.img_android);
        viewHolder.clickImage.setTag(i);
        viewHolder.clickImage.setOnClickListener(onClick);
    }

    private final View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            AddGoalPreferences.save(General.IMAGE, imageList.get(position).get(General.ID), TAG);
            AddGoalPreferences.save(General.URL_IMAGE, imageList.get(position).get(General.NAME), TAG);
            imageDialogFragment.dismiss();
            broadcast();
        }
    };

    private void broadcast() {
        Intent intent = new Intent();
        intent.setAction("0");
        intent.putExtra(General.IMAGE, "1");
        activity.sendBroadcast(intent);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public String getId(int position) {
        return imageList.get(position).get(General.ID);
    }

    private String getImage(int position) {
        return imageList.get(position).get(General.NAME);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img_android;
        private final ImageView clickImage;

        public ViewHolder(View view) {
            super(view);
            img_android = (ImageView) view.findViewById(R.id.imageview_addgoalimagegallerylist_image);
            clickImage = (ImageView) view.findViewById(R.id.imageview_addgoalimagegallerylist_dummyimage);
        }
    }

}