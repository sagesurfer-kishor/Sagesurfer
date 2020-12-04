package com.modules.wall;

import android.app.Activity;
import android.content.Context;
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
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Kailash Karankal on 5/30/2019.
 */
public class TeamListAdapter extends ArrayAdapter<Feed_> {

    private final List<Feed_> teamList;

    private final Activity activity;
    private final Context _context;

    TeamListAdapter(Activity activity, List<Feed_> announcementList) {
        super(activity, 0, announcementList);
        this.teamList = announcementList;
        this.activity = activity;
        this._context = this.activity.getApplicationContext();
    }

    @Override
    public int getCount() {
        return teamList.size();
    }

    @Override
    public Feed_ getItem(int position) {
        if (teamList != null && teamList.size() > 0) {
            return teamList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return teamList.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TeamListAdapter.ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new TeamListAdapter.ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.friend_squad_list_item_layout, parent, false);

            viewHolder.tagText = (TextView) view.findViewById(R.id.announcement_list_item_tag);
            viewHolder.profile = (ImageView) view.findViewById(R.id.announcement_list_item_image);

            viewHolder.mainLayout = (RelativeLayout) view.findViewById(R.id.announcement_list_item_main_layout);

            view.setTag(viewHolder);
        } else {
            viewHolder = (TeamListAdapter.ViewHolder) view.getTag();
        }

        if (teamList.get(position).getStatus() == 1) {

            viewHolder.tagText.setText(ChangeCase.toTitleCase(teamList.get(position).getName()));

            Glide.with(activity.getApplicationContext()).load(teamList.get(position).getProfilePhoto())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(GetThumbnails.userIcon(teamList.get(position).getProfilePhoto()))
                            .transform(new CircleTransform(activity.getApplicationContext())))
                    .into(viewHolder.profile);

            applyReadStatus(viewHolder, teamList.get(position));
        }
        return view;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy | hh:mm a", cal).toString();
        return date;
    }


    // Apply read/unread status to UI items
    private void applyReadStatus(TeamListAdapter.ViewHolder holder, Feed_ feed_) {
        if (Preferences.get(General.TEAM_ANNOUNCEMENT_ID) != null && !Preferences.get(General.TEAM_ANNOUNCEMENT_ID).equalsIgnoreCase("")) {
            if (Preferences.get(General.TEAM_ANNOUNCEMENT_ID).equalsIgnoreCase(String.valueOf(feed_.getId()))) {
                holder.mainLayout.setBackground(activity.getResources().getDrawable(R.drawable.white_rounded_rectangle_blue_border));
                Preferences.save(General.TEAM_ANNOUNCEMENT_ID, "");
            }
        }

        holder.tagText.setTextColor(ContextCompat.getColor(_context, R.color.text_color_primary));
    }

    private class ViewHolder {
        TextView tagText;
        ImageView profile;
        RelativeLayout mainLayout;
    }
}