package com.modules.platform;

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

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.datetime.GetTime;

import java.util.List;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 03-08-2017
 * Last Modified on 14-12-2017
 */

class PlatformMessageListAdapter extends ArrayAdapter<DashboardMessage_> {
    private final List<DashboardMessage_> messageList;
    private final Activity activity;

    PlatformMessageListAdapter(Activity activity, List<DashboardMessage_> messageList) {
        super(activity, 0, messageList);
        this.messageList = messageList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public DashboardMessage_ getItem(int position) {
        if (messageList != null && messageList.size() > 0) {
            return messageList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return messageList.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.platform_message_list_item_layout, parent, false);

            viewHolder.titleText = (TextView) view.findViewById(R.id.platform_message_item_title);
            viewHolder.dateText = (TextView) view.findViewById(R.id.platform_message_item_date);
            viewHolder.startDate = (TextView) view.findViewById(R.id.platform_message_item_from_time);
            viewHolder.endDate = (TextView) view.findViewById(R.id.platform_message_item_to_time);
            viewHolder.descriptionText = (TextView) view.findViewById(R.id.platform_message_item_priority_description);

            viewHolder.icon = (AppCompatImageView) view.findViewById(R.id.platform_message_item_priority_icon);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (messageList.get(position).getStatus() == 1) {
            viewHolder.titleText.setText(messageList.get(position).getSubject());
            viewHolder.startDate.setText(GetTime.getTodayMm(messageList.get(position).getStartDate()));
            viewHolder.endDate.setText(GetTime.getTodayMm(messageList.get(position).getEndDate()));
            viewHolder.descriptionText.setText(messageList.get(position).getDescription());
            viewHolder.dateText.setText(GetTime.wallTime(messageList.get(position).getCreatedDate()));
            if (messageList.get(position).getPriority() == 1) {
                viewHolder.icon.setVisibility(View.VISIBLE);
            } else {
                viewHolder.icon.setVisibility(View.GONE);
            }
            applyReadStatus(viewHolder, messageList.get(position));
        }
        return view;
    }

    // apply read/unread status to change text color
    private void applyReadStatus(ViewHolder holder, DashboardMessage_ dashboardMessage_) {
        if (dashboardMessage_.getIsRead() == 1) {
            holder.titleText.setTypeface(null, Typeface.NORMAL);
            holder.startDate.setTypeface(null, Typeface.NORMAL);
            holder.endDate.setTypeface(null, Typeface.NORMAL);

            holder.titleText.setTextColor(ContextCompat.getColor(activity.getApplicationContext(),
                    R.color.text_color_read));
        } else {
            holder.titleText.setTypeface(null, Typeface.BOLD);
            holder.startDate.setTypeface(null, Typeface.BOLD);
            holder.endDate.setTypeface(null, Typeface.BOLD);

            holder.titleText.setTextColor(ContextCompat.getColor(activity.getApplicationContext(),
                    R.color.text_color_primary));
        }
    }

    private class ViewHolder {
        TextView titleText, dateText, descriptionText, startDate, endDate;
        AppCompatImageView icon;
    }
}
