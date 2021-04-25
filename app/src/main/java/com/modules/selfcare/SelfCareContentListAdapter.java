package com.modules.selfcare;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.BuildConfig;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.models.Content_;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.storage.preferences.Preferences;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Kailash Karankal
 */

class SelfCareContentListAdapter extends ArrayAdapter<Content_> {
    private final List<Content_> contentList;
    private static final String TAG = SelfCareContentListAdapter.class.getSimpleName();
    private final Activity activity;
    private WeakReference<SelfCareContentListAdapterListener> listener;
    private ViewHolder viewHolder;

    SelfCareContentListAdapter(Activity activity, List<Content_> contentList, SelfCareContentListAdapterListener listener) {
        super(activity, 0, contentList);
        this.contentList = contentList;
        this.activity = activity;
        this.listener = new WeakReference<>(listener);
    }

    @Override
    public int getCount() {
        return contentList.size();
    }

    @Override
    public Content_ getItem(int position) {
        if (contentList != null && contentList.size() > 0) {
            return contentList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return contentList.get(position).getId();
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.self_care_content_list_item, parent, false);

            viewHolder.relativeLayoutCaseloadDetails = (RelativeLayout) view.findViewById(R.id.relativelayout_caseload_details);
            viewHolder.textViewSelfCareContentListItemTitle = (TextView) view.findViewById(R.id.textview_selfcarecontentlistitem_title);
            viewHolder.textViewSelfCareContentListItemDescription = (TextView) view.findViewById(R.id.textview_selfcarecontentlistitem_description);
            viewHolder.textViewSelfCareContentListItemCategory = (TextView) view.findViewById(R.id.textview_selfcarecontentlistitem_category);
            viewHolder.textViewSelfCareContentListItemLikeCount = (TextView) view.findViewById(R.id.textview_selfcarecontentlistitem_like_count);
            viewHolder.textViewSelfCareContentListItemCommentCount = (TextView) view.findViewById(R.id.tetxtview_selfcarecontentlistitem_comment_count);
            viewHolder.textViewSelfCareContentListItemShareCount = (TextView) view.findViewById(R.id.textview_selfcarecontentlistitem_share_count);
            viewHolder.imageViewSelfCareContentListItemIcon = (ImageView) view.findViewById(R.id.imageview_selfcarecontentlistitem_icon);
            viewHolder.imageViewSelfCareContentListItemTypeIcon = (AppCompatImageView) view.findViewById(R.id.imageview_selfcarecontentlistitem_type_icon);
            viewHolder.imageViewSelfCareContentListItemLike = (AppCompatImageButton) view.findViewById(R.id.btn_like);
            viewHolder.imageViewSelfCareContentListItemComment = (AppCompatImageView) view.findViewById(R.id.imageview_selfcarecontentlistitem_comment);
            viewHolder.imageViewSelfCareContentListItemShare = (ImageButton) view.findViewById(R.id.btn_share);
            viewHolder.relativeLayoutSelfCareContentListItemIcon = (RelativeLayout) view.findViewById(R.id.relativelayout_selfcarecontentlistitem_icon);
            viewHolder.mLinearLayoutShare = (LinearLayout) view.findViewById(R.id.linear_share);

            view.setTag(viewHolder);
        }

        if (contentList.get(position).getStatus() == 1) {
            viewHolder.textViewSelfCareContentListItemTitle.setText(contentList.get(position).getTitle());
            viewHolder.textViewSelfCareContentListItemDescription.setText(contentList.get(position).getDescription());
            viewHolder.textViewSelfCareContentListItemCategory.setText(contentList.get(position).getType());
            viewHolder.textViewSelfCareContentListItemLikeCount.setText(GetCounters.convertCounter(contentList.get(position).getLike()));
            viewHolder.textViewSelfCareContentListItemCommentCount.setText(GetCounters.convertCounter(contentList.get(position).getComments()));
            viewHolder.textViewSelfCareContentListItemShareCount.setVisibility(View.GONE);
            if (toggleThumbnail(contentList.get(position).getType(), viewHolder)) {
                Glide.with(activity.getApplicationContext())
                        .load(contentList.get(position).getThumb_path())
                        .transition(withCrossFade())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.vi_image_file)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .override(getThumbnailSize()[0], getThumbnailSize()[1]))
                        .into(viewHolder.imageViewSelfCareContentListItemIcon);

                applyIcon(viewHolder, position);
            }
            applyLike(viewHolder, position);
            applyComment(viewHolder, position);
//            applyClickEvents(viewHolder, position);

            viewHolder.imageViewSelfCareContentListItemLike.setTag(position);
            viewHolder.imageViewSelfCareContentListItemLike.setOnClickListener(onClick);
            viewHolder.imageViewSelfCareContentListItemComment.setTag(position);
            viewHolder.imageViewSelfCareContentListItemComment.setOnClickListener(onClick);
            /**
             * @Author : Mahesh
             * @Uses : clicked on recycler view item
             */
            viewHolder.relativeLayoutCaseloadDetails.setTag(position);
            viewHolder.relativeLayoutCaseloadDetails.setOnClickListener(onClick);
            /**
             * @Author : Mahesh
             * @Uses : clicked on share button
             */
            viewHolder.imageViewSelfCareContentListItemShare.setOnClickListener(onClick);
            viewHolder.imageViewSelfCareContentListItemShare.setTag(position);

            // added by kishor k 09-09-2020
            Glide.with(activity.getApplicationContext())
                    .load(contentList.get(position).getThumb_path())
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.vi_image_file)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override(getThumbnailSize()[0], getThumbnailSize()[1]))
                    .into(viewHolder.imageViewSelfCareContentListItemIcon);

            applyReadStatus(viewHolder, contentList.get(position), position);
        }
        return view;
    }

    private void applyReadStatus(ViewHolder holder, Content_ content_, int position) {
        if (Preferences.get(General.SELFCARE_ID) != null && !Preferences.get(General.SELFCARE_ID).equalsIgnoreCase("")) {
            if (Preferences.get(General.SELFCARE_ID).equalsIgnoreCase(String.valueOf(content_.getId()))) {
                holder.relativeLayoutCaseloadDetails.setBackground(activity.getResources().getDrawable(R.drawable.white_rounded_rectangle_blue_border));
                Preferences.save(General.SELFCARE_ID, "");
            }
        }
        if (content_.getIs_read() == 1) {
            holder.textViewSelfCareContentListItemTitle.setTextColor(ContextCompat.getColor(activity, R.color.text_color_read));
        } else {
            holder.textViewSelfCareContentListItemTitle.setTextColor(ContextCompat.getColor(activity, R.color.text_color_primary));
        }
    }

    private final View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            int status = 11;
            Content_ content_ = contentList.get(position);
            switch (v.getId()) {
                case R.id.imageview_selfcarecontentlistitem_comment:
                    Toast.makeText(activity, "Comment button clicked", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.btn_like:
                    status = SelfCareOperations.likeUnlike("" + content_.getId(), TAG, v, activity.getApplicationContext(), activity);
                    showResponses(status, v);
                    if (status == 1) {
                        toggleLike(position, content_.getIs_like());
                    }
                    break;

                case R.id.btn_share:
                    /**
                     * @Author mahesh
                     * after button clicked initializing clicked event previously :: applyClickEvents()
                     */
                        listener.get().onItemClicked(position, "mainContainer");
                    break;

                case R.id.relativelayout_caseload_details:
                    /**
                     * @Author mahesh
                     */
                        listener.get().onItemClicked(position, "listItem");
                    break;
            }
        }
    };

    // toggle like button based on user action
    private void toggleLike(int position, int is_like) {
        Content_ content_ = contentList.get(position);
        if (is_like == 1) {
            content_.setIs_like(0);
            content_.setLike(content_.getLike() - 1);
        } else {
            content_.setIs_like(1);
            content_.setLike(content_.getLike() + 1);
        }
        contentList.remove(position);
        contentList.add(position, content_);
        notifyDataSetChanged();
    }

    private void showResponses(int status, View view) {
        String message;
        if (status == 1) {
            message = activity.getApplicationContext().getResources().getString(R.string.successful);
        } else {
            message = activity.getApplicationContext().getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, view, activity.getApplicationContext());
    }

    //calculate thumbnail image size
    private int[] getThumbnailSize() {
        int width = (int) activity.getApplicationContext().getResources().getDimension(R.dimen.thumbnail_size);
        int height = (int) activity.getApplicationContext().getResources().getDimension(R.dimen.thumbnail_size);
        int[] dimension = new int[2];
        dimension[0] = width;
        dimension[1] = height;
        return dimension;
    }

    // get content based icon to display over thumbnail
    private void applyIcon(ViewHolder holder, int position) {
        switch (SelfCareContentType_.nameToType(contentList.get(position).getType())) {
            case 1:
                holder.imageViewSelfCareContentListItemTypeIcon.setImageResource(R.drawable.vi_images_white);
                break;
            case 2:
                holder.imageViewSelfCareContentListItemTypeIcon.setImageResource(R.drawable.vi_video_white);
                break;
            case 3:
                holder.imageViewSelfCareContentListItemTypeIcon.setImageResource(R.drawable.vi_care_image_white);
                break;
            case 4:
                holder.imageViewSelfCareContentListItemTypeIcon.setImageResource(R.drawable.vi_care_image_white);
                break;
            case 5:
                holder.imageViewSelfCareContentListItemTypeIcon.setImageResource(R.drawable.vi_audio_white);
                break;
            case 6:
                holder.imageViewSelfCareContentListItemTypeIcon.setImageResource(R.drawable.vi_video_white);
                break;
            case 8:
                holder.imageViewSelfCareContentListItemTypeIcon.setImageResource(R.drawable.vi_care_video_white);
                break;
            default:
                holder.imageViewSelfCareContentListItemTypeIcon.setVisibility(View.GONE);
                break;
        }
    }

    // apply my like status to respective icon and text
    private void applyLike(ViewHolder holder, int position) {
        if (contentList.get(position).getIs_like() > 0) {
            holder.textViewSelfCareContentListItemLikeCount.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.colorPrimary));
            holder.imageViewSelfCareContentListItemLike.setImageResource(R.drawable.vi_like_blue);
        } else {
            holder.textViewSelfCareContentListItemLikeCount.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.text_color_primary));
            holder.imageViewSelfCareContentListItemLike.setImageResource(R.drawable.vi_like_gray);
        }
    }

    // apply my comment status to respective icon and text
    private void applyComment(ViewHolder holder, int position) {
        if (contentList.get(position).getComments() > 0) {
            holder.textViewSelfCareContentListItemCommentCount.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.colorPrimary));
            holder.imageViewSelfCareContentListItemComment.setImageResource(R.drawable.vi_comment_blue);
        } else {
            holder.textViewSelfCareContentListItemCommentCount.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.text_color_primary));
            holder.imageViewSelfCareContentListItemComment.setImageResource(R.drawable.vi_comment_gray);
        }
    }

    // apply my comment status to respective icon and text
    private void applyShare(ViewHolder holder, int position) {
        holder.imageViewSelfCareContentListItemShare.setImageResource(R.drawable.vi_share_gray);
    }

    private boolean toggleThumbnail(String type, ViewHolder holder) {
        boolean isVisible;
        int content_type = SelfCareContentType_.nameToType(type);
        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
            isVisible = false;
            holder.mLinearLayoutShare.setVisibility(View.GONE);

        } else if (content_type == 1 || content_type == 2 || content_type == 3 || content_type == 4 || content_type == 5 || content_type == 6 || content_type == 8) {
            isVisible = true;
            holder.relativeLayoutSelfCareContentListItemIcon.setVisibility(View.VISIBLE);
        } else {
            isVisible = false;
            holder.relativeLayoutSelfCareContentListItemIcon.setVisibility(View.GONE);
        }
        return isVisible;
    }

    interface SelfCareContentListAdapterListener {
        void onItemClicked(int position, String itemView);
    }

    /**
     * @Author mahesh
     * deleted because of no use
     */
//    private void applyClickEvents(ViewHolder holder, final int position) {
//        WeakReference<SelfCareContentListAdapterListener> mListener = new WeakReference<>(listener);
//        holder.imageViewSelfCareContentListItemShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mListener.get().onItemClicked(position, "mainContainer");
//            }
//        });
//    }

    private class ViewHolder {
        TextView textViewSelfCareContentListItemTitle, textViewSelfCareContentListItemDescription, textViewSelfCareContentListItemCategory, textViewSelfCareContentListItemLikeCount, textViewSelfCareContentListItemCommentCount, textViewSelfCareContentListItemShareCount;
        ImageView imageViewSelfCareContentListItemIcon;
        AppCompatImageButton imageViewSelfCareContentListItemLike;
        ImageButton imageViewSelfCareContentListItemShare;
        AppCompatImageView imageViewSelfCareContentListItemTypeIcon, imageViewSelfCareContentListItemComment;
        RelativeLayout relativeLayoutCaseloadDetails, relativeLayoutSelfCareContentListItemIcon;
        LinearLayout mLinearLayoutShare;
    }
}
