package com.modules.teamtalk.adapter;

import android.app.Activity;
import android.os.Handler;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.modules.teamtalk.model.ChildComments;
import com.modules.teamtalk.model.Comments_;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Kailash Karankal on 8/9/2019.
 */
public class ChildTeamTalkCommentsAdaper extends RecyclerView.Adapter<ChildTeamTalkCommentsAdaper.MyViewHolder> {
    private static final String TAG = ChildTeamTalkCommentsAdaperListener.class.getSimpleName();
    private final Activity activity;
    private final List<ChildComments> commentsList;
    private List<Comments_> replyCommentsList = new ArrayList<>();
    private ChildTeamTalkCommentsAdaperListener childTeamTalkCommentsAdaper;
    private LinearLayoutManager mLinearLayoutManager;
    private int currentParentID;
    private String msg;

    public ChildTeamTalkCommentsAdaper(Activity activity, List<ChildComments> commentsList, int parentCommentID, ChildTeamTalkCommentsAdaperListener childTeamTalkCommentsAdaper) {
        this.activity = activity;
        this.commentsList = commentsList;
        this.currentParentID = parentCommentID;
        this.childTeamTalkCommentsAdaper = childTeamTalkCommentsAdaper;
    }

    @Override
    public ChildTeamTalkCommentsAdaper.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reply_team_talk_comment_item, parent, false);

        return new ChildTeamTalkCommentsAdaper.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ChildTeamTalkCommentsAdaper.MyViewHolder holder, final int position) {
        final ChildComments childComments = commentsList.get(position);
        holder.message.setText(childComments.getMessage());
        holder.name.setText(childComments.getFull_name());
        holder.time.setText(GetTime.wallTime(childComments.getPosted_on()));
        holder.repliesComment.setVisibility(View.GONE);
        holder.mainReply.setVisibility(View.GONE);

        Glide.with(activity.getApplicationContext()).load(childComments.getImage())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(childComments.getImage()))
                        .transform(new CircleTransform(activity.getApplicationContext())))
                .into(holder.icon);

        if (Preferences.get(General.USER_ID).equalsIgnoreCase(Preferences.get(General.GROUP_OWNER_ID))) {
            holder.deleteComment.setVisibility(View.VISIBLE);
        } else if (childComments.getUser_id() == Long.parseLong(Preferences.get(General.USER_ID))) {
            holder.deleteComment.setVisibility(View.VISIBLE);
        } else {
            holder.deleteComment.setVisibility(View.GONE);
        }

        // for tarzana module
        if (CheckRole.showInviteMember(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            holder.repliesComment.setVisibility(View.GONE);
            holder.replyComment.setVisibility(View.GONE);
            holder.deleteComment.setVisibility(View.GONE);
            holder.mainReply.setVisibility(View.GONE);
        }

        if (childComments.getChild_cnt() == 0) {
            holder.repliesComment.setVisibility(View.GONE);
        } else if (childComments.getChild_cnt() == 1) {
            holder.repliesComment.setVisibility(View.VISIBLE);
            holder.repliesComment.setText("Reply" + "(" + childComments.getChild_cnt() + ")");
        } else {
            holder.repliesComment.setVisibility(View.VISIBLE);
            holder.repliesComment.setText("Replies" + "(" + childComments.getChild_cnt() + ")");
        }

        if (childComments.isSelectMainLayout()) {
            holder.mainReply.setVisibility(View.VISIBLE);
        } else {
            holder.mainReply.setVisibility(View.GONE);
        }


        if (childComments.isHighlight()) {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    holder.mainHintLayout.setBackground(activity.getResources().getDrawable(R.color.white));
                }
            }, 1000);

            holder.mainHintLayout.setBackground(activity.getResources().getDrawable(R.color.transparent_40));

        }

        if (General.isCurruntUserHasPermissionToTeamTalkActions() ||
                Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1") ||
                Preferences.get(General.OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID))){
            holder.replyComment.setClickable(false);
        }

        holder.replyComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                childTeamTalkCommentsAdaper.onChildItemClicked(childComments.getId(), currentParentID);
                notifyDataSetChanged();
            }
        });

        holder.deleteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                childTeamTalkCommentsAdaper.onChildDeleteItemClicked(childComments.getId(), currentParentID);
            }
        });

        holder.repliesComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                childTeamTalkCommentsAdaper.onChildReplyItemClicked(childComments.getId(), currentParentID);
            }
        });

        holder.mainReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                childTeamTalkCommentsAdaper.onChildShowMainLayoutItemClicked(childComments.getId() - 1, currentParentID);
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, time, message;
        TextView deleteComment, replyComment, repliesComment, mainReply;
        ImageView icon;
        RelativeLayout mainHintLayout;


        MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.textview_wallcommentitem_name);
            time = (TextView) view.findViewById(R.id.textview_wallcommentitem_time);
            message = (TextView) view.findViewById(R.id.textview_wallcommentitem_text);
            icon = (ImageView) view.findViewById(R.id.imageview_wallcommentitem_image);

            deleteComment = (TextView) view.findViewById(R.id.delete_comment);
            replyComment = (TextView) view.findViewById(R.id.reply_comment);
            repliesComment = (TextView) view.findViewById(R.id.replylies_comment);
            mainReply = (TextView) view.findViewById(R.id.main_layout);
            mainHintLayout = (RelativeLayout) view.findViewById(R.id.main_hint_layout);

        }
    }

    public interface ChildTeamTalkCommentsAdaperListener {
        void onChildItemClicked(int commentId, int currentParentID);

        void onChildDeleteItemClicked(int commentId, int currentParentID);

        void onChildReplyItemClicked(int commentId, int currentParentID);

        void onChildShowMainLayoutItemClicked(int commentId, int currentParentID);
    }
}