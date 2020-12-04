package com.modules.calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.models.Event_;

import java.util.List;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 16-08-2017
 * Last Modified on 13-12-2017
 */

class EventListAdapter extends ArrayAdapter<Event_> {
    private final List<Event_> eventList;
    private final Activity activity;
    private final EventListAdapterListener listener;

    EventListAdapter(Activity activity, List<Event_> eventList, EventListAdapterListener listener) {
        super(activity, 0, eventList);
        this.eventList = eventList;
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Event_ getItem(int position) {
        if (eventList != null && eventList.size() > 0) {
            return eventList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return eventList.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.event_list_item_layout, parent, false);

            viewHolder.nameText = (TextView) view.findViewById(R.id.event_list_item_name);
            viewHolder.dateText = (TextView) view.findViewById(R.id.event_list_item_date);
            viewHolder.descriptionText = (TextView) view.findViewById(R.id.event_list_item_description);
            viewHolder.participants = (TextView) view.findViewById(R.id.event_list_item_participants);
            viewHolder.dateRange = (TextView) view.findViewById(R.id.event_list_item_date_rang);
            viewHolder.imageViewLocation = (AppCompatImageView) view.findViewById(R.id.imageview_location);
            viewHolder.textViewLocation = (TextView) view.findViewById(R.id.textview_location);

            viewHolder.mainLayout = (LinearLayout) view.findViewById(R.id.event_list_item_main_layout);

            viewHolder.imageViewLocation.setVisibility(View.GONE);
            viewHolder.textViewLocation.setVisibility(View.GONE);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (eventList.get(position).getStatus() == 1) {
            viewHolder.nameText.setText(ChangeCase.toTitleCase(eventList.get(position).getName()));
            viewHolder.descriptionText.setText(eventList.get(position).getDescription());
            viewHolder.dateText.setText(eventList.get(position).getEvent_time());
            viewHolder.dateRange.setText(eventList.get(position).getDate_string());
            String peopleCount = eventList.get(position).getParticipants() + " People";
            viewHolder.participants.setText(peopleCount);
            if (eventList.get(position).getLocation() != null && eventList.get(position).getLocation().length() > 0) {
                viewHolder.imageViewLocation.setVisibility(View.VISIBLE);
                viewHolder.textViewLocation.setVisibility(View.VISIBLE);
                viewHolder.textViewLocation.setText(eventList.get(position).getLocation());
            } else {
                viewHolder.imageViewLocation.setVisibility(View.GONE);
                viewHolder.textViewLocation.setVisibility(View.GONE);
                viewHolder.textViewLocation.setText(eventList.get(position).getLocation());
            }

            viewHolder.mainLayout.setTag(position);
            viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailsIntent = new Intent(activity.getApplicationContext(), EventDetailsActivity.class);
                    detailsIntent.putExtra(Actions_.GET_EVENTS, eventList.get(position));
                    activity.startActivity(detailsIntent);
                    activity.overridePendingTransition(0, 0);
                }
            });

            applyReadStatus(viewHolder, eventList.get(position));
            applyClickEvents(viewHolder, position);
        }

        return view;
    }

    // Apply read/unread status to UI items
    private void applyReadStatus(ViewHolder holder, Event_ event_) {
        if (event_.getIs_read() == 1) {
            holder.nameText.setTypeface(null, Typeface.NORMAL);
            holder.nameText.setTextColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.text_color_read));

        } else {
            holder.nameText.setTypeface(null, Typeface.BOLD);
            holder.nameText.setTextColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.text_color_primary));

            /*if(Preferences.get(General.EVENT_ID) != null && !Preferences.get(General.EVENT_ID).equalsIgnoreCase("")) {
                if(Preferences.get(General.EVENT_ID).equalsIgnoreCase(String.valueOf(event_.getId()))) {
                    holder.mainLayout.setBackground(activity.getResources().getDrawable(R.drawable.white_rounded_rectangle_blue_border));
                    Preferences.save(General.EVENT_ID, "");
                }
            }*/
        }
    }

    interface EventListAdapterListener {
        void onItemClicked(int position, String itemView);
    }

    private void applyClickEvents(ViewHolder holder, final int position) {
        holder.imageViewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(position, "location");
            }
        });

        holder.textViewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(position, "location");
            }
        });
    }

    private class ViewHolder {
        TextView nameText, dateText, descriptionText, participants, dateRange, textViewLocation;
        LinearLayout mainLayout;
        AppCompatImageView imageViewLocation;
    }
}
