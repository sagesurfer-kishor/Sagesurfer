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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.views.CircleTransform;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 12-07-2017
 * Last Modified on 12-07-2017
 */

public class SingleChoiceTeamListAdapter extends ArrayAdapter<Teams_> {

    private final List<Teams_> teamsList;
    private final List<Teams_> list;
    private final Context context;

    public SingleChoiceTeamListAdapter(Context context, List<Teams_> teamsList) {
        super(context, 0, teamsList);
        this.teamsList = teamsList;
        this.context = context;
        this.list = new ArrayList<>();
        this.list.addAll(this.teamsList);
    }

    @Override
    public int getCount() {
        return teamsList.size();
    }

    @Override
    public Teams_ getItem(int position) {
        if (teamsList != null && teamsList.size() > 0) {
            return teamsList.get(position);
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

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (teamsList.get(position).getStatus() == 0) {
            viewHolder.mainLayout.setVisibility(View.GONE);
            viewHolder.warningText.setVisibility(View.VISIBLE);
            view.setBackgroundColor(context.getResources().getColor(R.color.screen_background));
            return view;
        }
        if (teamsList.get(position).getStatus() == 1) {
            view.setBackgroundColor(context.getResources().getColor(R.color.white));
            viewHolder.mainLayout.setVisibility(View.VISIBLE);
            viewHolder.warningText.setVisibility(View.GONE);

            viewHolder.nameText.setText(teamsList.get(position).getName());

            //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
            Glide.with(context)
                    .load(teamsList.get(position).getPhoto())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_group_default)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL) //.RESULT
                            .transform(new CircleTransform(context)))
                    .into(viewHolder.image);
        }
        return view;
    }

    private class ViewHolder {
        TextView nameText, warningText;
        ImageView image;
        RelativeLayout mainLayout;
    }

    public void filterTeams(String searchString) {
        teamsList.clear();
        if (searchString == null) {
            teamsList.addAll(list);
            notifyDataSetChanged();
            return;
        }
        searchString = searchString.toLowerCase(Locale.getDefault());
        if (searchString.length() <= 0) {
            teamsList.addAll(list);
        } else {
            for (Teams_ teams_ : list) {
                String name = teams_.getName();
                if (name.toLowerCase(Locale.getDefault()).contains(searchString)) {
                    teamsList.add(teams_);
                }
            }
        }
        if (teamsList.size() <= 0) {
            Teams_ teams_ = new Teams_();
            teams_.setStatus(0);
            teamsList.add(teams_);
        }
        notifyDataSetChanged();
    }
}
