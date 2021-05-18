package com.modules.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.NotificationTypeDetector;
import com.sagesurfer.models.HomeRecentUpdates_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Kailash.
 */

public class HomeRecentUpdateAdapter extends RecyclerView.Adapter<HomeRecentUpdateAdapter.ViewHolder> {
    private final ArrayList<HomeRecentUpdates_> recentUpdatesList;
    private String openMainFont = "<font color=\"#0D79C2\">";
    private final Activity activity;
    public final HomeRecentUpdateAdapterListener homeRecentUpdateAdapterListener;

    public HomeRecentUpdateAdapter(Activity activity, ArrayList<HomeRecentUpdates_> recentUpdatesList, HomeRecentUpdateAdapterListener homeRecentUpdateAdapterListener) {
        this.recentUpdatesList = recentUpdatesList;
        this.activity = activity;
        this.homeRecentUpdateAdapterListener = homeRecentUpdateAdapterListener;
    }

    @Override
    public long getItemId(int position) {
        return recentUpdatesList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return recentUpdatesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, titleText, timeStamp, groupName;
        RelativeLayout linearLayoutRecentUpdates;
        ImageView mImageViewRecentUpdate;
        View vLine;

        ViewHolder(View view) {
            super(view);
            nameText = (TextView) view.findViewById(R.id.textview_name);
            titleText = (TextView) view.findViewById(R.id.textview_title);
            timeStamp = (TextView) view.findViewById(R.id.timestamp);
            groupName = (TextView) view.findViewById(R.id.textview_groupname);
            vLine = view.findViewById(R.id.v_line);
            mImageViewRecentUpdate = view.findViewById(R.id.image_view);
            linearLayoutRecentUpdates = view.findViewById(R.id.linearlayout_recentupdates);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage023)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage026))
                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage025))
                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage027))) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_student_recent_update_item, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recent_update_item, parent, false);
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        HomeRecentUpdates_ recentUpdates = recentUpdatesList.get(position);

        setText(recentUpdates, viewHolder);

        viewHolder.linearLayoutRecentUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeRecentUpdateAdapterListener.onItemClicked(recentUpdatesList.get(position));
            }
        });
    }

    private void setText(HomeRecentUpdates_ recentUpdates, ViewHolder holder) {
        String closeFont = "</font>";
        //String closeBold = "</b>";
        int color = Color.parseColor("#333333"); //The color u want

        String message;
        switch (NotificationTypeDetector.getType(recentUpdates.getType())) {
            case 1://messageboard
                message = recentUpdates.getAdded_by() + " added new message in " + recentUpdates.getGroup_name();
                /*if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage023)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage026))
                        || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage025)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage027))) {
                    holder.mImageViewRecentUpdate.setImageDrawable(activity.getResources().getDrawable(R.drawable.recent_wh_home_banner_messageboard_home));
                } else {
                    holder.linearLayoutRecentUpdates.setBackground(activity.getResources().getDrawable(R.drawable.recent_update_messageboard));
                }*/
                holder.nameText.setText(recentUpdates.getAdded_by());
                holder.titleText.setText(Html.fromHtml(message));
                holder.timeStamp.setText(getDate(recentUpdates.getTimestamp()));
                holder.groupName.setText(recentUpdates.getModule());
                break;
            case 2:
                message = recentUpdates.getAdded_by() + " posted announcement in " + recentUpdates.getGroup_name();
                /*if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage023)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage026))
                        || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage025)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage027))) {
                    holder.mImageViewRecentUpdate.setImageDrawable(activity.getResources().getDrawable(R.drawable.recent_wh_home_banner_announcement_home));
                } else {
                    holder.linearLayoutRecentUpdates.setBackground(activity.getResources().getDrawable(R.drawable.recent_update_announcement));
                }*/
                holder.nameText.setText(recentUpdates.getAdded_by());
                holder.titleText.setText(Html.fromHtml(message));
                holder.timeStamp.setText(getDate(recentUpdates.getTimestamp()));
                holder.groupName.setText(recentUpdates.getModule());
                break;
            case 3:
                message = recentUpdates.getAdded_by() + " uploaded file in " + recentUpdates.getGroup_name();
                /*if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage023)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage026))
                        || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage025)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage027))) {
                    holder.mImageViewRecentUpdate.setImageDrawable(activity.getResources().getDrawable(R.drawable.recent_wh_home_banner_uploadfile_home));
                } else {
                    holder.linearLayoutRecentUpdates.setBackground(activity.getResources().getDrawable(R.drawable.recent_update_fms));
                }*/
                holder.nameText.setText(recentUpdates.getAdded_by());
                holder.titleText.setText(Html.fromHtml(message));
                holder.timeStamp.setText(getDate(recentUpdates.getTimestamp()));
                holder.groupName.setText(recentUpdates.getModule());
                break;
            case 7:
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage015")) {
                    message = recentUpdates.getAdded_by() + " posted team discussion for " + recentUpdates.getGroup_name();
                } else {
                    message = recentUpdates.getAdded_by() + " posted team talk for " + recentUpdates.getGroup_name();
                }
                /*if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage023)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage026))
                        || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage025)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage027))) {
                    holder.mImageViewRecentUpdate.setImageDrawable(activity.getResources().getDrawable(R.drawable.recent_wh_home_banner_teamtalk_home));
                } else {
                    holder.linearLayoutRecentUpdates.setBackground(activity.getResources().getDrawable(R.drawable.recent_update_team_talk));
                }*/
                holder.nameText.setText(recentUpdates.getAdded_by());
                holder.titleText.setText(Html.fromHtml(message));
                holder.timeStamp.setText(getDate(recentUpdates.getTimestamp()));
                holder.groupName.setText(recentUpdates.getModule());
                break;
            case 9:
                message = recentUpdates.getAdded_by() + " added poll in " + recentUpdates.getGroup_name();
               /* if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage023)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage026))
                        || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage025)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage027))) {
                    holder.mImageViewRecentUpdate.setImageDrawable(activity.getResources().getDrawable(R.drawable.recent_wh_home_banner_poll_home));
                } else {
                    holder.linearLayoutRecentUpdates.setBackground(activity.getResources().getDrawable(R.drawable.recent_update_poll));
                }*/
                holder.nameText.setText(recentUpdates.getAdded_by());
                holder.titleText.setText(Html.fromHtml(message));
                holder.timeStamp.setText(getDate(recentUpdates.getTimestamp()));
                holder.groupName.setText(recentUpdates.getModule());
                break;
            case 12: //tasklist
                if (recentUpdates.getGroup_name() == null || recentUpdates.getGroup_name().length() == 0) {
                    message = "added new Task ";
                } else {
                    message = recentUpdates.getAdded_by() + " added Task in " + recentUpdates.getGroup_name();
                }
/*                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage023)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage026))
                        || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage025)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage027))) {
                    holder.mImageViewRecentUpdate.setImageDrawable(activity.getResources().getDrawable(R.drawable.recent_wh_home_banner_teamtask_home));
                } else {
                    holder.linearLayoutRecentUpdates.setBackground(activity.getResources().getDrawable(R.drawable.recent_update_team_task));
                }*/
                holder.nameText.setText(recentUpdates.getAdded_by());
                holder.titleText.setText(Html.fromHtml(message));
                holder.timeStamp.setText(getDate(recentUpdates.getTimestamp()));
                holder.groupName.setText(recentUpdates.getModule());
                break;
            case 17://upload_selfcare
                message = "added new content in your Self care ";
                /*if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage023)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage026))
                        || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage025)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage027))) {
                    holder.mImageViewRecentUpdate.setImageDrawable(activity.getResources().getDrawable(R.drawable.recent_wh_home_banner_selfcare_home));
                } else {
                    holder.linearLayoutRecentUpdates.setBackground(activity.getResources().getDrawable(R.drawable.recent_update_selfcare));
                }*/
                holder.nameText.setText(recentUpdates.getAdded_by());
                holder.titleText.setText(Html.fromHtml(message));
                holder.timeStamp.setText(getDate(recentUpdates.getTimestamp()));
                holder.groupName.setText(recentUpdates.getModule());
                break;
            case 18://comment_selfcare
                message = "added new comment in your Self care ";
                /*if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage023)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage026))
                        || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage025)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage027))) {
                    holder.mImageViewRecentUpdate.setImageDrawable(activity.getResources().getDrawable(R.drawable.recent_wh_home_banner_selfcare_home));
                } else {
                    holder.linearLayoutRecentUpdates.setBackground(activity.getResources().getDrawable(R.drawable.recent_update_selfcare));
                }*/
                holder.nameText.setText(recentUpdates.getAdded_by());
                holder.titleText.setText(Html.fromHtml(message));
                holder.timeStamp.setText(getDate(recentUpdates.getTimestamp()));
                holder.groupName.setText(recentUpdates.getModule());
            case 19://decline_selfcare
                message = "declined content in your Self care ";
               /* if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage023)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage026))
                        || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage025)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage027))) {
                    holder.mImageViewRecentUpdate.setImageDrawable(activity.getResources().getDrawable(R.drawable.recent_wh_home_banner_selfcare_home));
                } else {
                    holder.linearLayoutRecentUpdates.setBackground(activity.getResources().getDrawable(R.drawable.recent_update_selfcare));
                }*/
                holder.nameText.setText(recentUpdates.getAdded_by());
                holder.titleText.setText(Html.fromHtml(message));
                holder.timeStamp.setText(getDate(recentUpdates.getTimestamp()));
                holder.groupName.setText(recentUpdates.getModule());
            case 20://approve_selfcare
                message = "approved content in your Self care ";
                /*if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage023)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage026))
                        || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage025)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage027))) {
                    holder.mImageViewRecentUpdate.setImageDrawable(activity.getResources().getDrawable(R.drawable.recent_wh_home_banner_selfcare_home));
                } else {
                    holder.linearLayoutRecentUpdates.setBackground(activity.getResources().getDrawable(R.drawable.recent_update_selfcare));
                }*/
                holder.nameText.setText(recentUpdates.getAdded_by());
                holder.titleText.setText(Html.fromHtml(message));
                holder.timeStamp.setText(getDate(recentUpdates.getTimestamp()));
                holder.groupName.setText(recentUpdates.getModule());
                break;
            case 21://event or calendar
                if (recentUpdates.getGroup_name() == null || recentUpdates.getGroup_name().length() == 0) {
                    message = "added new Event";
                } else {
                    message = recentUpdates.getAdded_by() + " Added new Event in " + recentUpdates.getGroup_name();
                }
                /*if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage023)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage026))
                        || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage025)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage027))) {
                    holder.mImageViewRecentUpdate.setImageDrawable(activity.getResources().getDrawable(R.drawable.recent_wh_home_banner_event_home));
                } else {
                    holder.linearLayoutRecentUpdates.setBackground(activity.getResources().getDrawable(R.drawable.recent_update_team_event));
                }*/
                holder.nameText.setText(recentUpdates.getAdded_by());
                holder.titleText.setText(Html.fromHtml(message));
                holder.timeStamp.setText(getDate(recentUpdates.getTimestamp()));
                holder.groupName.setText(recentUpdates.getModule());
                break;
            /*case 24://mood
                message = openMainFont + (recentUpdates.getTitle()) + closeFont;
                holder.titleText.setText(Html.fromHtml(message));
                break;*/
            case 25://accept event or calendar
                if (recentUpdates.getGroup_name() == null || recentUpdates.getGroup_name().length() == 0) {
                    message = "accepted an event ";
                } else {
                    message = recentUpdates.getAdded_by() + " accepted an event in " + recentUpdates.getGroup_name();
                }
               /* if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage023)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage026))
                        || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage025)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage027))) {
                    holder.mImageViewRecentUpdate.setImageDrawable(activity.getResources().getDrawable(R.drawable.recent_wh_home_banner_event_home));
                } else {
                    holder.linearLayoutRecentUpdates.setBackground(activity.getResources().getDrawable(R.drawable.recent_update_team_event));
                }*/
                holder.nameText.setText(recentUpdates.getAdded_by());
                holder.titleText.setText(Html.fromHtml(message));
                holder.timeStamp.setText(getDate(recentUpdates.getTimestamp()));
                holder.groupName.setText(recentUpdates.getModule());
                break;
            case 26://decline event or calendar
                if (recentUpdates.getGroup_name() == null || recentUpdates.getGroup_name().length() == 0) {
                    message = "rejected an event ";
                } else {
                    message = recentUpdates.getAdded_by() + " rejected an event in " + recentUpdates.getGroup_name();
                }
                /*if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage023)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage026))
                        || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage025)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage027))) {
                    holder.mImageViewRecentUpdate.setImageDrawable(activity.getResources().getDrawable(R.drawable.recent_update_team_event_home));
                } else {
                    holder.linearLayoutRecentUpdates.setBackground(activity.getResources().getDrawable(R.drawable.recent_update_team_event));
                }*/
                holder.nameText.setText(recentUpdates.getAdded_by());
                holder.titleText.setText(Html.fromHtml(message));
                holder.timeStamp.setText(getDate(recentUpdates.getTimestamp()));
                holder.groupName.setText(recentUpdates.getModule());
                break;
        }
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy | hh:mm a", cal).toString();
        return date;
    }

    public interface HomeRecentUpdateAdapterListener {
        void onItemClicked(HomeRecentUpdates_ recentUpdates);
    }
}