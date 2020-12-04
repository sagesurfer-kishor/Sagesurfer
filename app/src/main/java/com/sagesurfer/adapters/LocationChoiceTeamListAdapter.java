package com.sagesurfer.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.models.Location_;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Monika on 7/2/2018.
 */

public class LocationChoiceTeamListAdapter extends ArrayAdapter<Location_> {

    private final List<Location_> locationList;
    private final List<Location_> list;
    private final Context context;

    public LocationChoiceTeamListAdapter(Context context, List<Location_> locationList) {
        super(context, 0, locationList);
        this.locationList = locationList;
        this.context = context;
        this.list = new ArrayList<>();
        this.list.addAll(this.locationList);
    }

    @Override
    public int getCount() {
        return locationList.size();
    }

    @Override
    public Location_ getItem(int position) {
        if (locationList != null && locationList.size() > 0) {
            return locationList.get(position);
        }
        return null;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.team_list_item_layout, parent, false);

            viewHolder.nameText = (TextView) view.findViewById(R.id.team_list_item_name);
            viewHolder.warningText = (TextView) view.findViewById(R.id.team_list_item_warning);

            viewHolder.image = (ImageView) view.findViewById(R.id.team_list_item_image);

            viewHolder.mainLayout = (RelativeLayout) view.findViewById(R.id.team_list_item_layout);

            viewHolder.image.setVisibility(View.GONE);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (locationList.get(position).getStatus() == 0) {
            viewHolder.mainLayout.setVisibility(View.GONE);
            viewHolder.warningText.setVisibility(View.VISIBLE);
            view.setBackgroundColor(context.getResources().getColor(R.color.screen_background));
            return view;
        }
        if (locationList.get(position).getStatus() == 1) {
            view.setBackgroundColor(context.getResources().getColor(R.color.white));
            viewHolder.mainLayout.setVisibility(View.VISIBLE);
            viewHolder.warningText.setVisibility(View.GONE);

            viewHolder.nameText.setText(locationList.get(position).getName());
        }
        return view;
    }

    private class ViewHolder {
        TextView nameText, warningText;
        ImageView image;
        RelativeLayout mainLayout;
    }

    public void filterTeams(String searchString) {
        locationList.clear();
        if (searchString == null) {
            locationList.addAll(list);
            notifyDataSetChanged();
            return;
        }
        searchString = searchString.toLowerCase(Locale.getDefault());
        if (searchString.length() <= 0) {
            locationList.addAll(list);
        } else {
            for (Location_ location_ : list) {
                String name = location_.getName();
                if (name.toLowerCase(Locale.getDefault()).contains(searchString)) {
                    locationList.add(location_);
                }
            }
        }
        if (locationList.size() <= 0) {
            Location_ teams_ = new Location_();
            teams_.setStatus(0);
            locationList.add(teams_);
        }
        notifyDataSetChanged();
    }
}
