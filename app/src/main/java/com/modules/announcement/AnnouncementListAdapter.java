package com.modules.announcement;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.text.format.DateFormat;
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
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.Announcement_;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 21-07-2017
 * Last Modified on 27-12-2017
 */

class AnnouncementListAdapter extends ArrayAdapter<Announcement_> {
    private final List<Announcement_> announcementList;
    private final Activity activity;
    private final Context _context;

    AnnouncementListAdapter(Activity activity, List<Announcement_> announcementList) {
        super(activity, 0, announcementList);
        this.announcementList = announcementList;
        this.activity = activity;
        this._context = this.activity.getApplicationContext();
    }

    @Override
    public int getCount() {
        return announcementList.size();
    }

    @Override
    public Announcement_ getItem(int position) {
        if (announcementList != null && announcementList.size() > 0) {
            return announcementList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return announcementList.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.announcement_list_item_layout, parent, false);

            viewHolder.nameText = (TextView) view.findViewById(R.id.announcement_list_item_name);
            viewHolder.dateText = (TextView) view.findViewById(R.id.announcement_list_item_date);
            viewHolder.teamText = (TextView) view.findViewById(R.id.announcement_list_item_team);
            viewHolder.tagText = (TextView) view.findViewById(R.id.announcement_list_item_tag);
            viewHolder.descriptionText = (TextView) view.findViewById(R.id.announcement_list_item_description);

            viewHolder.profile = (ImageView) view.findViewById(R.id.announcement_list_item_image);

            viewHolder.mainLayout = (RelativeLayout) view.findViewById(R.id.announcement_list_item_main_layout);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (announcementList.get(position).getStatus() == 1) {
            viewHolder.nameText.setText(ChangeCase.toTitleCase(announcementList.get(position).getCreatedBy()));
            viewHolder.teamText.setText(announcementList.get(position).getTeamName());
            viewHolder.descriptionText.setText(announcementList.get(position).getDescription());
            viewHolder.dateText.setText(getDate(announcementList.get(position).getDate()));

            //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
            Glide.with(activity.getApplicationContext()).load(announcementList.get(position).getImage())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(GetThumbnails.userIcon(announcementList.get(position).getImage()))
                            .transform(new CircleTransform(activity.getApplicationContext())))
                    .into(viewHolder.profile);

            applyReadStatus(viewHolder, announcementList.get(position));
        }
        return view;
    }

    // Apply read/unread status to UI items
    private void applyReadStatus(ViewHolder holder, Announcement_ announcement_) {
        if (announcement_.getIsRead() == 1) {
            holder.nameText.setTypeface(null, Typeface.NORMAL);
            holder.nameText.setTextColor(ContextCompat.getColor(_context, R.color.text_color_read));
            holder.teamText.setTextColor(ContextCompat.getColor(_context, R.color.text_color_read));
            holder.tagText.setTextColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.text_color_read));
        } else {
            holder.nameText.setTypeface(null, Typeface.BOLD);
            holder.nameText.setTextColor(ContextCompat.getColor(_context, R.color.text_color_primary));
            holder.teamText.setTextColor(ContextCompat.getColor(_context, R.color.text_color_primary));
            holder.tagText.setTextColor(ContextCompat.getColor(_context, R.color.text_color_primary));
            if (Preferences.get(General.TEAM_ANNOUNCEMENT_ID) != null && !Preferences.get(General.TEAM_ANNOUNCEMENT_ID).equalsIgnoreCase("")) {
                if (Preferences.get(General.TEAM_ANNOUNCEMENT_ID).equalsIgnoreCase(String.valueOf(announcement_.getId()))) {
                    holder.mainLayout.setBackground(activity.getResources().getDrawable(R.drawable.white_rounded_rectangle_blue_border));
                    Preferences.save(General.TEAM_ANNOUNCEMENT_ID, "");
                }
            }
        }
    }

    private class ViewHolder {
        TextView nameText, dateText, teamText, descriptionText, tagText;
        ImageView profile;
        RelativeLayout mainLayout;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy | hh:mm a", cal).toString();
        return date;
    }
}
