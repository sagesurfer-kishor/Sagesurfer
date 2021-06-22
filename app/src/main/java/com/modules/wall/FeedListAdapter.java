package com.modules.wall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.CheckFileType;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.tasks.PerformWallTask;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 11-07-2017
 * Last Modified on 15-12-2017
 */

class FeedListAdapter extends ArrayAdapter<Feed_> {
    private final List<Feed_> feedList;
    private final Context mContext;
    private final Activity activity;

    FeedListAdapter(Activity activity, List<Feed_> feedList) {
        super(activity, 0, feedList);
        this.feedList = feedList;
        this.mContext = activity.getApplicationContext();
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return feedList.size();
    }

    @Override
    public Feed_ getItem(int position) {
        if (feedList != null && feedList.size() > 0) {
            return feedList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return feedList.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.feed_list_item_layout, parent, false);

            viewHolder.feedText = (TextView) view.findViewById(R.id.feed_list_item_feed);
            viewHolder.nameText = (TextView) view.findViewById(R.id.feed_list_item_name);
            viewHolder.dateText = (TextView) view.findViewById(R.id.feed_list_item_date);
            viewHolder.likeCountText = (TextView) view.findViewById(R.id.feed_list_item_like_count);
            viewHolder.commentCountText = (TextView) view.findViewById(R.id.feed_list_item_comment_count);
            viewHolder.shareCountText = (TextView) view.findViewById(R.id.feed_list_item_share_count);
            viewHolder.attachmentCount = (TextView) view.findViewById(R.id.feed_list_item_attachment_count);

            viewHolder.attachmentLayout = (LinearLayout) view.findViewById(R.id.feed_list_item_attachment_layout);

            viewHolder.profile = (ImageView) view.findViewById(R.id.feed_list_item_image);
            viewHolder.attachmentOne = (AppCompatImageView) view.findViewById(R.id.feed_list_item_attachment_one);
            viewHolder.attachmentTwo = (AppCompatImageView) view.findViewById(R.id.feed_list_item_attachment_two);
            viewHolder.attachmentThree = (AppCompatImageView) view.findViewById(R.id.feed_list_item_attachment_three);
            viewHolder.commentButton = (AppCompatImageView) view.findViewById(R.id.feed_list_item_comment);
            viewHolder.likeButton = (AppCompatImageView) view.findViewById(R.id.feed_list_item_like);
            viewHolder.shareButton = (AppCompatImageView) view.findViewById(R.id.feed_list_item_share);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.likeButton.setTag(position);
        viewHolder.commentButton.setTag(position);
        viewHolder.shareButton.setTag(position);
        viewHolder.attachmentLayout.setTag(position);

        if (feedList.get(position).getStatus() == 1) {
            viewHolder.feedText.setText(feedList.get(position).getFeed());
            viewHolder.nameText.setText(ChangeCase.toTitleCase(feedList.get(position).getName()));
            String time = getDate(feedList.get(position).getDate());
            //viewHolder.dateText.setText(GetTime.wallTime());
            viewHolder.dateText.setText(time);
            viewHolder.likeCountText.setText(GetCounters.convertCounter(feedList.get(position).getLikeCount()));
            viewHolder.commentCountText.setText(GetCounters.convertCounter(feedList.get(position).getCommentCount()));
            viewHolder.shareCountText.setText(GetCounters.convertCounter(feedList.get(position).getShare_count()));

            if (feedList.get(position).getTotalUpload() > 0) {
                viewHolder.attachmentLayout.setVisibility(View.VISIBLE);
                setAttachment(viewHolder, position);
            } else {
                viewHolder.attachmentLayout.setVisibility(View.GONE);
            }

            //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
            Glide.with(mContext)
                    .load(feedList.get(position).getProfilePhoto())
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .centerCrop()
                            .placeholder(GetThumbnails.userIcon(feedList.get(position).getProfilePhoto()))
                            .transform(new CircleTransform(mContext)))
                    .into(viewHolder.profile);

            if (feedList.get(position).getIsLike() == 1) {
                viewHolder.likeButton.setImageResource(R.drawable.vi_like_blue);
                viewHolder.likeCountText.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            } else {
                viewHolder.likeButton.setImageResource(R.drawable.vi_like_grey);
                viewHolder.likeCountText.setTextColor(mContext.getResources().getColor(R.color.text_color_primary));
            }
            if (feedList.get(position).getCommentCount() > 0) {
                viewHolder.commentButton.setImageResource(R.drawable.vi_comment_blue);
                viewHolder.commentCountText.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            } else {
                viewHolder.commentButton.setImageResource(R.drawable.vi_comment_gray);
                viewHolder.commentCountText.setTextColor(mContext.getResources().getColor(R.color.text_color_primary));
            }

            int userID = Integer.parseInt(Preferences.get(General.USER_ID));

            if (feedList.get(position).getUser_id() == userID) {

                if (feedList.get(position).getShare_count() > 0) {
                    viewHolder.shareButton.setImageResource(R.drawable.share);
                    viewHolder.shareCountText.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                } else {
                    viewHolder.shareButton.setImageResource(R.drawable.share_gray);
                    viewHolder.shareButton.setVisibility(View.GONE);
                    viewHolder.shareCountText.setTextColor(mContext.getResources().getColor(R.color.text_color_primary));
                    viewHolder.shareCountText.setVisibility(View.GONE);
                }
            } else {
                if (feedList.get(position).getShare_count() > 0) {
                    viewHolder.shareButton.setImageResource(R.drawable.share);
                    viewHolder.shareButton.setVisibility(View.GONE);
                    viewHolder.shareCountText.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                    viewHolder.shareCountText.setVisibility(View.GONE);
                } else {
                    viewHolder.shareButton.setImageResource(R.drawable.share_gray);
                    viewHolder.shareButton.setVisibility(View.GONE);
                    viewHolder.shareCountText.setTextColor(mContext.getResources().getColor(R.color.text_color_primary));
                    viewHolder.shareCountText.setVisibility(View.GONE);
                }
            }
        }

        viewHolder.likeButton.setOnClickListener(onClick);
        viewHolder.commentButton.setOnClickListener(onClick);
        viewHolder.shareButton.setOnClickListener(onClick);
        viewHolder.attachmentLayout.setOnClickListener(onClick);
        return view;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy | hh:mm a", cal).toString();
        return date;
    }

    private void toggleLike(int position, int[] status) {
        if (feedList.get(position).getIsLike() == 1) {
            feedList.get(position).setIsLike(0);
            feedList.get(position).setLikeCount(status[1]);
        } else {
            feedList.get(position).setIsLike(1);
            feedList.get(position).setLikeCount(status[1]);
        }
    }

    private final View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            switch (v.getId()) {
                case R.id.feed_list_item_attachment_layout:
                    Intent attachmentIntent = new Intent(mContext, AttachmentListActivity.class);
                    attachmentIntent.putExtra(General.ATTACHMENTS, feedList.get(position).getAttachmentList());
                    activity.startActivity(attachmentIntent);
                    break;
                case R.id.feed_list_item_comment:
                    openComment(position);
                    break;
                case R.id.feed_list_item_like:
                    int status[] = PerformWallTask.like(feedList.get(position).getId(), mContext, activity);
                    showResponses(status, v, position);
                    break;

                case R.id.feed_list_item_share:
                    int userID = Integer.parseInt(Preferences.get(General.USER_ID));
                    if (feedList.get(position).getUser_id() == userID) {
                        Intent friendTeamIntent = new Intent(mContext, FriendsTeamActivity.class);
                        friendTeamIntent.putExtra("wall_id", feedList.get(position).getId());
                        activity.startActivity(friendTeamIntent);
                    }
                    break;
            }
        }
    };

    private void showResponses(int[] status, View view, int position) {
        String message;
        if (status[0] == 1) {
            message = mContext.getResources().getString(R.string.successful);
        } else {
            status[0] = 2;
            message = mContext.getResources().getString(R.string.action_failed);
        }
        toggleLike(position, status);
        notifyDataSetChanged();
        SubmitSnackResponse.showSnack(status[0], message, view, mContext);
    }

    // show list of files uploaded with wall post
    private void setAttachment(ViewHolder viewHolder, int position) {
        AppCompatImageView[] imageViews = {viewHolder.attachmentOne, viewHolder.attachmentTwo, viewHolder.attachmentThree};
        for (int i = 0; i < feedList.get(position).getAttachmentList().size(); i++) {
            if (i < imageViews.length) {
                setAttachmentImage(feedList.get(position).getAttachmentList().get(i).getPath(), imageViews[i]);
            }
        }
        if (feedList.get(position).getTotalUpload() > 3) {
            viewHolder.attachmentCount.setVisibility(View.VISIBLE);
            String countString = "+" + (feedList.get(position).getTotalUpload() - 3);
            viewHolder.attachmentCount.setText(countString);
        }
    }

    // set image thumbnail/ file icon to image view
    private void setAttachmentImage(String path, ImageView attachmentImage) {
        if (CheckFileType.imageFile(path)) {
            Glide.with(mContext)
                    .load(path)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_image)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)) //.RESULT
                    .into(attachmentImage);
        } else {
            attachmentImage.setImageResource(GetThumbnails.attachmentList(path));
        }
    }

    // open comment dialog fragment for respective wall post
    @SuppressLint("CommitTransaction")
    private void openComment(int position) {
        DialogFragment dialogFrag = new CommentDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(General.ID, feedList.get(position).getId());
        dialogFrag.setArguments(bundle);
        dialogFrag.show(activity.getFragmentManager().beginTransaction(), General.COMMENT_COUNT);
    }

    private class ViewHolder {
        TextView feedText, nameText, dateText, likeCountText, commentCountText, shareCountText, attachmentCount;
        ImageView profile;
        AppCompatImageView commentButton, likeButton, shareButton, attachmentOne, attachmentTwo, attachmentThree;
        LinearLayout attachmentLayout;
    }
}
