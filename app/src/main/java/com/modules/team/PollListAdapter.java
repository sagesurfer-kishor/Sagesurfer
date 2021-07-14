package com.modules.team;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.modules.postcard.PostcardListAdapter;
import com.modules.wall.CommentDialog;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.views.CircleTransform;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 08-08-2017
 *         Last Modified on 14-12-2017
 */

public class PollListAdapter extends RecyclerView.Adapter<PollListAdapter.MyViewHolder> {

    private final Activity activity;
    private final Context mContext;

    private final List<Poll_> pollList;

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView name;
        final TextView time;
        final TextView expiry;
        final TextView deleteVote;
        final TextView likeCount;
        final TextView commentCount;
        final ImageView photo;
        final AppCompatImageView likeIcon;
        final AppCompatImageView commentIcon;
        final LinearLayout optionLayout;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.poll_list_item_title);
            name = (TextView) view.findViewById(R.id.poll_list_item_name);
            time = (TextView) view.findViewById(R.id.poll_list_item_date);
            expiry = (TextView) view.findViewById(R.id.poll_list_item_expiry);
            deleteVote = (TextView) view.findViewById(R.id.poll_list_item_vote_delete);
            likeCount = (TextView) view.findViewById(R.id.poll_list_item_like_count);
            commentCount = (TextView) view.findViewById(R.id.poll_list_item_comment_count);

            likeIcon = (AppCompatImageView) view.findViewById(R.id.poll_list_item_like_icon);
            commentIcon = (AppCompatImageView) view.findViewById(R.id.poll_list_item_comment_icon);

            photo = (ImageView) view.findViewById(R.id.poll_list_item_photo);

            optionLayout = (LinearLayout) view.findViewById(R.id.poll_list_item_option_list);
        }
    }


    public PollListAdapter(Activity activity, List<Poll_> pollList) {
        this.activity = activity;
        this.pollList = pollList;
        mContext = this.activity.getApplicationContext();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.poll_list_item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Poll_ poll_ = pollList.get(position);

        holder.name.setText(poll_.getPosted_by());
        holder.title.setText(poll_.getPoll_question());
        holder.time.setText(GetTime.wallTime(poll_.getPosted_on()));
        String expiryDate = "Expiry Date:" + GetTime.getDate(poll_.getExpires_on());
        holder.expiry.setText(expiryDate);

        //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
        Glide.with(activity.getApplicationContext())
                .load(poll_.getPhoto())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(poll_.getPhoto()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new CircleTransform(activity.getApplicationContext())))
                .into(holder.photo);

        if (poll_.getPoll_answer() == 0) {
            holder.deleteVote.setVisibility(View.GONE);
        } else {
            holder.deleteVote.setVisibility(View.VISIBLE);
        }

        holder.likeCount.setText(GetCounters.convertCounter(poll_.getTotal_likes()));
        holder.commentCount.setText(GetCounters.convertCounter(poll_.getTotal_comments()));

        if (poll_.getIs_like() == 1) {
            holder.likeIcon.setImageResource(R.drawable.vi_like_blue);
            holder.likeCount.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.likeIcon.setImageResource(R.drawable.vi_like_grey);
            holder.likeCount.setTextColor(mContext.getResources().getColor(R.color.text_color_primary));
        }

        if (poll_.getTotal_comments() > 0) {
            holder.commentIcon.setImageResource(R.drawable.vi_comment_blue);
            holder.commentCount.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.commentIcon.setImageResource(R.drawable.vi_comment_gray);
            holder.commentCount.setTextColor(mContext.getResources().getColor(R.color.text_color_primary));
        }

        holder.deleteVote.setTag(position);
        holder.deleteVote.setOnClickListener(onClick);

        holder.likeIcon.setTag(position);
        holder.likeIcon.setOnClickListener(onClick);

        holder.commentIcon.setTag(position);
        holder.commentIcon.setOnClickListener(onClick);

        setList(holder, position);
    }

    @Override
    public int getItemCount() {
        return pollList.size();
    }

    private final View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            switch (v.getId()) {
                case R.id.poll_list_item_vote_delete:
                    Poll_ poll_ = PollOperations.deleteVote(pollList.get(position).getId(),
                            PollListAdapter.class.getSimpleName(), activity.getApplicationContext(), activity);
                    if (poll_.getStatus() == 1) {
                        ShowSnack.viewSuccess(v, mContext.getResources()
                                .getString(R.string.successful), activity.getApplicationContext());
                        pollList.remove(position);
                        pollList.add(position, poll_);
                        notifyDataSetChanged();
                    }
                    break;
                case R.id.poll_list_item_like_icon:
                    int[] result = PollOperations.likeUnlike(pollList.get(position).getId(),
                            pollList.get(position).getIs_like(),
                            PollListAdapter.class.getSimpleName(), activity.getApplicationContext(), activity);
                    toggleLike(result, position);
                    break;
                case R.id.poll_list_item_comment_icon:
                    openComment(pollList.get(position).getId());
                    break;
            }
        }
    };

    //change like icon/text color and counter based on user action
    private void toggleLike(int[] result, int position) {
        if (result[0] == 1) {
            pollList.get(position).setIs_like(result[2]);
            pollList.get(position).setTotal_likes(result[1]);
            notifyDataSetChanged();
        }
    }

    // open comment dialog fragment
    @SuppressLint("CommitTransaction")
    private void openComment(long poll_id) {
        DialogFragment dialogFrag = new CommentDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(General.ID, (int) poll_id);
        bundle.putString(General.FROM, "Poll");
        dialogFrag.setArguments(bundle);
        dialogFrag.show(activity.getFragmentManager().beginTransaction(), General.COMMENT_COUNT);
    }

    // add poll options view runtime
    @SuppressLint("InflateParams")
    private void setList(MyViewHolder holder, final int position) {
        holder.optionLayout.removeAllViews();
        int total_count = pollList.get(position).getTotal_count();

        for (int i = 0; i < pollList.get(position).getPollOptions().size(); i++) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            final View view = layoutInflater.inflate(R.layout.poll_vote_option_item_layout, null);

            final Options_ options_ = pollList.get(position).getPollOptions().get(i);

            TextView optionName = (TextView) view.findViewById(R.id.poll_vote_option_item_text);
            TextView countText = (TextView) view.findViewById(R.id.poll_vote_option_item_count);
            optionName.setText(options_.getTitle());
            String percentage = PollOperations.getPercent(total_count, options_.getCount()) + "%" + "";
            countText.setText(percentage);
            if (options_.getCount() > 0) {
                countText.setTextColor(PollOperations.getVoteColor(i, activity.getApplicationContext()));
            } else {
                countText.setTextColor(mContext.getResources().getColor(R.color.text_color_tertiary));
            }
            ProgressBar pb = (ProgressBar) view.findViewById(R.id.poll_vote_option_item_pb);
            pb.setProgress(PollOperations.getPercent(total_count, options_.getCount()));
            pb.setProgressDrawable(PollOperations.getProgressDrawable(i, activity.getApplicationContext()));

            view.setTag(position);
            holder.optionLayout.addView(view);

            RadioButton radioButton = (RadioButton) view.findViewById(R.id.poll_vote_option_item_radio);
            radioButton.setTag(i);
            if (options_.getIs_selected() == 1) {
                radioButton.setChecked(true);
            } else {
                radioButton.setChecked(false);
            }
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int poll_position = (Integer) view.getTag();
                    int position_vote = (Integer) v.getTag();
                    if (pollList.get(poll_position).getPollOptions().get(position_vote).getIs_selected() == 0) {
                        Poll_ poll_ = PollOperations.vote(pollList.get(poll_position).getId(),
                                pollList.get(poll_position).getPollOptions().get(position_vote).getId(),
                                PostcardListAdapter.class.getCanonicalName(), activity.getApplicationContext(), activity);
                        if (poll_.getStatus() == 1) {
                            ShowSnack.viewSuccess(v, mContext.getResources()
                                    .getString(R.string.successful), activity.getApplicationContext());
                            pollList.remove(poll_position);
                            pollList.add(poll_position, poll_);
                            notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }

}