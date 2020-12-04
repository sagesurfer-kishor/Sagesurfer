package com.modules.motivation.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.modules.motivation.fragment.MotivationContentType_;
import com.modules.motivation.model.MotivationLibrary_;
import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Monika on 3/28/2019.
 */

public class MotivationLibraryListAdapter extends ArrayAdapter<MotivationLibrary_> {
    private static final String TAG = MotivationLibraryListAdapter.class.getSimpleName();

    private final ArrayList<MotivationLibrary_> motivationLibraryList;
    private final Activity activity;

    public MotivationLibraryListAdapter(Activity activity, ArrayList<MotivationLibrary_> motivationLibraryList) {
        super(activity, 0, motivationLibraryList);
        this.motivationLibraryList = motivationLibraryList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return motivationLibraryList.size();
    }

    @Override
    public MotivationLibrary_ getItem(int position) {
        if (motivationLibraryList != null && motivationLibraryList.size() > 0) {
            return motivationLibraryList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return motivationLibraryList.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.motivation_list_item, parent, false);
            holder = new ViewHolder();

            holder.titleText = (TextView) view.findViewById(R.id.textview_title);
            holder.descriptionText = (TextView) view.findViewById(R.id.textview_description);
            holder.categoryText = (TextView) view.findViewById(R.id.textview_category);
            holder.relativeLayoutIcon = (RelativeLayout) view.findViewById(R.id.relativelayout_icon);
            holder.thumbnail = (ImageView) view.findViewById(R.id.imageview_icon);
            holder.icon = (AppCompatImageView) view.findViewById(R.id.imageview_type_icon);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        MotivationLibrary_ motivationLibrary = motivationLibraryList.get(position);

        if (motivationLibrary.getStatus() == 1) {
            int content_type = MotivationContentType_.nameToType(motivationLibraryList.get(position).getContent_type());
            holder.titleText.setText(motivationLibrary.getTitle());
            holder.descriptionText.setText(motivationLibrary.getDescription());

            if (content_type == 1 || content_type == 2) {
                String thumbnail_url = motivationLibrary.getContent_path();
                Glide.with(activity.getApplicationContext())
                        .load(thumbnail_url)
                        .transition(withCrossFade())
                        .apply(new RequestOptions()
                                //.placeholder(GetThumbnails.userIcon(thumbnail_url))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .override(getThumbnailSize()[0], getThumbnailSize()[1])
                                .centerCrop())
                        .into(holder.thumbnail);
            }

            applyIcon(holder, position, content_type);
        }
        return view;
    }

    private int[] getThumbnailSize() {
        int width = (int) activity.getApplicationContext().getResources().getDimension(R.dimen.thumbnail_size);
        int height = (int) activity.getApplicationContext().getResources().getDimension(R.dimen.thumbnail_size);
        int[] dimension = new int[2];
        dimension[0] = width;
        dimension[1] = height;
        return dimension;
    }

    private void applyIcon(ViewHolder holder, int position, int content_type) {
        switch (content_type) {
            case 1:
                holder.icon.setImageResource(R.drawable.vi_care_image_white);
                holder.categoryText.setText("Images");
                break;
            case 2:
                holder.icon.setImageResource(R.drawable.vi_care_video_white);
                holder.categoryText.setText("Videos");
                break;
            case 3:
                //holder.icon.setImageResource(R.drawable.vi_care_audio_white);
                holder.relativeLayoutIcon.setVisibility(View.GONE);
                holder.icon.setVisibility(View.GONE);
                holder.thumbnail.setVisibility(View.GONE);
                holder.categoryText.setText("Audio");
                break;
            default:
                holder.relativeLayoutIcon.setVisibility(View.GONE);
                holder.icon.setVisibility(View.GONE);
                holder.thumbnail.setVisibility(View.GONE);
                holder.categoryText.setText("Text Articles");
                break;
        }
    }

    private class ViewHolder {
        TextView titleText, descriptionText, categoryText;
        ImageView icon, thumbnail;
        RelativeLayout relativeLayoutIcon;
    }
}