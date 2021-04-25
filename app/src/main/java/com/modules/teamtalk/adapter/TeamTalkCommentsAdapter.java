package com.modules.teamtalk.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.modules.teamtalk.activity.TalkDetailsActivity;
import com.modules.teamtalk.model.Comments_;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Kailash Karankal on 8/9/2019.
 */
public class TeamTalkCommentsAdapter extends RecyclerView.Adapter<TeamTalkCommentsAdapter.MyViewHolder> implements ChildTeamTalkCommentsAdaper.ChildTeamTalkCommentsAdaperListener {
    private final Activity activity;
    private final List<Comments_> commentsList;
    private final TeamTalkCommentsAdapterListener teamTalkCommentsAdapterListener;
    private ChildTeamTalkCommentsAdaper childTeamTalkCommentsAdaper;
    private LinearLayoutManager mLinearLayoutManager;


    public TeamTalkCommentsAdapter(Activity activity, List<Comments_> commentsList, TeamTalkCommentsAdapterListener teamTalkCommentsAdapterListener) {
        this.activity = activity;
        this.commentsList = commentsList;
        this.teamTalkCommentsAdapterListener = teamTalkCommentsAdapterListener;
    }

    @Override
    public TeamTalkCommentsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.team_talk_comment_item, parent, false);

        return new TeamTalkCommentsAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(TeamTalkCommentsAdapter.MyViewHolder holder, final int position) {
        final Comments_ comments_ = commentsList.get(position);
        holder.message.setText(comments_.getMessage());
        holder.name.setText(comments_.getName());
        holder.time.setText(GetTime.getDateTime(comments_.getDate()));

        Glide.with(activity.getApplicationContext()).load(comments_.getImage())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(comments_.getImage()))
                        .transform(new CircleTransform(activity.getApplicationContext())))
                .into(holder.icon);

       /* if ((Long.parseLong(Preferences.get(General.USER_ID)) == (Integer.parseInt(Preferences.get(General.GROUP_OWNER_ID))))) {
            holder.deleteComment.setVisibility(View.VISIBLE);
        } else if (comments_.getUser_id() == Long.parseLong(Preferences.get(General.USER_ID))) {
            holder.deleteComment.setVisibility(View.VISIBLE);
        } else {
            holder.deleteComment.setVisibility(View.GONE);
        }*/

       //change by kishor k 11-09-2020
        if (Preferences.get(General.USER_ID).equals(Preferences.get(General.GROUP_OWNER_ID))) {
            holder.deleteComment.setVisibility(View.VISIBLE);
        } else if (comments_.getUser_id() == Long.parseLong(Preferences.get(General.USER_ID))) {
            holder.deleteComment.setVisibility(View.VISIBLE);
        } else {
            holder.deleteComment.setVisibility(View.GONE);
        }


        // for tarzana module
        if (CheckRole.showInviteMember(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            holder.repliesComment.setVisibility(View.GONE);
            holder.replyComment.setVisibility(View.GONE);
            holder.deleteComment.setVisibility(View.GONE);
        }

        if (General.isCurruntUserHasPermissionToTeamTalkActions() ||
                Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1") ||
                Preferences.get(General.OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID))) {
            holder.replyComment.setClickable(false);
        }

        if (comments_.getComment_count() == 0) {
            holder.repliesComment.setVisibility(View.GONE);
        } else if (comments_.getComment_count() == 1) {
            holder.repliesComment.setVisibility(View.VISIBLE);
            holder.repliesComment.setText("Reply" + "(" + comments_.getComment_count() + ")");
        } else {
            holder.repliesComment.setVisibility(View.VISIBLE);
            holder.repliesComment.setText("Replies" + "(" + comments_.getComment_count() + ")");
        }

        if (comments_.getChildComments().size() == 0) {
            holder.replyCommentList.setVisibility(View.GONE);
        } else {
            childTeamTalkCommentsAdaper = new ChildTeamTalkCommentsAdaper(activity, comments_.getChildComments(), comments_.getId(), this);
            holder.replyCommentList.setAdapter(childTeamTalkCommentsAdaper);
        }

        holder.replyComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamTalkCommentsAdapterListener.onReplyItemClicked(comments_.getId());
                notifyDataSetChanged();
            }
        });

        holder.deleteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamTalkCommentsAdapterListener.deleteComment(comments_.getId(), 0);
            }
        });

        holder.repliesComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamTalkCommentsAdapterListener.repliesDataItemClicked(comments_.getId(), 0);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, time, message;
        TextView deleteComment, replyComment, repliesComment;
        ImageView icon;
        RecyclerView replyCommentList;


        MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.textview_wallcommentitem_name);
            time = (TextView) view.findViewById(R.id.textview_wallcommentitem_time);
            message = (TextView) view.findViewById(R.id.textview_wallcommentitem_text);
            icon = (ImageView) view.findViewById(R.id.imageview_wallcommentitem_image);

            deleteComment = (TextView) view.findViewById(R.id.delete_comment);
            replyComment = (TextView) view.findViewById(R.id.reply_comment);
            repliesComment = (TextView) view.findViewById(R.id.replylies_comment);

            replyCommentList = (RecyclerView) view.findViewById(R.id.reply_comment_recycler_view);
            mLinearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
            replyCommentList.setLayoutManager(mLinearLayoutManager);
        }
    }

    public interface TeamTalkCommentsAdapterListener {

        void onReplyItemClicked(int commentId);

        void deleteComment(int commentId, int parentID);

        void repliesDataItemClicked(int commentId, int parentID);

        void highLightReplyLayout(int commentId, int parentID);
    }

    @Override
    public void onChildItemClicked(int commentId, int currentParentID) {
        TalkDetailsActivity.childOnItemClicked(commentId, currentParentID);
    }

    @Override
    public void onChildDeleteItemClicked(int commentId, int currentParentID) {
        teamTalkCommentsAdapterListener.deleteComment(commentId, currentParentID);
    }

    @Override
    public void onChildReplyItemClicked(int commentId, int currentParentID) {
        teamTalkCommentsAdapterListener.repliesDataItemClicked(commentId, currentParentID);
    }

    @Override
    public void onChildShowMainLayoutItemClicked(int commentId, int currentParentID) {
        teamTalkCommentsAdapterListener.highLightReplyLayout(commentId, currentParentID);
    }
}