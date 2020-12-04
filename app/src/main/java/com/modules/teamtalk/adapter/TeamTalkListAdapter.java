package com.modules.teamtalk.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.modules.teamtalk.model.TeamTalk_;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetCounters;

import java.util.List;

/**
 * @author Kailash Karankal
 */

public class TeamTalkListAdapter extends ArrayAdapter<TeamTalk_> {
    private final List<TeamTalk_> talkList;
    private final Context mContext;

    public TeamTalkListAdapter(Activity activity, List<TeamTalk_> talkList) {
        super(activity, 0, talkList);
        this.talkList = talkList;
        mContext = activity.getApplicationContext();
    }

    @Override
    public int getCount() {
        return talkList.size();
    }

    @Override
    public TeamTalk_ getItem(int position) {
        if (talkList != null && talkList.size() > 0) {
            return talkList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return talkList.get(position).getId();
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
            view = layoutInflater.inflate(R.layout.team_talk_list_item_layout, parent, false);

            viewHolder.nameText = (TextView) view.findViewById(R.id.team_talk_list_item_name);
            viewHolder.dateText = (TextView) view.findViewById(R.id.team_talk_list_item_date);
            viewHolder.teamText = (TextView) view.findViewById(R.id.team_talk_list_item_team);
            viewHolder.titleText = (TextView) view.findViewById(R.id.team_talk_list_item_title);
            viewHolder.descriptionText = (TextView) view.findViewById(R.id.team_talk_list_item_description);
            viewHolder.countText = (TextView) view.findViewById(R.id.team_talk_list_item_comment_count);
            viewHolder.tagText = (TextView) view.findViewById(R.id.team_talk_list_item_tag);
            viewHolder.countIcon = (AppCompatImageView) view.findViewById(R.id.team_talk_list_item_comment_icon);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (talkList.get(position).getStatus() == 1) {
            viewHolder.nameText.setText(ChangeCase.toTitleCase(talkList.get(position).getFullName()));
            viewHolder.teamText.setText(talkList.get(position).getTeamName());
            viewHolder.descriptionText.setText(talkList.get(position).getMessage());
            viewHolder.dateText.setText(GetTime.wallTime(talkList.get(position).getDate()));
            viewHolder.titleText.setText(talkList.get(position).getTitle());
            if (talkList.get(position).getCount() > 0) {
                viewHolder.countIcon.setImageResource(R.drawable.vi_comment_blue);
                viewHolder.countText.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));

                String countString;
                if (talkList.get(position).getCount() == 1) {
                    countString = talkList.get(position).getCount() + " " + mContext.getResources().getString(R.string.comment);
                } else {
                    countString = GetCounters.convertCounter(talkList.get(position).getCount()) + " " + mContext.getResources().getString(R.string.comments);
                }
                viewHolder.countText.setText(countString);
            } else {
                viewHolder.countText.setTextColor(mContext.getResources().getColor(R.color.text_color_primary));
                viewHolder.countIcon.setImageResource(R.drawable.vi_comment_gray);
                viewHolder.countText.setText(mContext.getResources().getString(R.string.comment));
            }
            applyReadStatus(viewHolder, talkList.get(position));
        }
        return view;
    }

    private void applyReadStatus(ViewHolder holder, TeamTalk_ teamTalk_) {
        if (teamTalk_.getIsRead() == 1) {
            holder.titleText.setTypeface(null, Typeface.NORMAL);
            holder.nameText.setTypeface(null, Typeface.NORMAL);
            holder.teamText.setTypeface(null, Typeface.NORMAL);
            holder.countText.setTypeface(null, Typeface.NORMAL);

            holder.titleText.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_read));
            holder.nameText.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_read));
            holder.teamText.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_read));
            holder.tagText.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_read));
        } else {
            holder.titleText.setTypeface(null, Typeface.BOLD);
            holder.nameText.setTypeface(null, Typeface.BOLD);
            holder.teamText.setTypeface(null, Typeface.BOLD);
            holder.countText.setTypeface(null, Typeface.BOLD);

            holder.titleText.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_primary));
            holder.nameText.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_primary));
            holder.teamText.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_primary));
            holder.tagText.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_primary));
        }
    }

    private class ViewHolder {
        TextView nameText, dateText, teamText, descriptionText, titleText, countText, tagText;
        AppCompatImageView countIcon;
    }
}
