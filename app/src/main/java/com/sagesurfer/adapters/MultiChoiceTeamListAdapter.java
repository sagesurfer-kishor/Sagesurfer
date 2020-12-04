package com.sagesurfer.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
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
 *         Created on 31-07-2017
 *         Last Modified on 15-12-2017
 */

public class MultiChoiceTeamListAdapter extends ArrayAdapter<Teams_> {

    private final List<Teams_> teamsList;
    private final List<Teams_> list;
    private final Context context;

    public MultiChoiceTeamListAdapter(Context context, List<Teams_> teamsList) {
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

    private int getStatus(int position) {
        if (teamsList != null && teamsList.size() > 0) {
            return teamsList.get(position).getStatus();
        }
        return 0;
    }

    private boolean getSelected(int position) {
        return teamsList != null && teamsList.size() > 0 && teamsList.get(position).getSelected();
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
            viewHolder.addIcon = (AppCompatImageView) view.findViewById(R.id.team_list_item_add);
            viewHolder.checkIcon = (AppCompatImageView) view.findViewById(R.id.team_list_item_check);
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
            viewHolder.mainLayout.setTag(position);
            viewHolder.mainLayout.setVisibility(View.VISIBLE);
            viewHolder.warningText.setVisibility(View.GONE);
            viewHolder.addIcon.setVisibility(View.GONE);
            viewHolder.checkIcon.setVisibility(View.VISIBLE);
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

            if (teamsList.get(position).getSelected()) {
                viewHolder.addIcon.setVisibility(View.GONE);
                viewHolder.checkIcon.setImageResource(R.drawable.vi_check_blue);
                viewHolder.addIcon.setImageResource(0);
            } else {
                viewHolder.addIcon.setVisibility(View.VISIBLE);
                viewHolder.addIcon.setImageResource(R.drawable.vi_add_task_list_p);
                viewHolder.checkIcon.setImageResource(0);
            }
        }
        //viewHolder.mainLayout.setOnClickListener(onClick);

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                if (getStatus(position) == 1) {
                    if (getSelected(position)) {
                        teamsList.get(position).setSelected(false);
                        viewHolder.addIcon.setVisibility(View.VISIBLE);
                        viewHolder.addIcon.setImageResource(R.drawable.vi_add_task_list_p);
                        viewHolder.checkIcon.setImageResource(0);
                    } else {
                        teamsList.get(position).setSelected(true);
                        viewHolder.addIcon.setVisibility(View.GONE);
                        viewHolder.addIcon.setImageResource(0);
                        viewHolder.checkIcon.setImageResource(R.drawable.vi_check_blue);
                    }
                }
            }
        });
        return view;
    }

    private final View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            if (getStatus(position) == 1) {
                if (getSelected(position)) {
                    teamsList.get(position).setSelected(false);
                } else {
                    teamsList.get(position).setSelected(true);
                }
                notifyDataSetChanged();
            }
        }
    };

    private class ViewHolder {
        AppCompatImageView addIcon,checkIcon;
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
